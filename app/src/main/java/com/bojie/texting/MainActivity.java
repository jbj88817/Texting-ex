package com.bojie.texting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.txtMsgEditText)
    EditText et_txtMsg;
    @InjectView(R.id.pNumEditText)
    EditText et_pNum;
    @InjectView(R.id.messagesEditText)
    EditText et_messages;
    @InjectView(R.id.sendButton)
    Button btn_send;

    static String messages = "";

    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                et_messages.setText(messages);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void sendMessage(View view) {

        String phoneNum = et_pNum.getText().toString();
        String message = et_txtMsg.getText().toString();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNum, null, message,
                    null, null);
            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException ex) {
            Toast.makeText(this, "Enter a Phone Number and Message", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();

        }

        messages = messages + "You: " + message + "\n";

    }

    public static class SmsReceier extends BroadcastReceiver {

        final SmsManager smsManager = SmsManager.getDefault();

        public SmsReceier(){}

        @Override
        public void onReceive(Context context, Intent intent) {

            final Bundle bundle = intent.getExtras();

            try {
                if(bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage smsMessage =
                                SmsMessage.createFromPdu((byte[]) pdusObj[i]);

                        String phoneNumber = smsMessage.getDisplayOriginatingAddress();
                        String message = smsMessage.getDisplayMessageBody();

                        messages = messages + phoneNumber + " : " + message + "\n";

                    }
                }
            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver");
            }
        }
    }

    public class MMSReceiver extends BroadcastReceiver {

        public MMSReceiver(){}
        @Override
        public void onReceive(Context context, Intent intent) {
            throw new UnsupportedOperationException("Not Implemented Yet");
        }
    }

    public class HeadlessSmsSendService extends BroadcastReceiver {

        public HeadlessSmsSendService(){}
        @Override
        public void onReceive(Context context, Intent intent) {
            throw new UnsupportedOperationException("Not Implemented Yet");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
