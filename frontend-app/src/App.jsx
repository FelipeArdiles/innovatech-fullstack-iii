import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { initAuth } from './auth/keycloak'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'
import UsuariosPage from './pages/UsuariosPage'
import ProyectosPage from './pages/ProyectosPage'
import './App.css'

function App() {
  const [ready, setReady] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    initAuth()
      .then(() => setReady(true))
      .catch((err) => setError(err.message))
  }, [])

  if (error) {
    return (
      <div className="app app--centered">
        <h1>Innovatech Solutions</h1>
        <p className="error">Error: {error}</p>
      </div>
    )
  }

  if (!ready) {
    return (
      <div className="app app--centered">
        <h1>Innovatech Solutions</h1>
        <p className="state-message">Cargando plataforma…</p>
      </div>
    )
  }

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Dashboard />} />
          <Route path="usuarios" element={<UsuariosPage />} />
          <Route path="proyectos" element={<ProyectosPage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
