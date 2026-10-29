package com.fundacion.enid.data

import com.fundacion.enid.model.DocumentoAdjunto
import com.fundacion.enid.model.Familiar
import com.fundacion.enid.model.Preinscripcion

/**
 * Lectura y escritura de la línea de texto donde se guarda una preinscripción.
 *
 * Vive fuera de [DataRepository] porque es lógica pura y hay que probarla: los
 * campos van separados por `|`, en orden, y un valor con un `|` o una coma
 * desalinea la lectura **en silencio** — el registro se guarda, pero al releerlo
 * aparece con los campos corridos o, en el caso del familiar, desaparece.
 *
 * Por eso los separadores van escapados con `%`: "Gómez, Luis" se guarda como
 * "Gómez%2CLuis" y vuelve a leerse igual. No se usa JSON a propósito: en las
 * pruebas unitarias de Android `org.json` es un stub que devuelve cero y las
 * pruebas pasarían sin comprobar nada.
 *
 * El formato es **posicional y compatible hacia atrás**: las preinscripciones de
 * 6, 21 y 22 campos, ya guardadas, siguen leyéndose. Los campos nuevos se
 * agregaron al final justamente por eso.
 */
object PreinscripcionTexto {

    /** Caracteres querompen la línea si van sueltos. */
    private const val ESCAPE = '%'
    private val A_ESCAPAR = listOf(ESCAPE, '|', ',', ';', '=')

    private fun escapar(valor: String): String {
        var salida = valor
        for (c in A_ESCAPAR) {
            salida = salida.replace(c.toString(), ESCAPE + c.code.toString(16).uppercase().padStart(2, '0'))
        }
        // Una línea no puede contener saltos de línea.
        return salida.replace("\n", " ").replace("\r", " ").trim()
    }

    private fun desescapar(valor: String): String {
        if (!valor.contains(ESCAPE)) return valor
        val salida = StringBuilder()
        var i = 0
        while (i < valor.length) {
            val c = valor[i]
            if (c == ESCAPE && i + 3 <= valor.length) {
                val hex = valor.substring(i + 1, i + 3)
                val code = hex.toIntOrNull(16)
                if (code != null) {
                    salida.append(code.toChar())
                    i += 3
                    continue
                }
            }
            salida.append(c)
            i++
        }
        return salida.toString()
    }

    fun serializar(p: Preinscripcion): String {
        // El familiar va anidado en un solo campo, escapado: sus seis datos se
        // separan con ';' y '=' porque las comas pueden venir en un nombre.
        val familiar = p.familiar?.let { f ->
            listOf("nombre", "vinculo", "tipoDocumento", "numeroDocumento", "telefono", "correo")
                .map { clave -> "$clave=${escapar(f.campo(clave))}" }
                .joinToString(";")
        } ?: ""

        // Los adjuntos van en el campo 22 con la misma mecánica. Cada adjunto es
        // un bloque `clave=valor;clave=valor` y los bloques se unen con ','.
        // Puede usarse ',' como unión porque todos los valores van escapados:
        // una coma dentro de un nombre ya quedó como %2C.
        val adjuntos = p.adjuntos.joinToString(",") { a ->
            listOf("documento", "formato", "nombre", "uri")
                .joinToString(";") { clave -> "$clave=${escapar(a.campo(clave))}" }
        }

        return listOf(
            escapar(p.nombre), escapar(p.documento), escapar(p.correo), escapar(p.telefono),
            escapar(p.programa), escapar(p.fecha),
            escapar(p.tipoDocumento), escapar(p.prefijoTelefono),
            escapar(p.fechaNacimiento), escapar(p.fechaExpedicion),
            escapar(p.nivelFormacion), escapar(p.icfes),
            p.documentos.joinToString(",") { escapar(it) }, escapar(p.estadoCivil),
            p.esMenorDeEdad?.toString() ?: "", p.tieneAcudiente?.toString() ?: "",
            escapar(p.nombreAcudiente), escapar(p.prefijoEmergencia),
            escapar(p.telefonoEmergencia), escapar(p.condicionEspecial),
            familiar, adjuntos, escapar(p.anio)
        ).joinToString("|")
    }

    /** null si la línea no es una preinscripción legible. */
    fun leer(linea: String): Preinscripcion? {
        val partes = linea.split("|")
        if (partes.size < 6) return null

        fun campo(i: Int) = partes.getOrNull(i)?.takeIf { it.isNotBlank() }?.let(::desescapar)
        fun booleano(i: Int) = when (campo(i)) {
            "true" -> true
            "false" -> false
            else -> null
        }

        return Preinscripcion(
            nombre = desescapar(partes[0]),
            documento = desescapar(partes[1]),
            correo = desescapar(partes[2]),
            telefono = desescapar(partes[3]),
            programa = desescapar(partes[4]),
            fecha = desescapar(partes[5]),
            tipoDocumento = campo(6) ?: "",
            prefijoTelefono = campo(7) ?: "",
            fechaNacimiento = campo(8) ?: "",
            fechaExpedicion = campo(9) ?: "",
            nivelFormacion = campo(10) ?: "",
            icfes = campo(11) ?: "",
            documentos = campo(12)?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
            estadoCivil = campo(13) ?: "",
            esMenorDeEdad = booleano(14),
            tieneAcudiente = booleano(15),
            nombreAcudiente = campo(16) ?: "",
            prefijoEmergencia = campo(17) ?: "",
            telefonoEmergencia = campo(18) ?: "",
            condicionEspecial = campo(19) ?: "",
            familiar = campo(20)?.let(::leerFamiliar),
            // A diferencia de los demás campos, aquí NO se desescapa el campo
            // entero antes de dividirlo: los adjuntos se unen entre sí con ','
            // y desescapar antes de partir volvería a ser coma la que traía un
            // nombre de archivo ("titulo%2C2 paginas.pdf") y partiría el bloque.
            // El desescape se hace valor por valor dentro de leerAdjuntos.
            adjuntos = leerAdjuntos(partes.getOrNull(21)?.takeIf { it.isNotBlank() }),
            // Campo 23 desde la versión con año/ciclo. Las líneas viejas no lo
            // traen y quedan con el valor vacío, sin inventar un ciclo.
            anio = campo(22) ?: ""
        )
    }

    /** Bloques `documento=..;formato=..;nombre=..;uri=..` unidos por comas. */
    private fun leerAdjuntos(crudo: String?): List<DocumentoAdjunto> {
        if (crudo.isNullOrBlank()) return emptyList()
        return crudo.split(",").mapNotNull { bloque ->
            if (bloque.isBlank()) return@mapNotNull null
            val valores = bloque.split(";").mapNotNull { par ->
                val i = par.indexOf('=')
                if (i <= 0) null else par.substring(0, i) to desescapar(par.substring(i + 1))
            }.toMap()
            val uri = valores["uri"] ?: return@mapNotNull null
            DocumentoAdjunto(
                documento = valores["documento"] ?: "",
                formato = valores["formato"] ?: "",
                nombreArchivo = valores["nombre"] ?: "",
                uri = uri
            )
        }
    }

    /**
     * Acepta los dos formatos del familiar: el actual (`clave=valor;clave=valor`)
     * y el antiguo de seis campos unidos por comas, para no perder lo guardado.
     */
    private fun leerFamiliar(crudo: String): Familiar? {
        if (crudo.contains('=')) {
            val valores = crudo.split(";").mapNotNull { par ->
                val i = par.indexOf('=')
                if (i <= 0) null
                else par.substring(0, i) to desescapar(par.substring(i + 1))
            }.toMap()
            return Familiar(
                nombre = valores["nombre"] ?: "",
                vinculo = valores["vinculo"] ?: "",
                tipoDocumento = valores["tipoDocumento"] ?: "",
                numeroDocumento = valores["numeroDocumento"] ?: "",
                telefono = valores["telefono"] ?: "",
                correo = valores["correo"] ?: ""
            )
        }
        val partes = crudo.split(",")
        return partes.takeIf { it.size == 6 }?.let {
            Familiar(it[0], it[1], it[2], it[3], it[4], it[5])
        }
    }

    /** Los seis datos del familiar, por nombre de clave. */
    private fun Familiar.campo(clave: String): String = when (clave) {
        "nombre" -> nombre
        "vinculo" -> vinculo
        "tipoDocumento" -> tipoDocumento
        "numeroDocumento" -> numeroDocumento
        "telefono" -> telefono
        else -> correo
    }

    /** Los cuatro datos de un adjunto, por nombre de clave. */
    private fun DocumentoAdjunto.campo(clave: String): String = when (clave) {
        "documento" -> documento
        "formato" -> formato
        "nombre" -> nombreArchivo
        else -> uri
    }
}
