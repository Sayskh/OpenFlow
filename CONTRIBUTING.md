# Contributing to OpenFlow

Thank you for contributing to OpenFlow, an open-source, non-linear video editor for Android.

## 1. Clean-Room Engineering Policy

OpenFlow strictly operates under a clean-room development philosophy:
- **Zero Proprietary Code:** Contributors must NOT inspect, decompile, trace, or paste code from proprietary, closed-source video editing applications (e.g., Alight Motion, CapCut, Premiere Rush).
- **Independent Implementation:** All algorithms, shaders, curve math, and timeline logic must be derived from public standards, academic papers, open documentation, or independently authored clean-room specifications.
- **Licensing:** All contributions must be original works licensed under the project's open-source license.

## 2. Commit Message Convention

OpenFlow enforces the **Conventional Commits** specification:

```
<type>(<scope>): <short description>
```

- **Types:** `feat`, `fix`, `perf`, `refactor`, `style`, `test`, `docs`, `chore`
- **Scope:** Typically the module name without prefix (e.g., `timeline`, `render`, `curve-editor`, `media`, `vision`, `build`)
- **Example:** `feat(timeline): implement pinch-to-zoom track scaling`

## 3. Modular Architecture Boundaries

OpenFlow is strictly decoupled across four module tiers:

1. **`:app`:** Android Application entry point and navigation graph.
2. **`:core:*`:** Fundamental foundations (`:common`, `:designsystem`, `:model`). No feature or engine dependencies.
3. **`:feature:*`:** Isolated UI/UX feature domains (`:timeline`, `:preview`, `:curve-editor`, `:expression-editor`, `:export`). Features communicate via domain models and state hoisted to ViewModel/engine layers. Features do NOT depend directly on each other.
4. **`:engine:*`:** Hardware acceleration and domain processing (`:media`, `:render`, `:scripting`, `:vision`). Engines interact with low-level Android frameworks (Media3, OpenGL/Vulkan, MediaPipe, NDK).

## 4. Development Workflow

1. **Fork & Branch:**
   ```bash
   git checkout -b feat/your-feature-name
   ```
2. **Code Style:**
   - Adhere to official Kotlin coding conventions (`kotlin.code.style=official`).
   - Follow Jetpack Compose API guidelines (hoisting state, unidirectional data flow, immutability).
3. **Verify Locally:**
   ```bash
   ./gradlew lint
   ./gradlew test
   ```
4. **Submit PR:**
   - Fill in the Pull Request template completely.
   - Confirm adherence to the clean-room checklist.
