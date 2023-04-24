import 'package:flutter_test/flutter_test.dart';
import 'package:rfiid_reader/rfiid_reader.dart';
import 'package:rfiid_reader/rfiid_reader_platform_interface.dart';
import 'package:rfiid_reader/rfiid_reader_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockRfiidReaderPlatform
    with MockPlatformInterfaceMixin
    implements RfiidReaderPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<String?> connectBluetoothDevice() {
    // TODO: implement connectBluetoothDevice
    throw UnimplementedError();
  }

  @override
  Future<bool> createBT4Conn({required String bT4Param, required log}) {
    // TODO: implement createBT4Conn
    throw UnimplementedError();
  }

  @override
  Future<String?> setBeep({required String connID, required int onORoff}) {
    // TODO: implement setBeep
    throw UnimplementedError();
  }

  @override
  Future<String?> startBluetoothDeviceScan({required String connID}) {
    // TODO: implement startBluetoothDeviceScan
    throw UnimplementedError();
  }

  @override
  Future<String?> getBluetoothDeviceSOC({required String connId}) {
    // TODO: implement getBluetoothDeviceSOC
    throw UnimplementedError();
  }

  @override
  Future<List?> getBT4DeviceStrList() {
    // TODO: implement getBT4DeviceStrList
    throw UnimplementedError();
  }

  @override
  Future<String?> openBluetooth() {
    // TODO: implement openBluetooth
    throw UnimplementedError();
  }

  @override
  Future<String?> startPingPong(
      {required String connID, required String filterBy}) {
    // TODO: implement startPingPong
    throw UnimplementedError();
  }

  @override
  Future<String?> closeAllConnection() {
    // TODO: implement closeAllConnection
    throw UnimplementedError();
  }

  @override
  Future<String?> closeBluetooth() {
    // TODO: implement closeBluetooth
    throw UnimplementedError();
  }

  @override
  Future<String?> closeConnection({required String connId}) {
    // TODO: implement closeConnection
    throw UnimplementedError();
  }

  @override
  Future<bool?> create485Conn({required String bT4Param, required log}) {
    // TODO: implement create485Conn
    throw UnimplementedError();
  }

  @override
  Future<String?> getConnectedDevice() {
    // TODO: implement getConnectedDevice
    throw UnimplementedError();
  }

  @override
  Future<bool> isBluetoothOn() {
    // TODO: implement isBluetoothOn
    throw UnimplementedError();
  }

  @override
  Stream? readDataStream({required String connID}) {
    // TODO: implement readDataStream
    throw UnimplementedError();
  }

  @override
  Future<Map<String, dynamic>?> stopConnection({required String connID}) {
    // TODO: implement stopConnection
    throw UnimplementedError();
  }

  @override
  Future<Map<String, dynamic>?> stopPingPong({required String connID}) {
    // TODO: implement stopPingPong
    throw UnimplementedError();
  }
}

void main() {
  final RfiidReaderPlatform initialPlatform = RfiidReaderPlatform.instance;

  test('$MethodChannelRfiidReader is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelRfiidReader>());
  });

  test('getPlatformVersion', () async {
    RfiidReader rfiidReaderPlugin = RfiidReader();
    MockRfiidReaderPlatform fakePlatform = MockRfiidReaderPlatform();
    RfiidReaderPlatform.instance = fakePlatform;

    expect(await rfiidReaderPlugin.getPlatformVersion(), '42');
  });
}
