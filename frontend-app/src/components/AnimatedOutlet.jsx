import { useLocation, Outlet } from 'react-router-dom'

export default function AnimatedOutlet() {
  const location = useLocation()
  return (
    <div key={location.pathname} className="route-transition">
      <Outlet />
    </div>
  )
}
