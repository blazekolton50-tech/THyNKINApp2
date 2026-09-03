from pathlib import Path
import subprocess
import sys

HOST = Path("app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt")
DESIGN = Path("app/src/main/java/com/patsy/app/thynk/ThynkDesignEditorScreen.kt")
LEGACY_SCRIPT = Path("scripts/apply_thynk_design_editor_integration.py")

NEW_HOST_MARKERS = (
    "ThynkWorkspaceNavigation.back(route)",
    "is ThynkWorkspaceRoute.Editor -> when",
    "ThynkDesignEditorScreen()",
)

DESIGN_INTEGRATION_MARKERS = (
    "onDuplicateObject",
    "studioCanvasPanelControls",
    "DesignMiniAction",
)


def main() -> None:
    host_source = HOST.read_text(encoding="utf-8")
    if all(marker in host_source for marker in NEW_HOST_MARKERS):
        design_source = DESIGN.read_text(encoding="utf-8")
        missing = [marker for marker in DESIGN_INTEGRATION_MARKERS if marker not in design_source]
        if missing:
            raise RuntimeError(
                "New THyNK workspace navigation is present, but the native Design integration "
                f"is incomplete: {', '.join(missing)}"
            )
        print("THyNK Design integration verified; new workspace navigation preserved")
        return

    subprocess.run([sys.executable, str(LEGACY_SCRIPT)], check=True)


if __name__ == "__main__":
    main()
