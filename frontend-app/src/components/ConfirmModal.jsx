import Button from './ui/Button'

export default function ConfirmModal({ open, title, message, onConfirm, onCancel, loading }) {
  if (!open) return null

  return (
    <div className="modal-overlay modal-overlay--animated" onClick={onCancel}>
      <div className="modal modal--animated" onClick={(e) => e.stopPropagation()} role="dialog" aria-modal="true">
        <h3>{title}</h3>
        <p>{message}</p>
        <div className="modal__actions">
          <Button variant="secondary" onClick={onCancel} disabled={loading}>
            Cancelar
          </Button>
          <Button variant="danger" onClick={onConfirm} disabled={loading}>
            {loading ? 'Eliminando…' : 'Eliminar'}
          </Button>
        </div>
      </div>
    </div>
  )
}
