import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { lazy, Suspense, useEffect, useState } from 'react'
import { initAuth } from './auth/keycloak'
import Layout from './components/Layout'
import ErrorBoundary from './components/ErrorBoundary'
import InicioPage from './pages/InicioPage'
import Dashboard from './pages/Dashboard'
import TrabajadoresPage from './pages/TrabajadoresPage'
import ProyectosPage from './pages/ProyectosPage'
import TareasPage from './pages/TareasPage'
import LoadingSkeleton, { Spinner } from './components/ui/LoadingSkeleton'
import './App.css'

const ProjectDetailPage = lazy(() => import('./pages/ProjectDetailPage'))
const CapacidadPage = lazy(() => import('./pages/CapacidadPage'))

const AUTH_INIT_TIMEOUT_MS = 15000

function LazyPage({ children }) {
  return (
    <Suspense fallback={<LoadingSkeleton variant="card" rows={3} />}>
      {children}
    </Suspense>
  )
}

function App() {
  const [ready, setReady] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false
    const timeoutId = window.setTimeout(() => {
      if (!cancelled) {
        setError('Tiempo de espera agotado al conectar con Keycloak. Verifica que el servicio esté activo.')
      }
    }, AUTH_INIT_TIMEOUT_MS)

    initAuth()
      .then(() => {
        if (!cancelled) {
          window.clearTimeout(timeoutId)
          setReady(true)
        }
      })
      .catch((err) => {
        if (!cancelled) {
          window.clearTimeout(timeoutId)
          setError(err.message)
        }
      })

    return () => {
      cancelled = true
      window.clearTimeout(timeoutId)
    }
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
    <ErrorBoundary>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<InicioPage />} />
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="trabajadores" element={<TrabajadoresPage />} />
            <Route path="usuarios" element={<Navigate to="/trabajadores" replace />} />
            <Route path="proyectos" element={<ProyectosPage />} />
            <Route
              path="proyectos/:id"
              element={
                <LazyPage>
                  <ProjectDetailPage />
                </LazyPage>
              }
            />
            <Route
              path="capacidad"
              element={
                <LazyPage>
                  <CapacidadPage />
                </LazyPage>
              }
            />
            <Route path="tareas" element={<TareasPage />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ErrorBoundary>
  )
}

export default App
