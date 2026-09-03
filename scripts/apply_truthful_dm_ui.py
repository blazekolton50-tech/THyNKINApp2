from pathlib import Path

path = Path("app/src/main/java/com/patsy/app/ui/finaldesign/FinalProfileDmScreens.kt")
text = path.read_text(encoding="utf-8")
old = "if (thread.unreadCount > 0) Text(thread.unreadCount.toString(), color = FinalWhite, fontSize = 11.sp)"
new = "thread.unreadCount?.takeIf { it > 0 }?.let { unread -> Text(unread.toString(), color = FinalWhite, fontSize = 11.sp) }"
if new not in text:
    if old not in text:
        raise SystemExit("Expected PDM unread badge anchor missing; refusing blind UI rewrite")
    text = text.replace(old, new, 1)
path.write_text(text, encoding="utf-8")
print("Truthful nullable PDM unread state integrated")
