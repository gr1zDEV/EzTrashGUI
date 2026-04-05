# EzTrashGUI

EzTrashGUI is a production-ready Paper plugin by **EzInnovations** that provides a safe trash menu with explicit confirmation before item deletion.

## Platform Targets
- Paper **1.21.11** (API baseline: 1.21)
- Folia-compatible behavior (no unsafe async Bukkit access)
- Java 21

## Commands
- `/trash` - opens the trash GUI (`eztrashgui.use`)
- `/eztrashgui reload` - reloads config, GUI, sounds, blocked items, and language files (`eztrashgui.admin`)

## Permissions
- `eztrashgui.use`
- `eztrashgui.admin`
- `eztrashgui.cooldown.bypass`

## Behavior Summary
- All rows above the last row are trash slots.
- The last row is always reserved for decoration + controls.
- Confirm permanently deletes trash-slot items.
- Cancel (or close without confirming) returns trash-slot items.
- Leftover returned items can be dropped at player location if configured.
- Blocked materials are enforced (blacklist/whitelist mode).

## Configuration Files
Generated in `plugins/EzTrashGUI/`:
- `config.yml`
- `gui.yml`
- `blocked-items.yml`
- `sounds.yml`
- `messages/en.yml`
- `messages/es.yml`

## Language System
- `config.yml -> language` selects active file from `messages/`.
- Missing selected file falls back to `en.yml`.
- Missing keys in selected language fall back per-key to `en.yml`.
- Reload supported with `/eztrashgui reload`.

## Installation
1. Build with Maven: `mvn clean package`
2. Place `EzTrashGUI-<version>.jar` in your server `plugins/` folder.
3. Start/restart server.
4. Adjust generated config files as needed.
5. Use `/eztrashgui reload` after config updates.
