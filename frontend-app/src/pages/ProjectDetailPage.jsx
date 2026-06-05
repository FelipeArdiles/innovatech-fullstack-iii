import { lazy, Suspense, useCallback, useEffect, useMemo, useState } from 'react'
import { Link, useNavigate, useParams, useSearchParams } from 'react-router-dom'
import { api } from '../api/client'
import FlashMessage from '../components/FlashMessage'
import Button from '../components/ui/Button'
import LoadingSkeleton from '../components/ui/LoadingSkeleton'
import WorkerAvatars from '../components/WorkerAvatars'
import TaskProgressChart from '../components/charts/TaskProgressChart'
import DeadlineTimeline from '../components/charts/DeadlineTimeline'
import ProgressRing from '../components/charts/ProgressRing'
import CapacityChart from '../components/charts/CapacityChart'
import DifficultyChart from '../components/charts/DifficultyChart'
import { timelinePercent } from '../utils/projectDates'
import { formatCLP, formatMargen, margenBadgeClass, OTROS_GASTOS_TOOLTIP } from '../utils/money'
import { isAdmin } from '../auth/roles'

const ProjectFinancialCharts = lazy(() => import('../components/charts/ProjectFinancialCharts'))

const TABS = [
  { id: 'resumen', label: 'Resumen' },
  { id: 'tareas', label: 'Tareas' },
  { id: 'equipo', label: 'Equipo' },
  { id: 'finanzas', label: 'Finanzas' },
  { id: 'tablero', label: 'Tablero' },
]

const DIFICULTADES = ['BAJA', 'MEDIA', 'ALTA']
const CATEGORIAS = ['DESARROLLO', 'QA', 'DISENO', 'GESTION', 'DEVOPS', 'ANALISIS']

export default function ProjectDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [searchParams, setSearchParams] = useSearchParams()
  const admin = isAdmin()
  const visibleTabs = admin ? TABS : TABS.filter((t) => t.id !== 'finanzas')
  const tabParam = searchParams.get('tab') || 'resumen'
  const tab = !admin && tabParam === 'finanzas' ? 'resumen' : tabParam

  const [proyecto, setProyecto] = useState(null)
  const [finanzas, setFinanzas] = useState(null)
  const [loading, setLoading] = useState(true)
  const [finanzasLoading, setFinanzasLoading] = useState(false)
  const [error, setError] = useState('')
  const [filtroDificultad, setFiltroDificultad] = useState('')
  const [filtroCategoria, setFiltroCategoria] = useState('')
  const [todosTrabajadores, setTodosTrabajadores] = useState([])
  const [miembros, setMiembros] = useState([])
  const [nuevoMiembroId, setNuevoMiembroId] = useState('')
  const [miembrosLoading, setMiembrosLoading] = useState(false)
  const [miembrosFlash, setMiembrosFlash] = useState('')
  const [avisoTexto, setAvisoTexto] = useState('')
  const [avisoEnviando, setAvisoEnviando] = useState(false)

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

  const loadMiembros = useCallback(() => {
    if (!admin || tab !== 'equipo') return
    setMiembrosLoading(true)
    Promise.all([api.getMiembrosProyecto(id), api.getTrabajadores()])
      .then(([miembrosData, trabajadoresData]) => {
        setMiembros(miembrosData)
        setTodosTrabajadores(trabajadoresData)
      })
      .catch((err) => setError(err.message))
      .finally(() => setMiembrosLoading(false))
  }, [admin, tab, id])

  useEffect(() => {
    loadMiembros()
  }, [loadMiembros])

  async function handleAgregarMiembro() {
    if (!nuevoMiembroId) return
    try {
      await api.agregarMiembroProyecto(id, Number(nuevoMiembroId))
      setMiembrosFlash('Miembro agregado al proyecto')
      setNuevoMiembroId('')
      await load()
      loadMiembros()
    } catch (err) {
      setMiembrosFlash(err.message)
    }
    setTimeout(() => setMiembrosFlash(''), 3000)
  }

  async function handlePublicarAviso() {
    if (!avisoTexto.trim()) return
    setAvisoEnviando(true)
    try {
      const res = await api.publicarAvisoProyecto(id, avisoTexto.trim())
      setMiembrosFlash(`Aviso enviado a ${res?.enviadas ?? 0} persona(s)`)
      setAvisoTexto('')
    } catch (err) {
      setMiembrosFlash(err.message)
    } finally {
      setAvisoEnviando(false)
    }
    setTimeout(() => setMiembrosFlash(''), 4000)
  }

  async function handleQuitarMiembro(trabajadorId) {
    try {
      await api.quitarMiembroProyecto(id, trabajadorId)
      setMiembrosFlash('Miembro removido del proyecto')
      await load()
      loadMiembros()
    } catch (err) {
      setMiembrosFlash(err.message)
    }
    setTimeout(() => setMiembrosFlash(''), 3000)
  }

  useEffect(() => {
    if (tab !== 'finanzas' || finanzas || finanzasLoading) return
    setFinanzasLoading(true)
    api.getProyectoFinanzas(id)
      .then(setFinanzas)
      .catch((err) => setError(err.message))
      .finally(() => setFinanzasLoading(false))
  }, [tab, id, finanzas, finanzasLoading])

  function setTab(next) {
    if (next === 'tablero') {
      navigate(`/tareas?proyectoId=${id}`)
      return
    }
    setSearchParams(next === 'resumen' ? {} : { tab: next }, { replace: true })
  }

  const progresoTareas = useMemo(() => {
    if (!proyecto) return 0
    const total = proyecto.tareasPorHacer + proyecto.tareasEnProgreso + proyecto.tareasHechas
    if (!total) return 0
    return Math.round((proyecto.tareasHechas / total) * 100)
  }, [proyecto])

  const capacidadEquipo = useMemo(() => {
    if (!proyecto?.trabajadores?.length) return []
    return (proyecto.trabajadores ?? []).map((t) => {
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

  const tareasFiltradas = useMemo(() => {
    let list = proyecto?.tareas ?? []
    if (filtroDificultad) list = list.filter((t) => t.dificultad === filtroDificultad)
    if (filtroCategoria) list = list.filter((t) => t.categoria === filtroCategoria)
    return list
  }, [proyecto, filtroDificultad, filtroCategoria])

  if (loading) return <LoadingSkeleton variant="card" rows={3} />
  if (error && !proyecto) return <FlashMessage message={error} type="error" />
  if (!proyecto) return <p className="empty-state">Proyecto no encontrado</p>

  const plazoPercent = timelinePercent(proyecto.fechaInicio, proyecto.fechaFin)

  return (
    <div className="page page--hub">
      {error && <FlashMessage message={error} type="error" onClose={() => setError('')} />}

      <header className="project-hub__welcome">
        <div className="project-hub__welcome-text">
          <p className="project-hub__eyebrow">Bienvenido al proyecto</p>
          <h1>{proyecto.nombre}</h1>
          <p className="project-hub__description">{proyecto.descripcion}</p>
        </div>
        <div className="project-hub__welcome-actions">
          <Button variant="secondary" onClick={() => navigate('/proyectos')}>← Proyectos</Button>
        </div>
      </header>

      <div className="detail-badges">
        <span className="badge badge--info">{proyecto.estado?.replace('_', ' ')}</span>
        {proyecto.atrasado && <span className="badge badge--danger">Atrasado</span>}
        {proyecto.margenPorcentaje != null && (
          <span className={`badge ${margenBadgeClass(proyecto.margenPorcentaje)}`}>
            Margen {formatMargen(proyecto.margenPorcentaje)}
          </span>
        )}
        {proyecto.presupuesto != null && (
          <span className="badge badge--neutral">Presupuesto {formatCLP(proyecto.presupuesto)}</span>
        )}
      </div>

      <nav className="project-hub__tabs" aria-label="Secciones del proyecto">
        {visibleTabs.map(({ id: tabId, label }) => (
          <button
            key={tabId}
            type="button"
            className={`project-hub__tab${tab === tabId ? ' project-hub__tab--active' : ''}`}
            onClick={() => setTab(tabId)}
          >
            {label}
          </button>
        ))}
      </nav>

      {tab === 'resumen' && (
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
            <h3>KPIs</h3>
            <div className="summary-cards summary-cards--compact">
              <article className="summary-card">
                <span className="summary-card__label">Tareas</span>
                <strong className="summary-card__value">{proyecto.tareas?.length ?? 0}</strong>
              </article>
              <article className="summary-card">
                <span className="summary-card__label">Horas est.</span>
                <strong className="summary-card__value">{proyecto.horasProyectoEstimadas} h</strong>
              </article>
              <article className="summary-card">
                <span className="summary-card__label">Ingresos</span>
                <strong className="summary-card__value">{formatCLP(proyecto.ingresosContrato)}</strong>
              </article>
            </div>
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
            <h3>Tareas por dificultad</h3>
            <DifficultyChart
              baja={proyecto.tareasDificultadBaja}
              media={proyecto.tareasDificultadMedia}
              alta={proyecto.tareasDificultadAlta}
            />
          </section>
          <section className="detail-card">
            <h3>Avance temporal</h3>
            <p className="detail-stat">{plazoPercent}% del plazo</p>
            <ProgressRing value={plazoPercent} />
          </section>
        </div>
      )}

      {tab === 'tareas' && (
        <section className="detail-card detail-card--wide">
          <div className="detail-card__header-row">
            <h3>Tareas ({tareasFiltradas.length})</h3>
            <div className="project-hub__filters">
              <select value={filtroDificultad} onChange={(e) => setFiltroDificultad(e.target.value)}>
                <option value="">Todas las dificultades</option>
                {DIFICULTADES.map((d) => (
                  <option key={d} value={d}>{d}</option>
                ))}
              </select>
              <select value={filtroCategoria} onChange={(e) => setFiltroCategoria(e.target.value)}>
                <option value="">Todas las categorías</option>
                {CATEGORIAS.map((c) => (
                  <option key={c} value={c}>{c}</option>
                ))}
              </select>
            </div>
          </div>
          {!tareasFiltradas.length && (
            <p className="empty-state">No hay tareas con los filtros seleccionados.</p>
          )}
          <ul className="detail-task-list">
            {tareasFiltradas.map((t) => (
              <li key={t.id}>
                <div>
                  <strong>{t.titulo}</strong>
                  <span className="detail-meta">{t.descripcion}</span>
                </div>
                <span className={`badge badge--${t.estado === 'HECHO' ? 'success' : t.estado === 'EN_PROGRESO' ? 'info' : 'warning'}`}>
                  {t.estado?.replace('_', ' ')}
                </span>
                {t.dificultad && <span className="badge badge--neutral">{t.dificultad}</span>}
                {t.categoria && <span className="badge badge--neutral">{t.categoria}</span>}
                {t.horasEstimadas != null && <span className="detail-meta">{t.horasEstimadas} h</span>}
                {t.valorMonetario != null && <span className="detail-meta">{formatCLP(t.valorMonetario)}</span>}
              </li>
            ))}
          </ul>
        </section>
      )}

      {tab === 'equipo' && (
        <div className="detail-grid">
          {admin && (
            <section className="detail-card detail-card--wide">
              <h3>Gestionar miembros del proyecto</h3>
              {miembrosFlash && <p className="detail-meta">{miembrosFlash}</p>}
              <div className="miembros-admin">
                <select
                  value={nuevoMiembroId}
                  onChange={(e) => setNuevoMiembroId(e.target.value)}
                  disabled={miembrosLoading}
                >
                  <option value="">Agregar trabajador…</option>
                  {todosTrabajadores
                    .filter((t) => !miembros.some((m) => m.trabajadorId === t.id))
                    .map((t) => (
                      <option key={t.id} value={t.id}>{t.nombre} — {t.rol}</option>
                    ))}
                </select>
                <Button onClick={handleAgregarMiembro} disabled={!nuevoMiembroId || miembrosLoading}>
                  Agregar
                </Button>
              </div>
              <ul className="detail-team-list">
                {miembros.map((m) => {
                  const t = todosTrabajadores.find((x) => x.id === m.trabajadorId)
                    || (proyecto.trabajadores ?? []).find((x) => x.id === m.trabajadorId)
                  return (
                    <li key={m.id ?? m.trabajadorId} className="miembros-admin__row">
                      <span>{t?.nombre ?? `Trabajador #${m.trabajadorId}`}</span>
                      <span className="detail-meta">{t?.rol ?? 'Miembro'}</span>
                      <Button variant="danger" size="sm" onClick={() => handleQuitarMiembro(m.trabajadorId)}>
                        Quitar
                      </Button>
                    </li>
                  )
                })}
                {!miembrosLoading && !miembros.length && (
                  <li className="detail-meta">No hay miembros registrados. Agrega trabajadores al equipo.</li>
                )}
              </ul>
            </section>
          )}
          {admin && (
            <section className="detail-card detail-card--wide">
              <h3>Avisos del proyecto</h3>
              <p className="detail-meta">
                Publica un aviso general para todos los miembros del equipo (llegará como notificación).
              </p>
              <textarea
                className="aviso-proyecto__input"
                rows={3}
                placeholder="Ej.: Reunión de sincronización mañana a las 10:00…"
                value={avisoTexto}
                onChange={(e) => setAvisoTexto(e.target.value)}
                disabled={avisoEnviando}
              />
              <Button onClick={handlePublicarAviso} disabled={!avisoTexto.trim() || avisoEnviando}>
                {avisoEnviando ? 'Enviando…' : 'Publicar aviso'}
              </Button>
            </section>
          )}
          <section className="detail-card">
            <h3>Equipo asignado</h3>
            <WorkerAvatars trabajadores={proyecto.trabajadores} max={10} />
            <ul className="detail-team-list">
              {(proyecto.trabajadores ?? []).map((t) => {
                const horas = (proyecto.tareas || [])
                  .filter((ta) => ta.asignadoId === t.id)
                  .reduce((s, ta) => s + (ta.horasEstimadas || 0), 0)
                const esMiembro = miembros.some((m) => m.trabajadorId === t.id)
                return (
                  <li key={t.id}>
                    <span>{t.nombre}</span>
                    <span className="detail-meta">{t.rol}</span>
                    {esMiembro && <span className="badge badge--info">Miembro</span>}
                    <span className="detail-meta">{horas} h en proyecto · {t.capacidadHoras ?? 0} h/sem</span>
                  </li>
                )
              })}
            </ul>
          </section>
          {capacidadEquipo.length > 0 && (
            <section className="detail-card detail-card--wide">
              <h3>Carga en este proyecto</h3>
              <CapacityChart trabajadores={capacidadEquipo} />
            </section>
          )}
        </div>
      )}

      {tab === 'finanzas' && (
        <div className="detail-grid">
          {finanzasLoading && <LoadingSkeleton variant="card" rows={2} />}
          {!finanzasLoading && finanzas && (
            <>
              <div className="summary-cards detail-card--wide" style={{ gridColumn: '1 / -1' }}>
                <article className="summary-card">
                  <span className="summary-card__label">Presupuesto (CLP)</span>
                  <strong className="summary-card__value">{formatCLP(finanzas.presupuesto)}</strong>
                </article>
                <article className="summary-card">
                  <span className="summary-card__label">Costo acumulado</span>
                  <strong className="summary-card__value">{formatCLP(finanzas.costoAcumulado)}</strong>
                </article>
                <article className="summary-card">
                  <span className="summary-card__label">Ingresos contrato</span>
                  <strong className="summary-card__value">{formatCLP(finanzas.ingresos)}</strong>
                </article>
                <article className={`summary-card${finanzas.rentable ? '' : ' summary-card--danger'}`}>
                  <span className="summary-card__label">Ganancia / margen</span>
                  <strong className="summary-card__value">{formatCLP(finanzas.ganancia)}</strong>
                  <span className="summary-card__hint">{formatMargen(finanzas.margenPorcentaje)}</span>
                </article>
              </div>
              <section className="detail-card detail-card--wide">
                <h3>Desglose de costos</h3>
                <div className="summary-cards summary-cards--compact">
                  <article className="summary-card">
                    <span className="summary-card__label">Sueldos del equipo</span>
                    <strong className="summary-card__value">{formatCLP(finanzas.costoSueldos)}</strong>
                    <span className="summary-card__hint">Prorrateo por horas asignadas</span>
                  </article>
                  <article className="summary-card">
                    <span className="summary-card__label">Costo tareas</span>
                    <strong className="summary-card__value">{formatCLP(finanzas.costoTareasOperacional)}</strong>
                    <span className="summary-card__hint">Valor operacional estimado</span>
                  </article>
                  <article className="summary-card">
                    <span
                      className="summary-card__label fin-label-tip"
                      title={OTROS_GASTOS_TOOLTIP}
                    >
                      Otros gastos ⓘ
                    </span>
                    <strong className="summary-card__value">{formatCLP(finanzas.otrosGastos)}</strong>
                    <span className="summary-card__hint" title={OTROS_GASTOS_TOOLTIP}>
                      Licencias / infra ~12% presupuesto
                    </span>
                  </article>
                </div>
              </section>
              <section className="detail-card detail-card--wide">
                <h3>Análisis financiero</h3>
                <Suspense fallback={<LoadingSkeleton variant="card" rows={1} />}>
                  <ProjectFinancialCharts finanzas={finanzas} />
                </Suspense>
              </section>
              <section className="detail-card detail-card--wide">
                <h3>Desglose por tarea</h3>
                <ul className="detail-task-list">
                  {(finanzas.desgloseTareas ?? []).map((t) => (
                    <li key={t.id}>
                      <div>
                        <strong>{t.titulo}</strong>
                        <span className="detail-meta">{t.categoria} · {t.dificultad}</span>
                      </div>
                      <span className="detail-meta">{t.horasEstimadas} h</span>
                      <span className="badge badge--info">{formatCLP(t.valorMonetario)}</span>
                    </li>
                  ))}
                </ul>
              </section>
            </>
          )}
        </div>
      )}
    </div>
  )
}
