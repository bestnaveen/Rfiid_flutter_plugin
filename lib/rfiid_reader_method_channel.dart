import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:rfiid_reader/base.dart';

import 'rfiid_reader_platform_interface.dart';

/// An implementation of [RfiidReaderPlatform] that uses method channels.
class MethodChannelRfiidReader extends RfiidReaderPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('rfiid_reader');
  final eventChannel = const EventChannel('rfiid_reader/readDataStream');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> setBeep(
      {required String connID, required int onORoff}) async {
    return await methodChannel
        .invokeMethod<String?>(setBeepMethod, <String, dynamic>{
      'connID': connID,
      'onORoff': onORoff,
    });
  }

  @override
  Future<bool?> createBT4Conn({required String bT4Param, required log}) async {
    return await methodChannel.invokeMethod<bool>(
      createBT4ConnMethod,
      <String, dynamic>{
        'bT4Param': bT4Param,
        'log': log,
      },
    );
  }

  @override
  Future<List<dynamic>?> getBT4DeviceStrList() async {
    return await methodChannel
        .invokeMethod<List<dynamic>?>(getBT4DeviceStrListMethod);
  }

  @override
  Future<String?> openBluetooth() async {
    return await methodChannel.invokeMethod<String?>(openBluetoothMethod);
  }

  @override
  Future<String?> startBluetoothDeviceScan({required String connID}) async {
    return await methodChannel
        .invokeMethod<String>(startBluetoothDeviceScanMethod, <String, dynamic>{
      'connID': connID,
    });
  }

  @override
  Future<String?> getBluetoothDeviceSOC({required String connId}) async {
    return await methodChannel
        .invokeMethod<String>(getBluetoothDeviceSOCMethod, <String, dynamic>{
      'connID': connId,
    });
  }

  @override
  Future<String?> startPingPong(
      {required String connID, required String filterBy}) async {
    return await methodChannel
        .invokeMethod<String?>(startPingPongMethod, <String, dynamic>{
      'connID': connID,
      'filterBy': filterBy,
    });
  }

  @override
  Future<Map<String, dynamic>?> stopPingPong({required String connID}) async {
    final res = await methodChannel.invokeMethod<Map<Object?, Object?>>(
        stopPingPongMethod, <String, dynamic>{
      'connID': connID,
    });
    Map<String, dynamic> newMap = {};
    res?.forEach((key, value) {
      newMap[key.toString()] = value;
    });
    return newMap;
  }

  @override
  Stream<dynamic>? readDataStream({required String connID}) {
    return eventChannel.receiveBroadcastStream(
      <String, dynamic>{
        'connID': connID,
      },
    );
  }

  @override
  Future<String?> closeAllConnection() async {
    return await methodChannel.invokeMethod<String?>(closeAllConnectionMethod);
  }

  @override
  Future<String?> closeBluetooth() async {
    return await methodChannel.invokeMethod<String?>(closeBluetoothMethod);
  }

  @override
  Future<String?> closeConnection({required String connId}) async {
    return await methodChannel
        .invokeMethod<String?>(closeConnectionMethod, <String, dynamic>{
      'connID': connId,
    });
  }

  @override
  Future<bool?> create485Conn({required String bT4Param, required log}) async {
    return await methodChannel
        .invokeMethod<bool?>(create485ConnMethod, <String, dynamic>{
      'connID': bT4Param,
      'log': log,
    });
  }

  @override
  Future<Map<String, dynamic>?> stopConnection({required String connID}) async {
    final res = await methodChannel.invokeMethod<Map<Object?, Object?>>(
        stopConnectionMethod, <String, dynamic>{
      'connID': connID,
    });
    Map<String, dynamic> newMap = {};
    res?.forEach((key, value) {
      newMap[key.toString()] = value;
    });
    return newMap;
  }

  @override
  Future<String?> getConnectedDevice() async {
    return await methodChannel.invokeMethod<String?>(connectedDeviceChannel);
  }

  @override
  Future<bool> isBluetoothOn() async {
    final res = await methodChannel.invokeMethod<Map<Object?, Object?>>(
      isBluetoothOnChannel,
    );
    if (res != null && res['isBluetoothOn'] != null) {
      return res['isBluetoothOn'] as bool;
    } else {
      return false;
    }
  }
}
