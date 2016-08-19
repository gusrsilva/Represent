package gusrsilva.represent;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by GusSilva on 2/29/16.
 */
public class WatchListenerService extends WearableListenerService{
    private String TAG = "Represent!";
    private String PATH = "/JSON";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //Toast.makeText(getApplicationContext(), "Message Received!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Watch: Message Received");
        String JSONString = new String(messageEvent.getData());
        if(messageEvent.getPath().equalsIgnoreCase(PATH))
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(MainActivity.KEY_JSON, JSONString);
            startActivity(intent);
        }
        else
        {
            Log.d(TAG, "WatchListenerService: Failure! Path was: " + messageEvent.getPath());
        }
    }
}
