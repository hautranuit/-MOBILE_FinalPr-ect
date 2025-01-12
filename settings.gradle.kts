pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Mapbox Maven repository
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            // Do not change the username below. It should always be "mapbox" (not your username).
            credentials {
                username = "mapbox"
                // Use the secret token stored in gradle.properties as the password
                password = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").orNull
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
        maven { url = uri("https://jitpack.io") }
    }
}


rootProject.name = "FinalProject"
include(":app")
 