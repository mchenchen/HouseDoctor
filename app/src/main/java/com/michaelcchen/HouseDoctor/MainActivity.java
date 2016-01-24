package com.michaelcchen.HouseDoctor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.List;

public class MainActivity extends Activity {
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    IntentFilter intentFilter;

    // string containing raw text message string which will be manipulated
    String receivedMessage;
    TextView SMSes;
    //formatted text
    JSONObject jsonObject;
    String formattedText;
    // misc view elements
    private EditText editText;
    private ProgressBar spinner;
    private View mMessengerView;
    // Buttons used
    private Button searchButton;
    private Button searchAgainButton;
    private Button instructButton;
    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //�-display the SMS received in the TextView�-
            SMSes.setText("");
            spinner.setVisibility(View.GONE);
            mMessengerView.setVisibility(View.GONE);
            receivedMessage = intent.getExtras().getString("sms");
            try {
                jsonObject = new JSONObject(receivedMessage.substring(62));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                if(jsonObject != null) {
                    if (jsonObject.getInt("formatted") == 1) {
                        JSONArray jsons = jsonObject.getJSONArray("formatted_response");
                        for (int i = 0; i < jsons.length(); ++i) {
                            SMSes.append(Html.fromHtml("<b>" + jsons.getJSONObject(i).getString("heading") + "</b>\n" + "\n"));
                            SMSes.append(jsons.getJSONObject(i).getString("body") + "\n");
                        }
                    } else {
                        SMSes.setText(jsonObject.getString("response"));
                    }
                }
                else {
                    SMSes.setText(receivedMessage.substring(62));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //�-intent to filter for SMS messages received�-
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");

        //---register the receiver---
        registerReceiver(intentReceiver, intentFilter);

        //misc view elements initialization
        mMessengerView = findViewById(R.id.messenger);
        editText = (EditText) findViewById(R.id.queryEdit);
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        SMSes = (TextView) findViewById(R.id.results);

        // misc buttons
        searchButton = (Button) findViewById(R.id.searchButton);
        searchAgainButton = (Button) findViewById(R.id.searchAgainButton);
        instructButton = (Button) findViewById(R.id.instructButton);
    }

    @Override
    public void onResume() {
        super.onResume();


        //---create the BroadcastReceiver when the SMS is sent---
        //broadcasts listens for intents that state sent and delivered
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        //---create the BroadcastReceiver when the SMS is delivered---
        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        //---register the two BroadcastReceivers---
        registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));
        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
    }

    @Override
    public void onPause() {
        super.onPause();

        //---unregister the two BroadcastReceivers---
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //---unregister the receiver---
        unregisterReceiver(intentReceiver);
    }

    //menus to crossfade
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }


    public void onClick(View v) {
        if (v == searchButton) {
            String string = editText.getText().toString();
            if (string.isEmpty()) {
                SMSes.setText("ERROR - No search terms detected");
            } else {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                spinner.setVisibility(View.VISIBLE);
                mMessengerView.setVisibility(View.GONE);
                Log.d("debug", "should be visible");
                sendSMS("6475034751", string);
            }
        } else if (v == instructButton) {
            startActivity(new Intent(this, Instructions.class));
        } else {
            mMessengerView.setVisibility(View.VISIBLE);
        }
    }

    //�-sends an SMS message to another device�-
    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        /*
        arg1 - phone number
		arg2 - service center address
		arg3 - message
		arg4 - intent to confirm sent status
		arg5 - intent to confirm deliver status
		 */
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }
}