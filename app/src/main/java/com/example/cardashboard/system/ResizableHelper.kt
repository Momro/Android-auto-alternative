package com.example.cardashboard.system

import android.content.Context
import android.content.Intent
import android.provider.Settings

object ResizableHelper {

    fun isForceResizableEnabled(context: Context): Boolean =
        Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.FORCE_RESIZABLE_ACTIVITIES,
            0
        ) == 1

    fun openDeveloperOptions(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
