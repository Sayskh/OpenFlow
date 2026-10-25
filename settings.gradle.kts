pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OpenFlow"

// App entry point
include(":app")

// Core modules
include(":core:common")
include(":core:designsystem")
include(":core:model")
include(":core:plugin")

// Feature modules
include(":feature:timeline")
include(":feature:preview")
include(":feature:curve-editor")
include(":feature:expression-editor")
include(":feature:export")

// Engine modules
include(":engine:media")
include(":engine:render")
include(":engine:scripting")
include(":engine:vision")
