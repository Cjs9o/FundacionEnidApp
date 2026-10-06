# Diagrama de navegación — Fundación ENID (Android)

Mapa de cómo se conectan las pantallas entre sí. Todo sale del código, no de
suposiciones: `navigation/Screen.kt` define las rutas, `ui/screens/AppRoot.kt`
decide qué se dibuja y cada pantalla declara a dónde lleva sus botones.

- **Rutas**: `app/src/main/java/com/fundacion/enid/navigation/Screen.kt`
- **Enrutador**: `app/src/main/java/com/fundacion/enid/ui/screens/AppRoot.kt`
- **Pila**: `app/src/main/java/com/fundacion/enid/navigation/NavController.kt`
- **Barra inferior**: `EnidTab` en `ui/components/Components.kt`

---

## 1. Mapa general (mermaid)

```mermaid
flowchart TD
    subgraph PRE["Pre-login"]
        WELCOME["Welcome<br/>Bienvenida"]
        LOGIN["Login<br/>Auth"]
    end

    subgraph CAPTACION["Captación (sin cuenta)"]
        PHOME["ProspectHome<br/>Inicio invitado"]
        FINDER["ProgramFinder<br/>¿Qué puedo estudiar?"]
        INTERESES["Intereses<br/>Mis interesados / Resumen"]
    end

    subgraph ESTUDIANTE["Estudiante"]
        HOME["Home<br/>Inicio"]
        SCHEDULE["DailySchedule<br/>Mis Clases"]
        PAYMENTS["Payments<br/>Pagos"]
        PSE["Pse<br/>Pago por PSE"]
        BENEFITS["Benefits<br/>Calculadora"]
        PLATFORM["Platform<br/>Plataforma educativa"]
        PROFILE["Profile<br/>Mi Cuenta"]
    end

    subgraph OFERTA["Oferta e institución"]
        CATALOG["Catalog<br/>Oferta académica"]
        DETAIL["ProgramDetail<br/>Detalle del programa"]
        PREINSC["Preinscription<br/>Preinscripción 1-2-3"]
        LOCATIONS["Locations<br/>Sedes y contacto"]
        FAQ["Faq<br/>Preguntas frecuentes"]
    end

    WELCOME -->|Iniciar sesión| LOGIN
    WELCOME -->|"Ingresar como invitado"| PHOME

    LOGIN -->|Acceso válido| HOME
    LOGIN -->|Invitado| PHOME

    PHOME --> FINDER
    PHOME --> INTERESES
    PHOME --> CATALOG
    PHOME --> PREINSC
    PHOME --> LOCATIONS
    PHOME --> FAQ
    PHOME -->|"Volver al inicio (logo)"| WELCOME

    HOME --> SCHEDULE
    HOME --> PAYMENTS
    HOME --> PLATFORM
    HOME --> PROFILE
    HOME --> CATALOG
    HOME --> LOCATIONS
    HOME --> PREINSC
    HOME --> FAQ
    HOME --> BENEFITS
    HOME --> INTERESES

    CATALOG --> DETAIL
    CATALOG --> FINDER
    FINDER --> DETAIL
    FINDER --> PREINSC
    DETAIL --> PREINSC
    INTERESES --> DETAIL
    INTERESES --> PREINSC
    INTERESES -->|"Sin programa elegido"| CATALOG
    PAYMENTS --> PSE
    PROFILE -->|"Cerrar sesión"| WELCOME

    subgraph CAMPO["Equipo de campo — módulo :campo (NavHost propio)"]
        CAMPO_RESUMEN["Resumen de jornada<br/>contadores + red + SINCRONIZAR"]
        CAMPO_LISTADO["Listado<br/>4 filtros"]
        CAMPO_CAPTURA["Captura<br/>nueva / edición"]
        CAMPO_DETALLE["Detalle<br/>evidencia"]
    end

    PHOME --> CAMPO
    HOME --> CAMPO
    CAMPO_RESUMEN --> CAMPO_LISTADO
    CAMPO_RESUMEN --> CAMPO_CAPTURA
    CAMPO_LISTADO --> CAMPO_DETALLE
    CAMPO_LISTADO --> CAMPO_CAPTURA
    CAMPO_DETALLE --> CAMPO_CAPTURA
    CAMPO_RESUMEN -->|"Volver a la app"| PHOME
```

> El módulo `:campo` es una **hoja con grafo propio**: dentro, `NavHost` de
> `navigation-compose` maneja las cuatro pantallas y el botón atrás del sistema
> lo resuelve primero a él. Solo cuando la pila interna queda en el resumen, el
> retroceso pasa a la pila de `:app` y vuelve al Home. Por eso `Campo` no lleva
> parámetros y `Screen.kt` no conoce las cuatro rutas internas.

## 2. Rutas declaradas

| Ruta | Pantalla | Archivo | Recibe datos |
|------|----------|---------|--------------|
| `Welcome` | Bienvenida | `WelcomeScreen.kt` | — |
| `Login` | Acceso / registro | `AuthScreen.kt` | — |
| `Home` | Inicio (estudiante) | `HomeScreen.kt` | — |
| `ProspectHome` | Inicio (invitado) | `ProspectHomeScreen.kt` | — |
| `Catalog` | Oferta académica | `CatalogScreen.kt` | — |
| `ProgramDetail` | Detalle de programa | `ProgramDetailScreen.kt` | `programId: Int` |
| `ProgramFinder` | ¿Qué puedo estudiar? | `ProgramFinderScreen.kt` | — |
| `Intereses` | Mis interesados | `InteresesScreen.kt` | — |
| `Locations` | Sedes y contacto | `LocationsScreen.kt` | — |
| `Faq` | Preguntas frecuentes | `FaqScreen.kt` | — |
| `Profile` | Mi cuenta | `ProfileScreen.kt` | — |
| `Preinscription` | Preinscripción | `PreinscriptionScreen.kt` | `programaId: Int?` |
| `DailySchedule` | Mis clases | `DailyScheduleScreen.kt` | — |
| `Payments` | Pagos | `PaymentsScreen.kt` | — |
| `Benefits` | Calculadora de beneficios | `BenefitsCalculatorScreen.kt` | — |
| `Platform` | Plataforma educativa | `PlatformScreen.kt` | — |
| `Pse` | Pago por PSE | `PseScreen.kt` | — |
| `Campo` | Módulo de captura en campo | `CampoSeccion.kt` → `campo/ui/EnidAppRaiz.kt` | — |

## 3. Tabla de conexiones (pantalla → destinos)

| Desde | Botón / zona | Destino | Cómo |
|-------|--------------|---------|-----|
| Welcome | Iniciar sesión | `Login` | `navegar` |
| Welcome | Ingresar como invitado | `ProspectHome` | `reemplazarTodo` |
| Login | Acceso válido | `Home` | `reemplazarTodo` |
| Login | Invitado | `ProspectHome` | `reemplazarTodo` |
| ProspectHome | ¿Qué puedo estudiar? | `ProgramFinder` | `navegar` |
| ProspectHome | Ver resumen (⭐ **solo si hay intereses guardados**) | `Intereses` | `navegar` |
| ProspectHome | Oferta | `Catalog` | `navegar` |
| ProspectHome | Ver programas / Dejar mis datos | `Catalog`, `Preinscription(null)` | `navegar` |
| ProspectHome | Preinscripción | `Preinscription(null)` | `navegar` |
| ProspectHome | Sedes y contacto / Preguntas frecuentes | `Locations`, `Faq` | `navegar` |
| ProspectHome | **"Tu camino"**: pasos 1 a 4 | `Locations`, `Catalog`, `ProgramFinder`, `Preinscription(null)` | `navegar` |
| ProspectHome | **"Tu camino"**: pasos 5 a 10 | **ninguno** — son `enSede = true` | — |
| ProspectHome | ¿Ya eres estudiante ENID? | `Welcome` | `reemplazarTodo` |
| Home | Mis Clases | `DailySchedule` | `navegar` |
| Home | Pagos | `Payments` | `navegar` |
| Home | Plataforma Educativa (tarjeta y enlace) | `Platform` | `navegar` |
| Home | Mi Cuenta / Mi Perfil | `Profile` | `navegar` |
| Home | Más servicios → Oferta / Preinscripción / Calculadora | `Catalog`, `Preinscription(null)`, `Benefits` | `navegar` |
| Home | Ver resumen (⭐ **solo si hay intereses guardados**) | `Intereses` | `navegar` |
| ProspectHome | Equipo de campo | `Campo` | `navegar` |
| Home | Más servicios → Captura en campo | `Campo` | `navegar` |
| Campo | Resumen → Volver a la app / botón atrás | `ProspectHome` o `Home` | `retroceder` |
| Catalog | Tarjeta de programa | `ProgramDetail(id)` | `navegar` |
| Catalog | ¿Qué puedo estudiar? | `ProgramFinder` | `navegar` |
| ProgramFinder | Recomendación | `ProgramDetail(id)` y `Preinscription(id)` | `navegar` |
| ProgramDetail | Quiero inscribirme | `Preinscription(id)` | `navegar` |
| Intereses | Ver programa / Preinscribirme | `ProgramDetail(id)`, `Preinscription(id)` | `navegar` |
| Intereses | Sin programa elegido | `Catalog` | `navegar` |
| Payments | Pagar por PSE | `Pse` | `navegar` |
| Profile | Cerrar sesión | `Welcome` | `reemplazarTodo` |
| Cualquiera | Botón **"Volver"** de la cabecera | pantalla anterior de la pila | `onBack` |
| Cualquiera | Botón "atrás" del teléfono | pantalla anterior de la pila | `BackHandler` |
| `Campo` | Resumen → "Volver a la app" | pantalla anterior de la pila | `retroceder` |

> La flecha de la cabecera la dibuja `AppScaffold` **solo si recibe `onBack`**.
> Hasta el 5 Oct la recibía y no la pintaba: no había ninguna flecha de retorno
> en toda la app. Las pantallas que no la piden son las que son raíz de su
> rama (`Welcome`, `Home`, `ProspectHome`).
>
> `AuthScreen` y `WelcomeScreen` **no usan `AppScaffold`** (traen su propio
> layout), así que la flecha hay que dibujarlas a mano. `AuthScreen` la dibuja;
> `WelcomeScreen` es la raíz de la pila y no la necesita. `Platform` y `Profile`
> también la reciben.
>
> `Screen.Platform` ya **no** tiene botón "Volver al inicio". Antes lo tenía y
> `AppRoot` lo encadenaba a `Screen.Home` fijo, de modo que un invitado que
> llegara allí saldría al Home de estudiante. El parámetro `onGoHome` estaba
> declarado y sin usar; se eliminó junto con el destino equivocado.

### Pantallas sin entrada propia

`Screen.Intereses` **no aparece en ninguna pestaña ni en ninguna tarjeta fija**.
Solo se abre desde `Home` y `ProspectHome`, y ambas lo hacen dentro de un
`if (primerInteres != null)`. Sin intereses registrados —es decir, en la
práctica, la primera vez— esa pantalla no tiene por dónde abrirse. Es un hueco
de navegación, no una decisión de diseño: está anotado como pendiente.

## 4. Barra inferior (`EnidTab`)

| Tab | Ruta | Visible para |
|-----|------|--------------|
| Inicio | `Home` (estudiante) / `ProspectHome` (invitado) | todos |
| Horario | `DailySchedule` | solo conectado |
| Pagos | `Payments` | solo conectado |
| Soporte | `Faq` | todos |

**Quién ve qué pestañas no lo decide cada pantalla.** `AppScaffold` lee
`SesionUi.conectado` y calcula la lista con `tabsSegunSesion(...)`; **ninguna
pantalla pasa el parámetro `tabs`**. Antes el valor por defecto era
`EnidTab.entries` y ocho pantallas no pasaban nada, así que el invitado veía las
cuatro pestañas y podía entrar a `Payments` —con un historial de pagos
fabricado— tocando "Pagos".

El tab **Inicio** tampoco siempre va a `Home`: `AppRoot` lo resuelve según la
sesión (`inicioDelUsuario`). El invitado que abre el catálogo desde la barra
vuelve a `ProspectHome`, no al Home de estudiante.

## 5. Flujo interno de la preinscripción (3 pasos)

No cambia de ruta: los tres pasos viven en `PreinscriptionScreen` y el avance es
estado local (`paso`).

```mermaid
flowchart LR
    P1["Paso 1<br/>Quién eres<br/>nombre · documento · fechas · contacto"] -->|CONTINUAR| P2["Paso 2<br/>Formación y documentos<br/>nivel · casillas · ICFES · adjuntos"]
    P2 -->|CONTINUAR| P3["Paso 3<br/>Situación personal<br/>edad · familia · emergencia"]
    P3 -->|ENVIAR| OK["Pantalla de confirmación<br/>¡Preinscripción enviada!"]
    P1 <-->|Atrás| P2
    P2 <-->|Atrás| P3
```

- Entrada al programa: se puede llegar con `programaId` (desde detalle, finder o
  intereses) o sin él (`null`, desde los accesos directos del Home); si es `null`
  el formulario deja elegir el programa en el paso 1.
- Al enviar se llama a `DataRepository.registrarPreinscripcion` y se muestra la
  confirmación; el registro queda en el dispositivo y aparece en `Intereses`.

## 6. Salidas a apps externas (fuera del diagrama)

`data/EnlacesExternos.kt` es la **única** salida de la app hacia otra aplicación:
WhatsApp, llamadas, mapa, web, plataforma y PSE. Ninguna pantalla llama a
`context.startActivity` por su cuenta; una prueba lo verifica leyendo el
código fuente.

Con `habilitados = false` **no se abre nada**: solo queda el registro en el log.
Por eso la barra del asesor y los botones de sede son visibles pero inertes.

**Cada función devuelve `Boolean`**: `true` solo si la app externa se abrió de
verdad, `false` si el interruptor está apagado o si el dispositivo no tiene
ninguna app capaz de atender la acción. Sin ese valor de retorno una pantalla
no puede distinguir "se abrió" de "no pasó nada", y `PseScreen` por eso mismo
llegaba a mostrar *"Pago procesado con éxito"*.

| Desde | Acción | App destino |
|-------|--------|-------------|
| Varias | Contactar asesor | WhatsApp |
| Locations | Llamar a sede | Marcador |
| Locations | Cómo llegar | Mapa |
| Varias | Enlace institucional | Navegador |
| Platform | Ir a la plataforma | Navegador |
| Pse | Pagar por PSE | PSE bancario |

`Payments` ya **no** sale por sí sola: se llega desde `Pse` o desde el botón
"PAGAR CON PSE", y en ambos casos la pasarela se abre desde `Pse`.
