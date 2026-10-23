# wii-tv (LRDOC fork) — agent notes

Fork of MauroProto/wii-tv (`upstream` remote) for Liam's **Google TV Streamer** (kirkwood, Android 14, 1920x1080 UI).
The home screen is `WiiMenuActivity` + `WiiMenuView` (drawn in code; colours in `WiiPalette`/`WiiBase`).
`MainActivity` is the older cleanyourtv launcher and is not used here.

## Hard rules
- **Never disable `com.google.android.apps.tv.launcherx`.** It runs the remote's star button (→ Home panel),
  the long-press-Home side panel, voice search results and the cast volume pop-up. Do not use
  `launcher/build-and-install.sh` (it disables it) or any `examples/*/debloat.txt` (the Philips list disables the
  Google Photos screensaver). Install with `tools/install-streamer.sh`.
- Never disable `com.google.android.tungsten.setupwraith` (owns the saved Wi-Fi network).
- Do not touch katniss (voice/Gemini), dreamx (screensaver + Home panel) or mediashell (casting).

## How Home works here
launcherx outranks any third-party home (Android caps third-party HOME priority at 0), so `set-home-activity`
alone does nothing. `HomeKeyService` (accessibility, `home_key_takeover`) takes KEYCODE_HOME: press → the menu,
long press → `home_long_press_action` (Google's side panel). The **Google TV** channel (`stock_home_component`)
sets `stock_mode` in prefs: Home then passes through to launcherx until the screen goes off (or the next boot).
Android turns the accessibility service off on every reinstall; `install-streamer.sh` turns it back on.
Keys injected with `adb shell input` never reach accessibility services, so Home behaviour needs the real remote.

## Fork config (`launcher/app/src/main/res/values/config.xml`)
`games_channel` false, `wii_sd_button` false (no media player), `home_panel_action`/`home_panel_package` (envelope →
Google Home panel, the same intent the star button sends), `stock_home_component`/`stock_home_label`,
`home_key_takeover`, `home_long_press_action`.

## Build
JDK 17 (`~/vendor/jdk-17`) and the SDK in `~/vendor/android-sdk` (platforms;android-34, build-tools;34.0.0); adb in
`~/vendor/android-sdk/platform-tools`. `cd launcher && JAVA_HOME=~/vendor/jdk-17 ./gradlew assembleRelease testReleaseUnitTest lintRelease`.

## Roadmap
- Phase 1 (done 2026-10-23): the above.
- Phase 2 (Tel, needs its own plan first): 15 light colour themes (tinted backgrounds: light pink, light purple, …)
  and 5 dark modes, text colour adjusted per theme for contrast, chosen on the TV, persisted across power cycles.
