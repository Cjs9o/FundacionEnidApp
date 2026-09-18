package com.fundacion.enid.data

import android.content.Context
import android.content.SharedPreferences
import com.fundacion.enid.model.ClassSlot
import com.fundacion.enid.model.EstadoPago
import com.fundacion.enid.model.FaqItem
import com.fundacion.enid.model.PaymentNotice
import com.fundacion.enid.model.Preinscripcion
import com.fundacion.enid.model.Program
import com.fundacion.enid.model.Rol
import com.fundacion.enid.model.Sede
import com.fundacion.enid.model.SubjectPlan
import com.fundacion.enid.model.TipoPrograma
import com.fundacion.enid.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repositorio local de la app Fundación ENID.
 * Datos institucionales de ejemplo + gestión de sesión e inscripciones
 * mediante SharedPreferences. Sin dependencias de red (build offline).
 */
class DataRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("fundacion_enid_prefs", Context.MODE_PRIVATE)

    // --------------------------------------------------------------------
    // Requerimiento 04: Catálogo institucional y oferta académica
    // --------------------------------------------------------------------
    val programas: List<Program> = listOf(
        Program(
            id = 1,
            nombre = "Técnico Laboral en Soporte de Sistemas e Informática",
            tipo = TipoPrograma.TECNICO,
            descripcion = "Formación práctica para instalar, mantener y dar soporte a " +
                "equipos de cómputo y redes de datos en organizaciones.",
            duracion = "12 meses",
            modalidad = "Presencial",
            pensum = listOf(
                "Fundamentos de Hardware y Ensamble",
                "Sistemas Operativos",
                "Redes de Datos",
                "Seguridad Informática",
                "Ofimática Aplicada",
                "Mantenimiento Preventivo y Correctivo",
                "Atención al Cliente y Servicio",
                "Bases de Datos",
                "Práctica Empresarial"
            ),
            perfilOcupacional = "Auxiliar de soporte técnico, mantenimiento de equipos, " +
                "administración básica de redes y mesa de ayuda de TI.",
            sedes = listOf("Chía", "Sopó")
        ),
        Program(
            id = 2,
            nombre = "Técnico Laboral en Seguridad y Salud en el Trabajo",
            tipo = TipoPrograma.TECNICO,
            descripcion = "Formación en prevención de riesgos laborales con fundamento en la " +
                "normatividad colombiana (Ley 1562 de 2012).",
            duracion = "12 meses",
            modalidad = "Presencial",
            pensum = listOf(
                "Fundamentos de Seguridad y Salud en el Trabajo",
                "Higiene Ocupacional",
                "Riesgos Físicos, Químicos y Biológicos",
                "Legislación Laboral y SST",
                "Medicina Preventiva del Trabajo",
                "Investigación de Incidentes y Accidentes",
                "Primeros Auxilios",
                "Práctica Empresarial"
            ),
            perfilOcupacional = "Auxiliar en SST, apoyo en la ejecución del SG-SST, " +
                "inspecciones de seguridad y campañas de prevención.",
            sedes = listOf("Chía")
        ),
        Program(
            id = 3,
            nombre = "Diplomado en Habilidades de Liderazgo",
            tipo = TipoPrograma.DIPLOMADO,
            descripcion = "Desarrollo de competencias de liderazgo, comunicación asertiva y " +
                "trabajo en equipo para el ámbito laboral.",
            duracion = "4 meses",
            modalidad = "Semipresencial",
            pensum = listOf(
                "Autoconocimiento y Proyecto de Vida",
                "Comunicación Asertiva",
                "Liderazgo de Equipos",
                "Gestión del Cambio",
                "Ética y Responsabilidad Social",
                "Taller Práctico de Liderazgo"
            ),
            perfilOcupacional = "Líderes de proyectos, coordinadores de equipos y " +
                "gestores comunitarios.",
            sedes = listOf("Chía", "Sopó")
        ),
        Program(
            id = 4,
            nombre = "Diplomado en Seguridad Vial",
            tipo = TipoPrograma.DIPLOMADO,
            descripcion = "Normatividad vial, educación ciudadana en movilidad y prevención " +
                "de siniestros.",
            duracion = "3 meses",
            modalidad = "Presencial",
            pensum = listOf(
                "Normatividad Vial Colombiana",
                "Educación y Cultura Vial",
                "Infraestructura Vial",
                "Investigación de Siniestros Viales",
                "Taller de Sensibilización"
            ),
            perfilOcupacional = "Gestores de seguridad vial, apoyo a entidades de tránsito " +
                "y empresas de transporte.",
            sedes = listOf("Sopó")
        ),
        Program(
            id = 5,
            nombre = "Curso de Primeros Auxilios",
            tipo = TipoPrograma.CURSO,
            descripcion = "Conocimientos básicos y prácticos de atención inicial de emergencias.",
            duracion = "1 mes",
            modalidad = "Presencial",
            pensum = listOf(
                "Valoración de la Escena",
                "RCP Básico (Adultos y Niños)",
                "Manejo de Obstrucción de Vía Aérea",
                "Heridas, Fracturas y Quemaduras",
                "Práctica con Maniquíes"
            ),
            perfilOcupacional = "Brigadistas de emergencia, personal en primeros auxilios y " +
                "comunidad en general.",
            sedes = listOf("Chía", "Sopó")
        ),
        Program(
            id = 6,
            nombre = "Curso de Ofimática para Adultos",
            tipo = TipoPrograma.CURSO,
            descripcion = "Introducción gradual a computadores, internet y herramientas de " +
                "oficina. Pensado especialmente para adultos mayores.",
            duracion = "2 meses",
            modalidad = "Presencial",
            pensum = listOf(
                "Uso del Computador desde Cero",
                "Navegación Segura en Internet",
                "Procesador de Texto",
                "Correo Electrónico y Videollamadas",
                "Tramites Digitales Sencillos"
            ),
            perfilOcupacional = "Fortalecimiento de habilidades digitales para la vida cotidiana.",
            sedes = listOf("Chía", "Sopó")
        )
    )

    val sedes: List<Sede> = listOf(
        Sede(
            nombre = "Sede Chía — Campus Principal",
            direccion = "Calle 11 # 9 - 47, Chía, Cundinamarca",
            telefono = "+57 601 745 0020",
            horario = "Lun a Vie 7:00 a.m. - 8:00 p.m. · Sáb 8:00 a.m. - 1:00 p.m.",
            lat = 4.8599,
            lng = -74.0518
        ),
        Sede(
            nombre = "Sede Sopó",
            direccion = "Cra 5 # 4 - 12, Sopó, Cundinamarca",
            telefono = "+57 601 863 0044",
            horario = "Lun a Vie 8:00 a.m. - 6:00 p.m.",
            lat = 4.9103,
            lng = -73.5965
        )
    )

    // --------------------------------------------------------------------
    // Requerimiento 06: Preguntas frecuentes
    // --------------------------------------------------------------------
    val faqs: List<FaqItem> = listOf(
        FaqItem(
            "¿Qué requisitos necesito para inscribirme?",
            "Debes tener al menos 15 años para programas técnicos y presentar: documento de " +
                "identidad, último certificado de estudios y diligenciar el formulario de " +
                "preinscripción dentro de la app."
        ),
        FaqItem(
            "¿Hay límite de edad para estudiar?",
            "No. La Fundación promueve la educación para todas las edades, incluidos adultos " +
                "mayores. El Curso de Ofimática para Adultos está diseñado específicamente " +
                "para ellos."
        ),
        FaqItem(
            "¿Cómo puedo pagar la matrícula?",
            "Puedes pagar por la pasarela de pagos en línea (PSE) dentro de esta app, con " +
                "las entidades bancarias autorizadas, o en caja de cada sede."
        ),
        FaqItem(
            "¿Qué pasa si un pago vence antes de la fecha límite?",
            "La app te envía alertas y recordatorios. Si la fecha vence se considera mora y " +
                "puede aplicar recargo sobre el valor de la matrícula."
        ),
        FaqItem(
            "¿Cómo obtengo mi certificado de estudios?",
            "Al finalizar y aprobar el programa se emite el certificado en un plazo de 10 a " +
                "15 días hábiles. Puedes consultar el estado desde la plataforma educativa."
        ),
        FaqItem(
            "¿Ofrecen descuentos por referir estudiantes?",
            "Sí. Al referir un nuevo estudiante obtienes beneficios que puedes calcular con " +
                "la Calculadora de Beneficios de esta app."
        ),
        FaqItem(
            "¿La app es accesible para personas mayores?",
            "Sí. La interfaz usa tipografía grande, botones amplios y alto contraste para " +
                "facilitar su uso a personas mayores de 60 años."
        )
    )

    // --------------------------------------------------------------------
    // Requerimiento 07: Cronograma general del plan de estudios
    // --------------------------------------------------------------------
    val cronograma: List<SubjectPlan> = listOf(
        SubjectPlan("Fundamentos de Hardware y Ensamble", "26 Ene", "20 Feb", "4 h/sem"),
        SubjectPlan("Sistemas Operativos", "23 Feb", "27 Mar", "5 h/sem"),
        SubjectPlan("Redes de Datos", "30 Mar", "30 Abr", "5 h/sem"),
        SubjectPlan("Seguridad Informática", "04 May", "29 May", "4 h/sem"),
        SubjectPlan("Ofimática Aplicada", "01 Jun", "26 Jun", "4 h/sem"),
        SubjectPlan("Mantenimiento Preventivo y Correctivo", "29 Jun", "31 Jul", "5 h/sem"),
        SubjectPlan("Atención al Cliente y Servicio", "03 Ago", "28 Ago", "3 h/sem"),
        SubjectPlan("Bases de Datos", "31 Ago", "25 Sep", "5 h/sem"),
        SubjectPlan("Práctica Empresarial", "28 Sep", "20 Nov", "Tiempo completo")
    )

    // --------------------------------------------------------------------
    // Requerimiento 08: Horario diario de clases
    // --------------------------------------------------------------------
    val horarioClases: List<ClassSlot> = listOf(
        ClassSlot("Lunes", "7:00 - 9:00", "Fundamentos de Hardware", "Aula 3", "Ing. R. Gómez"),
        ClassSlot("Lunes", "9:30 - 11:30", "Sistemas Operativos", "Laboratorio 1", "Ing. L. Nieto"),
        ClassSlot("Martes", "7:00 - 9:00", "Redes de Datos", "Laboratorio 1", "Ing. A. Ríos"),
        ClassSlot("Martes", "9:30 - 11:30", "Seguridad Informática", "Aula 4", "Ing. A. Ríos"),
        ClassSlot("Miércoles", "7:00 - 9:00", "Ofimática Aplicada", "Laboratorio 2", "Dra. M. Suárez"),
        ClassSlot("Miércoles", "9:30 - 11:30", "Bases de Datos", "Laboratorio 2", "Ing. R. Gómez"),
        ClassSlot("Jueves", "7:00 - 9:00", "Mantenimiento PC", "Taller Técnico", "Tec. S. Vera"),
        ClassSlot("Jueves", "9:30 - 11:30", "Atención al Cliente", "Aula 2", "Psic. C. Díaz"),
        ClassSlot("Viernes", "7:00 - 9:00", "Práctica Empresarial", "Empresa", "Tutor asignado")
    )

    // --------------------------------------------------------------------
    // Requerimiento 09: Alertas y notificaciones de pago
    // --------------------------------------------------------------------
    val pagos: List<PaymentNotice> = listOf(
        PaymentNotice(
            "Matrícula primer semestre",
            "$650.000",
            "30 Sep",
            EstadoPago.VENCE_PRONTAMENTE,
            "Realiza tu pago antes de la fecha límite para evitar mora."
        ),
        PaymentNotice(
            "Pensión de octubre",
            "$180.000",
            "10 Oct",
            EstadoPago.POR_PAGAR,
            "Pago mensual correspondiente al servicio académico."
        ),
        PaymentNotice(
            "Expedición de certificado de notas",
            "$25.000",
            "15 Dic",
            EstadoPago.POR_PAGAR,
            "Certificado parcial solicitado por el estudiante."
        ),
        PaymentNotice(
            "Matrícula período anterior",
            "$650.000",
            "25 Jul",
            EstadoPago.PAGADO,
            "Pago confirmado por caja. Sin novedad."
        )
    )

    // --------------------------------------------------------------------
    // Sesión (Requerimiento 02)
    // --------------------------------------------------------------------
    var currentUser: User?
        get() {
            val nombre = prefs.getString(KEY_NOMBRE, null) ?: return null
            val correo = prefs.getString(KEY_CORREO, null) ?: return null
            val rol = prefs.getString(KEY_ROL, Rol.ASPIRANTE.etiqueta) ?: Rol.ASPIRANTE.etiqueta
            return User(nombre, correo, Rol.desde(rol))
        }
        set(value) {
            val editor = prefs.edit()
            if (value == null) {
                editor.remove(KEY_NOMBRE).remove(KEY_CORREO).remove(KEY_ROL)
            } else {
                editor.putString(KEY_NOMBRE, value.nombre)
                    .putString(KEY_CORREO, value.correo)
                    .putString(KEY_ROL, value.rol.etiqueta)
            }
            editor.apply()
        }

    val estaConectado: Boolean
        get() = currentUser != null

    fun guardarSesion(usuario: User) {
        currentUser = usuario
    }

    fun cerrarSesion() {
        currentUser = null
        FirebaseAuth.getInstance().signOut()
    }

    // --------------------------------------------------------------------
    // Requerimiento 02: Autenticación real con Firebase (correo/clave)
    // --------------------------------------------------------------------
    private val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    /** Inicia sesión en Firebase y guarda la sesión local. Lanza excepción si falla. */
    suspend fun iniciarSesionFirebase(correo: String, clave: String): User {
        val resultado = auth.signInWithEmailAndPassword(correo, clave).await()
        val fb = resultado.user
            ?: throw IllegalStateException("No se pudo obtener el usuario de Firebase.")
        val nombre = fb.displayName?.takeIf { it.isNotBlank() }
            ?: prefs.getString(KEY_NOMBRE, null)
            ?: fb.email?.substringBefore("@")
            ?: "Usuario"
        val rol = prefs.getString(KEY_ROL, Rol.ASPIRANTE.etiqueta) ?: Rol.ASPIRANTE.etiqueta
        val usuario = User(nombre, fb.email ?: correo, Rol.desde(rol))
        guardarSesion(usuario)
        return usuario
    }

    /** Registra una cuenta nueva en Firebase y guarda la sesión local. */
    suspend fun registrarEnFirebase(usuario: User, clave: String): User {
        val resultado = auth.createUserWithEmailAndPassword(usuario.correo, clave).await()
        val fb = resultado.user
            ?: throw IllegalStateException("No se pudo crear la cuenta en Firebase.")
        fb.updateProfile(
            UserProfileChangeRequest.Builder().setDisplayName(usuario.nombre).build()
        ).await()
        guardarSesion(usuario)
        return usuario
    }

    /**
     * Inicio de sesión con respaldo local: intenta Firebase y, si no hay red,
     * valida contra la tabla local cuando la cuenta fue creada sin conexión.
     */
    suspend fun iniciarSesion(correo: String, clave: String): User {
        try {
            return iniciarSesionFirebase(correo, clave)
        } catch (e: Exception) {
            val local = autenticar(correo, clave) ?: throw e
            guardarSesion(local)
            return local
        }
    }

    /** Registro con respaldo local: si Firebase no responde, crea la cuenta localmente. */
    suspend fun registrar(usuario: User, clave: String): User {
        try {
            return registrarEnFirebase(usuario, clave)
        } catch (e: Exception) {
            val error = registrarUsuario(usuario, clave)
            if (error != null) throw IllegalStateException(error)
            guardarSesion(usuario)
            return usuario
        }
    }

    /** Traduce los errores de Firebase Auth a mensajes entendibles. */
    fun mensajeErrorAuth(e: Throwable): String = when (e) {
        is FirebaseAuthWeakPasswordException ->
            "La contraseña es muy débil (mínimo 6 caracteres)."
        is FirebaseAuthUserCollisionException ->
            "Ese correo ya está registrado. Por favor inicia sesión."
        is FirebaseAuthInvalidCredentialsException ->
            "Correo o contraseña incorrectos."
        is FirebaseAuthInvalidUserException ->
            "No existe una cuenta con ese correo."
        is FirebaseAuthException ->
            "No se pudo completar la operación (${e.errorCode})."
        is IllegalStateException ->
            e.message ?: "No se pudo completar la operación."
        else ->
            "Sin conexión con Firebase. Revisa tu internet e inténtalo de nuevo."
    }

    // --------------------------------------------------------------------
    // Requerimiento 03: Preinscripciones
    // Formato por línea: nombre|documento|correo|telefono|programa|fecha
    // --------------------------------------------------------------------
    fun registrarPreinscripcion(p: Preinscripcion) {
        val lineas = lineasPreinscripcion().toMutableList()
        val serializada = listOf(
            p.nombre, p.documento, p.correo, p.telefono, p.programa, p.fecha
        ).joinToString("|")
        lineas.add(0, serializada)
        prefs.edit().putStringSet(KEY_PREINSC, lineas.take(25).toSet()).apply()
    }

    private fun lineasPreinscripcion(): List<String> {
        return prefs.getStringSet(KEY_PREINSC, null)?.toList() ?: emptyList()
    }

    fun consultarPreinscripciones(): List<Preinscripcion> {
        return lineasPreinscripcion().mapNotNull { linea ->
            val partes = linea.split("|")
            if (partes.size == 6) {
                Preinscripcion(partes[0], partes[1], partes[2], partes[3], partes[4], partes[5])
            } else null
        }
    }

    // --------------------------------------------------------------------
    // Usuarios registrados (simulación de autenticación, requisito 02)
    // Formato por línea: nombre|correo|rol|clave
    // --------------------------------------------------------------------
    private fun lineasUsuarios(): List<String> {
        return prefs.getStringSet(KEY_USUARIOS, null)?.toList() ?: emptyList()
    }

    fun registrarUsuario(usuario: User, clave: String): String? {
        val lineas = lineasUsuarios().toMutableList()
        val yaExiste = lineas.any { linea ->
            val partes = linea.split("|")
            partes.size == 4 && partes[1].equals(usuario.correo, ignoreCase = true)
        }
        if (yaExiste) {
            return "Ese correo ya está registrado. Por favor inicia sesión."
        }
        if (clave.length < 4) {
            return "La contraseña debe tener al menos 4 caracteres."
        }
        lineas.add("${usuario.nombre}|${usuario.correo}|${usuario.rol.etiqueta}|$clave")
        prefs.edit().putStringSet(KEY_USUARIOS, lineas.toSet()).apply()
        return null
    }

    fun autenticar(correo: String, clave: String): User? {
        val linea = lineasUsuarios().lastOrNull { l ->
            val partes = l.split("|")
            partes.size == 4 &&
                partes[1].equals(correo, ignoreCase = true) &&
                partes[3] == clave
        } ?: return null
        val partes = linea.split("|")
        return User(partes[0], partes[1], Rol.desde(partes[2]))
    }

    // --------------------------------------------------------------------
    // Utilidad: fecha actual
    // --------------------------------------------------------------------
    fun fechaActual(): String {
        val formato = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return formato.format(Date())
    }

    companion object {
        private const val KEY_NOMBRE = "nombre"
        private const val KEY_CORREO = "correo"
        private const val KEY_ROL = "rol"
        private const val KEY_PREINSC = "preinscripciones"
        private const val KEY_USUARIOS = "usuarios"

        /** URL de la plataforma educativa (Requerimiento 12). Reemplazar si cambia. */
        const val PLATAFORMA_URL = "https://enid.fundacion.edu.co/plataforma/"

        /** URL de la pasarela de pagos PSE (Requerimiento 10). Reemplazar por la real. */
        const val PSE_URL = "https://www.pse.com.co/inicio/transacciones/confirmacionInit"

        /** Número base de WhatsApp de atención (Requerimiento 06). */
        const val WHATSAPP_NUMERO = "573017453334"
    }
}