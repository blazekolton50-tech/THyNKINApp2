from pathlib import Path


def replace_if_present(path: str, old: str, new: str) -> None:
    file = Path(path)
    text = file.read_text(encoding="utf-8")
    if new in text or old not in text:
        return
    file.write_text(text.replace(old, new, 1), encoding="utf-8")


def replace_all_if_present(path: str, old: str, new: str) -> None:
    file = Path(path)
    text = file.read_text(encoding="utf-8")
    if old not in text:
        return
    file.write_text(text.replace(old, new), encoding="utf-8")


def ensure_import(path: str, anchor_import: str, import_line: str) -> None:
    file = Path(path)
    text = file.read_text(encoding="utf-8")
    exact = import_line.rstrip("\n") + "\n"
    if exact in text:
        return
    anchor = anchor_import.rstrip("\n") + "\n"
    if anchor not in text:
        # Newer native revisions may legitimately move or replace the old anchor.
        return
    file.write_text(text.replace(anchor, anchor + exact, 1), encoding="utf-8")


def remove_from_marker(path: str, marker: str) -> None:
    file = Path(path)
    text = file.read_text(encoding="utf-8")
    if marker in text:
        file.write_text(text.split(marker, 1)[0].rstrip() + "\n", encoding="utf-8")


# This script is a compatibility upgrader for older heads. It must be safe to run on newer native
# heads where the THyNK/Camera/global-shell work has already advanced beyond these exact anchors.
final_main = "app/src/main/java/com/patsy/app/FinalMainActivity.kt"
ensure_import(final_main, "import com.patsy.app.ui.finaldesign.FinalWhite", "import com.patsy.app.thynk.LockedCameraHub")
ensure_import(final_main, "import com.patsy.app.thynk.LockedCameraHub", "import com.patsy.app.thynk.ThynkStudioScreen")
ensure_import(final_main, "import com.patsy.app.ui.finaldesign.FinalPrimaryNavigationBar", "import com.patsy.app.ui.finaldesign.FinalVisualContract")
replace_if_present(final_main, "FinalAppPage.THYNK -> Chat()", "FinalAppPage.THYNK -> ThynkStudioScreen()")
replace_if_present(final_main, "FinalAppPage.CREATE -> CreateStudio()", "FinalAppPage.CREATE -> LockedCameraHub()")

visual_contract = "app/src/main/java/com/patsy/app/ui/finaldesign/FinalVisualContract.kt"
replace_if_present(
    visual_contract,
    'val primaryNavigation = listOf("HOME", "THyNK", "CREATE", "PATSY DMS", "PROFILE")',
    'val primaryNavigation = listOf("HOME", "THyNK", "CAMERA", "PATSY DMS", "PROFILE")',
)

nav_bar = "app/src/main/java/com/patsy/app/ui/finaldesign/FinalPrimaryNavigationBar.kt"
replace_if_present(
    nav_bar,
    'FinalNavItem("THyNK", "THyNK", selected == FinalHomeDestination.THYNK)',
    'FinalNavItem("THyNK", "", selected == FinalHomeDestination.THYNK)',
)
replace_if_present(
    nav_bar,
    'FinalNavItem("◌", "PATSY DMS", selected == FinalHomeDestination.PATSY_DMS)',
    'FinalNavItem("◌", "PDMs", selected == FinalHomeDestination.PATSY_DMS)',
)

home = "app/src/main/java/com/patsy/app/ui/finaldesign/FinalHomeScreen.kt"
replace_if_present(
    home,
    "ContinueDesignsSection(onNewDesign = { onNavigate(FinalHomeDestination.CREATE) })",
    "ContinueDesignsSection(onNewDesign = { onNavigate(FinalHomeDestination.THYNK) })",
)
replace_if_present(home, 'NavItem("THyNK", "THyNK", false)', 'NavItem("THyNK", "", false)')
replace_if_present(home, 'NavItem("◌", "PATSY DMS", false)', 'NavItem("◌", "PDMs", false)')
replace_if_present(
    home,
    '''        FinalHomeBottomNavigation(
            modifier = Modifier.align(Alignment.BottomCenter),
            onNavigate = onNavigate,
        )
''',
    '',
)
remove_from_marker(home, "\n@Composable\nprivate fun FinalHomeBottomNavigation(")

# DEVICE-VISIBLE THyNK-IN! FIX: make the installed build unmistakably new and expose both current
# creator spaces from the first authenticated screen instead of hiding them behind legacy-looking UI.
replace_if_present(
    "app/src/main/AndroidManifest.xml",
    'android:label="PATSY"',
    'android:label="THyNK-IN!"',
)
replace_if_present("app/build.gradle.kts", "versionCode = 338", "versionCode = 339")
replace_if_present(
    "app/build.gradle.kts",
    'versionName = "3.3.8-patsy1"',
    'versionName = "3.3.9-thynkin-visible1"',
)
replace_if_present(
    home,
    '''fun FinalHomeScreen(
    onNavigate: (FinalHomeDestination) -> Unit,
    onAskPatsy: () -> Unit = {},
    onCreatePost: () -> Unit = {},
) {''',
    '''fun FinalHomeScreen(
    onNavigate: (FinalHomeDestination) -> Unit,
    onAskPatsy: () -> Unit = {},
    onCreatePost: () -> Unit = {},
    onOpenThynkMusic: () -> Unit = {},
    onOpenThynkIt: () -> Unit = {},
) {''',
)
replace_if_present(
    home,
    '''            item { ContinueDesignsSection(onNewDesign = { onNavigate(FinalHomeDestination.THYNK) }) }
''',
    '''            item {
                VisibleThynkLaunchSection(
                    onOpenThynkMusic = onOpenThynkMusic,
                    onOpenThynkIt = onOpenThynkIt,
                )
            }
            item { ContinueDesignsSection(onNewDesign = { onNavigate(FinalHomeDestination.THYNK) }) }
''',
)
replace_if_present(
    home,
    '''@Composable
private fun FinalHomeTopBar() {
''',
    '''@Composable
private fun VisibleThynkLaunchSection(
    onOpenThynkMusic: () -> Unit,
    onOpenThynkIt: () -> Unit,
) {
    Column(
        Modifier
            .padding(horizontal = 22.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            "CREATE IN THyNK-IN!",
            style = TextStyle(brush = FinalRainbow, fontSize = 13.sp, fontWeight = FontWeight.Black),
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(
                Modifier
                    .weight(1f)
                    .height(126.dp)
                    .border(2.dp, FinalRainbow, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0A0A0F))
                    .clickable(onClick = onOpenThynkMusic)
                    .padding(14.dp),
            ) {
                Text("THyNK Music", color = FinalWhite, fontSize = 19.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(8.dp))
                Text("MIX • RECORD • DJ • MASTER", color = FinalMuted, fontSize = 10.sp, lineHeight = 14.sp)
                Spacer(Modifier.weight(1f))
                Text("OPEN STUDIO →", style = TextStyle(brush = FinalRainbow, fontSize = 11.sp, fontWeight = FontWeight.Bold))
            }
            Column(
                Modifier
                    .weight(1f)
                    .height(126.dp)
                    .border(2.dp, FinalRainbow, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0A0A0F))
                    .clickable(onClick = onOpenThynkIt)
                    .padding(14.dp),
            ) {
                Text("THyNK-IT", color = FinalWhite, fontSize = 19.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(8.dp))
                Text("PUBLISH • FASHION • PHOTO • DESIGN", color = FinalMuted, fontSize = 10.sp, lineHeight = 14.sp)
                Spacer(Modifier.weight(1f))
                Text("OPEN STUDIO →", style = TextStyle(brush = FinalRainbow, fontSize = 11.sp, fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
private fun FinalHomeTopBar() {
''',
)
replace_all_if_present(
    final_main,
    '''                                onNavigate = ::navigate,
                                onAskPatsy = {
''',
    '''                                onNavigate = ::navigate,
                                onOpenThynkMusic = { navigateThynk(ThynkStudioEntry.MUSIC) },
                                onOpenThynkIt = { navigateThynk(ThynkStudioEntry.IT) },
                                onAskPatsy = {
''',
)

# Older THyNK heads had Music only. Upgrade those heads, but do nothing when a newer Editor route is
# already present (including the current Design-aware routing and category-return behavior).
thynk_screen = "app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt"
ensure_import(thynk_screen, "import com.patsy.app.ui.finaldesign.FinalWhite", "import com.patsy.app.studio.StudioEditorState")
ensure_import(thynk_screen, "import com.patsy.app.studio.StudioEditorState", "import com.patsy.app.studio.StudioVideoPlayer")
ensure_import(thynk_screen, "import com.patsy.app.studio.StudioVideoPlayer", "import com.patsy.app.studio.reduceStudioState")
replace_if_present(
    thynk_screen,
    '''private sealed interface ThynkRoute {
    data object Hub : ThynkRoute
    data class Category(val category: ThynkCategory) : ThynkRoute
    data class Music(val pageId: String) : ThynkRoute
}
''',
    '''private sealed interface ThynkRoute {
    data object Hub : ThynkRoute
    data class Category(val category: ThynkCategory) : ThynkRoute
    data class Music(val pageId: String) : ThynkRoute
    data class Editor(val pageId: String) : ThynkRoute
}
''',
)
replace_if_present(
    thynk_screen,
    '''                is ThynkRoute.Category -> ThynkCategoryScreen(current.category) { item ->
                    if (current.category.id == "music") {
                        route = ThynkRoute.Music(musicPageForItem(item))
                    }
                }
                is ThynkRoute.Music -> ThynkMusicScreen(
                    pageId = current.pageId,
                    onOpenPage = { route = ThynkRoute.Music(it) },
                )
''',
    '''                is ThynkRoute.Category -> ThynkCategoryScreen(current.category) { item ->
                    if (current.category.id == "music") {
                        route = ThynkRoute.Music(musicPageForItem(item))
                    } else {
                        editorPageForThynkItem(item)?.let { editorPage -> route = ThynkRoute.Editor(editorPage) }
                    }
                }
                is ThynkRoute.Music -> ThynkMusicScreen(
                    pageId = current.pageId,
                    onOpenPage = { route = ThynkRoute.Music(it) },
                )
                is ThynkRoute.Editor -> ThynkVideoEditorScreen()
''',
)

# The outer authenticated shell has already become canonical on current heads. These two legacy
# rewrites are intentionally best-effort so newer Profile/PDM/Owner work cannot break CI merely by
# changing formatting around an already-correct shell.
replace_if_present(
    final_main,
    '''                    page !in listOf(
                        FinalAppPage.HOME,
                        FinalAppPage.THYNK,
                        FinalAppPage.CREATE,
                        FinalAppPage.DMS,
                        FinalAppPage.PROFILE,
                    )
''',
    '''                    page !in listOf(FinalAppPage.LOGIN, FinalAppPage.DEBUG_SET_PASSWORD) &&
                    (session != null || debugPreview)
''',
)
replace_if_present(
    final_main,
    '''                        onNavigate = { destination ->
                            if (session == null && !debugPreview) {
                                page = FinalAppPage.LOGIN
                            } else {
                                navigate(destination)
                            }
                        },
                        enabled = session != null || debugPreview,
''',
    '''                        onNavigate = ::navigate,
''',
)

print("THyNK compatibility integration checked; visible device launch and newer native routes preserved")