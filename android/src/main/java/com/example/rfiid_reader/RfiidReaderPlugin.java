package com.example.rfiid_reader;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.rfidread.Enumeration.eReadType;
import com.rfidread.Helper.Helper_ThreadPool;
import com.rfidread.Interface.IAsynchronousMessage;
import com.rfidread.Models.Tag_Model;
import com.rfidread.RFIDReader;
import com.rfidread.usbserial.driver.UsbSerialPort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.rfiid_reader.MyHashMap;
import com.example.rfiid_reader.PublicData;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * RfiidReaderPlugin
 */
public class RfiidReaderPlugin implements FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity

    /// method names

    public static String TAG_NAME = "RFID Reader Plugin";

    private static final String OpenBluetooth = "openBluetooth";
    private static final String GetBT4DeviceStrList = "getBT4DeviceStrList";
    private static final String CreateBT4Conn = "createBT4Conn";
    private static final String GetBluetoothDeviceSOC = "getBluetoothDeviceSOC";
    private static final String StartBluetoothDeviceScan = "startBluetoothDeviceScan";
    private static final String SetBeep = "setBeep";

    private static final String StopPingPong = "stopPingPong";

    private static final String StartPingPong = "startPingPong";

    private static final String StopConnectionMethod = "stopConnection";
    private static final String CloseBluetoothMethod = "closeBluetooth";
    private static final String CloseConnectionMethod = "closeConnection";
    private static final String CloseAllConnectionMethod = "closeAllConnection";
    private static final String Create485ConnMethod = "create485Conn";

    private static final String IsBluetoothOn = "is_bluetooth_on";

    private static final String ConnectedDevice = "connected_device";

    private RfidReaderHelper rfidReaderHelper;


    private MethodChannel channel;

    private EventChannel eventChannel;

    // Reader Antenna No.
    protected static int _UpDataTime = 0;

    private EventChannel.EventSink eventSink;

    private Handler handler = new Handler(Looper.getMainLooper());

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(
                flutterPluginBinding.getBinaryMessenger(),
                "rfiid_reader"
        );
        eventChannel = new EventChannel(
                flutterPluginBinding.getBinaryMessenger(),
                "rfiid_reader/readDataStream"
        );
        channel.setMethodCallHandler(this);
        eventChannel.setStreamHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case OpenBluetooth:
                openBluetooth(call, result);
            case CloseBluetoothMethod:
                closeBluetooth(call, result);
            case StopConnectionMethod:
                stopConnection(call, result);
            case CloseAllConnectionMethod:
                closeAllConnection(call, result);
            case CloseConnectionMethod:
                closeConnection(call, result);
            case Create485ConnMethod:
                create485Conn(call, result);
            case GetBT4DeviceStrList:
                getBT4DeviceStrList(call, result);
                break;
            case CreateBT4Conn:
                createBT4Conn(call, result);
                break;
            case GetBluetoothDeviceSOC:
                getBluetoothDeviceSOC(call, result);
            case StartBluetoothDeviceScan:
                startBluetoothDeviceScan(call, result);
            case SetBeep:
                setBeep(call, result);
                break;
            case StopPingPong:
                stopPingPong(call, result);
                break;
            case StartPingPong:
                startPingPong(call, result);
                break;
            case IsBluetoothOn:
                isBluetoothOn(call, result);
                break;
            case ConnectedDevice:
                getConnectedDevice(call, result);
            case "getPlatformVersion":
                result.success("Android " + android.os.Build.VERSION.RELEASE);
            default:
                result.notImplemented();
        }
    }

    private void getConnectedDevice(MethodCall call, Result result) {
        try{
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            String deviceId = null;
            for (BluetoothDevice device : pairedDevices) {
                // Get the device ID
                deviceId = device.getAddress();
                Log.d("Bluetooth", "Connected device ID: " + deviceId);
                break;
            }
            result.success(deviceId);
        }catch (Exception e){
            Log.e(TAG_NAME, "getConnectedDevice: "+e);
            result.success(null);
        }
    }

    private void isBluetoothOn(MethodCall call, Result result) {
        try {
            Map<String, Object> responseData = new HashMap<>();
            if (bluetoothAdapter == null) {
                responseData.put("isSupported", false);
                responseData.put("isBluetoothOn", false);
            } else {
                if (bluetoothAdapter.isEnabled()) {
                    responseData.put("isSupported", true);
                    responseData.put("isBluetoothOn", true);
                } else {
                    responseData.put("isSupported", true);
                    responseData.put("isBluetoothOn", false);
                }
            }
            result.success(responseData);
        }catch (Exception e){
            Log.e(TAG_NAME, "isBluetoothOn: "+e);
            result.success(null);
        }
    }

    private void openBluetooth(MethodCall call, Result result) {
       try{
           RFIDReader.OpenBluetooth();
           result.success("true");
       }catch (Exception e){
           Log.e(TAG_NAME, "openBluetooth: "+e);
           result.success("false");
       }
    }

    private void closeBluetooth(MethodCall call, Result result) {
       try{
           RFIDReader.CloseBluetooth();
           result.success("true");
       }catch (Exception e){
           Log.e(TAG_NAME, "closeBluetooth: "+e);
           result.success("false");
       }
    }


    private void closeAllConnection(MethodCall call, Result result) {
        try {
            RFIDReader.CloseAllConnect();
            result.success("true");
        } catch (Exception e) {
            Log.e(TAG_NAME, "closeAllConnection: "+e);
            result.success("false");
        }
    }

    private void closeConnection(MethodCall call, Result result) {
        try {
            Map<String, Object> arguments = call.arguments();
            String connId = (String) arguments.get("connId");
            RFIDReader.CloseConn(connId);
            result.success("true");
        } catch (Exception e) {
            Log.e(TAG_NAME, "closeConnection: "+e);
            result.success("false");
        }
    }


    private void getBT4DeviceStrList(MethodCall call, Result result) {
        try {
            final List<String> res = RFIDReader.GetBT4DeviceStrList();
            result.success(res);
        } catch (Exception e) {
            Log.e(TAG_NAME, "getBT4DeviceStrList: " + e);
            result.success(null);
        }
    }

    private void getBluetoothDeviceSOC(MethodCall call, Result result) {
        Map<String, Object> arguments = call.arguments();
        String connId = (String) arguments.get("connId");
        final String res = RFIDReader.GetBluetoothDeviceSOC(connId);
        result.success(res);
    }

    private void createBT4Conn(MethodCall call, Result result) {
        try {
            Map<String, Object> arguments = call.arguments();
            String bT4Param = (String) arguments.get("bT4Param");
            Object log = arguments.get("log");
            rfidReaderHelper = new RfidReaderHelper();
            final boolean successS = RFIDReader.CreateBT4Conn(
                    bT4Param, rfidReaderHelper
            );
            if (successS) {
                UHF_GetReaderProperty(bT4Param);
                UHF_SetTagUpdateParam(bT4Param);
            }
            result.success(successS);
        } catch (Exception e) {
            Log.e(TAG_NAME, "createBT4Conn: " + e);
            result.success(false);
        }
    }


    private void create485Conn(MethodCall call, Result result) {
        try {
            Map<String, Object> arguments = call.arguments();
            String bT4Param = (String) arguments.get("bT4Param");
            Object log = arguments.get("log");
            rfidReaderHelper = new RfidReaderHelper();
            final boolean successS = RFIDReader.Create485Conn(
                    bT4Param, rfidReaderHelper
            );
            if (successS) {
                UHF_GetReaderProperty(bT4Param);
                UHF_SetTagUpdateParam(bT4Param);
            }
            result.success(successS);
        } catch (Exception e) {
            Log.e(TAG_NAME, "create485Conn: " + e);
            result.success(false);
        }
    }

    @SuppressLint("UseSparseArrays")
    @SuppressWarnings("serial")

    private void UHF_GetReaderProperty(String ConnID) {
        String propertyStr = RFIDReader.GetReaderProperty(ConnID);
        //Log.d("Debug", "Get Reader Property:" + propertyStr);
        String[] propertyArr = propertyStr.split("\\|");
        if (propertyArr.length > 3) {
            try {
                int _Min_Power = Integer.parseInt(propertyArr[0]);
                int _Max_Power = Integer.parseInt(propertyArr[1]);
                int _ReaderAntennaCount = Integer.parseInt(propertyArr[2]);
                //_NowAntennaNo = hm_Ant_value.get(_ReaderAntennaCount);

                Log.i("MIN POWER", "UHF_GetReaderProperty: " + _Min_Power);
                Log.i("MAX POWER", "UHF_GetReaderProperty: " + _Max_Power);
                Log.i("ReaderAntennaCount", "_ReaderAntennaCount: " + _ReaderAntennaCount);

            } catch (Exception ex) {
                Log.e("Debug", "Get Reader Property failure and conversion failed!");
            }
        } else {
            Log.d("Debug", "Get Reader Property failure");
        }
    }

    /**
     * Set tag upload parameters
     */
    private void UHF_SetTagUpdateParam(String ConnID) {
        // Check if the current settings are the same, if not, then set it.
        String searchRT = "";
        try {
            searchRT = RFIDReader._Config.GetTagUpdateParam(ConnID);
            String[] arrRT = searchRT.split("\\|");
            if (arrRT.length >= 2) {
                int nowUpDataTime = Integer.parseInt(arrRT[0]);
                int rssiFilter = Integer.parseInt(arrRT[1]);
                Log.d("Debug", "Check the label to upload time:" + nowUpDataTime);
                if (_UpDataTime != nowUpDataTime) {
                    String param = "1," + _UpDataTime;
                    RFIDReader._Config.SetTagUpdateParam(ConnID, _UpDataTime, rssiFilter); // Set repeat tag upload time to 20ms
                    Log.d("Debug", "Sets the label upload time...");
                }
            } else {
                Log.d("Debug", "Query tags while uploading failure...");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void startBluetoothDeviceScan(MethodCall call, Result result) {
        try {
            Map<String, Object> arguments = call.arguments();
            String connId = (String) arguments.get("connID");
            final String successS = RFIDReader.StartBluetoothDeviceScan(connId);
            result.success(successS);
        } catch (Exception e) {
            Log.e(TAG_NAME, "startBluetoothDeviceScan: " + e);
            result.success(null);
        }
    }

    private void setBeep(MethodCall call, Result result) {
        Map<String, Object> arguments = call.arguments();
        String connId = (String) arguments.get("connID");
        int onORoff = (int) arguments.get("onORoff");
        final String successS = RFIDReader.SetBeep(connId, onORoff);
        result.success(successS);
    }

    private void stopPingPong(MethodCall call, Result result) {
        try {
            Map<String, Object> arguments = call.arguments();
            String connId = (String) arguments.get("connID");
            if (rfidReaderHelper != null) {
                final int successS = rfidReaderHelper.stopPingPong(connId);
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", successS);
                Map<String, Object> data = new HashMap<>();
                for (Map.Entry<String, Tag_Model> entry : rfidReaderHelper.hmList.entrySet()) {
                    data.put(entry.getKey(), entry.getValue().toString());
                }
                responseData.put("data", data);
                result.success(responseData);
            } else {
                result.success(null);
            }
        } catch (Exception e) {
            Log.e(TAG_NAME, "Failed to stop rfid - " + e);
            result.success(null);
        }
    }

    private void stopConnection(MethodCall call, Result result) {
        try {
            Map<String, Object> arguments = call.arguments();
            String connId = (String) arguments.get("connID");
            final String successS = RFIDReader.Stop(connId);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("data", successS);
            result.success(responseData);
        } catch (Exception e) {
            Log.e(TAG_NAME, "Connection exception - " + e);
            result.success(null);
        }
    }

    private void startPingPong(MethodCall call, Result result) {
        try {
            Map<String, Object> arguments = call.arguments();
            String connId = (String) arguments.get("connID");
            String filterBy = (String) arguments.get("filterBy");
            if (rfidReaderHelper != null) {
                rfidReaderHelper.readPingPong(connId, filterBy);
                result.success("true");
            } else {
                result.success("false");
            }
        } catch (Exception e) {
            Log.e(TAG_NAME, "Failed to start - " + e);
            result.success("false");
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        eventSink = events;
        if (rfidReaderHelper != null) {
            try {
                rfidReaderHelper.hmList.addListener(hashMap -> {
                    Map<String, Object> data = new HashMap<>();
                    for (Map.Entry<String, Tag_Model> entry : hashMap.entrySet()) {
                        data.put(entry.getKey(), entry.getValue().toString());
                    }
                    System.out.println("data on listen - " + data);

                    handler.post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    eventSink.success(data);
                                }
                            });

                });
            } catch (Exception e) {
                Log.e(TAG_NAME, "Exception on listen rfid changes");
            }
        }
    }

    @Override
    public void onCancel(Object arguments) {
        if (eventSink != null) {
            eventChannel = null;
        }
    }

}





