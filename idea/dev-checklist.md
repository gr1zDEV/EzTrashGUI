# EzTrashGUI Developer Checklist

Use this checklist each time you change code/config so you can verify both **build quality** and **in-game behavior**.

---

## 1) Local pre-flight

- [ ] Confirm Java 21 is active:
  ```bash
  java -version
  ```
- [ ] Confirm Maven is available:
  ```bash
  mvn -version
  ```
- [ ] From repo root, verify project files are present (`pom.xml`, `README.md`, `src/` if applicable).

---

## 2) Build + static verification

- [ ] Clean + compile:
  ```bash
  mvn clean compile
  ```
- [ ] Run full package build:
  ```bash
  mvn clean package
  ```
- [ ] (Optional but recommended) Show dependency tree when troubleshooting API conflicts:
  ```bash
  mvn dependency:tree
  ```
- [ ] Confirm output jar exists in `target/`:
  - Expected pattern: `EzTrashGUI-<version>.jar`

---

## 3) Deploy to Paper test server

- [ ] Stop server cleanly.
- [ ] Copy latest jar to server `plugins/` folder.
- [ ] Start server and watch startup logs.
- [ ] Confirm plugin is enabled:
  - In console: `plugins`
  - or in-game: `/plugins`
- [ ] Verify no startup errors/warnings from EzTrashGUI.

---

## 4) First-run file generation

After first startup (or after deleting plugin config folder):

- [ ] Confirm `plugins/EzTrashGUI/` exists.
- [ ] Confirm generated files:
  - [ ] `config.yml`
  - [ ] `gui.yml`
  - [ ] `blocked-items.yml`
  - [ ] `sounds.yml`
  - [ ] `messages/en.yml`
  - [ ] `messages/es.yml`

---

## 5) Command + permission checks

### Base command

- [ ] `/trash` opens GUI for player with `eztrashgui.use`.
- [ ] Player **without** `eztrashgui.use` is blocked and receives expected message.

### Admin reload

- [ ] `/eztrashgui reload` works for player/op with `eztrashgui.admin`.
- [ ] Player without `eztrashgui.admin` is denied.
- [ ] Reload reflects edits to config/gui/messages/blocked-items/sounds without restart.

### Cooldown bypass

- [ ] Confirm normal cooldown applies to non-bypass users (if configured).
- [ ] Confirm `eztrashgui.cooldown.bypass` ignores cooldown.

---

## 6) Core trash GUI behavior

- [ ] Open `/trash` and verify GUI size/layout matches `gui.yml`.
- [ ] Confirm **last row is controls/decor only** (no trash storage behavior).
- [ ] Place items in trash slots (rows above last row).
- [ ] Confirm button permanently deletes trash-slot items.
- [ ] Cancel button returns trash-slot items to inventory.
- [ ] Close GUI without confirming returns trash-slot items.
- [ ] Attempt to move/control-row items; confirm intended restrictions are enforced.

---

## 7) Edge-case inventory tests

- [ ] Fill player inventory, then cancel/close and verify overflow behavior.
- [ ] If configured, overflow items drop at player location.
- [ ] Test with stacked and unstackable items.
- [ ] Test with renamed/lore/custom-model-data items (metadata should survive return path).
- [ ] Test quickly (spam open/close/click) to ensure no dupes/loss.

---

## 8) Blocked-items enforcement

- [ ] Set mode to blacklist and verify listed materials are blocked.
- [ ] Set mode to whitelist and verify only listed materials are allowed.
- [ ] Validate messaging/sound feedback when blocked item is attempted.
- [ ] Reload with `/eztrashgui reload` and confirm behavior updates immediately.

---

## 9) Language + fallback behavior

- [ ] Set `config.yml -> language: en` and validate English messages.
- [ ] Set `language: es` and validate Spanish messages.
- [ ] Set missing/invalid language file name; verify fallback to `en.yml`.
- [ ] Remove one key in selected language; verify per-key fallback to `en.yml`.

---

## 10) Sound behavior

- [ ] Verify configured sounds play for open/confirm/cancel/error events as intended.
- [ ] Set invalid sound in `sounds.yml`, reload, and verify safe handling (no plugin crash).

---

## 11) Regression sweep before merge

- [ ] Re-run build:
  ```bash
  mvn clean package
  ```
- [ ] Smoke test `/trash` and `/eztrashgui reload` one final time.
- [ ] Validate no new console errors after 5+ minutes idle.
- [ ] Update `CHANGELOG.md` if release-impacting behavior changed.

---

## 12) Release handoff notes (optional)

- [ ] Record Paper build used for QA.
- [ ] Record test world seed / scenario notes.
- [ ] Record any known limitations or follow-up tasks.

