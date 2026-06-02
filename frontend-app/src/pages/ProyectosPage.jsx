import { useCallback, useEffect, useState } from 'react'
import { api } from '../api/client'
import FlashMessage from '../components/FlashMessage'
import ConfirmModal from '../components/ConfirmModal'

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
  const [proyectos, setProyectos] = useState([])
  const [usuarios, setUsuarios] = useState([])
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

  const loadData = useCallback(() => {
    setLoading(true)
    setError('')
    return Promise.all([api.getProyectos(), api.getUsuarios()])
      .then(([proyectosData, usuariosData]) => {
        setProyectos(proyectosData)
        setUsuarios(usuariosData)
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    loadData()
  }, [loadData])

  function showFlash(message, type = 'success') {
    setFlash(message)
    setFlashType(type)
    setTimeout(() => setFlash(''), 4000)
  }

  function usuarioLabel(id) {
    if (!id) return '—'
    const u = usuarios.find((x) => x.id === id)
    return u ? `${u.nombre} (#${u.id})` : `#${id}`
  }

  function openCreate() {
    setEditingId(null)
    setForm(EMPTY_FORM)
    setFormErrors({})
    setShowForm(true)
  }

  function openEdit(proyecto) {
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

      <div className="page__header">
        <h3>Proyectos</h3>
        <button type="button" className="btn btn--primary" onClick={openCreate}>
          + Nuevo proyecto
        </button>
      </div>

      {showForm && (
        <form className="form-card" onSubmit={handleSubmit}>
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
              <textarea
                name="descripcion"
                rows={3}
                value={form.descripcion}
                onChange={handleChange}
              />
              {formErrors.descripcion && (
                <span className="field-error">{formErrors.descripcion}</span>
              )}
            </label>
            <label>
              Responsable
              <select name="responsableId" value={form.responsableId} onChange={handleChange}>
                <option value="">Sin asignar</option>
                {usuarios.map((u) => (
                  <option key={u.id} value={u.id}>{u.nombre} ({u.rol})</option>
                ))}
              </select>
              {formErrors.responsableId && (
                <span className="field-error">{formErrors.responsableId}</span>
              )}
            </label>
          </div>
          <div className="form-card__actions">
            <button type="button" className="btn btn--secondary" onClick={cancelForm}>
              Cancelar
            </button>
            <button type="submit" className="btn btn--primary" disabled={saving}>
              {saving ? 'Guardando…' : editingId ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      )}

      {loading && <p className="state-message">Cargando proyectos…</p>}
      {!loading && error && <FlashMessage message={error} type="error" />}
      {!loading && !error && proyectos.length === 0 && (
        <p className="empty-state">No hay proyectos. Crea el primero con el botón superior.</p>
      )}
      {!loading && !error && proyectos.length > 0 && (
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Estado</th>
                <th>Descripción</th>
                <th>Responsable</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {proyectos.map((p) => (
                <tr key={p.id}>
                  <td>{p.id}</td>
                  <td>{p.nombre}</td>
                  <td><span className="badge">{p.estado}</span></td>
                  <td className="cell-truncate">{p.descripcion}</td>
                  <td>{usuarioLabel(p.responsableId)}</td>
                  <td className="actions-cell">
                    <button type="button" className="btn btn--sm btn--secondary" onClick={() => openEdit(p)}>
                      Editar
                    </button>
                    <button
                      type="button"
                      className="btn btn--sm btn--danger"
                      onClick={() => setDeleteTarget(p)}
                    >
                      Eliminar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
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
