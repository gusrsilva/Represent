package gusrsilva.represent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by GusSilva on 2/29/16.
 */
public class PhoneListenerService extends WearableListenerService {

    //   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String REP_NUM = "/rep_num", ZIP_CODE = "/zip_code";
    private String TAG = "Represent!";

    @Override
    public void onMessageReceived(MessageEvent messageEvent)
    {
        Log.d(TAG, "in PhoneListenerService, got: " + messageEvent.getPath());
        //Toast.makeText(getApplicationContext(), "MessageReceived!", Toast.LENGTH_SHORT).show();
        if( messageEvent.getPath().equalsIgnoreCase(REP_NUM) ) {

            Log.d(TAG, "Received repNum, starting activity!");

            // Value contains the String we sent over in WatchToPhoneService, "good job"
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            int repNum = Integer.parseInt(value);
            Intent intent = new Intent(getApplicationContext(), ViewRepresentative.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(MainActivity.REP_NUM, repNum);
            startActivity(intent);

        }
        else if(messageEvent.getPath().equalsIgnoreCase(ZIP_CODE))
        {

            Log.d(TAG, "PhoneListenerService: Received ZipCode, updating activity!");

            // Value contains the String we sent over in WatchToPhoneService, "good job"
            String zip = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Log.d(TAG, "PhoneListenerService: ZipCode: " + zip);

            Intent intent = new Intent(getApplicationContext(), ChooseLocationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(MainActivity.ZIP_CODE, zip);
            startActivity(intent);
        }
        else
        {
            Log.d(TAG, " message did not match!");
            super.onMessageReceived( messageEvent );
        }

    }
}
