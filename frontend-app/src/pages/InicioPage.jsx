import { Link } from 'react-router-dom'
import PageHeader from '../components/ui/PageHeader'
import { getAccountTypeLabel, getAccountBadgeClass, isAdmin } from '../auth/roles'

export default function InicioPage() {
  const admin = isAdmin()

  return (
    <div className="page page--inicio">
      <section className="inicio-hero">
        <p className="inicio-hero__eyebrow">Innovatech Solutions</p>
        <h2 className="inicio-hero__title">
          {admin ? 'Gestión de proyectos tecnológicos' : 'Bienvenido, colaborador'}
        </h2>
        <p className="inicio-hero__text">
          {admin
            ? 'Panel demo: trabajadores, proyectos y tablero Kanban integrados con Keycloak y microservicios.'
            : 'Consulta tus proyectos, tareas asignadas y plazos. Tu cuenta tiene permisos limitados (sin finanzas ni administración).'}
        </p>
        <div className={`account-banner ${admin ? 'account-banner--admin' : 'account-banner--worker'}`} style={{ marginTop: '1rem' }}>
          <div>
            <span className="account-banner__eyebrow">Tipo de cuenta</span>
            <strong>{getAccountTypeLabel()}</strong>
          </div>
          <span className={`badge ${getAccountBadgeClass()}`}>{getAccountTypeLabel()}</span>
        </div>
        <div className="inicio-hero__actions">
          <Link to="/dashboard" className="btn btn--primary">
            {admin ? 'Ir al dashboard' : 'Ir a mi panel'}
          </Link>
          <Link to="/tareas" className="btn btn--secondary">
            {admin ? 'Abrir tablero Trello' : 'Ver mis tareas'}
          </Link>
        </div>
      </section>

      <PageHeader
        title="Accesos rápidos"
        subtitle={admin ? 'Elige un módulo para comenzar' : 'Módulos disponibles para tu perfil'}
      />
      <div className="inicio-grid">
        {admin && (
          <>
            <Link to="/trabajadores" className="inicio-card">
              <h3>Trabajadores</h3>
              <p>Equipo, roles y capacidad horaria.</p>
            </Link>
            <Link to="/finanzas" className="inicio-card">
              <h3>Finanzas empresa</h3>
              <p>Ingresos, costos y márgenes en pesos chilenos.</p>
            </Link>
            <Link to="/capacidad" className="inicio-card">
              <h3>Capacidad del equipo</h3>
              <p>Horas, nómina y sobrecarga por trabajador.</p>
            </Link>
          </>
        )}
        <Link to="/proyectos" className="inicio-card">
          <h3>{admin ? 'Proyectos' : 'Mis proyectos'}</h3>
          <p>{admin ? 'Portafolio y responsables asignados.' : 'Proyectos donde tienes tareas asignadas.'}</p>
        </Link>
        <Link to="/tareas" className="inicio-card">
          <h3>{admin ? 'Tablero Trello' : 'Mis tareas'}</h3>
          <p>{admin ? 'Kanban por proyecto con estados.' : 'Tablero Kanban con tus tareas.'}</p>
        </Link>
      </div>
    </div>
  )
}
