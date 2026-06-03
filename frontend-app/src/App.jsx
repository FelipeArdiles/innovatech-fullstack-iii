import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { initAuth } from './auth/keycloak'
import Layout from './components/Layout'
import InicioPage from './pages/InicioPage'
import Dashboard from './pages/Dashboard'
import TrabajadoresPage from './pages/TrabajadoresPage'
import ProyectosPage from './pages/ProyectosPage'
import ProjectDetailPage from './pages/ProjectDetailPage'
import CapacidadPage from './pages/CapacidadPage'
import TareasPage from './pages/TareasPage'
import { Spinner } from './components/ui/LoadingSkeleton'
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
        <div className="app__brand">Innovatech Solutions</div>
        <p className="error">{error}</p>
      </div>
    )
  }

  if (!ready) {
    return (
      <div className="app app--centered">
        <div className="app__brand">Innovatech Solutions</div>
        <Spinner label="Iniciando plataforma…" />
      </div>
    )
  }

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<InicioPage />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="trabajadores" element={<TrabajadoresPage />} />
          <Route path="usuarios" element={<Navigate to="/trabajadores" replace />} />
          <Route path="proyectos" element={<ProyectosPage />} />
          <Route path="proyectos/:id" element={<ProjectDetailPage />} />
          <Route path="capacidad" element={<CapacidadPage />} />
          <Route path="tareas" element={<TareasPage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
