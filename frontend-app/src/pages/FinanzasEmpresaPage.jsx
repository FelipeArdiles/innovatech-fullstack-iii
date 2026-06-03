import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import FlashMessage from '../components/FlashMessage'
import PageHeader from '../components/ui/PageHeader'
import LoadingSkeleton from '../components/ui/LoadingSkeleton'
import { formatMargen, formatMoney, margenBadgeClass } from '../utils/money'

export default function FinanzasEmpresaPage() {
  const [resumen, setResumen] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = useCallback(() => {
    setLoading(true)
    setError('')
    return api.getFinanzasResumen()
      .then(setResumen)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    load()
  }, [load])

  return (
    <div className="page">
      <PageHeader
        title="Finanzas empresa"
        subtitle="Ingresos, costos, margen y rentabilidad por proyecto · Innovatech Solutions"
      />

      {loading && <LoadingSkeleton variant="card" rows={4} />}
      {!loading && error && <FlashMessage message={error} type="error" />}
      {!loading && !error && resumen && (
        <>
          <div className="summary-cards">
            <article className="summary-card">
              <span className="summary-card__label">Ingresos totales</span>
              <strong className="summary-card__value">{formatMoney(resumen.ingresosTotales)}</strong>
            </article>
            <article className="summary-card">
              <span className="summary-card__label">Costos totales</span>
              <strong className="summary-card__value">{formatMoney(resumen.costosTotales)}</strong>
            </article>
            <article className={`summary-card${Number(resumen.gananciaNeta) >= 0 ? '' : ' summary-card--danger'}`}>
              <span className="summary-card__label">Ganancia neta</span>
              <strong className="summary-card__value">{formatMoney(resumen.gananciaNeta)}</strong>
              <span className="summary-card__hint">Margen {formatMargen(resumen.margenEmpresaPorcentaje)}</span>
            </article>
            <article className="summary-card">
              <span className="summary-card__label">Proyectos rentables</span>
              <strong className="summary-card__value">
                {resumen.proyectosRentables} / {resumen.proyectos?.length ?? 0}
              </strong>
            </article>
          </div>

          <section className="detail-card detail-card--wide" style={{ marginTop: '1rem' }}>
            <h3>Portafolio por proyecto</h3>
            <div className="fin-portfolio">
              {(resumen.proyectos ?? []).map((p) => (
                <Link key={p.proyectoId} to={`/proyectos/${p.proyectoId}?tab=finanzas`} className="fin-portfolio__row">
                  <div>
                    <strong>{p.nombreProyecto}</strong>
                    <span className="detail-meta">
                      Costo {formatMoney(p.costoAcumulado)} · Ingreso {formatMoney(p.ingresos)}
                    </span>
                  </div>
                  <span className={`badge ${margenBadgeClass(p.margenPorcentaje)}`}>
                    {formatMargen(p.margenPorcentaje)}
                  </span>
                  <span className="detail-meta">{formatMoney(p.ganancia)}</span>
                </Link>
              ))}
            </div>
          </section>
        </>
      )}
    </div>
  )
}
