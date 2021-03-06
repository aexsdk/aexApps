package com.androidex.apps.aexSmartBiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidex.devices.appDeviceDriver;

/**
 *
 */

public class SmartBizMainActivity extends AppCompatActivity {

    public static final String TAG = "SmartBiz";
    private Button btn_test_printer;
    private Button btn_bank_reader;
    private Button btn_cas_reader;
    private Button btn_test_password;
    private appSmartBizDevices mDevices;
    private SmartBizMainActivity mActivity;
    private Button btn_exit;
    private TextView tv_sdk_version;
    private TextView tv_uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_biz_main);

        mActivity = this;
        mDevices = new appSmartBizDevices(this);

        btn_test_printer = (Button)findViewById(R.id.btn_test_printer);
        btn_bank_reader = (Button)findViewById(R.id.btn_reader_card);
        btn_cas_reader = (Button)findViewById(R.id.btn_cas_reader);
        btn_test_password = (Button)findViewById(R.id.btn_test_password);
        btn_exit = (Button)findViewById(R.id.btn_exit);

        tv_sdk_version = (TextView)findViewById(R.id.tv_sdk_version);
        tv_uuid = (TextView)findViewById(R.id.tv_uuid);

        tv_sdk_version.setText(mDevices.mHwservice.getSdkVersion());
        tv_uuid.setText(mDevices.mHwservice.get_uuid());
        btn_test_printer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if(mDevices.mPrinter.Open()){
                    mDevices.mPrinter.selfTest();
                    mDevices.mPrinter.Close();
                }else{
                    Toast.makeText(mActivity,String.format("Open printer fial:%s",mDevices.mPrinter.mParams.optString(appDeviceDriver.PORT_ADDRESS)),Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_bank_reader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if(mDevices.mBankCardReader.Open()) {
                    mDevices.mBankCardReader.reset();
                    mDevices.mBankCardReader.queryCard();
                    mDevices.mBankCardReader.popCard();
                    mDevices.mBankCardReader.Close();
                }else{
                    Toast.makeText(mActivity,String.format("Open bank reader fial:%s",mDevices.mBankCardReader.mParams.optString(appDeviceDriver.PORT_ADDRESS)),Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_cas_reader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if(mDevices.mCasCardReader.Open()) {
                    mDevices.mCasCardReader.reset();
                    mDevices.mCasCardReader.queryCard();
                    mDevices.mCasCardReader.popCard();
                    mDevices.mCasCardReader.Close();
                }else{
                    Toast.makeText(mActivity,String.format("Open cas reader fial:%s",mDevices.mCasCardReader.mParams.optString(appDeviceDriver.PORT_ADDRESS)),Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_test_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if(mDevices.mPasswordKeypad.Open()) {
                    mDevices.mPasswordKeypad.pkReset();
                    String pkVersion = mDevices.mPasswordKeypad.pkGetVersion();
                    Log.d(TAG, pkVersion);
                    Toast.makeText(mActivity, pkVersion, Toast.LENGTH_LONG).show();
                    mDevices.mPasswordKeypad.Close();
                }else{
                    Toast.makeText(mActivity,String.format("Open password keypad fial:%s",mDevices.mPasswordKeypad.mParams.optString(appDeviceDriver.PORT_ADDRESS)),Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                System.exit(0);
            }
        });
    }
}
