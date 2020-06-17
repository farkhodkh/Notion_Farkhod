package ru.farkhodkhaknazarov.notion_farkhod.ui.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.farkhodkhaknazarov.notion_farkhod.App
import ru.farkhodkhaknazarov.notion_farkhod.R

class ManagePermissionsUtility {
    var list: List<String> = getPermissionList()

    val PermissionsRequestCode = 100

    suspend fun checkPermissions() {
        if (isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
            showAlert()
        }
    }

    suspend fun isPermissionsGranted(): Int {
        var counter = 0
        for (permission in list) {
            counter += ContextCompat.checkSelfPermission(App.context, permission)
        }

        return counter
    }

    private fun deniedPermission(): List<String> {
        return list.filter {
            ContextCompat.checkSelfPermission(
                App.context,
                it
            ) == PackageManager.PERMISSION_DENIED
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(App.activity)
        builder.setTitle(R.string.permission_header)
        builder.setMessage(R.string.permission_text)
        builder.setPositiveButton("OK", { dialog, which -> requestPermissions() })
        builder.setNeutralButton(R.string.cancel, null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun requestPermissions() {
        val deniedPermission = deniedPermission()
        ActivityCompat.requestPermissions(
            App.activity as Activity,
            deniedPermission.toTypedArray(),
            PermissionsRequestCode
        )
    }

    fun getPermissionList() = listOf<String>(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )
}