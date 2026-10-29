import { useState } from 'react'

type IconName = 'home' | 'book' | 'calendar' | 'user' | 'bell' | 'arrow' | 'play' | 'clock'

function Icon({ name, size = 20 }: { name: IconName; size?: number }) {
  const paths: Record<IconName, React.ReactNode> = {
    home: <><path d="m3 10 9-7 9 7"/><path d="M5 9v11h14V9M9 20v-7h6v7"/></>,
    book: <><path d="M4 4h6a3 3 0 0 1 3 3v13a3 3 0 0 0-3-3H4z"/><path d="M20 4h-4a3 3 0 0 0-3 3v13a3 3 0 0 1 3-3h4z"/></>,
    calendar: <><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M16 3v4M8 3v4M3 10h18"/></>,
    user: <><circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/></>,
    bell: <><path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9"/><path d="M10 21h4"/></>,
    arrow: <><path d="m9 18 6-6-6-6"/></>,
    play: <path d="m9 7 8 5-8 5z"/>,
    clock: <><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></>,
  }
  return <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">{paths[name]}</svg>
}

const programs = [
  { title: 'Inteligencia artificial', meta: '8 módulos · 24 horas', image: 'img_13.png', color: 'aqua' },
  { title: 'Gestión empresarial', meta: '10 módulos · 32 horas', image: 'img_23.png', color: 'violet' },
  { title: 'Turismo y hospitalidad', meta: '6 módulos · 18 horas', image: 'img_30.png', color: 'mint' },
]

export default function App() {
  const [activeNav, setActiveNav] = useState('Inicio')
  const [filter, setFilter] = useState('Todos')
  const [notice, setNotice] = useState(false)
  const [completed, setCompleted] = useState<number[]>([0, 1])
  const [registered, setRegistered] = useState(false)
  const [selectedProgram, setSelectedProgram] = useState(0)

  const toggleLesson = (lesson: number) => {
    setCompleted(current =>
      current.includes(lesson) ? current.filter(item => item !== lesson) : [...current, lesson],
    )
  }

  return (
    <main className="app-shell">
      <section className="phone">
        <header className="topbar">
          <img className="brand" src="/enid/logos_y_marca/img_04.png" alt="Fundación ENID" />
          <button className="icon-button" aria-label="Notificaciones" onClick={() => setNotice(!notice)}>
            <Icon name="bell" />
            <span className="notification-dot" />
          </button>
          {notice && <div className="toast">No tienes notificaciones nuevas</div>}
        </header>

        {activeNav === 'Inicio' && <div className="content screen-enter">
          <section className="welcome">
            <div>
              <p className="eyebrow">MIÉRCOLES, 12 DE JUNIO</p>
              <h1>Hola, Andrea</h1>
              <p>Sigamos aprendiendo juntos.</p>
            </div>
            <div className="avatar">AM</div>
          </section>

          <section className="hero-card">
            <div className="hero-copy">
              <span className="hero-tag">CURSO EN PROGRESO</span>
              <h2>Liderazgo y gestión de equipos</h2>
              <div className="progress-label"><span>Tu progreso</span><strong>68%</strong></div>
              <div className="progress"><span /></div>
              <button className="continue-button" onClick={() => setActiveNav('Aprender')}>
                Continuar <Icon name="play" size={17} />
              </button>
            </div>
            <img src="/enid/logos_y_marca/img_74.png" alt="Asistente virtual de ENID" />
            <div className="orb orb-one" />
            <div className="orb orb-two" />
          </section>

          <section className="section-block">
            <div className="section-heading">
              <div><span>EXPLORA</span><h2>Programas para ti</h2></div>
              <button onClick={() => setFilter('Todos')}>Ver todos <Icon name="arrow" size={16} /></button>
            </div>
            <div className="filters" aria-label="Filtrar programas">
              {['Todos', 'Negocios', 'Tecnología'].map(item => (
                <button key={item} className={filter === item ? 'active' : ''} onClick={() => setFilter(item)}>{item}</button>
              ))}
            </div>
            <div className="program-list">
              {programs.map((program, index) => (
                <article className={`program-card ${program.color}`} key={program.title}>
                  <div className="program-visual">
                    <img src={`/enid/ilustraciones_programas/${program.image}`} alt="" />
                  </div>
                  <div className="program-info">
                    <span>{index === 0 ? 'TECNOLOGÍA' : index === 1 ? 'NEGOCIOS' : 'SERVICIOS'}</span>
                    <h3>{program.title}</h3>
                    <p><Icon name="clock" size={15} /> {program.meta}</p>
                  </div>
                  <button className="round-arrow" aria-label={`Ver ${program.title}`} onClick={() => { setSelectedProgram(index); setActiveNav('Aprender') }}><Icon name="arrow" size={18} /></button>
                </article>
              ))}
            </div>
          </section>

          <button className="event-card" onClick={() => setActiveNav('Agenda')}>
            <img src="/enid/modelo_educativo/img_71.png" alt="Clase presencial en auditorio" />
            <div className="event-overlay">
              <span>PRÓXIMO EVENTO</span>
              <h2>Foro de innovación educativa</h2>
              <p><Icon name="calendar" size={16} /> 18 de junio · 9:00 a.m.</p>
            </div>
          </button>

          <p className="partner-label">CON EL RESPALDO DE</p>
          <img className="partners" src="/enid/convenios/img_10.png" alt="Organizaciones aliadas" />
        </div>}

        {activeNav === 'Aprender' && (
          <div className="content inner-screen screen-enter">
            <div className="page-intro">
              <p className="eyebrow">MI APRENDIZAJE</p>
              <h1>Continúa creciendo</h1>
              <p>Avanza a tu ritmo y marca cada lección al terminar.</p>
            </div>
            <div className="course-cover">
              <div>
                <span>PROGRAMA ACTIVO</span>
                <h2>{selectedProgram === 0 ? 'Liderazgo y gestión de equipos' : programs[selectedProgram].title}</h2>
                <p>{completed.length} de 5 lecciones completadas</p>
              </div>
              <img src={`/enid/ilustraciones_programas/${programs[selectedProgram].image}`} alt="" />
            </div>
            <div className="course-progress"><span style={{ width: `${completed.length * 20}%` }} /></div>
            <div className="lesson-heading"><h2>Contenido del curso</h2><span>{completed.length * 20}%</span></div>
            <div className="lesson-list">
              {[
                ['Bienvenida y objetivos', '6 min'],
                ['Tu estilo de liderazgo', '14 min'],
                ['Comunicación efectiva', '18 min'],
                ['Equipos de alto desempeño', '22 min'],
                ['Evaluación final', '10 min'],
              ].map(([title, time], index) => (
                <button key={title} className={completed.includes(index) ? 'lesson done' : 'lesson'} onClick={() => toggleLesson(index)}>
                  <span className="lesson-status">{completed.includes(index) ? '✓' : index + 1}</span>
                  <span><strong>{title}</strong><small><Icon name="clock" size={13} /> {time}</small></span>
                  <Icon name={completed.includes(index) ? 'book' : 'play'} size={18} />
                </button>
              ))}
            </div>
            <div className="achievement">
              <img src="/enid/iconos_decorativos/img_44.png" alt="" />
              <div><span>NUEVO LOGRO</span><h3>¡Vas por buen camino!</h3><p>Completa 3 lecciones para desbloquear tu primera insignia.</p></div>
            </div>
          </div>
        )}

        {activeNav === 'Agenda' && (
          <div className="content inner-screen screen-enter">
            <div className="page-intro">
              <p className="eyebrow">JUNIO 2025</p>
              <h1>Tu agenda</h1>
              <p>Eventos, talleres y sesiones para seguir conectado.</p>
            </div>
            <div className="date-strip">
              {[['L', '16'], ['M', '17'], ['M', '18'], ['J', '19'], ['V', '20']].map(([day, date], index) => (
                <button key={date} className={index === 2 ? 'active' : ''}><span>{day}</span><strong>{date}</strong></button>
              ))}
            </div>
            <section className="agenda-feature">
              <img src="/enid/modelo_educativo/img_71.png" alt="Foro de innovación educativa" />
              <div className="agenda-copy">
                <span>EVENTO DESTACADO</span>
                <h2>Foro de innovación educativa</h2>
                <p><Icon name="clock" size={15} /> 9:00 a.m. · Auditorio principal</p>
                <button onClick={() => setRegistered(!registered)}>{registered ? 'Inscripción confirmada ✓' : 'Reservar mi lugar'}</button>
              </div>
            </section>
            <div className="lesson-heading"><h2>Próximas actividades</h2></div>
            <div className="timeline">
              {[
                ['20 JUN', 'Mentoría grupal', 'Desarrollo profesional · 4:00 p.m.'],
                ['24 JUN', 'Taller de herramientas digitales', 'Tecnología · 10:30 a.m.'],
                ['28 JUN', 'Encuentro de egresados', 'Comunidad ENID · 6:00 p.m.'],
              ].map(([date, title, meta]) => (
                <article key={title}><div>{date}</div><span><strong>{title}</strong><small>{meta}</small></span><Icon name="arrow" size={18} /></article>
              ))}
            </div>
          </div>
        )}

        {activeNav === 'Perfil' && (
          <div className="content inner-screen screen-enter">
            <section className="profile-head">
              <div className="profile-avatar">AM</div>
              <h1>Andrea Morales</h1>
              <p>Estudiante ENID · Desde 2024</p>
              <button>Editar perfil</button>
            </section>
            <div className="stats">
              <div><strong>4</strong><span>Cursos</span></div>
              <div><strong>32h</strong><span>Aprendizaje</span></div>
              <div><strong>3</strong><span>Logros</span></div>
            </div>
            <div className="lesson-heading"><h2>Mis logros</h2><span>Ver todos</span></div>
            <div className="badges">
              {[
                ['img_33.png', 'Primer paso'],
                ['img_55.png', 'Constancia'],
                ['img_62.png', 'Innovadora'],
              ].map(([image, label]) => (
                <div key={label}><div><img src={`/enid/iconos_decorativos/${image}`} alt="" /></div><span>{label}</span></div>
              ))}
            </div>
            <div className="settings">
              {['Mis certificados', 'Notificaciones', 'Ayuda y soporte', 'Privacidad'].map((item, index) => (
                <button key={item}><span className="settings-icon"><Icon name={index === 0 ? 'book' : index === 1 ? 'bell' : 'user'} size={18} /></span><strong>{item}</strong><Icon name="arrow" size={18} /></button>
              ))}
            </div>
            <button className="logout">Cerrar sesión</button>
          </div>
        )}

        <nav className="bottom-nav" aria-label="Navegación principal">
          {[
            ['Inicio', 'home'], ['Aprender', 'book'], ['Agenda', 'calendar'], ['Perfil', 'user'],
          ].map(([label, icon]) => (
            <button key={label} className={activeNav === label ? 'active' : ''} onClick={() => setActiveNav(label)}>
              <Icon name={icon as IconName} /><span>{label}</span>
            </button>
          ))}
        </nav>
      </section>
    </main>
  )
}
