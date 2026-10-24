# Homestead Outfit — CleanTV custom edition

Built from Homestead 1.6.2 (GPL-3.0) for Insignia NS-50DF710NA21, Fire OS 7.7.1.5. Outfit is bundled under SIL OFL; see OUTFIT-LICENSE.txt. Original Homestead remains installed. Package: com.cleantv.outfit.

## Design
Outfit typography, Obsidian gradient, greeting/date/clock, five-column favorites in this order: YouTube, Netflix, HBO Max, ABC7, NBC Bay Area. Compact inputs and Settings, then remaining TV apps. No blur or video background. All visible text uses bundled Outfit. Menu opens Settings in one press; Settings is always the first control tile. Background is under Menu > Background. Long-press a tile to hide it. Automatic upstream update checks and automatic debloat reapplication default off in this fork.

## Build
Set `HOMESTEAD_KEYPASS` to your local signing password, then run `bash build-local.sh`. Local Java and Android tools are in ../output/toolchain. Keep ../output/outfit-signing.keystore to sign future updates. It is a local development key; do not publish it. Build verifies the resulting APK signature.

## Install / restore
Set `TV` to your TV address (IP:5555), then use the accompanying build-and-install.sh. The original official app cannot accept this locally signed update, so this edition uses a separate package. No system launcher was removed or disabled. The original Homestead was force-stopped to avoid competing startup behavior and can be reopened.

To return to official Homestead, run `bash restore-original-homestead.sh` with `TV` set. This replaces the accessibility service with the original and opens it. If you add other accessibility services later, preserve them when editing that setting.

READ_LOGS is used for the Home redirect, as approved in this session. WRITE_SECURE_SETTINGS is not granted. The app's own ADB tunnel is not configured. After real-remote testing, switch ADB debugging off on the TV and verify port 5555 is closed.

## Maintenance
Favorites live in HomeGrid.java in the `order` array. Change that list and rebuild to alter their fixed order. Wallpaper is changeable in Settings without rebuilding. This is a maintained local fork; official Homestead releases will not update it automatically.

The bundled Outfit file is a static regular-weight instance for reliable Android 9 rendering, derived from the Google Fonts variable font.
