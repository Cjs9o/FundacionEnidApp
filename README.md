# Fundación ENID App

Aplicación móvil **Android nativo (Kotlin + Jetpack Compose)** para la
**Fundación Escuela Nacional de Innovación y Desarrollo en Liderazgo (Fundación ENID)**.

Diseñada con una **interfaz accesible y adaptada a adultos mayores (60+)**: tipografía
grande, botones amplios y alto contraste. Cubre los módulos funcionales de la
institución (autenticación, oferta académica, preinscripciones, FAQ, sedes,
horarios, plataforma educativa, etc.).

---

## Estado

**Última iteración: 27 Oct** — **Aprender, Agenda y Perfil** en la barra
inferior, con el Inicio rediseñado según el Figma, sobre la sede única, las
redes oficiales y la paleta institucional.

### Integración del módulo de captura en campo (`:campo`)

La captura de preinscripciones **offline-first** —la promotora recorre el
territorio sin señal y todo se guarda primero en el teléfono— se integró como
**módulo Gradle `:campo`**, no como carpeta dentro de `:app`. Hay un solo APK,
una sola `Activity` y un solo punto de entrada.

Se llega desde **Inicio → Más servicios → Captura en campo** (estudiante) o
**Conoce ENID → Equipo de campo**, y se vuelve con el botón "Volver a la
app" de la cabecera o con el botón atrás del teléfono.

| Qué | Dónde |
|---|---|
| Código del módulo | `campo/src/main/java/com/fundacion/enid/campo/` |
| Único punto de contacto con `:app` | `ui/screens/CampoSeccion.kt` |
| Base local (Room) | `campo/.../data/local/` — fuente de verdad, sin red |
| Sincronización manual | `campo/.../data/sync/` — `PuertaRemota` (hoy simulada) |
| Pruebas | 34 en JVM + 10 en dispositivo |

Módulo y no carpeta porque las dos apps resuelven la navegación de forma distinta
y ambas con razón: `:app` usa su pila propia y `:campo` un `NavHost` de
`navigation-compose`. Como módulos, cada una resuelve lo suyo y se conectan en
un solo punto. El detalle completo, incluidos los cuatro problemas que hubo que
resolver al integrar, está en [BITACORA.md](BITACORA.md).

### Módulo `:ui` — tokens compartidos

`:app` y `:campo` comparten un tercer módulo con la **paleta base** y las
**medidas** del sistema visual. No contiene componentes ni lógica de negocio.

| Qué | Dónde |
|---|---|
| Paleta base (rojo `#C11721`, violeta `#352F6F`, azul `#4B6195`, turquesa `#2BA7A2`) | `ui/src/main/java/com/fundacion/enid/ui/shared/Color.kt` |
| Colores secundarios y de éxito | `ui/src/main/java/com/fundacion/enid/ui/shared/Green.kt` |
| Medidas (`EnidDimens`) | `ui/src/main/java/com/fundacion/enid/ui/shared/Dimens.kt` |
| Lo propio de `:app` | `app/.../ui/theme/Color.kt` — con alias hacia los compartidos |
| Lo propio de `:campo` | `campo/.../ui/theme/Color.kt` — ámbar de pendiente, rojo de error |

Nace porque al integrar `:campo` ese módulo traía copia de `Color.kt`: doce
colores con el mismo nombre y el mismo valor en dos ficheros, más seis colores
iguales con nombres distintos (`EnidGreen` / `EnidVerdeWhatsapp`,
`EnidGold` / `EnidOro`, `EnidChipDoneBg` / `EnidSincronizadoFondo`). Con dos
copias en un mismo APK, cambiar un color en un módulo deja el otro desalineado
sin aviso. Tres pruebas lo vigilan.

### Sede y redes oficiales

La Fundación opera hoy una sola sede: **Sede Chía**, `Calle 13 # 9-77,
Parque Ospina, Chía, Cundinamarca`, teléfonos **316 630 1033** y
**313 204 8325**. Cada número se marca por separado desde **Nuestra Sede**.

Al final del Inicio y de **Nuestra Sede** hay accesos a las redes oficiales:
Facebook (`Enid.Escuela`), Instagram (`fundacionenid`) y YouTube (canal
institucional). Los enlaces viven en `DataRepository` y se abren con
`EnlacesExternos`, que durante las pruebas está deshabilitado.

### Roles y datos de la cuenta

| Rol | Cómo se reconoce | Pestañas |
|---|---|---|
| Estudiante | Cuenta normal | Inicio · Aprender · Agenda · Perfil |
| Administrador | `Rol.ADMIN` (correo con `admin`) | Inicio · Panel · Soporte |
| Invitado | Sin cuenta | Inicio · Conoce ENID · Inscríbete · Soporte |

El administrador ve el resumen de preinscripciones, interesados, programas y
cuentas locales en **Panel**, sin los módulos de estudiante.

### Camino del invitado

El invitado tiene cuatro pestañas: **Inicio · Conoce ENID · Inscríbete · Soporte**.

| Pestaña | Qué muestra |
|---|---|
| **Inicio** | Bienvenida, los dos caminos y el recordatorio del programa que le interesa |
| **Conoce ENID** | Trayectoria, titulación, sede de Chía, programas, preguntas frecuentes y redes |
| **Inscríbete** | Los diez pasos, los atajos para explorar programas y dejar datos, y promociones |
| **Soporte** | Preguntas frecuentes |

La información institucional y el proceso de inscripción antes compartían una
sola pantalla larga. Ahora cada uno tiene su pestaña, sin inventar contenido: los
pasos que se resuelven con el asesor siguen marcados y la app no procesa pagos.

### Aprender, Agenda y Perfil

| Módulo | Qué muestra | De dónde sale |
|---|---|---|
| **Aprender** | Curso activo, porcentaje, cinco lecciones y una insignia por avance | Las asignaturas del horario real; el avance se guarda por correo en el teléfono |
| **Agenda** | Franja de los próximos cinco días, próxima clase real y clases de la fecha | `DataRepository.horarioClases`; descarta las clases ya terminadas y no inventa eventos |
| **Perfil** | Avatar, estadísticas, logros, datos de la cuenta, ajustes y cierre de sesión | La preinscripción más reciente manda; los valores antiguos de la cuenta son respaldo |

El detalle del horario y el plan de estudios siguen existiendo en **Mi Horario**,
pero ya no ocupan una pestaña: se abren desde **Agenda**. **Perfil** reemplaza a
la antigua “Mi Cuenta” en la barra y conserva sus accesos (certificados,
notificaciones, ayuda, privacidad, panel de administración y cierre de sesión).

El **Inicio** del estudiante se reorganizó con la estructura del Figma: saludo
con fecha, tarjeta del curso en progreso, filtros y tarjetas de programas,
próxima clase, accesos de servicio, respaldo institucional, promociones y redes
sociales. La barra inferior y las cabeceras usan los iconos de trazo del Figma;
la campana de la cabecera informa que no hay notificaciones nuevas en vez de
prometer avisos que la app todavía no envía.

La **carrera/programa** y el **año o ciclo** no se escriben en la cuenta: se
eligen en la preinscripción (lista cerrada de ciclos) y de ahí pasan a **Perfil**
como datos de solo lectura. El tipo de documento se muestra como sigla
compacta (`CC`, `CE`, `TI`, `NIT`) y el teléfono lleva un selector de prefijo
con banderas. El perfil conserva los valores antiguos de la cuenta solo como
respaldo cuando todavía no hay preinscripción.

### Sistema visual tomado del Figma

| Pieza | Decisión |
|---|---|
| Iconos | Trazos de 1.8 sobre cuadrícula de 24, remates y esquinas redondeadas, sin relleno (`EnidIcons.kt`) |
| Tipografía | Una sola escala: cuerpo 17/15/13 sp, títulos 23/20/17, rótulos 16/13/11 |
| Cajas | Radios de 10–16 dp, botones de 50 dp, campos de 48 dp y selectores compactos de 90 × 52 dp |
| Aire vertical | Separadores de 12–14 dp en formularios; tarjetas con borde de 1 dp en vez de 1.5–2 dp |
| Iconos internos | 20–26 dp en tarjetas, barra inferior y botones, para que se lean sin agrandar las cajas |

El detalle de los cambios y las pruebas de la última pasada está en
[BITACORA.md](BITACORA.md).

### Reglas de navegación que no hay que volver a romper

| Regla | Dónde se aplica |
|---|---|
| Las pestañas dependen de la sesión, no de la pantalla | `AppScaffold` lee `SesionUi`; **ninguna** pantalla pasa `tabs` |
| Toda salida a otra app pasa por `EnlacesExternos` | y cada función devuelve si de verdad abrió algo |
| La app nunca afirma que ocurrió algo que no verificó | `EnlacesExternos` devuelve si la app externa se abrió; las pantallas usan ese valor |
| Ningún dato institucional se inventa en una pantalla | lo que la Fundación no ha confirmado va con su aviso al lado |

### Fases 12 y 13 — adjuntos de documentos

El paso 2 de la preinscripción deja **adjuntar el archivo de cada documento**
desde el teléfono (selector del sistema con permiso persistente), con el nombre
real del archivo y no un `content://`. El formato elegido filtra de verdad el
selector, desmarcar la casilla borra el archivo y suelta el permiso, y la
elección sobrevive al cambio de paso. 54 pruebas en verde.

### Iteración anterior (29 Sep): plan de captación completo

- **Oferta académica potente.** Los 67 programas se clasifican en 9 áreas; el catálogo
  tiene buscador y filtros, y el detalle muestra información, qué aprenderás y requisitos,
  con "Quiero estudiar este programa", "Me interesa" y los CTAs de asesor y WhatsApp.
- **"¿Qué puedo estudiar?"** — cuestionario de 5 preguntas con recomendación ponderada
  por área, tipo de programa y campo laboral.
- **Dos caminos de Home.** "Soy estudiante" y "Quiero estudiar en ENID", con los 10 pasos
  del proceso de admisión a la vista y la barra del **asesor siempre visible**.
- **Promociones y testimonios.** Pantallas listas; la Fundación aún no ha entregado el
  contenido, y **no se inventaron** descuentos ni citas de estudiantes.
- **Seguimiento de interesados.** "Me interesa" guarda la decisión y "Mis interesados"
  resume el siguiente paso real y los datos ya dejados.

Durante las pruebas **no se abrió ninguna app externa**: todo eso pasa por
`EnlacesExternos`, que está en `habilitados = false`.

### Qué falta

- **De la Fundación**: competencias de 42 de los 67 programas, confirmación de
  que Chía seguirá siendo la única sede, promociones, testimonios, correos y la
  URL definitiva de la plataforma.
- **Fases 14, 15, 17, 18 y 19**: las pantallas que abren intents ignorando
  `EnlacesExternos.habilitados = false`, la navegación de invitado, los
  `StringSet` sin orden, los tamaños de `SmallButton` y de las etiquetas de la
  barra, y las pruebas que faltan.
- **Sistema de pagos:** retirado por completo el 27 Oct 2026 (pantallas,
  pasarela PSE, alertas, pestaña, montos y datos). La app no procesa pagos; el
  pago de la matrícula se coordina con un asesor o en la sede.
- **Del módulo de campo**: restringirlo a `Rol.ADMINISTRATIVO`, cifrar la base
  y autenticar el acceso, conectar Firebase RTDB y probar a mano la captura por
  cámara.

El detalle y el orden, con su dependencia, están en
[PLANDESARROLLO.md](PLANDESARROLLO.md) y [BITACORA.md](BITACORA.md).

## Documentación del proyecto

| Documento | Descripción |
|---|---|
| [PLANDESARROLLO.md](PLANDESARROLLO.md) | Plan, fases, requerimientos, entorno de compilación y definición de hecho. |
| [BITACORA.md](BITACORA.md) | Registro cronológico del trabajo: decisiones, pantallas y pruebas de compilación. |
| [DIAGRAMA_NAVEGACION.md](DIAGRAMA_NAVEGACION.md) | Mapa de pantallas y conexiones, incluido el módulo de campo. |
| [docs/imagenes/](docs/imagenes/) | Imágenes de referencia de la presentación del proyecto. |

---

## Guía rápida

- **Abrir y ejecutar:** ver [PLANDESARROLLO.md](PLANDESARROLLO.md) → *Cómo abrir y ejecutar*.
- **Requerimientos funcionales y estados:** ver [PLANDESARROLLO.md](PLANDESARROLLO.md) → *Requerimientos funcionales* y *Entregables actuales*.
- **Historial del trabajo y compilación:** ver [BITACORA.md](BITACORA.md).