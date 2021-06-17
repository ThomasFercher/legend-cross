import 'dart:async';

import 'package:flutter/services.dart';

class LegendInterface {
  static const MethodChannel _channel = const MethodChannel('legend_interface');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String?> get pickFile async {
    final String? version = await _channel.invokeMethod('pickFile');
    return version;
  }
}
