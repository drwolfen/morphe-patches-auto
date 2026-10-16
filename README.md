# Crackpipe Auto Patches

Morphe patch source repository, auto-maintained **for educational and research purposes only**.

> Not affiliated with the upstream app developers. Patches are generated automatically from APK
> analysis, so each release targets the app version it was built against. Use at your own risk.

- **Add as source in Morphe:** Sources → **+** → Remote → paste exactly:
  `https://codeberg.org/StandardRepo/morphe-patches-auto/raw/branch/dev/patches-bundle.json`
- Patches are generated automatically from Play Store / APKMirror APK analysis.
- Machine-readable catalog: [`patches-list.json`](patches-list.json).
- Update manifest consumed by Morphe: [`patches-bundle.json`](patches-bundle.json).

## Layout

- `patches/src/main/kotlin/standardrepo/morphe/patches/<app>/<feature>/` — one folder per patch:
  `Fingerprints.kt` (method location) + `<Name>Patch.kt` (mutation).
- `patches/src/main/kotlin/standardrepo/morphe/compatibility/Constants.kt` — `Compat` app/target registry.
- `patches/pairip/<app>.json` — Pairip reflection maps (codegen via `./gradlew codegen`).
- `extensions/` — runtime extension modules injected into patched apps.
