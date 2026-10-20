// ===========================================================================
// MODULO `:ui` — Tokens compartidos del sistema visual
//
// Existe por una razón concreta. Antes de integrar `:campo`, ese módulo traía
// su propia copia de `Color.kt`, `Type.kt` y sus métricas. Resultado: doce
// colores con el mismo nombre y el mismo valor escritos en dos ficheros, dos
// escalas tipográficas y dos juegos de medidas conviviendo en el mismo APK.
// El día que alguien cambiara un azul en un sitio y no en el otro, la app
// quedaría desalineada sin que ninguna prueba lo notara.
//
// Aquí viven solo los **tokens**: la paleta base, los tamaños de texto y las
// medidas de espaciado. Los componentes siguen perto de quien los usa, porque
// los de `:app` y los de `:campo` no son iguales: tienen firmas y
// comportamientos distintos. Unificar componentes es un refactor aparte
// (Fase 21 del PLANDESARROLLO); los tokens sí son la misma cosa y no tenían
// motivo para estar duplicados.
//
// `:app` y `:campo` dependen de este módulo. Ninguno depende del otro para el
// tema, de modo que el módulo de campo no hereda nada de la app institucional.
// ===========================================================================
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.fundacion.enid.ui.shared"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 24

    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.graphics)

    // `:ui` no necesita lifecycle en su código, pero sí lo necesita el grafo
    // que resuelve: solo con Compose, el BOM fija `lifecycle-common-jvm` en
    // 2.8.7, que no está en la caché offline. Se fija aquí la misma versión
    // que usan `:app` y `:campo` para que los tres módulos resuelvan el mismo
    // artefacto y el build siga siendo 100 % offline.
    implementation(libs.androidx.lifecycle.runtime.ktx)
}