package ru.farkhodkhaknazarov.notion_farkhod

import android.app.Application
import android.content.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.farkhodkhaknazarov.notion_farkhod.ui.MainActivity
import ru.farkhodkhaknazarov.notion_farkhod.ui.utils.ManagePermissionsUtility

class App: Application() {
    companion object{
        lateinit var mInstance: App
        lateinit var context: Context
        lateinit var activity: MainActivity
        lateinit var premissionManager: ManagePermissionsUtility
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        context = applicationContext
        premissionManager = ManagePermissionsUtility()
    }
}