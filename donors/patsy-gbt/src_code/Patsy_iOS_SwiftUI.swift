import SwiftUI
import Combine

/// Donor/reference iOS implementation of Patsy's canonical quick shrink.
///
/// Contract:
/// - 720x720 transparent Patsy source stage
/// - Big = 300 px visual size / 2 thumbs
/// - Mini = 150 px visual size / 1 thumb
/// - 12 authored transparent frames: `patsy_shrink_00` ... `patsy_shrink_11`
/// - shrink duration = exactly 0.8 s
/// - mission run = 0.4 s
///
/// The PNG sequence itself owns the Big -> Mini visual progression. We do not
/// apply a second 1.0 -> 0.5 scale while swapping frames, which would shrink
/// an already-authored shrink sequence twice.
struct PatsyView: View {
    var onMissionStart: () -> Void = {}

    @State private var frameIndex = 0
    @State private var showRainbow = false
    @State private var runOff: CGFloat = 0
    @State private var bounce: CGFloat = 0
    @State private var isShrinking = false
    @State private var shrinkStartedAt: Date?
    @State private var missionToken = UUID()

    private let shrinkDuration: TimeInterval = 0.8
    private let runDuration: TimeInterval = 0.4

    private let displayTimer = Timer.publish(
        every: 1.0 / 60.0,
        on: .main,
        in: .common
    ).autoconnect()

    var body: some View {
        ZStack {
            Color.black.ignoresSafeArea()

            if showRainbow {
                RainbowView(progress: CGFloat(frameIndex) / 11.0)
                    .frame(width: 720, height: 720)
                    .allowsHitTesting(false)
            }

            Image("patsy_shrink_\(String(format: "%02d", frameIndex))")
                .resizable()
                .scaledToFit()
                .frame(width: 300, height: 300)
                .offset(x: runOff, y: bounce)
                .onReceive(displayTimer) { now in
                    updateShrinkFrame(at: now)
                }

            VStack {
                Spacer()

                Button("Shrink & Run to Mission (0.8s quick)") {
                    quickShrinkAndRun()
                }
                .padding()
                .background(Color(red: 1, green: 0.31, blue: 1))
                .foregroundColor(.white)
                .clipShape(Capsule())
                .disabled(isShrinking)

                Button("Expand to Big") {
                    expandToBig()
                }
                .padding(.bottom, 30)
            }
        }
    }

    private func updateShrinkFrame(at now: Date) {
        guard isShrinking, let started = shrinkStartedAt else { return }

        let elapsed = now.timeIntervalSince(started)
        let progress = max(0.0, min(1.0, elapsed / shrinkDuration))
        let nextFrame = min(11, Int(floor(progress * 12.0)))

        frameIndex = nextFrame
        bounce = sin(CGFloat(nextFrame) * 1.2) * 6.0

        if progress >= 1.0 {
            isShrinking = false
            showRainbow = false
            frameIndex = 11
            bounce = 0
        }
    }

    private func quickShrinkAndRun() {
        let token = UUID()
        missionToken = token

        frameIndex = 0
        runOff = 0
        bounce = 0
        showRainbow = true
        isShrinking = true
        shrinkStartedAt = Date()

        DispatchQueue.main.asyncAfter(deadline: .now() + shrinkDuration) {
            guard missionToken == token else { return }

            isShrinking = false
            showRainbow = false
            frameIndex = 11
            bounce = 0

            withAnimation(.easeIn(duration: runDuration)) {
                runOff = 500
            }

            DispatchQueue.main.asyncAfter(deadline: .now() + runDuration) {
                guard missionToken == token else { return }
                onMissionStart()
            }
        }
    }

    private func expandToBig() {
        missionToken = UUID()
        isShrinking = false
        shrinkStartedAt = nil
        showRainbow = false
        bounce = 0

        withAnimation(.easeOut(duration: 0.6)) {
            runOff = 0
        }

        frameIndex = 0
    }
}

struct RainbowView: View {
    var progress: CGFloat

    var body: some View {
        Canvas { context, _ in
            let colors: [Color] = [
                .pink,
                .orange,
                .yellow,
                .green,
                .blue,
                .purple,
                .pink
            ]

            for k in 0..<20 {
                let angle = (Double(k * 18) + Double(progress * 720)) * .pi / 180
                let particleProgress = (Double(progress) + Double(k) * 0.05)
                    .truncatingRemainder(dividingBy: 1)
                let radius = 80 + particleProgress * 250
                let x = 360 + cos(angle) * radius
                let y = 360 + sin(angle) * radius * 0.8 - particleProgress * 100
                let particleSize = 6 + particleProgress * 8

                let rect = CGRect(
                    x: x - particleSize / 2,
                    y: y - particleSize / 2,
                    width: particleSize,
                    height: particleSize
                )

                context.fill(
                    Circle().path(in: rect),
                    with: .color(colors[k % colors.count].opacity(1 - particleProgress * 0.5))
                )
            }
        }
    }
}
