export default function Card({ children, className = '', hover = false, gradient, style }) {
  const classes = [
    'card',
    hover ? 'card--hover' : '',
    gradient ? `card--gradient-${gradient}` : '',
    className,
  ]
    .filter(Boolean)
    .join(' ')

  return (
    <article className={classes} style={style}>
      {children}
    </article>
  )
}
