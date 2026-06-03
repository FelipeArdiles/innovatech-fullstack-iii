import { Component } from 'react'
import Button from './ui/Button'

export default class ErrorBoundary extends Component {
  constructor(props) {
    super(props)
    this.state = { error: null }
  }

  static getDerivedStateFromError(error) {
    return { error }
  }

  componentDidCatch(error, info) {
    console.error('ErrorBoundary:', error, info)
  }

  render() {
    if (this.state.error) {
      return (
        <div className="app app--centered">
          <div className="app__brand">Innovatech Solutions</div>
          <p className="error">
            {this.state.error.message || 'Ocurrió un error inesperado en la aplicación.'}
          </p>
          <Button variant="secondary" onClick={() => window.location.reload()}>
            Recargar
          </Button>
        </div>
      )
    }

    return this.props.children
  }
}
