package com.example.phonesmsintegration;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION = 0;
    EditText txtPrefix, txtPhoneNumber;
    Button btnSendCode;
    private static final String TAG = "MainActivity";
    private boolean checkIfTxtPhoneNumberCanType = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //will check if permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_NUMBERS) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.SEND_SMS}, PERMISSION);

            }
        }

        //initialize views
        txtPrefix = findViewById(R.id.txtPrefix);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        btnSendCode = findViewById(R.id.btnSendCode);

        //will focus the typing to phone number
        txtPhoneNumber.requestFocus();

        //on click events
        txtPhoneNumber.setOnClickListener(this);
        btnSendCode.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnSendCode) {
            String phoneNumber = txtPrefix.getText().toString() + "" + txtPhoneNumber.getText().toString();

            //will give a random code for OTC
            Random rand = new Random();
            int randomCode = rand.nextInt(999999 - 100000);
            Log.d(TAG, "randomNumber: " + randomCode);

            //will text the verification code
           /* SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, "Your Code is "+ randomCode, null, null);*/

           if (txtPrefix.getText().toString().trim().length() != 0 && txtPhoneNumber.getText().toString().trim().length() != 0 ){
               //will send all to Codereceiver activity
               Intent intent = new Intent(MainActivity.this, CodeReceiver.class);
               intent.putExtra("phoneNumber", phoneNumber);
               //intent.putExtra("randomCode", String.valueOf(randomCode));
               startActivity(intent);
           }else{
               Toast.makeText(this, "Provide a phone a phone number", Toast.LENGTH_SHORT).show();
               txtPhoneNumber.requestFocus();
           }

        } else if (id == R.id.txtPhoneNumber) {

            if (checkIfTxtPhoneNumberCanType){
                checkIfTxtPhoneNumberCanType = false;
            }else {
                if (txtPhoneNumber.getText().toString().trim().length() == 0) {
                    TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

                    final Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.dialog_select_phone_number);

                    //views
                    final TextView phoneNumber1 = dialog.findViewById(R.id.phoneNumber1);
                    final TextView anotherNumber = dialog.findViewById(R.id.anotherNumber);

                    //if the permission is not granted. it will request new permission
                    if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_PHONE_NUMBERS}, PERMISSION);

                        return;
                    }

                    //get the 1st sim card number
                    phoneNumber1.setText(telephonyManager.getLine1Number());

                    //place the selected phone number to the txtPhoneNumber and remove the 0 number in the 11 digit number
                    phoneNumber1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String number[] = phoneNumber1.getText().toString().split("");
                            String finalNumber = "";

                            //if phone number starts with 09
                            if (phoneNumber1.getText().toString().trim().length() == 11) {
                                for (int i = 2; i < number.length; i++) {
                                    finalNumber = finalNumber + "" + number[i];
                                }
                            }
                            //if phone number starts with +63
                            else{
                                for (int i = 4; i < number.length; i ++){
                                    finalNumber = finalNumber + "" + number[i];
                                }
                            }

                            txtPhoneNumber.setText(finalNumber);
                            dialog.hide();
                        }
                    });


                    //when user choose the type another number
                    anotherNumber.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.hide();
                            txtPhoneNumber.requestFocus();

                            //if the user tap on type other number, the user can type the number
                            checkIfTxtPhoneNumberCanType = true;
                        }
                    });
                    dialog.show();
                } else {

                }
            }

        }
    }
}
