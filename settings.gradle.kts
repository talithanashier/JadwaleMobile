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

rootProject.name = "Jadwale"
include(":app")
include(":feature:contract")
include(":feature:dashboard")
include(":feature:schedule_generator")
include(":feature:schedule_view")
include(":feature:schedule_edit")
include(":feature:routine_activities")
include(":feature:teachers")
include(":feature:classes")
include(":feature:subjects")
include(":feature:assignments")
