import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

class LegendInterface {
  static const MethodChannel _channel = const MethodChannel('legend_interface');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<Map> pickFile({List<String>? extensions}) async {
    return await _channel.invokeMethod('pickFile', extensions);
  }

  static Future<List<Object?>> pickMultipleFiles(
      {List<String>? extensions}) async {
    return await _channel.invokeMethod('pickMultipleFiles', extensions);
  }
}
