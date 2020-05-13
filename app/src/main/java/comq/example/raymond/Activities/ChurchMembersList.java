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

public class ChurchMembersList extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView_members_list;

    private DatabaseReference members;

    private FirebaseRecyclerAdapter<MemberModel, MembersViewholder>adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_church_members_list);

        members = FirebaseDatabase.getInstance().getReference().child("COCINCHAMBER").child("members");

        recyclerView_members_list = findViewById(R.id.recycler_members_list);
        recyclerView_members_list.setLayoutManager(new LinearLayoutManager(this));


        toolbar = findViewById(R.id.members_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Church Members");
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<MemberModel>options = new FirebaseRecyclerOptions.Builder<MemberModel>()
                .setQuery(members.orderByChild("status").equalTo("member"), MemberModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<MemberModel, MembersViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MembersViewholder holder, final int position, @NonNull MemberModel model) {
               holder.textViewMemberAddress.setText(model.getAddress());
               holder.textViewMemberName.setText(model.getName());

                Picasso.get().load(model.getProfilePic()).placeholder(R.drawable.login).into(holder.profile_pix);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //get the user ID of the user clicked on
                        String clicked_user_id = getRef(position).getKey();
                        Intent profileIntent = new Intent(ChurchMembersList.this, MemberProfileActivity.class);
                        profileIntent.putExtra("clicked_user_id", clicked_user_id);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public MembersViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.member_display_layout, viewGroup, false);
                MembersViewholder viewholder = new MembersViewholder(view);
                return viewholder;
            }
        };
        //recyclerView_members_list.smoothScrollToPosition(recyclerView_members_list.getAdapter().getItemCount());
        recyclerView_members_list.setAdapter(adapter);
        adapter.startListening();

    }

    public static class MembersViewholder extends RecyclerView.ViewHolder{
        private TextView textViewMemberName, textViewMemberAddress;
        private CircleImageView profile_pix;
        public MembersViewholder(@NonNull View itemView) {
            super(itemView);

            textViewMemberName = itemView.findViewById(R.id.member_profile_name);
            textViewMemberAddress = itemView.findViewById(R.id.member_profile_address);
            profile_pix = itemView.findViewById(R.id.member_profile_image);
        }
    }
}
