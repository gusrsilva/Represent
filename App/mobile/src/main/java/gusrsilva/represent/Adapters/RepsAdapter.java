package gusrsilva.represent.Adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import gusrsilva.represent.R;
import gusrsilva.represent.Objects.Rep;
import io.fabric.sdk.android.Fabric;

/**
 * Created by GusSilva on 2/24/16.
 */
public class RepsAdapter extends RecyclerView.Adapter<RepsAdapter.ViewHolder> {

    private ArrayList<Rep> repList = new ArrayList<>();
    private Context context;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private String TAG = "Represent!";

    public RepsAdapter(ArrayList<Rep> reps, Context c)
    {
        //Constructor
        context = c;
        repList = reps;
        imageLoader = ImageLoader.getInstance(); // Get singleton instance

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.def)
                .showImageOnFail(R.drawable.def)
                .showImageOnLoading(R.drawable.def)
                .resetViewBeforeLoading(true)  // default
                .cacheInMemory(true)
                .displayer(new CircleBitmapDisplayer())
                .build();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rep_card, parent, false);
        return new ViewHolder(v);    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Rep currentRep = repList.get(position);

        if(holder != null)
        {
            holder.repType.setText(currentRep.getRepType());
            holder.name.setText(currentRep.getName());
            holder.party.setText(currentRep.getParty());
            holder.email.setText(currentRep.getEmail());
            holder.website.setText(currentRep.getWebsite());
            holder.moreInfo.setTag(position);


            TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
                @Override
                public void success(Result<AppSession> appSessionResult) {
                    AppSession session = appSessionResult.data;
                    TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                    twitterApiClient.getStatusesService().userTimeline(null, currentRep.getTwitterId(), 1, null, null, false, true, false, true, new Callback<List<Tweet>>() {
                        @Override
                        public void success(Result<List<Tweet>> listResult) {
                            for(Tweet tweet: listResult.data) {
                                TweetView tweetView = new TweetView(context, tweet);
                                tweetView.setClickable(false);
                                holder.tweetHolder.addView(tweetView);
                                holder.tweetHolder.setClickable(false);
                                holder.latestTweet.setVisibility(View.VISIBLE);
                            }
                        }
                        @Override
                        public void failure(TwitterException e) {
                            Log.d(TAG, "TwitterException - Failed to get tweet: " + e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    });
                }
                @Override
                public void failure(TwitterException e) {
                    Log.d(TAG, "Failed to get tweet: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            });


            imageLoader.displayImage(currentRep.getImageUri(), holder.image, options);

            holder.email.setTextColor(currentRep.getColor());
            holder.website.setTextColor(currentRep.getColor());
            holder.moreInfo.setTextColor(currentRep.getColor());

            holder.website.setLinkTextColor(currentRep.getColor());
            holder.email.setLinkTextColor(currentRep.getColor());
            holder.website.setLinksClickable(true);
            holder.email.setLinksClickable(true);

        }

    }

    @Override
    public int getItemCount() {
        return repList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView repType, name, party, email, website;
        public ImageView image;
        public Button moreInfo;
        public FrameLayout tweetHolder;
        private LinearLayout latestTweet;

        public ViewHolder(View itemView) {
            super(itemView);
            //get title and artist views
            repType = (TextView) itemView.findViewById(R.id.rep_type);
            name = (TextView) itemView.findViewById(R.id.name);
            party = (TextView) itemView.findViewById(R.id.party);
            email = (TextView) itemView.findViewById(R.id.email);
            website = (TextView) itemView.findViewById(R.id.website);
            image = (ImageView) itemView.findViewById(R.id.rep_image);
            moreInfo = (Button) itemView.findViewById(R.id.more_info);
            tweetHolder = (FrameLayout)itemView.findViewById(R.id.tweet_holder);
            latestTweet = (LinearLayout)itemView.findViewById(R.id.latest_tweet);
        }

    }
}
