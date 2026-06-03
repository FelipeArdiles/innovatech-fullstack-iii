import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/client'
import FlashMessage from '../components/FlashMessage'
import PageHeader from '../components/ui/PageHeader'
import Button from '../components/ui/Button'
import LoadingSkeleton from '../components/ui/LoadingSkeleton'
import WorkerAvatars from '../components/WorkerAvatars'
import TaskProgressChart from '../components/charts/TaskProgressChart'
import DeadlineTimeline from '../components/charts/DeadlineTimeline'
import ProgressRing from '../components/charts/ProgressRing'
import CapacityChart from '../components/charts/CapacityChart'
import { timelinePercent } from '../utils/projectDates'

export default function ProjectDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [proyecto, setProyecto] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = useCallback(() => {
    setLoading(true)
    setError('')
    return api.getProyectoDetalle(id)
      .then(setProyecto)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [id])

  useEffect(() => {
    load()
  }, [load])

  const progresoTareas = useMemo(() => {
    if (!proyecto) return 0
    const total = proyecto.tareasPorHacer + proyecto.tareasEnProgreso + proyecto.tareasHechas
    if (!total) return 0
    return Math.round((proyecto.tareasHechas / total) * 100)
  }, [proyecto])

  const capacidadEquipo = useMemo(() => {
    if (!proyecto?.trabajadores?.length) return []
    return proyecto.trabajadores.map((t) => {
      const horasEnProyecto = (proyecto.tareas || [])
        .filter((ta) => ta.asignadoId === t.id)
        .reduce((sum, ta) => sum + (ta.horasEstimadas || 0), 0)
      const disponibles = t.capacidadHoras || 0
      const porcentaje = disponibles > 0 ? (horasEnProyecto / disponibles) * 100 : 0
      return {
        id: t.id,
        nombre: t.nombre,
        rol: t.rol,
        horasDisponibles: disponibles,
        horasAsignadas: horasEnProyecto,
        porcentajeCarga: Math.round(porcentaje * 10) / 10,
        sobrecargado: horasEnProyecto > disponibles && disponibles > 0,
        horasProyecto: horasEnProyecto,
      }
    })
  }, [proyecto])

  if (loading) return <LoadingSkeleton variant="card" rows={3} />
  if (error) return <FlashMessage message={error} type="error" />
  if (!proyecto) return <p className="empty-state">Proyecto no encontrado</p>

  const plazoPercent = timelinePercent(proyecto.fechaInicio, proyecto.fechaFin)

  return (
    <div className="page page--detail">
      <PageHeader
        title={proyecto.nombre}
        subtitle={proyecto.descripcion}
        actions={
          <>
            <Button variant="secondary" onClick={() => navigate('/proyectos')}>
              ← Volver
            </Button>
            <Link className="btn btn--primary" to={`/tareas?proyectoId=${proyecto.id}`}>
              Ver tablero
            </Link>
          </>
        }
      />

      <div className="detail-badges">
        <span className="badge badge--info">{proyecto.estado?.replace('_', ' ')}</span>
        {proyecto.atrasado && <span className="badge badge--danger">Atrasado</span>}
        {proyecto.presupuesto != null && (
          <span className="badge badge--neutral">
            Presupuesto ${Number(proyecto.presupuesto).toLocaleString('es-CL')}
          </span>
        )}
      </div>

      <div className="detail-grid">
        <section className="detail-card detail-card--wide">
          <h3>Plazo del proyecto</h3>
          <DeadlineTimeline
            fechaInicio={proyecto.fechaInicio}
            fechaFin={proyecto.fechaFin}
            atrasado={proyecto.atrasado}
          />
        </section>

        <section className="detail-card">
          <h3>Progreso de tareas</h3>
          <div className="detail-card__split">
            <ProgressRing value={progresoTareas} label="Completadas" />
            <TaskProgressChart
              porHacer={proyecto.tareasPorHacer}
              enProgreso={proyecto.tareasEnProgreso}
              hechas={proyecto.tareasHechas}
            />
          </div>
        </section>

        <section className="detail-card">
          <h3>Avance temporal</h3>
          <p className="detail-stat">{plazoPercent}% del plazo</p>
          <ProgressRing value={plazoPercent} />
          <p className="detail-meta">{proyecto.horasProyectoEstimadas} h estimadas en el proyecto</p>
        </section>

        <section className="detail-card">
          <h3>Equipo asignado</h3>
          <WorkerAvatars trabajadores={proyecto.trabajadores} max={8} />
          <ul className="detail-team-list">
            {proyecto.trabajadores.map((t) => (
              <li key={t.id}>
                <span>{t.nombre}</span>
                <span className="detail-meta">{t.rol}</span>
              </li>
            ))}
          </ul>
        </section>

        {capacidadEquipo.length > 0 && (
          <section className="detail-card detail-card--wide">
            <h3>Capacidad del equipo en este proyecto</h3>
            <CapacityChart trabajadores={capacidadEquipo} />
          </section>
        )}

        <section className="detail-card detail-card--wide">
          <div className="detail-card__header-row">
            <h3>Tareas ({proyecto.tareas?.length || 0})</h3>
            <Link className="link-accent" to={`/tareas?proyectoId=${proyecto.id}`}>
              Abrir Kanban filtrado →
            </Link>
          </div>
          {!proyecto.tareas?.length && (
            <p className="empty-state">No hay tareas en este proyecto.</p>
          )}
          <ul className="detail-task-list">
            {proyecto.tareas?.map((t) => (
              <li key={t.id}>
                <div>
                  <strong>{t.titulo}</strong>
                  <span className="detail-meta">{t.descripcion}</span>
                </div>
                <span className={`badge badge--${t.estado === 'HECHO' ? 'success' : t.estado === 'EN_PROGRESO' ? 'info' : 'warning'}`}>
                  {t.estado?.replace('_', ' ')}
                </span>
                {t.horasEstimadas != null && (
                  <span className="detail-meta">{t.horasEstimadas} h</span>
                )}
              </li>
            ))}
          </ul>
        </section>
      </div>
    </div>
  )
}
