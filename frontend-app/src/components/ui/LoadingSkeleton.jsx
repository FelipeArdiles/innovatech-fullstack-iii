export default function LoadingSkeleton({ rows = 4, variant = 'card' }) {
  if (variant === 'table') {
    return (
      <div className="skeleton-table">
        {Array.from({ length: rows }).map((_, i) => (
          <div key={i} className="skeleton skeleton--row" style={{ animationDelay: `${i * 0.06}s` }} />
        ))}
      </div>
    )
  }

  if (variant === 'kanban') {
    return (
      <div className="kanban-skeleton">
        {Array.from({ length: 3 }).map((_, col) => (
          <div key={col} className="kanban-skeleton__col">
            <div className="skeleton skeleton--title" />
            {Array.from({ length: 2 }).map((_, row) => (
              <div
                key={row}
                className="skeleton skeleton--card"
                style={{ animationDelay: `${(col * 2 + row) * 0.08}s` }}
              />
            ))}
          </div>
        ))}
      </div>
    )
  }

  return (
    <div className="skeleton-grid">
      {Array.from({ length: rows }).map((_, i) => (
        <div
          key={i}
          className="skeleton skeleton--kpi"
          style={{ animationDelay: `${i * 0.08}s` }}
        />
      ))}
    </div>
  )
}

export function Spinner({ label = 'Cargando…' }) {
  return (
    <div className="spinner-wrap" role="status">
      <div className="spinner" />
      <span>{label}</span>
    </div>
  )
}
