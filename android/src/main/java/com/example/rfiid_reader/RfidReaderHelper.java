package com.example.rfiid_reader;
import android.util.Log;
import com.rfidread.Enumeration.eReadType;
import com.rfidread.Helper.Helper_ThreadPool;
import com.rfidread.Interface.IAsynchronousMessage;
import com.rfidread.Models.Tag_Model;
import com.rfidread.RFIDReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class RfidReaderHelper implements IAsynchronousMessage {


    protected static int _NowAntennaNo = 1;

    private int totalReadCount = 0; //
    private static int _ReadType = 0;

    public MyHashMap<String, Tag_Model> hmList = new MyHashMap<String, Tag_Model>();

    private Object hmList_Lock = new Object();

    @Override
    public void WriteDebugMsg(String s) {
        myLogger("WriteDebugMsg -" + s);
    }

    @Override
    public void WriteLog(String s) {
        myLogger("write log -" + s);
    }

    @Override
    public void PortConnecting(String s) {
        myLogger("PortConnecting -" + s);
    }

    @Override
    public void PortClosing(String s) {
        myLogger("PortClosing -" + s);
    }

    @Override
    public void OutPutTags(Tag_Model tag_model) {
        try {
            synchronized (hmList_Lock) {
                if (hmList.containsKey(tag_model._EPC + tag_model._TID)) {
                    Tag_Model tModel = hmList.get(tag_model._EPC + tag_model._TID);
                    assert tModel != null;
                    tModel._TotalCount++;
                    tag_model._TotalCount = tModel._TotalCount;
                    hmList.remove(tag_model._EPC + tag_model._TID);
                    hmList.put(tag_model._EPC + tag_model._TID, tag_model);
                } else {
                    tag_model._TotalCount = 1;
                    hmList.put(tag_model._EPC + tag_model._TID, tag_model);
                }
            }
            totalReadCount++;
            Log.i("Total read count", "OutPutTags: " + totalReadCount);
            Log.i("Read values - ", "OutPutTags: " + Arrays.toString(hmList.entrySet().toArray()));
        } catch (Exception ex) {
            Log.d("Debug", "Tags output exceptions:" + ex.getMessage());
        }
        myLogger("OutPutTags EPC -" + tag_model._EPC);
    }

    @SuppressWarnings({"rawtypes", "unused"})
    protected List<Map<String, Object>> GetData() {
        List<Map<String, Object>> rt = new ArrayList<Map<String, Object>>();
        synchronized (hmList_Lock) {
            // if(hmList.size() > 0){ //
            Iterator iter = hmList.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();
                Tag_Model val = (Tag_Model) entry.getValue();
                Map<String, Object> map = new HashMap<String, Object>();
                if (_ReadType == 0) {
                    map.put("EPC", val._EPC);
                    map.put("ReadCount", val._TotalCount);
                    rt.add(map);
                } else if (_ReadType == 1) {
                    if (!val._TID.equals("")) {
                        map.put("EPC", val._TID);
                        map.put("ReadCount", val._TotalCount);
                        rt.add(map);
                    }
                } else {
                    if (!val._UserData.equals("")) {
                        map.put("EPC", val._UserData);
                        map.put("ReadCount", val._TotalCount);
                        rt.add(map);
                    }
                }
            }
            // }
        }
        return rt;
    }

    @Override
    public void OutPutTagsOver() {
        myLogger("Output tags over");
    }

    @Override
    public void GPIControlMsg(int i, int i1, int i2) {
        myLogger("GPIControlMsg - " + i + "arg1 - " + i1 + "arg2 - " + i2);
    }

    @Override
    public void OutPutScanData(byte[] bytes) {
        myLogger("OutPutScanData -" + Arrays.toString(bytes));
    }


    // intermittent reading
    public void readPingPong(String connId, String filterValue) {
        Helper_ThreadPool.ThreadPool_StartSingle(new Runnable() {
            @Override
            public void run() {
                try {
                    String rt = "";
                    if (PublicData._IsCommand6Cor6B.equals("6C")) {// read 6c tags
                        GetEPC_6C(connId, filterValue);
                    } else {// read 6b tags
                        GetEPC_6B(connId, filterValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //6C,Read tag
    private int GetEPC_6C(String ConnID, String filterValue) {
        int ret = -1;

        if (filterValue.equals("ALL")) {
            ret = RFIDReader._Tag6C.GetEPC(ConnID, _NowAntennaNo, eReadType.Inventory);
        } else if (filterValue.equals("EPC")) {
            ret = RFIDReader._Tag6C.GetEPC(ConnID, _NowAntennaNo, eReadType.Inventory);
        } else if (filterValue.equals("TID")) {
            ret = RFIDReader._Tag6C.GetEPC_TID(ConnID, _NowAntennaNo, eReadType.Inventory);
        } else if (filterValue.equals("UserData")) {
            ret = RFIDReader._Tag6C.GetEPC_TID_UserData(ConnID, _NowAntennaNo, eReadType.Inventory, 0, 6);
        }
        return ret;
    }

    //6C,Read tag
    private int GetEPC_6B(String ConnID, String filterValue) {
        int ret = -1;
        if (filterValue.equals("ALL")) {
            ret = RFIDReader._Tag6B.Get6B(ConnID, _NowAntennaNo, eReadType.Inventory.GetNum(), 0);
        } else if (filterValue.equals("EPC")) {
            ret = RFIDReader._Tag6B.Get6B(ConnID, _NowAntennaNo, eReadType.Inventory.GetNum(), 0);
        } else if (filterValue.equals("TID")) {
            ret = RFIDReader._Tag6B.Get6B(ConnID, _NowAntennaNo, eReadType.Inventory.GetNum(), 0);
        } else if (filterValue.equals("UserData")) {
            ret = RFIDReader._Tag6B.Get6B_UserData(ConnID, _NowAntennaNo, eReadType.Inventory.GetNum(), 1, 0, 15);
        }
        return ret;
    }

    // stop reading
    public int stopPingPong(String ConnID) {
        try {
            return RFIDReader._Config.Stop(ConnID);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public void Clear() {
        totalReadCount = 0;
        hmList.clear();
    }

    void myLogger(String msg) {
        System.out.println("RFID Logger - " + msg);
    }

}

