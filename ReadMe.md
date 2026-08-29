<div align="center">
  <img src="app/src/main/ic_launcher-playstore.png" width="256">
  <h1>Portal 🔐</h1>
</div>

**Portal** — is a completely **free** and **open-source** smart wireless key that transforms your
Android smartphone and Wear OS smartwatch into tools for instant computer unlocking. No ads, no
tracking, no hidden costs.

> [!WARNING]
> To use this application, you must install the **PC Client** on your computer. You can find the download link in the section below.

## 📲 Links & Downloads

|                Platform                 |                                              Link                                              |
|:---------------------------------------:|:----------------------------------------------------------------------------------------------:|
|   **Android & Wear OS (Google Play)**   | [Get it on Google Play](https://play.google.com/store/apps/details?id=com.xxmrk888ytxx.portal) |
| **Android & Wear OS (GitHub Releases)** |            [Download APK](https://github.com/xXMRK888YTXx/Portal-Android/releases)             |
|         **PC Client (Windows)**         |            [Download PC Client](https://github.com/KoksMen/Portal-Windows/releases)            |

## ❤️ Support the Project

If you find Portal useful, consider supporting the developers:
- ☕ **[Support via Donate (EN)](https://github.com/xXMRK888YTXx/Portal-Docs/blob/master/Donate/donate-en.md)**
- **Android Developer:** [xXMRK888YTXx](https://github.com/xXMRK888YTXx)
- **PC Client Developer:** [xXKoksMenXx](https://github.com/KoksMen)

## 🚀 How it Works

The application operates in three global modes:
1.  **Unlock from Smartphone:** You initiate the unlock manually from the app or via a home screen shortcut.
2. **Request from PC:** Your computer sends an authorization request to your phone and smartwatch (
   e.g., when the PC wakes up). You simply confirm it on either device.
3. **Unlock from Wear OS Smartwatch:** Trigger unlocks, send Wake-on-LAN packets, or confirm
   incoming requests directly from your wrist without touching your phone.

## 📡 Connection Paths

Messages and unlock commands are delivered through:
-   **Bluetooth (RFCOMM):** A direct, stable connection between your phone and PC.
-   **WiFi (WebSockets):** High-speed communication over your local network.
- *Includes **Wake-on-LAN (WOL)** support to "wake up" your PC over WiFi before sending the unlock
  command.*
- **Wear Data Layer:** Instant local synchronization between your Android smartphone and Wear OS
  smartwatch.

## 🛡 Features & Security

- **⌚ Wear OS Companion:** Seamless companion app with interactive high-priority notifications (
  Allow / Deny quick action buttons), device list, and action launcher.
- **🔄 Multi-Node Decision Synchronization:** First decision wins — confirming or denying a request
  on your watch or phone automatically resolves and dismisses the prompt on both devices
  simultaneously.
- **👆 Biometric Protection:** Use fingerprint or facial recognition on your smartphone to confirm
  unlock actions.
- **⚡ Quick Shortcuts:** Instant access to specific devices right from your phone's home screen.
- **🔐 Enhanced Security:** Data encryption and automatic device unpairing if system biometric data
  changes.
- **🛡️ Privacy First & Secrets Isolation:** No cloud servers — all communication stays within your
  local environment. Network credentials and keys strictly remain on your phone; the watch receives
  metadata only.

## 📸 Screenshots

<div align="center">
  <img src="Screenshots/0.png" width="200">
  <img src="Screenshots/1.jpg" width="200">
</div>

<br>

<div align="center">
  <img src="Screenshots/2.jpg" width="200">
  <img src="Screenshots/3.jpg" width="200">
  <img src="Screenshots/4.jpg" width="200">
</div>

---
*License: GPL-3.0*
---

## 🛠 Technologies & Architecture

- **Language:** Kotlin (Coroutines, Flow, Serialization)
- **UI Framework:** Jetpack Compose (Material 3), Jetpack Compose for Wear OS
-   **Architecture:** Multi-module, Clean Architecture, MVI (Model-View-Intent)
- **Multi-Device Sync:** Google Play Services Wearable (Wear Data Layer)
-   **Dependency Injection:** Dagger 2
-   **Local Storage:** Room (SQLite), Jetpack DataStore (Preferences)
-   **Networking:** WebSockets, Bluetooth RFCOMM, mDNS, Wake-on-LAN (WOL)
-   **Security:** Biometric API, Data Encryption (AES), Secure Storage
-   **Build System:** Gradle Kotlin DSL, Version Catalogs, custom `buildSrc` plugins

## 🔐 Security & Verification

To ensure the authenticity of the application, please verify the build signature. Official builds are signed with one of the following keys:

-   **Google Play Build:** SHA-256 Fingerprint: 11:B8:C9:72:70:0E:87:EB:4E:B9:00:4E:15:68:D8:72:32:87:2C:9F:0F:73:A5:3D:30:E7:CD:CF:49:AF:88:85
-   **Personal Builds:** Signed with the personal key. You can find the valid signature in my [GitHub Profile ReadMe](https://github.com/xXMRK888YTXx).

> [!WARNING]
> Applications signed with any other keys are not built by me. Using such builds is highly discouraged unless you fully trust the provider.

## 📄 Documentation & Links

- 🛡 **[Privacy Policy](https://github.com/xXMRK888YTXx/Portal-Docs/blob/master/Android/Privacy%20policy.md)**
- 📜 **[Terms of Service](https://github.com/xXMRK888YTXx/Portal-Docs/blob/master/Android/Terms%20of%20Service.md)**

---

<div align="center">
  <img src="app/src/main/ic_launcher-playstore.png" width="256">
  <h1>Portal 🔐 (RU)</h1>
</div>

**Portal** — это полностью **бесплатный** и **открытый (open-source)** умный беспроводной ключ,
который превращает ваш Android-смартфон и смарт-часы на Wear OS в инструмент для мгновенной
разблокировки компьютера. Без рекламы, без слежки и без скрытых платежей.

> [!WARNING]
> Для использования приложения необходимо установить **ПК клиент** на ваш компьютер. Ссылка на скачивание находится в разделе ниже.

## 📲 Ссылки и загрузка

|                Платформа                |                                        Ссылка                                        |
|:---------------------------------------:|:------------------------------------------------------------------------------------:|
|   **Android & Wear OS (Google Play)**   | [Google Play](https://play.google.com/store/apps/details?id=com.xxmrk888ytxx.portal) |
| **Android & Wear OS (GitHub Releases)** |        [Скачать APK](https://github.com/xXMRK888YTXx/Portal-Android/releases)        |
|         **PC Клиент (Windows)**         |     [Скачать клиент для ПК](https://github.com/KoksMen/Portal-Windows/releases)      |

## ❤️ Поддержка проекта

Если Portal оказался вам полезен, вы можете поддержать разработчиков:
- ☕ **[Поддержать проект (RU)](https://github.com/xXMRK888YTXx/Portal-Docs/blob/master/Donate/donate-ru.md)**
- **Разработчик Android:** [xXMRK888YTXx](https://github.com/xXMRK888YTXx)
- **Разработчик ПК-клиента:** [xXKoksMenXx](https://github.com/KoksMen)

## 🚀 Как это работает

Приложение работает в трёх глобальных режимах:
1.  **Разблокировка со смартфона:** Вы сами инициируете разблокировку из приложения или через ярлык на рабочем столе.
2. **Запрос с ПК:** Ваш компьютер сам отправляет запрос на авторизацию на телефон и смарт-часы (
   например, при пробуждении ПК). Вам остается только подтвердить его на любом удобном устройстве.
3. **Управление с часов Wear OS:** Запускайте разблокировку, отправляйте пакеты Wake-on-LAN и
   подтверждайте входящие запросы прямо с запястья, не доставая телефон.

## 📡 Пути передачи данных

Сообщения и команды разблокировки передаются через:
-   **Bluetooth (RFCOMM):** Прямое и стабильное соединение между телефоном и ПК.
-   **WiFi (WebSockets):** Высокоскоростное взаимодействие через вашу локальную сеть.
- *Включает поддержку **Wake-on-LAN (WOL)** для «пробуждения» ПК по сети перед отправкой команды
  разблокировки.*
- **Wear Data Layer:** Мгновенная локальная синхронизация между смартфоном и часами Wear OS.

## 🛡 Особенности и безопасность

- **⌚ Компаньон для Wear OS:** Полноценное приложение для часов с интерактивными уведомлениями (
  кнопки «Разрешить» / «Отклонить»), списком сохраненных компьютеров и быстрым запуском команд.
- **🔄 Синхронизация «Первое решение побеждает»:** Подтверждение или отклонение запроса на часах или
  телефоне мгновенно завершает операцию и скрывает уведомления на обоих устройствах одновременно.
- **👆 Биометрическая защита:** Использование отпечатка пальца или распознавания лица на смартфоне
  для подтверждения любого действия.
- **⚡ Быстрые ярлыки:** Мгновенный доступ к разблокировке конкретных устройств прямо с рабочего
  стола телефона.
- **🔐 Повышенная безопасность:** Шифрование данных и автоматическая отвязка устройств при изменении
  биометрических данных в системе.
- **🛡️ Приватность и изоляция секретов:** Никаких облачных серверов — все взаимодействие происходит
  только в вашей локальной среде. Пароли и ключи авторизации хранятся исключительно на телефоне;
  часы получают только метаданные.

## 📸 Скриншоты

<div align="center">
  <img src="Screenshots/0.png" width="200">
  <img src="Screenshots/1.jpg" width="200">
</div>

<br>

<div align="center">
  <img src="Screenshots/2.jpg" width="200">
  <img src="Screenshots/3.jpg" width="200">
  <img src="Screenshots/4.jpg" width="200">
</div>

## 🛠 Технологии и архитектура

- **Язык:** Kotlin (Coroutines, Flow, Serialization)
- **UI Фреймворк:** Jetpack Compose (Material 3), Jetpack Compose for Wear OS
-   **Архитектура:** Многомодульность, Clean Architecture, MVI (Model-View-Intent)
- **Синхронизация устройств:** Google Play Services Wearable (Wear Data Layer)
-   **Внедрение зависимостей:** Dagger 2
-   **Хранение данных:** Room (SQLite), Jetpack DataStore (Preferences)
-   **Сеть:** WebSockets, Bluetooth RFCOMM, mDNS, Wake-on-LAN (WOL)
-   **Безопасность:** Biometric API, Шифрование данных (AES), Secure Storage
-   **Сборка:** Gradle Kotlin DSL, Version Catalogs, кастомные плагины в `buildSrc`

## 🔐 Безопасность и проверка

Для обеспечения подлинности приложения проверьте подпись сборки. Официальные версии подписываются одним из следующих ключей:

-   **Версия из Google Play:** SHA-256 Fingerprint: 11:B8:C9:72:70:0E:87:EB:4E:B9:00:4E:15:68:D8:72:32:87:2C:9F:0F:73:A5:3D:30:E7:CD:CF:49:AF:88:85
-   **Персональные сборки:** Подписаны личным ключом. Актуальную сигнатуру можно найти в моем [GitHub профиле](https://github.com/xXMRK888YTXx).

> [!WARNING]
> Приложения, подписанные любыми другими ключами, собраны не мной. Использование таких сборок крайне не рекомендуется, если вы не доверяете автору на 100%.

---
*License: GPL-3.0*
---
