import Keycloak from 'keycloak-js'

const keycloakConfig = {
  url: import.meta.env.VITE_KEYCLOAK_URL || 'http://localhost:8180',
  realm: import.meta.env.VITE_KEYCLOAK_REALM || 'innovatech',
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID || 'innovatech-frontend',
}

const keycloak = new Keycloak(keycloakConfig)

export async function initAuth() {
  const authenticated = await keycloak.init({
    onLoad: 'login-required',
    pkceMethod: 'S256',
    checkLoginIframe: false,
  })
  return authenticated
}

export function getToken() {
  return keycloak.token
}

export async function refreshToken() {
  try {
    await keycloak.updateToken(30)
    return keycloak.token
  } catch {
    await keycloak.login()
    return keycloak.token
  }
}

export function logout() {
  keycloak.logout({ redirectUri: window.location.origin })
}

export function getUsername() {
  return keycloak.tokenParsed?.preferred_username || 'usuario'
}

export default keycloak
