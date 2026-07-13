# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**FinanceFlow** — a native Android personal finance budgeting app built with Jetpack Compose and Material Design 3 Expressive. Single-module (`:app`) project, local-only data via Room.

## Build & Run Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.jd.financeflow.ExampleUnitTest"

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Lint check
./gradlew lint

# Clean build
./gradlew clean assembleDebug
```

## Architecture

**MVI + Clean Architecture** within a single `:app` module, organised by feature under `features/`:

```
features/
  core/
    domain/       — Result<D,E>, DataError, Error, EmptyResult
    presentation/ — UiText, DataErrorToUiText, shared composables
  finance/
    data/
      database/           — Room entity, DAO, database, type converters, factory
      localStorageDataSource/ — LocalStorageDataSource (wraps DAO, returns Result)
      mappers/            — FinanceEntity ↔ Finance domain model
      repository/         — FinanceRepositoryImpl
    domain/
      models/             — Finance, FinanceCategory, FinanceInterval, FilterConfiguration, ChartData, SortOrder
      FinanceRepository.kt — interface
    presentation/
      home/               — HomeViewModel / HomeState / HomeActions / HomeScreen
      financeList/        — list of all transactions
      financeDetails/     — add/edit a transaction
      financeListFilter/  — filter sheet for the list
      charts/             — ChartsViewModel + FinanceChartCardViewModel
      components/         — shared composables (FinanceList, currency helpers, etc.)
  navigation/
    domain/   — NavKey sealed interfaces (MainNavigationBarKey, HomeRootKey, FinanceRootKey, ChartsRootKey)
    presentation/ — MainNavigation (single NavDisplay, per-tab back stacks)
```

**Data flow:** `Room DAO → LocalStorageDataSource → FinanceRepositoryImpl → ViewModel → State (StateFlow) → Screen composable`

### Key patterns

- **Result wrapper** — `Result<D, E : Error>` with `.onSuccess`, `.onError`, `.map`, `asEmptyDataResult()`. All repository and data-source methods return this type.
- **MVI** — each screen has `*State` (data class), `*Actions` (sealed interface), and a ViewModel that exposes `val state: StateFlow<*State>` and `fun onAction(action: *Actions)`. Screen composables receive the VM and call `onAction`.
- **Navigation** — **Navigation 3** (`androidx.navigation3`). Nav keys are `@Serializable` data objects/classes implementing `NavKey`. Each bottom-nav tab owns its own `rememberNavBackStack`; `MainNavigation` switches between them and renders a single `NavDisplay`.
- **DI** — Koin `appModule` in `di/Modules.kt`. ViewModels with constructor parameters use `koinViewModel(parameters = { parametersOf(...) })`.
- **Currency** — `currencyCode` (default `"EUR"`) is passed as a parameter down from `MainNavigation` through nav keys into ViewModels.

## Key Dependencies

| Purpose | Library |
|---|---|
| UI | Jetpack Compose BOM 2026.06.00, Material3 Expressive 1.5.0-alpha22 |
| Navigation | Navigation 3 (`androidx.navigation3`) 1.1.3 |
| Database | Room 2.8.4 (KSP for annotation processing) |
| DI | Koin 4.2.2 |
| Serialization | kotlinx.serialization 1.11.0 |
| Charts | `io.github.ehsannarmani:compose-charts` 0.2.5 |
| Date/Time | `kotlinx-datetime` 0.8.0 |

## Skills (active coding standards)

The `.claude/skills/` directory contains binding rules for every layer. Always apply the relevant skill before writing or reviewing code:

- `android-compose-ui` — composable stability, recomposition, side effects, previews
- `android-data-layer` — data sources, repositories, DTOs, mappers, Room entities
- `android-di-koin` — Koin module definitions and ViewModel injection
- `android-error-handling` — `Result<T,E>`, `DataError`, `EmptyResult`, extension helpers
- `android-module-structure` — module layout and Gradle convention plugins
- `android-navigation` — Navigation 3 back-stack management and NavKey definitions
- `android-presentation-mvi` — State / Actions / ViewModel conventions
- `android-testing` — unit and instrumented test patterns
