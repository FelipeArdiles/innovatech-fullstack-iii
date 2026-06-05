import { useCallback, useEffect, useState } from 'react'
import { api } from '../api/client'
import Button from './ui/Button'

function formatFecha(iso) {
  if (!iso) return ''
  return new Date(iso).toLocaleString('es-CL', { dateStyle: 'short', timeStyle: 'short' })
}

export default function TaskComments({ tareaId, expanded, onToggle }) {
  const [comentarios, setComentarios] = useState([])
  const [texto, setTexto] = useState('')
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const load = useCallback(() => {
    if (!expanded || !tareaId) return
    setLoading(true)
    api.getComentarios(tareaId)
      .then(setComentarios)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [tareaId, expanded])

  useEffect(() => {
    load()
  }, [load])

  async function handleSubmit(e) {
    e.preventDefault()
    if (!texto.trim()) return
    setSaving(true)
    setError('')
    try {
      const creado = await api.crearComentario(tareaId, texto.trim())
      setComentarios((prev) => [...prev, creado])
      setTexto('')
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="task-comments">
      <button type="button" className="task-comments__toggle" onClick={onToggle}>
        {expanded ? '▾' : '▸'} Comentarios ({comentarios.length || (expanded ? '…' : '0')})
      </button>
      {expanded && (
        <div className="task-comments__panel">
          {loading && <p className="detail-meta">Cargando comentarios…</p>}
          {error && <p className="field-error">{error}</p>}
          <ul className="task-comments__list">
            {comentarios.map((c) => (
              <li key={c.id}>
                <strong>{c.autorNombre}</strong>
                <span className="detail-meta">{formatFecha(c.fecha)}</span>
                <p>{c.texto}</p>
              </li>
            ))}
            {!loading && !comentarios.length && (
              <li className="detail-meta">Aún no hay comentarios.</li>
            )}
          </ul>
          <form className="task-comments__form" onSubmit={handleSubmit}>
            <textarea
              rows={2}
              placeholder="Escribe un comentario…"
              value={texto}
              onChange={(e) => setTexto(e.target.value)}
            />
            <Button type="submit" size="sm" disabled={saving || !texto.trim()}>
              {saving ? 'Enviando…' : 'Comentar'}
            </Button>
          </form>
        </div>
      )}
    </div>
  )
}
