package com.example.phonesmsintegration;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class CodeReceiver extends AppCompatActivity implements View.OnClickListener {

    EditText txtCode;
    TextView txtPhoneNumber;
    TextView btnResendCode;
    Button btnNext;

    //code that will be sent to user using text message
    String randomCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_receiver);

        //code that will be sent to user using text message
        randomCode = getIntent().getStringExtra("randomCode");

        //intializing views
        txtCode = findViewById(R.id.txtCode);
        btnResendCode = findViewById(R.id.btnResendCode);
        btnNext = findViewById(R.id.btnNext);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);


        txtPhoneNumber.setText(getIntent().getStringExtra("phoneNumber"));


        //register the receiver
        registerReceiver(receiver, new IntentFilter("SampleBroadCastName"));



        //onclick events
        btnResendCode.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    //receive the intent gave by the IncomingSMSReceiver
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            txtCode.setText(message);

        }
    };


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnResendCode){
            if (btnResendCode.getText().equals("Resend Verification Code")){
                new CountDownTimer(30000, 1000) {
                    @Override
                    public void onTick(long l) {
                        btnResendCode.setText("Remaining seconds: "+l/1000);
                    }

                    @Override
                    public void onFinish() {
                        btnResendCode.setText("Resend Verification Code");
                    }
                }.start();

                //create a random number again
                Random rand = new Random();
                int resendRandomNumber = rand.nextInt(999999 - 100000);

                //will send the code again
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(getIntent().getStringExtra("phoneNumber"), null, "Your code is "+ resendRandomNumber, null, null);
                randomCode = String.valueOf(resendRandomNumber);
                Log.d("CodeReceiver", "randomNumber: " + resendRandomNumber);

            }else{
                Toast.makeText(CodeReceiver.this, "Wait for the time to finish", Toast.LENGTH_SHORT).show();
            }

        }else if (id == R.id.btnNext){
            if (txtCode.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Enter Verification code", Toast.LENGTH_SHORT).show();
            }else{

                if (txtCode.getText().toString().equals(randomCode)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Success");
                    builder.setMessage("Your number is now verified");
                    builder.setPositiveButton("Okay", null);
                    Dialog dialog = builder.create();
                    dialog.show();
                }else{
                    Toast.makeText(this, "Incorrect Code", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
