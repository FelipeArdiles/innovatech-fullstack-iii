import { Navigate } from 'react-router-dom'
import { isAdmin } from '../auth/roles'

export default function RoleGuard({ children, adminOnly = false }) {
  if (adminOnly && !isAdmin()) {
    return <Navigate to="/dashboard" replace />
  }
  return children
}
