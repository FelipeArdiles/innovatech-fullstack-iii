import { NavLink, Outlet } from 'react-router-dom'
import { getUsername, logout } from '../auth/keycloak'

const navItems = [
  { to: '/', label: 'Dashboard', end: true },
  { to: '/usuarios', label: 'Usuarios' },
  { to: '/proyectos', label: 'Proyectos' },
]

export default function Layout() {
  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="sidebar__brand">
          <h1>Innovatech</h1>
          <p>Solutions</p>
        </div>
        <nav className="sidebar__nav">
          {navItems.map(({ to, label, end }) => (
            <NavLink
              key={to}
              to={to}
              end={end}
              className={({ isActive }) =>
                `sidebar__link${isActive ? ' sidebar__link--active' : ''}`
              }
            >
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar__footer">
          <span className="sidebar__user">{getUsername()}</span>
          <button type="button" className="btn btn--secondary btn--block" onClick={logout}>
            Cerrar sesión
          </button>
        </div>
      </aside>

      <div className="main">
        <header className="main__header">
          <div>
            <h2>Gestión integral de proyectos tecnológicos</h2>
            <p className="main__subtitle">Panel de administración Innovatech Solutions</p>
          </div>
        </header>
        <main className="main__content">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
