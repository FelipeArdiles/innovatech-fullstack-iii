import { useCallback, useEffect, useState } from 'react'
import { api } from '../api/client'
import FlashMessage from '../components/FlashMessage'
import PageHeader from '../components/ui/PageHeader'
import LoadingSkeleton from '../components/ui/LoadingSkeleton'
import CapacityChart from '../components/charts/CapacityChart'
import ProgressRing from '../components/charts/ProgressRing'
import { formatCLP } from '../utils/money'

export default function CapacidadPage() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = useCallback(() => {
    setLoading(true)
    setError('')
    return api.getCapacidadEquipo()
      .then(setData)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    load()
  }, [load])

  const usoGlobal = data && data.horasDisponiblesTotal > 0
    ? Math.round((data.horasAsignadasTotal / data.horasDisponiblesTotal) * 100)
    : 0

  return (
    <div className="page">
      <PageHeader
        title="Capacidad del equipo"
        subtitle="Horas disponibles vs asignadas por trabajador · KPI de recursos"
      />

      {loading && <LoadingSkeleton variant="card" rows={3} />}
      {!loading && error && <FlashMessage message={error} type="error" />}
      {!loading && !error && data && (
        <>
          <div className="summary-cards">
            <article className="summary-card">
              <span className="summary-card__label">Trabajadores</span>
              <strong className="summary-card__value">{data.totalTrabajadores}</strong>
            </article>
            <article className="summary-card">
              <span className="summary-card__label">Horas disponibles</span>
              <strong className="summary-card__value">{data.horasDisponiblesTotal} h</strong>
            </article>
            <article className="summary-card">
              <span className="summary-card__label">Horas asignadas</span>
              <strong className="summary-card__value">{data.horasAsignadasTotal} h</strong>
            </article>
            <article className={`summary-card${data.trabajadoresSobrecargados > 0 ? ' summary-card--danger' : ''}`}>
              <span className="summary-card__label">Sobrecarga</span>
              <strong className="summary-card__value">{data.trabajadoresSobrecargados}</strong>
              <span className="summary-card__hint">trabajador(es) &gt;100%</span>
            </article>
            <article className="summary-card">
              <span className="summary-card__label">Nómina mensual (CLP)</span>
              <strong className="summary-card__value">{formatCLP(data.costoMensualNominaClp)}</strong>
              <span className="summary-card__hint">Sueldos líquidos del equipo</span>
            </article>
            <article className="summary-card">
              <span className="summary-card__label">Costo horas asignadas (CLP)</span>
              <strong className="summary-card__value">{formatCLP(data.costoHorasAsignadasClp)}</strong>
              <span className="summary-card__hint">Prorrateo semanal por tareas</span>
            </article>
          </div>

          <div className="detail-grid detail-grid--capacity">
            <section className="detail-card">
              <h3>Utilización global</h3>
              <div className="detail-card__center">
                <ProgressRing value={Math.min(100, usoGlobal)} />
                <p className="detail-meta">
                  {data.horasAsignadasTotal} / {data.horasDisponiblesTotal} h semanales
                </p>
              </div>
            </section>
            <section className="detail-card detail-card--wide">
              <h3>Horas por trabajador</h3>
              <CapacityChart trabajadores={data.trabajadores} />
            </section>
          </div>

          <div className="capacity-table-wrap">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Trabajador</th>
                  <th>Rol</th>
                  <th>Sueldo mensual</th>
                  <th>Disponibles</th>
                  <th>Asignadas</th>
                  <th>Costo asignado</th>
                  <th>Carga %</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody>
                {data.trabajadores.map((t) => (
                  <tr key={t.id} className={t.sobrecargado ? 'row-overload' : ''}>
                    <td>{t.nombre}</td>
                    <td>{t.rol}</td>
                    <td>{formatCLP(t.sueldoMensualClp)}</td>
                    <td>{t.horasDisponibles} h</td>
                    <td>{t.horasAsignadas} h</td>
                    <td>{formatCLP(t.costoHorasAsignadasClp)}</td>
                    <td>
                      <span className={t.sobrecargado ? 'text-danger' : ''}>
                        {t.porcentajeCarga}%
                      </span>
                    </td>
                    <td>
                      {t.sobrecargado ? (
                        <span className="badge badge--danger">Sobrecarga</span>
                      ) : (
                        <span className="badge badge--success">OK</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  )
}
