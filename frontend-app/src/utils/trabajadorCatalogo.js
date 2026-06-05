/** Roles permitidos al crear/editar trabajadores (catálogo fijo). */
export const ROLES_TRABAJADOR = [
  'Analista Financiero TI',
  'Arquitecto de Software',
  'Backend Lead',
  'Business Analyst',
  'Cloud Architect',
  'DBA',
  'Desarrolladora Backend',
  'Desarrolladora Frontend',
  'Desarrolladora Full Stack',
  'Desarrollador Backend',
  'Desarrollador Frontend',
  'Desarrollador Full Stack',
  'Desarrollador Mobile',
  'DevOps',
  'DevOps Engineer',
  'Diseñadora de Producto',
  'Ingeniero de Datos',
  'Product Owner',
  'Project Manager',
  'QA Automation',
  'QA Lead',
  'QA Manual',
  'Scrum Master',
  'Security Engineer',
  'SRE',
  'Tech Lead',
  'Technical Writer',
  'UX Researcher',
  'UX/UI Designer',
]

export const HORAS_SEMANALES_MIN = 20
export const HORAS_SEMANALES_MAX = 45

export function isRolValido(rol) {
  return ROLES_TRABAJADOR.includes(rol)
}

export function isCapacidadHorasValida(horas) {
  const n = Number(horas)
  return Number.isInteger(n) && n >= HORAS_SEMANALES_MIN && n <= HORAS_SEMANALES_MAX
}
