# Ejercicios de Firebase y notificaciones

**Combinación elegida:** E50-5 + E25-3 + E25-5 = **100 puntos**.

## Integrantes

- Jorge Pablo Ayaviri Hinojosa
- Jonathan Luizaga Zapata
- Rafael Alejandro Quisbert Paco
- Bryan Sejas Espinoza
- José Alejandro Terrazas Antezana

## Qué hace la aplicación

La pantalla principal permite abrir tres ejercicios independientes:

1. **E25-3 — CRUD de tareas en Firestore.** Agrega tareas con los campos `titulo`, `completada` y `timestamp`; las lista en tiempo real, permite marcarlas como completadas y eliminarlas.
2. **E25-5 — Notificación local.** Crea un `NotificationChannel`, solicita `POST_NOTIFICATIONS` en Android 13 o superior y muestra el texto **“Hola desde la app”**. Al tocar el aviso se abre la app.
3. **E50-5 — Listener y avisos de tareas.** Mantiene un listener de Firestore para detectar tareas nuevas, genera una notificación y ofrece un interruptor para silenciarlas mediante `SharedPreferences`. Al tocar la notificación se abre directamente el CRUD de tareas.

## Preparación de Firebase (necesaria una sola vez)

El archivo `androidApp/google-services.json` ya corresponde al proyecto de Firebase de la aplicación. En la consola de Firebase, habilitar **Cloud Firestore** en modo de prueba y publicar temporalmente estas reglas para la demostración:

```text
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /tasks/{taskId} {
      allow read, write: if true;
    }
  }
}
```

Estas reglas son solamente para la práctica académica; no deben usarse en producción. La misma versión está en `firestore.rules`.

## Ejecutar

1. Abrir la carpeta `tareaCotizacion` con Android Studio y esperar la sincronización de Gradle.
2. Conectar un teléfono con depuración USB o iniciar un emulador.
3. Seleccionar el dispositivo y ejecutar el módulo `androidApp`.
4. En Android 13 o superior, aceptar el permiso de notificaciones desde el ejercicio E25-5.

Desde consola también se puede generar el APK con:

```powershell
.\gradlew.bat :androidApp:assembleDebug
```

El APK se genera en `androidApp/build/outputs/apk/debug/androidApp-debug.apk`.

## Prueba rápida

1. Abrir **E25-5**, conceder el permiso y tocar “Mostrar Hola desde la app”. Verificar la notificación y tocarla.
2. Abrir **E25-3**, escribir una tarea, agregarla, marcarla como completada y eliminarla.
3. Abrir **E50-5**, dejar desactivado “Silenciar notificaciones” y agregar una tarea desde E25-3 (u otro cliente conectado a la misma colección). Debe aparecer un aviso; tocarlo abre las tareas.
4. Activar el interruptor de silencio y agregar otra tarea: no debe aparecer aviso.

## Decisiones técnicas

- Se usó una sola colección `tasks` de Cloud Firestore para que E25-3 y E50-5 compartan datos reales.
- `addSnapshotListener` refresca la lista sin recargar la pantalla y entrega los cambios `ADDED` que usan las notificaciones de E50-5.
- La preferencia `notifications_muted` se guarda localmente con `SharedPreferences`.
- Se usaron `ViewModel` y clases pequeñas (`TaskRepository`, `AppNotifications`) para separar UI, Firestore y notificaciones sin introducir una arquitectura compleja.
