#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"
bash build-local.sh
TV="${TV:?Set TV to your television IP:5555}"
adb connect "$TV"
adb -s "$TV" install -r homestead.apk
adb -s "$TV" shell pm grant com.cleantv.outfit android.permission.READ_LOGS
python3 outfit/configure-home.py "$TV" outfit
adb -s "$TV" shell am force-stop com.homestead.launcher
adb -s "$TV" shell am start -n com.cleantv.outfit/.MainActivity
