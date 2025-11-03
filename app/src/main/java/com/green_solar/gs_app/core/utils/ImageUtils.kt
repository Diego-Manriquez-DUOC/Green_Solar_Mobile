package com.green_solar.gs_app.core.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Guarda una imagen desde una URI de contenido (galería/cámara) en el almacenamiento interno de la app.
 * Devuelve la URI del archivo guardado si tiene éxito, o null si falla.
 */
fun saveImageToInternalStorage(context: Context, uri: Uri): Uri? {
    return try {
        // Usamos el contentResolver para abrir un stream de datos desde la URI original
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null

        // Creamos un nombre de archivo único para evitar colisiones
        val fileName = "${UUID.randomUUID()}.jpg"
        // Obtenemos el directorio de archivos de la app (privado)
        val file = File(context.filesDir, fileName)

        // Copiamos los datos desde el stream de entrada al archivo de salida
        val outputStream = FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        // Devolvemos la URI del nuevo archivo creado en el almacenamiento interno
        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
