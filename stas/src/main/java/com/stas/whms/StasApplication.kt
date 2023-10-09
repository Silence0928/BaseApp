package com.stas.whms

import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.lib_common.app.BaseApplication
import com.lib_common.utils.ActivityStackManager
import java.util.concurrent.ScheduledThreadPoolExecutor
import kotlin.system.exitProcess

class StasApplication: BaseApplication() {
    private val TAG = "StasApplication"

    private val executor = ScheduledThreadPoolExecutor(3)

    private val exceptionHandler =
        Thread.UncaughtExceptionHandler { thread: Thread?, throwable: Throwable ->
            Log.i(TAG, "throwable==>$throwable")
            Log.i(TAG, "getCause==>" + throwable.cause)
            throwable.printStackTrace()
            try {
                executor.execute {
                    Looper.prepare()
                    val toast =
                        Toast.makeText(
                            applicationContext,
                            "系统内部发生异常，即将重启",
                            Toast.LENGTH_LONG
                        )
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    Looper.loop()
                }
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            restartApp()
        }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "MyApplication")
        if (!BuildConfig.DEBUG){
            Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
        }
    }

    /**
     * 重启应用
     */
    fun restartApp() {
        ActivityStackManager.getInstance().finishAllActivities()
        applicationContext.startActivity(
            applicationContext.packageManager.getLaunchIntentForPackage(
                applicationContext.packageName
            )
        )
        exitProcess(0)
    }

}