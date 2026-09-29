package com.fundacion.enid.data

import java.util.Calendar

/**
 * Validación y enmascarado de los datos que escribe la persona en el formulario.
 *
 * Vive aquí y no dentro de la pantalla porque son funciones puras —sin Compose y
 * sin contexto— y así se pueden probar: una fecha que acepta "31/02" o una edad
 * que devuelve 3 para un niño de 23 meses son errores que no se ven en la
 * compilación.
 */
object FormatoEntrada {

    /** Máximo de dígitos de un documento. */
    const val MAX_DIGITOS_DOCUMENTO = 12

    /** Máximo de dígitos de un número de contacto, sin prefijo. */
    const val MAX_DIGITOS_TELEFONO = 10

    /** Deja solo dígitos, con tope de longitud: evita puntos, guiones y espacios. */
    fun soloDigitos(valor: String, maximo: Int): String =
        valor.filter { it.isDigit() }.take(maximo)

    /**
     * Inserta las barras de dd/mm/aaaa mientras se escribe.
     *
     * Si alguien pega "14-03-1972" queda "14/03/1972". El cursor puede saltar un
     * carácter al editar en la mitad del texto, pero el valor siempre es válido
     * para lo que la validación espera.
     */
    fun enmascaraFecha(entrada: String): String {
        val digitos = entrada.filter { it.isDigit() }.take(8)
        return when {
            digitos.length <= 2 -> digitos
            digitos.length <= 4 -> "${digitos.take(2)}/${digitos.drop(2)}"
            else -> "${digitos.take(2)}/${digitos.substring(2, 4)}/${digitos.drop(4)}"
        }
    }

    /**
     * dd/mm/aaaa: 2 dígitos, barra, 2 dígitos, barra, 4 dígitos, y que la fecha
     * exista de verdad. Rechaza "31/02" y "29/02" en años que no son bisiestos.
     */
    fun esFechaValida(fecha: String): Boolean {
        if (fecha.length != 10) return false
        val partes = fecha.split("/")
        if (partes.size != 3) return false
        if (partes[0].length != 2 || partes[1].length != 2 || partes[2].length != 4) return false
        if (!partes.all { it.all(Char::isDigit) }) return false

        val dia = partes[0].toInt()
        val mes = partes[1].toInt()
        val anio = partes[2].toInt()
        if (dia < 1 || mes !in 1..12 || anio < 1900) return false

        val diasDelMes = when (mes) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            else -> if (esBisiesto(anio)) 29 else 28
        }
        return dia <= diasDelMes
    }

    private fun esBisiesto(anio: Int): Boolean =
        anio % 4 == 0 && (anio % 100 != 0 || anio % 400 == 0)

    /**
     * true si [a] es cronológicamente anterior a [b].
     *
     * No se pueden comparar las cadenas dd/mm/aaaa directamente: "08/07/1994" es
     * *menor* que "14/03/1972" porque el día va primero, aunque 1994 sea posterior.
     * Se reordenan los componentes a aaaammdd, que sí compara bien.
     */
    fun esAnterior(a: String, b: String): Boolean {
        require(esFechaValida(a) && esFechaValida(b)) {
            "esAnterior requiere fechas válidas: '$a', '$b'"
        }
        return claveOrdenable(a) < claveOrdenable(b)
    }

    /** aaaammdd como entero de 8 dígitos, para ordenar. */
    private fun claveOrdenable(fecha: String): Long {
        val p = fecha.split("/")
        return (p[2] + p[1] + p[0]).toLong()
    }

    /**
     * Edad **cumplida** a la fecha de referencia, o null si la fecha no sirve.
     *
     * Se calcula contra una fecha explícita para que la prueba no dependa del día
     * en que se corra: quien nace el 15/05/2010 tiene 15 años un 15/05, no antes.
     */
    fun edadCumplida(fecha: String, hoy: Calendar = Calendar.getInstance()): Int? {
        if (!esFechaValida(fecha)) return null
        val partes = fecha.split("/")
        val dia = partes[0].toInt()
        val mes = partes[1].toInt()
        val anio = partes[2].toInt()

        var edad = hoy.get(Calendar.YEAR) - anio
        val mesActual = hoy.get(Calendar.MONTH) + 1
        val diaActual = hoy.get(Calendar.DAY_OF_MONTH)
        if (mesActual < mes || (mesActual == mes && diaActual < dia)) edad--

        return if (edad in 0..120) edad else null
    }

    /** Un correo con algo antes y después de una arroba. No valida el dominio a fondo. */
    fun esCorreoValido(correo: String): Boolean {
        val limpio = correo.trim()
        val arrobas = limpio.count { it == '@' }
        if (arrobas != 1) return false
        val partes = limpio.split("@")
        return partes[0].isNotBlank() &&
            partes[1].isNotBlank() &&
            partes[1].contains(".") &&
            !parteInvalida.any { limpio.contains(it) }
    }

    private val parteInvalida = listOf(" ", "..")
}
