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
`home_key_takeover`, `home_long_press_action`, `star_keycode` (313 = the remote's star; HomeKeyService opens the Home
panel while the menu is in front, because launcherx ignores it there), `colors_channel`.

## Themes
`WiiTheme` holds the 20 palettes (15 light, 5 dark; `WiiThemeTest` checks text contrast ≥ 4.5:1). `WiiBase` still
computes the console's greys and recolours them with `WiiTheme.map`, so CLASSIC is pixel-identical to upstream. The
Colors channel (`ColorsBanner` tile) opens a swatch strip in `WiiMenuView` (`openColors`): arrows preview live, OK saves
(SharedPreferences `wii_theme`), Back restores. Tile pictures that draw their own background implement
`WiiMenuView.Themed` (MiiBanner, ColorsBanner). The computed backdrop layers are cached as PNGs in
`cacheDir/wii-base` (bump `BASE_CACHE_VERSION` when WiiBase's drawing changes) so a cold start shows the panel at once.

## Build
JDK 17 (`~/vendor/jdk-17`) and the SDK in `~/vendor/android-sdk` (platforms;android-34, build-tools;34.0.0); adb in
`~/vendor/android-sdk/platform-tools`. `cd launcher && JAVA_HOME=~/vendor/jdk-17 ./gradlew assembleRelease testReleaseUnitTest lintRelease`.

## Power-on warnings
`res/values/couch_warnings.xml` (Couch 3 = the household): three aligned arrays (title, body, prompt) plus a late-night
one. `CouchWarnings.pick` chooses at each power-on: late-night 00:00–05:00, otherwise random but never the previous one
(`last_warning` in prefs). Titles ≈ 30 characters max. Preview without a power cycle:
`adb shell am start -a android.intent.action.MAIN -c android.intent.category.HOME -p com.gathin.tvhome --ez show_intro true`.

## Roadmap
- Phase 1 (done 2026-10-23): the above.
- Phase 2 (done 2026-10-23, by Claude Code at Liam's request rather than Tel): the themes above. Design preview:
  https://claude.ai/artifact/U92okk9wPE5uZ1aijfZ6LC
