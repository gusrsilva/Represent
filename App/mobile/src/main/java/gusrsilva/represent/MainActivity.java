package gusrsilva.represent;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import gusrsilva.represent.Adapters.RepsAdapter;
import gusrsilva.represent.Objects.Bill;
import gusrsilva.represent.Objects.Place;
import gusrsilva.represent.Objects.Rep;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , ActivityCompat.OnRequestPermissionsResultCallback {

    FloatingActionButton fab;
    private String TAG = "Represent!";
    public static String REP_NUM = "rep_num", ZIP_CODE = "zip_code";
    public static ArrayList<Rep> repList = new ArrayList<>();
    private ProgressDialog dialog;
    private RepsAdapter adt;
    private RecyclerView recyclerView;
    private GoogleApiClient mApiClient;
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "XI1YkrqjI0iKPWtHpxqvjoxY2";
    private static final String TWITTER_SECRET = "JzCQURwTp98Zip9rN5hNIpM68HGzNMG1DFOLa0qztlHtoO0oLe";
    private String PATH = "/JSON";
    private  List<Node> nodes = new ArrayList<>();
    private JSONObject info;
    int BILLS_THRESHOLD_NUM = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        // TODO: Enable

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Place currentPlace = ChooseLocationActivity.currentPlace;
        if(currentPlace == null) {
            Toast.makeText(getApplicationContext(), "Error retrieving location", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        else if(currentPlace.getPrettyLocation() == null)
            Toast.makeText(getApplicationContext(), "Current Zip: " + currentPlace.getZip(), Toast.LENGTH_SHORT).show();
        else {
            toolbar.setTitle(currentPlace.getPrettyLocation());
            //Toast.makeText(getApplicationContext(), "Current Location: " + currentPlace.getPrettyLocation(), Toast.LENGTH_SHORT).show();
        }

        setSupportActionBar(toolbar);

        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        dialog = ProgressDialog.show(MainActivity.this, "",
                "Loading Representatives. Please wait...", true);

        recyclerView = (RecyclerView)findViewById(R.id.recylcerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        String url = buildUrlFromZip(currentPlace.getZip());

        // Starts a chain: get reps -> get bills -> get committees -> JSON -> send to watch
        createRepListFromUrl(url);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChooseLocationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader.getInstance().init(config);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void moreInfoPressed(View view)
    {
        int pos = Integer.parseInt(view.getTag().toString());
        Intent intent = new Intent(getApplicationContext(), ViewRepresentative.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(REP_NUM, pos);
        startActivity(intent);
    }

    private String buildUrlFromZip(String zip)
    {
        String key = getResources().getString(R.string.KEY_SUNLIGHT);
        String url = String.format(Locale.ENGLISH
                ,"http://congress.api.sunlightfoundation.com/legislators/locate?zip=%s&apikey=%s"
                , zip
                , key);
        return url;
    }

    private void createRepListFromUrl(String url)
    {

        if ( ContextCompat.checkSelfPermission( getApplicationContext()
                , Manifest.permission.INTERNET )
                != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET}, 0);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try
                        {
                            if(response.getInt("count") == 0)
                            {
                                Toast.makeText(getApplicationContext(), "No results for that area.", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            JSONArray results = response.getJSONArray("results");
                            repList = new ArrayList<>();
                            for(int i=0; i<results.length(); i++)
                            {
                                Rep currRep = new Rep();
                                JSONObject curr = results.getJSONObject(i);
                                currRep.setName(curr.getString("first_name") + " " + curr.getString("last_name"));
                                currRep.setBioId(curr.getString("bioguide_id"));
                                currRep.setImageUri(String.format(Locale.ENGLISH
                                        ,"https://theunitedstates.io/images/congress/original/%s.jpg"
                                        , curr.get("bioguide_id")));
                                currRep.setRepType(curr.getString("chamber"));
                                currRep.setEmail(curr.getString("oc_email"));
                                currRep.setParty(curr.getString("party"));
                                if(curr.getString("party").equalsIgnoreCase("R"))
                                    currRep.setColor(ContextCompat.getColor(getApplicationContext(), R.color.rep_red));
                                else
                                    currRep.setColor(ContextCompat.getColor(getApplicationContext(), R.color.dem_blue));
                                currRep.setWebsite(curr.getString("website"));
                                currRep.setTermEnd(curr.getString("term_end"));
                                currRep.setTermStart(curr.getString("term_start"));
                                currRep.setTwitterId(curr.getString("twitter_id"));
                                //Log.d(TAG, currRep.toString());
                                repList.add(currRep);
                            }
                        }
                        catch (JSONException e)
                        {
                            Log.d(TAG, "Failed: " + e.toString());
                            Toast.makeText(getApplicationContext(), "Error retrieving representatives.", Toast.LENGTH_SHORT).show();
                        }


                        updateRecycler();
                        if(dialog != null)
                            dialog.dismiss();

                        getBills();
                        getCommittees();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
            }
        });
    }

    private String buildBillsUrlFromBioguide(String bioguide)
    {
        String key = getResources().getString(R.string.KEY_SUNLIGHT);
        return String.format(Locale.ENGLISH
                , "http://congress.api.sunlightfoundation.com/bills?sponsor_id=%s&apikey=%s"
                , bioguide
                , key);
    }

    private void addRepBillsFromUrl(String url, final int index)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest GET = new JsonObjectRequest(Request.Method.GET
                , url
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<Bill> billList = new ArrayList<>();
                    int count = Integer.parseInt(response.getString("count"));
                    JSONArray arr = response.getJSONArray("results");
                    int THRESHOLD_NUM = BILLS_THRESHOLD_NUM;
                    for(int i = 0; i < arr.length() && i < THRESHOLD_NUM; i++)
                    {
                        Bill bill = new Bill();
                        JSONObject currObj = arr.getJSONObject(i);
                        String introDate = currObj.getString("introduced_on");
                        String title = currObj.getString("short_title");
                        if (title == null)
                            title = currObj.getString("official_title");

                        if(title.equalsIgnoreCase("null"))
                            THRESHOLD_NUM++;
                        else
                        {
                            bill.setName(title);
                            bill.setIntroDate(introDate);
                            billList.add(bill);
                        }
                    }

                    repList.get(index).setBills(billList);

                }
                catch (JSONException e)
                {
                    Log.d(TAG, "JSONException when retreiving bills: " + e.getMessage());
                }
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error retreiving bills: " + error.getMessage());
            }
        });
        queue.add(GET);
    }

    private void getBills()
    {
        for(int i = 0; i < repList.size(); i++)
        {
            if(repList.get(i).getBioId() != null)
            {
                String url = buildBillsUrlFromBioguide(repList.get(i).getBioId());
                addRepBillsFromUrl(url, i);
            }
        }
    }

    private String buildCommitteesUrlFromBioguid(String bioguide)
    {
        String key = getResources().getString(R.string.KEY_SUNLIGHT);
        return String.format(Locale.ENGLISH
                ,"http://congress.api.sunlightfoundation.com/committees?member_ids=%s&apikey=%s"
                , bioguide
                , key);

    }

    private void addRepCommitteesFromUrl(String url, final int index)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest GET = new JsonObjectRequest(Request.Method.GET
                , url
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<String> committees = new ArrayList<>();
                    int THRESHOLD_NUM = 10;
                    int count = Integer.parseInt(response.getString("count"));
                    JSONArray arr = response.getJSONArray("results");
                    for(int i = 0; i < arr.length() && i < THRESHOLD_NUM; i++)
                    {
                        JSONObject currObj = arr.getJSONObject(i);
                        String title = currObj.getString("name");

                        if(title == null || title.equalsIgnoreCase("null"))
                            THRESHOLD_NUM++;
                        else
                        {
                            committees.add(title);
                        }
                    }

                    repList.get(index).setCommittees(committees);

                }
                catch (JSONException e)
                {
                    Log.d(TAG, "JSONException when retreiving committees: " + e.getMessage());
                }
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error retreiving committees: " + error.getMessage());
            }
        });
        queue.add(GET);
    }

    private void getCommittees()
    {
        for(int i = 0; i < repList.size(); i++)
        {
            if(repList.get(i).getBioId() != null)
            {
                String url = buildCommitteesUrlFromBioguid(repList.get(i).getBioId());
                addRepCommitteesFromUrl(url, i);
            }
        }

        transferDataToWatch();
    }

    private void updateRecycler()
    {
        adt = new RepsAdapter(repList, getApplicationContext());
        recyclerView.setAdapter(adt);
    }

    private JSONObject generateJSONforWatch()
    {
        if(repList == null || repList.size() == 0)
            return null;

        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (Rep rep : repList) {
                JSONObject temp = rep.toJSONObject();
                if (temp != null)
                    jsonArray.put(temp);
            }
            jsonObject.put("reps", jsonArray);
            JSONObject temp = ChooseLocationActivity.currentPlace.toJSONObject();
            if(temp != null)
            {
                jsonObject.put("place", temp);
            }

            return jsonObject;
        }
        catch (JSONException e)
        {
            Log.d(TAG, "Error creating JSON from reps: " + e.getMessage());
            return null;
        }
    }

    private void transferDataToWatch()
    {
        mApiClient.disconnect(); mApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        info = generateJSONforWatch();
        if(info == null)
        {
            Log.d(TAG, "Error generating JSON for watch");
            return;
        }

        // Get Nodes
        Wearable.NodeApi.getConnectedNodes(mApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        nodes = getConnectedNodesResult.getNodes();
                        for (Node node : nodes)
                        {
                            Wearable.MessageApi.sendMessage(
                                    mApiClient, node.getId(), PATH, info.toString().getBytes());
                            Log.d(TAG, "Sent JSON from MainActivity: " + info.toString());
                        }
                        Log.d(TAG, "MainActivity: Length of Nodes: " + nodes.size());
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "mobile/MainActivity, could not connect: " + connectionResult.getErrorMessage());
    }
}
