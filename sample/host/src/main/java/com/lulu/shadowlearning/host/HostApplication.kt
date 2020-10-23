package com.lulu.shadowlearning.host

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import android.os.StrictMode
import android.webkit.WebView
import com.tencent.shadow.core.common.LoggerFactory
import com.tencent.shadow.dynamic.host.DynamicRuntime

/**
 * @author zhanglulu on 2020/10/23.
 * for  宿主的 Application
 */
class HostApplication: Application() {

    companion object {
        private lateinit var app: HostApplication

        @JvmStatic
        public fun getApp(): HostApplication {
            return app
        }
    }
    override fun onCreate() {
        super.onCreate()
        app = this
        //Android P 适配
        detectNonSdkApiUsageOnAndroidP()
        setWebViewDataDirectorySuffix()
        //日志系统
        LoggerFactory.setILoggerFactory(AndroidLogLoggerFactory())

        if (isProcess(this, ":plugin")) {
            //在全动态架构中，Activity组件没有打包在宿主而是位于被动态加载的runtime，
            //为了防止插件crash后，系统自动恢复crash前的Activity组件，此时由于没有加载runtime而发生classNotFound异常，导致二次crash
            //因此这里恢复加载上一次的runtime
            DynamicRuntime.recoveryRuntime(this)
        }
        PluginHelper.getInstance().init(this)
    }

    private fun setWebViewDataDirectorySuffix() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return
        }
        WebView.setDataDirectorySuffix(getProcessName())
    }

    private fun detectNonSdkApiUsageOnAndroidP() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return
        }
        val builder = StrictMode.VmPolicy.Builder()
        builder.detectNonSdkApiUsage()
        StrictMode.setVmPolicy(builder.build())
    }

    private fun isProcess(context: Context, processName: String): Boolean {
        var currentProcessName = ""
        val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (processInfo in manager.runningAppProcesses) {
            if (processInfo.pid == Process.myPid()) {
                currentProcessName = processInfo.processName
                break
            }
        }
        return currentProcessName.endsWith(processName)
    }
}