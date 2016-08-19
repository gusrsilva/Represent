package gusrsilva.represent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements GoogleApiClient.OnConnectionFailedListener
        , GoogleApiClient.ConnectionCallbacks, ShakeEventListener.ShakeListener {


    public static String TAG = "Represent!", jsonString;
    private static GoogleApiClient mWatchApiClient;
    private List<Node> nodes = new ArrayList<>();
    private static int repNumber = -1;
    private ShakeEventListener shaker;
    private CardAdapter adapter;
    public static String KEY_JSON = "json_info";
    private final String PATH_REP_NUM = "/rep_num", PATH_ZIP_CODE = "/zip_code";
    private static final int ACTION_SEND_REP_NUM = 0, ACTION_SEND_RANDOM_ZIP = 1;
    private static int watchAction = -1;
    private long lastShake = System.currentTimeMillis(), SHAKE_TIME_THRESHOLD = 5000;
    private Place currPlace = null;
    private ArrayList<Rep> repList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final GridViewPager pager = (GridViewPager)findViewById(R.id.pager);
        //PagerAdapter adapter = new PagerAdapter(getApplicationContext(), getFragmentManager());

        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if( ConnectionResult.SUCCESS != result ){
            Log.d(TAG, "Showing update dialog");
            // Show appropriate dialog
            Dialog d = GooglePlayServicesUtil.getErrorDialog(result, this, 0);
            d.show();
        }

        jsonString = getIntent().getStringExtra(KEY_JSON);
        try
        {
            if(jsonString == null || jsonString.isEmpty())
                throw new JSONException("The Json String is empty!");
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray reps = jsonObject.getJSONArray("reps");
            for(int i = 0; i < reps.length(); i++)
            {
                JSONObject jsonRep = reps.getJSONObject(i);
                if(jsonRep != null)
                    repList.add(new Rep(jsonRep, getApplicationContext()));
            }
            repList.add(new Rep("dummy")); // Add extra to make room for 2012 Vote View
            JSONObject jsonPlace = jsonObject.getJSONObject("place");
            if(jsonPlace != null)
                currPlace = new Place(jsonPlace);
        }
        catch (JSONException e)
        {
            Log.d(TAG, "wear/MainActivity, JSONException: " + e.getMessage());
            finish();
            return;
        }



        adapter = new CardAdapter(repList, getApplicationContext(), currPlace);
        pager.setAdapter(adapter);


        if(mWatchApiClient == null || !mWatchApiClient.isConnected())
        {
            mWatchApiClient = new GoogleApiClient.Builder(this)
                    .addApi( Wearable.API )
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        shaker = new ShakeEventListener(this);
    }

    @Override
    public void onPause()
    {
        shaker.unregisterListener();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        shaker.registerListener();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mWatchApiClient != null)
            mWatchApiClient.disconnect();
    }

    public static void cardClicked(int pos)
    {
        Log.d("Represent!", "cardClicked() called");
        repNumber = pos;
        watchAction = ACTION_SEND_REP_NUM;
        mWatchApiClient.disconnect();
        mWatchApiClient.connect();
    }

    @Override //alternate method to connecting: no longer create this in a new thread, but as a callback
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected() called! Watch Action: " + watchAction);
        if(watchAction == ACTION_SEND_REP_NUM) {
            Wearable.NodeApi.getConnectedNodes(mWatchApiClient)
                    .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                        @Override
                        public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                            nodes = getConnectedNodesResult.getNodes();
                            //Log.d("T", "found nodes");
                            //when we find a connected node, we populate the list declared above
                            //finally, we can send a message
                            sendMessage(PATH_REP_NUM, repNumber + "");
                            Log.d(TAG, "Sent Rep number: " + repNumber);
                        }
                    });
        }
        else if(watchAction == ACTION_SEND_RANDOM_ZIP)
        {
            Wearable.NodeApi.getConnectedNodes(mWatchApiClient)
                    .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                        @Override
                        public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                            nodes = getConnectedNodesResult.getNodes();
                            //Log.d("T", "found nodes");
                            //when we find a connected node, we populate the list declared above
                            //finally, we can send a message
                            sendMessage(PATH_ZIP_CODE, "true");
                        }
                    });
            //restartActivity();
        }
        else
            Log.d(TAG, "Invalid Watch Action Specified");
    }

    @Override //we need this to implement GoogleApiClient.ConnectionsCallback
    public void onConnectionSuspended(int i) {}


    private void sendMessage(final String path, final String text ) {
        for (Node node : nodes) {
            Wearable.MessageApi.sendMessage(
                    mWatchApiClient, node.getId(), path, text.getBytes());
        }
        if(path == PATH_REP_NUM)
        {
            Intent intent = new Intent(getApplicationContext(), ConfirmationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
            startActivity(intent);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection Failed! " + connectionResult.toString());
    }

    @Override
    public void onShake() {

        //Log.d(TAG, "onShake()");

        long thisShake = System.currentTimeMillis();
        long difference = thisShake - lastShake;

        if(difference > SHAKE_TIME_THRESHOLD)
        {
            lastShake = thisShake;
            watchAction = ACTION_SEND_RANDOM_ZIP;
            Log.d(TAG, "thisShake: " + thisShake + "   lastShake: " + lastShake + "   diff: " + difference);
            adapter.notifyDataSetChanged();
            mWatchApiClient.disconnect();
            mWatchApiClient.connect();

            Intent intent = new Intent(getApplicationContext(), ConfirmationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
            startActivity(intent);

            /*
            if(adapter.mChart != null) {
                adapter.setData(2, 100);
                adapter.mChart.invalidate();
                adapter.mChart.notifyDataSetChanged();
            }
            */
        }

    }

    @Override
    public void onLittleShake() {
        //Log.d(TAG, "onLittleShake");
        //Toast.makeText(getApplicationContext(), "Little Shake!", Toast.LENGTH_SHORT).show();

    }

    private void restartActivity()
    {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
