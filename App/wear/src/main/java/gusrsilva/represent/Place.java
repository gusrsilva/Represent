package gusrsilva.represent;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/** WEAR
 * Created by GusSilva on 3/7/16.
 */
public class Place {
    private String city, county, state, zip, TAG = "Represent!";

    public Place(JSONObject jsonObject)
    {
        try
        {
            setCity(jsonObject.getString("city"));
            setCounty(jsonObject.getString("county"));
            setState(jsonObject.getString("state"));
            setZip(jsonObject.getString("zip"));
        }
        catch (JSONException e)
        {
            Log.d(TAG, "Error creating Place from JSON: " + e.getMessage());
        }
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
