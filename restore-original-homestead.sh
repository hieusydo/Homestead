#!/bin/sh
set -eu
TV="${TV:?Set TV to your television IP:5555}"
adb -s "$TV" shell settings put secure enabled_accessibility_services com.homestead.launcher/com.homestead.launcher.HomeRedirectService
adb -s "$TV" shell settings put secure accessibility_enabled 1
adb -s "$TV" shell am force-stop com.cleantv.outfit
adb -s "$TV" shell am start -n com.homestead.launcher/.MainActivity
