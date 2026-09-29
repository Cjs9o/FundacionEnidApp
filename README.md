# Fundación ENID App

Aplicación móvil **Android nativo (Kotlin + Jetpack Compose)** para la
**Fundación Escuela Nacional de Innovación y Desarrollo en Liderazgo (Fundación ENID)**.

Diseñada con una **interfaz accesible y adaptada a adultos mayores (60+)**: tipografía
grande, botones amplios y alto contraste. Cubre los **12 módulos funcionales** de la
institución (autenticación, oferta académica, preinscripciones, pagos PSE, FAQ, sedes,
horarios, etc.).

---

## Estado

**Última iteración: 29 Sep** — **plan de captación completo (4 fases) e instalado y
probado en dispositivo:**

- **Oferta académica potente.** Los 67 programas se clasifican en 9 áreas; el catálogo
  tiene buscador y filtros, y el detalle muestra información, qué aprenderás y requisitos,
  con "Quiero estudiar este programa", "Me interesa" y los CTAs de asesor y WhatsApp.
- **"¿Qué puedo estudiar?"** — cuestionario de 5 preguntas con recomendación ponderada
  por área, tipo de programa y campo laboral (5 pruebas unitarias, 0 fallos).
- **Dos caminos de Home.** "Soy estudiante" y "Quiero estudiar en ENID", con los 10 pasos
  del proceso de admisión a la vista y la barra del **asesor siempre visible**. El invitado
  recorre el proceso sin crear cuenta, y no ve las pestañas de Horario ni Pagos.
- **Promociones y testimonios.** Pantallas listas; la Fundación aún no ha entregado el
  contenido, y **no se inventaron** descuentos ni citas de estudiantes.
- **Seguimiento de interesados.** "Me interesa" guarda la decisión, el Home recuerda el
  programa y "Mis interesados" resume el siguiente paso real y los datos ya dejados.

Durante las pruebas **no se abrió ninguna app externa**: todo eso pasa por
`EnlacesExternos`, que está en `habilitados = false`.

Detalle completo, incluidos los fallos encontrados al probar y sus correcciones, en
[BITACORA.md](BITACORA.md).

**Iteración anterior (22 Sep):** actualización integral del catálogo con el
**Portafolio ENID 2026**: 67 programas (25 técnicos laborales, 11 cursos, 30 diplomados,
2 seminarios), cada uno con perfil profesional, perfil laboral y competencias. Se eliminó
el registro de usuarios y ahora la bienvenida ofrece **Iniciar Sesión** o
**Ingresar como Invitado**; el menú de Inicio cambia según haya sesión o no.

Detalle histórico de las 13 pruebas de compilación y correcciones: [BITACORA.md](BITACORA.md).

## Documentación del proyecto

| Documento | Descripción |
|---|---|
| [PLANDESARROLLO.md](PLANDESARROLLO.md) | Plan, fases, requerimientos (12 módulos), entorno de compilación y definición de hecho. |
| [BITACORA.md](BITACORA.md) | Registro cronológico del trabajo: decisiones, pantallas y pruebas de compilación. |
| [docs/imagenes/](docs/imagenes/) | Imágenes de referencia de la presentación del proyecto. |

---

## Guía rápida

- **Abrir y ejecutar:** ver [PLANDESARROLLO.md](PLANDESARROLLO.md) → *Cómo abrir y ejecutar*.
- **Requerimientos funcionales y estados:** ver [PLANDESARROLLO.md](PLANDESARROLLO.md) → *Requerimientos funcionales* y *Entregables actuales*.
- **Historial del trabajo y compilación:** ver [BITACORA.md](BITACORA.md).