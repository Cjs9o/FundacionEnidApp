# ENID App · Captura de preinscripción en campo

App Android **offline-first** para capturar preinscripciones sobre el terreno. Todo se
guarda primero en el teléfono y el envío a Firebase es **manual**: nadie pierde datos
porque no haya señal.

---

## 1. Qué resuelve

Una promotora recorre el territorio con el teléfono en modo avión y hace decenas de
capturas. Al volver a la sede descarga cobertura y pulsa **SINCRONIZAR** una vez.

De ahí salen tres exigencias que gobiernan todo el diseño:

| Exigencia | Consecuencia en el código |
|---|---|
| El dispositivo puede quedarse sin señal horas | La base de datos es la fuente de verdad, no Firebase |
| El envío es una decisión, no un efecto secundario | `SyncManager` solo arranca desde un toque explícito |
| La promotora no puede volver a salir sin la foto | Sin evidencia no hay registro: la regla se valida antes de escribir |

---

## 2. Arquitectura

```
ui/          Pantallas Compose + ViewModels (sin conocimiento de Firebase)
   │
data/
   local/       Room: entidad, DAO, base          ← fuente de verdad
   evidencia/   AlmacenEvidencias (fotos internas)
   validation/  ValidadorAspirante (reglas R1..R8)
   sync/        SyncManager + PuertaRemota + SerializadorRtdb
   conectividad/MonitorConectividad
```

Dos decisiones que vale la pena destacar:

**La foto se copia dentro de la app.** Una foto de galería es una ruta que se puede
quedar sin permisos o borrar. `AlmacenEvidencias` la comprime a JPEG, la deja en
`filesDir/evidencias/{idRegistro}.jpg` y aplica la orientación EXIF. La ruta que se
guarda en la base es siempre interna.

**Firebase entra por una costura.** `SyncManager` no conoce RTDB: conoce la interfaz
`PuertaRemota`. Hoy la implementa `PuertaRemotaSimulada`, así que la app se puede
demostrar y testear sin red ni credenciales. Cuando llegue el proyecto de Firebase solo
se añade otra implementación; ni las pantallas ni la base de datos cambian.

---

## 3. Reglas de validación (R1..R8)

Se aplican **antes** de escribir, no al enviar. Si un dato está mal, la promotora se
entera mientras todavía tiene a la persona enfrente.

| Regla | Qué protege |
|---|---|
| R1 | Teléfono con formato de móvil válido |
| R2 | Correo con formato válido |
| R3 | Edad entera entre 15 y 100 años |
| R4 | Documento: 10 dígitos, del catálogo, único en el dispositivo |
| R5 | Municipio de captura del catálogo |
| R6 | Jornada y programa del catálogo |
| R7 | Municipio de captura también se valida |
| R8 | La fecha de nacimiento debe cuadrar con la edad escrita |
| — | Consentimiento de datos y foto adjunta son obligatorios |

`ValidadorAspirante` es Kotlin puro sin dependencias de Android, por eso se testea en
la JVM en milisegundos y se puede probar cada regla por separado.

---

## 4. Ciclo de vida de un registro

```
          guardar (siempre local)
                 │
                 ▼
            PENDIENTE ──────► SINCRONIZADO  (solo al pulsar SINCRONIZAR)
                 │                   ▲
                 │                   │
                 ▼                   │
               ERROR ── Reintentar ───┘
```

- **Guardar es local y siempre funciona.** El registro existe en el teléfono aunque no
  haya red.
- **El envío es manual y en cola.** Se procesan de más antiguo a más nuevo, uno por uno.
- **Un fallo no se pierde.** Queda en `ERROR` con su motivo y su contador de intentos;
  la copia local no se toca.
- **Un registro sincronizado ya no se edita.** Se muestra en solo lectura, para que lo
  que ya está en el servidor no cambie debajo.

---

## 5. Cómo compilar y probar

```bash
export JAVA_HOME=/ruta/al/jbr/de/android-studio

./gradlew :app:assembleDebug          # APK
./gradlew :app:testDebugUnitTest      # 34 tests JVM
./gradlew :app:connectedDebugAndroidTest   # 10 tests en dispositivo
```

Requiere un dispositivo o emulador conectado para la suite instrumentada.

### Pruebas

| Suite | Dónde | Qué cubre |
|---|---|---|
| `ValidacionAspiranteTest` (26) | JVM | Reglas R1..R8, catálogos y los tres casos límite de cumpleaños |
| `SerializadorRtdbTest` (8) | JVM | Forma del payload que se envía a Firebase |
| `PersistenciaOfflineInstrumentadoTest` (10) | Android | Room real, copia de evidencia, duplicados, orden de captura, bloqueo de edición, foto inaccesible |

**Total: 44 pruebas, 0 fallos.**

Dos detalles sobre las pruebas que parecen detalles y no lo son:

- La edad de los borradores de prueba **se calcula, no se escribe**. Si el test fija
  `edad = "21"` con nacimiento `15/04/2004`, el día del cumpleaños todos los tests que
  guardan fallan por un motivo ajeno a la persistencia, y el diagnóstico se vuelve
  inútil.
- El caso límite del cumpleaños está cubierto por tres pruebas que fallan si se
  reintroduce el error. Están comprobadas reintroduciendo el fallo a propósito.

---

## 6. Errores encontrados durante la verificación

Todos aparecieron ejecutando la app de verdad, no leyendo el código.

| # | Error | Efecto en el taller | Corrección |
|---|---|---|---|
| 1 | `AlmacenEvidencias.decodificar` usaba `?: return null` tras leer solo las dimensiones | Con `inJustDecodeBounds = true`, `decodeStream()` devuelve `null` **por diseño**: **ninguna evidencia se podía guardar** | Se decide con `outWidth/outHeight`, no con el resultado de decodificar |
| 2 | La orientación EXIF solo se aplicaba si la foto necesitaba escalado | Fotos ya pequeñas salían giradas | Se aplica la rotación siempre y se conserva el resultado |
| 3 | `AspiranteDao.rearmarError` volvía a `PENDIENTE` sin borrar `errorSincronizacion` | Un registro pendiente seguía mostrando "Sin conexión a internet" | La consulta limpia también el motivo; los intentos se conservan |
| 4 | `ValidadorAspirante` armaba el cumpleaños con el día y el mes **de hoy** en vez de los de nacimiento | **Rechazaba a los registros correctos**: el 02/10/2026, cualquiera nacido el 15/04/2004 recibía "la fecha indica 21 años" y no se podía guardar | La edad cumplida se compara con la fecha de nacimiento completa |
| 5 | El texto de red caía en un `?: "activa"` | Con el móvil en modo avión el resumen decía "Conexión: activa" mientras el banner decía que no hay red | Se rotula "Red ahora" y se dice "sin conexión" |
| 6 | Las acciones vivían dentro del área desplazable | Con el aviso de error, **SINCRONIZAR caía fuera de pantalla** | Las tres acciones quedan fijas en el pie, fuera del scroll |
| 7 | El snackbar quedó debajo del pie de acciones | El aviso "No hay conexión…" no se veía nunca | El snackbar va antes de las acciones |
| 8 | `SerializadorRtdb` no enviaba diagnóstico | RTDB no podía explicar por qué un envío falló | Se envían `errorSincronizacion` e `intentosSincronizacion` |

El error 4 es el más importante: no era una molestia, impedía guardar datos correctos.

---

## 7. Verificación en dispositivo

Tres ejecuciones sobre un Motorola moto g(20), Android 11:

**Ejecución 1 · arranque en frío y jornada completa**
Resumen con los contadores correctos (5 locales · 3 pendientes · 1 sincronizado ·
1 con error) y el aviso de error visible. Listado con sus cuatro filtros
(todas / pendientes / sincronizados / con error), detalle del registro con error
mostrando motivo e intentos, evidencia fotográfica cargada desde el almacenamiento
interno, y el registro sincronizado en solo lectura.

**Ejecución 2 · modo avión**
`Active default network: none`. El banner avisa de que se trabaja sin conexión, el
indicador dice "Red ahora: sin conexión", y al pulsar SINCRONIZAR se informa
*"No hay conexión. Los registros siguen guardados en este teléfono y se enviarán
cuando vuelvas a tener señal"*. Los contadores no cambian: no se perdió nada.

**Ejecución 3 · con conexión**
El registro en `ERROR` vuelve a `PENDIENTE` con **Reintentar** y sin arrastrar el
error anterior. Al sincronizar, los cuatro pendientes se envían uno por uno:

```
SyncManager: Sincronizando 4 registros, uno por uno
SyncManager: Enviado 2180B8AE (Luis Fernando Pedraza)
SyncManager: Enviado DCF294DE (Sara Milena Duarte Rios)
SyncManager: Enviado 502693A7 (Carlos Andres Nino Vega)
SyncManager: Enviado 62EBEF04 (Valentina Cruz Salazar)
```

Resultado: 5 locales · **0 pendientes** · 5 sincronizados. La copia local se conserva
íntegra en los cinco casos.

---

## 8. Conectar Firebase más adelante

1. Añadir el plugin `google-services` y un `google-services.json` real.
2. Implementar `PuertaRemota` con Firebase Realtime Database usando `EstructuraRtdb`
   (ya define las rutas) y `SerializadorRtdb` (ya produce el `Map<String, Any>`).
3. Cambiar la instancia en el contenedor de dependencias.
4. Añadir las reglas de seguridad de RTDB. La app es la única que debe poder escribir:
   lectura y escritura solo desde la aplicación autenticada, nunca desde un cliente
   arbitrario con la base abierta.

Los tests de `SerializadorRtdb` siguen sirviendo: describen el contrato del payload
que el servidor va a recibir.

---

## 9. Limitaciones conocidas

- **`PuertaRemotaSimulada` no usa red.** El envío se demuestra sin backend; el
  transporte real es el trabajo pendiente del punto 8.
- **La base de datos no se cifra.** Room va en claro dentro del sandbox de la app.
  Para datos personales de menores convendría `SQLCipher` y cifrado de la evidencia.
- **No hay autenticación.** Cualquiera que abra el teléfono ve la jornada.
- **`AspiranteDao.borrar` no borra la foto.** Está marcado como/utilidad de pruebas y
  se llama directamente desde la suite instrumentada, saltándose el repositorio, que
  sí limpia la evidencia al editar un registro. No hay borrado desde la interfaz, así
  que en el camino de la app no se da.
- **No se probó la captura por cámara en la interfaz.** El flujo está implementado
  (`FileProvider` + `EXTRA_OUTPUT` + recuperación del temporal) pero la automatización
  por ADB no permite dispararlo de forma fiable; el almacenamiento y la validación de la
  evidencia sí están cubiertos por pruebas en dispositivo.