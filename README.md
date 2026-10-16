# Crackpipe Auto Patches

Morphe patch source repository, auto-maintained **for educational and research purposes only**.

> Not affiliated with the upstream app developers. Patches are generated automatically from APK
> analysis, so each release targets the app version it was built against. Use at your own risk.

- **Add as source in Morphe:** Sources → **+** → Remote → paste exactly:
  `https://raw.githubusercontent.com/drwolfen/morphe-patches-auto/main/patches-bundle.json`
  (or open `https://morphe.software/add-source?github=drwolfen/morphe-patches-auto`).
  Releases + `.mpp` assets live on Codeberg; this GitHub endpoint is the mirror the Manager reads,
  and it is the one that can actually offer you an upgrade.
- Patches are generated automatically from Play Store / APKMirror APK analysis.
- Machine-readable catalog: [`patches-list.json`](patches-list.json).
- Update manifest consumed by Morphe: [`patches-bundle.json`](patches-bundle.json).

## Layout

- `patches/src/main/kotlin/standardrepo/morphe/patches/<app>/<feature>/` — one folder per patch:
  `Fingerprints.kt` (method location) + `<Name>Patch.kt` (mutation).
- `patches/src/main/kotlin/standardrepo/morphe/compatibility/Constants.kt` — `Compat` app/target registry.
- `patches/pairip/<app>.json` — Pairip reflection maps (codegen via `./gradlew codegen`).
- `extensions/` — runtime extension modules injected into patched apps.
