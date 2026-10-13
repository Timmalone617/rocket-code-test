# NOVA Forge Automated APK Pipeline

This repository has been converted from the temporary Rocket Calculator proof into a NOVA Forge GitHub Actions build target.

Source lineage found during the Work session:

- Latest available Forge package: `NOVA_Forge_0.9.5_CHATGPT_FIX_CODEASSIST.caproj`
- Input SHA-256: `37b8d59c2ebd4dfe43bc979936191e5591580b8e072eab08fffe17d7eb46c3c2`
- Android package: `com.timmalone.forge`
- Version target: `0.9.5-CHATGPT-FIX`

## What the workflow does

- Installs JDK 17.
- Installs Android SDK platform 35 and build-tools 35.0.0.
- Uses Gradle 8.9 with Android Gradle Plugin 8.6.1.
- Builds `:app:assembleDebug`.
- Verifies the APK with `unzip -t` and checks for `AndroidManifest.xml` and `classes.dex`.
- Uploads `NOVA_Forge_debug.apk` and `NOVA_FORGE_DEBUG_APK_SHA256.txt` as the `nova-forge-debug-apk` artifact.

## Preserved Forge design

Forge remains scan-first: it should detect and reuse accessible phone-side JDK 17, Android SDK 35, ARM64 aapt2, Gradle, and valid archives before downloading anything. It must distinguish missing, inaccessible, unverified, corrupt, incompatible, partial, and execution-failed states, and it must not claim device success until run on the actual Samsung phone.

## How to trigger another build

Push to `main`, or open the Actions tab, select **NOVA Forge Android APK**, and run the workflow manually.

## Remaining work

- Physical Samsung device testing is not complete.
- NOVA Lab end-to-end testing is not complete.
- Google Drive repair-mailbox automation is still the planned next stage.
- A completed Forge 0.9.6 repair report/changelog was not found; 0.9.5 was the latest available source located.
