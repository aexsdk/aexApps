package com.androidex.devices;

import android.content.Context;

import com.androidex.apps.aexdeviceslib.R;
import com.androidex.hwuitls.Base16;
import com.androidex.logger.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yangjun on 2016/10/24.
 */

public class aexddKMY350 extends aexddPasswordKeypad {

    public static final String TAG = "kmy350";

    public aexddKMY350(Context ctx) {
        super(ctx);
    }

    public aexddKMY350(Context ctx, JSONObject args) {
        super(ctx, args);
    }

    @Override
    public String getDeviceName() {
        return mContext.getString(R.string.DEVICE_PK_KMY350);
    }

    /**
     * WebJavaBridge.OnJavaBridgePlugin接口的函数，当Web控件通过js调用插件时会调用此函数。
     * @param action        js调用java的动作
     * @param args          js调用java的参数
     * @param callbackId    js调用java完成后返回结果的回调函数
     * @return              返回结果，它会作为回调函数的参数使用
     */
    @Override
    public JSONObject onExecute(String action, JSONObject args, String callbackId) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("success", false);
            try {

                if (action.equals("pkReset")) {
                    pkReset();
                }else{
                    obj = super.onExecute(action,args,callbackId);
                }

            } catch (Exception e) {
                obj.put("success", false);
                obj.put("message", e.getLocalizedMessage());
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return obj;
    }

    /**
     *
     * @return
     */
    @Override
    public int ReciveDataLoop() {
        Runnable run=new Runnable() {
            public void run() {
                //在线程中执行jni函数
                //OnBackCall.ONBACKCALL_RECIVEDATA
            }
        };
        pthread = new Thread(run);
        pthread.start();
        return 0;
    }

    @Override
    public String getStatusStr(int st)
    {
        switch(st){
            case 0x15:
                return "命令参数错";
            case 0x80:
                return "超时错误";
            case 0xA4:
                return "命令可成功执行,但主密钥无效";
            case 0xB5:
                return "命令无效,且主密钥无效";
            case 0xC4:
                return "命令可成功执行,但电池可能损坏";
            case 0xD5:
                return "命令无效,且电池可能损坏";
            case 0xE0:
                return "无效命令";
            default:
                if((st&0xF0) == 0xF0){
                    switch(st&0x0F){
                        case 0:
                            return "自检错误，CPU错";
                        case 1:
                            return "自检错误，SRAM错";
                        case 2:
                            return "自检错误，键盘有短路错";
                        case 3:
                            return "自检错误，串口电平错";
                        case 4:
                            return "自检错误，CPU卡出错";
                        case 5:
                            return "自检错误，电池可能损坏";
                        case 6:
                            return "自检错误，主密钥失效";
                        case 7:
                            return "自检错误，杂项错";
                        default :
                            return "自检错误，未知类型";
                    }
                }else{
                    return "未知错误代码";
                }
        }
    }

    @Override
    public boolean pkReset()
    {
        boolean ret = false;
        String rhex = "";
        //
        WriteDataHex("0131");
        byte[] r = ReciveData(255,3000*delayUint);
        rhex = new String(r);
        Log.d(TAG,String.format("pkReset:%s",rhex));
        ret = rhex.contains("303500");
        return ret;
    }

    @Override
    public String pkGetVersion()
    {
        String ret = "";
        String rhex = "";
        //
        WriteDataHex("0130");
        byte[] r = ReciveData(255,3000*delayUint);
        rhex = new String(r);
        try {
            r = Base16.decode(rhex.substring(5,5+32));
            ret = new String(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
