package comq.example.raymond.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import comq.example.raymond.Model.MemberModel;
import comq.example.raymond.chamber2.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class MembershipApplications extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView_membership_application;

    private DatabaseReference members;

    private FirebaseRecyclerAdapter<MemberModel, MembershipApplicationsViewholder>adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_applications);


        members = FirebaseDatabase.getInstance().getReference().child("COCINCHAMBER").child("members");

        recyclerView_membership_application = findViewById(R.id.recycler_membership_applications);
        recyclerView_membership_application.setLayoutManager(new LinearLayoutManager(this));


        toolbar = findViewById(R.id.membership_application_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Membership Applications");
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<MemberModel>options = new FirebaseRecyclerOptions.Builder<MemberModel>()
                .setQuery(members.orderByChild("status").equalTo("WAITING"), MemberModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<MemberModel, MembershipApplicationsViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MembershipApplicationsViewholder holder, final int position, @NonNull MemberModel model) {
                holder.textViewMembername.setText(model.getName());
                holder.textViewMemberAddress.setText(model.getAddress());

                Picasso.get().load(model.profilePic).placeholder(R.drawable.login).into(holder.profile_pix);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //get the user ID of the user clicked on
                        String clicked_user_id = getRef(position).getKey();

                        Intent profileIntent = new Intent(MembershipApplications.this, MemberProfileActivity.class);
                        profileIntent.putExtra("clicked_user_id", clicked_user_id);
                        startActivity(profileIntent);
                    }
                });

            }

            @NonNull
            @Override
            public MembershipApplicationsViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.member_display_layout, viewGroup, false);
                MembershipApplicationsViewholder viewholder = new MembershipApplicationsViewholder(view);
                return viewholder;
            }
        };
        recyclerView_membership_application.setAdapter(adapter);
        adapter.startListening();

    }

    public static class MembershipApplicationsViewholder extends RecyclerView.ViewHolder{
        private TextView textViewMembername, textViewMemberAddress;
        private CircleImageView profile_pix;
        public MembershipApplicationsViewholder(@NonNull View itemView) {
            super(itemView);

            textViewMembername = itemView.findViewById(R.id.member_profile_name);
            textViewMemberAddress = itemView.findViewById(R.id.member_profile_address);
            profile_pix = itemView.findViewById(R.id.member_profile_image);
        }
    }
}
