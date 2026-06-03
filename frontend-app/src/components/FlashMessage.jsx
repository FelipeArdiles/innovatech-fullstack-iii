export default function FlashMessage({ message, type = 'success', onClose }) {
  if (!message) return null

  return (
    <div className={`flash flash--${type} flash--animated`} role="alert">
      <span>{message}</span>
      {onClose && (
        <button type="button" className="flash__close" onClick={onClose} aria-label="Cerrar">
          ×
        </button>
      )}
    </div>
  )
}
