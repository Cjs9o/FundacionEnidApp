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

Después, con el teléfono disponible, se cerraron los dos puntos que quedaban:

**1. El envío, la confirmación y la lectura del registro.** Se envió una
preinscripción completa (Ana Gomez, adulta) y se comprobó lo que ocurre al
guardar, leyendo las preferencias del dispositivo:

- Se creó la clave nueva `preinscripciones_ordenadas` con el registro nuevo
  primero, y la clave vieja `preinscripciones` **desapareció**: el registro que
  había de una versión anterior no se perdió, quedó migrado abajo en la misma
  lista. Eso confirma, en un dispositivo real y no solo en las pruebas, que se
  leen los dos formatos y que el nuevo no rompe el anterior.
- "Mis interesados" mostró "Ana Gomez · +57 3124567890 · ana.gomez@ejemplo.com"
  en la tarjeta de "Lo que ya dejaste", es decir, la más reciente primero.
- El familiar se registró con una coma en el nombre a propósito
  ("Luis Perez, Hermano"): en el archivo quedó como `Luis%2C Hermano`. Esa era
  justamente la parte que antes se perdía en silencio.
- La pantalla de confirmación aparece con "¡Preinscripción enviada!".

**2. Un fallo de paso 3 que solo se veía con una menor de edad.** Con fecha de
nacimiento de menor (14 años), la primera pregunta del paso 3 era "Estado civil ·
Solo para mayores de edad". La causa: `esMenor` empieza en `null` porque hay
quienes tienen 17 años y ya no dependen de un acudiente, y el `else` del bloque
mostraba el de mayores antes de que la persona contestara. Ahora, mientras no
haya respuesta, se muestra el bloque que concuerda con la fecha de nacimiento;
la respuesta sigue siendo obligatoria para poder enviar. Verificado con una
menor de 14 años: aparece "Nombre de tu acudiente" y no el estado civil, y al
guardar el registro el estado civil queda vacío, `esMenorDeEdad` en `true`, el
acudiente "Maria Ramirez" y en los documentos aparece `CEDULA_MENOR`.

También se comprobó en esa pasada que para una menor de edad el paso 2 pide la
cédula del acudiente además de los tres documentos de siempre, y que para un
adulto no la pide.

---

## Fase 12: adjuntar el documento como archivo, campos blancos y diagrama

### Adjuntar archivo en el paso 2

Antes la casilla de cada documento solo decía "puedo aportar esto" y la
fundación recibía la fotocopia por el canal del asesor. Ahora, cuando la casilla
está marcada, debajo aparece:

- Una lista de **formato** (PDF, JPG, PNG) con la misma mecánica que la lista
  del tipo de documento de la cédula: se elige de una lista y no se escribe a
  mano, porque el sistema de archivos decide la extensión y una foto de la
  galería puede no tenerla.
- Un botón **Cargar archivo** que abre el selector del sistema, filtrado por
  esos tres tipos.

Lo que se guarda es la **referencia** (documento, formato, nombre y URI), no el
archivo: copiar el contenido dentro de las preferencias llenaría el
almacenamiento. La URI conserva el permiso de lectura, así que el archivo se
puede volver a abrir después. Sigue sin haber servidor: esto deja el documento
bajo control de la persona, en su propio teléfono.

Al marcar la casilla el bloque de adjunto aparece; al desmarcarla desaparece, y
el archivo adjunto no es obligatorio para enviar (quien no pueda tomar la foto
ahora la sube después con el asesor).

Campo 22 nuevo en el formato de guardado, con el mismo esquema `clave=valor;`
del familiar. Los bloques de adjuntos se unen con coma, y ahí hubo un fallo que
atrapó una prueba: al leer, el campo se desescapaba entero **antes** de
dividirse, con lo que la coma de un nombre de archivo ("titulo%2C2 paginas.pdf")
volvía a ser coma y partía el bloque en dos, perdiendo el segundo adjunto. Ahora
el campo se divide en crudo y el desescape se hace valor por valor.

**Verificado en el dispositivo:** al marcar "Fotocopia del documento de identidad"
aparecen "Formato del archivo", los tres formatos y el botón "Cargar archivo";
al pulsarlo se abre el selector del sistema mostrando PDF y JPG. No se llegó a
completar la selección de un archivo: el picker del sistema no respondió al tap
programático y, para no elegir documentos personales del teléfono, se canceló.
La ida y vuelta de los adjuntos está cubierta por 4 pruebas nuevas.

### Campos de texto con fondo blanco

`BigTextField` y `CampoFecha` se pintan de blanco. Sin eso el `OutlinedTextField`
deja ver el crema del fondo de la pantalla y la caja de texto se confundía con
el resto del formulario.

### Diagrama de navegación

`DIAGRAMA_NAVEGACION.md`: mapa de las 18 rutas y de todas las conexiones entre
pantallas (mermaid + tablas), la barra inferior por tipo de sesión, el flujo
interno de los 3 pasos de la preinscripción y las salidas a apps externas.

### Pruebas

De 41 a 45, todas en verde: 4 nuevas para los adjuntos (ida y vuelta, nombre de
archivo con coma y signos, preinscripción sin adjuntos y línea vieja de 21
campos).

### Pendiente

- El archivo no se sube a ningún sitio: falta definir servidor, cuenta y
  privacidad de los documentos de identidad de menores de edad.
- La selección dentro del selector del sistema conviene probarla a mano una vez.

---

## 1 Oct — Revisión de lectura completa del proyecto (sin cambios de código)

Pasada de lectura sobre todo el árbol del proyecto, sin tocar código ni datos:
41 archivos fuente Kotlin, `AndroidManifest.xml`, los recursos (`values`,
`drawable`, `mipmap`, `xml`), los cuatro archivos de prueba, la configuración de
Gradle y los cuatro documentos (`README.md`, `PLANDESARROLLO.md`,
`DIAGRAMA_NAVEGACION.md` y esta bitácora). También se leyó el diff sin commitear
de la Fase 12 para no duplicar trabajo ya hecho. No se compiló ni se ejecutaron
pruebas: el alcance era solo lectura.

### Estado del repositorio al momento de la revisión

Siete archivos modificados sin commit (los siete de la Fase 12) y dos sin
rastrear: `DIAGRAMA_NAVEGACION.md` y `PORTAFOLIO ENID.pdf`. Ninguna modificación
de esta pasada.

### Lo que está bien y conviene no tocar

- `FormatoEntrada` es la pieza más sólida del proyecto: máscara, validación de
  fechas reales (incluye bisiestos y siglo), edad cumplida y límites. Sus 25
  pruebas de `FormatoEntradaTest` la cubren bien.
- `PreinscripcionTexto` mantiene compatibilidad hacia atrás con la línea de 6
  campos, la de 21 y el familiar antiguo unido por comas. Es la decisión correcta
  para un dispositivo con datos ya escritos.
- El orientador (`ProgramFinder`) tiene 5 preguntas con ≥4 opciones y ordena por
  puntaje estable.

### Adjuntos: lo que quedó a medias

- **El formato elegido no se aplica.** El selector del sistema se lanza siempre
  con los tres MIME (`FormatoArchivo.entries.map { it.mime }`), así que quien
  marca JPG puede terminar guardando un PDF y el registro dirá `JPG`.
- **Desmarcar la casilla no borra el adjunto.** `onDocumento` quita la clave de
  `documentos` pero el adjunto sigue en el mapa `adjuntos`, y al enviar se
  guarda con `adjuntos.values.toList()`. La casilla dice "no" y el archivo viaja.
- **El texto del paso 2 quedó desactualizado.** Sigue diciendo que "la app no
  adjunta archivos: las fotocopias se entregan al asesor por el canal que él
  indique", que es justo lo que la Fase 12 dejó de hacer.
- El formato elegido se guarda en un `remember` dentro de `SelectorFormatoArchivo`,
  que se destruye al salir del paso 2: si no hay archivo aún, al volver aparece
  PDF.
- Detalle menor: sobra una línea en blanco en `Models.kt` tras `DocumentoAdjunto`.

### Acciones externas

`EnlacesExternos.habilitados = false` es correcto mientras no haya enlaces
oficiales, pero el interruptor no se respeta en todas partes:
`LocationsScreen` (llamar y mapa), `PseScreen` (URL del PSE), `PlatformScreen` y
`FaqScreen` lanzan intents directamente. Mientras siga en `false`, esas cuatro
pantallas deben respetar el mismo criterio.

### Datos que necesitan validación institucional

- Montos contraditorios: `$180.000` en el resumen del PSE, `$830.000` en el
  bloque de valores y base de `$180.000` en el total.
- `PseScreen` muestra "Pago procesado" sin que exista confirmación real de pago.
- Catálogo con 67 programas: 42 sin `competencias`, lo que deja tarjetas y
  comparaciones vacías en la ficha.
- Sedes: el catálogo y el texto hablan de Girardot, pero `repository.sedes` solo
  tiene Chía y Sopó.
- Promociones y testimonios vacíos, a la espera de contenido oficial.
- Sin fuente institucional, también a la espera: correos, teléfonos, horarios,
  requisitos del cuestionario y textos del FAQ.

### Sesión de invitado y navegación

- El invitado puede llegar a `Payments` y `Platform`, pantallas pensadas para
  estudiante con tabs fijas; `Platform` además vuelve a `Screen.Home` de
  estudiante.
- `ProspectHomeScreen` no dibuja barra inferior aunque el diagrama le asigne
  "Inicio" en ambos tipos de sesión.
- Al abrir la preinscripción sin `programaId`, queda preseleccionado el primer
  programa del catálogo (Analítica de Datos e IA) sin avisar: parece elegido y
  no lo está.
- Sin uso: `loggedIn` en `MainActivity`, `onBack` en `AuthScreen`, `onAdvisor` en
  `ProgramDetailScreen` y el parámetro `repository` en varias pantallas.

### Seguridad y almacenamiento

- Las contraseñas del modo local quedan en texto plano en `SharedPreferences`
  (el respaldo local de `AuthScreen`).
- Preinscripciones, interesados y usuarios se guardan en `StringSet`, que no
  garantiza el orden: "Mis interesados" y "Mis datos" pueden mostrar una entrada
  al azar.
- El login local exige una contraseña más corta que el registro local, y el
  registro local tiene un máximo de 25 preinscripciones que se descartan en
  silencio.
- `allowBackup="true"` con `backup_rules.xml` y `data_extraction_rules.xml` sin
  excluir nada: los datos personales de los interesados pueden ir a la nube de
  Google sin que esté decidido.

### Accesibilidad

- `SmallButton` mide unos `34.dp` de alto, por debajo del mínimo cómodo para
  dedo de adulto mayor.
- Etiquetas de la barra inferior en `10.sp`.
- `SelectorDesplegable` pinta el borde rojo cuando ya tiene valor, lo que parece
  un error en un campo ya diligenciado.
- Las tarjetas de documentos y de requisitos no exponen `contentDescription` ni
  semántica de estado marcado/no marcado.

### Pruebas

- 45 pruebas declaradas: 25 de formato, 15 de preinscripción y 5 del
  orientador, más 1 instrumentada de plantilla y 2 de plantilla sin valor.
- Debilidad del orientador: `una respuesta neutra devuelve los tecnicos como
  base` da 1 punto a todos los tipos y espera solo técnicos; hoy pasa por el
  orden de la lista, no por la lógica de puntuación.
- Sin pruebas de `DataRepository` (sesión, orden de los `StringSet`, tope de 25)
  ni de la navegación de invitado.

### Pendiente

Nada de lo anterior se corrigió en esta pasada. En orden de urgencia:

1. Corregir el texto del paso 2 que niega el adjunto, y decidir si desmarcar la
   casilla debe borrar también el archivo.
2. Aplicar el formato elegido al filtro del selector, o quitar el formato como
   dato guardado si no se puede garantizar.
3. Cerrar el flujo de pago real o marcarlo como demostración explícita.
4. Unificar los montos y validar con la Fundación el catálogo, las sedes, las
   listas y los textos vacíos.
5. Cerrar o restringir las cuatro pantallas que abren intents con el interruptor
   en `false`.
6. Revisar el flujo de invitado a `Payments` y `Platform` y la barra inferior de
   `ProspectHomeScreen`.
7. Sacar las contraseñas del texto plano y definir qué se excluye del respaldo.
8. Sustituir los `StringSet` por almacenamiento ordenado y avisar cuando se
   llegue al tope de 25.
9. Subir el tamaño de `SmallButton`, las etiquetas de navegación y corregir el
   borde rojo de los desplegables ya diligenciados.
10. Probar a mano la selección dentro del selector del sistema (sigue pendiente
    desde la Fase 12).

### Plan por fases de lo pendiente

Ordenado por dependencia, no por urgencia: primero lo que no depende de nadie,
después lo que espera una respuesta de la Fundación, y al final el cierre.

| Fase | Nombre | Depende de | Esfuerzo |
|---|---|---|---|
| 13 | Adjuntos coherentes de verdad | — | bajo |
| 14 | Pagos y salidas externas | Fase 13 / Fase 16 (montos) | medio |
| 15 | Navegación y sesión de invitado | — | medio |
| 16 | Datos oficiales de la Fundación | Fundación | bajo el código, alto el espera |
| 17 | Seguridad y almacenamiento | — | medio |
| 18 | Accesibilidad | — | bajo |
| 19 | Pruebas y cierre | 13–18 | medio |

Las fases 13, 15, 17 y 18 se pueden hacer en cualquier orden: no se tocan entre
sí. La 16 es la única que bloquea a otra (los montos del PSE), y por eso conviene
enviar la consulta a la Fundación de una sola vez, antes de empezar la 13.

---

## Fase 13 — Adjuntos coherentes de verdad

Continúa la Fase 12, que dejó el camino a medio hacer. Sin dependencias y sin
riesgo: son cuatro correcciones dentro de archivos que ya están modificados y sin
commit, así que la fase se cierra sobre un diff conocido.

### Qué se hace

**1. El texto del paso 2 que contradice la función.** En
`PreinscriptionScreen.kt:675` sigue la nota "La app no adjunta archivos: las
fotocopias se entregan al asesor por el canal que él indique". Es exactamente lo
que la Fase 12 dejó de hacer, y la persona que lee el formulario antes de marcar
la casilla se lleva esa impresión. Se reemplaza por la explicación real: al
marcar la casilla puede adjuntar el archivo desde el teléfono, la copia queda en
el mismo dispositivo, no se sube a ningún servidor y si no puede tomar la foto
ahora la entrega después con el asesor.

**2. Desmarcar la casilla debe borrar el adjunto.** Hoy `onDocumento` (línea 335)
solo quita la clave del conjunto `documentos`; el mapa `adjuntos` queda intacto
y al enviar se guarda con `adjuntos.values.toList()` (línea 286). El resultado es
una preinscripción con el archivo adjunto de un documento que la persona
desmarcó. Se cambia para que quitar la casilla quite también la entrada del mapa
y llame a `releasePersistableUriPermission`, para no dejar concedido un permiso
de lectura sobre un archivo que ya no se referencia.

**3. El formato elegido tiene que valer algo.** En la línea 167 se lanza el
selector con los tres MIME siempre (`FormatoArchivo.entries.map { it.mime }`),
así que quien elige JPG puede terminar guardando un PDF y el registro dirá `JPG`.
Hay dos caminos y la decisión es de producto:

- *Filtrar de verdad*: pasar a `selectorArchivo.launch(arrayOf(formato.mime))` y
  dejar el formato como dato fiable.
- *Quitar el formato del formulario*: mostrar el nombre real del archivo que ya
  trae el sistema y no pedir un dato que no se puede garantir.

La primera es la coherente con la justificación que quedó escrita en la Fase 12
("el sistema decide la extensión"), pero obliga a que el botón "Cargar archivo"
deje de ser único y offererse por formato, o a que el formato elegido por
defecto sea el que manda. Se decide antes de tocar código.

**4. El formato elegido no sobrevive al cambio de paso.**
`SelectorFormatoArchivo` (línea 344) guarda la elección en un `remember` propio
(línea 351), que se destruye al salir del paso 2: al volver, y sin archivo aún,
el botón vuelve a PDF sin avisar. Se sube la elección al estado del paso, junto a
`adjuntos`, para que sobreviva.

**5. Limpieza menor.** Línea en blanco sobrante tras `DocumentoAdjunto` en
`Models.kt`, y el encabezado vacío `── Utilidades de entrada ──` al final de
`PreinscriptionScreen.kt`.

### Archivos

`PreinscriptionScreen.kt`, `FormComponents.kt`, `Models.kt`,
`PreinscripcionTextoTest.kt`.

### Cómo se verifica

- Prueba unitaria nueva: desmarcar tras adjuntar deja `adjuntos` vacío y la
  preinscripción serializada sin el campo 22.
- Prueba unitaria: el formato elegido llega al `DocumentoAdjunto` guardado.
- Prueba unitaria: `documentos` y `adjuntos` no se contradicen (todo adjunto
  tiene su casilla marcada).
- `testDebugUnitTest` en verde (hoy 45).
- En dispositivo: marcar, elegir JPG, confirmar que el selector solo ofrece JPG;
  cambiar de paso y volver, que el formato siga ahí; desmarcar, que el archivo
  desaparezca de la lista y del registro enviado.

---

## Fase 14 — Pagos y salidas externas

Dos mitades: una se puede hacer hoy y la otra espera los montos de la Fase 16.

### Qué se hace, parte 1 (hoy)

**El pago que no es pago.** `PseScreen.kt:141` pone `pagado = true` en el mismo
`onClick` que abre la URL del PSE, y la pantalla muestra "Pago procesado con
éxito" y "Recibirás el recibo en tu correo" (líneas 63 y 69). Nada de eso ocurre:
no hay pasarela, no hay confirmación y no hay recibo. Peor: con
`EnlacesExternos.habilitados = false` ni siquiera se abre la URL, así que el
"pago" se completa sin haber salido de la app. Se cambia por una pantalla que
diga lo que es verdad: que el pago se hace en la pasarela y que la app aún no
recibe la confirmación, con la opción de volver al inicio. El `pagado` se elimina
o se reemplaza por "redirigiendo".

**Cuatro pantallas que ignoran el interruptor.** `EnlacesExternos` existe
justamente para que con `habilitados = false` no se abra ninguna app externa, y
su comentario lo dice. Pero hay intents launching directo en:

- `LocationsScreen.kt:195` (marcador telefónico) y `:209` (mapa de Google).
- `FaqScreen.kt:139` (WhatsApp) y `:147` (sitio web).
- `PseScreen.kt:138` (URL del PSE).
- `PlatformScreen.kt:128` (URL de la plataforma).

Se reemplazan por `EnlacesExternos.abrirLlamar`, `abrirMapa`, `abrirWeb` y
`abrirAsesor`, que además traen el `try/catch` que evita el crash cuando el
dispositivo no tiene la app instalada y el log de la acción omitida. Efecto
colateral útil: mientras el interruptor siga en `false`, las cuatro pantallas
quedan inertes en las pruebas de dispositivo, como ya se pidió.

### Qué se hace, parte 2 (tras la Fase 16)

Los montos están escritos a mano y no coinciden: `$830.000` en el resumen y en
el botón (`PseScreen.kt:96` y `:133`), y `$180.000` en los dos pagos de
`DataRepository.kt:511` y `:518`. Se sacan a una constante única con su
descripción (por programa y por concepto, no un número suelto) y ambas pantallas
leen de ahí, para que la próxima actualización sea un cambio de un sitio y no
una búsqueda de cifras.

### Archivos

`PseScreen.kt`, `LocationsScreen.kt`, `FaqScreen.kt`, `PlatformScreen.kt`,
`DataRepository.kt`.

### Cómo se verifica

- Con `habilitados = false`: ninguno de los cuatro botones abre nada y la app no
  se cae.
- Con `habilitados = true`: cada botón abre su destino, y si la app falta se ve
  el log en vez de un crash.
- Búsqueda final de `startActivity` en `ui/screens`: cero resultados directos.
- El monto aparece una sola vez en el código.
- En dispositivo: recorrer el flujo de pago y confirmar que no promete un pago
  que no ocurre.

---

## Fase 15 — Navegación y sesión de invitado

Sin dependencias. `AppRoot.kt` ya sabe distinguir invitado y estudiante en la
barra inferior (`inicioDelUsuario`, línea 31); el problema es que esa distinción
no se aplica a las rutas.

### Qué se hace

**1. El invitado llega a pantallas de estudiante.** `inicioDelUsuario` corrige
el caso "volver a Inicio", pero `onNavigate` deja pasar a `Screen.Payments` y
`Screen.Platform`, que están diseñadas con tabs fijas de estudiante. Dos caminos:
cerrarlas con un aviso ("inicia sesión para ver tus pagos"), que es el cambio
menor, o abrir un equivalente de invitado. Se recomienda el aviso: un invitado no
tiene pagos que ver todavía.

**2. `Platform` vuelve al Home equivocado.** `AppRoot.kt:158` tiene
`onGoHome = { nav.reemplazarTodo(Screen.Home) }`, o sea, el Home de estudiante.
Debe pasar por `inicioDelUsuario` como ya hace la barra inferior.

**3. `ProspectHomeScreen` no tiene barra inferior.** El diagrama le asigna
"Inicio" en ambos tipos de sesión, y el catálogo, la ficha y el resto sí la
tienen. Hoy el invitado navega por Home sin barra y sin forma de volver al
catálogo. Se le pasa el mismo `onTabNavigate` que usan las demás pantallas.

**4. Programa preseleccionado sin avisar.** `PreinscriptionScreen` recibe
`programaId: Int?`; si viene `null`, toma el primero del catálogo. Quien entra
desde el Home ve "Analítica de Datos e IA" ya marcado y lo envía como elección
suya. Cuando no hay `programaId`, el campo debe quedar vacío y pedir elegir, o
mostrar el nombre como referencia no seleccionable. Ojo: el texto del botón dice
"Siguiente" y el nombre aparece arriba, así que el cambio debe verse claro en
ambos sitios.

**5. Limpieza de lo que no se usa.** `loggedIn` en `MainActivity.kt`,
`onBack` en `AuthScreen`, `onAdvisor` en `ProgramDetailScreen` y el parámetro
`repository` en varias pantallas que no leen datos. Se quitan; si alguno se va a
usar después, se vuelve a agregar cuando se use.

### Archivos

`AppRoot.kt`, `ProspectHomeScreen.kt`, `PreinscriptionScreen.kt`, `MainActivity.kt`,
`AuthScreen.kt`, `ProgramDetailScreen.kt`.

### Cómo se verifica

- Recorrido completo como invitado: Welcome → Home → catálogo → ficha → preinscripción
  → enviado, sin ver ninguna pantalla de estudiante y sin topar con pantallas
  bloqueadas salvo los avisos previstos.
- El mismo recorrido con sesión iniciada, comprobando que la barra lleva a
  Payments y Platform.
- En ambos casos, "Inicio" vuelve al Home que corresponde.
- Preinscripción abierta sin programa: no aparece ninguno elegido.
- `DIAGRAMA_NAVEGACION.md` se actualiza para que siga describiendo lo que hace
  el código.

---

## Fase 16 — Datos oficiales de la Fundación

Bloqueada por la Fundación. Es una fase de consulta: el código puede quedar
preparado, pero los valores se llenan cuando lleguen. Todo en una sola consulta,
porque cada respuesta llega lenta.

### Qué se pide

**Montos.** Valor de matrícula y de générosité por programa y por concepto. Es lo
único que bloquea otra fase (los montos del PSE, parte 2 de la Fase 14).

**Competencias.** 42 de los 67 programas del catálogo no tienen
`competencias`, lo que deja la ficha y el comparador vacíos. Se puede aceptar
entregar solo los programas con competencies completas y quitar los demás del
catálogo mientras tanto, que es mejor que mostrar fichas a medio llenar.

**Sedes.** El catálogo y los textos mencionan Girardot, pero
`repository.sedes` solo tiene Chía y Sopó. Confirmar cuáles son reales y si
`repository.sedes` es la lista buena o hay otra.

**Promociones y testimonios.** Listas vacías a la espera de contenido. Hace
falta texto, imagen y, si se puede, fecha de vigencia.

**Contacto y enlaces.** Teléfonos, correos, URL de la plataforma
(`PLATAFORMA_URL`) y del PSE (`PSE_URL`), y el número de WhatsApp
(`WHATSAPP_NUMERO`). Hoy son constantes provisionales en `DataRepository`.

**Contenido operativo.** Horarios del cronograma, requisitos del cuestionario
del orientador, textos del FAQ, y los textos de requisitos de cada documento del
paso 2.

### Cómo se avanza mientras tanto

Marcar en el código qué valores son provisionales, para que no se lea como
dato oficial. Los de contacto, al menos, son los que más riesgo tienen si
alguien los usa para llamar.

### Archivos

`DataRepository.kt` y, según el contenido, los textos de las pantallas.

### Cómo se verifica

Cada dato nuevo entra con su fuente anotada en esta bitácora: quién lo confirmó
y cuándo. Es la diferencia entre un dato que la Fundación puede cambiar en
octubre y uno que nadie va a saber de dónde salió.

---

## Fase 17 — Seguridad y almacenamiento

Sin dependencias. Lo más delicado del conjunto: aquí los datos son de personas
reales, en su mayoría mayores de edad con documentos de identidad.

### Qué se hace

**1. Contraseñas en texto plano.** El modo local de `AuthScreen` guarda la
contraseña tal cual en `SharedPreferences`. Un respaldo del dispositivo o un
`adb backup` la dejan legible. Opciones, de mejor a peor: no guardar contraseñas
en local y validar contra Firebase siempre; o guardar un hash con salt; o, como
mínimo, dejar claro en pantalla que el modo local es solo para demostración.

**2. `StringSet` no tiene orden.** Interesados, usuarios y preinscripciones se
guardan en conjuntos de strings, y `SharedPreferences` no garantiza el orden de
un `StringSet`: "Mis interesados" y "Mis datos" muestran una entrada al azar.
Se cambia a un string con separador por registro (el mismo criterio que ya usa
`PreinscripcionTexto` con `\n`) o a `DataStore`.

**3. Tope de 25 sin aviso.** Al llegar a 25 preinscripciones se descartan en
silencio. Pasa de "no veo el que guardé" a un mensaje que diga cuántos hay y que
el asesor los revise.

**4. Respaldo a la nube sin criterio.** `allowBackup="true"` con
`backup_rules.xml` y `data_extraction_rules.xml` sin una sola exclusión: los
datos personales de los interesados (documento, teléfono, correo, fecha de
nacimiento, condiciones especiales, archivos adjuntos) pueden ir al respaldo de
Google. Se excluye el directorio de preferencias y se documenta el criterio.

**5. Adjuntos y permisos.** Los permisos persistentes de lectura sobre los
documentos se conceden al vuelo (Fase 12) y quedan vivos en `Activity` aunque el
adjunto se desmarque. Se liberan cuando el registro se descarta.

### Archivos

`DataRepository.kt`, `AuthScreen.kt`, `MainActivity.kt`,
`res/xml/backup_rules.xml`, `res/xml/data_extraction_rules.xml`,
`AndroidManifest.xml`, `PreinscriptionScreen.kt`.

### Cómo se verifica

- `adb backup` de la app y revisar que no salga ninguna contraseña ni documento.
- Guardar 30 interesados y comprobar que aparecen en orden y que el aviso del
  tope aparece.
- Sesión local: registrar, cerrar la app, volver a entrar.
- Con `habilitados = false` y sin red, la app debe seguir siendo usable en modo
  local.

---

## Fase 18 — Accesibilidad

Sin dependencias, y es la fase de mejor relación entre trabajo y resultado:
`PLANDESARROLLO.md` pone a los adultos mayores como público prioritario, y casi
todo lo pendiente aquí es de un cambio de número.

### Qué se hace

**1. `SmallButton` mide 34.dp** (`Components.kt:186`). Es un botón que se toca
con el dedo y a veces con temblor. A 48.dp mínimo, con el texto centrado y sin
perder el color que lo distingue como acción secundaria.

**2. Las etiquetas de la barra inferior están en 10.sp** (`Components.kt:476`).
Con buen eyesight se leen, con el texto grande del sistema y a distancia de la
pantalla no. A 12.sp, revisando que la barra siga cabiendo en pantallas angostas
con "Mis interesados" y "Comunidad", que son las etiquetas más largas.

**3. `SelectorDesplegable` pinta borde rojo cuando ya tiene valor**
(`FormComponents.kt:101`). El rojo se lee como error en un campo ya diligenciado,
y estos campos son positivos, no de error. El borde de error se reserva para
cuando el campo está vacío y es obligatorio.

**4. Las tarjetas de documentos no se anuncian.** `CasillaDocumento` es una
tarjeta clicable con un `Checkbox` dentro, sin `contentDescription` ni semántica
de estado. Con TalkBack se lee el título y la nota, pero no que está marcado ni
que hay un archivo adjunto. Se agrega semántica de estado marcado y el nombre del
archivo. Lo mismo en las tarjetas de requisitos y de sesión de día.

**5. Una pasada con TalkBack al final**, no antes: recorrer Welcome, Home,
catálogo, preinscripción de punta a punta con el lector de pantalla activo, y
corregir lo que salga. Al probar de a un componente se pierde el contexto, que es
justo donde aparecen los problemas.

### Archivos

`Components.kt`, `FormComponents.kt`, `ProspectHomeScreen.kt`, y las pantallas
con tarjetas interactivas.

### Cómo se verifica

- `SmallButton` y las etiquetas medidos con el inspector de Compose.
- TalkBack de punta a punta en el recorrido principal, anotando cada anuncio que
  no se entienda.
- Revisar la barra inferior en un emulador de pantalla angosta con fuente de
  sistema al máximo.

---

## Fase 19 — Pruebas y cierre

Depende de todas las anteriores. Nada de esto se puede hacer antes.

### Qué se hace

**1. Pruebas de `DataRepository`, que hoy no existen.** Son la pieza con más
lógica y menos cobertura: sesión local y de Firebase, orden de los registros tras
el cambio de `StringSet`, aviso del tope, y que las contraseñas no queden legibles.

**2. Pruebas de navegación de invitado**, que hoy no existen. Con la Fase 15
toca poder comprobar que el invitado no alcanza pantallas de estudiante. Si el
`NavController` es una lista, es trabajo barato: recorrer la pila desde Welcome y
afirmar qué rutas son alcanzables con y sin sesión.

**3. Endurecer la prueba débil del orientador.**
`una respuesta neutra devuelve los tecnicos como base` (en `ProgramFinderTest`)
da 1 punto a todos los tipos y espera solo técnicos; hoy pasa por el orden de la
lista, no por la lógica de puntuación. Si un cambio en el orden del catálogo la
rompe, la culpa será de la prueba, no del orientador. O se corrige la puntuación
o se reescribe la prueba para que afirme lo que realmente quiere afirmar.

**4. Borrar `ExampleUnitTest` y `ExampleInstrumentedTest`**, que son de plantilla
y no prueban nada del proyecto. La instrumentada se puede convertir en la primera
prueba real de la app.

**5. Prueba manual del selector del sistema**, pendiente desde la Fase 12: elegir
un archivo de verdad, verificar que la URI persiste al cerrar y reabrir la
preinscripción, y que un archivo con coma y signos en el nombre sobrevive al
guardado. Con un archivo de prueba del propio proyecto, no con documentos del
teléfono.

**6. Cierre.** `README.md` y `PLANDESARROLLO.md` al día con lo implementado,
`DIAGRAMA_NAVEGACION.md` coherente con el código, y esta bitácora con el
resultado de cada fase y lo que quedó fuera.

### Cómo se verifica

`testDebugUnitTest` en verde (hoy 45 pruebas; la meta tras la Fase 17 es de 55 o
más). `compileDebugKotlin --offline` limpio. Recorrido de las cuatro rutas en
dispositivo: invitado, estudiante, asesor y preinscripción de punta a punta.

---

## 5 Oct — Fases 12 y 13 cerradas, e integración del módulo `:campo`

Dos cosas en esta pasada, en este orden porque la segunda necesitaba la primera
en verde.

### Fase 12 y Fase 13 (cierre del trabajo que estaba a medias)

La Fase 12 estaba commiteada a medias y la Fase 13 empezada sin terminar. El
proyecto **no compilaba**: `PasoFormacion` ya exigía el parámetro
`formatosElegidos` y la llamada no lo pasaba.

- **Arreglo:** `PreinscriptionScreen.kt` pasa `formatosElegidos` al paso 2.
  Con eso las cuatro correcciones de la Fase 13 quedan cerradas: el texto ya no
  niega el adjunto, desmarcar la casilla borra el archivo y suelta el permiso
  persistente, el formato elegido filtra de verdad el selector del sistema, y la
  elección sobrevive al cambio de paso.
- **Pruebas:** de 45 a **54**, todas en verde.

Commit `9601faf`.

### Integración de `EnidAppOfflineFirst` como módulo `:campo`

El módulo de captura en campo era una app Android independiente, con su
`applicationId`, su `Activity` lanzadora y su `Application`. Se integra aquí como
**biblioteca Gradle** (`:campo`), de modo que hay un solo APK, una sola
`Activity` y un solo punto de entrada: el institucional.

**Por qué módulo y no carpeta dentro de `:app`.** Las dos apps resuelven la
navegación de forma distinta y ambas tienen razón: `:app` usa una pila propia
(`NavController`) y `:campo` un `NavHost` de `navigation-compose`. En un solo
módulo las dos pilas conviven en el mismo grafo sin relación entre sí; como
módulos, cada una resuelve lo suyo y se conectan en un único punto.

**Qué cambió dentro del código del módulo**

| Cambio | Por qué |
|---|---|
| `MainActivity.kt` y `EnidApplication.kt` eliminados | En una biblioteca no puede haber una `Activity` lanzadera ni robarle el `Application` a la app que lo contiene |
| `EnidAppHolder` nuevo | Reemplaza a `EnidApplication`: mismo criterio de una sola instancia por proceso, resuelta desde `applicationContext` y con doble comprobación `volatile` |
| `campo/src/main/AndroidManifest.xml` reducido | Solo permisos, `<queries>` y el `FileProvider`. Icono, etiqueta y tema los pone `:app` |
| `BotonSalirDelModulo` nuevo en la cabecera del resumen | El resumen era la pantalla de inicio y no tenía de dónde salir. Va en la cabecera y no en el pie porque el pie lo ocupa SINCRONIZAR |

**Lo que hubo que resolver y no estaba previsto**

1. **`com.android.library` no se podía resolver sin red.** El artefacto marcador
   del plugin no está en la caché de Gradle, porque hasta ahora solo se usaba
   `com.android.application`. Se resuelve en `settings.gradle.kts` con
   `resolutionStrategy.eachPlugin`, que apunta al mismo JAR de AGP que ya está
   descargado. Así el build sigue siendo reproducible con
   `org.gradle.offline=true`.
2. **`android.disallowKotlinSourceSets=false`** en `gradle.properties`: KSP
   registra las carpetas generadas de Room con el DSL `kotlin.sourceSets`, que
   AGP 9 con Kotlin integrado rechaza.
3. **La autoridad del `FileProvider` had que coincidir con el código.**
   `AlmacenEvidencias` construye `${context.packageName}.fileprovider`; al ser
   ahora el mismo proceso, el paquete es `com.fundacion.enid` y no el que tenía
   como app aparte. Si se hubiera cambiado solo un lado, la captura por cámara
   falla con `FileNotFoundException`.
4. **`SecondaryLink` era `private`** en `HomeScreen.kt` y hacía falta en el Home
   de invitado. Ahora es pública, con el comentario que explica que es un
   componente de navegación y no un detalle de una pantalla.

**Verificación en dispositivo** — Motorola moto g(20), Android 11, `ZT322CVFC9`.

| Ejecución | Qué se comprobó | Resultado |
|---|---|---|
| 1 · arranque en frío | Bienvenida → invitado → "Equipo de campo" → resumen → nueva captura | Sin crash. `0 locales / 0 pendientes / 0 sincronizados`, "Red ahora: WiFi". Las reglas R1..R8 disparan: "Ingrese el número de documento", "Ingrese la edad del aspirante" |
| 2 · modo avión | Banner, indicador y SINCRONIZAR sin conexión | "Trabajando sin conexión…", "Red ahora: **sin conexión**" (no "activa", el error 5 del módulo ya corregido). Los contadores no cambian: no se pierde nada |
| 3 · con conexión, tras cerrar y reabrir la app | Reinicio de red y persistencia | `airplane_mode_on=0` → banner desaparece y vuelve "Red ahora: WiFi". La base Room se reabre sin error tras el cierre |

El retroceso del sistema se comporta como debe en la frontera de los dos
grafos: dentro de `:campo` lo resuelve su `NavHost` (captura → resumen) y
solo después pasa a la pila de `:app` (resumen → Home). El botón "Volver a la
app" de la cabecera hace lo mismo de forma explícita, sin depender del gesto.

**Pruebas tras la integración**

| Suite | Dónde | Pruebas | Fallos |
|---|---|---|---|
| Formato, preinscripción, orientador | `:app` JVM | 54 | 0 |
| Plantilla de instrumentación | `:app` dispositivo | 1 | 0 |
| Validación R1..R8 y serializador RTDB | `:campo` JVM | 34 | 0 |
| Room real, evidencia, duplicados, ciclo de vida | `:campo` dispositivo | 10 | 0 |
| **Total** | | **99** | **0** |

### Lo que queda pendiente después de esta pasada

Lo de las fases 13 a 19 sigue pendiente salvo lo ya listado; lo nuevo que
apareció aquí:

1. **Restringir "Captura en campo" por rol.** Ahora se ofrece a cualquier
   sesión para poder verificarla sin crear cuenta. Debería quedar solo para
   `Rol.ADMINISTRATIVO`, que es quien lo usa en la Fundación. Depende de que la
   Fundación confirme quién entra.
2. **El scroll del Home se pierde al entrar y salir del módulo.** Al volver con
   "Volver a la app" el Home se recompone desde arriba. Es el comportamiento de
   una pila que no guarda estado de scroll, no un error del módulo.
3. **`PuertaRemotaSimulada` sigue siendo la puerta remota.** El envío se
   demuestra sin backend; conectar Firebase Realtime Database es el punto 8 del
   `README` del módulo y sigue sin hacer.
4. **La base de datos de campo no se cifra** y no hay autenticación en el
   módulo: cualquiera que abra el teléfono y entre a "Equipo de campo" ve la
   jornada y las fotos. Es el mismo punto que la Fase 17 plantea para `:app`, y
   aquí es más delicado porque hay documentos de identidad de menores.
5. **La captura por cámara y la galería siguen sin probarse a mano.** La
   automatización por ADB no permite dispararlas de forma fiable; el
   almacenamiento y la validación de la evidencia sí están cubiertos por las 10
   pruebas en dispositivo.

---

## 6 Oct — Auditoría de las 18 pantallas y corrección del desorden

Al revisar la app en el teléfono, no por fases sino pantalla por pantalla,
salieron siete cosas que el usuario ve de verdad. Ninguna era de maquetación:
todas eran de estructura.

### Lo que se encontró y se corrigió

1. **El invitado llegaba a la pantalla de Pagos con datos inventados.**
   Verificado en el moto g(20): como invitado, el paso "Realizar el pago" de
   `rutaProspecto` abría `Payments` y mostraba "Valor de la Matrícula $180.000",
   "Vence: 13 Oct" y un historial con "Matrícula Agosto 2025 · Pagado el 05 Ago ·
   Pago confirmado. Ciclo 2025-2". Tres datos que la Fundación no ha dicho.
   Los pasos 5 a 10 de `rutaProspecto` son ahora `enSede = true`.

2. **`PseScreen` confirmaba pagos que no ocurrían.** El botón ponía
   `pagado = true` después de un `try/catch` vacío, así que mostraba
   "Pago procesado con éxito" incluso con las acciones externas apagadas.
   Ahora `EnlacesExternos.abrirPse` devuelve `true` solo si la pasarela se
   abrió, y la pantalla dice lo que corresponde en cada caso.

3. **Ocho pantallas pintaban las pestañas de estudiante a cualquiera.**
   `AppScaffold` tenía `tabs: List<EnidTab> = EnidTab.entries` como valor por
   defecto, y ocho pantallas no pasaban `tabsSegunSesion(...)`. La afirmacion
   del BITACORA anterior ("el invitado no ve Horario ni Pagos") era cierta solo
   en 7 de 15 pantallas. Ahora `AppScaffold` lee `SesionUi.conectado` y ninguna
   pantalla pasa `tabs`: se acabaron las dos formas de equivocarse.

4. **No había botón de volver en ninguna pantalla.** `AppScaffold` recibía
   `onBack` y nunca lo usaba; no existía ninguna flecha de retorno en toda la
   app. El único camino de vuelta era el gesto del sistema, que no es lo que
   asume una app para mayores de 60 años. Ahora se dibuja con 48 dp de área
   táctil.

5. **Cuatro pantallas eran callejones sin salida.** `ProspectHome` —el Home del
   invitado—, `Profile`, `Pse` y `Benefits` no tenían barra inferior. Las cuatro
   la reciben ya.

6. **Cinco pantallas abrían apps externas por su cuenta**, saltándose el
   interruptor `EnlacesExternos.habilitados`. Ahora las cinco pasan por
   `EnlacesExternos`, y una prueba lo verifica leyendo el código fuente.

7. **El monto de matrícula estaba escrito en siete sitios con dos valores.**
   `$180.000` en `PaymentsScreen` y `BenefitsCalculatorScreen`, `$830.000` en
   `PseScreen`. Ahora hay un `DataRepository.MONTO_MATRICULA` y las pantallas
   que lo muestran añaden `AVISO_MONTO`, que dice que es preliminar.

8. **El sistema visual estaba duplicado.** Al integrar `:campo`, ese módulo
   traía copia de `Color.kt`: doce colores con el mismo nombre y el mismo valor
   en dos ficheros, y seis colores iguales con nombres distintos
   (`EnidGreen`/`EnidVerdeWhatsapp`, `EnidGold`/`EnidOro`,
   `EnidChipDoneBg`/`EnidSincronizadoFondo`, `EnidPhoto`/`EnidFotoPlaceholder`).
   Nace el módulo `:ui`, con la paleta base y `EnidDimens` en un solo sitio.

### Lo que NO se corrigió, y por qué

Los trece componentes con el mismo nombre en `:app` y `:campo`
(`BigButton`, `SelectorDesplegable`, `AppScaffold`…) **no son código duplicado**:
se comparó el cuerpo de los trece y **ninguno es idéntico** a su homólogo.
Son dos implementaciones que convergieron en los mismos nombres, con firmas y
tamaños distintos. Borrar una copia no compila; unificarlas es elegir una
implementación por componente y adaptar la otra, cambiando el aspecto de uno de
los dos módulos. Queda como Fase 23, no como limpieza.

### Ejecuciones en el dispositivo (moto g(20), Android 11)

| # | Qué se comprobó | Resultado |
|---|---|---|
| 1 | Arranque en frío, entrada como invitado | Pestañas **Inicio + Soporte** (antes ninguna). Los pasos 9 y 10 muestran "con asesor" en vez de abrir Pagos y Platform |
| 2 | Navegación a Sedes y botón "Volver" | El botón aparece (`content-desc="Volver"`) y devuelve al Home del invitado. Pestañas correctas |
| 3 | Pantallas de Pagos y PSE con las firmas nuevas | Compilan; el monto sale del repositorio y no se puede afirmar el pago |
| 4 | Módulo `:campo` tras el refactor de tokens | Arranca igual. `#F4F3EF`, `#322D6B`, `#C81920`, `#D1D5DB`, `#4B5563`, `#111827`, `#EEEDF5` presentes con el valor exacto |
| 5 | `:app` tras el refactor de tokens | `#F4F3EF`, `#322D6B`, `#C81920`, `#F5BDB8`, `#111827`, `#4B5563`, `#D1D5DB` presentes con el valor exacto |

Los colores se comprobaron por muestreo de píxeles de la captura, no a ojo:
siete de los once tokens de `:app` y ocho de `:campo` aparecen en pantalla con
el valor exacto del token. Los que no aparecen no se usan en esa pantalla.

### Pruebas

| Ámbito | Antes | Ahora |
|---|---|---|
| Unitarias `:app` | 54 | 67 |
| Unitarias `:campo` | 34 | 34 |
| Instrumentadas `:app` | 1 | 1 |
| Instrumentadas `:campo` | 10 | 10 |
| **Total** | **99** | **115** |

Las 13 pruebas nuevas (`NavegacionInvitadoTest`) cubren lo que se rompió: que
ningún paso de la ruta del invitado abra pagos o la plataforma, que solo los
cuatro primeros tengan pantalla, que el invitado no vea Horario ni Pagos, que el
monto se formatee como pesos colombianos, que ninguna pantalla abra un `Intent`
por su cuenta y que la paleta no vuelva a declararse dos veces.

### Pendiente que sigue

Lo de las fases 13 a 19, más:

1. **Fase 23 — unificar los 13 componentes duplicados.** Es un refactor, no una
   limpieza: hay que decidir qué implementación gana en cada uno.
2. **`Screen.Intereses` sigue sin entrada propia.** Solo se llega desde el Home
   con `if (primerInteres != null)`. Cuando no hay intereses guardados la
   pantalla no tiene por dónde abrirse.
3. **La barra inferior sigue durante los 3 pasos de la preinscripción**
   (`PreinscriptionScreen.kt`), con `activeTab = INICIO`: a media inscripción se
   puede tocar "Pagos" y perder lo escrito.

### Dos hallazgos adicionales del reescaneo

Al reescanear los parámetros al final de la pasada salieron dos que el primer
barrido no había visto, y los dos importan:

1. **`PlatformScreen` recibía `onGoHome` y no lo usaba.** El diagrama de
   navegación documentaba un botón "Volver al inicio" que **no existe**, y
   `AppRoot` lo encadenaba a `Screen.Home` fijo. Para un invitado eso significaba
   salir al Home de estudiante: el mismo error que se arregló con las pestañas.
   Se eliminó el parámetro muerto y con él el destino equivocado. La pantalla
   conserva `onBack` y la barra inferior, así que el camino de vuelta correcto
   sigue estando.

2. **`AuthScreen` recibía `onBack` y no lo usaba.** No usa `AppScaffold` —trae
   su propio `Column`— así que la flecha de volver había que dibujarla a mano y
   no estaba dibujada. Como `Login` se alcanza con `nav.navegar` desde
   Bienvenida, sí hay algo a lo que volver: se llega con un gesto invisible y no
   hay forma visible de deshacerlo. Ahora `AuthScreen` dibuja el botón con los
   mismos 48 dp y el mismo `contentDescription` que el resto.

`WelcomeScreen` tampoco usa `AppScaffold`, pero es la raíz de la pila: ahí no hay
nada a lo que volver y no se le pone botón.

## 27 Oct — Icono de la aplicación con el asistente virtual

| Acción | Detalle | Estado |
|---|---|---|
| Imagen elegida | `img_74.png` de `docs/imagenes/figma/logos_y_marca/`, la misma que la app usa como **Asistente virtual de ENID** en el Inicio | Hecho |
| Icono adaptativo | Capa frontal `drawable-nodpi/ic_launcher_fg_robot.png`, con el robot dentro de la zona segura de 66 dp | Hecho |
| Fondo adaptativo | `#EAF3FA`, un fondo claro para que el robot conserve contraste | Hecho |
| Iconos heredados | Se regeneraron los `mipmap-*` (`ic_launcher` y `ic_launcher_round`) en 48, 72, 96, 144 y 192 px | Hecho |
| Verificación | `:app:assembleDebug` en verde, APK reinstalado en `ZT322CVFC9` y app abierta sin errores | Hecho |

## 27 Oct — Roles, perfil alimentado por la preinscripción y tema visual del Figma

| Acción | Detalle | Estado |
|---|---|---|
| Rol administrador | Se añadió `Rol.ADMIN`. Mientras la Fundación no asigne roles desde un servidor, un correo que contenga `admin` entra al panel; el aviso está visible en el acceso | Hecho |
| Menú por rol | Estudiante: **Inicio · Horario · Pagos · Soporte**. Administrador: **Inicio · Panel · Soporte**, sin horario ni pagos de estudiante | Hecho |
| Panel de administración | `AdminScreen.kt` resume preinscripciones, interesados, programas y cuentas locales, y lista las preinscripciones recibidas | Hecho |
| Perfil de solo lectura | `ProfileScreen.kt` ya no pide carrera ni año a mano: los muestra como datos académicos y personales tomados de la preinscripción más reciente. Si no hay ninguna, conserva los valores antiguos de la cuenta como respaldo y ofrece **HACER MI PREINSCRIPCIÓN** | Hecho |
| Año o ciclo | Lista cerrada de `DataRepository.ciclos` (año actual y siguiente × primer/segundo semestre). Se elige en el paso 1 de la preinscripción y se guarda en `Preinscripcion.anio` | Hecho |
| Documento de identidad | Selector compacto que muestra solo la sigla (`CC`, `CE`, `TI`, `NIT`); al desplegarlo aparece la sigla y el nombre completo | Hecho |
| Prefijo telefónico | Selector compacto a la izquierda del número (`+57`), con banderas de 24 × 16 dp dentro de la lista y ejemplo `300 123 4567` en el campo | Hecho |
| ICFES | Se eliminó el campo **Número del ICFES** del paso 2: el certificado se entrega como documento y el número no aporta al trámite. El modelo conserva `icfes` para leer registros antiguos | Hecho |
| Iconos del Figma | `EnidIcons.kt` pasó de glifos rellenos de Material a trazos de **1.8** sobre cuadrícula de 24, con remates y esquinas redondeadas, como el `Icon` de `Review Attached Content/src/App.tsx` | Hecho |
| Tipografía | `Type.kt` declara una sola escala: cuerpo 17/15/13 sp, títulos 23/20/17, rótulos 16/13/11. Antes convivían estilos de Material de 12 sp con títulos de 24 sp | Hecho |
| Cajas y espaciado | Botones, campos, tarjetas y listas comparten radios de 10–16 dp y menos aire vertical (botones de 50 dp, campos de 48 dp, separadores de 12–14 dp). Los selectores compactos miden 90 × 52 dp | Hecho |
| Iconos internos | Se agrandaron los glifos de las tarjetas y accesos (26/25/24 dp), la barra inferior (20 dp), los botones (20/19/17 dp) y las flechas de los selectores (20/18 dp) | Hecho |
| Pasos al inicio | `AppScaffold` acepta un `scrollState` opcional; la preinscripción comparte uno y vuelve a `0` al cambiar de paso o al llegar a la confirmación. Antes, avanzar desde el final del paso 1 abría el paso 2 ya desplazado y ocultaba **Nivel de formación** | Hecho |
| Error que se limpia | Un `LaunchedEffect` sobre los campos del formulario borra el mensaje de validación en cuanto se corrige cualquier dato, sin exigir otro toque en CONTINUAR. Mostrar el error no lo reinicia | Hecho |
| Verificación | `:app:compileDebugKotlin`, `:app:testDebugUnitTest` (72 pruebas) y `:app:assembleDebug` en verde. APK instalado en `ZT322CVFC9` y app abierta **3 veces**; con `uiautomator` se comprobó el menú por rol, el perfil de solo lectura, los selectores compactos con banderas y la ausencia del número del ICFES. En la última pasada se comprobó además que el **paso 2 abre arriba** al avanzar desde el final del paso 1 y que el error de validación **desaparece al elegir el tipo de documento** | Hecho |

## 27 Oct — Sede única, redes oficiales y paleta institucional

| Acción | Detalle | Estado |
|---|---|---|
| Sede única | `DataRepository.sedes` queda con **Sede Chía**: `Calle 13 # 9-77, Parque Ospina, Chía, Cundinamarca`, teléfonos **316 630 1033** y **313 204 8325**, horario de lunes a viernes 7:00 a.m.–8:00 p.m. y sábados 8:00 a.m.–1:00 p.m. Desaparecen Sopó y Girardot de los datos y de la oferta | Hecho |
| Teléfonos múltiples | `Sede` guarda `telefonos: List<String>`; cada número se toca por separado y abre el marcador con ese número. `telefono` queda como primer valor para las acciones que solo admiten uno | Hecho |
| Pantalla de sede | `LocationsScreen` pasa a **“Nuestra Sede”**; muestra la sede, la dirección, los dos teléfonos con icono y el botón **Cómo Llegar** a pantalla completa. La tarjeta de estadísticas dice `1 sede: Chía, Cundinamarca` | Hecho |
| Redes oficiales | Enlaces tomados de `enid.edu.co`: Facebook `https://www.facebook.com/Enid.Escuela/`, Instagram `https://www.instagram.com/fundacionenid/`, YouTube `https://www.youtube.com/channel/UCb_yG90hqnKELFCBp0Dcqeg`. Viven en `DataRepository` y se abren con `EnlacesExternos.abrirFacebook/Instagram/YouTube` | Hecho |
| Iconos sociales | `VFacebook`, `VInstagram` y `VYoutube` siguen el tema de trazo del Figma (24 × 24, grosor 1.8, remates redondeados, sin relleno) | Hecho |
| Fila reutilizable | `RedesSociales` dibuja tres accesos con su color de marca sobre fondo suave: Facebook azul `#4B6195`, Instagram violeta `#352F6F`, YouTube rojo `#C11721`. Se añadió al final del **Inicio** (estudiante y administrador), del **Inicio del invitado** y de **Nuestra Sede** | Hecho |
| Paleta oficial | Rojo `#C11721` para CTAs, etiquetas, fechas, navegación activa y conversión; violeta `#352F6F` como identidad y estructura; azul `#4B6195` en fondos y degradados; turquesa `#2BA7A2` para progreso y estados positivos. Tonos suaves: rojo `#F7DADC`, azul `#E3E8F3`, violeta `#ECE9F4`, turquesa `#DEF3F1` | Hecho |
| Contraste del turquesa | El turquesa puro no alcanza AA como texto (2.9:1 sobre blanco). El texto de estados completados usa `EnidTealDark` `#176B68` (5.1:1); el turquesa puro queda para rellenos y acentos | Hecho |
| Verde de WhatsApp | `#25D366` se conserva **solo** para la marca WhatsApp; los chips “Pagado / Completado” pasaron de verde a turquesa | Hecho |
| Verificación | `:app:compileDebugKotlin`, `:app:testDebugUnitTest` y `:app:assembleDebug` en verde. APK instalado y app abierta **3 veces** en `ZT322CVFC9`. Con `uiautomator` se comprobó “Nuestra Sede”, la dirección, los dos teléfonos y la fila Facebook/Instagram/YouTube en Inicio y en la sede. La captura de pantalla se muestreó por color y aparecen `#C11721`, `#352F6F`, `#4B6195`, `#F7DADC`, `#E3E8F3` y `#ECE9F4` | Hecho |

## 27 Oct — Retiro completo del sistema de pagos

| Acción | Detalle | Estado |
|---|---|---|
| Pantallas | Se borraron `PaymentsScreen.kt` (Mis Pagos) y `PseScreen.kt` (pago por PSE con selección de banco). No queda ninguna pantalla que muestre saldos, cuotas, historial ni pasarela | Hecho |
| Rutas | Se quitaron `Screen.Payments` y `Screen.Pse` y sus ramas en `AppRoot`. La pila ya no puede llegar a un módulo de pagos | Hecho |
| Pestaña | Se eliminó `EnidTab.PAGOS`; el estudiante ve **Inicio · Horario · Soporte** y el invitado **Inicio · Soporte**. El administrador sigue con **Inicio · Panel · Soporte** | Hecho |
| Modelos y datos | Se borraron `PaymentNotice`, `EstadoPago` y `DataRepository.pagos`. También `PSE_URL`, `MONTO_MATRICULA`, `montoMatriculaTexto` y `AVISO_MONTO` | Hecho |
| Salidas externas | Se eliminó `EnlacesExternos.abrirPse`; el objeto ya no conoce la pasarela PSE | Hecho |
| Componentes | Se retiraron `StatusChip`, los chips de deuda `EnidChipWarnBg/Text` y el icono `VPagos`, que solo usaba el módulo retirado | Hecho |
| Inicio | La tarjeta **Pagos** del menú del estudiante desapareció. “Mis Clases” y “Mi Cuenta” comparten fila, y “Plataforma Educativa” ocupa una fila completa. El Home del invitado ya no promete “horario, pagos y soporte” | Hecho |
| FAQ | Se quitó la categoría **PAGOS**. La pregunta “¿Cómo puedo pagar la matrícula?” queda en MATRÍCULAS y ahora responde que el pago se coordina con un asesor o en la sede y que **la app no procesa pagos ni recibe datos bancarios**. Se eliminó la pregunta sobre mora y alertas automáticas | Hecho |
| Plan Referidos | Se conserva la calculadora, pero ya no muestra precios: solo el porcentaje de descuento y la nota de que se aplica al formalizar la matrícula con un asesor | Hecho |
| Ruta del prospecto | El paso 9 dice “Pagas tu matrícula con el asesor o en la sede de Chía”; se quitó la mención a PSE. Los pasos 5 a 10 siguen resolviéndose fuera de la app | Hecho |
| Pruebas | Se actualizaron las pestañas esperadas (3 del estudiante) y se eliminaron las pruebas de montos y de `PSE_URL`. Se añadió `el sistema de pagos no vuelve al codigo`, que busca las piezas retiradas en `app/src/main/java` | Hecho |
| Verificación | `:app:compileDebugKotlin`, `:app:testDebugUnitTest` (70 pruebas) y `:app:assembleDebug` en verde. APK instalado y app abierta **3 veces** en `ZT322CVFC9`. Con `uiautomator`: Inicio con **Inicio · Horario · Soporte**, sin “Pagos” ni “PSE”; Plan Referidos muestra “Descuento Obtenido: 15%” sin importes; FAQ sin categoría PAGOS | Hecho |

## 27 Oct — Aprender, Agenda y Perfil, y el Inicio del Figma

| Acción | Detalle | Estado |
|---|---|---|
| Pestañas | `EnidTab` pasa a **Inicio · Aprender · Agenda · Perfil** para el estudiante. El invitado conserva **Inicio · Soporte** y el administrador **Inicio · Panel · Soporte** | Hecho |
| Aprender | `LearnScreen.kt` muestra el programa activo, la barra de progreso, cinco lecciones y una insignia de avance. Las lecciones son las asignaturas distintas del horario real, no un temario inventado | Hecho |
| Progreso | `DataRepository.leccionesCompletadas(correo)` guarda los índices marcados en `SharedPreferences`, separados por correo. Marcar y desmarcar una lección actualiza el porcentaje y se conserva al volver a entrar | Hecho |
| Agenda | `AgendaScreen.kt` dibuja los próximos cinco días con `LocalDate`, la próxima clase real y las clases de la fecha elegida. `AgendaClases.proxima` descarta las clases de hoy que ya terminaron: a las 3 p. m. no llama “próxima” a una de las 7 a. m. Los días sin clase lo dicen en vez de inventar eventos | Hecho |
| Mi Horario | `DailyScheduleScreen.kt` deja de tener pestaña propia: se abre desde **Agenda** con **VER HORARIO Y PLAN DE ESTUDIOS** y conserva el plan de estudios | Hecho |
| Perfil | `ProfileScreen.kt` se rehace con la estructura del Figma: avatar, estadísticas reales (programa, horas marcadas, logros), tres insignias, datos desplegables, ajustes y cierre de sesión | Hecho |
| Datos de solo lectura | La carrera, el año/ciclo, el documento y el teléfono se leen de la preinscripción más reciente; los valores antiguos de la cuenta quedan como respaldo. Sin preinscripción se ofrece **HACER MI PREINSCRIPCIÓN** | Hecho |
| Privacidad | El ajuste **Privacidad** abre un diálogo que explica qué se guarda en el teléfono y recuerda que la app no procesa pagos ni datos bancarios | Hecho |
| Notificaciones | El interruptor de **Perfil** guarda una preferencia local. No enciende avisos push: la app no tiene un servicio de notificaciones conectado y el texto no lo promete | Hecho |
| Inicio estudiante | Saludo con fecha, tarjeta **Curso en progreso** hacia Aprender, filtros y tarjetas de programas, **Próxima clase**, accesos de servicio, respaldo institucional, promociones y redes | Hecho |
| Inicio administrador | Conserva la tarjeta de gestión, los accesos al panel, la oferta, el perfil y los servicios, con la nueva cabecera de pestañas. Se quitaron los enlaces duplicados de **Panel** y **Mi Cuenta** de “Más servicios”: las tarjetas de arriba ya llevan a esos destinos y ahora el perfil se llama **Mi perfil** | Hecho |
| Cabecera y barra | `EnidTabHeader` muestra la marca y una campana que avisa “No tienes notificaciones nuevas”. La barra inferior usa indicador rojo, iconos de trazo y el violeta para la pestaña activa | Hecho |
| Campana sin punto | Se quitó el punto rojo de la campana: marcaba una notificación sin leer que no existe. El aviso real aparece al tocarla | Hecho |
| Pruebas | Se actualizó la pestaña esperada del estudiante y se añadieron tres pruebas de `CursoAprendizaje` (porcentaje y título del curso) y cinco de `AgendaClases` (próxima clase real) | Hecho |
| Verificación | `:app:compileDebugKotlin`, `:app:testDebugUnitTest` (78 pruebas) y `:app:assembleDebug` en verde. APK instalado en `ZT322CVFC9`; con `uiautomator` se recorrieron las cuatro pestañas, se marcó una lección (0% → 20%), se abrió Mi Horario desde Agenda, se desplegaron los datos del Perfil y se comprobó el diálogo de Privacidad. También se verificaron el Home del invitado (2 pestañas) y el del administrador (3 pestañas) | Hecho |
