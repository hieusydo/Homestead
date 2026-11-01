#!/bin/sh
set -eu
TV="${TV:?Set TV to your television IP:5555}"
python3 "$(dirname "$0")/outfit/configure-home.py" "$TV" original
adb -s "$TV" shell am force-stop com.cleantv.outfit
adb -s "$TV" shell am start -n com.homestead.launcher/.MainActivity
