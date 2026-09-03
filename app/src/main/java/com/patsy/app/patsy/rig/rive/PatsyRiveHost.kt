package com.patsy.app.patsy.rig.rive

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import app.rive.Artboard
import app.rive.Fit
import app.rive.Result
import app.rive.Rive
import app.rive.RiveFile
import app.rive.RiveFileSource
import app.rive.StateMachine
import app.rive.ViewModelInstance
import app.rive.ViewModelSource
import app.rive.rememberArtboardResult
import app.rive.rememberRiveFile
import app.rive.rememberRiveWorkerOrNull
import app.rive.rememberStateMachineResult
import app.rive.rememberViewModelInstanceResult
import com.patsy.app.patsy.rig.PatsyRigContractV1
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout

/**
 * Renders the authored rig only after it resolves and passes the V1 ABI checks. In every missing,
 * loading, invalid, or runtime-error state [fallback] remains the visible Patsy implementation.
 */
@Composable
fun PatsyRiveHost(
    runtime: PatsyRiveRuntimeAdapter,
    modifier: Modifier = Modifier,
    playing: Boolean = true,
    fallback: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val rawResource = remember(context) { PatsyRiveAssetLocator.find(context) }
    if (rawResource == 0) {
        LaunchedEffect(runtime) { runtime.detach() }
        fallback()
        return
    }

    val workerError = remember { mutableStateOf<Throwable?>(null) }
    val worker = rememberRiveWorkerOrNull(workerError)
    if (worker == null) {
        LaunchedEffect(runtime, workerError.value) {
            runtime.markFailed("Patsy animation runtime could not start")
        }
        fallback()
        return
    }

    val fileResult = rememberRiveFile(RiveFileSource.RawRes.from(rawResource), worker)
    val contentResult = fileResult.andThen { file ->
        val artboardResult = rememberArtboardResult(file, PatsyRigContractV1.ARTBOARD)
        val instanceResult = rememberViewModelInstanceResult(
            file,
            ViewModelSource.Named(PatsyRigContractV1.VIEW_MODEL)
                .namedInstance(PatsyRigContractV1.DEFAULT_VIEW_MODEL_INSTANCE),
        )
        artboardResult.andThen { artboard ->
            rememberStateMachineResult(artboard, PatsyRigContractV1.STATE_MACHINE)
                .zip(instanceResult) { stateMachine, instance ->
                    PatsyRiveContent(file, artboard, stateMachine, instance)
                }
        }
    }

    when (contentResult) {
        is Result.Loading -> {
            LaunchedEffect(runtime) { runtime.markLoading() }
            fallback()
        }
        is Result.Error -> {
            LaunchedEffect(runtime, contentResult.throwable) {
                runtime.markFailed("Patsy animation file is unavailable or incompatible")
            }
            fallback()
        }
        is Result.Success -> ValidatedPatsyRive(
            content = contentResult.value,
            runtime = runtime,
            modifier = modifier,
            playing = playing,
            fallback = fallback,
        )
    }
}

@Composable
private fun ValidatedPatsyRive(
    content: PatsyRiveContent,
    runtime: PatsyRiveRuntimeAdapter,
    modifier: Modifier,
    playing: Boolean,
    fallback: @Composable () -> Unit,
) {
    var validated by remember(content.instance) { mutableStateOf(false) }

    LaunchedEffect(content.instance) {
        runtime.markLoading()
        val missing = PatsyRiveContractValidator.findMissing(content)
        if (missing.isEmpty()) {
            runtime.attach(content.instance)
            validated = true
        } else {
            runtime.markInvalid(missing)
            validated = false
        }
    }
    DisposableEffect(runtime, content.instance) {
        onDispose { runtime.detach() }
    }

    if (!validated) {
        fallback()
        return
    }
    Rive(
        file = content.file,
        modifier = modifier,
        playing = playing,
        artboard = content.artboard,
        stateMachine = content.stateMachine,
        viewModelInstance = content.instance,
        fit = Fit.Contain(),
    )
}

private data class PatsyRiveContent(
    val file: RiveFile,
    val artboard: Artboard,
    val stateMachine: StateMachine,
    val instance: ViewModelInstance,
)

internal object PatsyRiveAssetLocator {
    const val RAW_RESOURCE_NAME = "patsy_assistant"

    fun find(context: Context): Int = sequenceOf(context.packageName, "com.patsy.app")
        .map { packageName ->
            context.resources.getIdentifier(RAW_RESOURCE_NAME, "raw", packageName)
        }
        .firstOrNull { it != 0 }
        ?: 0
}

private object PatsyRiveContractValidator {
    private val booleanPaths = setOf(
        PatsyRigContractV1.Property.MOTION_REDUCED,
        PatsyRigContractV1.Property.EAR_PHYSICS,
        PatsyRigContractV1.Property.SPEECH_TALKING,
    )
    private val enumPaths = setOf(
        PatsyRigContractV1.Property.MOTION_MODE,
        PatsyRigContractV1.Property.FACE_EXPRESSION,
        PatsyRigContractV1.Property.SPEECH_VISEME,
    )

    suspend fun findMissing(content: PatsyRiveContent): Set<String> {
        val missing = linkedSetOf<String>()
        if (PatsyRigContractV1.ARTBOARD !in content.file.getArtboardNames()) {
            missing += "artboard:${PatsyRigContractV1.ARTBOARD}"
        }
        if (PatsyRigContractV1.STATE_MACHINE !in content.artboard.getStateMachineNames()) {
            missing += "state-machine:${PatsyRigContractV1.STATE_MACHINE}"
        }
        if (content.instance.getViewModelName() != PatsyRigContractV1.VIEW_MODEL) {
            missing += "view-model:${PatsyRigContractV1.VIEW_MODEL}"
        }
        if (content.instance.getName() != PatsyRigContractV1.DEFAULT_VIEW_MODEL_INSTANCE) {
            missing += "instance:${PatsyRigContractV1.DEFAULT_VIEW_MODEL_INSTANCE}"
        }
        missing += coroutineScope {
            PatsyRigContractV1.requiredProperties.map { path ->
                async { if (propertyResolves(content.instance, path)) null else "property:$path" }
            }.awaitAll().filterNotNull()
        }
        return missing
    }

    private suspend fun propertyResolves(instance: ViewModelInstance, path: String): Boolean =
        try {
            withTimeout(1_500) {
                when (path) {
                    in booleanPaths -> instance.getBooleanFlow(path).first()
                    in enumPaths -> instance.getEnumFlow(path).first()
                    else -> instance.getNumberFlow(path).first()
                }
            }
            true
        } catch (_: Throwable) {
            false
        }
}
