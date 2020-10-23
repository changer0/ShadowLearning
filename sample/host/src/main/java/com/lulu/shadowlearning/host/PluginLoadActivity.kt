package com.lulu.shadowlearning.host

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lulu.shadowlearning.R

/**
 * 用于加载插件的 Activity，会展示加载 Loading
 */
class PluginLoadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plugin_load)
    }
}