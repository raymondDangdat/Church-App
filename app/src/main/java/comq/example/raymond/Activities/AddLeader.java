package comq.example.raymond.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewUtils;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import comq.example.raymond.Model.LeadersModel;
import comq.example.raymond.Utils.FirebaseUtils;
import comq.example.raymond.chamber2.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddLeader extends AppCompatActivity {
    private Toolbar toolbar;
    private CircleImageView profilePix;
    private Button btnAddLeader;
    private EditText editTextName, editTextStatus, editTextPosition, editTextServiceYear, editTextPhone;
    private Spinner spinnerPosition, spinnerStatus, spinnerChangeStatus, spinnerChangePosition;
    private static final int GALLERY_REQUEST_CODE = 1;
    private Uri mImageUri = null;
    private LeadersModel getLeaderExtra;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private String mPhone;
    private String mPosition;
    private String mServiceYear;
    private String mStatus;
    private String mName;
    private LeadersModel mNewLeader;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_leader);
        //toolbar
        //initialize our toolBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add Leader");
        initializeViews();
        profilePix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfilePix();
            }
        });
        btnAddLeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLeader();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);

        if (getLeaderExtra == null){
            menu.findItem(R.id.action_edit).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                resetViews();
                displayViews();
            default:
                return true;

        }
    }

    private void resetViews() {
        this.mNewLeader = getLeaderExtra;
        Picasso.get().load(getLeaderExtra.getProfilePix()).placeholder(R.drawable.images).into(profilePix);
        editTextName.setText(getLeaderExtra.getName());
        editTextServiceYear.setText(getLeaderExtra.getServiceYear());
        editTextPhone.setText(getLeaderExtra.getPhone());

    }

    private void displayViews() {

        profilePix.setEnabled(true);
        editTextPosition.setVisibility(View.GONE);
        editTextStatus.setVisibility(View.GONE);

        spinnerChangePosition.setVisibility(View.VISIBLE);
        spinnerChangeStatus.setVisibility(View.VISIBLE);
        btnAddLeader.setVisibility(View.VISIBLE);
        btnAddLeader.setText("Update Info");

        btnAddLeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateLeaderInfo();
            }
        });

    }

    private void UpdateLeaderInfo() {
        final String leaderId = getLeaderExtra.getId();
        mName = editTextName.getText().toString().trim();
        mStatus = spinnerChangeStatus.getSelectedItem().toString();
        mServiceYear = editTextServiceYear.getText().toString().trim();
        mPhone = editTextPhone.getText().toString().trim();
        mPosition = spinnerChangePosition.getSelectedItem().toString();
        if (mPosition.equals("Change Position")){
            Toast.makeText(this, "Please select a valid position to be change.", Toast.LENGTH_SHORT).show();
        }else if (mStatus.equals("Change Status")){
            Toast.makeText(this, "Make a valid selection for the leader's status", Toast.LENGTH_LONG).show();
        }else {
            if (mImageUri == null){
                Toast.makeText(this, "Please select a valid passport of the leader to be added.", Toast.LENGTH_SHORT).show();
            }if (TextUtils.isEmpty(mName) || TextUtils.isEmpty(mStatus) || TextUtils.isEmpty(mServiceYear) || TextUtils.isEmpty(mPhone)){
                Toast.makeText(this, "Please fill all the fields to add a leader.", Toast.LENGTH_SHORT).show();
            }else {
                progressDialog.setMessage("Updating Info...");
                progressDialog.show();
                StorageReference filepath = mStorageReference.child(mImageUri.getLastPathSegment());
                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final String profileUrl = taskSnapshot.getDownloadUrl().toString();

                        mNewLeader = new LeadersModel(mPosition, profileUrl, mName, mStatus, mServiceYear, mPhone);

                        mDatabaseReference.child(leaderId).setValue(mNewLeader).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddLeader.this, "Info Updated", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                startActivity(new Intent(AddLeader.this, ChurchLeadership.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddLeader.this, "Error: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddLeader.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void addLeader() {
        mName = editTextName.getText().toString().trim();
        mStatus = spinnerStatus.getSelectedItem().toString();
        mServiceYear = editTextServiceYear.getText().toString().trim();
        mPhone = editTextPhone.getText().toString().trim();
        mPosition = spinnerPosition.getSelectedItem().toString();
        if (mPosition.equals("Select Position")){
            Toast.makeText(this, "Please select a valid position to be added.", Toast.LENGTH_SHORT).show();
        }else if (mStatus.equals("Select Status")){
            Toast.makeText(this, "Select if the leader is serving or a past leader", Toast.LENGTH_LONG).show();
        }else {
            if (mImageUri == null){
                Toast.makeText(this, "Please select a valid passport of the leader to be added.", Toast.LENGTH_SHORT).show();
            }if (TextUtils.isEmpty(mName) || TextUtils.isEmpty(mStatus) || TextUtils.isEmpty(mServiceYear) || TextUtils.isEmpty(mPhone)){
                Toast.makeText(this, "Please fill all the fields to add a leader.", Toast.LENGTH_SHORT).show();
            }else {
                progressDialog.setMessage("Adding leader...");
                progressDialog.show();
                StorageReference filepath = mStorageReference.child(mImageUri.getLastPathSegment());
                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final String profileUrl = taskSnapshot.getDownloadUrl().toString();

                        mNewLeader = new LeadersModel(mPosition, profileUrl, mName, mStatus, mServiceYear, mPhone);

                        mDatabaseReference.push().setValue(mNewLeader).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddLeader.this, "New leader added!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                startActivity(new Intent(AddLeader.this, ChurchLeadership.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddLeader.this, "Error: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddLeader.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
    private void initializeViews() {
        progressDialog = new ProgressDialog(this);
        FirebaseUtils.openFbReference("leaders");
        mFirebaseDatabase = FirebaseUtils.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtils.mDatabaseReferenace;
        mFirebaseStorage = FirebaseUtils.mFirebaseStorage;
        mStorageReference = FirebaseUtils.mStorageReference;
        spinnerPosition = findViewById(R.id.spinner_position);
        profilePix = findViewById(R.id.imageView_profile);
        btnAddLeader = findViewById(R.id.button_add_leader);
        editTextName = findViewById(R.id.editText_name);
        editTextPhone = findViewById(R.id.editText_phone);
        editTextServiceYear = findViewById(R.id.editText_year);
        editTextStatus = findViewById(R.id.editText_status);
        editTextPosition = findViewById(R.id.editText_position);
        spinnerStatus = findViewById(R.id.spinner_status);
        spinnerChangePosition = findViewById(R.id.spinner_change_position);
        spinnerChangeStatus = findViewById(R.id.change_status);


        Intent intent = getIntent();
        getLeaderExtra = (LeadersModel) intent.getSerializableExtra("Leader");
        if (getLeaderExtra == null){

        }else {
            this.mNewLeader = getLeaderExtra;
            Picasso.get().load(getLeaderExtra.getProfilePix()).placeholder(R.drawable.images).into(profilePix);
            editTextName.setText("Name: " + getLeaderExtra.getName());
            editTextServiceYear.setText("Service Year: " + getLeaderExtra.getServiceYear());
            editTextPhone.setText(getLeaderExtra.getPhone());

            editTextStatus.setVisibility(View.VISIBLE);
            editTextPosition.setVisibility(View.VISIBLE);

            editTextPosition.setText("Position: " + getLeaderExtra.getPosition());
            editTextStatus.setText("Status: " + getLeaderExtra.getStatus());


            spinnerStatus.setVisibility(View.GONE);
            spinnerPosition.setVisibility(View.GONE);
            btnAddLeader.setVisibility(View.GONE);


            profilePix.setEnabled(false);
            getSupportActionBar().setTitle("Update Info");



        }
    }
    private void uploadProfilePix() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(4,4)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                profilePix.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
