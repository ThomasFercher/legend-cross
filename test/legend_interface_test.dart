import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:legend_interface/legend_interface.dart';

void main() {
  const MethodChannel channel = MethodChannel('legend_interface');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await LegendInterface.platformVersion, '42');
  });
}
