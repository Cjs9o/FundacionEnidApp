package com.fundacion.enid.data

import android.content.Context
import android.content.SharedPreferences
import com.fundacion.enid.model.ClassSlot
import com.fundacion.enid.model.Area
import com.fundacion.enid.model.EstadoPago
import com.fundacion.enid.model.Familiar
import com.fundacion.enid.model.FaqItem
import com.fundacion.enid.model.PaymentNotice
import com.fundacion.enid.model.Preinscripcion
import com.fundacion.enid.model.Program
import com.fundacion.enid.model.Promocion
import com.fundacion.enid.model.RutaStep
import com.fundacion.enid.model.Testimonio
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
    val programas: List<Program> = buildList {
        // ── Programas técnicos laborales (Portafolio ENID 2026) ──────────
        tecnico(
            1, "Analítica de Datos e Inteligencia Artificial",

            Area.SISTEMAS,            "Capacitado para recopilar, analizar e interpretar datos con herramientas de analítica e IA, apoyando la toma de decisiones con ética y responsabilidad.",
            "Auxiliar o analista junior en analítica de datos, IA y transformación digital, en organizaciones públicas y privadas de diversos sectores.",
            listOf("Gestión de datos", "Automatización de procesos", "Automatización con IA", "Desarrollo de agentes de IA"),
            listOf("Empresas de tecnología y consultoría", "Multinacionales tech", "Sector público", "Bancos y entidades financieras")
        )
        tecnico(
            2, "Auxiliar Administrativo",

            Area.ADMINISTRACION,            "Capacitado para apoyar la gestión administrativa, documental, financiera y de servicio al cliente, mediante el uso de herramientas digitales que optimizan los procesos organizacionales.",
            "Asistente Administrativo, Auxiliar de Archivo, Recepcionista, Auxiliar de Talento Humano, Auxiliar de Compras, Auxiliar de Facturación.",
            listOf("Gestión documental y de archivo", "Manejo de herramientas ofimáticas", "Atención y servicio al cliente", "Apoyo en procesos de talento humano"),
            listOf("Sector de la salud", "Sector educativo", "Sector público", "Bancos y entidades financieras")
        )
        tecnico(
            3, "Sistemas y Soporte Técnico",

            Area.SISTEMAS,            "Capacitado para instalar, configurar, mantener y brindar soporte a equipos, redes y sistemas informáticos, utilizando herramientas tecnológicas e inteligencia artificial para solucionar incidentes de manera eficiente.",
            "Auxiliar de Sistemas, Técnico de Soporte Técnico, Mesa de Ayuda (Help Desk), Soporte TI, Auxiliar de Infraestructura Tecnológica, Técnico de Mantenimiento de Equipos de Cómputo o Auxiliar de Redes.",
            listOf("Cirugía de hardware", "Sistemas y rescate", "Redes y WI-FI", "Soporte con IA"),
            listOf("Sector de la salud", "Sector educativo", "Empresa de telecomunicaciones", "Call centers y BPO")
        )
        tecnico(
            4, "Atención a la Primera Infancia",

            Area.PERSONAS,            "Capacitado para promover el desarrollo integral de niños y niñas de 0 a 6 años mediante actividades pedagógicas, lúdicas y de cuidado, favoreciendo entornos seguros e inclusivos.",
            "Auxiliar en Atención a la Primera Infancia, Auxiliar Pedagógico, Agente Educativo, Auxiliar de Jardín Infantil o Cuidador Infantil.",
            listOf("Estimulación oportuna", "Salud y nutrición", "Entornos seguros", "Acompañamiento familiar"),
            listOf("Sector de la salud", "Jardines infantiles", "Fundaciones", "Sector público y privado")
        )
        tecnico(
            5, "Seguridad Ocupacional y Laboral",

            Area.OPERATIVOS,            "Capacitado para apoyar la implementación del Sistema de Gestión de Seguridad y Salud en el Trabajo, identificar riesgos laborales, promover la prevención de accidentes y fomentar ambientes de trabajo seguros, conforme a la norma vigente.",
            "Auxiliar en Seguridad y Salud en el Trabajo, Auxiliar del SG-SST, Inspector de Seguridad, Auxiliar de Gestión del Riesgo o Asistente de Seguridad Ocupacional.",
            listOf("Implementación de SG-SST", "Control de riesgos", "Planes de emergencia", "Investigación y analítica"),
            listOf("Sector industrial y manufactura", "Sector de construcción", "Sector minero y energético", "Consultoras en SST")
        )
        tecnico(
            6, "Contable y Financiero",

            Area.ADMINISTRACION,            "Conocimientos en registro contable, estados financieros, conciliaciones bancarias y procesos tributarios básicos. Manejo de herramientas contables y ofimáticas, con atención al detalle y ética profesional.",
            "Apoya el área contable y financiera de empresas registrando transacciones, elaborando comprobantes, haciendo conciliaciones bancarias y generando reportes financieros.",
            listOf("Registro contable y financiero", "Manejo de software contable", "Conciliaciones bancarias", "Atención al detalle y ética profesional"),
            listOf("Empresas comerciales", "Sector público", "Sector salud", "Cooperativas y cajas de compensación")
        )
        tecnico(
            7, "Mercadeo Digital y Redes Sociales",

            Area.MARKETING,            "Conocimientos en creación de contenido, manejo de redes sociales y estrategias básicas de publicidad digital, con creatividad y orientación a resultados.",
            "Apoya la gestión de marca en medios digitales: creación de contenido, manejo de redes sociales, campañas publicitarias básicas y análisis de métricas.",
            listOf("Creación de contenido digital", "Manejo de redes sociales", "Publicidad digital básica", "Análisis de métricas y resultados"),
            listOf("Agencias de marketing digital y publicidad", "Restaurantes y sector gastronómico", "Sector inmobiliario", "Turismo y hotelería")
        )
        tecnico(
            8, "Auxiliar de Compras, Almacén e Inventarios",

            Area.OPERATIVOS,            "Conocimientos en gestión de compras, control de existencias y manejo de bodega. Capacitado(a) en recepción, almacenamiento y despacho de mercancía, con atención al detalle y organización.",
            "Apoya compras y logística: cotizaciones, órdenes de compra, control de inventarios y despacho de mercancía.",
            listOf("Gestión de compras y cotizaciones", "Control de inventarios y existencias", "Recepción y despacho de mercancía", "Manejo de software de inventarios"),
            listOf("Comercio y retail", "Sector farmacéutico", "Logística y transporte", "E-commerce")
        )
        tecnico(
            9, "Diseño Gráfico",

            Area.ARTE,            "Composición visual, edición de imágenes y manejo de software de diseño, con creatividad y sentido estético.",
            "Apoya la creación de piezas visuales: diseño de logos, artes para redes sociales, material publicitario e impreso, usando herramientas como Photoshop e Illustrator.",
            listOf("Composición visual y creatividad", "Manejo de Photoshop e Illustrator", "Diseño de piezas publicitarias e impresas", "Atención al detalle y sentido estético"),
            listOf("Agencias de marketing y publicidad", "Empresas editoriales", "Sector audiovisual", "Imprentas y litografías")
        )
        tecnico(
            10, "Servicios Hosteleros y Turismo",

            Area.TURISMO,            "Conocimientos en atención al cliente, gestión de reservas y servicios de alojamiento y alimentación, con vocación de servicio y buena presentación personal.",
            "Apoya la operación de hoteles, restaurantes y agencias de turismo: atención al huésped, gestión de reservas, check-in/check-out y servicio de alimentos y bebidas.",
            listOf("Atención al cliente y servicio", "Gestión de reservas y check-in/check-out", "Servicio de alimentos y bebidas", "Buena presentación y comunicación"),
            listOf("Hoteles y hostels", "Agencias de viajes y turismo", "Cruceros y transporte fluvial", "Parques temáticos y de entretenimiento")
        )
        tecnico(
            11, "Control y Manejo Ambiental",

            Area.AMBIENTE,            "Conocimientos en gestión de residuos, normatividad ambiental y procesos de conservación, con responsabilidad y conciencia ecológica.",
            "Apoya la gestión ambiental de empresas: manejo de residuos, control de impacto ambiental, cumplimiento normativo y apoyo en planes de sostenibilidad.",
            listOf("Monitoreo de calidad de aire, agua y suelo", "Apoyo en estudios de impacto ambiental", "Acompañamiento en auditorías ambientales", "Verificación del cumplimiento de normatividad ambiental"),
            listOf("Empresas de servicios públicos", "ONG y fundaciones ambientales", "Consultoras ambientales", "Empresas de manejo de residuos")
        )
        tecnico(
            12, "Auxiliar Judicial y Notarial",

            Area.LEGAL,            "Conocimientos en trámites judiciales, notariales y manejo documental, con discreción y rigurosidad en el manejo de la información.",
            "Apoya despachos judiciales y notarías: elaboración y radicación de documentos, atención al usuario, archivo y organización de expedientes, y apoyo en trámites legales.",
            listOf("Manejo de trámites judiciales y notariales", "Organización y archivo documental", "Atención al usuario", "Discreción y rigurosidad"),
            listOf("Notarías", "Fiscalía General de la Nación", "Despachos y bufetes de abogados", "Cámaras de comercio")
        )
        tecnico(
            13, "Auxiliar de Entrenamiento Deportivo y Recreación",

            Area.PERSONAS,            "Conocimientos en actividad física, planes de entrenamiento básicos y actividades recreativas, con condición física y vocación de servicio.",
            "Apoya procesos de entrenamiento y recreación: acompañamiento en rutinas de ejercicio, organización de actividades deportivas y recreativas, y promoción de hábitos de vida saludable.",
            listOf("Apoyo en planes de entrenamiento", "Organización de actividades recreativas", "Promoción de hábitos saludables", "Condición física y vocación de servicio"),
            listOf("Sector educativo", "Gimnasios y centros de acondicionamiento físico", "Cajas de compensación familiar", "Ligas y federaciones deportivas")
        )
        tecnico(
            14, "Gestor de Proyectos Sociales y Comunitarios",

            Area.PERSONAS,            "Conocimientos en formulación de proyectos, trabajo comunitario y gestión social, con liderazgo y sentido de responsabilidad social.",
            "Apoya el diseño y ejecución de proyectos sociales: diagnóstico de necesidades comunitarias, formulación y seguimiento de proyectos, y articulación con entidades públicas o privadas.",
            listOf("Formulación y seguimiento de proyectos", "Trabajo y diagnóstico comunitario", "Articulación interinstitucional", "Liderazgo y responsabilidad social"),
            listOf("ONG y fundaciones", "Sector público", "Iglesias y organizaciones religiosas", "Entidades de atención a población vulnerable")
        )
        tecnico(
            15, "Mantenimiento de Mecatrónica, Robótica y Modelado 3D",

            Area.SISTEMAS,            "Conocimientos en sistemas mecánicos, electrónicos y de control, y diseño/modelado 3D, con habilidad técnica y pensamiento analítico.",
            "Apoya el mantenimiento y montaje de sistemas mecatrónicos y robóticos: diagnóstico de fallas, mantenimiento preventivo/correctivo y diseño de piezas mediante modelado 3D.",
            listOf("Mantenimiento de sistemas mecatrónicos", "Diagnóstico y reparación técnica", "Modelado y diseño 3D", "Pensamiento analítico y técnico"),
            listOf("Sector automotriz", "Sector metalmecánico", "Empresas de impresión 3D y prototipado", "Startups de tecnología e innovación")
        )
        tecnico(
            16, "Actuación, Expresión Escénica y Asistencia Pedagógica Teatral",

            Area.ARTE,            "Conocimientos en técnicas de actuación, expresión corporal y apoyo pedagógico en procesos teatrales, con creatividad y sensibilidad artística.",
            "Apoya procesos artísticos y pedagógicos en teatro: participación en montajes escénicos, apoyo en talleres de formación teatral y acompañamiento a procesos creativos con niños, jóvenes o adultos.",
            listOf("Técnicas de actuación y expresión corporal", "Apoyo pedagógico en formación teatral", "Participación en montajes escénicos", "Creatividad y sensibilidad artística"),
            listOf("Teatros y salas de espectáculos", "Producciones audiovisuales", "Sector publicitario", "Festivales de teatro y artes escénicas")
        )
        tecnico(
            17, "Producción Audiovisual y Creación de Contenidos Digitales",

            Area.ARTE,            "Conocimientos en grabación, edición de video y producción de contenido digital.",
            "Apoya la producción de contenido audiovisual: grabación, edición de video, creación de piezas para redes sociales y plataformas digitales.",
            listOf("Grabación y edición de video", "Creación de contenido digital", "Manejo de herramientas audiovisuales", "Creatividad y sentido narrativo"),
            listOf("Agencias de marketing digital y publicidad", "Plataformas digitales y streaming", "Empresas de videojuegos", "Productoras audiovisuales")
        )
        tecnico(
            18, "Analista de Talento Humano",

            Area.PERSONAS,            "Conocimientos en procesos de reclutamiento, selección y bienestar laboral, con habilidades comunicativas, organización y orientación al servicio para la gestión del talento humano.",
            "Apoya procesos de gestión humana: reclutamiento y selección de personal, afiliaciones a seguridad social, elaboración de nómina y programas de bienestar y capacitación.",
            listOf("Elaboración de contratos y documentación laboral", "Elaboración y liquidación de nómina", "Conocimiento básico de normatividad laboral colombiana", "Manejo de procesos de reclutamiento y selección de personal"),
            listOf("Empresas de todos los sectores", "Sector financiero", "Sector retail y comercio", "Sector público")
        )
        tecnico(
            19, "Auxiliar de Criminalística y Asistencia Judicial",

            Area.LEGAL,            "Conocimientos en técnicas básicas de criminalística, procedimientos judiciales y manejo de evidencias, con rigurosidad y discreción.",
            "Apoya procesos judiciales y de investigación: recolección y manejo básico de evidencias, apoyo documental en procesos judiciales y asistencia en diligencias.",
            listOf("Técnicas básicas de criminalística", "Manejo y cadena de custodia de evidencias", "Apoyo en procesos judiciales", "Rigurosidad y discreción"),
            listOf("CTI (Cuerpo Técnico de Investigación)", "Empresas de seguridad privada e investigación", "Fiscalía General de la Nación", "Policía Nacional (áreas de policía judicial)")
        )
        tecnico(
            20, "Agente de Tránsito y Transporte",

            Area.OPERATIVOS,            "Conocimientos en normatividad de tránsito, señalización vial y control del transporte, con disciplina y sentido de responsabilidad.",
            "Apoya el control y regulación de la movilidad: aplicación de normas de tránsito, señalización vial, control de flujo vehicular y apoyo en operativos de seguridad vial.",
            listOf("Normatividad de tránsito y transporte", "Señalización y control vial", "Apoyo en operativos de seguridad", "Primeros auxilios básicos"),
            listOf("Alcaldías y gobernaciones", "Empresas de logística y transporte de carga", "Empresas de seguridad vial privada", "Policía Nacional (área de tránsito)")
        )
        tecnico(
            21, "Desarrollo de Software",

            Area.SISTEMAS,            "Conocimientos en programación, lógica computacional y construcción de aplicaciones, con pensamiento analítico y capacidad de resolución de problemas.",
            "Apoya la construcción y mantenimiento de software: programación, pruebas básicas, corrección de errores y documentación de aplicaciones.",
            listOf("Programación y lógica computacional", "Desarrollo y pruebas de software", "Documentación técnica", "Conocimientos básicos de estructuras de datos"),
            listOf("Multinacionales tech", "Empresas de e-commerce y retail", "Telecomunicaciones", "Empresas de videojuegos")
        )
        tecnico(
            22, "Auxiliar de Veterinaria",

            Area.PERSONAS,            "Conocimientos en cuidado y manejo básico de animales, apoyo en procedimientos veterinarios y primeros auxilios, con vocación de servicio y sensibilidad animal.",
            "Apoya la atención veterinaria: manejo y sujeción de animales, asistencia en consultas y procedimientos, cuidados básicos e higiene.",
            listOf("Manejo y cuidado básico de animales", "Asistencia en procedimientos veterinarios", "Primeros auxilios animales", "Apoyo en toma y procesamiento de muestras de laboratorio"),
            listOf("Zoológicos y acuarios", "Hospitales veterinarios", "Fincas y empresas ganaderas (sector pecuario)", "Laboratorios de productos veterinarios y farmacéuticos")
        )
        tecnico(
            23, "Ciberseguridad, Redes y Telemática",

            Area.SISTEMAS,            "Conocimientos en seguridad informática, administración de redes y sistemas de telecomunicaciones, con pensamiento analítico y responsabilidad en el manejo de la información.",
            "Apoya la protección y administración de infraestructura tecnológica: configuración y monitoreo de redes, prevención de amenazas informáticas y soporte en sistemas de telecomunicaciones.",
            listOf("Seguridad informática básica", "Administración y monitoreo de redes", "Cableado estructurado y telecomunicaciones", "Configuración y administración de redes (routers, switches)"),
            listOf("Telecomunicaciones: Claro, Movistar, Tigo", "Multinacionales tech", "Empresas de tecnología y consultoras de TI", "Data centers y proveedores de servicios en la nube")
        )
        tecnico(
            24, "Gestión Documental y Archivo Digital",

            Area.LEGAL,            "Conocimientos en organización documental, digitalización y normatividad archivística, con orden y confidencialidad.",
            "Apoya la gestión de archivos: clasificación, digitalización y custodia de documentos físicos y digitales, y cumplimiento de normas archivísticas.",
            listOf("Organización, clasificación y foliación de archivos", "Digitalización de archivos", "Manejo de bases de datos y herramientas ofimáticas", "Gestión de correspondencia y radicación de documentos"),
            listOf("Sector salud", "Sector educativo", "Notarías y curadurías urbanas", "Sector público")
        )
        tecnico(
            25, "Auxiliar de Farmacia y Droguería",

            Area.PERSONAS,            "Conocimientos en dispensación de medicamentos, manejo de inventarios farmacéuticos y atención al cliente, con responsabilidad y precisión.",
            "Apoya la operación de farmacias y droguerías: dispensación de medicamentos, control de inventarios, atención al cliente y manejo de normatividad farmacéutica básica.",
            listOf("Manejo de dispensación de medicamentos", "Control de inventarios y vencimientos de medicamentos", "Manejo de software de facturación y punto de venta", "Manejo de historia clínica básica y formulación médica"),
            listOf("Farmacia hospitalaria", "EPS e IPS", "Centros de salud y puestos de atención primaria", "Cadenas de farmacias: Cruz Verde, Farmatodo")
        )

        // ── Cursos y diplomados especializados (formación superior) ───────
        complementario(26, "Mecanismos Alternativos de Resolución de Conflictos (MASC)", TipoPrograma.CURSO, Area.LEGAL)
        complementario(27, "Gestión Administrativa con Énfasis en Derechos Sociales", TipoPrograma.CURSO, Area.ADMINISTRACION)
        complementario(28, "Nómina, Prestaciones Sociales y Aportes Parafiscales", TipoPrograma.CURSO, Area.ADMINISTRACION)
        complementario(29, "Sistemas de Gestión Integral y Estrategias de Talento Humano", TipoPrograma.CURSO, Area.PERSONAS)
        complementario(30, "Gerencia de Mercado y Ventas con Énfasis en Marketing", TipoPrograma.CURSO, Area.MARKETING)
        complementario(31, "Gestión Administrativa en Salud Enfocada en Alta Gerencia", TipoPrograma.CURSO, Area.ADMINISTRACION)
        complementario(32, "Gerencia, Control y Supervisión en S.S.T, Gestión Ambiental y H.S.E.Q", TipoPrograma.CURSO, Area.OPERATIVOS)
        complementario(33, "Logística Empresarial", TipoPrograma.CURSO, Area.OPERATIVOS)
        complementario(34, "Código Sustantivo del Trabajo", TipoPrograma.CURSO, Area.LEGAL)
        complementario(35, "Gerencia en Planificación de Proyectos (P.M.I)", TipoPrograma.CURSO, Area.ADMINISTRACION)
        complementario(36, "Ejercicio, Actividad Física, Salud y Rendimiento", TipoPrograma.CURSO, Area.PERSONAS)

        // ── Diplomados en liderazgo comunitario y desarrollo social ───────
        complementario(37, "Diplomado en Guía Turístico Local", TipoPrograma.DIPLOMADO, Area.TURISMO)
        complementario(38, "Diplomado en Guardabosques", TipoPrograma.DIPLOMADO, Area.AMBIENTE)
        complementario(39, "Diplomado en Liderazgo Comunitario y Gestión Social", TipoPrograma.DIPLOMADO, Area.PERSONAS)
        complementario(40, "Diplomado en Gestión de Proyectos Comunitarios", TipoPrograma.DIPLOMADO, Area.PERSONAS)
        complementario(41, "Diplomado en Desarrollo Local y Sostenibilidad", TipoPrograma.DIPLOMADO, Area.AMBIENTE)
        complementario(42, "Diplomado en Gestión de Conflictos y Mediación Comunitaria", TipoPrograma.DIPLOMADO, Area.LEGAL)
        complementario(43, "Diplomado en Participación Ciudadana y Gobernanza Local", TipoPrograma.DIPLOMADO, Area.LEGAL)
        complementario(44, "Diplomado en Empoderamiento de Mujeres en el Liderazgo Comunitario", TipoPrograma.DIPLOMADO, Area.PERSONAS)
        complementario(45, "Diplomado en Innovación Social y Liderazgo Comunitario", TipoPrograma.DIPLOMADO, Area.PERSONAS)
        complementario(46, "Diplomado en Comunicación para el Cambio Social", TipoPrograma.DIPLOMADO, Area.ARTE)

        // ── Diplomados de desarrollo comunitario y ambiental ──────────────
        complementario(47, "Diplomado en Gestión Ambiental", TipoPrograma.DIPLOMADO, Area.AMBIENTE)
        complementario(48, "Diplomado en Cambio Climático", TipoPrograma.DIPLOMADO, Area.AMBIENTE)
        complementario(49, "Diplomado en Energías Renovables", TipoPrograma.DIPLOMADO, Area.AMBIENTE)
        complementario(50, "Diplomado en Economía Circular", TipoPrograma.DIPLOMADO, Area.AMBIENTE)
        complementario(51, "Diplomado en Sostenibilidad y Responsabilidad Social Corporativa (RSC)", TipoPrograma.DIPLOMADO, Area.AMBIENTE)
        complementario(52, "Diplomado en Gestión de Recursos Hídricos", TipoPrograma.DIPLOMADO, Area.AMBIENTE)
        complementario(53, "Diplomado en Biodiversidad y Conservación de Ecosistemas", TipoPrograma.DIPLOMADO, Area.AMBIENTE)
        complementario(54, "Diplomado en Agroecología y Agricultura Sostenible", TipoPrograma.DIPLOMADO, Area.AMBIENTE)
        complementario(55, "Diplomado en Gestión de Proyectos Ambientales", TipoPrograma.DIPLOMADO, Area.AMBIENTE)
        complementario(56, "Diplomado en Derecho Ambiental", TipoPrograma.DIPLOMADO, Area.AMBIENTE)

        // ── Diplomados en política y sostenibilidad urbana ────────────────
        complementario(57, "Diplomado en Políticas Públicas y Desarrollo Comunitario", TipoPrograma.DIPLOMADO, Area.ADMINISTRACION)
        complementario(58, "Urbanismo 4.0: Diplomado en Derecho, Participación y Herramientas Digitales para Ciudades Inteligentes", TipoPrograma.DIPLOMADO, Area.LEGAL)
        complementario(59, "Fortaleza Organizacional: Diplomado en Control Interno y Gestión de Riesgos", TipoPrograma.DIPLOMADO, Area.ADMINISTRACION)
        complementario(60, "Conectando Puntadas: Un Seminario de Creación Textil", TipoPrograma.SEMINARIO, Area.ARTE)
        complementario(61, "Diplomado en Gestión de Crisis y Manejo de Situaciones de Emergencia", TipoPrograma.DIPLOMADO, Area.OPERATIVOS)

        // ── Cursos y seminarios en sostenibilidad y turismo ───────────────
        complementario(62, "Gestión Forestal Guardabosques", TipoPrograma.CURSO, Area.AMBIENTE)
        complementario(63, "Gestión Forestal Viverista", TipoPrograma.CURSO, Area.AMBIENTE)
        complementario(64, "Gestión del Recurso Hídrico - Plomero", TipoPrograma.CURSO, Area.OPERATIVOS)
        complementario(65, "Mantenimiento de Poda y Césped", TipoPrograma.CURSO, Area.OPERATIVOS)
        complementario(66, "Gestión Turística Local", TipoPrograma.CURSO, Area.TURISMO)
        complementario(67, "Seminario en Agricultura", TipoPrograma.SEMINARIO, Area.AMBIENTE)
    }

    // ── Constructores de programas del catálogo (Portafolio ENID 2026) ──
    private fun MutableList<Program>.tecnico(
        id: Int,
        nombre: String,
        area: Area,
        descripcion: String,
        perfilOcupacional: String,
        competencias: List<String>,
        campoLaboral: List<String>
    ) {
        add(
            Program(
                id = id,
                nombre = nombre,
                tipo = TipoPrograma.TECNICO,
                area = area,
                descripcion = descripcion,
                duracion = "6 meses",
                modalidad = "Presencial",
                competencias = competencias,
                perfilOcupacional = perfilOcupacional,
                campoLaboral = campoLaboral,
                sedes = listOf("Chía", "Sopó", "Girardot")
            )
        )
    }

    private fun MutableList<Program>.complementario(
        id: Int,
        nombre: String,
        tipo: TipoPrograma,
        area: Area
    ) {
        add(
            Program(
                id = id,
                nombre = nombre,
                tipo = tipo,
                area = area,
                descripcion = "Educación complementaria modalidad " +
                    "${if (tipo == TipoPrograma.SEMINARIO) "seminario" else tipo.etiqueta.lowercase()} " +
                    "para el desarrollo profesional y social.",
                duracion = "Consulte con un asesor",
                modalidad = "Presencial / Semipresencial",
                competencias = emptyList(),
                perfilOcupacional = "Formación complementaria que fortalece el desarrollo " +
                    "profesional y social de nuestros estudiantes y egresados.",
                campoLaboral = emptyList(),
                sedes = listOf("Chía", "Sopó")
            )
        )
    }

    /** Programas técnicos: los 25 del Portafolio ENID 2026. */
    val programasTecnicos: List<Program>
        get() = programas.filter { it.tipo == TipoPrograma.TECNICO }

    /** Formación complementaria: cursos, seminarios y diplomados. */
    val programasComplementarios: List<Program>
        get() = programas.filter { it.tipo != TipoPrograma.TECNICO }

    fun buscarProgramas(consulta: String, tipo: TipoPrograma?, area: Area?): List<Program> =
        programas.filter { programa ->
            val coincideTexto = consulta.isBlank() ||
                programa.nombre.contains(consulta, ignoreCase = true) ||
                programa.descripcion.contains(consulta, ignoreCase = true) ||
                programa.perfilOcupacional.contains(consulta, ignoreCase = true)
            val coincideTipo = tipo == null || programa.tipo == tipo
            val coincideArea = area == null || programa.area == area
            coincideTexto && coincideTipo && coincideArea
        }

    /**
     * Requerimiento 05: promociones y beneficios.
     *
     * Se deja **vacía a propósito**. Publicar un porcentaje, una fecha límite o una
     * alianza que ENID no ha confirmado sería inventar una condición comercial, y eso
     * se pierde en cuanto el beneficiario pregunta. La pantalla y los modelos ya
     * están listos: seLlena con los datos oficiales y aparece en el Home sin más cambios.
     */
    val promociones: List<Promocion> = emptyList()

    /**
     * Requerimiento 06: testimonios de estudiantes.
     *
     * También vacía por el mismo motivo que [promociones]: no se inventan nombres ni
     * citas de personas que no han dado su testimonio.
     */
    val testimonios: List<Testimonio> = emptyList()

    /**
     * Requerimiento 02: camino B ("Quiero estudiar en ENID").
     * [rutaProspecto] se define en el `companion object` de esta clase
     * porque son datos constantes de la Fundación: no dependen de la sesión
     * ni de nada guardado en el dispositivo, y así se pueden verificar en
     * pruebas unitarias sin levantar un `Context`.
     */

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
            "Matrícula — Sistemas y Computación Básica",
            "$180.000",
            "13 Oct",
            EstadoPago.VENCE_PRONTAMENTE,
            "Realiza tu pago antes de la fecha límite para evitar mora."
        ),
        PaymentNotice(
            "Matrícula Agosto 2025",
            "$180.000",
            "05 Ago",
            EstadoPago.PAGADO,
            "Pago confirmado. Ciclo 2025-2."
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
    // Requerimiento 08: seguimiento de interesados
    // Guarda los programas que la persona marcó como "me interesa", para poder
    // lembrarle su decisión y mostrarle el resumen con los siguientes pasos.
    // --------------------------------------------------------------------
    fun registrarInteres(programaId: Int) {
        val actuales = prefs.getStringSet(KEY_INTERESES, null)?.toMutableSet() ?: mutableSetOf()
        if (actuales.add(programaId.toString())) {
            prefs.edit().putStringSet(KEY_INTERESES, actuales).apply()
        }
    }

    fun quitarInteres(programaId: Int) {
        val actuales = prefs.getStringSet(KEY_INTERESES, null)?.toMutableSet() ?: return
        if (actuales.remove(programaId.toString())) {
            prefs.edit().putStringSet(KEY_INTERESES, actuales).apply()
        }
    }

    fun esInteres(programaId: Int): Boolean =
        prefs.getStringSet(KEY_INTERESES, null)?.contains(programaId.toString()) == true

    /** Programas marcados como "me interesa", en el orden en que se guardaron. */
    fun consultarIntereses(): List<Program> {
        val guardados = prefs.getStringSet(KEY_INTERESES, null) ?: return emptyList()
        return programas.filter { guardados.contains(it.id.toString()) }
    }

    /**
     * Siguiente paso pendiente de un interesado: si ya dejó preinscripción, se
     * guide al asesor; si no, a completar la preinscripción. Es el "qué hago ahora"
     * que pedía el requerimiento 08.
     */
    fun siguientePaso(programa: String?): String {
        val tienePreinscripcion = consultarPreinscripciones().any { it.programa == programa }
        return if (tienePreinscripcion) {
            "Ya dejaste tu preinscripción. El siguiente paso es hablar con un asesor para revisar tu proceso."
        } else {
            "Te interesa este programa. El siguiente paso es dejar tu información para que te contactemos."
        }
    }

    // --------------------------------------------------------------------
    // Requerimiento 03: Preinscripciones
    // Formato por línea: nombre|documento|correo|telefono|programa|fecha...
    // --------------------------------------------------------------------
    /**
     * Guarda la preinscripción, de la más reciente a la más antigua.
     *
     * Se usa un solo `String` con las líneas separadas por salto de línea, y no un
     * `StringSet` como antes: un conjunto **no tiene orden garantizado**, así que
     * "Mis interesados" podía mostrar en "lo que ya dejaste" una preinscripción
     * vieja en vez de la última. Dentro de una línea nunca hay salto de línea
     * porque [PreinscripcionTexto] los convierte en espacios.
     *
     * Lo que hubiera en el `StringSet` viejo se lee igual, para no perder lo ya
     * registrado en los teléfonos donde la app ya se usó.
     */
    fun registrarPreinscripcion(p: Preinscripcion) {
        val lineas = listOf(PreinscripcionTexto.serializar(p)) + lineasPreinscripcion()
        prefs.edit()
            .putString(KEY_PREINSC, lineas.take(MAX_PREINSC).joinToString("\n"))
            .remove(KEY_PREINSC_VIEJO)
            .apply()
    }

    private fun lineasPreinscripcion(): List<String> {
        val actuales = prefs.getString(KEY_PREINSC, null)
        if (!actuales.isNullOrBlank()) return actuales.split("\n").filter { it.isNotBlank() }
        // Datos de la versión anterior, que sí estaban en un conjunto sin orden.
        return prefs.getStringSet(KEY_PREINSC_VIEJO, null)?.filter { it.isNotBlank() } ?: emptyList()
    }

    fun consultarPreinscripciones(): List<Preinscripcion> =
        lineasPreinscripcion().mapNotNull(PreinscripcionTexto::leer)

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

        // ----------------------------------------------------------------
        // Requerimiento 02: camino B ("Quiero estudiar en ENID").
        // ----------------------------------------------------------------
        // Es la ruta que ve la persona que aún no está inscrita. Se muestra
        // como lista de pasos en el Home del invitado para que quede claro qué
        // sigue después de tomar la decisión. Los pasos que dependen del asesor
        // o se resuelven en sede se marcan con [RutaStep.enSede] en vez de
        // prometer una pantalla que la app no tiene.
        //
        // El invitado solo llega hasta el paso 4. Los pasos 9 y 10 (pago y
        // plataforma) antes apuntaban a `Payments` y `Platform`, así que
        // cualquiera que entrara como invitado podía abrir la pantalla de pagos
        // de un estudiante —con un historial de pagos inventado— y la de la
        // plataforma educativa. Esos módulos son de estudiante y se resuelven
        // con el asesor o en sede, igual que los pasos 5 a 8.
        val rutaProspecto: List<RutaStep> = listOf(
            RutaStep(1, "Conocer ENID", "Conoce la Fundación, su misión y dónde encontrarnos.", "Locations"),
            RutaStep(2, "Explorar programas", "Revisa los 67 programas técnicos y complementarios.", "Catalog"),
            RutaStep(3, "Elegir programa", "Responde 5 preguntas y te sugerimos los programas para ti.", "ProgramFinder"),
            RutaStep(4, "Recibir información", "Déjanos tus datos y te contactamos con la información del programa.", "Preinscription"),
            RutaStep(5, "Hablar con un asesor", "Un asesor ENID te acompaña para resolver dudas del proceso.", enSede = true),
            RutaStep(6, "Completar la preinscripción", "Formalizas tu interés y dejas listo tu proceso de admisión.", enSede = true),
            RutaStep(7, "Entregar documentos", "Los documentos se validan con el asesor, en sede o por el canal que él indique.", enSede = true),
            RutaStep(8, "Realizar la matrícula", "Proceso de matrícula para activos y nuevos admitidos.", enSede = true),
            RutaStep(9, "Realizar el pago", "Pagas tu matrícula con el asesor, por PSE o en sede.", enSede = true),
            RutaStep(10, "¡Bienvenido a ENID!", "Al matricularte activamos tu acceso a la plataforma y al horario.", enSede = true)
        )

        private const val KEY_NOMBRE = "nombre"
        private const val KEY_CORREO = "correo"
        private const val KEY_ROL = "rol"
        private const val KEY_PREINSC = "preinscripciones_ordenadas"
        private const val KEY_PREINSC_VIEJO = "preinscripciones"
        private const val MAX_PREINSC = 25
        private const val KEY_INTERESES = "intereses"
        private const val KEY_USUARIOS = "usuarios"

        /** URL de la plataforma educativa (Requerimiento 12). Reemplazar si cambia. */
        const val PLATAFORMA_URL = "https://enid.fundacion.edu.co/plataforma/"

        /** URL de la pasarela de pagos PSE (Requerimiento 10). Reemplazar por la real. */
        const val PSE_URL = "https://www.pse.com.co/inicio/transacciones/confirmacionInit"

        /** Sitio web institucional (Requerimiento 11). Reemplazar si cambia. */
        const val SITIO_WEB = "https://www.enid.edu.co"

        /** Número base de WhatsApp de atención (Requerimiento 06). */
        const val WHATSAPP_NUMERO = "573017453334"

        // ----------------------------------------------------------------
        // Montos de matrícula  ·  PENDIENTE DE CONFIRMAR POR LA FUNDACIÓN
        // ----------------------------------------------------------------
        // Antes el valor estaba escrito en cinco sitios y con dos cifras
        // distintas (180.000 en Pagos y Beneficios, 830.000 en PSE). Ahora hay
        // una sola fuente y las pantallas la leen de aquí. El valor sigue
        // siendo referencial: la Fundación aún no ha confirmado la cifra real
        // (Fase 16 del PLANDESARROLLO), así que las pantallas que lo muestran
        // lo etiquetan como preliminar en vez de presentarlo como un hecho.
        const val MONTO_MATRICULA = 180000

        /** El monto de matrícula, formateado como pesos colombianos. */
        fun montoMatriculaTexto(conSigno: Boolean = true): String =
            (if (conSigno) "$" else "") + "%,d".format(MONTO_MATRICULA).replace(',', '.')

        /** Aviso que acompaña a los montos mientras la Fundación no confirme la cifra. */
        const val AVISO_MONTO =
            "Valor preliminar. La Fundación ENID aún no confirma el monto oficial " +
                "de la matrícula."
    }
}