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
  let response
  try {
    response = await fetch(`${API_URL}${path}`, { ...options, headers })
  } catch (err) {
    const hint = err?.message === 'Load failed' || err?.name === 'TypeError'
      ? ' (verifica que Docker exponga el API en ' + API_URL + ')'
      : ''
    throw new Error((err?.message || 'Error de red') + hint)
  }
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
  getUsuario: (id) => authFetch(`/api/usuarios/${id}`),
  createUsuario: (data) =>
    authFetch('/api/usuarios', { method: 'POST', body: JSON.stringify(data) }),
  updateUsuario: (id, data) =>
    authFetch(`/api/usuarios/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  deleteUsuario: (id) =>
    authFetch(`/api/usuarios/${id}`, { method: 'DELETE' }),

  getProyectos: () => authFetch('/api/proyectos'),
  getProyecto: (id) => authFetch(`/api/proyectos/${id}`),
  createProyecto: (data) =>
    authFetch('/api/proyectos', { method: 'POST', body: JSON.stringify(data) }),
  updateProyecto: (id, data) =>
    authFetch(`/api/proyectos/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  deleteProyecto: (id) =>
    authFetch(`/api/proyectos/${id}`, { method: 'DELETE' }),
}
