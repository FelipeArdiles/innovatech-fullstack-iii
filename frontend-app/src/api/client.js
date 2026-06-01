import { getToken, refreshToken } from '../auth/keycloak'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

async function authFetch(path, options = {}) {
  await refreshToken()
  const token = getToken()
  const headers = {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
    ...options.headers,
  }
  const response = await fetch(`${API_URL}${path}`, { ...options, headers })
  if (!response.ok) {
    const message = await response.text()
    throw new Error(message || `Error ${response.status}`)
  }
  if (response.status === 204) {
    return null
  }
  return response.json()
}

export const api = {
  getDashboard: () => authFetch('/api/dashboard'),
  getUsuarios: () => authFetch('/api/usuarios'),
  getProyectos: () => authFetch('/api/proyectos'),
  createUsuario: (data) => authFetch('/api/usuarios', { method: 'POST', body: JSON.stringify(data) }),
  createProyecto: (data) => authFetch('/api/proyectos', { method: 'POST', body: JSON.stringify(data) }),
}
