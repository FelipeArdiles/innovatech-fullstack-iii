export default function WorkerAvatars({ trabajadores = [], max = 5 }) {
  if (!trabajadores.length) {
    return <span className="worker-avatars worker-avatars--empty">Sin asignar</span>
  }

  const visible = trabajadores.slice(0, max)
  const extra = trabajadores.length - visible.length

  return (
    <div className="worker-avatars" aria-label={`${trabajadores.length} trabajadores`}>
      {visible.map((t) => (
        <span
          key={t.id}
          className="worker-avatars__item"
          title={`${t.nombre} (${t.rol})`}
        >
          {(t.nombre || '?').charAt(0).toUpperCase()}
        </span>
      ))}
      {extra > 0 && <span className="worker-avatars__more">+{extra}</span>}
    </div>
  )
}
