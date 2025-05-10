package com.yurazhovnir.myfeeling
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
//        Shake.start(this, "")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        initToothpick()
//        initAppScope()
//        FirebaseApp.initializeApp(this)
//        Branch.getAutoInstance(this)
//        oneSignal()
    }

//    private fun initToothpick() {
//        if (BuildConfig.DEBUG) {
//            KTP.setConfiguration(Configuration.forDevelopment().preventMultipleRootScopes())
//        } else {
//            KTP.setConfiguration(Configuration.forProduction())
//        }
//    }

//    fun updateAppScope() {
//        KTP.closeScope(scope.name)
////        initAppScope()
//    }
//
//    private fun initAppScope() {
//        scope = KTP.openScope(DI.APP_SCOPE)
//            .installModules(AppModule(this))
//    }
//
//    override fun onTerminate() {
//        super.onTerminate()
//        KTP.closeScope(scope.name)
//    }

//    private fun oneSignal() {
//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
//        OneSignal.initWithContext(this)
//        OneSignal.setAppId(getString(R.string.onesignal_app_id))
//        OneSignal.unsubscribeWhenNotificationsAreDisabled(true)
//        OneSignal.setNotificationOpenedHandler { result ->
//            val notification = result.notification
//
//            if (notification != null) {
//                var action: String? = null
//                var id: String? = null
//
//                action = (notification.additionalData.get("action") as? String) ?: ""
//                id = (notification.additionalData.get("id") as? String) ?: ""
//
//                val temp = NotificationMessage()
//                if (id == "") {
//                    id = action
//                }
//                temp.id = UUID.randomUUID().toString()
//                temp.profileId = id.toString()
//                temp.action = action
//
//                RealmHelper.save(temp)
//            }
//        }
//        OneSignal.setNotificationWillShowInForegroundHandler { notificationReceivedEvent: OSNotificationReceivedEvent ->
//            OneSignal.onesignalLog(
//                OneSignal.LOG_LEVEL.VERBOSE,
//                "NotificationWillShowInForegroundHandler fired!" +
//                        " with notification event: " + notificationReceivedEvent.toString()
//            )
//        }
//    }
}