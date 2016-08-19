package gusrsilva.represent.Objects;

import android.util.Log;

import java.util.Locale;

/**
 * Created by GusSilva on 3/8/16.
 */
public class Bill {
    private String TAG = "Represent!";
    private String introDate;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroDate() {
        return introDate;
    }

    public void setIntroDate(String introDate) {
        if(introDate.isEmpty())
            return;

        String[] strings = introDate.split("-");
        String year = strings[0];
        String day = strings[1];
        String month = getMonth(strings[2]);
        this.introDate = String.format(Locale.ENGLISH
        , "%s %s, %s", month, day, year);
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
}
