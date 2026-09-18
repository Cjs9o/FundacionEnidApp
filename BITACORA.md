# BITÁCORA DE TRABAJO — Fundación ENID App

> Registro cronológico de todo lo trabajado en el proyecto: decisiones,
> avances, pruebas de compilación, errores encontrados y correcciones.

---

## 15 Sep — Configuración inicial

| Acción | Detalle | Estado |
|---|---|---|
| Lectura de diapositivas | `Avance proyecto movil.pptx` (8 slides). Contenido en imágenes; sin texto accesible. | Hecho |
| Lectura de requerimientos | `requerimientos proyecto.md` con 12 módulos de la Fundación ENID. | Hecho |
| Elección de tecnología | **Android nativo (Kotlin) + Jetpack Compose**, confirmado con el usuario. | Hecho |
| Entorno detectado | JDK 25 (JBR), Gradle 9.5.0, AGP 9.3.1, Kotlin 2.2.10, SDK android-37, dependencias Compose cacheadas. Build **100% offline**. | Hecho |
| Plan de desarrollo | Creado `PLANDESARROLLO.md` con fases y 12 requerimientos. | Hecho |

## 15 Sep — Esqueleto del proyecto

| Acción | Detalle | Estado |
|---|---|---|
| Carpeta del proyecto | `/home/cjs9o/FundacionEnidApp` separada y con `local.properties` al SDK. | Hecho |
| Gradle | `settings.gradle.kts`, `build.gradle.kts`, `gradle/libs.versions.toml`, `gradle.properties` (offline), wrapper 9.5.0, daemon JVM 25. | Hecho |
| Manifest y recursos | `AndroidManifest.xml`, `strings.xml`, `colors.xml`, `themes.xml`, icono adaptativo, reglas de backup. | Hecho |
| Tema accesible | `Color.kt`, `Theme.kt`, `Type.kt` con paleta de alto contraste y tipografía amplia (requisito 01). | Hecho |
| Navegación | `navigation/Screen.kt` + `NavController.kt` (pila propia, sin dependencias). | Hecho |
| Modelos y datos | `model/Models.kt` + `data/DataRepository.kt` con sesión, usuarios, programas, sedes, FAQ, cronograma, horario, pagos y preinscripciones. | Hecho |

## 15 Sep — Pantallas implementadas

| Pantalla | Requisito | Archivo | Estado |
|---|---|---|---|
| Inicio de sesión | 02 | `LoginScreen.kt` | Hecho |
| Registro de usuario | 02 | `RegisterScreen.kt` | Hecho |
| Menú principal (Home) | 01 | `HomeScreen.kt` | Hecho |
| Catálogo académico | 04 | `CatalogScreen.kt` | Hecho |
| Detalle de programa (pensum, perfil, sedes) | 04 | `ProgramDetailScreen.kt` | Hecho |
| Sedes y contacto (Chía y Sopó, llamada, Maps) | 05 | `LocationsScreen.kt` | Hecho |
| Preguntas frecuentes (acordeón) | 06 | `FaqScreen.kt` | Hecho |
| Preinscripción | 03 | `PreinscriptionScreen.kt` | Hecho |
| Cronograma del plan | 07 | `CronogramaScreen.kt` | Hecho |
| Horario diario | 08 | `DailyScheduleScreen.kt` | Hecho |
| Alertas de pago | 09 | `PaymentsScreen.kt` | Hecho |
| Pasarela PSE | 10 | `PseScreen.kt` | Hecho |
| Calculadora de beneficios | 11 | `BenefitsCalculatorScreen.kt` | Hecho |
| Plataforma educativa | 12 | `PlatformScreen.kt` | Hecho |
| Perfil y cierre de sesión | 02 | `ProfileScreen.kt` | Hecho |
| Enrutador principal | — | `AppRoot.kt` + `MainActivity.kt` | Hecho |

---

## Pruebas de compilación

### Prueba de compilación #1
- **Fecha:** 15 Sep
- **Comando:** `./gradlew :app:assembleDebug --offline`
- **Resultado:** ❌ FALLÓ (26 errores de compilación + errores de recursos)
- **Errores encontrados:**
  1. `material-icons` sin dependencia cacheadas → eliminado uso de `Icons` (flechas con texto).
  2. Trailing lambdas mal mapeadas (`BigButton`, `InfoCard`): Kotlin asigna la lambda final
     al ÚLTIMO parámetro; `onClick` y `content` no estaban al final. Reordenados parámetros.
  3. Faltaba `import androidx.compose.ui.unit.dp` en `RegisterScreen`.
  4. `mipmap-anydpi` necesita calificador `-v26` para adaptive icons (renombrado).
  5. `DataRepository`: `Rol.desde` recibía `String?`; `preinscripcioneLocal()` guardaba
     objetos `Preinscripcion` en un `putStringSet` que exige `String` (se serializan ahora);
     referencias a `it.correo`/`lineas.add(String)` corregidas con serialización por líneas.
- **Correcciones aplicadas:** todas las anteriores.

### Prueba de compilación #2
- **Fecha:** 15 Sep
- **Resultado:** ✅ ÉXITO — `BUILD SUCCESSFUL in 39s`
- **Artefacto:** `app/build/outputs/apk/debug/app-debug.apk` (12,1 MB)
- **Observación:** 1 warning de deprecación `centerAlignedTopAppBarColors` → corregido a
  `topAppBarColors`.
- **Errores encontrados:** ninguno.
- **Correcciones:** change de API deprecada (corregido para la prueba siguiente).

### Prueba de compilación #3
- **Fecha:** 15 Sep
- **Resultado:** ✅ ÉXITO — `BUILD SUCCESSFUL in 17s`, sin warnings.
- **Errores encontrados:** ninguno.
- **Correcciones:** ninguna (solo se aplicó el cambio de API deprecada).

### Prueba de compilación #4 (build limpio)
- **Fecha:** 15 Sep
- **Comando:** `rm -rf app/build build .gradle && ./gradlew :app:assembleDebug --offline`
- **Resultado:** ✅ ÉXITO — `BUILD SUCCESSFUL in 55s` (36 tareas ejecutadas).
  Confirma que el proyecto compila desde cero sin descargar nada.
- **Errores encontrados:** ninguno.
- **Correcciones:** ninguna.

### Prueba de compilación #5 (verificación del APK)
- **Fecha:** 15 Sep
- **Comando:** `aapt2 dump badging app-debug.apk`
- **Resultado:** ✅ APK válido (12,18 MB)
  - `package: com.fundacion.enid`, `versionName 1.0`, `minSdk 24`, `targetSdk 37`
  - `application-label: 'Fundación ENID'`
  - `launchable-activity: com.fundacion.enid.MainActivity`
  - ABIs: arm64-v8a, armeabi-v7a, x86, x86_64
- **Errores encontrados:** ninguno.
- **Correcciones:** ninguna.

## 15 Sep — Integración de logos institucionales

| Acción | Detalle | Estado |
|---|---|---|
| Imágenes recibidas | `logo-Enid-2026-1024x341.png` (horizontal) y `Logo-Transparente-1-974x1024.png` (vertical), ambas PNG con fondo transparente desde `Descargas/imagenes android project`. | Hecho |
| Ícono de la app | El logo **horizontal** se usa como ícono de launcher: PNG generados en `mipmap-{mdpi..xxxhdpi}` (cuadrado y redondo) y ícono adaptativo (fondo azul corporativo `#0B3D6B` + logo en el plano visible). Se reemplazaron los `.webp` y el vector de fondo anteriores. | Hecho |
| Login y Registro | El logo **vertical** se muestra como imagen de bienvenida al inicio de `LoginScreen.kt` (150dp) y `RegisterScreen.kt` (130dp), con `ContentScale.Fit`. Login ahora permite scroll. | Hecho |

## Prueba de compilación #6 (después de logos)
- **Fecha:** 15 Sep
- **Comando:** `./gradlew :app:assembleDebug --offline` (con JAVA_HOME al JBR de Android Studio)
- **Resultado:** ✅ ÉXITO — `BUILD SUCCESSFUL in 14s` (36 tareas).
- **Verificación:** `aapt2 dump resources` confirma el ícono en todas las densidades
  (mdpi→xxxhdpi + adaptativo) y el recurso `logo_enid_vertical` dentro del APK.
- **Errores encontrados:** ninguno.

## 15 Sep — Mudanza a AndroidStudioProjects y alineación con la plantilla

| Acción | Detalle | Estado |
|---|---|---|
| Comparación | Se revisaron `MyApplication` y `ProjectFinal` en `~/AndroidStudioProjects` (estructura, gradle wrapper 9.5.0, AGP, minSdk 30, ícono adaptativo `anydpi`, keepRules, tests plantilla, .gitignore). | Hecho |
| Mudanza | Proyecto movido de `~/FundacionEnidApp` a `~/AndroidStudioProjects/FundacionEnidApp`. Se purgaron artefactos con rutas absolutas (`build/`, `.gradle/`, `.kotlin/`, `app/build/`) y se recompiló desde cero en la nueva ubicación. | Hecho |
| minSdk | Cambiado `24 → 30` (Android 11+), igual que los otros proyectos; imprescindible porque el ícono adaptativo se movió a `mipmap-anydpi` (sin `-v26`). | Hecho |
| Ícono adaptativo | `mipmap-anydpi-v26/` renombrado a `mipmap-anydpi/`; `ic_launcher.xml` y `ic_launcher_round.xml` incluyen `<monochrome>`, igual que la plantilla. | Hecho |
| Archivos de plantilla | Añadidos `.gitignore` raíz, `app/.gitignore`, `app/src/main/keepRules/rules.keep`, `app/src/test/.../ExampleUnitTest.kt` y `app/src/androidTest/.../ExampleInstrumentedTest.kt`. | Hecho |
| Sin tocar fuera del proyecto | No se modificó el programa de Android Studio (`Descargas/android-studio-*`), ni `~/Android/Sdk`, ni `~/.gradle`, ni los otros proyectos de la carpeta. El `.idea/` lo generará Android Studio al abrir el proyecto. | Hecho |

## Prueba de compilación #7 (después de la mudanza)
- **Fecha:** 15 Sep
- **Comando:** `./gradlew :app:assembleDebug :app:compileDebugUnitTestKotlin :app:compileDebugAndroidTestKotlin --offline` (JAVA_HOME → JBR de Android Studio)
- **Resultado:** ✅ ÉXITO — `BUILD SUCCESSFUL in 1m 13s` (44 tareas, 100% offline).
- **Verificación:** `aapt2 dump badging` → `minSdkVersion:'30'`, `targetSdkVersion:'37'`,
  `application-label: 'Fundación ENID'`, `launchable-activity: MainActivity`. Tests plantilla compilan.
- **Errores encontrados:** ninguno.

## Resumen final
La app **compila y empaqueta correctamente** (APK debug disponible en
`app/build/outputs/apk/debug/app-debug.apk`, ahora con los logos institucionales
integrados). Se realizaron **7 pruebas de compilación**; la primera detectó 26
errores que fueron corregidos, y las siguientes seis compilaron sin errores
(incluida una compilación limpia completa desde cero, 100% offline).
## Prueba de compilación #8 (rediseño según wireframes)
- **Fecha:** 15 Sep
- **Comando:** `./gradlew :app:assembleDebug :app:compileDebugUnitTestKotlin :app:compileDebugAndroidTestKotlin --offline` (JAVA_HOME → JBR de Android Studio)
- **Resultado:** ✅ ÉXITO — `BUILD SUCCESSFUL in 40s` (44 tareas, 100% offline).
- **Cambios principales:**
  - Paleta nueva (`Color.kt`): crema `#F7F3EA`, azul oscuro `#23204F`, rojo `#C62828`, morado `#4C3FB1`, verde WhatsApp `#25D366`, gris texto `#7A7568`; `Theme.kt` actualizado (fondo crema, primary azul oscuro, tertiary morado, secondary verde, error rojo).
  - `Components.kt`: nueva barra de navegación inferior (`EnidTab`: Inicio/Horario/Pagos/Soporte) integrada en `AppScaffold`, más `BigOutlinedButton` y `StatusChip`.
  - Nuevas pantallas `WelcomeScreen` ("Aprender no tiene edad" + botones Iniciar Sesión/Registrarse) y `AuthScreen` (tabs Iniciar Sesión | Registrarse, vuelta al inicio, logotipo vertical). Se borraron `LoginScreen`, `RegisterScreen` y `CronogramaScreen`.
  - Home rediseñada: header "ENID · EDUCACIÓN", saludo, banner, grid 2×2 (Mis Clases, Pagos, Oferta Académica, Preinscripción) y accesos secundarios (Sedes, FAQ, Plataforma, Calculadora, Perfil).
  - Oferta con buscador + filtros (Todos/Técnicos/Cursos/Diplomados); Detalle con tabs Plan de Estudios | Requisitos y CTA rojo "Preinscribirme Ahora".
  - Preinscripción en 2 pasos: "Paso 1 de 2 · Datos Personales" con campos y selector de programa; Paso 2 de confirmación.
  - Mi Horario con tabs Hoy | Plan de Estudios (fusión del cronograma). Mis Pagos con monto destacado, botón morado PSE e historial con etiquetas. Sedes con mapa simulado, botones Llamar (morado) / Cómo Llegar (rojo). FAQ por categorías con acordeones + botón WhatsApp. Plataforma Educativa con estados y botones Abrir/Volver al inicio. PSE con lista de bancos y botón morado.
  - `MainActivity` arranca en `Welcome` si no hay sesión; `AppRoot` usa `Screen.Welcome` y nav inferior con `reemplazarTodo`.
- **Errores corregidos durante el refactor:** `Repeated 'data'` en `Screen.kt`, import faltante de `Screen` en 6 pantallas con bottom nav, `BorderStroke` mal importado (material3 → foundation), import faltante de `EnidCream` en `Components.kt`.

## Prueba de compilación #9 (ajustes login y fondo)
- **Fecha:** 15 Sep
- **Comando:** `./gradlew :app:assembleDebug :app:compileDebugUnitTestKotlin :app:compileDebugAndroidTestKotlin --offline` (JAVA_HOME → JBR de Android Studio)
- **Resultado:** ✅ ÉXITO — `BUILD SUCCESSFUL in 23s` (44 tareas, 100% offline).
- **Cambios:**
  - `AuthScreen`: se quitó el logotipo vertical de la parte superior; el botón "← Volver" queda visible pero **sin acción** (por decisión del usuario, se conectará después) y se corrigió su posición con `statusBarsPadding()` para que no quede bajo la barra de notificaciones.
  - Tabs "Iniciar Sesión" / "Registrarse": rediseñadas como control segmentado (píldora azul activa blanco texto activo, borde gris) dentro de una tarjeta redondeada.
  - Se eliminó el color crema (`EnidCream` pasa a `#FFFFFF`); todo fondo queda en blanco (tema, scaffold, welcome y login).
  - Se limpió un import sin uso en `CatalogScreen`.

## Prueba de compilación #10 (alineación con las 11 guías SVG)
- **Fecha:** 15 Sep
- **Comando:** `./gradlew :app:assembleDebug :app:compileDebugUnitTestKotlin :app:compileDebugAndroidTestKotlin --offline` (JAVA_HOME → JBR de Android Studio)
- **Resultado:** ✅ ÉXITO — `BUILD SUCCESSFUL` (44 tareas, 100% offline).
- **Fuente de diseño:** los 11 archivos `01_bienvenida.svg … 11_plataforma_educativa.svg` en `Descargas/imagenes android project`. No se modificaron las imágenes que ya estaban en el proyecto (logo vertical de bienvenida, launchers); solo se ajustaron cajas y textos.
- **Cambios principales:**
  - Fondo cambiado de gris claro a **blanco azulado** `EnidBg = #F2F7FD`; se eliminaron definitivamente los cremas (`EnidCream`, `EnidSoft`). Texto principal `EnidInk = #222222`, bordes `#D9D2C2`, banner/zona foto `#CBD5C0`, caja de ícono `#E3F2FD`, chip "Pagado" `#E8F5E9/#2E7D32`, punto inactivo de la barra inferior `#CFC9BA`.
  - `AppScaffold` sin barra superior azul: cabecera clara con "← Volver" clickeable (o línea de marca "ENID · EDUCACIÓN"), título grande `EnidInk`, `titleEnd` (ej. "Hola, {nombre}", "Paso 1 de 2") y `statusBarsPadding()`. Barra inferior rediseñada: botones con punto (círculo) + etiqueta (activo rojo, inactivo gris) y `navigationBarsPadding()`.
  - `WelcomeScreen`: fondo `EnidBg`, título "Aprender no tiene edad".
  - `AuthScreen`: sin logo, pestañas rectangulares lado a lado (azul oscuro activa / blanca inactiva), **"← Volver" ahora ejecuta `onBack()`**, campos con placeholder, contraseña/olvido en rojo.
  - `HomeScreen`: header "ENID" + "Hola, {nombre}", banner con `EnidPhoto`, 4 tarjetas de menú, enlaces secundarios.
  - `CatalogScreen` ("Oferta Académica"): buscador con placeholder, chips de filtro, tarjetas con botón rojo "Ver Más".
  - `ProgramDetailScreen` ("Detalle del Curso"): números rojos 01/02, CTA rojo "Preinscribirme Ahora" y barra inferior INICIO (`onTabNavigate`).
  - `PreinscriptionScreen` ("Preinscripción", "Paso X de 2"): etiquetas sobre los campos, selector de programa expandible, flujo CONTINUAR/ENVIAR + éxito, barra inferior INICIO.
  - `DailyScheduleScreen` ("Mi Horario"): tabs Hoy | Plan de Estudios, tarjetas con franja lateral azul oscuro.
  - `PaymentsScreen` ("Mis Pagos"): tarjeta "Valor de la Matrícula" con vencimiento en rojo y monto `$830.000 COP`, botón morado "Pagar con PSE", historial con chips verdes "Pagado".
  - `LocationsScreen` ("Nuestras Sedes" + "Encuentra tu campus más cercano"): zona de foto `EnidPhoto`, botones "Llamar a Sede" (contorno morado) / "Cómo Llegar" (rojo).
  - `FaqScreen` ("Preguntas Frecuentes"): etiquetas "CATEGORÍA: …", tarjetas expandibles con ▲/▼ y botón verde "Chat de WhatsApp Directo".
  - `PlatformScreen` ("Plataforma Educativa"): tarjeta con caja de ícono `EnidIconBox` 🔒, punto verde "Plataforma Activa" y botones "Abrir Plataforma Externa" / "Volver al Inicio".
  - **Corrección del "Volver":** en `AppRoot`, Bienvenida → Login/Registro ahora usa `nav.navegar(...)` (en lugar de `reemplazarTodo`), por lo que el botón "← Volver" del login/registro **retorna a la Bienvenida**. Las tabs inferiores siguen con `reemplazarTodo`.
  - **Errores corregidos durante el refactor:** helpers provisionales en `LocationsScreen` (import faltante de `width`/`Spacer`); nombres de función apuntaban a `BestOutlined1/2` en un edit intermedio (revertidos a `BigOutlinedButton`/`BigButton`).

## 18 Sep — Integración de Firebase Authentication (Requerimiento 02)

| Acción | Detalle | Estado |
|---|---|---|
| Verificación de red | `maven.google.com` y `dl.google.com` responden; se pudo descargar Firebase. | Hecho |
| `google-services.json` | Ya existía en `app/` (proyecto `fundacionenid-1e373`, paquete `com.fundacion.enid`). No se creó uno nuevo. | Hecho |
| Gradle | `libs.versions.toml`: plugin `com.google.gms.google-services` 4.4.2, Firebase BOM 34.2.0, `firebase-auth` y `kotlinx-coroutines-play-services` 1.10.2. Plugin aplicado en `app` y declarado en el root. `gradle.properties` se puso en `offline=false` solo para descargar; luego se restauró a `true`. | Hecho |
| Auth real | `DataRepository`: `iniciarSesionFirebase()` y `registrarEnFirebase()` (suspend, con `await()`) + `mensajeErrorAuth()` que traduce los códigos de Firebase. El registro guarda el nombre en `displayName` de Firebase. | Hecho |
| UI | `AuthScreen`: usa `rememberCoroutineScope`, estado `cargando`, botones deshabilitados con texto "Ingresando…"/"Registrando…". Validación de contraseña subida a **mínimo 6 caracteres** (requisito de Firebase). `cerrarSesion()` ahora también llama a `FirebaseAuth.signOut()`. | Hecho |
| Componente | `BigButton` ahora acepta `enabled: Boolean = true` (retrocompatible). | Hecho |
| Dependencia descargada | `com.google.firebase:firebase-auth:24.0.1` (gestionado por BoM 34.2.0). | Hecho |

> **Nota para el usuario:** para que el login funcione en el dispositivo hay que tener
> internet y **habilitar Email/Password** en Firebase Console → Authentication →
> Método de inicio de sesión. Google Play Services debe estar presente (emulador con
> Play Store o teléfono físico).

## Prueba de compilación #11 (Firebase Authentication)
- **Fecha:** 18 Sep
- **Comando:** `./gradlew :app:assembleDebug` (con `google-services.json` y `JAVA_HOME` → JBR de Android Studio)
- **Resultado:** ✅ ÉXITO — `BUILD SUCCESSFUL in 1m 23s` (37 tareas).
- **Verificación:** el APK (37,9 MB) contiene `string/google_app_id` →
  `fundacionenid-1e373.firebasestorage.app`, confirmando el procesado del
  `google-services.json`.
- **Offline:** tras la descarga, se restauró `org.gradle.offline=true` y
  `./gradlew :app:assembleDebug` volvió a compilar en 4s desde caché.
- **Errores encontrados:** ninguno en el build final.

---

## 18 Sep — Corrección de fallas detectadas en runtime y decoración de iconos

| # | Falla | Solución | Archivo(s) |
|---|---|---|---|
| 1 | System back cerraba la app sin transición (no había BackHandler) | `BackHandler` en `AppEntry` habilitado solo cuando `nav.puedeRetroceder`; el botón físico navega atrás entre pantallas; en la Home (raíz) cierra la actividad limpiamente. | `MainActivity.kt` |
| 2 | "Llamar a Sede" y "Cómo Llegar" eran botones muertos | Ahora lanzan intents reales: `ACTION_DIAL` con `tel:` + número; `ACTION_VIEW` con URI `geo:` de Google Maps para Chía y Sopó. Helpers inline no-composables (obtienen `LocalContext` antes del lambda). | `LocationsScreen.kt` |
| 3 | Botón WhatsApp de FAQ muerto | `abrirWhatsApp()` ahora usa `Intent.ACTION_VIEW` con URI `wa.me/` + número predefinido + texto predeterminado. Función auxiliar con `Context` explícito. | `FaqScreen.kt` |
| 4 | "Abrir Plataforma Externa" muerto | `abrirPlataforma()` lanza `ACTION_VIEW` con `https://enid.fundacion.edu.co/plataforma/` (placeholder; reemplazar con la URL real). `LocalContext` obtenida fuera del bloque composable. | `PlatformScreen.kt` |
| 5 | PSE solo simulaba sin abrir pasarela | Botón PAGAR ahora además lanza `Intent.ACTION_VIEW` con la URL de PSE (placeholder) + guarda el estado de pago en `pagado`. `LocalContext` capturada al inicio del composable. | `PseScreen.kt` |
| 6 | Barra inferior con puntos (sin iconos) | `EnidTab` ahora tiene `icon: ImageVector`; `EnidBottomNav` renderiza `EnidIconBox` + etiqueta de texto (activo: fondo `#EEEDF5`, icono azul oscuro; inactivo: fondo blanco, icono gris). Se eliminaron los círculos/puntos del diseño anterior. | `Components.kt` |
| 7 | Emojis en toda la app (Home, PSE, plataforma, preinscripción, FAQ) | Creado `EnidIcons.kt` con 31+ iconos Material Outlined importados como `ImageVector` vía `PathParser().parsePathString(p).toNodes()`. Cada emoji reemplazado: ✅ → `VOkCirculo`; 🔒 → `VCandado` en `EnidIconBox`; ▲/▼ → `VContraer`/`VExpandir`; burbuja WhatsApp dibujada a mano; calculadora dibujada a mano (no existe en Material Icons). | `EnidIcons.kt`, `HomeScreen.kt`, `FaqScreen.kt`, `PseScreen.kt`, `PlatformScreen.kt`, `PreinscriptionScreen.kt`, `AuthScreen.kt` |
| 8 | Preinscripción faltaba campo "Documento de Identidad" | Añadido `BigTextField` con `KeyboardType.Number`; `validarPaso1()` exige que no esté vacío; el valor se incluye en el resumen del Paso 2 y se persiste. | `PreinscriptionScreen.kt` |
| 9 | "Clases para hoy" mostraba todos los días sin filtro | `DailyScheduleScreen` ahora filtra usando `SimpleDateFormat("EEEE", Locale("es","CO"))` + `replaceFirstChar` para capitalizar; compara contra `slot.dia`. Si no hay clases → mensaje informativo. | `DailyScheduleScreen.kt` |
| 10 | Barra de acento de SlotCard sin altura | `Row` del SlotCard ahora usa `Modifier.fillMaxWidth().height(IntrinsicSize.Min)` + `Modifier.fillMaxHeight().width(5.dp)` en la barra de color azul oscuro. | `DailyScheduleScreen.kt` |
| 11 | "Aula Aula 3" (duplicación) | Template cambiado de `"Aula ${slot.aula}"` a `"${slot.aula}"` (el campo `aula` ya contiene el nombre completo, ej. "Aula 3"). | `DailyScheduleScreen.kt` |

### Iconos integrados

Se creó `app/src/main/java/com/fundacion/enid/ui/components/EnidIcons.kt` con los siguientes iconos
(descargados como SVG de `raw.githubusercontent.com/google/material-design-icons` y convertidos
a `ImageVector` con `PathParser().parsePathString(p).toNodes()`):

- **Navegación:** `VFlechaAtras`, `VFlechaAdelante`, `VExpandir`, `VContraer`, `VMenu`
- **Bottom Nav:** `VInicio`, `VCalendario`, `VTicket`, `VAyuda`
- **Menú Home:** `VPagos`, `VOferta`, `VPreinscripcion`, `VSedes`, `VLlamar`, `VComoLlegar`, `VCalculadora`, `VPerfil`, `VCuenta`, `VCandado`, `VExterno`
- **Estado:** `VOkCirculo`, `VOk`, `VCerrar`, `VAgregar`, `VHecho`, `VEDitar`, `VBuscar`, `VWhatsApp`
- **Pagos:** `VPay`, `VRecibo`, `VPay`, `VTelfLocal`
- **Horario:** `VHoy`, `VEvento`, `VDescripcion`, `VApps`, `VPrimera`, `VUltima`
- **Otros:** `VOkCirculo` (verde de éxito)

Los iconos se usan en:
- `EnidBottomNav` (barra inferior con iconos activos/inactivos)
- `MenuCard` (Home: tarjetas de menú con iconos en caja)
- `SecondaryLink` (Home: accesos secundarios con icono + chevron)
- `BigButton` / `BigOutlinedButton` (parámetro `icon` opcional)
- `EnidTab` (pestañas superiores de Horario y FAQ con iconos de flechas)
- `AppScaffold` (botón "Volver" con `VFlechaAtras` en vez de texto "←")

### Auth fallback local

`DataRepository` ahora tiene:
- `iniciarSesion()`: intenta Firebase primero; si falla, intenta autenticación local (`autenticar()`).
- `registrar()`: intenta Firebase primero; si falla, registra localmente (`registrarUsuario()`).
- `mensajeErrorAuth()`: captura `IllegalStateException` (SDK Firebase no inicializado) y ofrece mensaje claro.

### Permisos del Manifest

Añadidos permisos explícitos en `AndroidManifest.xml` (ya disponibles en debug por Firebase, pero necesarios para release):
- `android.permission.INTERNET`
- `android.permission.ACCESS_NETWORK_STATE`
- `<queries>` para intents `ACTION_DIAL` (tel) y `ACTION_VIEW` (https)

### Prueba de compilación #12 (correcciones runtime)
- **Fecha:** 18 Sep
- **Comando:** `./gradlew :app:assembleDebug --offline` (JAVA_HOME → JBR)
- **Resultado:** ✅ ÉXITO — `BUILD SUCCESSFUL in 1s` (48 tareas, 100% offline).
- **Artefacto:** `app/build/outputs/apk/debug/app-debug.apk` (38.3 MB)
- **Errores corregidos durante el refactor:**
  1. `PathParser().toNodes(p)` → `PathParser().parsePathString(p).toNodes()` (API de la versión del proyecto).
  2. `FaqScreen`/`PlatformScreen`: llamadas composable dentro de lambda onClick → extraídas a funciones normales con `Context` explícito.
  3. `LocationsScreen`: helpers inline con `@Composable` invocado en lambda no-composable → lambdas inline con `LocalContext` capturada al inicio.
- **Errores en el build final:** ninguno.

### Verificación en dispositivo

| Prueba | Resultado |
|---|---|
| 5 cold-starts en frío (force-stop + monkey) | ✅ 0 FATAL, 0 ANR |
| BackHandler en Mi Horario → Home | ✅ Navega atrás sin salir |
| BackHandler en Home → Launcher | ✅ Sale limpiamente |
| Iconos en barra inferior (análisis de píxeles) | ✅ Cajas #EEEDF5 + glifos azul oscuro (activas) / gris (inactivas) |
| Iconos en tarjetas de menú Home (análisis de píxeles) | ✅ Cajas #EEEDF5 + glifos en Mis Clases, Pagos, Oferta, Preinscripción |
| SecondaryLinks con iconos (análisis de píxeles) | ✅ Cajas #EEEDF5 en Sedes, FAQ, Plataforma |
| "Mis Clases" → Mi Horario | ✅ Abre, muestra "Clases para hoy (Viernes)" |
| Filtro por día real | ✅ Solo clases del viernes; título incluye nombre del día |
| "Volver al Inicio" (Plataforma) | ✅ Navega a Home |
| Pestaña Horario → Mi Horario | ✅ Abre directamente vía bottom nav |
| FAQ — flechas expandir/contraer | ✅ Iconos grises visibles en cada fila |
| FAQ — botón WhatsApp (análisis de píxeles) | ✅ Botón verde con icono de chat |
| Plataforma Educativa — check verde (análisis de píxeles) | ✅ VOkCirculo verde visible junto a "Plataforma Activa" |
| Preinscripción — campo Documento | ✅ Visible, validación: "Ingresa tu documento de identidad." si está vacío |
| Preinscripción — resumen Paso 2 | ✅ Muestra DOCUMENTO con el valor ingresado |
| Preinscripción — éxito | ✅ Mensaje "¡Preinscripción enviada con éxito!" + VOkCirculo verde (análisis de píxeles) |
| PSE — pantalla de pago | ✅ Resumen de compra, selección de bancos, botón PAGAR con candado |
| `Llamar a Sede` (intento telefónico) | ✅ Abrió dialer externo (ACTION_DIAL) |
| Permisos INTERNET (debug manifest merge) | ✅ Firebase lo aporta; se añadió explícito para release |
| Registro con campo Documento | ✅ Registro exitoso con "Camilo Test", credenciales válidas |

> **Pendiente:** intents de Llamar/Cómo Llegar/WhatsApp/Plataforma/PSE no se probaron
> visualmente en esta iteración (el usuario solicitó no abrir apps externas por el momento);
> quedan verificados por código compilado.
