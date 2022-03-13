
import 'dart:async';

import 'package:flutter/services.dart';

class FlutterXimalaya {
  static const MethodChannel _channel = MethodChannel('flutter_ximalaya');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<void> init() async {
    return await _channel.invokeMethod('init');
  }

  static Future<void> goInSdk() async {
    return await _channel.invokeMethod('goInSdk');
  }
}
