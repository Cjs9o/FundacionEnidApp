package com.fundacion.enid.model

// ---------- Requerimiento 02: Usuario / Rol ----------
enum class Rol(val etiqueta: String) {
    ASPIRANTE("Aspirante"),
    ESTUDIANTE("Estudiante"),
    DOCENTE("Docente"),
    ADMINISTRATIVO("Administrativo"),
    ADMIN("Admin");

    companion object {
        fun desde(value: String): Rol =
            entries.firstOrNull { it.etiqueta.equals(value, ignoreCase = true) } ?: ASPIRANTE
    }
}

data class User(
    val nombre: String,
    val correo: String,
    val rol: Rol,
    val programa: String? = null,
    val anio: String? = null,
    val telefono: String? = null,
    val documento: String? = null,
    val datosPersonales: String? = null
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
    /** Todos los teléfonos de atención de la sede, en orden de publicación. */
    val telefonos: List<String>,
    val horario: String,
    val lat: Double,
    val lng: Double
) {
    /** Primer teléfono, para las acciones que solo admiten uno. */
    val telefono: String get() = telefonos.firstOrNull().orEmpty()
}

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
 * Un documento que el aspirante debe entregar.
 *
 * La casilla sigue siendo la confirmación de que puede aportar el documento,
 * pero ahora además puede **adjuntar el archivo**: la persona elige el formato
 * en una lista y carga el archivo desde el teléfono. El archivo no viaja a
 * ningún servidor: se guarda la referencia (nombre y URI) en la preinscripción
 * para que la Fundación lo abra desde el mismo dispositivo.
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

/**
 * Formatos en los que la Fundación acepta el archivo adjunto.
 *
 * Es la misma mecánica que la lista de "Tipo de documento" de la cédula: se elige
 * de una lista, no se escribe a mano, porque "documento.pdf", "documento.jpg" y
 * "documento" son cosas distintas para quien recibe la preinscripción y una
 * extensión mal escrita se descubre tarde.
 */
enum class FormatoArchivo(val etiqueta: String, val ayuda: String, val mime: String) {
    PDF("PDF", "Documento escaneado en PDF", "application/pdf"),
    JPG("JPG", "Foto del documento con el celular", "image/jpeg"),
    PNG("PNG", "Captura o imagen sin pérdida", "image/png");

    companion object {
        /** El formato guardado en un [DocumentoAdjunto], o `null` si no se reconoce. */
        fun desdeEtiqueta(etiqueta: String?): FormatoArchivo? =
            entries.firstOrNull { it.etiqueta == etiqueta }
    }
}

/**
 * Archivo que la persona adjuntó para un documento requerido.
 *
 * [uri] es la referencia del archivo en el dispositivo. No se copia el contenido:
 * guardarlo entero en las preferencias llenaría el almacenamiento. Con el permiso
 * de lectura que otorga el selector de archivos, la URI sigue siendo válida al
 * volver a abrir la preinscripción.
 */
data class DocumentoAdjunto(
    val documento: String,
    val formato: String,
    val nombreArchivo: String,
    val uri: String
)

/**
 * Los adjuntos que sí se pueden enviar: los de documentos que siguen marcados.
 *
 * Es una red de seguridad, no el mecanismo principal. Desmarcar una casilla ya
 * borra su adjunto (y suelta el permiso del archivo), pero si por lo que sea
 * quedara un adjunto de un documento desmarcado, enviarlo significaría incluir
 * una copia de un documento que la persona dijo no poder aportar.
 */
fun adjuntosDeDocumentosMarcados(
    adjuntos: Map<String, DocumentoAdjunto>,
    documentos: Set<String>
): List<DocumentoAdjunto> = adjuntos.filterKeys { documentos.contains(it) }.values.toList()

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
    val familiar: Familiar? = null,
    val adjuntos: List<DocumentoAdjunto> = emptyList(),
    /**
     * Año o ciclo con el que la persona quiere iniciar. Se elige de una lista
     * cerrada en la preinscripción; no se escribe a mano en la cuenta.
     */
    val anio: String = ""
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