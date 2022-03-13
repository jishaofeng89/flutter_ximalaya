package com.ximalaya.flutter.flutter_ximalaya

import android.content.Context
import androidx.annotation.NonNull
import com.ximalaya.ting.android.miyataopensdk.IntentXmUISdk
import com.ximalaya.ting.android.miyataopensdk.IntentXmUISdk.UISdkConfig
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

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    this.context = flutterPluginBinding.applicationContext
    this.flutterPluginBinding = flutterPluginBinding

    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_ximalaya")
    channel.setMethodCallHandler(this)
  }

  private fun init(@NotNull call: MethodCall, @NotNull result: Result) {

    var mAppSecret = "7791a1892a4c1b1566d004cbacd79e19"
    var mAppKey = "35cc6d4e1e097d0105f283e29d961186"
    var mPackId = "com.kiss_you_5g"
    var mSiteId = "6347"
    var mRedirectUrl = DTransferConstants.BASE_COLLECTOR_URL + "/get_access_token"
    val sdkConfig = UISdkConfig(mAppSecret, mAppKey, mPackId, mSiteId, mRedirectUrl, false)
    IntentXmUISdk.getInstance().init(context, sdkConfig)
    CommonRequest.getInstanse().setNetLoggable(true)
  }

  private fun goInSdk(@NotNull call: MethodCall, @NotNull result: Result) {
    IntentXmUISdk.getInstance().startMain(context)
//    var intent = Intent()
//    intent.setClass(context, ProxyActivity().javaClass)
//    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//    context.startActivity(intent)
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
