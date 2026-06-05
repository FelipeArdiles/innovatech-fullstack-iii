import { useCallback, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/client'
import FlashMessage from '../components/FlashMessage'
import ConfirmModal from '../components/ConfirmModal'
import PageHeader from '../components/ui/PageHeader'
import Button from '../components/ui/Button'
import LoadingSkeleton from '../components/ui/LoadingSkeleton'
import { daysUntil, formatDate, isProyectoAtrasado } from '../utils/projectDates'
import { formatCLP, formatMargen, margenBadgeClass } from '../utils/money'
import { isAdmin } from '../auth/roles'

const ESTADOS = ['PLANIFICADO', 'EN_PROGRESO', 'COMPLETADO', 'CANCELADO']

const EMPTY_FORM = { nombre: '', estado: 'PLANIFICADO', descripcion: '', responsableId: '' }

function validate(form) {
  const errors = {}
  if (!form.nombre.trim()) errors.nombre = 'Nombre requerido'
  if (!form.estado.trim()) errors.estado = 'Estado requerido'
  if (!form.descripcion.trim()) errors.descripcion = 'Descripción requerida'
  if (form.responsableId !== '' && Number(form.responsableId) <= 0) {
    errors.responsableId = 'ID de responsable inválido'
  }
  return errors
}

export default function ProyectosPage() {
  const navigate = useNavigate()
  const [proyectos, setProyectos] = useState([])
  const [trabajadores, setTrabajadores] = useState([])
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

  const admin = isAdmin()

  const loadData = useCallback(() => {
    setLoading(true)
    setError('')
    if (!admin) {
      return api.getMiPanel()
        .then((panel) => {
          const ids = new Set((panel?.proyectos ?? []).map((p) => p.id))
          return api.getProyectos().then((all) => {
            setProyectos(all.filter((p) => ids.has(p.id)))
            setTrabajadores(panel?.perfil ? [panel.perfil] : [])
          })
        })
        .catch((err) => setError(err.message))
        .finally(() => setLoading(false))
    }
    return Promise.all([api.getProyectos(), api.getTrabajadores()])
      .then(([proyectosData, trabajadoresData]) => {
        setProyectos(proyectosData)
        setTrabajadores(trabajadoresData)
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [admin])

  useEffect(() => {
    loadData()
  }, [loadData])

  function showFlash(message, type = 'success') {
    setFlash(message)
    setFlashType(type)
    setTimeout(() => setFlash(''), 4000)
  }

  function trabajadorLabel(id) {
    if (!id) return '—'
    const t = trabajadores.find((x) => x.id === id)
    return t ? `${t.nombre} (#${t.id})` : `#${id}`
  }

  function openCreate() {
    setEditingId(null)
    setForm(EMPTY_FORM)
    setFormErrors({})
    setShowForm(true)
  }

  function openEdit(proyecto, e) {
    e?.stopPropagation()
    setEditingId(proyecto.id)
    setForm({
      nombre: proyecto.nombre ?? '',
      estado: proyecto.estado ?? 'PLANIFICADO',
      descripcion: proyecto.descripcion ?? '',
      responsableId: proyecto.responsableId != null ? String(proyecto.responsableId) : '',
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
      nombre: form.nombre.trim(),
      estado: form.estado.trim(),
      descripcion: form.descripcion.trim(),
      responsableId: form.responsableId !== '' ? Number(form.responsableId) : null,
    }

    setSaving(true)
    try {
      if (editingId) {
        await api.updateProyecto(editingId, { ...payload, id: editingId })
        showFlash('Proyecto actualizado correctamente')
      } else {
        await api.createProyecto(payload)
        showFlash('Proyecto creado correctamente')
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
      await api.deleteProyecto(deleteTarget.id)
      showFlash('Proyecto eliminado')
      setDeleteTarget(null)
      await loadData()
    } catch (err) {
      showFlash(err.message, 'error')
    } finally {
      setDeleting(false)
    }
  }

  return (
    <div className="page">
      <FlashMessage message={flash} type={flashType} onClose={() => setFlash('')} />

      <PageHeader
        title={admin ? 'Proyectos' : 'Mis proyectos'}
        subtitle={admin
          ? 'Boards de innovación tecnológica con responsables asignados'
          : 'Proyectos donde tienes tareas asignadas (solo lectura)'}
        actions={admin ? <Button onClick={openCreate}>+ Nuevo proyecto</Button> : null}
      />

      {admin && showForm && (
        <form className="form-card form-card--animated" onSubmit={handleSubmit}>
          <h4>{editingId ? 'Editar proyecto' : 'Nuevo proyecto'}</h4>
          <div className="form-grid">
            <label>
              Nombre *
              <input name="nombre" value={form.nombre} onChange={handleChange} />
              {formErrors.nombre && <span className="field-error">{formErrors.nombre}</span>}
            </label>
            <label>
              Estado *
              <select name="estado" value={form.estado} onChange={handleChange}>
                {ESTADOS.map((e) => (
                  <option key={e} value={e}>{e.replace('_', ' ')}</option>
                ))}
              </select>
              {formErrors.estado && <span className="field-error">{formErrors.estado}</span>}
            </label>
            <label className="form-grid__full">
              Descripción *
              <textarea name="descripcion" rows={3} value={form.descripcion} onChange={handleChange} />
              {formErrors.descripcion && (
                <span className="field-error">{formErrors.descripcion}</span>
              )}
            </label>
            <label>
              Responsable
              <select name="responsableId" value={form.responsableId} onChange={handleChange}>
                <option value="">Sin asignar</option>
                {trabajadores.map((t) => (
                  <option key={t.id} value={t.id}>{t.nombre} ({t.rol})</option>
                ))}
              </select>
              {formErrors.responsableId && (
                <span className="field-error">{formErrors.responsableId}</span>
              )}
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

      {loading && <LoadingSkeleton variant="card" rows={4} />}
      {!loading && error && <FlashMessage message={error} type="error" />}
      {!loading && !error && proyectos.length === 0 && (
        <p className="empty-state">
          {admin ? 'No hay proyectos. Crea el primero con el botón superior.' : 'No tienes proyectos con tareas asignadas.'}
        </p>
      )}
      {!loading && !error && proyectos.length > 0 && (
        <div className="proyecto-cards">
          {proyectos.map((p) => {
            const atrasado = isProyectoAtrasado(p)
            const dias = daysUntil(p.fechaFin)
            return (
              <article
                key={p.id}
                className="proyecto-card proyecto-card--clickable"
                role="button"
                tabIndex={0}
                onClick={() => navigate(`/proyectos/${p.id}`)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault()
                    navigate(`/proyectos/${p.id}`)
                  }
                }}
              >
                <div className="proyecto-card__head">
                  <h3>{p.nombre}</h3>
                  <div className="proyecto-card__badges">
                    <span className="badge badge--info">{p.estado?.replace('_', ' ')}</span>
                    {admin && p.margenPorcentaje != null && (
                      <span className={`badge ${margenBadgeClass(p.margenPorcentaje)}`}>
                        {formatMargen(p.margenPorcentaje)}
                      </span>
                    )}
                  </div>
                </div>
                <p className="proyecto-card__desc">{p.descripcion}</p>
                <div className="proyecto-card__meta">
                  <span>Responsable: {trabajadorLabel(p.responsableId)}</span>
                  {admin && p.ingresosContrato != null && (
                    <span>Ingresos: {formatCLP(p.ingresosContrato)}</span>
                  )}
                  {admin && p.presupuesto != null && (
                    <span>Presupuesto: {formatCLP(p.presupuesto)}</span>
                  )}
                  {(p.fechaInicio || p.fechaFin) && (
                    <span>{formatDate(p.fechaInicio)} – {formatDate(p.fechaFin)}</span>
                  )}
                  {dias != null && !atrasado && dias >= 0 && (
                    <span>{dias} días restantes</span>
                  )}
                </div>
                {atrasado && <span className="badge badge--danger">Atrasado</span>}
                {admin && (
                  <div className="proyecto-card__actions" onClick={(e) => e.stopPropagation()}>
                    <Button variant="secondary" size="sm" type="button" onClick={(e) => openEdit(p, e)}>
                      Editar
                    </Button>
                    <Button variant="danger" size="sm" type="button" onClick={() => setDeleteTarget(p)}>
                      Eliminar
                    </Button>
                  </div>
                )}
              </article>
            )
          })}
        </div>
      )}

      <ConfirmModal
        open={!!deleteTarget}
        title="Eliminar proyecto"
        message={`¿Eliminar "${deleteTarget?.nombre}"? Esta acción no se puede deshacer.`}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
        loading={deleting}
      />
    </div>
  )
}
