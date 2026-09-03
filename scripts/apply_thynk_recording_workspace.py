from pathlib import Path

HOST = Path("app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt")

OLD = '''                is ThynkWorkspaceRoute.Music -> ThynkMusicScreen(
                    pageId = current.pageId,
                    onOpenPage = { route = ThynkWorkspaceRoute.Music(it) },
                )
'''

NEW = '''                is ThynkWorkspaceRoute.Music -> {
                    if (current.pageId == "recording") {
                        ThynkRecordingWorkspaceScreen()
                    } else {
                        ThynkMusicScreen(
                            pageId = current.pageId,
                            onOpenPage = { route = ThynkWorkspaceRoute.Music(it) },
                        )
                    }
                }
'''


def main() -> None:
    source = HOST.read_text(encoding="utf-8")
    if NEW in source:
        return
    if OLD not in source:
        raise RuntimeError("Could not wire native THyNK recording workspace: expected Music host block was not found")
    HOST.write_text(source.replace(OLD, NEW, 1), encoding="utf-8")


if __name__ == "__main__":
    main()
