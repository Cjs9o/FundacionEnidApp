// ===========================================================================
// MODULO `:campo` — Captura de preinscripcion en campo (offline-first)
//
// Nace de `EnidAppOfflineFirst`, que era una app Android independiente. Se
// integra aqui como biblioteca, no como app: la app institucional (`:app`)
// sigue siendo la unica que produce el APK, la unica `Activity` y la unica
// pantalla de inicio.
//
// Que sea modulo y no una carpeta dentro de `:app` es una decision de
// arquitectura, no de comodidad:
//   · No choca la navegacion. `:app` resuelve su pila propia (`NavController`)
//     y `:campo` su `NavHost` de navigation-compose. En un solo modulo las dos
//     pilas coexistirian en el mismo grafo sin relacion entre si.
//   · No choca el tema. `:campo` se tematiza con `EnidAppTheme`, la app con
//     `FundacionEnidTheme`; anidados, cada modulo manda en su parte.
//   · Room + KSP quedan encapsulados aqui. Quien lea la app institucional no
//     encuentra una sola linea de base de datos.
//
// Lo que si comparte es lo que tiene que compartir: `res/xml/file_paths.xml` y
// el `FileProvider`, porque las fotos de evidencia se copian al almacenamiento
// interno y se entregan a la app de camara mediante un content:// temporal.
// ===========================================================================
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.fundacion.enid.campo"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
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

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    // `api` y no `implementation`: `:app` compone `EnidAppRaiz` y llama a
    // rememberLauncherForActivityResult / ViewModel, que vienen de aqui.
    api(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.core.ktx)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.androidx.navigation.compose)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}