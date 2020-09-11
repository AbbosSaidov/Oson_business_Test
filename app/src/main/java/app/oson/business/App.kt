package app.oson.business

import android.app.Application
import android.content.Context

class App: Application() {


    override fun onCreate() {
        super.onCreate()
        App.context = applicationContext
    }


    companion object {
        lateinit var context: Context
    }

}