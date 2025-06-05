# NeuroVerse

**NeuroVerse** is a futuristic AI-powered Android assistant that gives you full control of your phone using natural language processing.

<p align="center">
  <img src="https://img.shields.io/badge/Built%20With-Kotlin%20%7C%20Jetpack%20Compose-purple" />
  <img src="https://img.shields.io/badge/AI-OpenRouter%20API-black" />
</p>

---

## Features

* **Natural Language Understanding** – OpenRouter-powered prompt parsing into structured JSON commands.
* **Full App Control** – Automate and trigger system actions using downloadable modular plugins.
* **Smart UI** – Built with Jetpack Compose and Material 3.
* **Dynamic Plugin System** – Install new capabilities on the fly (in development).

---

## Plugin System (Under Development)

NeuroVerse introduces a **dynamic plugin framework** that allows third-party developers to create and load functionality as modular APK plugins.

* Plugins can respond to voice commands or AI-generated JSON.
* They run in a sandboxed environment with controlled permissions.
* Easily updatable and installable from a Firebase-powered Plugin Market screen.

**What can be built with plugins?**

* App launchers, automation triggers, content fetchers, accessibility-based actions, custom AI interpreters, and more.

---

## Screenshots

> *Some Experimental Previews*
<p align="center">
  <img src="https://github.com/user-attachments/assets/4087806f-9e5d-4888-89ee-2d95edfc26b1" alt="Preview 1" width="200"/>
  <img src="https://github.com/user-attachments/assets/43634f85-be9b-4b17-82c5-285f724fa717" alt="Preview 2" width="200"/>
   <br/>
  <img src="https://github.com/user-attachments/assets/7aae9d05-080b-4280-b4a3-5d8a272deea2" alt="Preview 3" width="200"/>
  <img src="https://github.com/user-attachments/assets/d0b0daad-e35b-4a49-8db9-0988d99d702e" alt="Preview 4" width="200"/>
</p>



---

## Built With

* Kotlin + Jetpack Compose
* Firebase Realtime Database + Storage
* RoomDB + DataStore
* OpenRouter API ([openrouter.ai](https://openrouter.ai/))
* ONNX Runtime (planned)
* Accessibility Services
* Coroutine + Flow
* Compose Navigation + State Management

---

## 📦 Installation

```bash
# Clone the repo
git clone https://github.com/yourusername/NeuroVerse.git

# Open in Android Studio
# Build & run on Android 11+ device
```

### Note

Some advanced features (like automation and plugin permissions) may require enabling accessibility services and allowing unknown sources for plugin APKs.

---

## 🧠 Example Prompts

* "Open WhatsApp"
* "Turn on WiFi"
* "List installed apps"
* "Record a voice note and email it"

NeuroVerse will parse these prompts, convert them into structured JSON, and invoke the appropriate plugins to execute them.

---

## 🤝 Contributing

Contributions are welcome! If you're interested in plugin development, core functionality, or UI improvements:

* Fork the repo
* Follow the code style and structure
* Document your changes properly
* Submit a Pull Request

---

## 📄 License

[![License: CC BY-NC 4.0](https://img.shields.io/badge/License-BY--NC%204.0-lightgrey.svg)](https://creativecommons.org/licenses/by-nc/4.0/)

**Licensed under the Creative Commons Attribution-NonCommercial 4.0**

```
Copyright (c) 2025 Siddhesh Sonar

This software and associated content are protected under the Creative Commons Attribution-NonCommercial 4.0 International License.

You are permitted to:

- Share — Copy and redistribute the material in any medium or format.
- Adapt — Remix, transform, and build upon the material for non-commercial purposes.

Under the following conditions:

- Attribution — You must provide appropriate credit, include a link to the license, and indicate if changes were made. Attribution must not suggest endorsement by the original author.
- NonCommercial — You may not use the material for commercial purposes, including but not limited to: selling, licensing, bundling, or integrating into paid products or services, without explicit written permission.

Patent and Usage Disclaimer:
All rights related to any novel techniques, inventions, or patents implemented or implied by this software are reserved by the author. Unauthorized use of patented techniques, especially for commercial gain, is strictly prohibited.

If you wish to:
✔ Use this software commercially
✔ Obtain a commercial license
✔ Inquire about patents or innovations

Please contact: siddheshsonar2377@gmail.com

Full license details: https://creativecommons.org/licenses/by-nc/4.0/
```

---

## ✨ Author

**[Siddhesh Sonar (DARK)](https://github.com/Siddhesh2377)**
*Android Developer | AI Enthusiast | Open Source Contributor*

---

## 🙏 Special Thanks

* [OpenRouter.ai](https://openrouter.ai) – For powering natural language to structured command conversion.
* JetBrains – For Kotlin and tooling.
* Android Open Source Project – For making custom AI automation possible.
* Firebase – For realtime syncing and storage.
* GitHub community – For inspiring open-source contributions.
