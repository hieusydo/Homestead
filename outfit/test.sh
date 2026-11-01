#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
TEST_DIR="$(mktemp -d)"
trap 'rm -rf "$TEST_DIR"' EXIT
"${JAVA_HOME:?Set JAVA_HOME}/bin/javac" -d "$TEST_DIR" src/com/homestead/launcher/OutfitWallpaperSchedule.java outfit/tests/WallpaperTest.java
"$JAVA_HOME/bin/java" -cp "$TEST_DIR" com.homestead.launcher.WallpaperTest
python3 outfit/check-boundary.py
python3 - <<'PYTEST'
import runpy
module=runpy.run_path('outfit/configure-home.py')
f=module['service_list']; services=module['SERVICES']; other='example.reader/.Reader'
assert f('null','outfit')==services['outfit']
assert f(other+':'+services['original'],'outfit')==other+':'+services['outfit']
assert f(other+':'+services['outfit'],'original')==other+':'+services['original']
assert f(other+':'+services['outfit'],'outfit')==other+':'+services['outfit']
print('PASS: accessibility services preserved across install, restore and repeated setup')
PYTEST
