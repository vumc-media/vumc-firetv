# Versailles UMC Fire TV — v0.3 layout rebuild

This branch replaces the Leanback BrowseSupportFragment home screen with a custom two-column Android TV interface.

- Fixed 188dp left navigation that never overlays content
- Independent right content area and scrollable rows
- 24dp gaps between cards
- Larger section headings and branded welcome header
- D-pad focus animation and placeholder click feedback
- Left from the first card returns to its corresponding menu item
- Back returns focus to the sidebar

## Testing
Download this branch as a ZIP, extract it, and open the extracted project folder in Android Studio. Sync and Run on the Television (1080p) emulator.

This code has been committed but has **not** been compiled or emulator-tested by the assistant. Report compiler errors or screenshots before merging. Live streaming, catalog, imagery, and schedule remain unconfigured. Do not embed API secrets.
