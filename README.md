# NeuroVerse

**NeuroVerse** is a futuristic AI-powered Android assistant that gives you full control of your phone using natural language processing.

<p align="center">
  <img src="https://img.shields.io/badge/Built%20With-Kotlin%20%7C%20Jetpack%20Compose-purple" />
  <img src="https://img.shields.io/badge/AI-OpenRouter%20API-black" />
</p>

---

https://github.com/user-attachments/assets/5aab8f3b-40de-4407-a11a-15dd9471776f

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
  <img src="https://github.com/user-attachments/assets/7aae9d05-080b-4280-b4a3-5d8a272deea2" alt="Preview 3" width="200"/>
  <img src="https://github.com/user-attachments/assets/ac8559d6-77ef-4bf5-83d0-a3b6be90a905" alt="Preview 4" width="200"/>
</p>

https://github.com/user-attachments/assets/546037b8-05f1-43d4-bc24-15ae2caf4bb0

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
* "Turn on WiFi" **(!!..Under Development..!!)**
* "List installed apps" 
* "Record a voice note and email it" **(!!..Under Development..!!)**

NeuroVerse will parse these prompts, convert them into structured JSON, and invoke the appropriate plugins to execute them.

---

## 🤝 Contributing

Contributions are welcome! If you're interested in plugin development, core functionality, or UI improvements:

* Fork the repo
* Follow the code style and structure
* Document your changes properly
* Submit a Pull Request

---

## 🔒 Licensing & Commercial Use

```
NeuroVerse is licensed for **personal and non-commercial use only**.

🚫 You may **not** use this code in any commercial product, app, service, or organization  
without a paid commercial license.

💼 Want to use NeuroVerse commercially?
📬 Contact me at: siddheshsonar2377@gmail.com to request a license and pricing.

Unauthorized commercial use is prohibited and may result in legal action.
```

![license: custom](https://img.shields.io/badge/license-custom-blue)

```
Custom License – Personal & Non-Commercial Use Only

Copyright (c) 2025 Siddhesh Sonar

This software is provided for personal, educational, and non-commercial use only.  
You may view, study, and modify the code for non-commercial purposes.

🚫 Commercial use of this software in any form is **strictly prohibited** unless  
you have obtained a commercial license from the author.

Commercial use includes but is not limited to:
- Integrating the code into commercial products, apps, or services
- Selling, sublicensing, or offering this software for a fee
- Using the software in business environments, SaaS platforms, or monetized tools

📬 To obtain a commercial license, contact:
    siddheshsonar2377@gmail.com

Unauthorized commercial use of this code may result in legal action.

This software is provided "as is" without any warranties or guarantees.

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
