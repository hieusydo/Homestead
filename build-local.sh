#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"
ROOT="$(cd .. && pwd)"
export JAVA_HOME="$ROOT/output/toolchain/jdk-17.0.21+9/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
export ANDROID_BUILD_TOOLS="$ROOT/output/toolchain/build-tools/android-15"
export ANDROID_JAR="$ROOT/output/toolchain/platform/android-34/android.jar"
export HOMESTEAD_KEYSTORE="$ROOT/output/outfit-signing.keystore"
export HOMESTEAD_KEYALIAS=outfit
: "${HOMESTEAD_KEYPASS:?Set HOMESTEAD_KEYPASS to your local signing password}"
export HOMESTEAD_KEYPASS
if [ ! -f "$HOMESTEAD_KEYSTORE" ]; then
 keytool -genkeypair -keystore "$HOMESTEAD_KEYSTORE" -alias outfit -storepass "$HOMESTEAD_KEYPASS" -keypass "$HOMESTEAD_KEYPASS" -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=CleanTV Local Outfit"
fi
bash build.sh
