import { daysUntil, formatDate, timelinePercent } from '../../utils/projectDates'

export default function DeadlineTimeline({ fechaInicio, fechaFin, atrasado }) {
  const percent = timelinePercent(fechaInicio, fechaFin)
  const dias = daysUntil(fechaFin)

  let countdownLabel = 'Sin plazo definido'
  if (dias != null) {
    if (dias > 0) countdownLabel = `${dias} día${dias === 1 ? '' : 's'} restantes`
    else if (dias === 0) countdownLabel = 'Vence hoy'
    else countdownLabel = `${Math.abs(dias)} día${Math.abs(dias) === 1 ? '' : 's'} de atraso`
  }

  return (
    <div className={`deadline-timeline${atrasado ? ' deadline-timeline--late' : ''}`}>
      <div className="deadline-timeline__meta">
        <span>{formatDate(fechaInicio)} → {formatDate(fechaFin)}</span>
        <strong>{countdownLabel}</strong>
      </div>
      <div className="deadline-timeline__track" role="progressbar" aria-valuenow={percent} aria-valuemin={0} aria-valuemax={100}>
        <div
          className="deadline-timeline__fill"
          style={{ width: `${percent}%` }}
        />
      </div>
      <span className="deadline-timeline__percent">{percent}% del plazo transcurrido</span>
    </div>
  )
}
