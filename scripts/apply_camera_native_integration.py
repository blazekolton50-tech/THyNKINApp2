from pathlib import Path


final_main = Path("app/src/main/java/com/patsy/app/FinalMainActivity.kt")
text = final_main.read_text(encoding="utf-8")

replacements = (
    (
        "import com.patsy.app.thynk.LockedCameraHub\n",
        "import com.patsy.app.thynk.NativeCameraHub\n",
    ),
    (
        "FinalAppPage.CREATE -> LockedCameraHub()",
        "FinalAppPage.CREATE -> NativeCameraHub(onOpenThynk = { navigate(FinalHomeDestination.THYNK) })",
    ),
)

for old, new in replacements:
    if new in text:
        continue
    if old not in text:
        raise SystemExit(f"Expected Camera integration anchor missing: {old!r}")
    text = text.replace(old, new, 1)

if "FinalAppPage.CREATE -> NativeCameraHub(" not in text:
    raise SystemExit("Native Camera route was not wired")
if "FinalAppPage.CREATE -> LockedCameraHub()" in text:
    raise SystemExit("Legacy Camera shell route still owns the center Camera destination")

final_main.write_text(text, encoding="utf-8")
print("Native Camera capture/import hub wired into locked center Camera destination")
