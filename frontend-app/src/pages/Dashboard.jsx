import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import FlashMessage from '../components/FlashMessage'

export default function Dashboard() {
  const [dashboard, setDashboard] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    setLoading(true)
    setError('')
    api
      .getDashboard()
      .then(setDashboard)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return <p className="state-message">Cargando dashboard…</p>
  }

  if (error) {
    return <FlashMessage message={error} type="error" />
  }

  return (
    <div className="page">
      <div className="page__header">
        <h3>Dashboard</h3>
        <div className="page__actions">
          <Link to="/usuarios" className="btn btn--secondary">Gestionar usuarios</Link>
          <Link to="/proyectos" className="btn btn--secondary">Gestionar proyectos</Link>
        </div>
      </div>

      <section className="kpi-grid">
        <article className="kpi-card">
          <h4>Usuarios</h4>
          <p className="kpi-value">{dashboard?.totalUsuarios ?? 0}</p>
        </article>
        <article className="kpi-card">
          <h4>Proyectos</h4>
          <p className="kpi-value">{dashboard?.totalProyectos ?? 0}</p>
        </article>
      </section>

      <section className="panels">
        <article className="panel">
          <h4>Recursos humanos</h4>
          {!dashboard?.usuarios?.length ? (
            <p className="empty-state">No hay usuarios registrados.</p>
          ) : (
            <div className="table-wrap">
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
                  {dashboard.usuarios.map((u) => (
                    <tr key={u.id}>
                      <td>{u.nombre}</td>
                      <td>{u.rol}</td>
                      <td>{u.email}</td>
                      <td>{u.capacidadHoras}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </article>

        <article className="panel">
          <h4>Proyectos</h4>
          {!dashboard?.proyectos?.length ? (
            <p className="empty-state">No hay proyectos registrados.</p>
          ) : (
            <div className="table-wrap">
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
                  {dashboard.proyectos.map((p) => (
                    <tr key={p.id}>
                      <td>{p.nombre}</td>
                      <td><span className="badge">{p.estado}</span></td>
                      <td>{p.descripcion}</td>
                      <td>{p.responsableId ?? '—'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </article>
      </section>
    </div>
  )
}
