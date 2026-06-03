import { Link } from 'react-router-dom'
import PageHeader from '../components/ui/PageHeader'

export default function InicioPage() {
  return (
    <div className="page page--inicio">
      <section className="inicio-hero">
        <p className="inicio-hero__eyebrow">Innovatech Solutions</p>
        <h2 className="inicio-hero__title">Gestión de proyectos tecnológicos</h2>
        <p className="inicio-hero__text">
          Bienvenido al panel demo: trabajadores, proyectos y tablero Kanban integrados con
          Keycloak y microservicios.
        </p>
        <div className="inicio-hero__actions">
          <Link to="/dashboard" className="btn btn--primary">
            Ir al dashboard
          </Link>
          <Link to="/tareas" className="btn btn--secondary">
            Abrir tablero Trello
          </Link>
        </div>
      </section>

      <PageHeader
        title="Accesos rápidos"
        subtitle="Elige un módulo para comenzar"
      />
      <div className="inicio-grid">
        <Link to="/trabajadores" className="inicio-card">
          <h3>Trabajadores</h3>
          <p>Equipo, roles y capacidad horaria.</p>
        </Link>
        <Link to="/proyectos" className="inicio-card">
          <h3>Proyectos</h3>
          <p>Portafolio y responsables asignados.</p>
        </Link>
        <Link to="/tareas" className="inicio-card">
          <h3>Tablero Trello</h3>
          <p>Kanban por proyecto con estados.</p>
        </Link>
      </div>
    </div>
  )
}
