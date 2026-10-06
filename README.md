# Fundación ENID App

Aplicación móvil **Android nativo (Kotlin + Jetpack Compose)** para la
**Fundación Escuela Nacional de Innovación y Desarrollo en Liderazgo (Fundación ENID)**.

Diseñada con una **interfaz accesible y adaptada a adultos mayores (60+)**: tipografía
grande, botones amplios y alto contraste. Cubre los **12 módulos funcionales** de la
institución (autenticación, oferta académica, preinscripciones, pagos PSE, FAQ, sedes,
horarios, etc.).

---

## Estado

**Última iteración: 5 Oct** — **módulo de captura en campo integrado** y las
fases 12 y 13 cerradas.

### Integración del módulo de captura en campo (`:campo`)

La captura de preinscripciones **offline-first** —la promotora recorre el
territorio sin señal y todo se guarda primero en el teléfono— se integró como
**módulo Gradle `:campo`**, no como carpeta dentro de `:app`. Hay un solo APK,
una sola `Activity` y un solo punto de entrada.

Se llega desde **Inicio → Más servicios → Captura en campo** (estudiante) o
**Inicio invitado → Equipo de campo**, y se vuelve con el botón "Volver a la
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
| Paleta base (12 colores de la marca) | `ui/src/main/java/com/fundacion/enid/ui/shared/Color.kt` |
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

### Reglas de navegación que no hay que volver a romper

| Regla | Dónde se aplica |
|---|---|
| Las pestañas dependen de la sesión, no de la pantalla | `AppScaffold` lee `SesionUi`; **ninguna** pantalla pasa `tabs` |
| Toda salida a otra app pasa por `EnlacesExternos` | y cada función devuelve si de verdad abrió algo |
| La app nunca afirma que ocurrió algo que no verificó | `PseScreen` usa el valor devuelto, no un `pagado = true` |
| El monto de matrícula sale de un solo sitio | `DataRepository.MONTO_MATRICULA`, y se etiqueta como preliminar |
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

- **De la Fundación** (Fase 16, bloquea los montos del PSE): valor de matrícula,
  competencias de 42 de los 67 programas, sedes reales (el catálogo menciona
  Girardot y `repository.sedes` solo tiene Chía y Sopó), promociones,
  testimonios, teléfonos, correos y URLs de plataforma y PSE.
- **Fases 14, 15, 17, 18 y 19**: cerrar el flujo de pago (hoy "Pago procesado"
  sin pasarela), las cuatro pantallas que abren intents ignorando
  `EnlacesExternos.habilitados = false`, la navegación de invitado, las
  contraseñas en texto plano y los `StringSet` sin orden, los tamaños de
  `SmallButton` y de las etiquetas de la barra, y las pruebas que faltan.
- **Del módulo de campo**: restringirlo a `Rol.ADMINISTRATIVO`, cifrar la base
  y autenticar el acceso, conectar Firebase RTDB y probar a mano la captura por
  cámara.

El detalle y el orden, con su dependencia, están en
[PLANDESARROLLO.md](PLANDESARROLLO.md) y [BITACORA.md](BITACORA.md).

## Documentación del proyecto

| Documento | Descripción |
|---|---|
| [PLANDESARROLLO.md](PLANDESARROLLO.md) | Plan, fases, requerimientos (12 módulos), entorno de compilación y definición de hecho. |
| [BITACORA.md](BITACORA.md) | Registro cronológico del trabajo: decisiones, pantallas y pruebas de compilación. |
| [DIAGRAMA_NAVEGACION.md](DIAGRAMA_NAVEGACION.md) | Mapa de pantallas y conexiones, incluido el módulo de campo. |
| [docs/imagenes/](docs/imagenes/) | Imágenes de referencia de la presentación del proyecto. |

---

## Guía rápida

- **Abrir y ejecutar:** ver [PLANDESARROLLO.md](PLANDESARROLLO.md) → *Cómo abrir y ejecutar*.
- **Requerimientos funcionales y estados:** ver [PLANDESARROLLO.md](PLANDESARROLLO.md) → *Requerimientos funcionales* y *Entregables actuales*.
- **Historial del trabajo y compilación:** ver [BITACORA.md](BITACORA.md).