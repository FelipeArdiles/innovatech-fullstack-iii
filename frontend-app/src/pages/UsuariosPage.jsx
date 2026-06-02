import { useCallback, useEffect, useState } from 'react'
import { api } from '../api/client'
import FlashMessage from '../components/FlashMessage'
import ConfirmModal from '../components/ConfirmModal'

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

export default function UsuariosPage() {
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

  const loadUsuarios = useCallback(() => {
    setLoading(true)
    setError('')
    return api
      .getUsuarios()
      .then(setUsuarios)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    loadUsuarios()
  }, [loadUsuarios])

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

  function openEdit(usuario) {
    setEditingId(usuario.id)
    setForm({
      nombre: usuario.nombre ?? '',
      rol: usuario.rol ?? '',
      email: usuario.email ?? '',
      capacidadHoras: String(usuario.capacidadHoras ?? ''),
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
        await api.updateUsuario(editingId, { ...payload, id: editingId })
        showFlash('Usuario actualizado correctamente')
      } else {
        await api.createUsuario(payload)
        showFlash('Usuario creado correctamente')
      }
      cancelForm()
      await loadUsuarios()
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
      await api.deleteUsuario(deleteTarget.id)
      showFlash('Usuario eliminado')
      setDeleteTarget(null)
      await loadUsuarios()
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
        <h3>Usuarios</h3>
        <button type="button" className="btn btn--primary" onClick={openCreate}>
          + Nuevo usuario
        </button>
      </div>

      {showForm && (
        <form className="form-card" onSubmit={handleSubmit}>
          <h4>{editingId ? 'Editar usuario' : 'Nuevo usuario'}</h4>
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
            <button type="button" className="btn btn--secondary" onClick={cancelForm}>
              Cancelar
            </button>
            <button type="submit" className="btn btn--primary" disabled={saving}>
              {saving ? 'Guardando…' : editingId ? 'Actualizar' : 'Crear'}
            </button>
          </div>
        </form>
      )}

      {loading && <p className="state-message">Cargando usuarios…</p>}
      {!loading && error && <FlashMessage message={error} type="error" />}
      {!loading && !error && usuarios.length === 0 && (
        <p className="empty-state">No hay usuarios. Crea el primero con el botón superior.</p>
      )}
      {!loading && !error && usuarios.length > 0 && (
        <div className="table-wrap">
          <table>
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
              {usuarios.map((u) => (
                <tr key={u.id}>
                  <td>{u.id}</td>
                  <td>{u.nombre}</td>
                  <td>{u.rol}</td>
                  <td>{u.email}</td>
                  <td>{u.capacidadHoras} h/sem</td>
                  <td className="actions-cell">
                    <button type="button" className="btn btn--sm btn--secondary" onClick={() => openEdit(u)}>
                      Editar
                    </button>
                    <button
                      type="button"
                      className="btn btn--sm btn--danger"
                      onClick={() => setDeleteTarget(u)}
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
        title="Eliminar usuario"
        message={`¿Eliminar a "${deleteTarget?.nombre}"? Esta acción no se puede deshacer.`}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
        loading={deleting}
      />
    </div>
  )
}
