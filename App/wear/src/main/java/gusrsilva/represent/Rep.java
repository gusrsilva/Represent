package gusrsilva.represent;

/** WEAR
 * Created by GusSilva on 2/28/16.
 */

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Rep {

    private String repType;
    private String name;
    private String party;
    private String imageUri;
    private String TAG = "Represent!";
    private int color;
    private Context mContext;

    public Rep(JSONObject jsonObject, Context c)
    {
        mContext = c;
        try
        {
            setName(jsonObject.getString("name"));
            setRepType(jsonObject.getString("repType"));
            setParty(jsonObject.getString("party"));
            setImageUri(jsonObject.getString("imageUri"));
        }
        catch (JSONException e)
        {
            Log.d(TAG, "Error creating Rep from JSON: " + e.getMessage());
        }
    }

    public Rep(String name)
    {
        setName(name);
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getRepType() {
        return repType;
    }

    public void setRepType(String repType) {
        this.repType = repType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;

        if(party.equalsIgnoreCase("republican"))
            setColor(ContextCompat.getColor(mContext, R.color.rep_red));
        else
            setColor(ContextCompat.getColor(mContext, R.color.dem_blue));
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}

