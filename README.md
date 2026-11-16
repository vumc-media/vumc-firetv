# Versailles UMC Fire TV — Integrated development branch

Based on the v0.3 custom two-column layout, without modifying the Roku repository.

## Connected public services
- Live status and HLS selection: https://vumc-roku-status.versaillesumcworshipteam.workers.dev/status
- Latest Traditional and Contemporary recordings: https://vumc-roku-status.versaillesumcworshipteam.workers.dev/latest
- R2 sermon catalog: https://vumc-video-catalog.versaillesumcworshipteam.workers.dev/videos.xml

## Implemented
- Watch Live checks both Cloudflare live inputs and launches the first active HLS stream.
- Off-air message shows the next service from the status response when present.
- Latest Streams launches the most recent recording for each service.
- Browse Sermons loads the RSS/XML catalog and offers recording selection.
- The existing ExoPlayer handles HLS and MP4.
- Removed the extra Next Worship home card.

## Remaining before release
- Replace basic sermon list with polished series navigation and poster imagery.
- Add Roku-equivalent ministry/QR detail screens and real destinations.
- Add the on-screen off-air countdown (currently text only).
- Add loading feedback, error handling and playback diagnostics.
- Validate layouts and remote focus on Fire TV hardware.
- Build and test the APK in Android Studio, then sign and submit.

## Testing
Open this branch in Android Studio, sync Gradle, and Run on the TV emulator.
This branch has NOT been compiled or tested by the assistant. Never put Cloudflare or Planning Center secrets into the APK.
