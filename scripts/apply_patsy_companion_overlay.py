#!/usr/bin/env python3
from pathlib import Path

path = Path("app/src/main/java/com/patsy/app/FinalMainActivity.kt")
text = path.read_text()

if "import com.patsy.app.patsy.ui.PatsyCompanionOverlay" not in text:
    needle = "import com.patsy.app.navigation.ShellNavigationGate\n"
    replacement = needle + (
        "import com.patsy.app.patsy.PatsyCompanionTarget\n"
        "import com.patsy.app.patsy.ui.PatsyCompanionCommand\n"
        "import com.patsy.app.patsy.ui.PatsyCompanionOverlay\n"
    )
    if needle not in text:
        raise SystemExit("FinalMainActivity import anchor not found")
    text = text.replace(needle, replacement, 1)

if "var patsyCommand by remember" not in text:
    needle = "    var thynkEntry by remember { mutableStateOf(ThynkStudioEntry.IT) }\n"
    replacement = needle + "    var patsyCommand by remember { mutableStateOf<PatsyCompanionCommand?>(null) }\n"
    if needle not in text:
        raise SystemExit("Patsy command state anchor not found")
    text = text.replace(needle, replacement, 1)

if "fun guidePatsyTo(" not in text:
    needle = "    val scope = rememberCoroutineScope()\n\n"
    replacement = needle + (
        "    fun guidePatsyTo(target: PatsyCompanionTarget) {\n"
        "        patsyCommand = PatsyCompanionCommand.GuideTo(target)\n"
        "    }\n\n"
        "    fun returnPatsyHome() {\n"
        "        patsyCommand = PatsyCompanionCommand.ReturnHome\n"
        "    }\n\n"
    )
    if needle not in text:
        raise SystemExit("Patsy command helper anchor not found")
    text = text.replace(needle, replacement, 1)

navigate_anchor = "    fun navigate(destination: FinalHomeDestination) {\n"
if navigate_anchor in text and "    fun navigate(destination: FinalHomeDestination) {\n        returnPatsyHome()\n" not in text:
    text = text.replace(navigate_anchor, navigate_anchor + "        returnPatsyHome()\n", 1)

plain_home = "FinalHomeScreen(onNavigate = ::navigate)"
guided_home = (
    "FinalHomeScreen(\n"
    "                                onNavigate = ::navigate,\n"
    "                                onAskPatsy = {\n"
    "                                    guidePatsyTo(PatsyCompanionTarget(0.50f, 0.16f))\n"
    "                                },\n"
    "                            )"
)
if plain_home in text:
    text = text.replace(plain_home, guided_home)

surface_anchor = (
    "        Surface(Modifier.fillMaxSize(), color = FinalCharcoal) {\n"
    "            Column(Modifier.fillMaxSize().background(FinalCharcoal)) {\n"
)
if "Surface(Modifier.fillMaxSize(), color = FinalCharcoal) {\n            Box(Modifier.fillMaxSize()) {" not in text:
    if surface_anchor not in text:
        raise SystemExit("Patsy overlay root anchor not found")
    text = text.replace(
        surface_anchor,
        "        Surface(Modifier.fillMaxSize(), color = FinalCharcoal) {\n            Box(Modifier.fillMaxSize()) {\n                Column(Modifier.fillMaxSize().background(FinalCharcoal)) {\n",
        1,
    )

if "PatsyCompanionOverlay(" not in text:
    footer_anchor = (
        "                    ThynkPrimaryNavigationBar(\n"
        "                        selected = selectedPanelDestination,\n"
        "                        onNavigate = ::navigatePanel,\n"
        "                    )\n"
        "                }\n"
        "            }\n"
        "        }\n"
        "    }\n"
        "}\n"
    )
    footer_replacement = (
        "                    ThynkPrimaryNavigationBar(\n"
        "                        selected = selectedPanelDestination,\n"
        "                        onNavigate = ::navigatePanel,\n"
        "                    )\n"
        "                }\n"
        "                }\n\n"
        "                if (\n"
        "                    page !in listOf(\n"
        "                        FinalAppPage.LOGIN,\n"
        "                        FinalAppPage.DEBUG_SET_PASSWORD,\n"
        "                        FinalAppPage.PROTECTED,\n"
        "                    ) && (session != null || debugPreview)\n"
        "                ) {\n"
        "                    PatsyCompanionOverlay(\n"
        "                        command = patsyCommand,\n"
        "                        onCommandConsumed = { patsyCommand = null },\n"
        "                        modifier = Modifier.fillMaxSize(),\n"
        "                    )\n"
        "                }\n"
        "            }\n"
        "        }\n"
        "    }\n"
        "}\n"
    )
    if footer_anchor not in text:
        raise SystemExit("Patsy overlay footer anchor not found")
    text = text.replace(footer_anchor, footer_replacement, 1)

path.write_text(text)
print("Patsy full-screen companion overlay integration applied")
