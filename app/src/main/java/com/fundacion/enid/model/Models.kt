package com.fundacion.enid.model

// ---------- Requerimiento 02: Usuario / Rol ----------
enum class Rol(val etiqueta: String) {
    ASPIRANTE("Aspirante"),
    ESTUDIANTE("Estudiante"),
    DOCENTE("Docente"),
    ADMINISTRATIVO("Administrativo");

    companion object {
        fun desde(value: String): Rol =
            entries.firstOrNull { it.etiqueta == value } ?: ASPIRANTE
    }
}

data class User(
    val nombre: String,
    val correo: String,
    val rol: Rol
)

// ---------- Requerimiento 04: Catálogo académico ----------
enum class TipoPrograma(val etiqueta: String) {
    TECNICO("Programa Técnico"),
    DIPLOMADO("Diplomado"),
    CURSO("Curso"),
    SEMINARIO("Seminario")
}

/** Áreas temáticas: agrupar la oferta y alimentar el orientador "¿Qué puedo estudiar?". */
enum class Area(val etiqueta: String) {
    ADMINISTRACION("Administración y gestión"),
    SISTEMAS("Sistemas y tecnología"),
    MARKETING("Marketing y ventas"),
    PERSONAS("Personas, educación y salud"),
    TURISMO("Turismo y hotelería"),
    AMBIENTE("Medio ambiente y sostenibilidad"),
    ARTE("Arte, comunicación y audiovisual"),
    LEGAL("Legal, justicia y gestión documental"),
    OPERATIVOS("Operaciones, logística y servicios")
}

data class Program(
    val id: Int,
    val nombre: String,
    val tipo: TipoPrograma,
    val area: Area,
    val descripcion: String,
    val duracion: String,
    val modalidad: String,
    val competencias: List<String>,
    val perfilOcupacional: String,
    val sedes: List<String>,
    val campoLaboral: List<String> = emptyList()
)

// ---------- Requerimiento 05: Sedes ----------
data class Sede(
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val horario: String,
    val lat: Double,
    val lng: Double
)

// ---------- Requerimiento 06: Preguntas frecuentes ----------
data class FaqItem(
    val pregunta: String,
    val respuesta: String
)

// ---------- Requerimiento 07: Cronograma general ----------
data class SubjectPlan(
    val nombre: String,
    val inicio: String,
    val fin: String,
    val intensidad: String
)

// ---------- Requerimiento 08: Horario diario ----------
data class ClassSlot(
    val dia: String,
    val hora: String,
    val asignatura: String,
    val aula: String,
    val docente: String
)

// ---------- Requerimiento 09: Alertas de pago ----------
enum class EstadoPago(val etiqueta: String) {
    POR_PAGAR("Por pagar"),
    VENCE_PRONTAMENTE("Vence pronto"),
    PAGADO("Pagado")
}

data class PaymentNotice(
    val concepto: String,
    val monto: String,
    val fechaLimite: String,
    val estado: EstadoPago,
    val detalle: String
)

// ---------- Requerimiento 03: Preinscripción ----------
data class Preinscripcion(
    val nombre: String,
    val documento: String,
    val correo: String,
    val telefono: String,
    val programa: String,
    val fecha: String
)

// ---------- Captación: promociones y testimonios (requerimientos 05 y 06) ----------
enum class TipoPromocion { DESCUENTO, ALIANZA, CONVOCATORIA, OPORTUNIDAD }

/**
 * Promoción o beneficio de la Fundación.
 *
 * Las listas de [com.fundacion.enid.data.DataRepository.promociones] arrancan
 * **vacías a propósito**: una promoción con fecha, porcentaje o condición inventada
 * es una afirmación comercial falsa. La app ya muestra el contenido y el advisor
 * solo tiene que agregar los datos oficiales.
 */
data class Promocion(
    val titulo: String,
    val detalle: String,
    val vigencia: String,
    val tipo: TipoPromocion,
    val destacada: Boolean = false
)

/**
 * Testimonio de estudiante.
 *
 * Igual que [Promocion], no se inventan citas ni nombres: la lista arranca vacía
 * y se llena con los testimonios que entregue la Fundación.
 */
data class Testimonio(
    val nombre: String,
    val programa: String,
    val cita: String,
    val destacada: Boolean = false
)

// ---------- Captación: recorrido del prospecto (camino "Quiero estudiar en ENID") ----------
/**
 * Un paso del camino B. [pantalla] es la pantalla de la app que resuelve el paso
 * cuando existe; los pasos que se resuelven con el asesor o en sede se marcan
 * con [enSede] para no prometer algo que la app no hace.
 */
data class RutaStep(
    val numero: Int,
    val titulo: String,
    val detalle: String,
    val pantalla: String? = null,
    val enSede: Boolean = false
)