package com.stas.whms

import android.content.Intent
import android.os.Looper
import android.os.Process
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.lib_common.app.BaseApplication
import com.stas.whms.module.login.SystemLoginActivity
import java.util.concurrent.ScheduledThreadPoolExecutor
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
        val intent = Intent(applicationContext, SystemLoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        applicationContext.startActivity(intent)
        Process.killProcess(Process.myPid())
    }

}