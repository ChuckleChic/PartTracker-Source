//pluginManagement {
//    repositories {
//        google()
//        mavenCentral()
//        gradlePluginPortal()
//    }
//    resolutionStrategy {
//        eachPlugin {
//            // Optional: Handle plugin aliases or overrides here if needed
//        }
//    }
//}
//
//dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // ✅ More flexible than FAIL_ON_PROJECT_REPOS
//    repositories {
//        google()
//        mavenCentral()
//    }
//    versionCatalogs {
//        create("libs") {
//            from(files("gradle/libs.versions.toml"))
//        }
//    }
//}
//
//
//
//
//rootProject.name = "PartTracker"
//include(":app")
//
//
//

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal() // ✅ Needed for plugins like KSP, SafeArgs, etc.
    }
    resolutionStrategy {
        eachPlugin {
            // Optional: Map legacy plugin IDs to aliases if needed
            // Example:
            // if (requested.id.id == "androidx.navigation.safeargs.kotlin") {
            //     useVersion("2.7.7")
            // }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // ✅ Prevents module-level override issues
    repositories {
        google()
        mavenCentral()
    }

}

rootProject.name = "PartTracker"
include(":app")

