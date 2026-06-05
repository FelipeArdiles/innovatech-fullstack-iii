import { useState } from 'react'
import { NavLink } from 'react-router-dom'
import { getUsername, logout } from '../auth/keycloak'
import { getAccountTypeLabel, getAccountBadgeClass, isAdmin } from '../auth/roles'
import { NavIcon } from './icons/NavIcons'
import Button from './ui/Button'
import AnimatedOutlet from './AnimatedOutlet'
import NotificationBell from './NotificationBell'

const adminNavItems = [
  { to: '/', label: 'Inicio', icon: 'home', end: true },
  { to: '/dashboard', label: 'Dashboard', icon: 'dashboard' },
  { to: '/trabajadores', label: 'Trabajadores', icon: 'workers' },
  { to: '/proyectos', label: 'Proyectos', icon: 'projects' },
  { to: '/finanzas', label: 'Finanzas empresa', icon: 'finanzas' },
  { to: '/capacidad', label: 'Capacidad', icon: 'capacity' },
  { to: '/tareas', label: 'Tablero Trello', icon: 'tasks' },
]

const workerNavItems = [
  { to: '/', label: 'Inicio', icon: 'home', end: true },
  { to: '/dashboard', label: 'Mi panel', icon: 'dashboard' },
  { to: '/proyectos', label: 'Mis proyectos', icon: 'projects' },
  { to: '/tareas', label: 'Mis tareas', icon: 'tasks' },
]

export default function Layout() {
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const admin = isAdmin()
  const navItems = admin ? adminNavItems : workerNavItems

  function closeSidebar() {
    setSidebarOpen(false)
  }

  return (
    <div className={`shell${sidebarOpen ? ' shell--sidebar-open' : ''}`}>
      <button
        type="button"
        className="shell__overlay"
        aria-label="Cerrar menú"
        onClick={closeSidebar}
      />

      <aside className="sidebar">
        <div className="sidebar__brand">
          <div className="sidebar__logo">IT</div>
          <div>
            <h1>Innovatech</h1>
            <p>Solutions</p>
          </div>
          <button
            type="button"
            className="sidebar__close"
            aria-label="Cerrar menú"
            onClick={closeSidebar}
          >
            <NavIcon name="close" />
          </button>
        </div>

        <div className={`sidebar__role-pill ${getAccountBadgeClass()}`}>
          Cuenta: {getAccountTypeLabel()}
        </div>

        <nav className="sidebar__nav">
          {navItems.map(({ to, label, icon, end }) => (
            <NavLink
              key={to}
              to={to}
              end={end}
              onClick={closeSidebar}
              className={({ isActive }) =>
                `sidebar__link${isActive ? ' sidebar__link--active' : ''}`
              }
            >
              <NavIcon name={icon} />
              <span>{label}</span>
            </NavLink>
          ))}
        </nav>

        <div className="sidebar__footer">
          <div className="sidebar__user-badge">
            <span className="sidebar__avatar">{getUsername().charAt(0).toUpperCase()}</span>
            <span className="sidebar__user">{getUsername()}</span>
          </div>
          <Button variant="secondary" size="block" onClick={logout}>
            Cerrar sesión
          </Button>
        </div>
      </aside>

      <div className="main">
        <header className="main__header">
          <button
            type="button"
            className="main__menu-btn"
            aria-label="Abrir menú"
            onClick={() => setSidebarOpen(true)}
          >
            <NavIcon name="menu" />
          </button>
          <div>
            <h2>{admin ? 'Gestión integral de proyectos tecnológicos' : 'Panel del colaborador'}</h2>
            <p className="main__subtitle">
              Innovatech Solutions · <span className={`badge ${getAccountBadgeClass()}`}>{getAccountTypeLabel()}</span>
            </p>
          </div>
          <NotificationBell />
        </header>
        <main className="main__content">
          <AnimatedOutlet />
        </main>
      </div>
    </div>
  )
}
