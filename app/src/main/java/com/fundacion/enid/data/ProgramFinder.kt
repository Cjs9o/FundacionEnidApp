package com.fundacion.enid.data

import com.fundacion.enid.model.Area
import com.fundacion.enid.model.Program
import com.fundacion.enid.model.TipoPrograma

/**
 * Orientador "¿Qué puedo estudiar?".
 *
 * Cinco preguntas breves que, según las respuestas, ordenan el catálogo del
 * Portafolio ENID y sugieren los programas que mejor encajan. No sustituye la
 * asesoría: al final siempre se ofrece hablar con un asesor.
 *
 * Las respuestas no filtran (para no dejar al usuario sin resultados), sino que
 * **puntúan**: cada programa acumula puntos y se devuelven los mejor clasificados.
 */
data class FinderOpcion(
    val texto: String,
    val areas: Map<Area, Int> = emptyMap(),
    val tipos: Map<TipoPrograma, Int> = emptyMap(),
    /** Palabras clave que se buscan dentro del campo laboral del programa. */
    val campoClaves: List<String> = emptyList()
)

data class FinderPregunta(
    val titulo: String,
    val ayuda: String,
    val opciones: List<FinderOpcion>
)

object ProgramFinder {

    val preguntas: List<FinderPregunta> = listOf(
        FinderPregunta(
            titulo = "¿Qué te interesa más?",
            ayuda = "Elige el tema que más te llama la atención.",
            opciones = listOf(
                FinderOpcion("Administración y gestión", mapOf(Area.ADMINISTRACION to 3)),
                FinderOpcion("Sistemas, datos e internet", mapOf(Area.SISTEMAS to 3)),
                FinderOpcion("Marketing, ventas y redes sociales", mapOf(Area.MARKETING to 3)),
                FinderOpcion("Cuidar y enseñar a las personas", mapOf(Area.PERSONAS to 3)),
                FinderOpcion("Turismo y hotelería", mapOf(Area.TURISMO to 3)),
                FinderOpcion("Medio ambiente y naturaleza", mapOf(Area.AMBIENTE to 3)),
                FinderOpcion("Arte, diseño y comunicación", mapOf(Area.ARTE to 3)),
                FinderOpcion("Ley, justicia y documentos", mapOf(Area.LEGAL to 3)),
                FinderOpcion("Bodega, logística y servicios", mapOf(Area.OPERATIVOS to 3))
            )
        ),
        FinderPregunta(
            titulo = "¿Qué te gustaría hacer en tu trabajo?",
            ayuda = "Piensa en la tarea diaria que más te gustaría hacer.",
            opciones = listOf(
                FinderOpcion(
                    "Organizar y apoyar los procesos de una empresa",
                    mapOf(Area.ADMINISTRACION to 2, Area.OPERATIVOS to 2)
                ),
                FinderOpcion("Analizar datos y resolver problemas técnicos", mapOf(Area.SISTEMAS to 2)),
                FinderOpcion("Vender, manejar marcas y crear contenido", mapOf(Area.MARKETING to 2)),
                FinderOpcion("Acompañar, enseñar o cuidar a las personas", mapOf(Area.PERSONAS to 2)),
                FinderOpcion("Atender viajeros y clientes en turismo o hotel", mapOf(Area.TURISMO to 2)),
                FinderOpcion("Cuidar el ambiente y los recursos naturales", mapOf(Area.AMBIENTE to 2)),
                FinderOpcion("Crear piezas visuales, video o teatro", mapOf(Area.ARTE to 2)),
                FinderOpcion("Resolver trámites y trabajar con expedientes", mapOf(Area.LEGAL to 2)),
                FinderOpcion("Manejar bodega, inventarios o mantenimiento", mapOf(Area.OPERATIVOS to 2))
            )
        ),
        FinderPregunta(
            titulo = "¿Con qué te gustaría trabajar?",
            ayuda = "Elige el tipo de cosa con la que te sentirías cómodo trabajando.",
            opciones = listOf(
                FinderOpcion("Computadores, celular e internet", mapOf(Area.SISTEMAS to 2)),
                FinderOpcion("Personas de todas las edades", mapOf(Area.PERSONAS to 2)),
                FinderOpcion("Documentos, cifras y sistemas", mapOf(Area.ADMINISTRACION to 1, Area.LEGAL to 1)),
                FinderOpcion("Libros, imágenes, sonido y redes", mapOf(Area.ARTE to 2, Area.MARKETING to 1)),
                FinderOpcion("Máquinas, herramientas y equipos", mapOf(Area.OPERATIVOS to 2, Area.SISTEMAS to 1)),
                FinderOpcion("La naturaleza y el campo", mapOf(Area.AMBIENTE to 2)),
                FinderOpcion("El derecho, los trámites y la documentación", mapOf(Area.LEGAL to 2)),
                FinderOpcion("Los viajes y la hospitalidad", mapOf(Area.TURISMO to 2))
            )
        ),
        FinderPregunta(
            titulo = "¿Qué título necesitas?",
            ayuda = "Esto define cuánto tiempo quieres estudiar.",
            opciones = listOf(
                FinderOpcion("Un técnico de 6 meses para trabajar ya", mapOf(), mapOf(TipoPrograma.TECNICO to 3)),
                FinderOpcion("Un diplomado para especializarme", mapOf(), mapOf(TipoPrograma.DIPLOMADO to 3)),
                FinderOpcion("Un curso corto, práctico y rápido", mapOf(), mapOf(TipoPrograma.CURSO to 3, TipoPrograma.SEMINARIO to 2)),
                FinderOpcion("Todavía no lo sé, quiero que me orienten", tipos = todosLosTipos())
            )
        ),
        FinderPregunta(
            titulo = "¿Dónde te gustaría trabajar al terminar?",
            ayuda = "Buscamos programas cuyo campo laboral se parezca a ese sector.",
            opciones = listOf(
                FinderOpcion("En tecnología, banca o telecomunicaciones", campoClaves = listOf("tecnolog", "tech", "telecomunic", "banco", "financier", "nube")),
                FinderOpcion("En salud, educación o atención a personas", campoClaves = listOf("salud", "educativ", "jardín", "fundacion", "social", "eps", "hospital", "clínica")),
                FinderOpcion("En comercio, industria, construcción o minería", campoClaves = listOf("comercio", "retail", "industrial", "construcción", "logística", "metal", "automotriz", "minero")),
                FinderOpcion("En el ambiente, el campo o la energía", campoClaves = listOf("ambiental", "residuos", "energía", "agua", "forestal", "agricola", "agricultura", "sostenib")),
                FinderOpcion("En turismo, hoteles o restaurantes", campoClaves = listOf("hotel", "hostel", "turismo", "viaje", "restaurante", "gastron")),
                FinderOpcion("En cultura, comunicación o publicidad", campoClaves = listOf("agencia", "publicidad", "audiovisual", "teatro", "cultural", "editorial", "imprenta", "marketing", "streaming"))
            )
        )
    )

    private fun todosLosTipos(): Map<TipoPrograma, Int> =
        TipoPrograma.entries.associateWith { 1 }

    /**
     * Devuelve los [limite] programas mejor puntuados para las respuestas dadas.
     * Si ninguna respuesta aporta puntos, devuelve los técnicos como recomendación base.
     */
    fun recomendar(programas: List<Program>, respuestas: List<FinderOpcion>, limite: Int = 5): List<Program> {
        val conPuntos = programas.map { programa ->
            val puntaje = respuestas.sumOf { opcion -> puntajeDe(programa, opcion) }
            programa to puntaje
        }
        val mejorPuntaje = conPuntos.maxOfOrNull { it.second } ?: 0
        if (mejorPuntaje <= 0) {
            return programas.filter { it.tipo == TipoPrograma.TECNICO }.take(limite)
        }
        return conPuntos
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .map { it.first }
            .take(limite)
    }

    private fun puntajeDe(programa: Program, opcion: FinderOpcion): Int {
        var puntos = opcion.areas[programa.area] ?: 0
        puntos += opcion.tipos[programa.tipo] ?: 0
        if (opcion.campoClaves.isNotEmpty() && programa.campoLaboral.isNotEmpty()) {
            val campo = programa.campoLaboral.joinToString(" ") { it.lowercase() }
            if (opcion.campoClaves.any { campo.contains(it) }) puntos += 2
        }
        return puntos
    }
}
