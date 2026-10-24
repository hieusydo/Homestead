#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"
bash build-local.sh
TV="${TV:?Set TV to your television IP:5555}"
adb connect "$TV"
adb -s "$TV" install -r homestead.apk
adb -s "$TV" shell pm grant com.cleantv.outfit android.permission.READ_LOGS
adb -s "$TV" shell settings put secure enabled_accessibility_services com.cleantv.outfit/com.cleantv.outfit.HomeRedirectService
adb -s "$TV" shell settings put secure accessibility_enabled 1
adb -s "$TV" shell am force-stop com.homestead.launcher
adb -s "$TV" shell am start -n com.cleantv.outfit/.MainActivity
