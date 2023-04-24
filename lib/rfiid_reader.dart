import 'rfiid_reader_platform_interface.dart';

class RFIFilterOption {
  static String userData = 'UserData';
  static String tid = 'TID';
  static String epc = 'EPC';
  static String all = 'ALL';
}

class RfiidReader {
  Future<String?> getPlatformVersion() {
    return RfiidReaderPlatform.instance.getPlatformVersion();
  }

  Future<String?> openBluetooth() =>
      RfiidReaderPlatform.instance.openBluetooth();

  Future<List<dynamic>?> getBT4DeviceStrList() =>
      RfiidReaderPlatform.instance.getBT4DeviceStrList();

  Future<bool?> createBT4Conn(
          {required String bT4Param, required dynamic log}) =>
      RfiidReaderPlatform.instance.createBT4Conn(bT4Param: bT4Param, log: log);

  Future<String?> getBluetoothDeviceSOC({required String connId}) =>
      RfiidReaderPlatform.instance.getBluetoothDeviceSOC(connId: connId);

  Future<String?> startBluetoothDeviceScan({required String connID}) =>
      RfiidReaderPlatform.instance.startBluetoothDeviceScan(connID: connID);

  Future<String?> setBeep({required String connID, required int onORoff}) =>
      RfiidReaderPlatform.instance.setBeep(connID: connID, onORoff: onORoff);

  Future<String?> startPingPong(
          {required String connID, required String filterBy}) =>
      RfiidReaderPlatform.instance.startPingPong(
        connID: connID,
        filterBy: filterBy,
      );

  Future<Map<String, dynamic>?> stopPingPong({required String connID}) =>
      RfiidReaderPlatform.instance.stopPingPong(connID: connID);

  Stream<dynamic>? readDataStream(String connID) =>
      RfiidReaderPlatform.instance.readDataStream(connID: connID);

  Future<String?> closeBluetooth() =>
      RfiidReaderPlatform.instance.closeBluetooth();
  Future<Map<String, dynamic>?> stopConnection({required String connID}) =>
      RfiidReaderPlatform.instance.stopConnection(connID: connID);

  Future<String?> closeConnection({required String connId}) =>
      RfiidReaderPlatform.instance.closeConnection(connId: connId);
  Future<String?> closeAllConnection() =>
      RfiidReaderPlatform.instance.closeAllConnection();
  Future<bool?> create485Conn(
          {required String bT4Param, required dynamic log}) =>
      RfiidReaderPlatform.instance.create485Conn(bT4Param: bT4Param, log: log);

  Future<String?> getConnectedDevice() =>
      RfiidReaderPlatform.instance.getConnectedDevice();

  Future<bool> isBluetoothOn() => RfiidReaderPlatform.instance.isBluetoothOn();
}
