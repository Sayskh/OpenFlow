# OpenFlow

> High-performance, open-source, non-linear video editing and motion graphics engine for Android. Built with modern Android stack: Jetpack Compose, Material 3, AndroidX Media3, and MediaPipe.

---

## Clean-Room Engineering Mandate

OpenFlow is developed strictly under clean-room engineering principles:
- **Clean-Room Specification:** All feature sets, data schemas, and UI designs are defined strictly through independent requirement specifications and public API standards.
- **Zero Reverse-Engineering:** Contributed code must be original. Reverse engineering, decompiling, or copying proprietary closed-source mobile video editors is strictly forbidden.
- **Full Traceability:** Every commit is tracked via Conventional Commits and verified against open-source compliance standards.

---

## Architectural Overview

OpenFlow utilizes a modular architecture divided into four distinct layers:

```
                      ┌──────────────────────────┐
                      │          :app            │
                      └─────────────┬────────────┘
                                    │
    ┌───────────────────────────────┴───────────────────────────────┐
    ▼                               ▼                               ▼
┌──────────────────────────┐  ┌──────────────────────────┐  ┌──────────────────────────┐
│    :feature:timeline     │  │     :feature:preview     │  │  :feature:curve-editor   │
├──────────────────────────┤  ├──────────────────────────┤  ├──────────────────────────┤
│:feature:expression-editor│  │     :feature:export      │  │                          │
└────────────┬─────────────┘  └─────────────┬────────────┘  └─────────────┬────────────┘
             │                              │                             │
             └──────────────────────┬───────┴─────────────────────────────┘
                                    │
    ┌───────────────────────────────┴───────────────────────────────┐
    ▼                               ▼                               ▼
┌──────────────────────────┐  ┌──────────────────────────┐  ┌──────────────────────────┐
│      :engine:media       │  │      :engine:render      │  │     :engine:vision       │
├──────────────────────────┤  ├──────────────────────────┤  ├──────────────────────────┤
│    :engine:scripting     │  │                          │  │                          │
└────────────┬─────────────┘  └─────────────┬────────────┘  └─────────────┬────────────┘
             │                              │                             │
             └──────────────────────┬───────┴─────────────────────────────┘
                                    ▼
       ┌────────────────────────────────────────────────────────┐
       │                        :core:*                         │
       │   :core:model   │   :core:designsystem   │ :core:common │
       └────────────────────────────────────────────────────────┘
```

### Module Breakdown

| Module | Category | Description |
|---|---|---|
| `:app` | Application | Android entry point, dependency injection root, navigation router |
| `:core:common` | Core | Shared utilities, coroutines dispatchers, extensions, math helpers |
| `:core:designsystem` | Core | Material 3 dark-theme design tokens, neutral D65 canvas palette, base UI primitives |
| `:core:model` | Core | Domain models: project schema, timeline tracks, keyframes, Bézier curves |
| `:feature:timeline` | Feature | Multi-track timeline UI, clip trimming, snapping, zoomable canvas |
| `:feature:preview` | Feature | Real-time playback surface, aspect ratio masking, interactive transform handles |
| `:feature:curve-editor` | Feature | Multi-point Bézier velocity / value curve manipulator |
| `:feature:expression-editor` | Feature | Dynamic parameter scripting & expression evaluation interface |
| `:feature:export` | Feature | Encoding preset selection, bitrate configurator, progress dashboard |
| `:engine:media` | Engine | AndroidX Media3 (ExoPlayer & Transformer) pipeline, demuxing/muxing |
| `:engine:render` | Engine | Surface rendering, OpenGL ES / Vulkan shader graph, visual effects |
| `:engine:scripting` | Engine | Evaluator for keyframe expressions and dynamic property bindings |
| `:engine:vision` | Engine | Google MediaPipe integration (smart segmentation, tracking, keypoints) |

---

## Tech Stack & Dependencies

- **Language:** Kotlin 2.0+
- **Build Tool:** Gradle with Kotlin DSL (`build.gradle.kts`) & Version Catalog (`gradle/libs.versions.toml`)
- **UI Framework:** Jetpack Compose with Material 3
- **Media Pipeline:** AndroidX Media3 (`1.4.1`) — ExoPlayer, Transformer, Effect, Session
- **Computer Vision:** Google MediaPipe Tasks Vision (`0.10.14`)
- **Concurrency & Serialization:** Kotlinx Coroutines (`1.9.0`) & Kotlinx Serialization JSON (`1.7.3`)

---

## Getting Started

### Prerequisites

- **Android Studio:** Ladybug (2024.2.1+) or newer
- **JDK:** OpenJDK 17 or 21
- **Android SDK:** Compile SDK 35 / Min SDK 26 (Android 8.0 Oreo)
- **NDK:** 26.x+ (required for native render and media acceleration)

### Building the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/openflow-editor/openflow.git
   cd openflow
   ```

2. Sync and compile via Gradle:
   ```bash
   ./gradlew assembleDebug
   ```

3. Run unit tests across modules:
   ```bash
   ./gradlew test
   ```

---

## License

OpenFlow is released under the [Apache License 2.0](LICENSE).
