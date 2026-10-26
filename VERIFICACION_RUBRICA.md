# Verificación de rúbrica

Fecha de verificación: 26 de octubre de 2026.

| Requisito | Resultado | Evidencia |
|---|---|---|
| Pantalla principal con tres ejercicios independientes | Cumple | Inicio con botones E25-3, E25-5 y E50-5. Probado en Samsung S21 Plus. |
| E25-3: alta de tarea con `titulo`, `completada`, `timestamp` | Cumple | `TaskRepository.add` guarda los tres campos. Se creó una tarea real de prueba en Firestore. |
| E25-3: listar en tiempo real | Cumple | `addSnapshotListener`; la tarea creada apareció sin recargar. |
| E25-3: completar y eliminar | Cumple | La casilla cambió a completada y la tarea temporal fue eliminada de Firestore. |
| E25-3: errores básicos | Cumple | Se muestra el error del listener o de las operaciones; no se permite título vacío. |
| E25-5: NotificationChannel | Cumple | `AppNotifications.createChannel` crea el canal `tasks_channel`. |
| E25-5: `POST_NOTIFICATIONS` | Cumple | Declarado en el manifiesto y solicitado en Android 13+. |
| E25-5: aviso “Hola desde la app” y `PendingIntent` | Cumple en código y UI | Botón disponible y el aviso incluye `PendingIntent` hacia `MainActivity`. Para ver el aviso se debe aceptar el permiso en el teléfono. |
| E50-5: listener de nuevas tareas | Cumple | Listener independiente identifica cambios `ADDED` después de la carga inicial. |
| E50-5: aviso al agregar tarea | Cumple en código | Cada nueva tarea no silenciada llama a `AppNotifications.show`. Requiere conceder el permiso de notificaciones en el teléfono. |
| E50-5: abrir pantalla de tareas desde aviso | Cumple | El extra `open_tasks` abre E25-3; se verificó iniciando la actividad con dicho extra en el Samsung. |
| E50-5: silenciar con SharedPreferences | Cumple | Interruptor guarda `notifications_muted` y evita mostrar avisos. |
| Compilación | Cumple | `:androidApp:assembleDebug --offline --no-daemon --no-configuration-cache` finalizó exitosamente. |
| Ejecución en dispositivo | Cumple | APK instalado y abierto sin fallos en Samsung S21 Plus (`R3CNC0GQG3J`). |

Pendiente manual mínimo: aceptar el permiso de notificaciones la primera vez desde E25-5. Esto depende de la decisión del usuario del teléfono; no se forzó durante la prueba.
