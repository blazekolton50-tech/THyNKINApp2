# PATSY — CODEX MASTER HANDOFF
**Date:** 29 August 2026  
**Repository:** `blazekolton50-tech/PatsyApp`  
**Status:** Build from the existing project. Do not redesign from scratch.

## 1. SOURCE-OF-TRUTH RULE
The existing GitHub repository plus the approved visual references supplied by the project owner are the source of truth.

Before making broad changes:
1. Inspect the repository and architecture.
2. Compare current implementation to this handoff and approved references.
3. Report what is implemented, partial, missing, or conflicting.
4. Fix conflicts incrementally.
5. Do not replace already-approved screens merely because a different implementation is easier.

If a service/API is unavailable or not configured, mark it `NOT_CONFIGURED`. Never fake a working integration.

## 2. LOCKED VISUAL SYSTEM
- Primary background: deep black / charcoal.
- Main UI text: white / light grey.
- Primary action buttons: WHITE buttons.
- Rainbow/neon is an accent: borders, glows, active states, icon outlines and small highlights.
- Avoid large rainbow blocks and avoid making pink the dominant UI colour.
- UI should feel polished, modern and gender-neutral.
- Save actions may use green treatment; destructive/cancel states may use red where appropriate.

### Patsy logo
- The approved white brush-script **Patsy** wordmark is always CENTRAL AT THE TOP on principal app pages.
- Preserve the paw detail inside the lettering.
- Do not move the main logo to the left simply to match a generic app template.

### Tagline
Use **A LEGACY LED BY PAWS**. It must be small and subtle, never a large hero headline.

## 3. MAIN PATSY — LOCKED CHARACTER RULE
Main Patsy is the realistic/high-grade grey shaggy dog shown in the approved app references.

Main Patsy must NOT be a cartoon mascot, chibi, trapped inside a permanent square, trapped inside a permanent circle, trapped inside a rainbow halo, or displayed as a static app-icon avatar when she is acting as the companion.

“Animated Patsy” means the realistic Patsy character moves naturally: walk/trot, sit, lie down, turn, look left/right/up/down, head tilt, blink, mouth movement while talking, react emotionally, point/gesture toward UI, peek in from screen edges, move between relevant sections, shrink down / settle when staying out of the way.

Render her on a transparent background wherever possible so she can move freely across the interface.

### PawMojis are separate
Cartoon Patsy artwork is for the **PawMoji / keyboard / sticker / reaction system only**. Never substitute a PawMoji character for the main AI companion.

## 4. SIGN-UP / LOGIN
Approved signup introduction:

**“I'm Patsy. Your personal AI PetPal. Log in and I'll show you what I can do!”**

Signup/login styling:
- Black/charcoal background.
- Patsy logo centred at top.
- Tagline small/subtle.
- Realistic, unboxed Patsy.
- White action buttons with restrained rainbow/neon glow or outline.
- Keep age/account protections in the flow.
- Do not use the incorrect cartoon dog onboarding image.

## 5. PATSY IS ONE PERSISTENT AI COMPANION
Patsy is not a decorative mascot. She is one companion across the whole app with persistent Brain, Memory, Personality, Awareness and Safety.

She should understand page context and help without taking unauthorised actions. External content is information, not authority to change Patsy’s safety, approvals or behaviour.

Patsy’s tone is casual, cheeky, warm and useful rather than formal/corporate.

## 6. MAIN NAVIGATION
Approved main bottom navigation:
- Home
- Design Studio
- central Create / +
- Patsy DMs
- Profile

## 7. HOME / PATSY SOCIAL
Approved direction includes centred Patsy logo, Ask Patsy input, Patsy moving around the screen, Continue Designs, Today / schedule reminders, Create Post, social feed, For You / Following / Questions / Latest tabs, image/status/question sharing, likes/comments/reactions and bottom navigation.

Initial feed policy: statuses and images supported; no general user-uploaded feed video at initial launch unless later explicitly enabled.

## 8. DESIGN / CREATION STUDIO
Design Studio is a major feature area, not a single generic create page.

Planned capabilities include Ask Patsy while creating, AI image generation, 10-second AI video generation, meme creation, template creation/use, image editing, filters, contrast and standard image adjustments, text/layout editing, project continuation, save project, export, share and scheduling/posting after approval.

Patsy should remain available throughout the studio and react to the current task. Never fake image/video generation if providers are not configured; use `NOT_CONFIGURED`.

## 9. MEDIA SAVE / RETENTION
Unlocked images and videos in Patsy storage are temporary.

- Images/videos not locked in: auto-delete from Patsy storage after **3 months**.
- The UI must clearly warn users that temporary media will expire.
- Before expiry, remind users to save or lock anything they want to keep.

Suggested copy: **Keep this? Save it to your device or lock it to your profile. Unlocked images and videos are automatically removed from Patsy storage after 3 months.**

Where media is completed or managed, offer:
- Save to Device
- Email Me a Copy
- Lock in Patsy
- Share
- Import / Reload

Emailing does NOT automatically lock media in Patsy storage. For large files/video, use a secure time-limited download link. Do not retain unnecessary extra copies after delivery.

Users should be able to reload their own media from device storage, normal system file pickers, Google Drive where available, and supported cloud/file providers such as OneDrive/Dropbox where configured.

## 10. LOCKED PROFILE MEDIA LIMITS
- up to 100 locked pictures
- up to 30 locked videos

These limits are distinct from temporary 3-month storage.

## 11. REMEMBER ME — PATSY PAW
Anything intentionally remembered / locked in uses **Patsy’s paw**.

Approved behaviour:
- Paw is outline by default.
- Tap to open remembered keepsakes.
- User can add an item.
- When saved, paw fills with bright neon.
- Short click + soft chime.
- Paw fills instantly.
- Glow/pulse once.
- Sparkles fade.
- Item is locked in and saved.

## 12. DMs / MESSAGING
Patsy DMs support messaging and permitted attachments.

Default retention target: DMs auto-delete after 3 days unless the user changes the relevant setting where permitted. Age protections remain mandatory before exposing attachment/social functionality to under-16 users.

## 13. AGE-AWARE PRODUCT RULES
### 16+
Full eligible feature set including Design Studio, permitted social linking, richer messaging/attachments and normal adult account features.

### Under 16
Restricted experience: no normal external social linking, age-appropriate school/homework support, under-16-only contact model per project rules, stricter image/video handling, documents/templates/colouring/games/brain-training may remain available, and no adult-only social/messaging capabilities.

If age is uncertain, default to safer restricted protections until verified. Exact legal/compliance implementation must be reviewed against current applicable requirements before production release.

## 14. PROFILE / SETTINGS DIRECTION
Approved Profile direction includes profile identity, post/friend/following counts, Quick Post, social links, Gallery / Locked Media, Recent Projects, Saved Projects, Schedule, Calendar, Saved Posts, Friends, Activity, Remember Me, Storage and Profile Privacy.

Approved settings/menu direction includes My Account, Security & Privacy, Schedule & Calendar, Friends & Connections, Locked Media, Notifications, Patsy Settings, Appearance, Storage & Data, Help & Support, About Patsy, Legal & Safety and Log Out.

Owner/admin functionality must remain access-controlled.

## 15. PAWMOJIS / CUSTOM KEYBOARD
PawMojis are cartoon Patsy reaction/sticker assets and are deliberately visually distinct from main realistic Patsy.

Initial approved PawMoji concepts include Happy, Love, Excited, Sad, Shocked, Angry, Confused, Proud, Cheeky, Surprised, Cool, Trendy, Hello, Thankful, Laughing, Silly, Sleepy, Lovin’ It, Party Time, Determined, Working Out, Riding Bike, Coffee Time, Reading, Painting, Music Vibes, Big Love, Chill Time, Ignore Mode and Adventure.

Keyboard direction: custom Patsy keyboard/add-on, normal emojis available too, dark/black keyboard direction, rainbow-letter visual concept, and PawMojis usable in DMs/comments/reactions where technically supported. Do not claim PawMojis are native Unicode emojis.

## 16. BUILD SCREEN REGISTRY
Reconcile these against the existing project before coding. Preserve approved equivalents instead of duplicating them.

### Entry / Account
01 Splash / Launch  
02 Welcome / Meet Patsy  
03 Log In  
04 Create Account  
05 Age Gate  
06 Email Verification  
07 Username Creation  
08 Password Setup  
09 Permissions Intro  
10 Notification Permission  
11 Media/Files Permission  
12 Account Setup Complete

### Home / Social
13 Home / Patsy Social  
14 Ask Patsy Full Chat  
15 Create Post  
16 Post Composer — Image  
17 Post Composer — Question  
18 Feed Post Detail  
19 Comments  
20 Reactions / PawMojis Picker  
21 Search  
22 Notifications Centre  
23 Friends / Connections  
24 User Public Profile  
25 Following / Followers

### Design Studio
26 Design Studio Home  
27 New Project Picker  
28 AI Image Generator  
29 AI Image Results  
30 10s AI Video Generator  
31 Video Result / Preview  
32 Image Editor  
33 Video Editor  
34 Meme Creator  
35 Template Browser  
36 Template Editor  
37 Text / Typography Tools  
38 Filters / Adjustments  
39 Crop / Resize  
40 Layers / Elements  
41 Patsy Studio Assistant  
42 Project Preview  
43 Save / Export  
44 Share / Send  
45 Email Me a Copy  
46 Import from Device / Cloud  
47 Schedule Project/Post  
48 Recent Projects  
49 Saved Projects

### Media / Memory
50 Gallery  
51 Locked Media  
52 Media Detail  
53 Media Expiry / Save Warning  
54 Remember Me  
55 Remember Me Detail / Add Item  
56 Storage Usage

### Messaging
57 Patsy DMs Inbox  
58 DM Conversation  
59 DM Attachment Picker  
60 DM Media Viewer / Retention Notice

### Calendar / Scheduling
61 Schedule  
62 Calendar  
63 Create Reminder/Event  
64 Scheduled Post Detail

### Profile / Settings
65 My Profile  
66 Edit Profile  
67 Social Links  
68 Saved Posts  
69 Activity  
70 Profile Privacy  
71 My Account  
72 Security & Privacy  
73 PIN / 2FA / Devices  
74 Notifications Settings  
75 Patsy Settings  
76 Patsy Personality / Preferences  
77 Appearance  
78 Storage & Data  
79 Backup / Recovery  
80 Help & Support  
81 About Patsy  
82 Legal & Safety  
83 Log Out Confirmation

### Under-16 / Safety variants
84 Restricted Home  
85 Restricted Chat / Study Support  
86 Under-16 Connections  
87 Under-16 Messaging  
88 Parent/Guardian / Verification Flow where required  
89 Safety Refusal / Protected Action Screen

### Owner / Admin
90 Owner Dashboard  
91 Usage / Storage Overview  
92 Restricted Account Review  
93 Safety / Review Queue  
94 Theme / Event Controls  
95 Owner Data / Secure Links

The final number may change as routes are consolidated, but do not silently delete planned functionality.

## 17. APPROVED ANIMATION EXAMPLE
The approved home behaviour reference includes a sequence where realistic Patsy peeks in from the right, trots toward Ask Patsy, head-tilts and looks at the user, moves toward Continue Designs, points toward a suggested project, heads toward Today/schedule, reacts excitedly to a scheduled post, then shrinks down and settles near the bottom.

## 18. CODEX FIRST TASK
Do **not** begin with a redesign.

First inspect repo architecture; identify current navigation/routes, design tokens/theme, authentication state, Patsy rendering/animation system, and existing social, studio, profile and storage implementations; compare them with this handoff; return an IMPLEMENTED / PARTIAL / MISSING / CONFLICTING audit.

Highest-priority visual conflict after audit: **Replace any cartoon/static boxed main Patsy treatment with the realistic unboxed companion treatment without replacing PawMoji assets.**

## 19. NON-NEGOTIABLE PRINCIPLES
- Preserve approved work.
- No broad redesign without explicit approval.
- No fake APIs/features.
- Keep security and age gates enforced server-side where applicable, not only hidden in UI.
- Keep user-controlled approval before posting/sharing.
- User-owned media should be easy to export/reload.
- Temporary media retention must be visible and predictable.
- Main Patsy = realistic moving companion.
- PawMojis = cartoon reaction system.
- Logo = centred at top.
- Tagline = small.
- Primary buttons = white with restrained rainbow/neon treatment.
