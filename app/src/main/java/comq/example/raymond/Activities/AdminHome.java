package comq.example.raymond.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Date;

import comq.example.raymond.Common.Common;
import comq.example.raymond.Model.HistoryModel;
import comq.example.raymond.Utils.ChurchUtility;
import comq.example.raymond.chamber2.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdminHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private DatabaseReference historyData;

    private ProgressDialog progressDialog;

    private HistoryModel updateHistory;

    private FirebaseRecyclerAdapter<HistoryModel, ViewHolder>adapter;

    private RecyclerView recycler_history;
    RecyclerView.LayoutManager layoutManager;

    private EditText editTextChurchHistory;

    private TextView textViewLastUpdated, textViewHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        //recycler_history = findViewById(R.id.recycler_history);
//        recycler_history.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recycler_history.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();

        textViewHistory = findViewById(R.id.txt_history);
        textViewLastUpdated = findViewById(R.id.txt_last_updated);

        historyData = FirebaseDatabase.getInstance().getReference().child("COCINCHAMBER").child("history");

        progressDialog = new ProgressDialog(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                historyDialog();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        CircleImageView logo = headerView.findViewById(R.id.imageView_logo);

        Picasso.get().load(R.drawable.logo).into(logo);
        //loadHistory();

    }
//
//    private void loadHistory() {
//        FirebaseRecyclerOptions<HistoryModel>options = new FirebaseRecyclerOptions.Builder<HistoryModel>()
//                .setQuery(historyData, HistoryModel.class)
//                .build();
//        adapter = new FirebaseRecyclerAdapter<HistoryModel, ViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull HistoryModel model) {
//                holder.txt_history.setText(model.getHistory());
//                holder.txt_date_updated.setText("Last Updated " + ChurchUtility.dateFromLong(model.getDateUpdated()));
//
//
//
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(AdminHome.this, "Say good!", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            @NonNull
//            @Override
//            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_layout, viewGroup,false);
//                ViewHolder viewHolder = new ViewHolder(view);
//                return viewHolder;
//            }
//        };
//        recycler_history.setAdapter(adapter);
//        adapter.startListening();
//    }


    private void historyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminHome.this);
        View view = getLayoutInflater().inflate(R.layout.history_dialog, null);


        final EditText editTextHistory = view.findViewById(R.id.edit_histoty);
        Button btn_save = view.findViewById(R.id.btn_save);

        //set onclick listener on login button
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String history = editTextHistory.getText().toString().trim();
                if (!history.isEmpty()){
                    progressDialog.setTitle("Church History");
                    progressDialog.setMessage("Saving History...");
                    progressDialog.show();

                    final long dateUpdated = new Date().getTime();

                    updateHistory = new HistoryModel(history, dateUpdated);
                    historyData.push().setValue(updateHistory).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            Toast.makeText(AdminHome.this, "History Updated!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AdminHome.this, AdminHome.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdminHome.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }else {
                    Toast.makeText(AdminHome.this, "Type a valid history", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_approve_member) {
            // membership application
            startActivity(new Intent(AdminHome.this, MembershipApplications.class));

        } else if (id == R.id.nav_upload_event) {
            Intent evenIntent = new Intent(AdminHome.this, UpcomingEventsList.class);
            startActivity(evenIntent);
        } else if (id == R.id.nav_manage_group) {
            Intent intent = new Intent(AdminHome.this, ChurchGroupsList.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage_gallery) {
            Intent intent = new Intent(AdminHome.this, ChurchGallery.class);
            startActivity(intent);

        } else if (id == R.id.nav_update_church_leadership) {
            Intent intent = new Intent(AdminHome.this, ChurchLeadership.class);
            startActivity(intent);

        } else if (id == R.id.nav_update_church_history) {
            startActivity(new Intent(AdminHome.this, ChurchHistoryActivity.class));

        }else if(id == R.id.nav_change_password){

        }else if (id == R.id.nav_church_members){
            startActivity(new Intent(AdminHome.this, ChurchMembersList.class));

        }else if (id == R.id.nav_exit){
            //logout
            mAuth.getCurrentUser();
            mAuth.signOut();
            finish();
            Intent signoutIntent = new Intent(AdminHome.this, MainActivity.class);
            signoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signoutIntent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)){
            updateHistoryMethod(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }else if (item.getTitle().equals(Common.DELETE)){
            deleteHistory(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void updateHistoryMethod(String key, HistoryModel item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminHome.this);
        alertDialog.setTitle("Update Church History");

        LayoutInflater inflater = this.getLayoutInflater();
        View church_history = inflater.inflate(R.layout.update_history_dialog, null);

        editTextChurchHistory = church_history.findViewById(R.id.edit_histoty);
//        btnSelect = add_chalet_layout.findViewById(R.id.btnSelect);
//        btnUpload = add_chalet_layout.findViewById(R.id.btnUpload);

        alertDialog.setView(church_history);


        //set button
        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                final long dateUpdated = new Date().getTime();
                final String updatedHistory = editTextChurchHistory.getText().toString().trim();
                updateHistory =  new HistoryModel(updatedHistory, dateUpdated);

                //we just create a new chalet
                if (updateHistory != null){
                    historyData.push().setValue(updateHistory);
                    Toast.makeText(AdminHome.this, "Church history updated", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(AdminHome.this, "Church history is empty", Toast.LENGTH_SHORT).show();
                }

            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertDialog.show();
    }

    private void deleteHistory(String key) {
    }




    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_date_updated, txt_history;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_date_updated = itemView.findViewById(R.id.txt_last_updated);
            txt_history = itemView.findViewById(R.id.txt_history);

        }

    }
}
