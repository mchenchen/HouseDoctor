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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SMSActivity extends Activity {
	String SENT = "SMS_SENT";
	String DELIVERED = "SMS_DELIVERED";
	PendingIntent sentPI, deliveredPI;
	BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    IntentFilter intentFilter;

	//cross fade animation
	/**
	 * The flag indicating whether content is loaded (text is shown) or not (loading spinner is
	 * shown).
	 */
	private boolean mContentLoaded;

	/**
	 * The view (or view group) containing the content. This is one of two overlapping views.
	 */
	private View mHowToView;

	/**
	 * The view containing the loading indicator. This is the other of two overlapping views.
	 */
	private View mMessengerView;

	/**
	 * The system "short" animation time duration, in milliseconds. This duration is ideal for
	 * subtle animations or animations that occur very frequently.
	 */
	private int mShortAnimationDuration;
    
    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //�-display the SMS received in the TextView�-
            TextView SMSes = (TextView) findViewById(R.id.results);
            SMSes.setText(intent.getExtras().getString("sms"));
        }
    };
	
	/** Called when the activity is first created. */
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

		//cross fade declarations
		mHowToView = findViewById(R.id.howto);
		mMessengerView = findViewById(R.id.messenger);

		// Retrieve and cache the system's default "short" animation time.
		mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
	}

	@Override
	public void onResume() {
		super.onResume();

        //---register the receiver---
        //registerReceiver(intentReceiver, intentFilter);
		
		//---create the BroadcastReceiver when the SMS is sent---
		//broadcasts listens for intents that state sent and delivered
		smsSentReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode())
				{
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
		smsDeliveredReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode())
				{
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
		
        //---unregister the receiver---
        //unregisterReceiver(intentReceiver);

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
		getMenuInflater().inflate(R.menu.smsactivity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

			case R.id.action_toggle:
				// Toggle whether content is loaded.
				mContentLoaded = !mContentLoaded;
				showContentOrLoadingIndicator(mContentLoaded);
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showContentOrLoadingIndicator(boolean contentLoaded) {
		// Decide which view to hide and which to show.
		final View showView = contentLoaded ? mHowToView : mMessengerView;
		final View hideView = contentLoaded ? mMessengerView : mHowToView;

		// Set the "show" view to 0% opacity but visible, so that it is visible
		// (but fully transparent) during the animation.
		showView.setAlpha(0f);
		showView.setVisibility(View.VISIBLE);

		// Animate the "show" view to 100% opacity, and clear any animation listener set on
		// the view. Remember that listeners are not limited to the specific animation
		// describes in the chained method calls. Listeners are set on the
		// ViewPropertyAnimator object for the view, which persists across several
		// animations.
		showView.animate()
				.alpha(1f)
				.setDuration(mShortAnimationDuration)
				.setListener(null);

		// Animate the "hide" view to 0% opacity. After the animation ends, set its visibility
		// to GONE as an optimization step (it won't participate in layout passes, etc.)
		hideView.animate()
				.alpha(0f)
				.setDuration(mShortAnimationDuration)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						hideView.setVisibility(View.GONE);
					}
				});
	}
	public void onClick(View v) {
		sendSMS("6475034157", "Hello my friends!");
	}

	//�-sends an SMS message to another device�-
	private void sendSMS(String phoneNumber, String message)
	{
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