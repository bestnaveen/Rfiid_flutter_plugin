// ignore_for_file: use_build_context_synchronously

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:rfiid_reader/permission_manager.dart';
import 'package:rfiid_reader/rfiid_reader.dart';
import 'package:rfiid_reader/rfiid_reader_platform_interface.dart';

void main() {
  runApp(const MaterialApp(home: MyApp()));
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _rfiidReaderPlugin = RfiidReader();
  List<dynamic> _device = [];

  String? currentConnection;
  String? batteryLevel;

  bool _working = false;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  void setListener() {
    if (currentConnection != null) {
      _rfiidReaderPlugin.readDataStream(currentConnection!)?.listen((event) {
        print("listen event - $event");
      });
    }
  }

  Future<void> channelTest() async {
    if (await BluetoothPermissionManager.checkAndRequestPermissions()) {
      final isBluetoothOpen = await _rfiidReaderPlugin.isBluetoothOn();
      print("is open blue - $isBluetoothOpen");
      if (!isBluetoothOpen) {
        await _rfiidReaderPlugin.openBluetooth();
      }
      final getConnectedDevice = await _rfiidReaderPlugin.getConnectedDevice();
      print("connected device - $getConnectedDevice");
      final res = await RfiidReaderPlatform.instance.getBT4DeviceStrList();
      print("devices - $res");
      if (res != null) {
        setState(() {
          _device = res;
        });
      } else {
        _device = [];
      }
    }
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      _working = true;
      platformVersion = await _rfiidReaderPlugin.getPlatformVersion() ??
          'Unknown platform version';

      await channelTest();
      _working = false;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Plugin example app'),
      ),
      body: _working
          ? const Center(
              child: CircularProgressIndicator(),
            )
          : Padding(
              padding: const EdgeInsets.symmetric(
                horizontal: 20,
                vertical: 20,
              ),
              child: Column(
                children: [
                  Padding(
                    padding: const EdgeInsets.only(
                      bottom: 20,
                    ),
                    child:
                        Text('Running on device version : $_platformVersion\n'),
                  ),
                  const Padding(
                    padding: EdgeInsets.only(
                      bottom: 20,
                    ),
                    child: Text('Available devices'),
                  ),
                  Padding(
                    padding: const EdgeInsets.only(
                      bottom: 20,
                    ),
                    child: Column(
                      children: _device
                          .map((e) => ListTile(
                                onTap: () async {
                                  final res =
                                      await _rfiidReaderPlugin.createBT4Conn(
                                    bT4Param: e,
                                    log: null,
                                  );

                                  print("connected - $res");
                                  if (res ?? false) {
                                    setState(() {
                                      currentConnection = e;
                                    });

                                    final charging = await _rfiidReaderPlugin
                                        .getBluetoothDeviceSOC(connId: e);
                                    if (charging != null) {
                                      batteryLevel = charging;
                                      setState(() {
                                        batteryLevel = charging;
                                      });
                                    }
                                  } else {
                                    ScaffoldMessenger.of(context).showSnackBar(
                                      const SnackBar(
                                        content: Text(
                                            "Failed to connect device, please make sure your device is ready connect"),
                                      ),
                                    );
                                  }
                                },
                                title: Text(
                                  e.toString(),
                                  style: Theme.of(context)
                                      .textTheme
                                      .displaySmall
                                      ?.copyWith(
                                        color: Colors.lightBlue,
                                        fontSize: 16,
                                      ),
                                ),
                              ))
                          .toList(),
                    ),
                  ),
                  Visibility(
                    visible: currentConnection != null,
                    child: Padding(
                      padding: const EdgeInsets.only(
                        bottom: 20,
                      ),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text(
                            'Connected device - $currentConnection',
                            style: Theme.of(context)
                                .textTheme
                                .bodyMedium
                                ?.copyWith(
                                  color: Colors.red,
                                ),
                          ),
                          TextButton(
                            onPressed: () async {
                              final success =
                                  await _rfiidReaderPlugin.closeAllConnection();
                              if (success == "true") {
                                setState(() {
                                  currentConnection = null;
                                });
                              }
                            },
                            child: const Text("Disconnect"),
                          )
                        ],
                      ),
                    ),
                  ),
                  Visibility(
                    visible: batteryLevel != null,
                    child: Padding(
                      padding: const EdgeInsets.only(
                        bottom: 20,
                      ),
                      child: Text(
                        'Remaining charging - $batteryLevel',
                        style: Theme.of(context)
                            .textTheme
                            .bodyMedium
                            ?.copyWith(color: Colors.red),
                      ),
                    ),
                  ),
                  if (currentConnection != null)
                    Padding(
                      padding: const EdgeInsets.symmetric(
                        vertical: 20,
                      ),
                      child: Row(
                        children: [
                          TextButton(
                            onPressed: () {
                              _rfiidReaderPlugin.startPingPong(
                                connID: currentConnection!,
                                filterBy: "EPC",
                              );
                            },
                            child: const Text("Start"),
                          ),
                          TextButton(
                            onPressed: () async {
                              final result =
                                  await _rfiidReaderPlugin.stopPingPong(
                                connID: currentConnection!,
                              );
                              print("read result - $result");
                            },
                            child: const Text("Stop"),
                          ),
                        ],
                      ),
                    ),
                  currentConnection != null
                      ? StreamBuilder(
                          stream: _rfiidReaderPlugin
                              .readDataStream(currentConnection!),
                          builder: (_, snapshot) {
                            if (snapshot.connectionState ==
                                ConnectionState.waiting) {
                              return const Text('waiting');
                            } else {
                              return Text(snapshot.data.toString() ?? '');
                            }
                          },
                        )
                      : const SizedBox()
                ],
              ),
            ),
    );
  }
}
