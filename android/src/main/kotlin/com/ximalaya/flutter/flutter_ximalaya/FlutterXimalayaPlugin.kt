package com.ximalaya.flutter.flutter_ximalaya

import android.content.Context
import androidx.annotation.NonNull
import androidx.multidex.MultiDex
import com.ximalaya.ting.android.miyataopensdk.XmUISdk
import com.ximalaya.ting.android.miyataopensdk.XmUISdk.UISdkConfig
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import org.jetbrains.annotations.NotNull

/** FlutterXimalayaPlugin */
class FlutterXimalayaPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var context : Context
  private lateinit var flutterPluginBinding: FlutterPlugin.FlutterPluginBinding

  constructor(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    this.context = flutterPluginBinding.applicationContext
    this.flutterPluginBinding = flutterPluginBinding
  }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_ximalaya")
    channel.setMethodCallHandler(this)
  }

  private fun init(@NotNull call: MethodCall, @NotNull result: Result) {

    var mAppSecret = "3b8bd8ff0278fff9e174f1ac5ce8010d"
    var mAppKey = "2c2cfad6a0cc6def2dca4b8f0f8e5416"
    var mPackId = "12345"
    var mSiteId = "5144"
    var mRedirectUrl = DTransferConstants.BASE_COLLECTOR_URL + "/get_access_token"
    val sdkConfig = UISdkConfig(mAppSecret, mAppKey, mPackId, mSiteId, mRedirectUrl, false)
    XmUISdk.getInstance().init(context, sdkConfig)
    CommonRequest.getInstanse().setNetLoggable(true)
  }

  private fun goInSdk(@NotNull call: MethodCall, @NotNull result: Result) {
    XmUISdk.getInstance().startMain(context)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else if(call.method == "init") {
      init(call, result)
    } else if(call.method == "goInSdk") {
      goInSdk(call, result)
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
