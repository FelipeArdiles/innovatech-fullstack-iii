import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import FlashMessage from '../components/FlashMessage'
import Card from '../components/ui/Card'
import PageHeader from '../components/ui/PageHeader'
import LoadingSkeleton from '../components/ui/LoadingSkeleton'
import { formatCLP, formatMargen, margenBadgeClass } from '../utils/money'

const KPI_CONFIG = [
  { key: 'totalUsuarios', label: 'Trabajadores', gradient: 1, link: '/trabajadores' },
  { key: 'totalProyectos', label: 'Proyectos', gradient: 2, link: '/proyectos' },
  { key: 'totalTareas', label: 'Tareas totales', gradient: 3, link: '/tareas' },
  { key: 'tareasPorHacer', label: 'Por hacer', gradient: 4, link: '/tareas' },
  { key: 'tareasEnProgreso', label: 'En progreso', gradient: 5, link: '/tareas' },
  { key: 'tareasHechas', label: 'Completadas', gradient: 3, link: '/tareas' },
]

export default function Dashboard() {
  const [dashboard, setDashboard] = useState(null)
  const [finanzas, setFinanzas] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    setLoading(true)
    setError('')
    Promise.all([api.getDashboard(), api.getFinanzasResumen()])
      .then(([dash, fin]) => {
        setDashboard(dash)
        setFinanzas(fin)
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <div className="page">
        <PageHeader title="Dashboard KPIs" subtitle="Métricas agregadas de la plataforma" />
        <LoadingSkeleton rows={6} />
      </div>
    )
  }

  if (error) {
    return <FlashMessage message={error} type="error" />
  }

  return (
    <div className="page">
      <PageHeader
        title="Dashboard KPIs"
        subtitle="Vista general de trabajadores, proyectos, tablero Kanban y finanzas CLP"
        actions={
          <>
            <Link to="/finanzas" className="btn btn--secondary">Finanzas empresa</Link>
            <Link to="/tareas" className="btn btn--secondary">Ver tablero</Link>
            <Link to="/proyectos" className="btn btn--primary">Nuevo proyecto</Link>
          </>
        }
      />

      <section className="kpi-grid">
        {KPI_CONFIG.map(({ key, label, gradient, link }, index) => (
          <Link key={key} to={link} className="kpi-link" style={{ animationDelay: `${index * 0.07}s` }}>
            <Card gradient={gradient} hover className="kpi-card">
              <h4>{label}</h4>
              <p className="kpi-value">{dashboard?.[key] ?? 0}</p>
            </Card>
          </Link>
        ))}
      </section>

      {finanzas && (
        <section className="summary-cards" style={{ marginTop: '1.25rem' }}>
          <article className="summary-card">
            <span className="summary-card__label">Ingresos totales (CLP)</span>
            <strong className="summary-card__value">{formatCLP(finanzas.ingresosTotales)}</strong>
          </article>
          <article className="summary-card">
            <span className="summary-card__label">Costos totales</span>
            <strong className="summary-card__value">{formatCLP(finanzas.costosTotales)}</strong>
          </article>
          <article className={`summary-card${Number(finanzas.gananciaNeta) >= 0 ? '' : ' summary-card--danger'}`}>
            <span className="summary-card__label">Ganancia neta</span>
            <strong className="summary-card__value">{formatCLP(finanzas.gananciaNeta)}</strong>
            <span className={`summary-card__hint badge ${margenBadgeClass(finanzas.margenEmpresaPorcentaje)}`}>
              Margen {formatMargen(finanzas.margenEmpresaPorcentaje)}
            </span>
          </article>
          <article className="summary-card">
            <span className="summary-card__label">Proyectos rentables</span>
            <strong className="summary-card__value">
              {finanzas.proyectosRentables} / {finanzas.proyectos?.length ?? 0}
            </strong>
          </article>
        </section>
      )}

      <section className="panels">
        <Card className="panel panel--animated">
          <h4>Trabajadores recientes</h4>
          {!dashboard?.usuarios?.length ? (
            <p className="empty-state">No hay trabajadores registrados.</p>
          ) : (
            <div className="table-wrap">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Rol</th>
                    <th>Email</th>
                    <th>Sueldo mensual</th>
                    <th>Capacidad</th>
                  </tr>
                </thead>
                <tbody>
                  {dashboard.usuarios.map((u) => (
                    <tr key={u.id}>
                      <td>{u.nombre}</td>
                      <td><span className="badge">{u.rol}</span></td>
                      <td>{u.email}</td>
                      <td>{formatCLP(u.sueldoMensualClp)}</td>
                      <td>{u.capacidadHoras} h/sem</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>

        <Card className="panel panel--animated" style={{ animationDelay: '0.1s' }}>
          <h4>Proyectos activos</h4>
          {!dashboard?.proyectos?.length ? (
            <p className="empty-state">No hay proyectos registrados.</p>
          ) : (
            <div className="table-wrap">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Estado</th>
                    <th>Ingresos (CLP)</th>
                    <th>Margen</th>
                  </tr>
                </thead>
                <tbody>
                  {dashboard.proyectos.map((p) => (
                    <tr key={p.id}>
                      <td>{p.nombre}</td>
                      <td><span className="badge badge--info">{p.estado?.replace('_', ' ')}</span></td>
                      <td>{formatCLP(p.ingresosContrato)}</td>
                      <td>
                        {p.margenPorcentaje != null ? (
                          <span className={`badge ${margenBadgeClass(p.margenPorcentaje)}`}>
                            {formatMargen(p.margenPorcentaje)}
                          </span>
                        ) : '—'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      </section>
    </div>
  )
}
