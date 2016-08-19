package gusrsilva.represent;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import gusrsilva.represent.Adapters.BillAdapter;
import gusrsilva.represent.Adapters.CommitteeAdapter;
import gusrsilva.represent.Objects.Rep;

public class ViewRepresentative extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_representative);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This would bring up email, website, etc...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int pos = getIntent().getIntExtra(MainActivity.REP_NUM, -1);

        if(pos == -1 || MainActivity.repList == null || pos >= MainActivity.repList.size())
        {
            Toast.makeText(getApplication(), "Error retreiving representatives", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Rep rep = MainActivity.repList.get(pos);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(" ");
        collapsingToolbarLayout.setBackgroundResource(R.drawable.header);
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(getApplicationContext(), R.color.dem_blue));

        LinearLayout header = (LinearLayout)findViewById(R.id.header);

        if(rep.getParty().equalsIgnoreCase("republican"))
        {
            collapsingToolbarLayout.setContentScrimColor(rep.getColor());
            collapsingToolbarLayout.setStatusBarScrimColor(rep.getColor());
            //fab.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dem_blue));
            fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.dem_blue)));
            header.setBackgroundColor(rep.getColor());

        }


        TextView partyType = (TextView)findViewById(R.id.party_type);
        TextView name = (TextView)findViewById(R.id.name);
        TextView currentTerm = (TextView)findViewById(R.id.currentTerm);
        partyType.setText((rep.getParty().equalsIgnoreCase("Democrat")? rep.getParty() + "ic": rep.getParty()) + " " + rep.getRepType());
        name.setText(rep.getName());
        currentTerm.setText(rep.getTermStart() + " to " + rep.getTermEnd());

        // Set up bills list
        RecyclerView billRecycler = (RecyclerView)findViewById(R.id.billsRecycler);
        billRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        BillAdapter billAdt = new BillAdapter(rep.getBills(), getApplicationContext(), rep.getColor());
        billRecycler.setAdapter(billAdt);
        billRecycler.setNestedScrollingEnabled(false);

        // Set up Committee List
        RecyclerView committeesRecyler = (RecyclerView)findViewById(R.id.committeesRecycler);
        committeesRecyler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        CommitteeAdapter committeeAdapter = new CommitteeAdapter(rep.getCommittees(), getApplicationContext(), rep.getColor());
        committeesRecyler.setAdapter(committeeAdapter);
        committeesRecyler.setNestedScrollingEnabled(false);



    }
}
