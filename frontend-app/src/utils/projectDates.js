export function parseDate(value) {
  if (!value) return null
  const d = new Date(value)
  return Number.isNaN(d.getTime()) ? null : d
}

export function formatDate(value) {
  const d = parseDate(value)
  if (!d) return '—'
  return d.toLocaleDateString('es-CL', { day: '2-digit', month: 'short', year: 'numeric' })
}

export function daysUntil(value) {
  const end = parseDate(value)
  if (!end) return null
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  end.setHours(0, 0, 0, 0)
  return Math.ceil((end - today) / (1000 * 60 * 60 * 24))
}

export function timelinePercent(inicio, fin) {
  const start = parseDate(inicio)
  const end = parseDate(fin)
  if (!start || !end || end <= start) return 0
  const now = new Date()
  if (now <= start) return 0
  if (now >= end) return 100
  return Math.round(((now - start) / (end - start)) * 100)
}

export function isProyectoAtrasado(proyecto) {
  if (proyecto?.atrasado) return true
  const dias = daysUntil(proyecto?.fechaFin)
  if (dias == null) return false
  const estado = proyecto?.estado
  if (estado === 'COMPLETADO' || estado === 'CANCELADO') return false
  return dias < 0
}
