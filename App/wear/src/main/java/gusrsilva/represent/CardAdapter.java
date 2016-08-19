package gusrsilva.represent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.CardFrame;
import android.support.wearable.view.CardScrollView;
import android.support.wearable.view.GridPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.zip.Inflater;

/** WEAR
 * Created by GusSilva on 2/29/16.
 */
public class CardAdapter extends GridPagerAdapter {

    ArrayList<Rep> repList;
    Context mContext;
    public PieChart mChart;
    private String TAG = "Represent!";
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Place place;
    float obamaPercent = -1, romneyPercent = -1;

    public CardAdapter(ArrayList<Rep> reps, Context context, Place p)
    {
        repList = reps;
        mContext = context;
        place = p;
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.def)
                .showImageOnLoading(R.drawable.def)
                .showImageOnFail(R.drawable.def)
                .cacheInMemory(true)
                .build();
        get2012Info();
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int i) {
        return repList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int row, final int col) {
        View view;
        Rep rep = repList.get(col);
        if(!rep.getName().equalsIgnoreCase("dummy"))
        {
            view = LayoutInflater.from(mContext).inflate(R.layout.rep_card, null);

            TextView type, name, party;
            BoxInsetLayout holder = (BoxInsetLayout) view.findViewById(R.id.holder);

            type = (TextView) view.findViewById(R.id.type);
            name = (TextView) view.findViewById(R.id.name);
            party = (TextView) view.findViewById(R.id.party);
            ImageView bg = (ImageView)view.findViewById(R.id.background);

            type.setText(rep.getRepType());
            name.setText(rep.getName());
            party.setText(rep.getParty());
            party.setTextColor(rep.getColor());
            imageLoader.displayImage(rep.getImageUri(), bg, options);
            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.cardClicked(col);
                }
            });
        }
        else
        {
            view = LayoutInflater.from(mContext).inflate(R.layout.pi_screen, null);
            if(place != null)
            {
                TextView location = (TextView)view.findViewById(R.id.location);
                if(place.getCounty() != null && place.getState() != null)
                    location.setText(place.getCounty() + ", " + place.getState());
            }

            mChart = (PieChart)view.findViewById(R.id.chart);
            mChart.setNoDataText("");
            mChart.setUsePercentValues(true);
            mChart.setDescription("");
            mChart.getLegend().setEnabled(false);
            //mChart.setExtraOffsets(5, 10, 5, 5);

            get2012Info();
            mChart.setDrawHoleEnabled(false);
            mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
            mChart.setNoDataText("2012 Voting Data Not Available");

        }
        viewGroup.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup viewGroup, int i, int i1, Object o) {
        viewGroup.removeView((View) o);

    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view.equals(o);
    }

    public void setData(float obamaPercent, float romneyPercent) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.

        //Log.d(TAG, " setting PieChart Data. oldZip: " + oldZip + "   currZip: " + MainActivity.zipCode);
        yVals.add(new Entry(obamaPercent, 0));
        yVals.add(new Entry(romneyPercent, 1));
        yVals.add(new Entry((100 - (obamaPercent + romneyPercent)), 2));

        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Obama"); xVals.add("Romney");xVals.add("Other");

        PieDataSet dataSet = new PieDataSet(yVals, "Election Results");
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(ContextCompat.getColor(mContext, R.color.dem_blue));
        colors.add(ContextCompat.getColor(mContext, R.color.rep_red));
        colors.add(Color.DKGRAY);


        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(8f);
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    public void get2012Info()
    {

        if(romneyPercent != -1 || obamaPercent != -1 && mChart != null)
        {
            setData(obamaPercent, romneyPercent);
            return;
        }
        String url = "https://raw.githubusercontent.com/cs160-sp16/voting-data/master/election-county-2012.json";
        RequestQueue queue = Volley.newRequestQueue(mContext);

        // prepare the Request
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url
                ,new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d(TAG, "JSON response: " + response.toString().substring(0,100));

                        try
                        {
                            if(place == null || place.getCounty() == null || place.getCounty().equals("null"))
                            {
                                Log.d(TAG, "Get2012Info, county not available!");
                                return;
                            }

                            String county = place.getCounty();
                            for(int i = 0; i < response.length(); i++)
                            {
                                JSONObject currObj = response.getJSONObject(i);
                                if(county.toLowerCase().contains(currObj.getString("county-name").toLowerCase()))
                                {
                                    //Log.d(TAG, "Found: " + county);
                                    obamaPercent = Float.parseFloat(currObj.getString("obama-percentage"));
                                    romneyPercent = Float.parseFloat(currObj.getString("romney-percentage"));
                                    //Log.d(TAG, "Calling setData with Obama: " + obamaPercent + " Romney: " + romneyPercent);
                                    if(mChart != null)
                                        setData(obamaPercent, romneyPercent);
                                    return;
                                }
                            }

                            Log.d(TAG, county + " not found!");

                        }
                        catch (JSONException error)
                        {
                            Log.d(TAG, "2012 JSON Exception: " + error.getMessage());
                        }
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error with 2012 info: " + error.getMessage());
            }
        });
        queue.add(getRequest);
    }
}

