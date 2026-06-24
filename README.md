# Spaceship

An arcade dodge game for Android. Navigate your spaceship through a dual-tunnel environment, dodge two enemies whose speed increases as you score, collect coins to boost your score, and unlock new ships as you improve.

[![Get it on Google Play](https://img.shields.io/badge/Google_Play-Available-green?logo=google-play&logoColor=white)](https://play.google.com/store/apps/details?id=com.qwertycreations.spaceship)
![Downloads](https://img.shields.io/badge/Downloads-50%2B-blue)
![Platform](https://img.shields.io/badge/Platform-Android-lightgrey?logo=android)
![Language](https://img.shields.io/badge/Language-Java-orange?logo=java)

---

## How It Works

The game takes place inside two horizontal tunnels stacked vertically. Your spaceship occupies one tunnel at a time. Two enemy ships move through the tunnels simultaneously, one left-to-right, one right-to-left, starting slow and accelerating as your score climbs.

**The teleportation mechanic** is the core of the game: a moving arrow marks a position within the opposite tunnel. Tapping the switch button instantly teleports your ship to the arrow's current position. If an enemy occupies that spot when you jump, the game ends. Timing your jumps and anticipating where the arrow will be when the enemy passes is the primary skill the game demands.

Coins appear and disappear in the tunnels on a timer. Collecting them increments your score. Bonus points are awarded for holding a high score midway through a round.

---

## Key Features

- **Dual-tunnel teleportation mechanic** — a timing-based twist on the standard dodge game formula
- **Accelerating enemy speed** — difficulty scales directly with score, not time
- **9 unlockable ship sprites** — each locked behind a score or average score threshold, visible in a ship selection screen
- **Persistent statistics** — high score, total score, games played, and average score stored across sessions via SharedPreferences
- **Settings panel** — independent music volume and sound effects volume sliders, persistent across sessions
- **Pause and resume** — mid-game pause with full state preservation
- **Background music + 5 sound effects** — coin collection, explosion, click confirmations, and jump sound
- **No ads**

---

## Technical Implementation

The game loop runs on a 2ms `Timer` with a `Handler` to post UI updates back to the main thread. Enemy movement is calculated as a linear offset per tick, with speed increasing proportionally to the current score (`score / 100.0f + 1`). Coin spawn and despawn run on a separate randomised `Timer` with intervals between 5–25 seconds. Collision detection uses bounding box intersection across all active game objects each tick.

Audio uses `MediaPlayer` for background music (with position preservation across `onStop`/`onStart` lifecycle events) and `SoundPool` with `AudioAttributes` for low-latency sound effects. Ship unlocking uses a threshold system tied to average score (for mid-tier ships) and high score (for top-tier ships), evaluated against stored SharedPreferences values on each screen load.

**Stack:** Java · Android SDK · SoundPool · MediaPlayer · SharedPreferences · Handler / Timer · XML layouts

---

## Installation

Available on the [Google Play Store](https://play.google.com/store/apps/details?id=com.qwertycreations.spaceship). Requires Android 5.0 (Lollipop) or higher.

To build from source:
```bash
git clone https://github.com/thedemogorgon3/spaceship
```
Open in Android Studio and run on a device or emulator.
