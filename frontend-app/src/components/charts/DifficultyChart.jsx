import { Bar, BarChart, CartesianGrid, Cell, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'

const COLORS = { BAJA: '#10b981', MEDIA: '#f59e0b', ALTA: '#ef4444' }

export default function DifficultyChart({ baja, media, alta }) {
  const data = [
    { name: 'Baja', cantidad: baja ?? 0, fill: COLORS.BAJA },
    { name: 'Media', cantidad: media ?? 0, fill: COLORS.MEDIA },
    { name: 'Alta', cantidad: alta ?? 0, fill: COLORS.ALTA },
  ]

  return (
    <ResponsiveContainer width="100%" height={180}>
      <BarChart data={data} margin={{ top: 8, right: 8, left: 0, bottom: 0 }}>
        <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
        <XAxis dataKey="name" tick={{ fontSize: 12 }} />
        <YAxis allowDecimals={false} tick={{ fontSize: 11 }} />
        <Tooltip />
        <Bar dataKey="cantidad" radius={[6, 6, 0, 0]}>
          {data.map((entry) => (
            <Cell key={entry.name} fill={entry.fill} />
          ))}
        </Bar>
      </BarChart>
    </ResponsiveContainer>
  )
}
