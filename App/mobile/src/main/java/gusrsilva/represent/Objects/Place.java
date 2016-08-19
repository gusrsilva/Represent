package gusrsilva.represent.Objects;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/** MOBILE
 * Created by GusSilva on 3/7/16.
 */
public class Place {
    private String city, county, state, zip, TAG = "Represent!";

    public Place()
    {
        // Empty constructor
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

    public String getPrettyLocation()
    {
        if(city != null && state != null)
            return city + ", " + state;
        else
            return null;
    }

    public JSONObject toJSONObject()
    {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("city", this.city);
            jsonObject.put("county", this.county);
            jsonObject.put("state", this.state);
            jsonObject.put("zip", this.zip);
            return jsonObject;
        }
        catch (JSONException e)
        {
            Log.d(TAG, "Error converting Place to JSONObject: " + e.getMessage());
            return null;
        }
    }


    @Override
    public String toString() {
        return "Place{" +
                "city='" + city + '\'' +
                ", county='" + county + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                '}';
    }
}
