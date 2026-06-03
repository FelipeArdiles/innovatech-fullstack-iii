import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
  Legend,
} from 'recharts'

export default function CapacityChart({ trabajadores = [] }) {
  const data = trabajadores.map((t) => ({
    nombre: t.nombre?.split(' ')[0] || `#${t.id}`,
    disponibles: t.horasDisponibles,
    asignadas: t.horasAsignadas,
    sobrecargado: t.sobrecargado,
  }))

  if (!data.length) {
    return <p className="chart-empty">No hay datos de capacidad</p>
  }

  return (
    <ResponsiveContainer width="100%" height={280}>
      <BarChart data={data} margin={{ top: 8, right: 8, left: 0, bottom: 0 }}>
        <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
        <XAxis dataKey="nombre" tick={{ fontSize: 12 }} />
        <YAxis tick={{ fontSize: 12 }} />
        <Tooltip />
        <Legend />
        <Bar dataKey="disponibles" name="Horas disponibles" fill="var(--color-info)" radius={[4, 4, 0, 0]} />
        <Bar dataKey="asignadas" name="Horas asignadas" radius={[4, 4, 0, 0]}>
          {data.map((entry, index) => (
            <Cell
              key={`cell-${index}`}
              fill={entry.sobrecargado ? 'var(--color-danger, #ef4444)' : 'var(--color-primary)'}
            />
          ))}
        </Bar>
      </BarChart>
    </ResponsiveContainer>
  )
}
