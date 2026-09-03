export enum PatsyBodyState {
  Idle = 'Idle',
  Walking = 'Walking',
  Running = 'Running',
  Sitting = 'Sitting',
  Lying = 'Lying',
  Standing = 'Standing',
}

export enum PatsyVoiceState {
  Silent = 'Silent',
  Listening = 'Listening',
  Thinking = 'Thinking',
  Speaking = 'Speaking',
  Laughing = 'Laughing',
}

export enum PatsyActionState {
  None = 'None',
  Wave = 'Wave',
  Point = 'Point',
  Jump = 'Jump',
  Peek = 'Peek',
  CoverEyes = 'CoverEyes',
  Celebrate = 'Celebrate',
  Shrink = 'Shrink',
  Expand = 'Expand',
}

export enum PatsyEmotionState {
  Neutral = 'Neutral',
  Happy = 'Happy',
  Curious = 'Curious',
  Focused = 'Focused',
  Concerned = 'Concerned',
  Shy = 'Shy',
  Judging = 'Judging',
  Baffled = 'Baffled',
}

export enum PatsyAttentionState {
  Neutral = 'Neutral',
  User = 'User',
  Camera = 'Camera',
  UIControl = 'UIControl',
  WorldTarget = 'WorldTarget',
  AIExplicit = 'AIExplicit',
}

export enum PatsySizeState {
  Big = 'Big',
  Mini = 'Mini',
}

export interface PatsyState {
  body: PatsyBodyState;
  voice: PatsyVoiceState;
  action: PatsyActionState;
  emotion: PatsyEmotionState;
  attention: PatsyAttentionState;
  size: PatsySizeState;
  scale: number;
}

export interface PatsyMissionTarget {
  x?: number;
  y?: number;
  attention?: PatsyAttentionState;
}

export type PatsyStateListener = (state: Readonly<PatsyState>) => void;

const SHRINK_MS = 800;
const RUN_MS = 400;
const BIG_SCALE = 1.0;
const MINI_SCALE = 0.5;

const nowMs = () =>
  typeof performance !== 'undefined' && typeof performance.now === 'function'
    ? performance.now()
    : Date.now();

/**
 * Donor/reference state machine shared by web builders.
 *
 * The native THyNK-IN! Android controller remains production authority.
 * This class mirrors the canonical behaviour without claiming that any named
 * Drive asset exists until the resource loader verifies it.
 */
export class PatsyStateMachine {
  state: PatsyState = {
    body: PatsyBodyState.Sitting,
    voice: PatsyVoiceState.Silent,
    action: PatsyActionState.None,
    emotion: PatsyEmotionState.Neutral,
    attention: PatsyAttentionState.Neutral,
    size: PatsySizeState.Big,
    scale: BIG_SCALE,
  };

  private timer: ReturnType<typeof setTimeout> | null = null;
  private missionTimer: ReturnType<typeof setTimeout> | null = null;
  private listener?: PatsyStateListener;
  private generation = 0;

  constructor(listener?: PatsyStateListener) {
    this.listener = listener;
  }

  setListener(listener?: PatsyStateListener) {
    this.listener = listener;
    this.emit();
  }

  private emit() {
    this.listener?.({ ...this.state });
  }

  private clearTimers() {
    if (this.timer != null) clearTimeout(this.timer);
    if (this.missionTimer != null) clearTimeout(this.missionTimer);
    this.timer = null;
    this.missionTimer = null;
  }

  cancelCurrentAction() {
    this.generation += 1;
    this.clearTimers();
  }

  quickShrinkAndRunToMission(
    onMissionStart: () => void,
    target: PatsyMissionTarget = { attention: PatsyAttentionState.UIControl },
  ) {
    this.cancelCurrentAction();
    const generation = this.generation;
    const started = nowMs();

    this.state.action = PatsyActionState.Shrink;
    this.state.size = PatsySizeState.Big;
    this.state.scale = BIG_SCALE;
    this.state.attention = target.attention ?? PatsyAttentionState.UIControl;
    this.emit();

    const tick = () => {
      if (generation !== this.generation) return;

      const elapsed = nowMs() - started;
      const progress = Math.max(0, Math.min(1, elapsed / SHRINK_MS));
      this.state.scale = BIG_SCALE + (MINI_SCALE - BIG_SCALE) * progress;
      this.emit();

      if (progress < 1) {
        this.timer = setTimeout(tick, 16);
        return;
      }

      this.timer = null;
      this.state.size = PatsySizeState.Mini;
      this.state.scale = MINI_SCALE;
      this.state.body = PatsyBodyState.Running;
      this.state.action = PatsyActionState.None;
      this.emit();

      this.missionTimer = setTimeout(() => {
        if (generation !== this.generation) return;
        this.missionTimer = null;
        onMissionStart();
      }, RUN_MS);
    };

    tick();
  }

  expandToBig() {
    this.cancelCurrentAction();
    this.state.action = PatsyActionState.Expand;
    this.state.body = PatsyBodyState.Standing;
    this.state.size = PatsySizeState.Big;
    this.state.scale = BIG_SCALE;
    this.state.attention = PatsyAttentionState.Neutral;
    this.emit();

    this.state.action = PatsyActionState.None;
    this.emit();
  }

  pointAtUIControl() {
    this.state.action = PatsyActionState.Point;
    this.state.attention = PatsyAttentionState.UIControl;
    this.emit();
  }

  setEmotion(emotion: PatsyEmotionState) {
    this.state.emotion = emotion;
    this.emit();
  }

  /**
   * Returns the expected donor asset name only. Availability must be verified
   * by the resource loader before use.
   */
  getPreferredAssetName() {
    switch (this.state.emotion) {
      case PatsyEmotionState.Judging:
        return 'patsy_judging.mp4';
      case PatsyEmotionState.Shy:
        return 'photo3491664879986803385.jpeg';
      default:
        return 'patsy_base_idle.mp4';
    }
  }
}

export const PatsyQuickShrinkContract = Object.freeze({
  artboard: 'Patsy',
  width: 720,
  height: 720,
  bigPx: 300,
  miniPx: 150,
  bigScale: BIG_SCALE,
  miniScale: MINI_SCALE,
  shrinkMs: SHRINK_MS,
  missionRunMs: RUN_MS,
  frameCount: 12,
});
