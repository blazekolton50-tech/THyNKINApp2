from pathlib import Path

HOST = Path("app/src/main/java/com/patsy/app/thynk/ThynkStudioScreen.kt")

OLD = '''                is ThynkWorkspaceRoute.Tool -> ThynkToolScreen(
                    categoryId = current.categoryId,
                    item = current.item,
                )
'''

NEW = '''                is ThynkWorkspaceRoute.Tool -> {
                    if (current.categoryId == "fashion") {
                        FashionWorkspaceScreen(initialItem = current.item)
                    } else {
                        ThynkToolScreen(
                            categoryId = current.categoryId,
                            item = current.item,
                        )
                    }
                }
'''


def main() -> None:
    source = HOST.read_text(encoding="utf-8")
    if NEW in source:
        return
    if OLD not in source:
        raise RuntimeError("Could not wire THyNK-IT Fashion workspace: expected tool host block was not found")
    HOST.write_text(source.replace(OLD, NEW, 1), encoding="utf-8")


if __name__ == "__main__":
    main()
