import { useCallback, useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import { getNotificacionMeta } from '../utils/notificacionTipos'

function formatFecha(iso) {
  if (!iso) return ''
  const d = new Date(iso)
  return d.toLocaleString('es-CL', { dateStyle: 'short', timeStyle: 'short' })
}

export default function NotificationBell() {
  const [open, setOpen] = useState(false)
  const [count, setCount] = useState(0)
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(false)
  const panelRef = useRef(null)

  const refreshCount = useCallback(() => {
    api.getNotificacionesPendientes()
      .then((r) => setCount(r?.count ?? 0))
      .catch(() => setCount(0))
  }, [])

  const loadItems = useCallback(() => {
    setLoading(true)
    api.getNotificaciones()
      .then(setItems)
      .catch(() => setItems([]))
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    refreshCount()
    const interval = setInterval(refreshCount, 30000)
    return () => clearInterval(interval)
  }, [refreshCount])

  useEffect(() => {
    if (!open) return
    loadItems()
    function handleClick(e) {
      if (panelRef.current && !panelRef.current.contains(e.target)) {
        setOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClick)
    return () => document.removeEventListener('mousedown', handleClick)
  }, [open, loadItems])

  async function handleMarcarLeida(id) {
    try {
      await api.marcarNotificacionLeida(id)
      setItems((prev) => prev.map((n) => (n.id === id ? { ...n, leida: true } : n)))
      refreshCount()
    } catch {
      /* ignore */
    }
  }

  async function handleMarcarTodas() {
    try {
      await api.marcarTodasNotificaciones()
      setItems((prev) => prev.map((n) => ({ ...n, leida: true })))
      setCount(0)
    } catch {
      /* ignore */
    }
  }

  function toggle() {
    setOpen((v) => !v)
  }

  return (
    <div className="notif-bell" ref={panelRef}>
      <button
        type="button"
        className="notif-bell__trigger"
        aria-label="Notificaciones"
        onClick={toggle}
      >
        <span className="notif-bell__icon" aria-hidden>🔔</span>
        {count > 0 && <span className="notif-bell__badge">{count > 99 ? '99+' : count}</span>}
      </button>

      {open && (
        <div className="notif-bell__panel">
          <header className="notif-bell__header">
            <h4>Notificaciones</h4>
            {count > 0 && (
              <button type="button" className="notif-bell__mark-all" onClick={handleMarcarTodas}>
                Marcar todas
              </button>
            )}
          </header>
          {loading && <p className="notif-bell__empty">Cargando…</p>}
          {!loading && !items.length && (
            <p className="notif-bell__empty">Sin notificaciones</p>
          )}
          <ul className="notif-bell__list">
            {items.map((n) => {
              const meta = getNotificacionMeta(n.tipo)
              return (
              <li key={n.id} className={`notif-bell__item${n.leida ? ' notif-bell__item--read' : ''}`}>
                <div className={`notif-bell__tipo ${meta.className}`}>
                  <span aria-hidden>{meta.icon}</span>
                  <span>{meta.label}</span>
                </div>
                <p className="notif-bell__msg">{n.mensaje}</p>
                <div className="notif-bell__meta">
                  <span>{formatFecha(n.fecha)}</span>
                  {n.proyectoId && (
                    <Link
                      to={`/proyectos/${n.proyectoId}`}
                      onClick={() => setOpen(false)}
                    >
                      Ver proyecto
                    </Link>
                  )}
                  {n.tareaId && (
                    <Link
                      to={`/tareas?proyectoId=${n.proyectoId || ''}`}
                      onClick={() => setOpen(false)}
                    >
                      Ver tarea
                    </Link>
                  )}
                  {!n.leida && (
                    <button type="button" onClick={() => handleMarcarLeida(n.id)}>
                      Marcar leída
                    </button>
                  )}
                </div>
              </li>
            )})}
          </ul>
        </div>
      )}
    </div>
  )
}
