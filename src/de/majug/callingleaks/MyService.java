package de.majug.callingleaks;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class MyService extends BroadcastReceiver {
	private Context context2 = null;
	private String number = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		context2 = context;
		Toast.makeText(context, "Create", Toast.LENGTH_SHORT).show();

		TelephonyManager phone = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		PhoneStateListener listener = new PhoneStateListener() {

			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				String name = null;
				if (state == TelephonyManager.CALL_STATE_RINGING) {

					try {
						Uri lookupUri = Uri
								.withAppendedPath(
										ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
										Uri.encode(incomingNumber));
						Cursor c = context2.getContentResolver().query(
								lookupUri,
								new String[] { PhoneLookup.DISPLAY_NAME },
								null, null, null);

						try {
							c.moveToFirst();
							try {
								name = c.getString(0);
							} catch (Exception e) {

							}
						} finally {
							c.close();
						}
					} catch (Exception e) {
						name = "Unknown";
					}

					TwitterConnector twitter = new TwitterConnector();
					SimpleDateFormat formatter = new SimpleDateFormat(
							"dd.MM.yyyy 'at' HH:mm");
					Date currentTime_1 = new Date();
					if (number == null) {

						LocationManager lm = (LocationManager) context2
								.getSystemService(Context.LOCATION_SERVICE);
						Criteria crit = new Criteria();
						crit.setAccuracy(Criteria.ACCURACY_FINE);
						String provider = lm.getBestProvider(crit, true);
						Location loc = lm
								.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						Double latitude = null;
						Double longitude = null;
						if (loc != null) {
							Log.d("CallingLeaks", "Setting Location");
							latitude = loc.getLatitude();
							longitude = loc.getLongitude();
						} else {
							Log.d("CallingLeaks", "No Location");
						}

						try {
							if (name != null && !name.equals("")) {
								twitter.updateStatus(
										"Currently called by @"
												+ name
												+ " "
												+ formatter
														.format(currentTime_1),
										latitude, longitude);
							} else {
								twitter.updateStatus(
										"Currently called by "
												+ incomingNumber
														.substring(
																0,
																incomingNumber
																		.length() - 3)
												+ "XXX "
												+ formatter
														.format(currentTime_1),
										latitude, longitude);
							}
						} catch (Exception e) {
							Log.e("CallingLeaks", "", e);
						}
					} else {
						number = incomingNumber;
					}
				} else if (state == TelephonyManager.CALL_STATE_IDLE) {
					number = null;
				}
			}

		};

		phone.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}
}
