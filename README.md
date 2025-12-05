<div align="center">
<img width="150" height="150" alt="pimlogo" src="https://github.com/user-attachments/assets/aaa4e871-c4c9-412d-a07c-d7361517f82f" />
</div>
# 🐧 Penguin Inventory Manager (PIM)

**Die intelligente Offline-First Lagerverwaltung für Android & Desktop.**

Der **Penguin Inventory Manager (PIM)** ist eine moderne Kotlin Multiplatform (KMP) Anwendung, um Vorräte und Lagerbestände effizient zu verwalten – egal ob im Keller ohne Empfang oder am Schreibtisch im Büro.

Dieses Projekt demonstriert eine robuste **Clean Architecture** und eine komplexe **Synchronisations-Logik** zwischen einem lokalen Client (Room) und einem Laravel-Backend.

---

### 🌟 Key Features

* **📡 Offline-First Architektur:** Volle Funktionalität ohne Internetverbindung. Die lokale Datenbank ist die "Single Source of Truth" für die UI.
* **🔄 Robuster Sync:** Eigener `SyncManager` mit Batch-Processing, Konfliktlösung ("Last Write Wins") und Soft-Delete Support.
* **📱 Cross-Platform:** Eine gemeinsame Codebasis für Android und Desktop (JVM) dank Compose Multiplatform.
* **🎨 Modernes UI:** Material 3 Design mit adaptiven Layouts, Sticky Headers und Custom Components.
* **🔒 Datensicherheit:** Nutzung von UUIDs zur Vermeidung von ID-Kollisionen in verteilten Systemen.

---

### 📸 Screenshots

<div align="center">
  <h4>Desktop</h4>
  <img src="https://github.com/user-attachments/assets/6c85073b-60b5-4b8a-8b22-89c430043168" width="800" alt="Desktop Screenshot">
  <br/><br/> <h4>Android</h4>
  <img src="https://github.com/user-attachments/assets/3927d8da-66f8-4f55-8a16-209b71216990" width="300" alt="Android Screenshot">
</div>


---

### 🛠️ Tech Stack

Dieses Projekt nutzt modernste Technologien und Best Practices:

* **Sprache:** [Kotlin](https://kotlinlang.org/)
* **UI Framework:** [Jetpack Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) (Android + JVM/Desktop)
* **Architecture:** Clean Architecture (Core/Feature Modularization) & MVVM
* **Dependency Injection:** [Koin](https://insert-koin.io/)
* **Local Data:** [Room KMP](https://developer.android.com/kotlin/multiplatform/room) (SQLite mit nativem Treiber)
* **Network:** [Ktor Client](https://ktor.io/) (Content Negotiation, Logging, Auth)
* **Settings:** [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (Type-Safe mit Kotlin Serialization)
* **Concurrency:** Kotlin Coroutines & Flows
* **Utils:** Kotlinx-Datetime, Konnectivity (Network Monitor)

---

### 🚀 Getting Started (Setup)

Da dieses Projekt sensible API-Zugangsdaten benötigt, ist eine lokale Konfiguration erforderlich.

**1. Repository klonen**
```bash
git clone [https://github.com/ayudizzle/PenguinInventoryManager.git](https://github.com/ayudizzle/PenguinInventoryManager.git)
cd PenguinInventoryManager
```

**2. Secrets konfigurieren**
Erstelle im Root-Verzeichnis eine Datei `local.properties` und füge deine API-Konfiguration hinzu (oder nutze `localhost` für eine eigene Laravel-Instanz):

```properties
# local.properties
API_URL=http://localhost:8000/api/
API_KEY=dein-eigener-key
```

**3. App starten**

* **Android:** Öffne das Projekt in Android Studio und starte die `composeApp` Run-Configuration.
* **Desktop:** Führe folgenden Befehl im Terminal aus:
  ```bash
  ./gradlew :composeApp:jvmRun
  ```

###  Installer erstellen

**4. Installer erstellen (Optional)**
Um eine `.msi` (Windows) oder `.dmg` (macOS) zu erstellen:
```bash
./gradlew packageDistributionForCurrentOS
```

---

### 🏗️ Modul-Struktur

Das Projekt ist modular aufgebaut, um eine saubere Trennung der Zuständigkeiten zu gewährleisten (`:app`, `:core`, `:feature`).

* **`app` (composeApp):** Einstiegspunkt, Navigation & DI-Wiring.
* **`core`:**
    * `:database`: Room Entities, DAOs und Plattform-spezifische Factory.
    * `:network`: Ktor Client, DTOs und API-Definitionen.
    * `:datastore`: User-Einstellungen und Sync-Metadaten.
    * `:data`: Repositories und der zentrale `SyncManager` (Verbindet DB & Network).
    * `:ui`: Gemeinsames Design-System, Theme und wiederverwendbare Komponenten.
    * `:model`: Reine Domain-Modelle.
* **`feature`:**
    * `:inventory`: Globale Suche und Bestandsliste.
    * `:storageOverview`: Übersicht der Lagerorte (mit Sticky Headers).
    * `:storageDetails`: Detailansicht mit Filtern (Ablaufdatum).
    * `:itemEntry`: Formular zum Erfassen und Bearbeiten.
    * `:itemMaster`: Verwaltung der Produkt-Stammdaten (Umbenennen & Aufräumen).
 
---

### 💡 Lizenz

Copyright © 2025 ayupi.dev
