package com.patsy.app.thynk

sealed interface DesignEntryPoint {
    data object Blank : DesignEntryPoint
    data object CustomSize : DesignEntryPoint
    data class Templates(val kind: String) : DesignEntryPoint
}

private val designTemplateKinds = mapOf(
    "POSTERS" to "posters",
    "FLYERS" to "flyers",
    "INVITATIONS" to "invitations",
    "CARDS" to "cards",
    "MENUS" to "menus",
    "PRICE LISTS" to "price-lists",
    "SIGNS" to "signs",
    "CERTIFICATES" to "certificates",
    "BROCHURES" to "brochures",
    "LABELS" to "labels",
    "TEMPLATES" to "templates",
)

fun designEntryForThynkItem(item: String): DesignEntryPoint? = when (val label = item.trim().uppercase()) {
    "BLANK DESIGNS" -> DesignEntryPoint.Blank
    "CUSTOM SIZE" -> DesignEntryPoint.CustomSize
    else -> designTemplateKinds[label]?.let(DesignEntryPoint::Templates)
}
