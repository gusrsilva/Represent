package gusrsilva.represent.Objects;

/**
 * Created by GusSilva on 2/28/16.
 */

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by GusSilva on 2/24/16.
 */
public class Rep {

    private String repType;
    private String name;
    private String party;
    private String email;
    private String website;
    private String imageUri;
    private String bioId;
    private String twitterId;
    private String termStart, termEnd;
    private int color;
    private ArrayList<Bill> bills;
    private ArrayList<String> committees;
    private String TAG = "Represent!";

    public Rep()
    {
        //Default Constructor
    }

    public String getRepType() {
        return repType;
    }

    public void setRepType(String repType) {
        if(repType.equalsIgnoreCase("house"))
            this.repType = "Representative";
        else
            this.repType = "Senator";
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
        if(party.equalsIgnoreCase("R")) {
            this.party = "Republican";
        }
        else {
            this.party = "Democrat";
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getBioId() {
        return bioId;
    }

    public void setBioId(String bioId) {
        this.bioId = bioId;
    }


    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }


    public String getTermStart() {
        return termStart;
    }

    public void setTermStart(String termStart) {
        if (termStart.isEmpty())
            return;

        String[] strings = termStart.split("-");
        String year = strings[0];
        String day = strings[1];
        String month = getMonth(strings[2]);
        this.termStart = String.format(Locale.ENGLISH
                , "%s %s, %s", month, day, year);
    }

    public String getTermEnd() {
        return termEnd;
    }

    public void setTermEnd(String termEnd) {
        if (termEnd.isEmpty())
            return;

        String[] strings = termEnd.split("-");
        String year = strings[0];
        String day = strings[1];
        String month = getMonth(strings[2]);
        this.termEnd = String.format(Locale.ENGLISH
                , "%s %s, %s", month, day, year);
    }


    public ArrayList<Bill> getBills() {
        return bills;
    }

    public void setBills(ArrayList<Bill> bills) {
        this.bills = bills;
    }

    public ArrayList<String> getCommittees() {
        return committees;
    }

    public void setCommittees(ArrayList<String> committees) {
        this.committees = committees;
    }

    private String getMonth(String str)
    {
        int choice = Integer.parseInt(str);
        switch (choice)
        {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            default:
                return "Dec";
        }
    }

    public JSONObject toJSONObject()
    {
        try
        {
            JSONObject object = new JSONObject();
            object.put("repType", repType);
            object.put("name", name);
            object.put("party", party);
            object.put("imageUri", imageUri);
            object.put("bioId", bioId);

            return object;
        }
        catch (JSONException e)
        {
            Log.d(TAG, " Error creating Rep JSON");
            return null;
        }

    }

    @Override
    public String toString() {
        return "Rep{" +
                "repType='" + repType + '\'' +
                ", name='" + name + '\'' +
                ", party='" + party + '\'' +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", bioId='" + bioId + '\'' +
                ", twitterId='" + twitterId + '\'' +
                ", termStart='" + termStart + '\'' +
                ", termEnd='" + termEnd + '\'' +
                ", color=" + color +
                '}';
    }


}

