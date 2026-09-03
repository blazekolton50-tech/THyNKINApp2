package com.patsy.app.patsy.rig.rive

import app.rive.ViewModelInstance
import com.patsy.app.patsy.rig.PatsyRigContractV1
import com.patsy.app.patsy.rig.PatsyRigMutation
import com.patsy.app.patsy.rig.PatsyRigRuntimePort
import com.patsy.app.patsy.rig.PatsyRigStatus
import com.patsy.app.patsy.rig.PatsyRigValue

/**
 * Thread-safe bridge between app-owned Patsy state and Rive's Compose View Model instance.
 *
 * The bridge deliberately keeps only the newest value for each property while a file is loading.
 * A ViewModelInstance is attached only after its contract has been validated by [PatsyRiveHost].
 */
class PatsyRiveRuntimeAdapter : PatsyRigRuntimePort {
    private val lock = Any()
    private val pendingByPath = linkedMapOf<String, PatsyRigMutation>()

    @Volatile
    override var status: PatsyRigStatus = PatsyRigStatus.Detached
        private set

    private var writer: PatsyRiveMutationWriter? = null

    override fun apply(mutations: List<PatsyRigMutation>) {
        if (mutations.isEmpty()) return
        synchronized(lock) {
            mutations.forEach { pendingByPath[it.propertyPath] = it }
            writer?.let { activeWriter ->
                if (!writeSafely(activeWriter, mutations)) return
                mutations.forEach { pendingByPath.remove(it.propertyPath) }
            }
        }
    }

    internal fun markLoading() = synchronized(lock) {
        writer = null
        status = PatsyRigStatus.Loading
    }

    internal fun attach(viewModelInstance: ViewModelInstance) {
        attach(PatsyRiveViewModelWriter(viewModelInstance))
    }

    internal fun attach(writer: PatsyRiveMutationWriter) = synchronized(lock) {
        this.writer = writer
        val pending = pendingByPath.values.toList()
        if (!writeSafely(writer, pending)) return
        pendingByPath.clear()
        status = PatsyRigStatus.Ready
    }

    internal fun markInvalid(missingContractItems: Set<String>) = synchronized(lock) {
        writer = null
        status = PatsyRigStatus.InvalidAsset(missingContractItems)
    }

    internal fun markFailed(safeMessage: String) = synchronized(lock) {
        writer = null
        status = PatsyRigStatus.Failed(safeMessage)
    }

    internal fun detach() = synchronized(lock) {
        writer = null
        status = PatsyRigStatus.Detached
    }

    override fun close() = synchronized(lock) {
        writer = null
        pendingByPath.clear()
        status = PatsyRigStatus.Detached
    }

    private fun writeSafely(
        activeWriter: PatsyRiveMutationWriter,
        mutations: List<PatsyRigMutation>,
    ): Boolean = try {
        mutations.forEach { mutation ->
            require(mutation.propertyPath in PatsyRigContractV1.requiredProperties) {
                "Unknown Patsy rig property"
            }
            activeWriter.write(mutation)
        }
        true
    } catch (_: Throwable) {
        writer = null
        status = PatsyRigStatus.Failed("Patsy animation controls could not be applied")
        false
    }
}

internal fun interface PatsyRiveMutationWriter {
    fun write(mutation: PatsyRigMutation)
}

private class PatsyRiveViewModelWriter(
    private val instance: ViewModelInstance,
) : PatsyRiveMutationWriter {
    override fun write(mutation: PatsyRigMutation) {
        when (val value = mutation.value) {
            is PatsyRigValue.Number -> instance.setNumber(mutation.propertyPath, value.value)
            is PatsyRigValue.Boolean -> instance.setBoolean(mutation.propertyPath, value.value)
            is PatsyRigValue.Enum -> instance.setEnum(mutation.propertyPath, value.value)
        }
    }
}
