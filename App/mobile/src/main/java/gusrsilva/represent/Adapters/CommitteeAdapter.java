package gusrsilva.represent.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import gusrsilva.represent.R;

/**
 * Created by GusSilva on 3/9/16.
 */
public class CommitteeAdapter extends RecyclerView.Adapter<CommitteeAdapter.ViewHolder> {

    private ArrayList<String> committees;
    private Context mContext;
    private int color;

    public CommitteeAdapter(ArrayList<String> comList, Context c, int col)
    {
        committees = comList;
        mContext = c;
        color = col;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.committee_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String committee = committees.get(position);

        if(committee == null || holder == null)
            return;

        holder.committeeName.setText(committee);
        holder.committeeName.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        if(committees == null)
            return 0;
        else
            return committees.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView committeeName;

        public ViewHolder(View itemView)
        {
            super(itemView);
            committeeName = (TextView)itemView.findViewById(R.id.committeeName);
        }

    }
}
