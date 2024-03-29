import 'package:permission_handler/permission_handler.dart';

class BluetoothPermissionManager {
  static Future<bool> checkAndRequestPermissions() async {
    bool isScanBluetoothGranted =
        await Permission.bluetoothScan.status.isGranted;
    //bool isBluetoothGranted = await Permission.bluetooth.status.isGranted;
    bool isConnectBluetoothGranted =
        await Permission.bluetoothConnect.status.isGranted;
    bool isAdvertiseBluetoothGranted =
        await Permission.bluetoothAdvertise.status.isGranted;
    if (
        !isScanBluetoothGranted ||
        !isConnectBluetoothGranted ||
        !isAdvertiseBluetoothGranted) {
      Map<Permission, PermissionStatus> statuses = await [
        //Permission.bluetooth,
        Permission.bluetoothAdvertise,
        Permission.bluetoothScan,
        Permission.bluetoothConnect
      ].request();
      isScanBluetoothGranted =
          statuses[Permission.bluetoothScan] == PermissionStatus.granted;
      isConnectBluetoothGranted =
          statuses[Permission.bluetoothConnect] == PermissionStatus.granted;
      isAdvertiseBluetoothGranted =
          statuses[Permission.bluetoothAdvertise] == PermissionStatus.granted;
    }
    return 
        isScanBluetoothGranted &&
        isConnectBluetoothGranted &&
        isAdvertiseBluetoothGranted;
  }
}
