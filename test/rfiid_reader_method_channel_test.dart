import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:rfiid_reader/rfiid_reader_method_channel.dart';

void main() {
  MethodChannelRfiidReader platform = MethodChannelRfiidReader();
  const MethodChannel channel = MethodChannel('rfiid_reader');

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
    expect(await platform.getPlatformVersion(), '42');
  });
}
