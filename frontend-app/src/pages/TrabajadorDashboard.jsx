import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import { getAccountTypeLabel } from '../auth/roles'
import FlashMessage from '../components/FlashMessage'
import PageHeader from '../components/ui/PageHeader'
import LoadingSkeleton from '../components/ui/LoadingSkeleton'
import Card from '../components/ui/Card'
import { formatDate } from '../utils/projectDates'

export default function TrabajadorDashboard() {
  const [panel, setPanel] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    setLoading(true)
    api.getMiPanel()
      .then(setPanel)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <div className="page">
        <PageHeader title="Mi panel" subtitle="Cargando tu información…" />
        <LoadingSkeleton rows={4} />
      </div>
    )
  }

  if (error) {
    return <FlashMessage message={error} type="error" />
  }

  if (!panel) {
    return (
      <FlashMessage
        message="No se encontró tu perfil de trabajador en el sistema. Usa una cuenta vinculada al email del equipo (ej. ana@innovatech.cl)."
        type="error"
      />
    )
  }

  const { perfil, proyectos, tareasAsignadas } = panel

  return (
    <div className="page">
      <div className="account-banner account-banner--worker">
        <div>
          <span className="account-banner__eyebrow">Tipo de cuenta</span>
          <strong>{getAccountTypeLabel()}</strong>
          <p>
            {perfil.nombre} · {perfil.rol}
            {perfil.email ? ` · ${perfil.email}` : ''}
          </p>
        </div>
        <span className="badge badge--worker">Vista restringida</span>
      </div>

      <PageHeader
        title="Mi panel de trabajo"
        subtitle="Proyectos asignados, tareas y plazos · sin acceso a finanzas ni administración"
      />

      <section className="kpi-grid">
        <Card gradient={1} className="kpi-card">
          <h4>Tareas asignadas</h4>
          <p className="kpi-value">{panel.totalTareasAsignadas}</p>
        </Card>
        <Card gradient={4} className="kpi-card">
          <h4>Por hacer</h4>
          <p className="kpi-value">{panel.tareasPorHacer}</p>
        </Card>
        <Card gradient={5} className="kpi-card">
          <h4>En progreso</h4>
          <p className="kpi-value">{panel.tareasEnProgreso}</p>
        </Card>
        <Card gradient={3} className="kpi-card">
          <h4>Completadas</h4>
          <p className="kpi-value">{panel.tareasHechas}</p>
        </Card>
        <Card gradient={2} className="kpi-card">
          <h4>Horas estimadas</h4>
          <p className="kpi-value">{panel.horasAsignadasTotal} h</p>
        </Card>
        <Card gradient={1} className="kpi-card">
          <h4>Proyectos activos</h4>
          <p className="kpi-value">{proyectos?.length ?? 0}</p>
        </Card>
      </section>

      <section className="panels" style={{ marginTop: '1.25rem' }}>
        <Card className="panel">
          <h4>Mis proyectos</h4>
          {!proyectos?.length ? (
            <p className="empty-state">No tienes proyectos con tareas asignadas.</p>
          ) : (
            <div className="worker-project-list">
              {proyectos.map((p) => (
                <Link key={p.id} to={`/proyectos/${p.id}`} className="worker-project-card">
                  <div>
                    <strong>{p.nombre}</strong>
                    <span className="detail-meta">{p.estado?.replace('_', ' ')}</span>
                  </div>
                  <p className="worker-project-card__desc">{p.descripcion}</p>
                  <div className="worker-project-card__meta">
                    <span>Entrega: {formatDate(p.fechaFin)}</span>
                    {p.diasRestantes != null && (
                      <span className={p.atrasado ? 'text-danger' : ''}>
                        {p.atrasado
                          ? `Atrasado ${Math.abs(p.diasRestantes)} días`
                          : `${p.diasRestantes} días restantes`}
                      </span>
                    )}
                    <span>{p.misTareasTotal} tareas tuyas</span>
                  </div>
                </Link>
              ))}
            </div>
          )}
        </Card>

        <Card className="panel">
          <h4>Mis tareas</h4>
          {!tareasAsignadas?.length ? (
            <p className="empty-state">Sin tareas asignadas.</p>
          ) : (
            <div className="table-wrap">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Tarea</th>
                    <th>Proyecto</th>
                    <th>Estado</th>
                    <th>Horas</th>
                  </tr>
                </thead>
                <tbody>
                  {tareasAsignadas.map((t) => {
                    const proyecto = proyectos?.find((p) => p.id === t.proyectoId)
                    return (
                      <tr key={t.id}>
                        <td>{t.titulo}</td>
                        <td>{proyecto?.nombre ?? `#${t.proyectoId}`}</td>
                        <td><span className="badge badge--soft">{t.estado?.replace('_', ' ')}</span></td>
                        <td>{t.horasEstimadas ?? '—'} h</td>
                      </tr>
                    )
                  })}
                </tbody>
              </table>
            </div>
          )}
          <div style={{ marginTop: '1rem' }}>
            <Link to="/tareas" className="btn btn--secondary">Ver tablero Kanban</Link>
          </div>
        </Card>
      </section>
    </div>
  )
}
