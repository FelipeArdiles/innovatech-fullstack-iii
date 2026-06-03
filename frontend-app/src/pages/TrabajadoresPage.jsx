import { useCallback, useEffect, useState } from 'react'
import { api } from '../api/client'
import FlashMessage from '../components/FlashMessage'
import ConfirmModal from '../components/ConfirmModal'
import PageHeader from '../components/ui/PageHeader'
import Button from '../components/ui/Button'
import LoadingSkeleton from '../components/ui/LoadingSkeleton'

const EMPTY_FORM = { nombre: '', rol: '', email: '', capacidadHoras: '' }

function validate(form) {
  const errors = {}
  if (!form.nombre.trim()) errors.nombre = 'Nombre requerido'
  if (!form.rol.trim()) errors.rol = 'Rol requerido'
  if (!form.email.trim()) errors.email = 'Email requerido'
  else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) errors.email = 'Email inválido'
  if (form.capacidadHoras === '' || Number(form.capacidadHoras) < 0) {
    errors.capacidadHoras = 'Capacidad requerida (≥ 0)'
  }
  return errors
}

export default function TrabajadoresPage() {
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

  const loadTrabajadores = useCallback(() => {
    setLoading(true)
    setError('')
    return api
      .getTrabajadores()
      .then(setTrabajadores)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    loadTrabajadores()
  }, [loadTrabajadores])

  function showFlash(message, type = 'success') {
    setFlash(message)
    setFlashType(type)
    setTimeout(() => setFlash(''), 4000)
  }

  function openCreate() {
    setEditingId(null)
    setForm(EMPTY_FORM)
    setFormErrors({})
    setShowForm(true)
  }

  function openEdit(trabajador) {
    setEditingId(trabajador.id)
    setForm({
      nombre: trabajador.nombre ?? '',
      rol: trabajador.rol ?? '',
      email: trabajador.email ?? '',
      capacidadHoras: String(trabajador.capacidadHoras ?? ''),
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
      rol: form.rol.trim(),
      email: form.email.trim(),
      capacidadHoras: Number(form.capacidadHoras),
    }

    setSaving(true)
    try {
      if (editingId) {
        await api.updateTrabajador(editingId, { ...payload, id: editingId })
        showFlash('Trabajador actualizado correctamente')
      } else {
        await api.createTrabajador(payload)
        showFlash('Trabajador creado correctamente')
      }
      cancelForm()
      await loadTrabajadores()
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
      await api.deleteTrabajador(deleteTarget.id)
      showFlash('Trabajador eliminado')
      setDeleteTarget(null)
      await loadTrabajadores()
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
        title="Trabajadores"
        subtitle="Equipo y capacidad semanal (CRUD completo)"
        actions={
          <Button onClick={openCreate}>+ Nuevo trabajador</Button>
        }
      />

      {showForm && (
        <form className="form-card form-card--animated" onSubmit={handleSubmit}>
          <h4>{editingId ? 'Editar trabajador' : 'Nuevo trabajador'}</h4>
          <div className="form-grid">
            <label>
              Nombre *
              <input name="nombre" value={form.nombre} onChange={handleChange} />
              {formErrors.nombre && <span className="field-error">{formErrors.nombre}</span>}
            </label>
            <label>
              Rol *
              <input name="rol" value={form.rol} onChange={handleChange} placeholder="Ej: Desarrollador" />
              {formErrors.rol && <span className="field-error">{formErrors.rol}</span>}
            </label>
            <label>
              Email *
              <input name="email" type="email" value={form.email} onChange={handleChange} />
              {formErrors.email && <span className="field-error">{formErrors.email}</span>}
            </label>
            <label>
              Capacidad (h/sem) *
              <input
                name="capacidadHoras"
                type="number"
                min="0"
                value={form.capacidadHoras}
                onChange={handleChange}
              />
              {formErrors.capacidadHoras && (
                <span className="field-error">{formErrors.capacidadHoras}</span>
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

      {loading && <LoadingSkeleton variant="table" rows={5} />}
      {!loading && error && <FlashMessage message={error} type="error" />}
      {!loading && !error && trabajadores.length === 0 && (
        <p className="empty-state">No hay trabajadores. Crea el primero con el botón superior.</p>
      )}
      {!loading && !error && trabajadores.length > 0 && (
        <div className="table-wrap table-wrap--animated">
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Rol</th>
                <th>Email</th>
                <th>Capacidad</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {trabajadores.map((t) => (
                <tr key={t.id}>
                  <td>{t.id}</td>
                  <td>{t.nombre}</td>
                  <td><span className="badge">{t.rol}</span></td>
                  <td>{t.email}</td>
                  <td>{t.capacidadHoras} h/sem</td>
                  <td className="actions-cell">
                    <Button variant="secondary" size="sm" type="button" onClick={() => openEdit(t)}>
                      Editar
                    </Button>
                    <Button variant="danger" size="sm" type="button" onClick={() => setDeleteTarget(t)}>
                      Eliminar
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <ConfirmModal
        open={!!deleteTarget}
        title="Eliminar trabajador"
        message={`¿Eliminar a "${deleteTarget?.nombre}"? Esta acción no se puede deshacer.`}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
        loading={deleting}
      />
    </div>
  )
}
