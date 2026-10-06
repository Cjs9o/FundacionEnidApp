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

    // COMPILACIÓN OFFLINE: `com.android.library` no tiene descargado su
    // artefacto marcador (`com.android.library.gradle.plugin`), porque hasta
    // ahora este proyecto solo usaba `com.android.application`. El motor de
    // Android sí está completo en `com.android.tools.build:gradle`, que es el
    // mismo JAR que registra los dos plugins.
    //
    // Sin esto, añadir el módulo `:campo` obliga a poner `org.gradle.offline`
    // en false solo para bajar una etiqueta de dos kilobytes. Con esto el
    // build sigue siendo reproducible sin red.
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android" && requested.id.id != "com.android.application") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FundacionEnid"
include(":app")
// Módulo de captura de preinscripción en campo (offline-first). Nació como app
// independiente (`EnidAppOfflineFirst`) y se integra aquí como biblioteca: un
// solo APK, una sola Activity, y la app institucional como punto de entrada.
include(":campo")
// Tokens compartidos del sistema visual (paleta base y medidas). `:app` y
// `:campo` dependían de él para que un color no se declare dos veces con
// distinta deriva. No contiene componentes ni lógica de negocio.
include(":ui")