package comq.example.raymond.Activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import comq.example.raymond.chamber2.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class MemberProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private String applicant_id;
    private String profile, name, address, phone, gender, email;

    private DatabaseReference members;

    private ProgressDialog dialog;

    private CircleImageView applicant_profile_pic;
    private EditText editTextName, editTextEmail, editTextAddress, editTextPhone, editTextGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profile);

        members = FirebaseDatabase.getInstance().getReference().child("COCINCHAMBER").child("members");

        applicant_id = getIntent().getExtras().get("clicked_user_id").toString();

        dialog = new ProgressDialog(this);



        toolbar = findViewById(R.id.member_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Member Details");


        //get views
        applicant_profile_pic = findViewById(R.id.set_profile_image);
        editTextAddress = findViewById(R.id.set_member_address);
        editTextName = findViewById(R.id.set_member_name);
        editTextEmail = findViewById(R.id.set_email);
        editTextPhone = findViewById(R.id.set_member_phone);
        editTextGender = findViewById(R.id.set_gender);

        retrieveApplicantInfo();
    }


    private void retrieveApplicantInfo() {
        members.child(applicant_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("profilePic"))){
                    name = dataSnapshot.child("name").getValue().toString();
                    address = dataSnapshot.child("address").getValue().toString();
                    profile = dataSnapshot.child("profilePic").getValue().toString();
                    email = dataSnapshot.child("email").getValue().toString();
                    phone = dataSnapshot.child("phone").getValue().toString();
                    gender = dataSnapshot.child("gender").getValue().toString();


                    //set values to views
                    Picasso.get().load(profile).placeholder(R.drawable.login).into(applicant_profile_pic);

                    editTextAddress.setText(address);
                    editTextName.setText(name);
                    editTextEmail.setText(email);
                    editTextPhone.setText(phone);
                    editTextGender.setText(gender);
                }else {

                    name = dataSnapshot.child("name").getValue().toString();
                    address = dataSnapshot.child("address").getValue().toString();
                    email = dataSnapshot.child("email").getValue().toString();
                    phone = dataSnapshot.child("phone").getValue().toString();
                    gender = dataSnapshot.child("gender").getValue().toString();

                    editTextAddress.setText(address);
                    editTextName.setText(name);
                    editTextEmail.setText(email);
                    editTextPhone.setText(phone);
                    editTextGender.setText(gender);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
