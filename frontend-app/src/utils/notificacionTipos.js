const TIPOS = {
  TAREA_ASIGNADA: { label: 'Nueva tarea', icon: '📋', className: 'notif-tipo--task' },
  TAREA_COMPLETADA: { label: 'Tarea completada', icon: '✅', className: 'notif-tipo--done' },
  PLAZO_PROXIMO: { label: 'Plazo próximo', icon: '⏰', className: 'notif-tipo--deadline' },
  PLAZO_VENCIDO: { label: 'Plazo vencido', icon: '⚠️', className: 'notif-tipo--late' },
  AVISO_PROYECTO: { label: 'Aviso del proyecto', icon: '📢', className: 'notif-tipo--broadcast' },
  COMENTARIO_TAREA: { label: 'Comentario', icon: '💬', className: 'notif-tipo--comment' },
  MIEMBRO_AGREGADO: { label: 'Agregado al proyecto', icon: '👥', className: 'notif-tipo--team' },
  MIEMBRO_REMOVIDO: { label: 'Removido del proyecto', icon: '👤', className: 'notif-tipo--team' },
}

export function getNotificacionMeta(tipo) {
  return TIPOS[tipo] ?? { label: 'Notificación', icon: '🔔', className: 'notif-tipo--default' }
}
