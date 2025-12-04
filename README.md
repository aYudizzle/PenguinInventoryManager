🐧 Penguin Inventory Manager (PIM)
Eine Offline-First Inventory App, entwickelt mit Kotlin Multiplatform (KMP) und Compose Multiplatform für Android und Desktop.

Dieses Projekt demonstriert eine robuste Clean Architecture und eine komplexe Synchronisations-Logik zwischen lokalem Client und einem Laravel-Backend.

🌟 Key Features
Offline-First Architektur: Volle Funktionalität ohne Internetverbindung dank lokaler Datenbank.

Robuster Sync: Eigener SyncManager mit Batch-Processing, Konfliktlösung ("Last Write Wins") und Soft-Delete Support.

Modern UI: Material 3 Design mit Adaptive Layouts, Sticky Headers und Custom Components.

Datensicherheit: Nutzung von UUIDs zur Vermeidung von ID-Kollisionen in verteilten Systemen.

🛠 Tech Stack
UI: Compose Multiplatform (Android + JVM/Desktop)

Architecture: Clean Architecture (Core/Feature Modularization), MVVM

Dependency Injection: Koin

Local Data: Room KMP (SQLite)

Network: Ktor Client (Content Negotiation, Logging)

Settings: DataStore (Type-Safe mit Kotlin Serialization)

Concurrency: Kotlin Coroutines & Flows

Utils: Kotlinx-Datetime, Napier (Logging), Konnectivity (Network Monitor)