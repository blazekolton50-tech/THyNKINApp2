from pathlib import Path

HOST = Path("app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt")

OLD = '''private fun androidx.compose.foundation.lazy.LazyListScope.trackEditorItems(onOpenPage: (String) -> Unit) {
    item {
        InfoPanel("My New Track", "00:00 / 03:24 • local editor preview")
    }
'''

NEW = '''private fun androidx.compose.foundation.lazy.LazyListScope.trackEditorItems(onOpenPage: (String) -> Unit) {
    item { ThynkAudioImportCard() }
    item {
        InfoPanel("My New Track", "Local editor state • imported audio plays from the real selected URI")
    }
'''


def main() -> None:
    source = HOST.read_text(encoding="utf-8")
    if NEW in source:
        return
    if OLD not in source:
        raise RuntimeError("Could not wire real THyNK audio import: expected Track Editor block was not found")
    HOST.write_text(source.replace(OLD, NEW, 1), encoding="utf-8")


if __name__ == "__main__":
    main()
