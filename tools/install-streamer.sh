#!/usr/bin/env bash
# Build and install the menu on a Google TV Streamer (or any Google TV) WITHOUT disabling the stock launcher.
#
# launcher/build-and-install.sh disables launcherx, which takes the remote's star button, the side panel,
# voice search results and the cast volume pop-up with it. Here launcherx stays enabled. It outranks any
# third-party home (Android caps their HOME priority at 0), so HomeKeyService takes the Home button instead
# (home_key_takeover in config.xml), and the menu's Google TV channel hands Home back to launcherx until the
# screen next goes off.
#
# Uses the platform-tools adb, already paired and connected to the TV (wireless debugging):
#   ADB=~/vendor/android-sdk/platform-tools/adb ADB_SERIAL=<serial from `adb devices`> tools/install-streamer.sh
# Undo: adb uninstall com.gathin.tvhome (nothing else was changed; launcherx was never touched).
set -euo pipefail
cd "$(dirname "$0")/../launcher"
ADB="${ADB:-adb}"
adb_() { if [ -n "${ADB_SERIAL:-}" ]; then "$ADB" -s "$ADB_SERIAL" "$@"; else "$ADB" "$@"; fi; }
[ -f local.properties ] || { echo "ERROR: launcher/local.properties needs sdk.dir=<Android SDK>" >&2; exit 1; }
./gradlew assembleRelease -q --console=plain
APK=app/build/outputs/apk/release/app-release.apk
adb_ install -r "$APK" | tail -n 1
START=$(adb_ shell 'am start -W -n com.gathin.tvhome/.WiiMenuActivity')
printf '%s\n' "$START" | grep -qF 'Status: ok' || { printf '%s\n' "$START" >&2; echo "ERROR: the menu did not start." >&2; exit 1; }
# Android switches accessibility services off on every reinstall, so HomeKeyService is put back each time,
# appended to whatever other services are on.
adb_ shell 'pm grant com.gathin.tvhome android.permission.ACCESS_FINE_LOCATION 2>/dev/null
appops set com.gathin.tvhome WRITE_SETTINGS allow
cmd package compile -m speed -f com.gathin.tvhome >/dev/null
cmd package set-home-activity --user 0 com.gathin.tvhome/.WiiMenuActivity >/dev/null
svc=com.gathin.tvhome/.HomeKeyService
s=$(settings get secure enabled_accessibility_services)
case "$s" in *HomeKeyService*) ;; null|"") settings put secure enabled_accessibility_services $svc ;; *) settings put secure enabled_accessibility_services "$s:$svc" ;; esac
settings put secure accessibility_enabled 1
pm list packages -d | grep -q launcherx && echo "WARNING: launcherx is disabled; the star button and side panel need it: pm enable com.google.android.apps.tv.launcherx"
echo "HomeKeyService: $(settings get secure enabled_accessibility_services)"'
adb_ shell 'am start -a android.intent.action.MAIN -c android.intent.category.HOME -p com.gathin.tvhome' >/dev/null
echo "Installed. Press Home on the remote to check that it lands on the menu."
