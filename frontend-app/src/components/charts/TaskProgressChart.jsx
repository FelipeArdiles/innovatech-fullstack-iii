import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip, Legend } from 'recharts'

const COLORS = {
  'Por hacer': 'var(--color-warning, #f59e0b)',
  'En progreso': 'var(--color-info, #3b82f6)',
  Hecho: 'var(--color-success, #10b981)',
}

export default function TaskProgressChart({ porHacer = 0, enProgreso = 0, hechas = 0 }) {
  const data = [
    { name: 'Por hacer', value: porHacer },
    { name: 'En progreso', value: enProgreso },
    { name: 'Hecho', value: hechas },
  ].filter((d) => d.value > 0)

  if (!data.length) {
    return <p className="chart-empty">Sin tareas registradas</p>
  }

  return (
    <ResponsiveContainer width="100%" height={220}>
      <PieChart>
        <Pie
          data={data}
          dataKey="value"
          nameKey="name"
          cx="50%"
          cy="50%"
          innerRadius={50}
          outerRadius={78}
          paddingAngle={2}
        >
          {data.map((entry) => (
            <Cell key={entry.name} fill={COLORS[entry.name]} />
          ))}
        </Pie>
        <Tooltip />
        <Legend />
      </PieChart>
    </ResponsiveContainer>
  )
}
