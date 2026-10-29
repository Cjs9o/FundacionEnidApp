# PLAN DE DESARROLLO — Aplicación Móvil Fundación ENID

> Proyecto: Aplicación móvil Android (Kotlin) para la **Fundación Escuela Nacional
> de Innovación y Desarrollo en Liderazgo (Fundación ENID)**.
> Basado en las diapositivas del avance de proyecto y el documento
> `requerimientos proyecto.md`.

## Contexto del proyecto

Fundación ENID es una institución de educación técnica colombiana con más de
veinte años de trayectoria (calidad certificada bajo NTC desde 2015). Ofrece
programas como **Técnico Laboral en Seguridad Ocupacional y Laboral**,
**Soporte de Sistemas e Informática**, diplomados y cursos.

La aplicación está pensada para una población que **incluye adultos mayores
(60+)**, por lo que la prioridad #1 es una **interfaz accesible y adaptada**:
tipografía grande y legible, botones de gran tamaño, alto contraste y
navegación sencilla.

---

## Requerimientos funcionales (12 originales; 09 y 10 retirados el 27 Oct 2026)

| # | Requerimiento | Descripción resumida |
|---|---|---|
| 01 | Interfaz Adaptada y Accesible (UI/UX) | Tipografía legible, botones grandes, alto contraste para adultos mayores |
| 02 | Registro e Inicio de Sesión | Perfil + autenticación (nombre, correo, rol); control de la comunidad |
| 03 | Formulario y Gestión de Preinscripciones | Datos del aspirante + programa de interés, almacenados para consulta institucional |
| 04 | Catálogo Institucional y Oferta Académica | Programas técnicos, diplomados y cursos con pensum, perfil ocupacional y sedes |
| 05 | Sedes e Información de Contacto | **Sede Chía** (Calle 13 # 9-77, Parque Ospina); dos teléfonos, llamada telefónica y ubicación en Google Maps / Waze |
| 06 | Preguntas Frecuentes (FAQ) | Lista desplegable: admisiones, matrícula y certificados |
| 07 | Cronograma General del Plan de Estudios | Fechas de inicio/fin de cada asignatura |
| 08 | Horario Diario de Clases | Vista diaria de las clases del estudiante |
| 09 | Alertas y Notificaciones de Pago | **Retirado el 27 Oct 2026** a petición: la app no gestiona pagos ni montos |
| 10 | Redireccionamiento de Pagos (PSE) | **Retirado el 27 Oct 2026** a petición: no hay pasarela ni datos bancarios |
| 11 | Calculadora de Beneficios | Simulador de descuentos en matrícula por referir nuevos estudiantes |
| 12 | Acceso a la Plataforma Educativa | Enlace directo a la plataforma académica institucional |

---

## Fases de desarrollo

### Fase 1 — Planificación y estructura del proyecto
- [x] Definir fases, alcance y requerimientos.
- [x] Elegir tecnología: **Android nativo en Kotlin + Jetpack Compose**.
- [x] Crear carpeta del proyecto separada y documento de trabajo (bitácora).
- [x] Definir arquitectura (MVVM ligero con repositorio local).
- [x] Definir navegación tipo "protegida por sesión" (login primero).

> **Decisión de arquitectura:** un solo `Activity` + Compose, navegación por
> pila propia (sin dependencias adicionales), repositorios locales con datos de
> ejemplo de la institución, preferencias con `SharedPreferences` para la
> sesión. Todo compilable **offline** con SDK, Gradle y JDK ya presentes.

### Fase 2 — Esqueleto del proyecto (Gradle, manifest, tema accesible)
- [x] `settings.gradle.kts`, `build.gradle.kts` (root y app) con AGP/Kotlin.
- [x] `AndroidManifest.xml`, recursos base (strings, colores, estilos, icono).
- [x] Tema accesible: paleta de **alto contraste** (azul oscuro + blanco +
      amarillo/verde de acento), tipografía grande, botones grandes.
- [x] `local.properties` apuntando al SDK del equipo.

### Fase 3 — Navegación y sesión
- [x] Modelos: `User`, `Rol`.
- [x] Pantallas: **Login** y **Registro** (nombre, correo, rol; validaciones).
- [x] Guardar sesión con `SharedPreferences`; cierre de sesión en perfil.
- [x] Contenedor principal (Home) con menú principal accesible.

### Fase 4 — Módulos institucionales
- [x] **Home / Dashboard** con botones grandes por módulo.
- [x] **Catálogo Académico**: lista de programas/diplomados/cursos → detalle
      con pensum, perfil ocupacional y sedes.
- [x] **Sedes y Contacto**: Sede Chía, dos teléfonos, botones de llamada
      (`tel:`) e integración Google Maps / Waze (URLs geo+llamadas).
- [x] **Preguntas Frecuentes (FAQ)** con acordeón: admisiones, matrícula, certificados.

### Fase 5 — Módulos de estudiante
- [x] **Preinscripción**: formulario (datos personales + programa de interés),
      validación y confirmación; datos guardados localmente.
- [x] **Cronograma del plan de estudios**: fechas por asignatura.
- [x] **Horario diario**: selección de día y vista de clases.
- [x] ~~**Alertas de Pago**~~: retirado el 27 Oct 2026.
- [x] ~~**Pagos PSE**~~: retirado el 27 Oct 2026.
- [x] **Calculadora de Beneficios**: descuento por referidos (regla publicada
      por la institución, simulada).
- [x] **Plataforma Educativa**: enlace directo de acceso.

### Fase 6 — Pruebas y depuración
- [x] Compilar/build al menos **5 veces**, corregir errores de compilación.
- [x] Revisar navegación, accesibilidad y manejo de estados vacíos.
- [x] Verificación final: `gradle :app:assembleDebug` OK (5/5 pruebas, APK generado).

---

## Plan de captación (fases 7 a 10) — aprobado 29 Sep 2026

Objetivo: pasar de una app centrada en el estudiante a una app que **capta prospectos**
y los lleva hasta la matrícula, manteniendo los módulos vigentes y la accesibilidad 60+.

| Fase | Nombre | Alcance | Estado |
|---|---|---|---|
| 7 | Oferta académica potente | Detalle completo por programa (modalidad, duración, perfil ocupacional, qué aprenderás, plan de estudios, sedes) + botones "Quiero estudiar este programa", "Hablar con un asesor" y WhatsApp + botón "¿Qué puedo estudiar?" (5 preguntas de orientación) | ✅ Hecha — commit `9a67b19` |
| 8 | Recorrido del prospecto | Home con dos caminos: **A. Soy estudiante** (Inicio → Clases → Horario → Soporte) y **B. Quiero estudiar en ENID** (Conocer → Explorar → Elegir → Información → Asesor → Preinscripción → Documentos → Matrícula → Pago → Bienvenida). El invitado deja datos y se preinscribe. Botón permanente "Habla con un asesor". | ✅ Hecha — commit `c80158f` |
| 9 | Promociones y testimonios | Módulo "Promociones y oportunidades" (becas, convocatorias, próximos inicios, Facilidades de pago, matrículas abiertas) editable desde un solo lugar, y "Historias ENID" con testimonios de estudiantes y graduados | ✅ Hecha — commit `0977e9a` |
| 10 | Seguimiento de interesados | Estados: Interesado → Contactado → Información enviada → Preinscrito → Documentos → Matrícula pendiente → Matriculado | ✅ Hecha — commit `124f8a5` |
| 11 | Preinscripción completa y adjuntos | Datos de identificación, formación, documentos y familiar; el archivo de cada documento se adjunta desde el teléfono | ✅ Hecha — commits `663c56c`, `9601faf` |
| 20 | Integración del módulo de captura en campo | `EnidAppOfflineFirst` pasa a ser el módulo Gradle `:campo` dentro del APK | ✅ Hecha — 5 Oct |
| 21 | Tokens compartidos (`:ui`) | La paleta base y las medidas salen de un módulo compartido en vez de estar declaradas dos veces | ✅ Hecha — 5 Oct |
| 22 | Orden en el flujo de navegación y datos | Un invitado no llega a módulos de estudiante; las pestañas dependen de la sesión; `EnlacesExternos` es la única salida externa y devuelve si abrió de verdad; botón de volver en todas las pantallas | ✅ Hecha — 5 Oct |
| 23 | Unificación de los componentes duplicados | `:app` y `:campo` tienen 13 componentes con el mismo nombre y **cero cuerpos idénticos**: son dos implementaciones distintas. Unificarlos exige elegir una por componente y adaptar la otra | 🔲 Pendiente |
| 24 | Roles, datos desde la preinscripción y tema del Figma | Rol `ADMIN` con panel propio; perfil de solo lectura alimentado por la preinscripción; carrera y año/ciclo como listas cerradas; documento y prefijo compactos con banderas; sin número del ICFES; iconos de trazo y tipografía única del Figma | ✅ Hecha — 27 Oct |
| 25 | Sede única, redes oficiales y paleta institucional | Solo **Sede Chía** con sus dos teléfonos; accesos a Facebook, Instagram y YouTube tomados de `enid.edu.co`; paleta oficial rojo `#C11721`, violeta `#352F6F`, azul `#4B6195` y turquesa `#2BA7A2` con tonos suaves | ✅ Hecha — 27 Oct |
| 26 | Retiro completo del sistema de pagos | Se eliminaron `PaymentsScreen`, `PseScreen`, las rutas, la pestaña, los modelos de alerta, el historial, la pasarela PSE y los montos. La app ya no procesa ni muestra pagos | ✅ Hecha — 27 Oct |
| 27 | Aprender, Agenda y Perfil + Inicio del Figma | Cuatro pestañas de estudiante, curso con progreso real desde el horario, agenda de cinco días, perfil con logros y datos de solo lectura, y Home reorganizado con la estructura del Figma | ✅ Hecha — 27 Oct |

> **Decisión:** No se añaden aún valor de matrícula, próxima fecha de inicio, horarios
> disponibles ni formas de pago (el portafolio y la web no los publican). Queda pendiente.
> El sistema de pagos se retiró por completo el 27 Oct 2026.
>
> **Restricción de pruebas:** no se abre ninguna app externa (llamadas, páginas web,
> WhatsApp, correos) durante las pruebas en dispositivo.

### Cómo se implementó la fase 10

Los estados del plan (Interesado → Contactado → … → Matriculado) se implementaron como
**el siguiente paso que la app le recuerda a la persona**, no como una tabla de estados
interna: ENID necesita saber a quién contactar, y la app solo conoce dos hechos
verificables — que la persona marcó un programa y si dejó o no su preinscripción.
Por eso el resumen dice exactamente una de dos cosas, y ambas son ciertas:

- sin preinscripción → *"El siguiente paso es dejar tu información para que te contactemos."*
- con preinscripción → *"Ya dejaste tu preinscripción. El siguiente paso es hablar con un asesor."*

Los estados intermedios (Contactado, Información enviada, Documentos, Matriculado) los
lleva el asesor: son estados de la Fundación, no de la app. Registrarlos aquí sin que
nadie los actualice habría creado estados que siempre mienten.

### Contenido que falta y debe entregar la Fundación

| Qué | Dónde se llena | Estado |
|---|---|---|
| Promociones, becas y convocatorias | `DataRepository.promociones` | Lista vacía a propósito |
| Testimonios de estudiantes | `DataRepository.testimonios` | Lista vacía a propósito |
| Matrícula, próxima fecha, horarios | `Program` | No se agrega hasta tener dato oficial |

Las tres primeras pantallas ya están construidas: al llenar esas listas, el contenido
aparece sin tocar ninguna pantalla.


---

## Estructura del proyecto

Dos módulos Gradle, un solo APK.

| Módulo | Qué es | Qué tiene |
|---|---|---|
| `:app` | La app institucional. Punto de entrada único | 10 módulos funcionales vigentes, catálogo, preinscripción con adjuntos, sesión, tema accesible |
| `:campo` | Biblioteca: captura de preinscripción en campo, offline-first | Room (fuente de verdad), validación R1..R8, evidencia fotográfica, `SyncManager` con envío manual, tema propio |

`:campo` nació como app independiente (`EnidAppOfflineFirst`) y se integra como
biblioteca. El punto de contacto es **un solo composable**,
`ui/screens/CampoSeccion.kt`: `:app` compone `EnidAppRaiz` del módulo y no ve ni
la base de datos, ni los modelos, ni el tema de campo. `:campo` no sabe nada de
catálogos, sesión ni preinscripción.

Las dos apps resuelven la navegación de forma distinta y ambas con razón: `:app`
usa una pila propia (`NavController`) y `:campo` un `NavHost` de
`navigation-compose`. Por eso son módulos y no carpetas. El botón atrás del
sistema lo resuelve primero el `NavHost` de campo y solo después la pila de
`:app`.

---

### Fase 20 — Módulo de captura en campo (`:campo`) — integrada 5 Oct 2026

| Fase | Nombre | Estado |
|---|---|---|
| 20 | Integración de `EnidAppOfflineFirst` como módulo `:campo` | ✅ Hecha — commit de integración |

La captura de preinscripciones offline-first queda dentro del APK como módulo
Gradle. Se entra desde el menú y se sale con "Volver a la app" o con el botón
atrás. Su diseño interno (offline-first, reglas R1..R8, evidencia en
almacenamiento interno, envío manual) está documentado en su propio `README`,
que se conserva como referencia de la parte del taller.

Lo que **no** se hizo, y sigue pendiente:

| Qué | Depende de |
|---|---|
| Restringir la entrada a `Rol.ADMINISTRATIVO` | La Fundación debe decir quién usa el módulo |
| Cifrar la base de campo y autenticar el acceso | Decisión de la Fundación (SQLCipher o equivalente) |
| `PuertaRemotaFirebase`: hoy la puerta simulada | Proyecto de Firebase RTDB y reglas de seguridad |
| Prueba manual de captura por cámara y galería | No se puede automatizar con fiabilidad por ADB |

### Fases 21 y 22 — Orden en el flujo y tokens compartidos — 5 Oct 2026

Una auditoría de las 18 pantallas encontró siete fallos que el usuario veía de verdad.
No eran de maquetación sino de estructura: la app no tenía un modelo de permisos de
navegación, y el sistema visual estaba duplicado.

**Fase 22 — orden en el flujo:**

| Qué estaba mal | Qué se hizo |
|---|---|
| El invitado llegaba a `Payments` y `Platform` por los pasos 9 y 10 de `rutaProspecto`, y veía "$180.000" y un historial de pagos **inventado** | Los pasos 5 a 10 son `enSede = true`. El invitado solo navega hasta el paso 4 |
| 8 pantallas no pasaban `tabsSegunSesion` y pintaban las 4 pestañas de estudiante a cualquiera | `AppScaffold` decide con `SesionUi.conectado`. Ninguna pantalla pasa `tabs` ya |
| `ProspectHome`, `Profile`, `Pse` y `Benefits` sin barra inferior: callejones sin salida | Las cuatro reciben `onTabNavigate` |
| **`AppScaffold` recibía `onBack` y nunca dibujaba el botón.** No había flecha de retorno en toda la app | Se dibuja, con área táctil de 48 dp y `contentDescription` |
| `PseScreen` ponía `pagado = true` y mostraba **"Pago procesado con éxito"** sin que pasara nada | `EnlacesExternos.abrirPse` devuelve si abrió; la pantalla dice la verdad en los dos casos |
| 5 pantallas abrían `context.startActivity` saltándose el interruptor | Todas pasan por `EnlacesExternos`; una prueba lo verifica sobre el código fuente |
| `$180.000` y `$830.000` escritos en 7 sitios, con dos valores distintos | `DataRepository.MONTO_MATRICULA` + `AVISO_MONTO`, que dice que es preliminar |

> **Nota (27 Oct 2026):** esta tabla es el registro histórico de la Fase 22. Ese
> mismo día se retiraron `Payments`, `Pse`, la pestaña Pagos, los montos y la
> pasarela PSE (Fase 26). Las filas se conservan para no reescribir la historia,
> pero esas piezas ya no están en el código.

**Fase 21 — tokens compartidos (` :ui `):** antes de integrar `:campo`, ese módulo
traía copia de `Color.kt`, `Type.kt` y sus métricas. Había **doce colores con el
mismo nombre y el mismo valor en dos ficheros** y seis colores iguales con nombres
distintos (`EnidGreen` / `EnidVerdeWhatsapp`, `EnidGold` / `EnidOro`,
`EnidChipDoneBg` / `EnidSincronizadoFondo`…). El módulo `:ui` deja la paleta base y
`EnidDimens` en un solo sitio; cada módulo conserva solo lo propio y mantiene sus
nombres antiguos como alias. Tres pruebas vigilan que no vuelva a duplicarse.

**Lo que queda (Fase 23):** los 13 componentes con el mismo nombre en `:app` y
`:campo` **no son copias**: ninguno tiene el cuerpo idéntico. Son dos
implementaciones que convergieron en los mismos nombres. No es limpieza, es un
refactor: hay que elegir una implementación por componente y adaptar la otra,
cambiando el aspecto de uno de los dos módulos.

---

### Fase 24 — Roles, datos desde la preinscripción y tema del Figma — 27 Oct 2026

**Roles.** Se añadió `Rol.ADMIN`. Mientras no haya un servidor de roles, un
correo que contenga `admin` entra al panel; el aviso está a la vista en el
acceso. El administrador ve **Inicio · Panel · Soporte** y un resumen de
preinscripciones, interesados, programas y cuentas locales. El estudiante
conserva **Inicio · Horario · Soporte**.

**Datos académicos.** La carrera y el año/ciclo dejaron de ser campos de texto
de la cuenta. Se eligen en la preinscripción —el ciclo, de una lista cerrada de
año actual/siguiente × primer/segundo semestre— y **Mi Cuenta** los muestra como
solo lectura desde el último registro. El tipo de documento usa un selector
compacto que solo muestra la sigla cerrada (`CC`) y el nombre completo al
desplegar; el teléfono usa el mismo patrón para el prefijo, con banderas dentro
de la lista. Se eliminó el campo **Número del ICFES**: el certificado se entrega
como documento y el modelo conserva el dato solo para leer registros antiguos.

**Tema visual.** Se tomó del Figma el juego de iconos de trazo (1.8 sobre
cuadrícula de 24, remates redondeados, sin relleno) y una tipografía única
(cuerpo 17/15/13 sp, títulos 23/20/17, rótulos 16/13/11). Se redujeron los radios
y el aire vertical de botones, campos, tarjetas y listas, y se agrandaron los
iconos internos a 20–26 dp. La verificación está en
[BITACORA.md](BITACORA.md): compilación, 72 pruebas unitarias, APK instalado y
tres arranques en `ZT322CVFC9`.

---

### Fase 25 — Sede única, redes oficiales y paleta institucional — 27 Oct 2026

**Sede.** La Fundación opera hoy una sola sede. `DataRepository.sedes` quedó con
**Sede Chía** (`Calle 13 # 9-77, Parque Ospina`), teléfonos **316 630 1033** y
**313 204 8325**, y se quitaron Sopó y Girardot del catálogo y de la oferta. El
modelo `Sede` guarda una **lista** de teléfonos: cada número se toca por separado
y abre el marcador con ese número, en vez de concatenar dos cifras en una sola
cadena que el teléfono no podría marcar.

**Redes.** Se tomaron los enlaces oficiales de `enid.edu.co`: Facebook
(`Enid.Escuela`), Instagram (`fundacionenid`) y YouTube (canal institucional).
Viven en `DataRepository` y salen por `EnlacesExternos`, así que durante las
pruebas siguen sin abrir apps externas. Los iconos son de trazo (24 × 24, 1.8,
remates redondeados) y la fila `RedesSociales` se repite al final del Inicio y de
**Nuestra Sede**.

**Paleta.** Se aplicó la entregada por la Fundación:

| Color | Uso |
|---|---|
| Rojo `#C11721` | CTAs, etiquetas, fechas, navegación activa y conversión |
| Violeta `#352F6F` | Identidad institucional y estructura |
| Azul `#4B6195` | Fondos, degradados y realces |
| Turquesa `#2BA7A2` | Progreso, lecciones completadas y estados positivos |
| Rojos/azules/violetas suaves | Fondos de chips y tarjetas, sin competir con el rojo |

El turquesa de texto usa `#176B68` para conservar contraste AA; el verde de
WhatsApp se mantiene solo en la marca WhatsApp. La verificación está en
[BITACORA.md](BITACORA.md): compilación, pruebas unitarias, APK instalado, tres
arranques y muestreo de color sobre la captura de pantalla.

---

## Entorno de compilación (offline)

| Componente | Versión / Ruta |
|---|---|
| JDK | 25 (JBR de Android Studio) |
| Gradle | 9.5.0 (wrapper en caché) |
| AGP | 9.3.1 |
| Kotlin | 2.2.10 |
| KSP | 2.2.10-2.0.2 (solo `:campo`, para Room) |
| compileSdk / targetSdk | 37 / 37 |
| minSdk | 30 |
| build-tools | 36.0.0 |
| UI | Jetpack Compose (material3 1.4.0, foundation 1.10.4, activity-compose 1.13.0, navigation-compose 2.9.0) |
| Base de datos local | Room 2.8.5 (solo `:campo`) |
| Autenticación | Firebase Authentication (correo/contraseña) — BoM 34.2.0 / firebase-auth 24.0.1 |

Dos ajustes de `gradle.properties` que no son opcionales:

- `org.gradle.offline=true` — todo se compila sin red.
- `android.disallowKotlinSourceSets=false` — KSP registra las carpetas generadas
  de Room con el DSL `kotlin.sourceSets`, que AGP 9 con Kotlin integrado rechaza.

Y uno en `settings.gradle.kts`: `com.android.library` se resuelve por
`resolutionStrategy.eachPlugin` apuntando a `com.android.tools.build:gradle`,
porque su artefacto marcador no está en la caché local y sin esto habría que
poner `org.gradle.offline` en `false` solo para descargar dos kilobytes.

### Comandos

```bash
export JAVA_HOME=/ruta/al/jbr/de/android-studio

./gradlew :app:assembleDebug          # APK (incluye :campo)
./gradlew :app:testDebugUnitTest      # 70 pruebas JVM de :app
./gradlew :campo:testDebugUnitTest    # 34 pruebas JVM de :campo
./gradlew :campo:connectedDebugAndroidTest   # 10 pruebas de :campo en dispositivo
```

## Definición de hecho (DoD)

- La app **compila** en modo debug con `gradle :app:assembleDebug`. ✔
- Cubre **10 de los 12 requerimientos** en pantallas navegables; 09 y 10 se retiraron el 27 Oct. ✔
- Tema de **alto contraste**, botones y tipografía grandes (accesible 60+). ✔
- Bitácora y plan de desarrollo actualizados. ✔
- El módulo de campo está integrado y sus 4 pantallas son alcanzables desde
  el menú. ✔
- **104 pruebas JVM en verde (70 de `:app` y 34 de `:campo`).** Las 11 de dispositivo se ejecutaron en la iteración de integración; no se re-ejecutaron en esta. ✔

## Entregables actuales

| Artefacto | Ubicación |
|---|---|
| Proyecto Android (Kotlin + Compose) | `FundacionEnidApp/` |
| App institucional | `app/src/main/java/com/fundacion/enid/` |
| Módulo de captura en campo | `campo/src/main/java/com/fundacion/enid/campo/` |
| APK debug (41 MB) | `app/build/outputs/apk/debug/app-debug.apk` |
| Plan de desarrollo y fases | `PLANDESARROLLO.md` |
| Bitácora de trabajo | `BITACORA.md` |
| Diagrama de navegación | `DIAGRAMA_NAVEGACION.md` |
| Imágenes de referencia de la presentación | `docs/imagenes/` |

## Cómo abrir y ejecutar

1. Abrir con **Android Studio**: `File → Open` y seleccionar la carpeta
   `FundacionEnidApp`.
2. Esperar la sincronización de Gradle (usa el Gradle 9.5.0 ya descargado).
   Aparece un segundo módulo en el panel, `:campo`.
3. Ejecutar la configuración **`app`** en un dispositivo o emulador. **No** se
   ejecuta `campo`: es una biblioteca, no tiene `Activity` lanzadora.
4. Flujo inicial: **Iniciar Sesión** o **Ingresar como Invitado**. La
   bienvenida ya no ofrece registro: la cuenta se crea desde el acceso.
5. Módulo de campo: **Inicio → Más servicios → Captura en campo** (con sesión)
   o **Inicio invitado → Equipo de campo** (sin sesión).

> Para probar sin emulador: `adb install app/build/outputs/apk/debug/app-debug.apk`
>
> El APK ya incluye `:campo`. No hay nada aparte que instalar ni que mantener
> sincronizado.

---

### Fase 26 — Retiro completo del sistema de pagos — 27 Oct 2026

A petición de la Fundación se eliminó el sistema de gestión de pagos **entero**,
no solo su entrada visible:

| Qué se retiró | Dónde estaba |
|---|---|
| Pantalla **Mis Pagos** | `PaymentsScreen.kt` (borrado) |
| Pantalla **Pago por PSE** y sus bancos | `PseScreen.kt` (borrado) |
| Rutas `Screen.Payments` y `Screen.Pse` | `navigation/Screen.kt`, `AppRoot.kt` |
| Pestaña **Pagos** de la barra inferior | `EnidTab.PAGOS` en `Components.kt` |
| Modelos `PaymentNotice` y `EstadoPago` | `model/Models.kt` |
| Historial y alertas de pago | `DataRepository.pagos` |
| Pasarela PSE | `DataRepository.PSE_URL`, `EnlacesExternos.abrirPse` |
| Montos y avisos de matrícula | `MONTO_MATRICULA`, `montoMatriculaTexto`, `AVISO_MONTO` |
| Chip de deuda y tarjeta de crédito | `EnidChipWarnBg/Text`, `StatusChip`, `EnidIcons.VPagos` |
| Categoría **PAGOS** del FAQ | `FaqScreen.kt` |

La tarjeta **Pagos** del Inicio y el texto “horario, pagos y soporte” del Home
del invitado también desaparecieron. El **Plan Referidos** se conserva como
calculadora de descuento, pero ya no muestra precios ni montos: solo el
porcentaje. La pregunta frecuente sobre cómo pagar ahora responde que el pago
se coordina con un asesor o en la sede y que **la app no procesa pagos ni
recibe datos bancarios**.

Verificación: `:app:compileDebugKotlin`, `:app:testDebugUnitTest` (70 pruebas)
y `:app:assembleDebug` en verde; APK instalado y app abierta **3 veces** en
`ZT322CVFC9`. Con `uiautomator` se comprobó que el Inicio y la barra inferior
solo muestran **Inicio · Horario · Soporte**, que no queda ningún texto de
pagos/PSE y que el Plan Referidos calcula el porcentaje sin importes. Una prueba
(`el sistema de pagos no vuelve al codigo`) vigila que las piezas retiradas no
reaparezcan en `app/src/main/java`.


---

### Fase 27 — Aprender, Agenda y Perfil, y el Inicio del Figma — 27 Oct 2026

Se implementaron los tres módulos del Figma y se integraron en la barra inferior
sin romper las reglas por rol:

| Rol | Pestañas |
|---|---|
| Estudiante | **Inicio · Aprender · Agenda · Perfil** |
| Administrador | **Inicio · Panel · Soporte** |
| Invitado | **Inicio · Soporte** |

**Aprender.** El curso activo no es un temario aparte: son las cinco primeras
asignaturas distintas del horario real de la Fundación. Cada lección muestra su
duración y aula, y se marca al terminarla. El avance se guarda en
`SharedPreferences` por correo (`DataRepository.leccionesCompletadas`), de modo
que el porcentaje, la barra y la insignia sobreviven a la navegación y a reabrir
la app, sin heredarse entre cuentas del mismo teléfono.

**Agenda.** La franja muestra los próximos cinco días calculados con
`LocalDate`; al elegir una fecha se listan las clases de ese día del horario. Si
no hay clases, lo dice. La tarjeta **Próxima clase** sale de
`AgendaClases.proxima`, que descarta las clases de hoy ya terminadas y busca en
los días siguientes; Inicio usa la misma función para no dar una respuesta
distinta. El botón **VER HORARIO Y PLAN DE ESTUDIOS** abre
`DailySchedule`, que conserva el horario completo y el plan de estudios y deja
de ocupar una pestaña propia.

**Perfil.** Reemplaza a “Mi Cuenta” en la barra. Muestra avatar, estadísticas
reales (programa, horas de las lecciones marcadas y logros), tres insignias,
datos de la cuenta desplegables, ajustes y cierre de sesión. La carrera, el
año/ciclo, el documento y el teléfono siguen siendo de solo lectura y salen de
la preinscripción más reciente; los valores antiguos de la cuenta son solo
respaldo. **Privacidad** explica qué se guarda y recuerda que la app no procesa
pagos ni datos bancarios. El interruptor de notificaciones guarda una
preferencia local, sin prometer avisos push que la app aún no envía.

**Inicio.** Se reorganizó con la estructura del Figma: saludo con fecha, tarjeta
del curso en progreso, filtros y tarjetas de programas, próxima clase, accesos
de servicio, respaldo institucional, promociones y redes sociales. El Home del
administrador conserva su tarjeta de gestión y sus accesos. La cabecera
(`EnidTabHeader`) usa la marca y la campana; la barra inferior usa indicador
rojo, iconos de trazo y violeta para la pestaña activa.

Verificación: `:app:compileDebugKotlin`, `:app:testDebugUnitTest` (**78
pruebas**) y `:app:assembleDebug` en verde. APK instalado en `ZT322CVFC9` y app
abierta **3 veces**. Con `uiautomator` se recorrieron las cuatro pestañas, se
marcó una lección (0% → 20%), se abrió Mi Horario desde Agenda, se desplegaron
los datos del Perfil y se comprobó Privacidad; también se verificaron el Home
del invitado y el del administrador.

Pendiente de esta fase: los logros y las horas son un reflejo del marcado
manual, no de una plataforma de cursos conectada; las notificaciones siguen sin
servicio push.
