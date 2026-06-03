import Button from './Button'

export default function PageHeader({ title, subtitle, actions }) {
  return (
    <header className="page-header">
      <div className="page-header__text">
        <h3>{title}</h3>
        {subtitle && <p className="page-header__subtitle">{subtitle}</p>}
      </div>
      {actions && <div className="page-header__actions">{actions}</div>}
    </header>
  )
}

export { Button }
