package comq.example.raymond.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import comq.example.raymond.Activities.AddLeader;
import comq.example.raymond.Model.LeadersModel;
import comq.example.raymond.Utils.FirebaseUtils;
import comq.example.raymond.chamber2.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class PastorsAdapter extends RecyclerView.Adapter<PastorsAdapter.PastorsViewHolder> {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    ArrayList<LeadersModel> mArrayleaders;


    public PastorsAdapter(){
        FirebaseUtils.openFbReference("leaders");
        mFirebaseDatabase = FirebaseUtils.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtils.mDatabaseReferenace;

        mArrayleaders = FirebaseUtils.mLeadersModels;

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                LeadersModel leadersModel = dataSnapshot.getValue(LeadersModel.class);
                Log.d("leader: ", leadersModel.getName());
                leadersModel.setId(dataSnapshot.getKey());
                mArrayleaders.add(leadersModel);
                notifyItemInserted(mArrayleaders.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.orderByChild("position").equalTo("Pastor").addChildEventListener(mChildEventListener);
    }

    @NonNull
    @Override
    public PastorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.leaders_layout, parent, false);
        return new PastorsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PastorsViewHolder revViewHolder, int position) {
        LeadersModel leadersModel = mArrayleaders.get(position);
        revViewHolder.bind(leadersModel);
    }

    @Override
    public int getItemCount() {
        return mArrayleaders.size();
    }


    public class PastorsViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        private CircleImageView profilePic;
        private TextView txtName, txtStatus, txtPhone, txtPosition, txtServiceYear;

        public PastorsViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.text_name);
            txtStatus = itemView.findViewById(R.id.text_status);
            txtPosition = itemView.findViewById(R.id.text_position);
            txtPhone = itemView.findViewById(R.id.text_mobile);
            txtServiceYear = itemView.findViewById(R.id.text_from_to);
            profilePic = itemView.findViewById(R.id.profile_pic);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("Click", String.valueOf(position));

            LeadersModel selectedLeader = mArrayleaders.get(position);
            Intent intent = new Intent(view.getContext(), AddLeader.class);
            intent.putExtra("Leader", selectedLeader);
            view.getContext().startActivity(intent);
        }

        public void bind(LeadersModel model){
            txtServiceYear.setText(model.getServiceYear());
            txtName.setText(model.getName());
            txtPosition.setText(model.getPosition());
            txtStatus.setText(model.getStatus());
            txtPhone.setText(model.getPhone());
            Picasso.get().load(model.getProfilePix()).fit().centerCrop().placeholder(R.drawable.logo).into(profilePic);
        }
    }
}
