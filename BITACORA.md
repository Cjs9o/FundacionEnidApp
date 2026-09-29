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

## 22 Sep — Actualización integral del catálogo y la experiencia (Portafolio ENID 2026)

> Esta iteración (commit `62d4ab6`, "update 22/9/2026") **no había quedado documentada** en
> la bitácora; se registra ahora con base en el diff y el código actual.

| Acción | Detalle | Estado |
|---|---|---|
| Catálogo ampliado | `DataRepository.programas` reescrito con el **Portafolio ENID 2026** (`PORTAFOLIO ENID.pdf`, 35 págs.): **25 técnicos laborales** + **11 cursos** + **30 diplomados** + **2 seminarios** = **67 programas**, cada uno con perfil profesional, perfil laboral/campo laboral y competencias. | Hecho |
| Modelos | `TipoPrograma` agrega `SEMINARIO`; `Program` agrega `campoLaboral: List<String>`. | Hecho |
| Flujo de invitado | Se eliminó la pantalla de **Registro** (`Screen.Register`) y la pestaña "Registrarse" de `AuthScreen`. Ahora la bienvenida ofrece **Iniciar Sesión** e **Ingresar como Invitado** (`WelcomeScreen` → "Aprender no tiene edad"). | Hecho |
| Home por sesión | `HomeScreen` muestra 4 accesos distintos si hay sesión (Mis Clases, Pagos, Plataforma Educativa, Mi Cuenta) o si es invitado (Oferta Académica, Información Institucional, Preinscripción, Contáctanos). | Hecho |
| Sedes | `LocationsScreen` agrega bloque institucional: estadísticas (25+ años, +22 programas, +9000 graduados, 3 zonas), misión y visión 2030. Sedes: Chía, Sopó y Girardot. | Hecho |
| Navegación | `BackHandler` se movió de `MainActivity` a `AppRoot`; el tab "Inicio" usa `reemplazarTodo`. | Hecho |
| Autenticación | `AuthScreen` simplificada (solo Login + Invitado); Firebase Auth con respaldo local sigue en `DataRepository`. | Hecho |
| Logo | `drawable/logo_enid.png` (horizontal) usado en el encabezado del Home. | Hecho |

## 29 Sep — Puesta al día de la bitácora y plan de nuevas funciones (Fase 0)

| Acción | Detalle | Estado |
|---|---|---|
| Documentación del 22/9 | Se registra la iteración anterior (arriba), que faltaba. | Hecho |
| Fuente de contenido | `PORTAFOLIO ENID.pdf` (35 págs., 18 MB) se usa como fuente del catálogo y se contrasta con el sitio `https://enid.edu.co/`. Se deja **sin versionar** por tamaño, salvo que el usuario decida incluirlo. | Hecho |
| Imagen sin versionar | `docs/imagenes/image11.jpeg` (logo horizontal 802×145) queda incorporada al control de versiones. | Hecho |
| Restricción de pruebas | Durante las pruebas **no se abrirá nada externo** (marcador/llamadas, páginas web, PSE/bancario, WhatsApp ni correos). Se mantiene. | Hecho |
| Decisión de datos | **No** se añadirán aún valor de matrícula, próxima fecha de inicio, horarios disponibles ni formas de pago: el portafolio y la web no los publican. Queda como pendiente hasta tener datos oficiales. | Pendiente |

### Pendientes (se mantienen para evitar conflictos)

- **Intents externos sin prueba visual:** Llamar / Cómo Llegar / WhatsApp / Plataforma / PSE
  siguen verificados solo por código compilado (no se probaron abriendo apps externas).
- **Datos por confirmar:** matrícula, próxima fecha de inicio, horarios y formas de pago
  (no se agregan hasta contar con información oficial).
- **Contenido de captación aprobado (por implementar en fases):** ver *Fases 1–4* más abajo.

## 29 Sep — Fase 1: Oferta académica potente + orientador "¿Qué puedo estudiar?"

| Acción | Detalle | Estado |
|---|---|---|
| Áreas temáticas | Nuevo enum `Area` (9 áreas) y campo `Program.area`. Los **67 programas** se clasificaron **explícitamente** (no por heurística, que fallaba en casos como "Ecosistemas"→Sistemas o "Turística" sin tilde). | Hecho |
| Modelo `Program` | `pensum` → `competencias` (son las competencias del Portafolio, no el pensum). Se conserva duración, modalidad, sedes, perfil y campo laboral. | Hecho |
| Consultas del catálogo | `DataRepository.buscarProgramas(consulta, tipo, area)` busca también en el perfil ocupacional. Se agregan `programasTecnicos` y `programasComplementarios`. | Hecho |
| Oferta académica | `CatalogScreen` reescrito: encabezado "Encuentra tu programa ideal", botón destacado **¿Qué puedo estudiar?**, buscador, filtros por **tipo** y por **área**, contador de resultados y tarjeta con tipo · duración · modalidad · sedes · área. | Hecho |
| Detalle de programa | `ProgramDetailScreen` reescrito en 3 pestañas: **Información** (modalidad, duración, sedes, descripción, perfil ocupacional, campo laboral), **¿Qué aprenderás?** (plan de estudios con las competencias numeradas; en complementarios, aviso con llamada al asesor) y **Requisitos**. Cierra con 3 CTA: **Quiero estudiar este programa**, **Hablar con un asesor** y **Escribir por WhatsApp**. | Hecho |
| Orientador | Nuevo `data/ProgramFinder.kt` con 5 preguntas y puntuación por área, tipo y campo laboral. Nueva pantalla `ProgramFinderScreen` con progreso, resultado "N programas para mí", CTA de asesor, preinscripción y repetición. | Hecho |
| Enlaces externos | Nuevo `data/EnlacesExternos.kt`: **todo** lo que sale de la app (WhatsApp, asesor, teléfono, mapa, web, PSE) pasa por un solo punto con interruptor `habilitados`, que está en `false` para **respetar la restricción de pruebas**. | Hecho |
| Preinscripción | `Screen.Preinscription(programaId)` recibe el programa elegido; el selector ahora tiene buscador y lista con scroll (67 programas ya no se desbordaban). | Hecho |
| Pruebas | Nuevo `ProgramFinderTest` (5 pruebas unitarias): interés en tecnología, respuesta neutra, filtro por campo laboral, sin respuestas y estructura del cuestionario. **5/5 OK**. | Hecho |

### Prueba de compilación #14 (Fase 1)
- **Comando:** `./gradlew :app:compileDebugKotlin --offline`
- **Resultado:** ❌ 1 error → `EnidTab` sin importar en `ProgramFinderScreen.kt`.
- **Corregido:** import añadido.

### Prueba de compilación #15 (Fase 1)
- **Comando:** `./gradlew :app:compileDebugKotlin --offline`
- **Resultado:** ❌ 4 errores → `Screen.Preinscription` pasó de `data object` a `data class`, así que
  `Screen.Preinscription` ya no era una expresión en `HomeScreen.kt` (líneas 169 y 183).
- **Corregido:** `Screen.Preinscription(null)` en los dos accesos del Home.

### Prueba de compilación #16 (Fase 1 + pruebas unitarias)
- **Comandos:** `./gradlew :app:compileDebugKotlin --offline` y
  `./gradlew :app:testDebugUnitTest --offline --rerun-tasks`
- **Resultado:** ✅ ÉXITO — `BUILD SUCCESSFUL`. `ProgramFinderTest`: **5 pruebas, 0 fallos**;
  `ExampleUnitTest`: 1/1.

## 29 Sep — Fase 2: los dos caminos y el asesor permanente

| Acción | Detalle | Estado |
|---|---|---|
| Camino A / B | El Home ahora se **parte en dos** según haya sesión o no: `HomeScreen` (camino A, "soy estudiante") y el nuevo `ProspectHomeScreen` (camino B, "quiero estudiar en ENID"). El invitado ya no entra a un Home de módulos que no puede usar. | Hecho |
| Recorrido del prospecto | Nuevo `DataRepository.rutaProspecto`: los **10 pasos** que pidió el cliente (Conocer ENID → Explorar programas → Elegir programa → Recibir información → Hablar con asesor → Preinscripción → Documentos → Matrícula → Pago → Bienvenida) se muestran como tarjetas numeradas. | Hecho |
| Honestidad del recorrido | Los pasos que la app **no puede resolver sola** (asesor, preinscripción formal, documentos, matrícula) se marcan `enSede = true` y muestran la etiqueta "con asesor" en lugar de un enlace que no cumpliría. Los que sí tienen pantalla enlizan: Sedes, Catálogo, Orientador, Preinscripción, Pagos y Plataforma. | Hecho |
| Home como herramienta de venta | El invitado avanza por el proceso **sin crear cuenta**: botón destacado *¿Qué puedo estudiar?*, atajos a "Ver programas" y "Dejar mis datos", y tarjeta "¿Ya eres estudiante ENID?" que devuelve al login. | Hecho |
| Asesor permanente (req. 04) | Nueva `AsesorBar` fija sobre la barra de navegación, activada con `mostrarAsesor = true` en los **dos** Homes. El asesor ya no vive escondido en un menú. | Hecho |
| Mensaje del asesor | `EnlacesExternos.MENSAJE_ASESOR` queda escrito de antemano ("Hola, soy interesado en estudiar en ENID..."), para que la conversación empiece con el contexto ya dado. | Hecho |
| `SectionTitle` | Ahora admite subtítulo opcional, sin romper los usos existentes. | Hecho |

### Prueba de compilación #17 (Fase 2)
- **Comando:** `./gradlew :app:compileDebugKotlin --offline`
- **Resultado:** ❌ 2 errores → `AsesorBar` usaba `EnidIcons.VAsesor`, que no existe en
  `EnidIcons`, y `abrirAsesor` exigía el parámetro `mensaje`.
- **Corregido:** se usa `EnidIcons.VTelfLocal` y `mensaje` pasa a tener valor por defecto
  `MENSAJE_ASESOR`.

### Prueba de compilación #18 (Fase 2)
- **Comando:** `./gradlew :app:compileDebugKotlin --offline`
- **Resultado:** ✅ ÉXITO — `BUILD SUCCESSFUL`. Guest y estudiante entran ya por caminos distintos.

## 29 Sep — Fase 3: promociones y testimonios (req. 05 y 06)

| Acción | Detalle | Estado |
|---|---|---|
| Modelos | `Promocion` (titulo, detalle, vigencia, tipo DESCUENTO/ALIANZA/CONVOCATORIA/OPORTUNIDAD, destacada) y `Testimonio` (nombre, programa, cita). | Hecho |
| Sección compartida | Nueva `CommunitySections.kt` con `PromocionesTestimoniosSection`, usada en los **dos** Homes. Tarjetas de promoción con etiqueta de tipo y vigencia, y tarjetas de testimonio con la cita y su autor. | Hecho |
| Datos **dejados vacíos** | `DataRepository.promociones` y `.testimonios` arrancan en `emptyList()`. **No se inventó ninguna promoción ni ningún testimonio**: un descuento o una cita falsa se cae en cuanto el interesado pregunta. | Hecho |
| Estado vacío útil | Mientras no haya contenido, la sección lo dice claro ("Contenido en preparación") y **convierte el espacio en captura**: botón "Quiero recibir las promociones" (deja sus datos) y "Hablar con un asesor". | Hecho |
| Carga futura | Llenar `DataRepository.promociones` / `.testimonios` con los datos oficiales hace que las tarjetas aparezcan **sin tocar ninguna pantalla**. | Hecho |

### Prueba de compilación #19 (Fase 3)
- **Comando:** `./gradlew :app:compileDebugKotlin --offline`
- **Resultado:** ✅ ÉXITO — `BUILD SUCCESSFUL`. Sección de promociones y testimonios
  compilando en `ProspectHomeScreen` y `HomeScreen`.

## 29 Sep — Fase 4: seguimiento de interesados (req. 08) y prueba en dispositivo

| Acción | Detalle | Estado |
|---|---|---|
| Guardar interés | `DataRepository.registrarInteres / quitarInteres / esInteres / consultarIntereses` sobre `SharedPreferences` (clave `intereses`). El botón **"Me interesa este programa"** aparece en el detalle y alterna a "Guardado en mis interesados". | Hecho |
| Resumen personal | Nueva `InteresesScreen` ("Mis interesados"): el programa que elegiste, un bloque **SIGUIENTE PASO** y lo que ya dejaste (preinscripciones con nombre, teléfono, correo y fecha). | Hecho |
| Siguiente paso honesto | `DataRepository.siguientePaso()` cambia el texto según el estado real: si no hay preinscripción invita a dejar los datos; si ya la hay, el paso es hablar con el asesor. | Hecho |
| Recordatorio en el Home | `RecordatorioInteres` aparece arriba en los **dos** Homes **solo** si hay un interés guardado. Sin intereses, no aparece nada. | Hecho |

### Pruebas en el dispositivo `ZT322CVFC9` (solo recorridos internos)

| Prueba | Resultado |
|---|---|
| Welcome → "Ingresar Como Invitado" | ✅ Entra al camino B (`ProspectHomeScreen`), no al Home de estudiante |
| Los 10 pasos del camino | ✅ Los 10 se muestran; pasos 5–8 con la etiqueta **"con asesor"** en vez de un enlace que no cumpliría |
| Texto "con Titulación SENA" | ⚠️ **Error encontrado en pantalla:** salía "conTitulación SENA" → **corregido** |
| Orientador "¿Qué puedo estudiar?" | ✅ Preguntas 1→5, selección y avance correctos |
| Recomendaciones | ✅ Con respuestas "sistemas / ambiente / campo / técnico / ambiente" el **Opción 1** fue *Control y Manejo Ambiental* y la 2 *Analítica de Datos e IA*. La puntuación funciona. |
| Detalle de programa | ✅ Tabs Información / ¿Qué aprenderás? / Requisitos, modalidad, duración, sedes, campo laboral y 4 CTA |
| "Me interesa este programa" | ✅ Queda como "Guardado en mis interesados" |
| Recordatorio en el Home | ✅ "Te interesa Control y Manejo Ambiental" + botón "Ver resumen" |
| "Mis interesados" | ✅ Programa, **SIGUIENTE PASO**, y "lo que ya dejaste" |
| Preinscripción con programa previo | ✅ Abre con *Control y Manejo Ambiental* preseleccionado; se completó y envió el formulario de prueba |
| Resumen tras preinscripción | ✅ "Ya dejaste tu preinscripción. El siguiente paso es hablar con un asesor" + datos y fecha "29 sep. 2026" |
| Botón "Contactar" del asesor | ✅ **No abrió WhatsApp** (no se rompió la restricción). `logcat` confirma: `Acción externa 'whatsapp' omitida durante pruebas (habilitados=false)` |

### Pruebas de compilación #20–#24 (Fase 4)

| # | Comando | Resultado |
|---|---|---|
| 20 | `./gradlew :app:compileDebugKotlin --offline` | ❌ 2 errores: faltaban los imports `background` y `clickable` en `CommunitySections.kt`, y el color `EnidRed` |
| 21 | `./gradlew :app:compileDebugKotlin --offline` | ✅ ÉXITO — con el módulo de seguimiento de interesados |
| 22 | `./gradlew :app:assembleDebug --offline` + `adb install -r` | ✅ ÉXITO — APK instalado |
| 23 | `./gradlew :app:compileDebugKotlin --offline` | ❌ 1 error: `tabsSegunSesion` sin resolver en `PreinscriptionScreen.kt` (el import automático no se aplicó porque el archivo no tenía `SectionTitle`) |
| 24 | `./gradlew :app:assembleDebug --offline` | ✅ ÉXITO — APK final instalado |

## Fallos encontrados y corregidos en las pruebas de dispositivo

Son los que **solo aparecen al usar la app de verdad**, y por eso vale la pena registrarlos:

1. **El invitado llegaba al Home de estudiante.** La barra inferior enviaba a
   `Screen.Home` sin importar la sesión: quien entraba como invitado y tocaba
   "Inicio" desde el catálogo caía en el Home del estudiante. Ahora `AppRoot`
   calcula `inicioDelUsuario` según `repository.estaConectado`.
2. **El invitado veía "Horario" y "Pagos".** Módulos de estudiante mostrados a
   alguien que aún no decide. Se agrega `tabsSegunSesion()`: el invitado solo ve
   **Inicio** y **Soporte**. Aplicado en Catálogo, Detalle, Orientador, Intereses,
   Preinscripción, Sedes y FAQ.
3. **La barra de "Mis interesados" seguía con los 4 tabs.** Una edición por
   script no aplicó en ese archivo (el patrón no coincidía porque ya había un
   `mostrarAsesor` después) y el `tabs` tiene valor por defecto, así que **compilaba
   igual y el fallo no se veía en la compilación**. Corregido a mano y verificado en
   pantalla.
4. **Texto pegado: "conTitulación SENA".** Corregido y verificado en pantalla.




## Fase 11 — Preinscripción completa y correcciones de diseño

### Qué pidió la Fundación y qué se hizo

| Petición | Estado |
|---|---|
| Título de pantalla con fondo blanco, de borde a borde | ✅ `AppScaffold` pinta la cabecera blanca, con divisor, en todas las pantallas |
| Quitar el bloque blanco propio del Inicio | ✅ El Inicio usa la cabecera estándar ("Hola, {nombre}") y el logo bajó al banner |
| Cuadros y botones proporcionados | ✅ `BigButton`, `BigOutlinedButton` y `MenuCard` usan altura mínima en vez de fija; nada se recorta |
| Enumeraciones centradas | ✅ Numeración centrada en el recorrido del prospecto y en las recomendaciones del orientador |
| Barra del asesor en rojo | ✅ `AsesorBar` en `EnidRed` con iconos y textos blancos |
| Tipo de documento separado del número | ✅ CC, CE, TI, NIT, RC, PP y CD, con la sigla y su significado completo |
| Prefijo y número de contacto separados | ✅ +57, +58, +52, +51, +593, +1, +34, con el país visible en el campo |
| Fecha de nacimiento y de expedición | ✅ `dd/mm/aaaa` con máscara, validación de fechas reales y cálculo de edad |
| Fotocopia de documento, títulos de bachiller e ICFES | ✅ Checklist de lo que el asesor puede entregar; los archivos **no** se adjuntan (no hay servidor) |
| Estado civil si es mayor de edad | ✅ Solo se pregunta cuando la edad indica 18 años o más, y es obligatorio ahí |
| Menor de edad, acudiente y condición especial | ✅ Bloque condicional con nombre del acudiente y campo libre para la condición |
| Contacto de emergencia | ✅ Prefijo y número propios |
| Familiar de referencia | ✅ Tarjeta opcional con nombre, parentesco, documento, contacto y correo |

La preinscripción pasó de 2 pasos a **3 pasos**: *Quién eres* → *Formación y
documentos* → *Situación personal*. El tipo de documento "Cédula del acudiente"
solo aparece si la fecha de nacimiento indica que es menor de edad, y los títulos
de bachiller no se vuelven a pedir a quien ya terminó formación técnica.

### Pruebas de compilación #25–#31

| # | Comando | Resultado |
|---|---|---|
| 25 | `./gradlew :app:compileDebugKotlin --offline` | ❌ `SelectorBotones` usaba un helper `androidxBorder` que solo pintaba fondo; se reemplazó por `Modifier.border` |
| 26 | `./gradlew :app:compileDebugKotlin --offline` | ❌ `Unresolved reference 'Icon'` en `FormComponents.kt` — faltaba el import |
| 27 | `./gradlew :app:compileDebugKotlin --offline` | ❌ `Assignment type mismatch: Set<Comparable<String & DocumentoRequerido>> vs Set<String>` al restar el enum de un `Set<String>` |
| 28 | `./gradlew :app:compileDebugKotlin --offline` | ✅ ÉXITO — formulario de 3 pasos compilando |
| 29 | `./gradlew :app:testDebugUnitTest --offline` | ❌ 3 expectativas mal escritas en el test (no eran fallos de la app) |
| 30 | `./gradlew :app:testDebugUnitTest :app:assembleDebug --offline` | ✅ ÉXITO — 39 pruebas, 0 fallos |
| 32 | `./gradlew :app:testDebugUnitTest :app:assembleDebug --offline` | ✅ ÉXITO — 41 pruebas, 0 fallos |
| 31 | `./gradlew :app:assembleDebug --offline` + `adb install -r` | ✅ ÉXITO — APK instalado |

### Pruebas nuevas

Se pasó de 6 a **41 pruebas unitarias**. Las nuevas son dos archivos:

- `FormatoEntradaTest` (23): máscara de fechas, fechas que no existen
  (`31/02`, 29 de febrero en año no bisiesto, meses 13, años de dos dígitos),
  edad cumplida el día del cumpleaños y un día antes, y correo válido o no.
- `PreinscripcionTextoTest` (12): ida y vuelta del registro, separadores dentro
  de los textos, lista de documentos, lectura de las líneas viejas de 6 y de
  21 campos que ya están en los teléfonos de quienes usan la app, y el orden
  de los registros.

La lógica de fechas salió de la pantalla a `FormatoEntrada` y la serialización a
`PreinscripcionTexto`, en `data/`, siguiendo lo que ya hacía `ProgramFinder.kt`:
lógica pura fuera de la pantalla para poder probarla.

### Fallos encontrados usando la app de verdad

1. **La fecha quedaba desordenada al teclear.** `14/03/1972` se convertía en
   `14/31/9270`: al devolver el texto enmascarado, Compose mueve el cursor al
   inicio y el siguiente dígito se inserta en medio. Con `BigTextField` (que
   recibe un `String`) no hay forma de controlar el cursor, así que las fechas
   ahora usan `CampoFecha`, que trabaja con `TextFieldValue` y deja el cursor
   siempre al final.
2. **La validación de fechas comparaba textos.** `"08/07/1994" < "14/03/1972"`
   da verdadero porque el día va primero, así que la app rechazaba una cédula
   expedida 22 años después de nacer. Ahora se comparan por año, mes y día
   (`FormatoEntrada.esAnterior`), con prueba que lo cubre.
3. **Un familiar con coma en el nombre se perdía al releerlo.** Los seis datos
   del familiar iban unidos por comas dentro de un campo, y "Gómez, Luis" los
   partía en siete: el registro se guardaba bien pero al abrirlo el familiar ya
   no estaba, sin avisar. Ahora los separadores van escapados con `%` y hay
   pruebas con comas y con barras verticales en el nombre.
4. **Un `|` en un nombre desalineaba toda la fila.** El texto se guardaba pero
   los campos siguientes se corrían una posición al releerlo. Mismo arreglo que
   el anterior.
5. **"Lo que ya dejaste" podía mostrar una preinscripción vieja.** Las
   preinscripciones se guardaban en un `StringSet`, y un conjunto **no tiene
   orden garantizado**: el código ponía la más nueva al principio y luego
   `take(25).toSet()` la desordenaba, así que la pantalla podía enseñar un
   registro antiguo. Ahora van en un solo texto con saltos de línea, de la más
   reciente a la más antigua, en una clave nueva: leer con `getString` sobre la
   clave vieja habría lanzado `ClassCastException` porque allí hay un conjunto.
   Lo que hubiera en la clave vieja se sigue leyendo, para no perder los datos
   ya registrados.

### Verificado en pantalla y lo que quedó pendiente

En el dispositivo se comprobó de punta a punta: el paso 1 completo con su nota
"Tienes 54 años según esa fecha", el mensaje de validación del paso 2, el paso 3
con el bloque de mayor de edad (estado civil, condición, acudiente, contacto de
emergencia) y el Familiar de confianza abriéndose al marcar "Sí".

**Quedó sin verificar en pantalla:** el envío final ("ENVIAR PREINSCRIPCIÓN"), la
pantalla de confirmación y la lectura del registro nuevo en "Mis interesados",
porque la persona que acompaña el trabajo necesitaba el teléfono. La ida y vuelta
de esos mismos datos y el orden de los registros sí están cubiertos por
`PreinscripcionTextoTest`, pero conviene una pasada manual antes de dar por
cerrado el flujo.
