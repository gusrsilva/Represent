package gusrsilva.represent.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import gusrsilva.represent.Objects.Bill;
import gusrsilva.represent.R;

/**
 * Created by GusSilva on 3/9/16.
 */
public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {

    private ArrayList<Bill> bills;
    private Context mContext;
    private int color;

    public BillAdapter(ArrayList<Bill> billArrayList, Context c, int col)
    {
        bills = billArrayList;
        mContext = c;
        color = col;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.bill_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bill bill = bills.get(position);

        if(bill == null || holder == null)
            return;

        holder.billName.setText(bill.getName());
        holder.billName.setTextColor(color);
        holder.billDate.setText(bill.getIntroDate());
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView billName, billDate;

        public ViewHolder(View itemView)
        {
            super(itemView);
            billName = (TextView)itemView.findViewById(R.id.billTitle);
            billDate = (TextView)itemView.findViewById(R.id.billDate);
        }

    }
}
