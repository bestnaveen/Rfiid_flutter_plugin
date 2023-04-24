import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'rfiid_reader_method_channel.dart';

abstract class RfiidReaderPlatform extends PlatformInterface {
  /// Constructs a RfiidReaderPlatform.
  RfiidReaderPlatform() : super(token: _token);

  static final Object _token = Object();

  static RfiidReaderPlatform _instance = MethodChannelRfiidReader();

  /// The default instance of [RfiidReaderPlatform] to use.
  ///
  /// Defaults to [MethodChannelRfiidReader].
  static RfiidReaderPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [RfiidReaderPlatform] when
  /// they register themselves.
  static set instance(RfiidReaderPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> openBluetooth();
  Future<String?> closeBluetooth();

  Future<List<dynamic>?> getBT4DeviceStrList();

  Future<bool?> createBT4Conn({required String bT4Param, required dynamic log});
  Future<bool?> create485Conn({required String bT4Param, required dynamic log});

  Future<String?> getBluetoothDeviceSOC({required String connId});

  Future<String?> closeConnection({required String connId});
  Future<String?> closeAllConnection();

  Future<String?> startBluetoothDeviceScan({required String connID});
  Future<String?> startPingPong(
      {required String connID, required String filterBy});

  Future<Map<String, dynamic>?> stopPingPong({required String connID});
  Future<Map<String, dynamic>?> stopConnection({required String connID});
  Future<bool> isBluetoothOn();
  Future<String?> getConnectedDevice();

  Future<String?> setBeep({required String connID, required int onORoff});

  Stream<dynamic>? readDataStream({required String connID});
}
