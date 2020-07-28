package comq.example.raymond.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import comq.example.raymond.Interface.ItemClickListener;
import comq.example.raymond.Model.HistoryModel;
import comq.example.raymond.Utils.ChurchUtility;
import comq.example.raymond.chamber2.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChurchHistoryActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView_history;

    private FloatingActionButton fab;

    //-Lzi95jViTMp44fwcHla

    private DatabaseReference history;

    private FirebaseRecyclerAdapter<HistoryModel, HistoryViewholder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_church_history);

        history = FirebaseDatabase.getInstance().getReference().child("COCINCHAMBER").child("history");

        recyclerView_history = findViewById(R.id.recycler_church_history);
        recyclerView_history.setLayoutManager(new LinearLayoutManager(this));

        fab = findViewById(R.id.fab);


        toolbar = findViewById(R.id.church_history_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Church Members");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChurchHistoryActivity.this, EditChurchHistory.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<HistoryModel> options = new FirebaseRecyclerOptions.Builder<HistoryModel>()
                .setQuery(history, HistoryModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<HistoryModel, HistoryViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HistoryViewholder holder, final int position, @NonNull HistoryModel model) {
                holder.textViewHistory.setText(model.getHistory());
                holder.textViewDateUpdated.setText("Last Updated " + ChurchUtility.dateFromLong(model.getDateUpdated()));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(ChurchHistoryActivity.this, "Thank God!", Toast.LENGTH_SHORT).show();
                    }
                });

//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //get the user ID of the user clicked on
//                        String clicked_user_id = getRef(position).getKey();
//                        Intent profileIntent = new Intent(ChurchHistoryActivity.this, MemberProfileActivity.class);
//                        profileIntent.putExtra("clicked_user_id", clicked_user_id);
//                        startActivity(profileIntent);
//                    }
//                });

            }

            @NonNull
            @Override
            public HistoryViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_layout, viewGroup, false);
                HistoryViewholder viewholder = new HistoryViewholder(view);
                return viewholder;
            }
        };
        recyclerView_history.setAdapter(adapter);
        adapter.startListening();
    }

    public static class HistoryViewholder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textViewDateUpdated, textViewHistory;
        private ItemClickListener itemClickListener;
        public HistoryViewholder(@NonNull View itemView) {
            super(itemView);

            textViewDateUpdated = itemView.findViewById(R.id.txt_last_updated);
            textViewHistory = itemView.findViewById(R.id.txt_history);

            itemView.setOnClickListener(this);
        }
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }
}
