export function formatMoney(value) {
  if (value == null || value === '') return '—'
  return `$${Number(value).toLocaleString('es-CL')}`
}

export function formatMargen(pct) {
  if (pct == null) return '—'
  const n = Number(pct)
  const sign = n >= 0 ? '+' : ''
  return `${sign}${n.toFixed(1)}%`
}

export function margenBadgeClass(pct) {
  if (pct == null) return 'badge--neutral'
  if (pct >= 20) return 'badge--success'
  if (pct >= 0) return 'badge--warning'
  return 'badge--danger'
}
