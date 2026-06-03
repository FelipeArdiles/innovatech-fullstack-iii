import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Legend,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import { formatCLP, formatCLPCompact } from '../../utils/money'

const CATEGORY_COLORS = [
  '#6366f1', '#8b5cf6', '#ec4899', '#f59e0b', '#10b981', '#06b6d4',
]

export default function ProjectFinancialCharts({ finanzas }) {
  if (!finanzas) return null

  const barData = [
    { name: 'Costo', valor: Number(finanzas.costoAcumulado) || 0 },
    { name: 'Ingresos', valor: Number(finanzas.ingresos) || 0 },
  ]

  const pieData = (finanzas.desglosePorCategoria ?? []).map((c) => ({
    name: c.categoria?.replace('_', ' ') ?? 'Sin categoría',
    value: Number(c.valor) || 0,
  }))

  return (
    <div className="fin-charts">
      <div className="fin-charts__block">
        <h4>Costo vs ingreso</h4>
        <ResponsiveContainer width="100%" height={220}>
          <BarChart data={barData} margin={{ top: 8, right: 8, left: 0, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
            <XAxis dataKey="name" tick={{ fontSize: 12 }} />
            <YAxis tick={{ fontSize: 11 }} tickFormatter={(v) => formatCLPCompact(v)} />
            <Tooltip formatter={(v) => formatCLP(v)} />
            <Bar dataKey="valor" radius={[6, 6, 0, 0]}>
              <Cell fill="#f97316" />
              <Cell fill="#6366f1" />
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </div>

      {pieData.length > 0 && (
        <div className="fin-charts__block">
          <h4>Gasto por categoría</h4>
          <ResponsiveContainer width="100%" height={220}>
            <PieChart>
              <Pie
                data={pieData}
                dataKey="value"
                nameKey="name"
                cx="50%"
                cy="50%"
                innerRadius={50}
                outerRadius={80}
                paddingAngle={2}
              >
                {pieData.map((_, i) => (
                  <Cell key={i} fill={CATEGORY_COLORS[i % CATEGORY_COLORS.length]} />
                ))}
              </Pie>
              <Tooltip formatter={(v) => formatCLP(v)} />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>
      )}
    </div>
  )
}
