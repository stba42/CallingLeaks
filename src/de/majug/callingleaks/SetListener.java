package de.majug.callingleaks;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class SetListener extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TelephonyManager phone = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        
        
        PhoneStateListener listener = new PhoneStateListener(){

			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				Toast.makeText(SetListener.this, "Hallo", Toast.LENGTH_SHORT).show(); 		 
			}
        	
        };
        
        phone.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        
        
    }
}