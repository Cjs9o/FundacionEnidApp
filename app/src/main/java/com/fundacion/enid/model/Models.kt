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

// ---------- Preinscripción: tipos de documento y de contacto (Colombia) ----------
/**
 * Documentos de identidad aceptados por la Registraduría.
 * Se pide el tipo **por separado del número** porque "12345678" no dice si es una
 * cédula, una tarjeta de identidad o un NIT, y ENID los trata distinto.
 */
enum class TipoDocumento(val etiqueta: String, val ayuda: String) {
    CC("CC", "Cédula de Ciudadanía"),
    CE("CE", "Cédula de Extranjería"),
    TI("TI", "Tarjeta de Identidad (menores de edad)"),
    NIT("NIT", "Número de Identificación Tributaria"),
    RC("RC", "Registro de Nacimiento"),
    PP("PP", "Pasaporte"),
    CD("CD", "Carné de Extranjería")
}

/** Prefijo telefónico. ENID recibe &_-llamadas deColombia y del exterior. */
enum class PrefijoTelefono(val codigo: String, val pais: String) {
    COL("+57", "Colombia"),
    VEN("+58", "Venezuela"),
    MEX("+52", "México"),
    PER("+51", "Perú"),
    ECU("+593", "Ecuador"),
    USA("+1", "Estados Unidos / Canadá"),
    ESP("+34", "España")
}

enum class EstadoCivil(val etiqueta: String) {
    SOLTERO("Soltero/a"),
    CASADO("Casado/a"),
    UNION("Unión libre"),
    DIVORCIADO("Divorciado/a"),
    VIUDO("Viudo/a"),
    OTRO("Otro")
}

/** Nivel de formación previo. Define qué documentos hay que pedir. */
enum class NivelFormacion(val etiqueta: String, val pideTitulos: Boolean) {
    BACHILLER("Bachillerato completo", true),
    TECNICO("Técnico terminado", false),
    UNIVERSITARIO("Estudios universitarios", true),
    POSGRADO("Posgrado", true),
    SIN_TERMINAR("Aún no he terminado el bachillerato", true)
}

/**
 * Un documento que el aspirante debe entregar. La app **registra el estado**
 * (lo deja marcado) pero no adjunta el archivo: no hay servidor donde subirlo
 * y ENID recibe las fotocopias por el canal que indique el asesor.
 */
enum class DocumentoRequerido(
    val etiqueta: String,
    val obligatorio: Boolean,
    val nota: String
) {
    DOCUMENTO("Fotocopia del documento de identidad", true, "Cédula o tarjeta, por las dos caras"),
    TITULOS("Fotocopia de los títulos de bachiller", true, "Título de bachillerato registrado"),
    ICFES("Certificado del ICFES", true, "Resultado del examen Saber 11"),
    CEDULA_MENOR("Cédula del acudiente (si es menor de edad)", false, "Documento del responsable")
}

// ---------- Requerimiento 03: Preinscripción ----------
/** Familiar o persona de confianza que la Fundación puede contactar. */
data class Familiar(
    val nombre: String,
    val vinculo: String,
    val tipoDocumento: String,
    val numeroDocumento: String,
    val telefono: String,
    val correo: String
)

/**
 * Solicitud de preinscripción. Se guardó con campos pipes (`|`) y por eso el
 * formato es posicional: [DataRepository] la serializa en orden y la lee igual.
 * Los campos nuevos van al final para que las preinscripciones ya guardadas
 * (6 campos) sigan leyéndose.
 */
data class Preinscripcion(
    val nombre: String,
    val documento: String,
    val correo: String,
    val telefono: String,
    val programa: String,
    val fecha: String,
    val tipoDocumento: String = "",
    val prefijoTelefono: String = "",
    val fechaNacimiento: String = "",
    val fechaExpedicion: String = "",
    val nivelFormacion: String = "",
    val icfes: String = "",
    val documentos: List<String> = emptyList(),
    val estadoCivil: String = "",
    val esMenorDeEdad: Boolean? = null,
    val tieneAcudiente: Boolean? = null,
    val nombreAcudiente: String = "",
    val telefonoEmergencia: String = "",
    val prefijoEmergencia: String = "",
    val condicionEspecial: String = "",
    val familiar: Familiar? = null
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