"""Build the Patsy quick-shrink donor video from real local Patsy assets.

This is a donor/reference build utility. It does not replace the native Android
Patsy controller/Rive boundary in THyNK-IN!.

Required local assets under donors/patsy-gbt/assets/:
  patsy_shrink_00.png ... patsy_shrink_11.png
  video4635308202773325454.mp4   # rainbow/glitter effect

Contract:
  artboard/canvas: 720x720
  Big: 300 px visual size / scale 1.0
  Mini: 150 px visual size / scale 0.5
  shrink: 0.8 s
  mission run: 0.4 s

The transparent Patsy PNG sequence is authoritative for the shrink. The rainbow
video is treated only as an effect layer; it is never presented as Patsy.
"""

from __future__ import annotations

from pathlib import Path
import math
import sys

try:
    # MoviePy 2.x
    from moviepy import CompositeVideoClip, ImageClip, VideoFileClip
except ImportError:  # pragma: no cover - compatibility with MoviePy 1.x
    from moviepy.editor import CompositeVideoClip, ImageClip, VideoFileClip

ROOT = Path(__file__).resolve().parent
ASSETS = ROOT / "assets"
OUTPUT = ROOT / "patsy_REAL_QUICK_FINAL.mp4"

CANVAS = (720, 720)
BIG_PX = 300
MINI_PX = 150
FRAME_COUNT = 12
SHRINK_SECONDS = 0.8
RUN_SECONDS = 0.4
TOTAL_SECONDS = SHRINK_SECONDS + RUN_SECONDS
RAINBOW_FILE = ASSETS / "video4635308202773325454.mp4"


def frame_path(index: int) -> Path:
    return ASSETS / f"patsy_shrink_{index:02d}.png"


def require_assets() -> None:
    missing = [frame_path(i) for i in range(FRAME_COUNT) if not frame_path(i).is_file()]
    if not RAINBOW_FILE.is_file():
        missing.append(RAINBOW_FILE)
    if missing:
        print("Patsy build stopped. Required real source assets are missing:", file=sys.stderr)
        for item in missing:
            print(f"  - {item.relative_to(ROOT)}", file=sys.stderr)
        raise SystemExit(2)


def _duration(clip, seconds: float):
    return clip.with_duration(seconds) if hasattr(clip, "with_duration") else clip.set_duration(seconds)


def _start(clip, seconds: float):
    return clip.with_start(seconds) if hasattr(clip, "with_start") else clip.set_start(seconds)


def _position(clip, pos):
    return clip.with_position(pos) if hasattr(clip, "with_position") else clip.set_position(pos)


def _resize(clip, *, width: int | None = None, height: int | None = None):
    if hasattr(clip, "resized"):
        return clip.resized(width=width, height=height)
    if width is not None:
        return clip.resize(width=width)
    return clip.resize(height=height)


def _subclip(clip, start: float, end: float):
    if hasattr(clip, "subclipped"):
        return clip.subclipped(start, end)
    return clip.subclip(start, end)


def build_quick_shrink() -> Path:
    require_assets()

    # Rainbow is an effect layer only. Speed up enough source material to fit
    # the canonical 0.8-second shrink window, without claiming it is Patsy.
    rainbow = VideoFileClip(str(RAINBOW_FILE))
    usable = min(rainbow.duration, 6.4)
    rainbow = _subclip(rainbow, 0, usable)
    if hasattr(rainbow, "with_speed_scaled"):
        rainbow = rainbow.with_speed_scaled(usable / SHRINK_SECONDS)
    else:
        from moviepy.video.fx import speedx
        rainbow = rainbow.fx(speedx, usable / SHRINK_SECONDS)
    rainbow = _resize(rainbow, width=CANVAS[0])
    rainbow = _duration(rainbow, SHRINK_SECONDS)
    rainbow = _position(rainbow, ("center", "center"))

    clips = [rainbow]
    frame_duration = SHRINK_SECONDS / FRAME_COUNT

    # The supplied transparent sequence is authoritative: 00=Big, 11=Mini.
    for index in range(FRAME_COUNT):
        progress = index / (FRAME_COUNT - 1)
        visual_size = round(BIG_PX + (MINI_PX - BIG_PX) * progress)
        bounce = math.sin(index * 1.2) * 6.0

        patsy = ImageClip(str(frame_path(index)))
        patsy = _resize(patsy, width=visual_size)
        patsy = _duration(patsy, frame_duration)
        patsy = _start(patsy, index * frame_duration)
        patsy = _position(
            patsy,
            (lambda _t, y=bounce: ((CANVAS[0] - visual_size) / 2, (CANVAS[1] - visual_size) / 2 + y)),
        )
        clips.append(patsy)

    # Mission travel: keep the final real transparent Patsy frame at Mini size
    # and move her to the right over 0.4 seconds with a small running bounce.
    mini = ImageClip(str(frame_path(FRAME_COUNT - 1)))
    mini = _resize(mini, width=MINI_PX)
    mini = _duration(mini, RUN_SECONDS)
    mini = _start(mini, SHRINK_SECONDS)

    start_x = (CANVAS[0] - MINI_PX) / 2
    start_y = (CANVAS[1] - MINI_PX) / 2

    def mission_position(t: float):
        progress = max(0.0, min(1.0, t / RUN_SECONDS))
        x = start_x + progress * (CANVAS[0] - start_x + MINI_PX)
        y = start_y + math.sin(t * 36.0) * 5.0
        return (x, y)

    mini = _position(mini, mission_position)
    clips.append(mini)

    final = CompositeVideoClip(clips, size=CANVAS)
    final = _duration(final, TOTAL_SECONDS)
    final.write_videofile(
        str(OUTPUT),
        fps=30,
        codec="libx264",
        audio=False,
        pixel_format="yuv420p",
    )

    print(f"Built {OUTPUT.name}: real Patsy frames, 300→150 px in 0.8 s + 0.4 s mission run")
    return OUTPUT


if __name__ == "__main__":
    build_quick_shrink()
