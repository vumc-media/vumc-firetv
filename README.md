# Versailles UMC Fire TV — Starter v0.1

Native Android TV / Amazon Fire TV starter project. The Roku repository is separate and untouched.

## Included
- Fire TV launcher intent and D-pad-friendly Leanback menu
- Live Worship, Latest Streams, Sermon Series, Partner With Us, Connect With Us sections
- Media3/ExoPlayer HLS-capable player activity
- Unconfigured sources are explicitly placeholders, not working playback URLs

## Build
Open in Android Studio with JDK 17 and Android SDK 35. Sync Gradle and run on a Fire TV device using ADB. This is source code, **not** a compiled or tested APK.

## Next steps
1. Inspect Roku catalog and Cloudflare Worker endpoints.
2. Wire public live HLS and status checks with an off-air fallback.
3. Integrate R2 video catalog and image thumbnails.
4. Obtain Planning Center schedule via server-side Worker (never put secrets in the APK).
5. Add VUMC graphics, TV banner/icon and QR screens.
6. Build, test and sign for Amazon Appstore.

Do not embed Cloudflare API tokens, PCO credentials or private R2 keys in the app.
