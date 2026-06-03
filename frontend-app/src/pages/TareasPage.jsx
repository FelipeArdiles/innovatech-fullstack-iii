import { useCallback, useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { api } from '../api/client'
import FlashMessage from '../components/FlashMessage'
import ConfirmModal from '../components/ConfirmModal'
import PageHeader from '../components/ui/PageHeader'
import Button from '../components/ui/Button'
import LoadingSkeleton from '../components/ui/LoadingSkeleton'

const COLUMNAS = [
  { estado: 'POR_HACER', titulo: 'Por hacer', accent: 'todo' },
  { estado: 'EN_PROGRESO', titulo: 'En progreso', accent: 'progress' },
  { estado: 'HECHO', titulo: 'Hecho', accent: 'done' },
]

const EMPTY_FORM = {
  titulo: '',
  descripcion: '',
  estado: 'POR_HACER',
  proyectoId: '',
  asignadoId: '',
}

function validate(form) {
  const errors = {}
  if (!form.titulo.trim()) errors.titulo = 'Título requerido'
  if (!form.descripcion.trim()) errors.descripcion = 'Descripción requerida'
  if (!form.estado.trim()) errors.estado = 'Estado requerido'
  if (form.proyectoId === '' || Number(form.proyectoId) <= 0) {
    errors.proyectoId = 'Proyecto requerido'
  }
  return errors
}

export default function TareasPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const proyectoFromUrl = searchParams.get('proyectoId') || ''
  const [tareas, setTareas] = useState([])
  const [proyectos, setProyectos] = useState([])
  const [trabajadores, setTrabajadores] = useState([])
  const [filtroProyecto, setFiltroProyecto] = useState(proyectoFromUrl)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [flash, setFlash] = useState('')
  const [flashType, setFlashType] = useState('success')
  const [form, setForm] = useState(EMPTY_FORM)
  const [formErrors, setFormErrors] = useState({})
  const [editingId, setEditingId] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [saving, setSaving] = useState(false)
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [deleting, setDeleting] = useState(false)
  const [draggingId, setDraggingId] = useState(null)
  const [dropTarget, setDropTarget] = useState(null)

  const loadData = useCallback(() => {
    setLoading(true)
    setError('')
    const proyectoId = filtroProyecto !== '' ? Number(filtroProyecto) : undefined
    return Promise.all([
      api.getTareas(proyectoId),
      api.getProyectos(),
      api.getTrabajadores(),
    ])
      .then(([tareasData, proyectosData, trabajadoresData]) => {
        setTareas(tareasData)
        setProyectos(proyectosData)
        setTrabajadores(trabajadoresData)
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [filtroProyecto])

  useEffect(() => {
    setFiltroProyecto(proyectoFromUrl)
  }, [proyectoFromUrl])

  useEffect(() => {
    loadData()
  }, [loadData])

  function handleFiltroProyecto(value) {
    setFiltroProyecto(value)
    if (value) {
      setSearchParams({ proyectoId: value })
    } else {
      setSearchParams({})
    }
  }

  function showFlash(message, type = 'success') {
    setFlash(message)
    setFlashType(type)
    setTimeout(() => setFlash(''), 4000)
  }

  function proyectoLabel(id) {
    const p = proyectos.find((x) => x.id === id)
    return p ? p.nombre : `#${id}`
  }

  function trabajadorLabel(id) {
    if (!id) return 'Sin asignar'
    const t = trabajadores.find((x) => x.id === id)
    return t ? t.nombre : `#${id}`
  }

  function openCreate(estado = 'POR_HACER') {
    setEditingId(null)
    setForm({
      ...EMPTY_FORM,
      estado,
      proyectoId: filtroProyecto !== '' ? filtroProyecto : '',
    })
    setFormErrors({})
    setShowForm(true)
  }

  function openEdit(tarea) {
    setEditingId(tarea.id)
    setForm({
      titulo: tarea.titulo ?? '',
      descripcion: tarea.descripcion ?? '',
      estado: tarea.estado ?? 'POR_HACER',
      proyectoId: tarea.proyectoId != null ? String(tarea.proyectoId) : '',
      asignadoId: tarea.asignadoId != null ? String(tarea.asignadoId) : '',
    })
    setFormErrors({})
    setShowForm(true)
  }

  function cancelForm() {
    setShowForm(false)
    setEditingId(null)
    setForm(EMPTY_FORM)
    setFormErrors({})
  }

  function handleChange(e) {
    const { name, value } = e.target
    setForm((prev) => ({ ...prev, [name]: value }))
    setFormErrors((prev) => ({ ...prev, [name]: undefined }))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    const errors = validate(form)
    if (Object.keys(errors).length) {
      setFormErrors(errors)
      return
    }

    const payload = {
      titulo: form.titulo.trim(),
      descripcion: form.descripcion.trim(),
      estado: form.estado.trim(),
      proyectoId: Number(form.proyectoId),
      asignadoId: form.asignadoId !== '' ? Number(form.asignadoId) : null,
    }

    setSaving(true)
    try {
      if (editingId) {
        await api.updateTarea(editingId, { ...payload, id: editingId })
        showFlash('Tarea actualizada')
      } else {
        await api.createTarea(payload)
        showFlash('Tarea creada')
      }
      cancelForm()
      await loadData()
    } catch (err) {
      showFlash(err.message, 'error')
    } finally {
      setSaving(false)
    }
  }

  async function confirmDelete() {
    if (!deleteTarget) return
    setDeleting(true)
    try {
      await api.deleteTarea(deleteTarget.id)
      showFlash('Tarea eliminada')
      setDeleteTarget(null)
      await loadData()
    } catch (err) {
      showFlash(err.message, 'error')
    } finally {
      setDeleting(false)
    }
  }

  async function moverTarea(tareaId, nuevoEstado) {
    const tarea = tareas.find((t) => t.id === tareaId)
    if (!tarea || tarea.estado === nuevoEstado) return
    try {
      await api.updateTarea(tareaId, { ...tarea, estado: nuevoEstado })
      setTareas((prev) =>
        prev.map((t) => (t.id === tareaId ? { ...t, estado: nuevoEstado } : t))
      )
      showFlash('Estado actualizado')
    } catch (err) {
      showFlash(err.message, 'error')
    }
  }

  function handleDragStart(e, tareaId) {
    e.dataTransfer.setData('tareaId', String(tareaId))
    e.dataTransfer.effectAllowed = 'move'
    setDraggingId(tareaId)
  }

  function handleDragEnd() {
    setDraggingId(null)
    setDropTarget(null)
  }

  function handleDragOver(e, estado) {
    e.preventDefault()
    setDropTarget(estado)
  }

  function handleDragLeave() {
    setDropTarget(null)
  }

  async function handleDrop(e, estado) {
    e.preventDefault()
    setDropTarget(null)
    const tareaId = Number(e.dataTransfer.getData('tareaId'))
    if (tareaId) await moverTarea(tareaId, estado)
    setDraggingId(null)
  }

  return (
    <div className="page">
      <FlashMessage message={flash} type={flashType} onClose={() => setFlash('')} />

      <PageHeader
        title="Tablero Trello"
        subtitle="Arrastra tarjetas entre columnas o edítalas con CRUD completo"
        actions={
          <>
            <select
              className="filter-select"
              value={filtroProyecto}
              onChange={(e) => handleFiltroProyecto(e.target.value)}
            >
              <option value="">Todos los proyectos</option>
              {proyectos.map((p) => (
                <option key={p.id} value={p.id}>{p.nombre}</option>
              ))}
            </select>
            <Button onClick={() => openCreate()}>+ Nueva tarea</Button>
          </>
        }
      />

      {showForm && (
        <form className="form-card form-card--animated" onSubmit={handleSubmit}>
          <h4>{editingId ? 'Editar tarea' : 'Nueva tarea'}</h4>
          <div className="form-grid">
            <label>
              Título *
              <input name="titulo" value={form.titulo} onChange={handleChange} />
              {formErrors.titulo && <span className="field-error">{formErrors.titulo}</span>}
            </label>
            <label>
              Estado *
              <select name="estado" value={form.estado} onChange={handleChange}>
                {COLUMNAS.map((c) => (
                  <option key={c.estado} value={c.estado}>{c.titulo}</option>
                ))}
              </select>
            </label>
            <label className="form-grid__full">
              Descripción *
              <textarea name="descripcion" rows={3} value={form.descripcion} onChange={handleChange} />
              {formErrors.descripcion && (
                <span className="field-error">{formErrors.descripcion}</span>
              )}
            </label>
            <label>
              Proyecto *
              <select name="proyectoId" value={form.proyectoId} onChange={handleChange}>
                <option value="">Seleccionar…</option>
                {proyectos.map((p) => (
                  <option key={p.id} value={p.id}>{p.nombre}</option>
                ))}
              </select>
              {formErrors.proyectoId && (
                <span className="field-error">{formErrors.proyectoId}</span>
              )}
            </label>
            <label>
              Asignado a
              <select name="asignadoId" value={form.asignadoId} onChange={handleChange}>
                <option value="">Sin asignar</option>
                {trabajadores.map((t) => (
                  <option key={t.id} value={t.id}>{t.nombre}</option>
                ))}
              </select>
            </label>
          </div>
          <div className="form-card__actions">
            <Button variant="secondary" type="button" onClick={cancelForm}>Cancelar</Button>
            <Button type="submit" disabled={saving}>
              {saving ? 'Guardando…' : editingId ? 'Actualizar' : 'Crear'}
            </Button>
          </div>
        </form>
      )}

      {loading && <LoadingSkeleton variant="kanban" />}
      {!loading && error && <FlashMessage message={error} type="error" />}

      {!loading && !error && (
        <div className="kanban">
          {COLUMNAS.map((col, colIndex) => {
            const items = tareas.filter((t) => t.estado === col.estado)
            return (
              <section
                key={col.estado}
                className={`kanban__column kanban__column--${col.accent}${
                  dropTarget === col.estado ? ' kanban__column--drop' : ''
                }`}
                style={{ animationDelay: `${colIndex * 0.1}s` }}
                onDragOver={(e) => handleDragOver(e, col.estado)}
                onDragLeave={handleDragLeave}
                onDrop={(e) => handleDrop(e, col.estado)}
              >
                <header className="kanban__header">
                  <h4>{col.titulo}</h4>
                  <span className="kanban__count">{items.length}</span>
                </header>
                <div className="kanban__cards">
                  {items.map((tarea, i) => (
                    <article
                      key={tarea.id}
                      draggable
                      onDragStart={(e) => handleDragStart(e, tarea.id)}
                      onDragEnd={handleDragEnd}
                      className={`kanban-card${
                        draggingId === tarea.id ? ' kanban-card--dragging' : ''
                      }`}
                      style={{ animationDelay: `${colIndex * 0.1 + i * 0.05}s` }}
                    >
                      <h5>{tarea.titulo}</h5>
                      <p>{tarea.descripcion}</p>
                      <div className="kanban-card__meta">
                        <span className="badge badge--soft">{proyectoLabel(tarea.proyectoId)}</span>
                        <span>{trabajadorLabel(tarea.asignadoId)}</span>
                      </div>
                      <div className="kanban-card__actions">
                        <Button variant="ghost" size="sm" type="button" onClick={() => openEdit(tarea)}>
                          Editar
                        </Button>
                        <Button variant="danger" size="sm" type="button" onClick={() => setDeleteTarget(tarea)}>
                          ×
                        </Button>
                      </div>
                    </article>
                  ))}
                  <button
                    type="button"
                    className="kanban__add"
                    onClick={() => openCreate(col.estado)}
                  >
                    + Agregar tarjeta
                  </button>
                </div>
              </section>
            )
          })}
        </div>
      )}

      <ConfirmModal
        open={!!deleteTarget}
        title="Eliminar tarea"
        message={`¿Eliminar "${deleteTarget?.titulo}"?`}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
        loading={deleting}
      />
    </div>
  )
}
