import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const ROOT = path.dirname(fileURLToPath(import.meta.url));
const ASSETS = path.join(ROOT, 'assets');
const OUTPUT = path.join(ROOT, 'patsy_lottie.json');
const FRAME_COUNT = 12;
const FPS = 15;
const WIDTH = 720;
const HEIGHT = 720;

function frameName(index) {
  return `patsy_shrink_${String(index).padStart(2, '0')}.png`;
}

function pngDimensions(filePath) {
  const data = fs.readFileSync(filePath);
  const signature = '89504e470d0a1a0a';
  if (data.subarray(0, 8).toString('hex') !== signature) {
    throw new Error(`${path.basename(filePath)} is not a valid PNG file`);
  }
  if (data.subarray(12, 16).toString('ascii') !== 'IHDR') {
    throw new Error(`${path.basename(filePath)} has no PNG IHDR header`);
  }
  return {
    width: data.readUInt32BE(16),
    height: data.readUInt32BE(20),
  };
}

function requireFrames() {
  const frames = Array.from({ length: FRAME_COUNT }, (_, index) => {
    const name = frameName(index);
    const filePath = path.join(ASSETS, name);
    if (!fs.existsSync(filePath)) {
      throw new Error(`Missing real Patsy frame: assets/${name}`);
    }
    const dimensions = pngDimensions(filePath);
    return { name, ...dimensions };
  });

  const first = frames[0];
  const mismatch = frames.find(
    (frame) => frame.width !== first.width || frame.height !== first.height,
  );
  if (mismatch) {
    throw new Error(
      `Patsy frame dimensions must match. ${mismatch.name} is ${mismatch.width}x${mismatch.height}; expected ${first.width}x${first.height}.`,
    );
  }
  return frames;
}

function buildLottieFromPatsy() {
  const frames = requireFrames();
  const imageWidth = frames[0].width;
  const imageHeight = frames[0].height;

  const lottie = {
    v: '5.7.4',
    fr: FPS,
    ip: 0,
    op: FRAME_COUNT,
    w: WIDTH,
    h: HEIGHT,
    nm: 'Patsy Quick Shrink — Big 300px to Mini 150px',
    ddd: 0,
    meta: {
      contract: {
        artboard: 'Patsy',
        bigPx: 300,
        miniPx: 150,
        durationSeconds: 0.8,
        authoredFrameProgression: true,
      },
    },
    assets: frames.map((frame, index) => ({
      id: `image_${index}`,
      w: frame.width,
      h: frame.height,
      u: 'assets/',
      p: frame.name,
      e: 0,
    })),
    layers: frames.map((frame, index) => ({
      ddd: 0,
      ind: index + 1,
      ty: 2,
      nm: frame.name.replace('.png', ''),
      refId: `image_${index}`,
      sr: 1,
      ks: {
        o: { a: 0, k: 100 },
        r: { a: 0, k: 0 },
        p: {
          a: 0,
          k: [WIDTH / 2, HEIGHT / 2 + Math.sin(index * 1.2) * 6, 0],
        },
        a: { a: 0, k: [imageWidth / 2, imageHeight / 2, 0] },
        // The 12 PNGs already encode Big -> Mini. Keep 100% here to avoid
        // applying a second 100 -> 50% shrink on top of the authored frames.
        s: { a: 0, k: [100, 100, 100] },
      },
      ao: 0,
      ip: index,
      op: index + 1,
      st: index,
      bm: 0,
    })),
  };

  fs.writeFileSync(OUTPUT, `${JSON.stringify(lottie, null, 2)}\n`, 'utf8');
  console.log(
    `Built ${path.basename(OUTPUT)} from 12 verified real Patsy PNGs at 15 fps (0.8 s).`,
  );
}

buildLottieFromPatsy();
