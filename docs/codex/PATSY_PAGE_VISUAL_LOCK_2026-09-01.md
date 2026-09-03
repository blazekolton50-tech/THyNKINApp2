# Patsy Page Visual Lock — 2026-09-01

## Status

SAVE MAIN APP / SAVE LOCK IN

This document records the latest owner-supplied page compositions and the production implementation meaning of those references. It supplements, and does not weaken, the existing navigation/auth/security/design preservation contracts.

Visual screenshots control composition and intended interaction. Donor HTML/React/Tailwind/WebAudio code is reference/recovery material only unless a behavior is deliberately ported into the native Kotlin/Compose architecture.

## Global shell lock

Authenticated semantic destinations:

`HOME · THyNK · CAMERA · PATSY DMS · PROFILE`

Visible shared bar:

`Home · [coloured THyNK only] · [large + only] · PDMs · Profile`

The centre `+` is Camera semantically. Do not show a CAMERA caption below it. Keep one shared homebar across authenticated pages, nested THyNK/editor/Music screens, Profile, Owner-authorized pages and authenticated protected states.

Homebar geometry:
- black/charcoal base
- thin restrained rainbow separator
- straight outer runs
- smooth raised centre arch around the Camera +
- large circular white centre control with restrained rainbow ring/glow
- no duplicate page-owned primary navigation

## Profile — current composition target

Top area:
- approved Patsy wordmark centred
- notifications button
- top-right compact account/menu control
- profile image with restrained rainbow ring
- camera/edit affordance on profile image
- display name
- handle
- bio

Profile summary:
- Projects
- Followers
- Following
- Likes

Statistics must be real backend-derived values. Never ship reference/sample counts as facts. If data is absent, show a truthful zero/empty/loading state according to product rules.

Primary profile action:
- rainbow-outline `Edit Profile`

Profile sections:
- `About Me`
- `Quick Post / Creator Tools`
- `Recent Projects`
- `Saved Projects`

Quick Post / Creator Tools:
- Image
- Video
- Document
- Template
- More

These route into the existing shared THyNK/Create architecture. Do not create parallel editors from Profile.

Recent Projects:
- use real account-scoped project data
- project thumbnail only when a real preview/asset exists
- truthful project type/status
- real modified timestamps
- View all routes to the shared Projects/Library destination

Saved Projects:
- account-scoped folders/albums/projects
- real counts only
- View all routes to shared project/storage architecture

Do not ship fake verification badges, fake engagement counts, fake folders, fake project cards or sample dog/media thumbnails as production user data.

## Expanded Profile / Owner menu

This is the larger profile/menu surface, not a replacement for compact top-right account access.

### ACCOUNT

- Scheduling — `Plan your posts`
- Calendar — `Events & reminders`
- Friends — `Connect & collaborate`
- Locked Media — `Private & secure`
- THyNK Storage — `Put-Away Portal`

### PREFERENCES

- Settings — `App preferences`
- Account Info — `Personal details`
- Theme — `Dark (Default)`
- Notifications — `Manage alerts`

### SUPPORT

- Help & Support — `Get help anytime`
- About Patsy
- Log Out

Visual treatment:
- black/charcoal panel
- subdued fine borders
- white primary copy
- grey secondary copy
- pink section headings
- simple white line icons
- restrained purple/rainbow accents
- destructive/logout action in pink/red family

Authority rules:
- menu visibility never grants Owner authority
- Owner tools/actions remain server-capability checked
- every privileged action rechecks backend authority
- Theme/Notifications/preferences are WORKING only when real persisted settings exist

## Patsy DMs — current responsive lock

### Phone/narrow width

Inbox first, then conversation view.

Inbox includes:
- `PATSY DMs`
- supportive subtitle/copy
- search
- filters: All / Unread / Friends / Groups / Archived
- conversation cards
- unread badges
- new-message/compose affordance
- privacy/retention explanation
- permanent shared bottom bar

### Wide phone / tablet / foldable / landscape

Use one split-view messaging page:
- conversation list on the left
- selected/open conversation on the right
- no unnecessary duplicate destination/page

Conversation view includes:
- participant/group identity
- conversation info
- approved video-call entry control
- approved call entry control where reference requires it
- real message history
- image/media messages when real attachments exist
- composer
- attachment affordance
- emoji affordance
- send action

Calling rule:
- call/video-call UI may exist as an approved entry point
- provider-backed calling stays disabled / `NOT_CONFIGURED` until a real secure provider and authorization path are integrated and verified
- never simulate a successful call

Messaging truth rules:
- no fake users
- no fabricated messages
- no delayed automatic fake replies
- no fake delivered/read states
- no local-only membership authority
- unread state must reconcile with real backend state

Production data path must use existing Supabase DM membership/message/attachment contracts, realtime, block/report rules, age/protected-mode gates, notifications and retention.

Default DM retention target remains approximately three days unless a permitted real setting/lock exception changes it. Server cleanup must enforce retention; UI copy alone is not enforcement.

## THyNK Studio — Image / Design editor visual lock

This is a professional creation workspace, not a simplified toy canvas.

### Core layout

- dark/charcoal application shell
- professional white/light-neutral active canvas/work area
- centred/approved THyNK branding where the design calls for it
- permanent shared app homebar
- no separate app identity

### Top editor controls

- Back
- project name/state
- Undo
- Redo
- help/info
- share/export entry
- More
- truthful autosave/sync state

### Primary design tools

- Templates
- Upload
- Photos
- Elements
- Text
- AI Generate
- Stickers
- Draw
- Shapes
- Background
- Frames
- More

### Canvas controls

- rulers
- guides
- grid
- snapping
- object selection
- move
- resize
- rotate
- crop where relevant
- opacity
- position
- size
- layer ordering
- Bring Forward
- Send Backward
- Align
- Distribute
- Lock
- Group/Ungroup
- Duplicate
- Delete
- zoom/viewport

### Layers/properties

Layers panel supports truthful:
- object/layer identity
- visibility
- lock
- reorder
- selection

Properties panel supports applicable:
- X/Y position
- width/height
- rotation
- opacity
- type-specific properties

### Template flow

Locked route shape:

`THyNK Studio → Category → Subcategory → Template/Starter Choice → Preview → Customize/Use Template → Correct Design Space Mode`

The supplied template screens establish the structural browser behavior. Counts such as `10,000+ Designs`, `1000+ Fonts` and category counts are not implementation evidence. Only show real catalogue counts.

Reference capabilities such as AI generation, Magic Resize and Background Remover remain status controlled. If no real engine/provider exists, use `NOT_CONFIGURED` or truthful unavailable state rather than a decorative working button.

### Responsive behavior

Large screens may expose persistent left tools and right layers/properties simultaneously.

Phones must reorganize the same underlying tool/state model into trays, sheets or panels. Do not remove core editing state merely to fit the screen.

## THyNK Studio — Music editor visual lock

The Music workspace belongs to the same THyNK Studio and shared app shell.

### Main timeline workspace

- multitrack timeline
- timeline ruler
- playhead
- track lanes
- real waveforms/MIDI/automation visuals only when derived from real track data
- example roles may include Vocals, Beat, Bass, Keys, Guitar and FX

### Track operations

- Add Track
- delete track
- reorder track
- mute
- solo
- volume
- pan
- track inspection

### Quick edit operations

- Select
- Split
- Trim
- Cut
- Copy
- Paste
- Delete
- Duplicate

### Audio operations

- Volume
- Pan
- Mute
- Solo
- Fade In
- Fade Out
- Reverse
- Stretch
- Pitch
- EQ
- Effects
- Automation
- More

Only enable an operation when the Android production audio engine genuinely performs it on project audio state.

### Transport / project controls

- play
- stop/pause
- time position/duration
- zoom
- BPM
- Tap tempo where implemented
- key signature where project metadata supports it
- master volume
- meters driven by real audio
- L/R balance where implemented
- autosave
- history/undo
- project backup
- cloud sync only when real

### Templates inside Music

Music template/category/subcategory discovery is part of the shared THyNK catalogue, not a second template system.

### Sound library

The uploaded `music-clips-100-original (1).zip` is a local sound-library candidate containing 100 WAV assets. Preserve rather than regenerate. Promotion requires byte/audio validation, stable identity/checksums, provenance/license/originality evidence, duplicate/supersession review and Android decode compatibility.

### Export/provider truth

The visual reference includes WAV/MP3/AAC/FLAC/share/cloud concepts. Advertise only formats actually encoded and verified by Android production code.

Remain incomplete/`NOT_CONFIGURED` where no real verified pipeline exists:
- AI music generation
- provider-backed lyrics/vocal processing
- provider mastering
- unsupported audio encoders
- stems export
- external share/publish destinations without real integration

No mock alerts or format-selection-only success states.

## THyNK logos / branding

Never recreate THyNK or THyNK Music with CSS text, system font, SVG `<text>` or approximate lettering.

Use only the exact approved assets after their actual bytes and identity are verified. Until then, retain the current truthful fallback rather than manufacturing a replacement.

Exact capitalization is `THyNK`.

## Patsy companion on these pages

Main Patsy remains the realistic companion, not PawMoji/cartoon art.

Patsy must not cover:
- active text fields
- primary CTA
- timeline controls
- canvas selection handles
- layers/properties controls
- homebar hit targets

On dense editing pages Patsy may use MINI / GUIDE / PEEK / REST presentation and move/reposition according to the existing companion contract. Do not fabricate production Rive behavior while the authored `.riv` remains unverified.

## Truth vocabulary

Use only:
- VERIFIED
- PARTIAL
- NOT IMPLEMENTED
- BLOCKED
- LOCKED DESIGN

Provider-facing capability state may additionally use the established `NOT_CONFIGURED` boundary.

A screenshot, donor component, catalogue label or button is not proof of production functionality.
