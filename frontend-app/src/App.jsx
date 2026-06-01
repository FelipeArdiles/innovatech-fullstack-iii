import { useEffect, useState } from 'react'
import { api } from './api/client'
import { getUsername, logout, initAuth } from './auth/keycloak'
import './App.css'

function App() {
  const [ready, setReady] = useState(false)
  const [error, setError] = useState('')
  const [dashboard, setDashboard] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    initAuth()
      .then(() => setReady(true))
      .catch((err) => setError(err.message))
  }, [])

  useEffect(() => {
    if (!ready) return
    setLoading(true)
    api.getDashboard()
      .then(setDashboard)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [ready])

  if (error) {
    return (
      <div className="app">
        <header><h1>Innovatech Solutions</h1></header>
        <p className="error">Error: {error}</p>
      </div>
    )
  }

  if (!ready || loading) {
    return (
      <div className="app">
        <header><h1>Innovatech Solutions</h1></header>
        <p>Cargando plataforma...</p>
      </div>
    )
  }

  return (
    <div className="app">
      <header className="header">
        <div>
          <h1>Innovatech Solutions</h1>
          <p>Gestión integral de proyectos tecnológicos</p>
        </div>
        <div className="user-bar">
          <span>Hola, {getUsername()}</span>
          <button type="button" onClick={logout}>Cerrar sesión</button>
        </div>
      </header>

      <section className="kpi-grid">
        <article className="kpi-card">
          <h2>Usuarios</h2>
          <p className="kpi-value">{dashboard?.totalUsuarios ?? 0}</p>
        </article>
        <article className="kpi-card">
          <h2>Proyectos</h2>
          <p className="kpi-value">{dashboard?.totalProyectos ?? 0}</p>
        </article>
      </section>

      <section className="panels">
        <article>
          <h3>Recursos humanos</h3>
          <table>
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Rol</th>
                <th>Email</th>
                <th>Capacidad (h/sem)</th>
              </tr>
            </thead>
            <tbody>
              {dashboard?.usuarios?.map((u) => (
                <tr key={u.id}>
                  <td>{u.nombre}</td>
                  <td>{u.rol}</td>
                  <td>{u.email}</td>
                  <td>{u.capacidadHoras}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </article>

        <article>
          <h3>Proyectos</h3>
          <table>
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Estado</th>
                <th>Descripción</th>
                <th>Responsable ID</th>
              </tr>
            </thead>
            <tbody>
              {dashboard?.proyectos?.map((p) => (
                <tr key={p.id}>
                  <td>{p.nombre}</td>
                  <td>{p.estado}</td>
                  <td>{p.descripcion}</td>
                  <td>{p.responsableId}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </article>
      </section>
    </div>
  )
}

export default App
