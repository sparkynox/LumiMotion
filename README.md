# LumiMotion

**Animated PNG-sequence background for your Minecraft title screen.**

LumiMotion plays back a folder of PNG frames — which you extract from a video yourself using any tool of your choice — as a smooth animated background. The mod itself only handles displaying pre-made PNG sequences; it does not read, decode, or convert video files.

Right now the animated background fully replaces the **title screen** (custom menu, no vanilla panorama). The pause menu can also show a video, but only as a background layer behind vanilla's existing pause menu — it isn't a full custom replacement there yet.

Written entirely in Kotlin, built on Fabric.

---

## ✨ Features

- 🎬 Play a sequence of PNG frames as an animated background — title screen, pause menu, or both
- 🔁 Loop or play-once modes
- ⚙️ Adjustable FPS per video
- 🖼️ Fullscreen or custom position/size (great for a small looping logo animation instead of a full background)
- 📁 Drop-in folder system — no config file editing required to add a new video
- 🔍 Auto-detects frame order from filenames — `frame_0001.png`, `1.png`, `shot-42.png` all work
- 🧩 [Mod Menu](https://modrinth.com/mod/modmenu) integration for easy access to settings
- 🪶 No video decoding at all — plays plain PNG images, so it works identically on every platform Minecraft supports

## 📦 Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for **Minecraft 1.21.4**
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Install [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin)
4. Drop `LumiMotion.jar` into your `mods` folder
5. Launch the game once — this generates `config/lumimotion/videos/`

## 🎥 Setting up a video

LumiMotion plays PNG frame sequences, not video files directly. Extract frames from your video first, using any tool you like — [ffmpeg](https://ffmpeg.org/), an online converter, or your video editor's "export as image sequence" option.

```
config/lumimotion/videos/
  MyIntro/
    frame_0001.png
    frame_0002.png
    frame_0003.png
    ...
```

The folder name (`MyIntro`) is what appears in the config UI. Frame files just need to end in a number — LumiMotion sorts them automatically, no strict naming required.

**Example with ffmpeg:**
```bash
ffmpeg -i input.mp4 -vf fps=24 frame_%04d.png
```

## ⚙️ Configuring

Open the config screen from **Mod Menu**, then for each target screen (Title Screen / Pause Menu) you can independently:

| Setting | Description |
|---|---|
| Enabled | Turn playback on/off for this screen |
| Video | Pick which folder to play |
| Loop | Loop forever or stop on the last frame |
| FPS | Playback speed, 1–60 |
| Fullscreen | Fill the whole screen, or... |
| X / Y / Width / Height | ...place it in a custom region instead |

Changes apply live — no restart needed.

## 🛠️ Building from source

Standard Fabric Loom project, 100% Kotlin (no Java sources).

```bash
git clone https://github.com/sparkynox/LumiMotion.git
cd LumiMotion
./gradlew build
```

Output jar: `build/libs/lumimotion-<version>.jar`

## 🧱 Tech

- Kotlin + [Fabric Language Kotlin](https://github.com/FabricMC/fabric-language-kotlin)
- Fabric API
- Mixin-based rendering hooks into `TitleScreen` / `GameMenuScreen`
- GPU-backed textures via `NativeImageBackedTexture` — each PNG is loaded once as an image and swapped on-screen, no video decoding involved

## 📄 License

MIT — see [LICENSE](LICENSE)

## 🙌 Credits

Built by [SparkyNox](https://www.modrinth.com/user/sparkynox)
