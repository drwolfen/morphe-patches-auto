# Crackpipe Auto Patches

Morphe patch source repository, auto-maintained **for educational and research purposes only**.

> Not affiliated with the upstream app developers. Patches are generated automatically from APK
> analysis, so each release targets the app version it was built against. Use at your own risk.

- **Add as source in Morphe:** Sources → **+** → Remote → paste exactly:
  `https://codeberg.org/StandardRepo/morphe-patches-auto/raw/branch/dev/patches-bundle.json`

  Do **not** use `https://morphe.software/add-source?github=OWNER/REPO` for this repo: that
  page only understands `github.com`/`gitlab.com` hosts and will render the wrong host. Morphe
  Manager itself handles other hosts by falling back to the endpoint URL as-is, so point it
  straight at the raw `patches-bundle.json`. Consequence of the non-GitHub host: no prerelease
  toggle and no repo avatar/issue links (documented degraded UX, not a breakage).
- Patches are generated automatically from Play Store / APKMirror APK analysis.
- Machine-readable catalog: [`patches-list.json`](patches-list.json).
- Update manifest consumed by Morphe: [`patches-bundle.json`](patches-bundle.json).

## Layout

- `patches/src/main/kotlin/standardrepo/morphe/patches/<app>/<feature>/` — one folder per patch:
  `Fingerprints.kt` (method location) + `<Name>Patch.kt` (mutation).
- `patches/src/main/kotlin/standardrepo/morphe/compatibility/Constants.kt` — `Compat` app/target registry.
- `patches/pairip/<app>.json` — Pairip reflection maps (codegen via `./gradlew codegen`).
- `extensions/` — runtime extension modules injected into patched apps.

## Build

```
GITHUB_TOKEN=... ./gradlew :patches:buildAndroid clean --no-daemon
# → patches/build/libs/patches-<version>.mpp
```

Requires JDK 21 and a GitHub token with read access to `MorpheApp/registry`
(GitHub Packages Maven repository for `app.morphe:morphe-patches-library`).

## Release

Push a `feat(<app>): …` commit to `main`/`dev`; semantic-release builds, tags,
uploads the `.mpp` + `.asc`, and rewrites both JSON manifests automatically.
