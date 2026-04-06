# Zorvyn — Personal Finance Tracker

A clean, minimal Android finance app built with Jetpack Compose. Zorvyn helps you track income and expenses, set daily spending goals, and visualize your financial habits — all stored locally on your device with no accounts, no cloud sync, and no ads.

## Features

**Core Tracking**
- Log income and expenses with categories, notes, and custom dates
- View your total balance, total income, and total expense at a glance
- Add past transactions for historical data entry

**Transaction Management**
- Swipe left on any transaction to reveal the **Edit** button — tap to update amount, type, category, note, or date
- Swipe right on any transaction to reveal the **Delete** button — tap to confirm and remove
- Search transactions by category or note with a live search bar
- Filter transactions by All, Income, or Expense
- Full transaction history with result count

**Daily Goal & Streak**
- Set a daily spending limit
- Track how many consecutive days you've stayed within budget
- Visual progress bar showing today's spend vs. your limit

**Insights**
- Swipeable feed cards with personalized financial tips and your own spending patterns (week-over-week comparisons, top categories, projected monthly totals)
- Analytics tab with a weekly spending bar chart, category breakdown bars, and a 35-day spending heatmap

**Security**
- PIN-based authentication on app open (4-digit, SHA-256 hashed)
- All data lives on-device nothing leaves your phone

**Theming**
- Dark mode (black + vivid mint green)
- Light mode (soft mint greens + deep forest dark text)
- Theme persists across sessions via DataStore

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI Framework | Jetpack Compose (Material 3) |
| Architecture | MVVM (ViewModel + StateFlow) |
| Local Database | Room (SQLite) |
| Preferences | DataStore Preferences |
| Navigation | Navigation Compose |
| Async | Kotlin Coroutines + Flow |
| Build System | Gradle (Kotlin DSL) |
| Min SDK | API 24 (Android 7.0) |
| Target SDK | API 35 (Android 15) |

### Key Libraries

```toml
# UI
androidx.compose.bom            = "2024.09.00"
androidx.compose.material3
androidx.compose.material:material-icons-extended

# Navigation
androidx.navigation:navigation-compose    = "2.7.7"

# ViewModel
androidx.lifecycle:lifecycle-viewmodel-compose  = "2.8.3"

# Room
androidx.room:room-runtime      = "2.7.0"
androidx.room:room-ktx          = "2.7.0"

# DataStore
androidx.datastore:datastore-preferences = "1.1.1"

# Annotation Processing
com.google.devtools.ksp         = "2.2.10-2.0.2"
```

---

## Project Structure

```
app/src/main/java/com/example/zorvyn_task/
│
├── MainActivity.kt                  # Entry point, theme state management
│
├── data/
│   ├── local/
│   │   ├── TransactionEntity.kt     # Room entity (amount, type, category, date, note)
│   │   ├── TransactionDao.kt        # DAO — insert, update, delete, getAllTransactions
│   │   ├── TransactionDatabase.kt   # Room database singleton
│   │   ├── GoalPreferences.kt       # DataStore — daily limit, streak, last checked date
│   │   └── UserPreferences.kt       # DataStore — user ID, name, PIN hash, auth state
│   │
│   └── repository/
│       ├── TransactionRepository.kt
│       ├── GoalRepository.kt
│       └── UserRepository.kt
│
└── ui/
    ├── auth/
    │   ├── AuthScreen.kt            # Setup (new user) + PIN entry screens
    │   ├── AuthViewModel.kt
    │   └── AuthUiState.kt
    │
    ├── home/
    │   ├── HomeScreen.kt            # Balance card, income/expense row, transaction list
    │   ├── HomeViewModel.kt
    │   └── HomeUiState.kt
    │
    ├── goal/
    │   ├── GoalSection.kt           # Daily goal card with progress bar
    │   ├── GoalViewModel.kt         # Streak evaluation logic
    │   └── GoalState.kt
    │
    ├── addtransaction/
    │   ├── AddTransactionScreen.kt  # Type toggle, amount, category chips, note
    │   ├── AddTransactionViewModel.kt
    │   └── AddTransactionUiState.kt
    │
    ├── insights/
    │   ├── InsightsScreen.kt        # Swipe card feed + analytics charts
    │   ├── InsightsViewModel.kt     # Weekly/monthly aggregation, heatmap data
    │   └── InsightsUiState.kt
    │
    ├── profile/
    │   ├── ProfileScreen.kt         # User stats, theme toggle, transaction history,
    │   │                            # swipe-to-edit/delete, add past tx, reset
    │   └── ProfileViewModel.kt
    │
    ├── navigation/
    │   ├── AppNavigation.kt         # NavHost — Auth → Main → AddTransaction
    │   ├── MainScreen.kt            # Tab host with AnimatedContent
    │   └── BottomNavBar.kt          # Custom pill nav bar with center FAB
    │
    ├── components/
    │   ├── GlassComponents.kt       # GlassCard + GlassBackground composables
    │   └── HapticHelper.kt          # Vibration feedback (tick, click, error)
    │
    └── theme/
        ├── Color.kt                 # Dark + light color palettes
        ├── Theme.kt                 # AppColors token system + ZorvynTaskTheme
        ├── Type.kt                  # Typography scale (Color.Unspecified for theme-awareness)
        └── ThemeManager.kt          # DataStore-backed dark/light mode persistence
```

---

## Architecture Overview

Zorvyn follows a standard **MVVM** pattern with unidirectional data flow:

```
UI (Composable)
    │  observes StateFlow
    ▼
ViewModel
    │  calls suspend functions
    ▼
Repository
    │  wraps
    ▼
Room DAO / DataStore
```

Each screen has its own `ViewModel` with a corresponding `UiState` data class. The `ViewModel` exposes a single `StateFlow<UiState>` that the composable collects. User actions call methods on the `ViewModel`, which update the state — the UI never mutates state directly.

The theme system uses a custom `AppColors` data class distributed via `CompositionLocal`, giving every composable type-safe access to the correct color tokens for the active theme without relying on `MaterialTheme.colorScheme` indirection.

---

## Transaction Edit & Delete

Transactions in the Profile screen support gesture-based actions:

- **Swipe left** → green Edit button slides in from the right. Tap it to open the edit dialog where you can change the amount, type, category, note, and date.
- **Swipe right** → red Delete button slides in from the left. Tap it to open a confirmation dialog before permanently removing the transaction.
- **Tap the card while swiped** → snaps it back to rest position.

The action buttons are hidden completely at rest — they only appear once the card has been dragged past a threshold, so the list looks clean by default.

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 21 (configured via `gradle-daemon-jvm.properties`)
- Android device or emulator running API 24+

### Build & Run

```bash
# Clone the repository
git clone https://github.com/your-username/zorvyn.git
cd zorvyn

# Open in Android Studio and sync Gradle,
# or build from the command line:
./gradlew assembleDebug

# Install on a connected device
./gradlew installDebug
```

No API keys or external services are required. The app is entirely self-contained.

---

## Data & Privacy

All data is stored locally using **Room** (SQLite) and **DataStore** on the device. No data is transmitted to any server. The user's PIN is never stored in plaintext — it is hashed with **SHA-256** before being saved to DataStore. Deleting the app removes all data permanently.

---

## Permissions

| Permission | Reason |
|---|---|
| `VIBRATE` | Haptic feedback on navigation and key presses |

No internet, location, camera, or contacts permissions are requested.

---

