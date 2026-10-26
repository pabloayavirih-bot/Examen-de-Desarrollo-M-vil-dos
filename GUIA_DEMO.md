# Guía de demo / defensa (2–3 minutos)

1. **Inicio (15 s).** “La aplicación reúne tres ejercicios independientes; desde esta pantalla cada uno se puede probar por separado.” Mostrar los tres botones.
2. **E25-5 (30 s).** Abrir el ejercicio, conceder `POST_NOTIFICATIONS` y tocar “Mostrar Hola desde la app”. Explicar que Android 8+ requiere `NotificationChannel` y Android 13+ requiere el permiso. Tocar la notificación para mostrar el `PendingIntent`.
3. **E25-3 (45 s).** Abrir CRUD, agregar una tarea. Señalar en Firestore los campos `titulo`, `completada` y `timestamp`. Marcarla como completada y luego eliminarla. Explicar que `addSnapshotListener` actualiza la lista sin recargar.
4. **E50-5 (45 s).** Abrir el monitor y dejar desactivado “Silenciar notificaciones”. Agregar una tarea desde el CRUD: el listener recibe el cambio `ADDED` y presenta una notificación. Tocarla: abre directamente la pantalla de tareas. Activar silencio y repetir para demostrar `SharedPreferences`.
5. **Cierre (15 s).** “No hay datos simulados: las tareas se guardan en Cloud Firestore, y las notificaciones y la preferencia de silencio son funciones reales del dispositivo.”

Preguntas probables:

- **¿Por qué el listener no notifica las tareas ya existentes?** El primer snapshot inicial solo carga datos; las notificaciones se activan en cambios posteriores para no inundar al usuario.
- **¿Dónde se guarda el silencio?** En `SharedPreferences`, clave `notifications_muted`.
- **¿Cómo abre la pantalla correcta?** El `PendingIntent` envía `open_tasks=true` a `MainActivity`.
