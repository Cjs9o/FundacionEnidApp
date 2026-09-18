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

## Requerimientos funcionales (12 módulos)

| # | Requerimiento | Descripción resumida |
|---|---|---|
| 01 | Interfaz Adaptada y Accesible (UI/UX) | Tipografía legible, botones grandes, alto contraste para adultos mayores |
| 02 | Registro e Inicio de Sesión | Perfil + autenticación (nombre, correo, rol); control de la comunidad |
| 03 | Formulario y Gestión de Preinscripciones | Datos del aspirante + programa de interés, almacenados para consulta institucional |
| 04 | Catálogo Institucional y Oferta Académica | Programas técnicos, diplomados y cursos con pensum, perfil ocupacional y sedes |
| 05 | Sedes e Información de Contacto | Sedes **Chía y Sopó**; llamada telefónica y ubicación en Google Maps / Waze |
| 06 | Preguntas Frecuentes (FAQ) | Lista desplegable: admisiones, pagos y certificados |
| 07 | Cronograma General del Plan de Estudios | Fechas de inicio/fin de cada asignatura |
| 08 | Horario Diario de Clases | Vista diaria de las clases del estudiante |
| 09 | Alertas y Notificaciones de Pago | Recordatorios de fechas límite y montos de matrícula (evitar mora) |
| 10 | Redireccionamiento de Pagos (PSE) | Acceso a la pasarela oficial de pagos PSE y bancos autorizados |
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
- [x] **Sedes y Contacto**: Chía y Sopó, botones de llamada
      (`tel:`) e integración Google Maps / Waze (URLs geo+llamadas).
- [x] **Preguntas Frecuentes (FAQ)** con acordeón: admisiones, pagos, certificados.

### Fase 5 — Módulos de estudiante
- [x] **Preinscripción**: formulario (datos personales + programa de interés),
      validación y confirmación; datos guardados localmente.
- [x] **Cronograma del plan de estudios**: fechas por asignatura.
- [x] **Horario diario**: selección de día y vista de clases.
- [x] **Alertas de Pago**: listado de pagos pendientes + estado.
- [x] **Pagos PSE**: información + redirección a la pasarela
      oficial (abrir enlace externo).
- [x] **Calculadora de Beneficios**: descuento por referidos (regla publicada
      por la institución, simulada).
- [x] **Plataforma Educativa**: enlace directo de acceso.

### Fase 6 — Pruebas y depuración
- [x] Compilar/build al menos **5 veces**, corregir errores de compilación.
- [x] Revisar navegación, accesibilidad y manejo de estados vacíos.
- [x] Verificación final: `gradle :app:assembleDebug` OK (5/5 pruebas, APK generado).

---

## Entorno de compilación (offline)

| Componente | Versión / Ruta |
|---|---|
| JDK | 25 (JBR de Android Studio) |
| Gradle | 9.5.0 (wrapper en caché) |
| AGP | 9.3.1 |
| Kotlin | 2.2.10 |
| compileSdk / targetSdk | 37 / 37 |
| minSdk | 30 |
| build-tools | 36.0.0 |
| UI | Jetpack Compose (material3 1.4.0, foundation 1.10.4, activity-compose 1.13.0) |
| Autenticación | Firebase Authentication (correo/contraseña) — BoM 34.2.0 / firebase-auth 24.0.1 |

## Definición de hecho (DoD)

- La app **compila** en modo debug con `gradle :app:assembleDebug`. ✔
- Cubre los **12 requerimientos** en pantallas navegables. ✔
- Tema de **alto contraste**, botones y tipografía grandes (accesible 60+). ✔
- Bitácora y plan de desarrollo actualizados. ✔

## Entregables actuales

| Artefacto | Ubicación |
|---|---|
| Proyecto Android (Kotlin + Compose) | `FundacionEnidApp/` |
| Código fuente | `app/src/main/java/com/fundacion/enid/` |
| APK debug (12,2 MB) | `app/build/outputs/apk/debug/app-debug.apk` |
| Plan de desarrollo y fases | `PLANDESARROLLO.md` |
| Bitácora de trabajo | `BITACORA.md` |
| Imágenes de referencia de la presentación | `docs/imagenes/` |

## Cómo abrir y ejecutar

1. Abrir con **Android Studio**: `File → Open` y seleccionar la carpeta
   `FundacionEnidApp`.
2. Esperar la sincronización de Gradle (usa el Gradle 9.5.0 ya descargado).
3. Ejecutar `app` en un dispositivo o emulador (o instalar `app-debug.apk`).
4. Flujo inicial: **Regístrate** (nombre, correo, rol, contraseña), luego
   inicia sesión para entrar al menú principal con los 12 módulos.

> Para probar sin emulador: `adb install app/build/outputs/apk/debug/app-debug.apk`