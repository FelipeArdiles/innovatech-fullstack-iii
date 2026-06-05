import { isAdmin } from '../auth/roles'
import Dashboard from './Dashboard'
import TrabajadorDashboard from './TrabajadorDashboard'

export default function DashboardRouter() {
  return isAdmin() ? <Dashboard /> : <TrabajadorDashboard />
}
