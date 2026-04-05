# CHANGELOG

## [1.0.1] - 2026-04-05
### Fixed
- Simplified and hardened GUI session creation to remove duplicated inventory construction and ensure each GUI holder tracks its inventory safely.
- Improved close/cancel return flow to avoid orphaned sessions and to safely return existing session items before opening a new GUI.
- Added validated control-slot fallback behavior (safe default bottom-row slots) when configured control slots are invalid.
- Improved blocked-item prevention checks for both cursor placement and hotbar-swap placement into trash slots.
- Expanded language reload behavior to reload all `messages/*.yml` files and keep per-key fallback to English.

## [1.0.0] - 2026-04-05
### Added
- Initial EzTrashGUI release with confirmation-based trash GUI workflow.
- Paper/Folia-safe inventory interaction handling for clicks, drags, close-return logic, and control-slot protection.
- Config system for GUI layout, sounds, blocked items, permissions, cooldown, and language selection.
- Language fallback system with bundled English and Spanish message files.
- Admin reload command for runtime config/language refresh.
- Concise optional deletion logging with optional item content summaries.
