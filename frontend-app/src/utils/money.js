const clpFormatter = new Intl.NumberFormat('es-CL', {
  style: 'currency',
  currency: 'CLP',
  maximumFractionDigits: 0,
})

export function formatCLP(value) {
  if (value == null || value === '') return '—'
  return clpFormatter.format(Number(value))
}

/** @deprecated Usar formatCLP */
export const formatMoney = formatCLP

export function formatCLPCompact(value) {
  if (value == null || value === '') return '—'
  const n = Number(value)
  if (Math.abs(n) >= 1_000_000) return `$${(n / 1_000_000).toFixed(1)}M`
  if (Math.abs(n) >= 1_000) return `$${Math.round(n / 1_000)}k`
  return formatCLP(n)
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

export const OTROS_GASTOS_TOOLTIP =
  'Licencias, infraestructura cloud y gastos indirectos (~12% del presupuesto del proyecto)'
