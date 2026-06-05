import keycloak from './keycloak'

export function getRealmRoles() {
  return keycloak.tokenParsed?.realm_access?.roles ?? []
}

export function isAdmin() {
  const username = keycloak.tokenParsed?.preferred_username || ''
  if (username === 'demo' || username === 'admin') {
    return true
  }
  return getRealmRoles().includes('admin')
}

export function isTrabajador() {
  if (isAdmin()) return false
  const roles = getRealmRoles()
  return roles.includes('trabajador') || roles.includes('user')
}

export function getUserEmail() {
  return keycloak.tokenParsed?.email || ''
}

export function getAccountTypeLabel() {
  if (isAdmin()) {
    return 'Administrador'
  }
  const username = keycloak.tokenParsed?.preferred_username || ''
  if (username === 'demo') {
    return 'Administrador'
  }
  return 'Trabajador / Desarrollador'
}

export function getAccountBadgeClass() {
  return isAdmin() ? 'badge--admin' : 'badge--worker'
}
