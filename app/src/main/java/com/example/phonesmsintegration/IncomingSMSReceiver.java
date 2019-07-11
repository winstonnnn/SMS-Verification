package com.example.phonesmsintegration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class IncomingSMSReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    String msg = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == SMS_RECEIVED){

            try {
                Object[] myPdu = (Object[]) intent.getExtras().get("pdus");
                final SmsMessage[] message = new SmsMessage[myPdu.length];
                Log.d("sample", "onReceive1: "+ myPdu.length);

                for (int i = 0; i < myPdu.length; i++) {
                    message[i] = SmsMessage.createFromPdu((byte[]) myPdu[i]);
                    msg = message[i].getMessageBody();
                }

            }catch (NullPointerException e){
                Log.d("IncomingSMSReceiver", "onReceive: "+ e.getMessage());
            }

            String finalCode = msg.replaceAll("[^0-9]", "");


           Intent i = new Intent("SampleBroadCastName");
           i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           i.putExtra("message", finalCode);
           context.sendBroadcast(i);

       }

    }

}
