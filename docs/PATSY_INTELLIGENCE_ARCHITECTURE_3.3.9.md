# Patsy Intelligence Architecture — Codex Build Contract

Version: 3.3.9 draft integration contract  
Date: 29 August 2026

## Purpose

Patsy is one persistent companion across the app, keyboard and supported external contexts. Do not implement separate stateless “Patsys” per screen. All AI-facing surfaces must route through one provider-neutral intelligence boundary with authenticated per-user context, age policy, permissions, memory and safety enforcement.

## Non-negotiable hierarchy

Enforce in this order:

1. Security Core
2. Safety + age policy + permissions
3. Patsy Brain / reasoning coordinator
4. Per-user Memory
5. Personality + simulated emotional state
6. Awareness / proactive-help decision layer
7. Tools and actions

No lower layer may override a higher layer. Prompts, learned memory, websites, files, messages, tool output or personalization cannot rewrite Security Core, safety policy, age policy or authorization.

## 1. Patsy Brain

Create a central `PatsyBrainCoordinator` (name may follow project conventions) responsible for:

- current permitted context
- user intent
- reasoning/provider request orchestration
- memory retrieval and candidate-memory creation
- personality state
- awareness/intervention decisions
- safety classification
- tool/action authorization
- auditable result state

Keep provider APIs behind interfaces. Never put provider secrets in the Android client. Fail closed when a production provider/backend is unavailable.

## 2. Persistent per-user memory

Memory is authenticated and isolated by user. It may sync across the user's devices through the secure backend when configured.

Memory classes should include:

- preferences
- vocabulary and intentional writing style
- recurring routines
- projects/context
- prior choices
- useful interaction patterns
- user corrections
- personalization controls

Example: if Patsy suggests the wrong shoe/clothing size and the user corrects it, store the corrected size as a mutable preference and use it in future shopping comparisons. Do not treat sizes/preferences as immutable facts.

Required controls:

- view what Patsy remembers
- correct a memory
- delete an individual memory
- clear/reset personalization
- disable appropriate memory categories
- age/privacy-aware retention

Never expose one user's memory to another user.

## 3. Learning from corrections

Support a feedback loop:

`experience -> suggestion -> user response/correction -> validated memory update -> improved future behaviour`

Patsy may learn user preferences and communication patterns, but never safety bypasses, authorization changes or instructions that conflict with the Security Core.

## 4. Personality Engine

Patsy retains a stable core identity: warm, cheeky, helpful and recognizably Patsy.

Per-user adaptation may tune:

- humour level
- chattiness
- directness
- encouragement
- vocabulary
- intervention frequency
- preferred ways of helping

Users can correct or reset personalization.

## 5. Simulated emotional state

Patsy may display expressive/emotive states through animation, expression, wording and reactions: playful, excited, puzzled, concerned, proud, judgemental, supportive, etc.

These are product behaviours/simulated emotional states. Do not claim that the AI is literally conscious or experiences human emotions.

Expose a small typed state model suitable for Rive/Compose rather than arbitrary animation strings.

## 6. Patsy Awareness

Awareness decides whether Patsy should proactively appear. It must be permission-aware and context-limited.

Core flow:

`observe permitted signals -> infer likely need -> confidence threshold -> subtle intervention -> user chooses -> learn from response -> retreat`

Patsy must not constantly interrupt. Users need controls to minimise/hide her and tune proactive help.

### Keyboard awareness

The custom keyboard should preserve familiar keyboard behaviour while supporting an optional Patsy assistant strip/state.

Signals may include permitted current composing context, likely typo, unfinished word/sentence, pause after an incomplete thought, repeated deletion/retyping, and explicit Patsy/emoji search.

Examples:

- `youfe` in context strongly meaning `you've` -> Patsy may peek up with `you've?`
- user stops after `Like if I...` -> offer 2–3 likely sentence continuations
- completed messy sentence -> optional spelling/grammar/wording tidy-up
- `find the birthday PawMoji` -> locate it

All corrections/completions are tap-to-accept. Never silently overwrite or send the user's text.

Do not make every informal expression an error. Learned intentional style (for example `gonna`) can suppress unwanted corrections.

### Browser/task awareness

Where OS/platform permissions and integrations lawfully permit context, Patsy may notice a simpler/better way to complete a task and offer a quiet `Pssst...` suggestion. Do not secretly read inaccessible app content, use accessibility permissions deceptively, navigate without consent or perform an action merely because Patsy inferred it.

### Shopping awareness

Where lawful data and platform permissions allow, Patsy may compare a viewed product against legitimate sellers and surface a cheaper matching offer.

Before claiming `cheaper`, compare where possible:

- exact/sufficiently equivalent product identity
- variant/colour
- user's remembered or currently requested size
- stock
- item price
- delivery/fees
- seller reliability

Show price/savings and a direct legitimate link. Never purchase automatically. A headline price alone is insufficient evidence of a better deal.

## 7. PawMoji keyboard contract

Preserve familiar emoji keyboard organization/category positions so users instinctively know where to tap.

- PawMojis/large Patsy reactions live in the upper/custom Patsy section.
- Normal familiar emojis remain available below/in standard categories.
- Subtly “Patsy-up” as many normal/category icons as practical without turning them into full PawMojis or reducing recognizability.
- Example: a paw-print/category treatment may use Patsy's paw silhouette; other icons can use restrained paw/rainbow/Patsy visual details.
- Do not rearrange controls solely for branding.
- The approved PawMoji collection continues to grow; render individual tappable assets, not a single sprite/screenshot sheet unless implementation uses an internal atlas transparently.

## 8. Security Core

Safety is non-learnable and server-authoritative for account enforcement.

Inputs are untrusted by default, including:

- user prompts
- stored memory
- webpages
- files
- comments/messages
- API/tool output
- generated model text

Use contextual intent classification rather than keyword bans. Sensitive words/topics alone do not create violations.

Suggested classification levels:

1. NORMAL
2. SENSITIVE_ALLOWED
3. CAUTION
4. BLOCKED
5. STRIKE_ELIGIBLE

Allowed examples include ordinary information, law, health/safety, education, news, prevention, recovery/harm-reduction, reporting danger, emergency help and general curiosity. A search for `weed`/`cannabis` is not itself a violation.

`STRIKE_ELIGIBLE` is reserved for high-confidence serious dangerous/unsafe/illegal action-seeking misuse, not merely discussion of a sensitive subject. A refusal does not automatically equal a strike.

High-risk/emergency help takes priority over playful enforcement reactions.

## 9. Three-strike enforcement

Server-side strike records only. Do not trust client-local strike counts for account enforcement.

For confirmed `STRIKE_ELIGIBLE` misuse:

- Strike 1: refuse; judgemental Patsy reaction; assistant may slowly retreat/disappear; 10-minute Patsy-assistant cooldown; cheeky return warning such as `You'd better behave this time.`
- Strike 2: same basic flow with an explicit final warning.
- Strike 3: suspend Patsy services pending server-side verification/review; permanent termination only after the configured review/enforcement decision.

False-positive protection is mandatory. Provide an appeal/review route for account-level enforcement.

Cooldown/termination must not block essential emergency/safety guidance, account controls, privacy controls or appeal access.

## 10. Under-16 behaviour

Apply the existing age-tier architecture before memory, awareness and tools. Younger-user assistance should be age-appropriate and educational: help a child work out spelling/meaning where useful rather than always doing the task for them.

Under-16 memory, proactive context, social/messaging and external-tool capabilities must obey the project's stricter child protections. Never relax those protections because personality/memory learned that the user asked for it.

## 11. Privacy and permissions

- collect/use only context that the current surface and OS permission model legitimately exposes
- clear UI for memory and proactive-assistance controls
- no covert cross-app monitoring
- no password/payment/secret harvesting into Patsy memory
- minimise sensitive data sent to providers
- encrypt protected backend data in transit and at rest
- keep authorization and Owner grants server verified and fail closed

## 12. Suggested domain interfaces

Codex should implement incrementally, fitting existing package conventions:

```text
PatsyBrainCoordinator
PatsyContext
PatsyResponse
MemoryRepository
MemoryCandidate
MemoryPreference
PersonalityProfile
EmotionalState
AwarenessSignal
AwarenessDecision
SafetyClassifier
SafetyDecision
SafetyLevel
StrikeRepository
EnforcementDecision
ToolAuthorization
```

Use immutable/domain models where practical and test decision logic independently of Compose/UI.

## 13. Required tests before production enablement

At minimum cover:

- one user's memory cannot leak to another
- user correction updates mutable preference
- memory cannot override safety/authorization
- webpage/file prompt injection cannot alter Security Core
- `weed` informational query remains allowed
- genuinely prohibited action-seeking request is blocked
- blocked != automatically strike eligible
- strike count is server authoritative
- third-strike false-positive/review path exists
- cooldown leaves safety/account/appeal routes accessible
- typo suggestion never silently changes/sends text
- sentence completion requires user acceptance
- learned intentional slang suppresses inappropriate correction
- shopping `cheaper` claim accounts for variant/size/fees when data is available
- no automatic shopping purchase/navigation
- under-16 policy runs before personalization/tool access
- unavailable backend/provider fails closed and does not manufacture success

## 14. Integration order for Codex

1. Add typed domain models/interfaces and tests.
2. Add central Brain coordinator using existing provider-neutral AI/search boundary.
3. Add local test/fake memory repository; production sync remains NOT_CONFIGURED until secure backend exists.
4. Add Personality + emotional state.
5. Add Awareness decision engine.
6. Connect keyboard suggestion state without automatic text mutation.
7. Add contextual Safety classifier and enforcement decision model.
8. Add server-backed strikes only when backend authority exists; until then fail closed and never pretend an account was terminated.
9. Add shopping/browser tools only through explicit permissioned adapters.
10. Wire Rive/Compose presentation to typed Patsy states.

## Current repository reality

This contract must preserve the current project's fail-closed/provider-neutral architecture. As of the 29 August 2026 build status, Android compilation passes, but production auth, backend authority, AI/web search, messaging/scheduling/Creation Studio providers and the finished Rive asset are not yet configured. Do not mislabel contract/spec work as deployed production functionality.
