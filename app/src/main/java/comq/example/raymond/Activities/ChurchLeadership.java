package comq.example.raymond.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import comq.example.raymond.Adapters.LeaderAdapter;
import comq.example.raymond.Adapters.PastLeadersAdapter;
import comq.example.raymond.Adapters.PastorsAdapter;
import comq.example.raymond.Adapters.PresentLeadersAdapter;
import comq.example.raymond.Adapters.RevAdapter;
import comq.example.raymond.chamber2.R;

public class ChurchLeadership extends AppCompatActivity {

    private RecyclerView mRecyclerView_leaders;
    private LeaderAdapter mAdapter;
    private RevAdapter mRevAdapter;
    private PastorsAdapter mPastorsAdapter;
    private PastLeadersAdapter mPastLeadersAdapter;
    private PresentLeadersAdapter mPresentLeadersAdapter;
    private LinearLayoutManager mLeadersLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_church_leadership);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Church Leaders");

        initializeControlers();
        displayLeaders();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChurchLeadership.this, AddLeader.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart() {
        displayLeaders();
        super.onStart();
    }
    private void initializeControlers() {
        mRecyclerView_leaders = findViewById(R.id.list_items);
        mAdapter = new LeaderAdapter();
        mRevAdapter = new RevAdapter();
        mPastorsAdapter = new PastorsAdapter();
        mPastLeadersAdapter = new PastLeadersAdapter();
        mPresentLeadersAdapter = new PresentLeadersAdapter();
        mLeadersLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mGridLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.grid_span));
        mRecyclerView_leaders.setLayoutManager(mLeadersLinearLayoutManager);
    }
    private void displayLeaders() {
        getSupportActionBar().setTitle("All Leaders");
        initializeControlers();
        mRecyclerView_leaders.setAdapter(mAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sort:
                showSortDialog();
        }
        return super.onOptionsItemSelected(item);
    }
    private void showSortDialog() {
        //Options to display
        String[] sortOptions = {"Present", "Past", "Reverends", "Pastors", "All"};
        //create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort by:")
                .setIcon(R.drawable.ic_sort_white)
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //the which contains the index position of the selected item
                        if (which==0){
                            loadPresentLeader();
                        }else if (which==1){
                            loadPastLeaders();
                        }else if (which==2){
                            loadReverends();
                            //to be sorted according to chalet number
                        }else if (which == 3){
                            loadPastors();
                        }else if (which == 4){
                            displayLeaders();
                        }
                    }
                });
        builder.show();
    }
    private void loadPresentLeader() {
        getSupportActionBar().setTitle("Present Leaders");
        initializeControlers();
        mRecyclerView_leaders.setAdapter(mPresentLeadersAdapter);
    }
    private void loadPastLeaders() {
        getSupportActionBar().setTitle("Past Leaders");
        initializeControlers();
        mRecyclerView_leaders.setAdapter(mPastLeadersAdapter);
    }
    private void loadReverends(){
        getSupportActionBar().setTitle("Reverends");
        initializeControlers();
        mRecyclerView_leaders.setAdapter(mRevAdapter);
    }
    private void loadPastors(){
        getSupportActionBar().setTitle("Pastors");
        initializeControlers();
        mRecyclerView_leaders.setAdapter(mPastorsAdapter);
    }
}
