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
    CURSO("Curso")
}

data class Program(
    val id: Int,
    val nombre: String,
    val tipo: TipoPrograma,
    val descripcion: String,
    val duracion: String,
    val modalidad: String,
    val pensum: List<String>,
    val perfilOcupacional: String,
    val sedes: List<String>
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