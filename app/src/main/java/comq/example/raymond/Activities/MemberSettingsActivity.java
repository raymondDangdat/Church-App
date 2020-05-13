package comq.example.raymond.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import comq.example.raymond.chamber2.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class MemberSettingsActivity extends AppCompatActivity {
    private Toolbar memberSettingsToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference members;

    private String uId, userEmail = "";
    private static final int GALLERY_REQUEST_CODE = 1;
    private Uri mImageUri = null;

    private StorageReference mStorageImage;

    private ProgressDialog settingsDialog;

    private EditText editTextName, editTextEmail, editTextPhone, editTextAddress;
    private CircleImageView profilePix;

    private Button buttonUpdate, buttonChangePassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_settings);
        //toolbar
        //initialize our toolBar
        memberSettingsToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(memberSettingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile Settings");

        mAuth = FirebaseAuth.getInstance();
        mStorageImage = FirebaseStorage.getInstance().getReference().child("COCINCHAMBER").child("profilepix");
        members = FirebaseDatabase.getInstance().getReference().child("COCINCHAMBER").child("members");

        uId  = mAuth.getCurrentUser().getUid();

        editTextAddress = findViewById(R.id.edt_address);
        editTextEmail = findViewById(R.id.edt_email);
        editTextName = findViewById(R.id.edt_full_name);
        editTextPhone = findViewById(R.id.edt_phone);
        buttonUpdate = findViewById(R.id.btn_update_profile);
        profilePix = findViewById(R.id.profile_pix);
        buttonChangePassword = findViewById(R.id.btn_changepassword);

        settingsDialog =new ProgressDialog(this);

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        profilePix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfilePix();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfilePix();
            }
        });

        getUserDetails();

    }

    private void changePassword() {
        userEmail = mAuth.getCurrentUser().getEmail();
        settingsDialog.setMessage("Sending password reset link to " + userEmail);
        settingsDialog.show();

        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    settingsDialog.dismiss();
                    Toast.makeText(MemberSettingsActivity.this, "Reset email sent to "+ userEmail, Toast.LENGTH_SHORT).show();
                    //logout
                    Intent signIn = new Intent(MemberSettingsActivity.this, MainActivity.class);
                    signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mAuth.signOut();
                    startActivity(signIn);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                settingsDialog.dismiss();
                Toast.makeText(MemberSettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MemberSettingsActivity.this, MemberHome.class));
                finish();

            }
        });
    }

    private void updateProfilePix() {
        if (mImageUri != null){
            final String address = editTextAddress.getText().toString().trim();
            final String phone = editTextPhone.getText().toString().trim();
            settingsDialog.setMessage("Updating profile picture...");
            settingsDialog.show();
            StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String profile_url = taskSnapshot.getDownloadUrl().toString();

                    members.child(uId).child("address").setValue(address);
                    members.child(uId).child("phone").setValue(phone);
                    members.child(uId).child("profilePic").setValue(profile_url).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    settingsDialog.dismiss();
                                    Toast.makeText(MemberSettingsActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MemberSettingsActivity.this, MemberHome.class));
                                    finish();
                                }
                            }
                    ).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            settingsDialog.dismiss();
                            Toast.makeText(MemberSettingsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MemberSettingsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            final String address = editTextAddress.getText().toString().trim();
            final String phone = editTextPhone.getText().toString().trim();
            if (TextUtils.isEmpty(address) || TextUtils.isEmpty(phone)){
                Toast.makeText(this, "Please include your phone number and address", Toast.LENGTH_SHORT).show();
            }else {
                settingsDialog.setMessage("Updating info...");
                settingsDialog.show();

                members.child(uId).child("phone").setValue(phone);
                members.child(uId).child("address").setValue(address).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        settingsDialog.dismiss();
                        Toast.makeText(MemberSettingsActivity.this, "Info updated!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MemberSettingsActivity.this, MemberHome.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MemberSettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }


        }
    }

    private void getUserDetails() {
        members.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String name_retrieved = dataSnapshot.child(uId).child("name").getValue(String.class);
                final String email_retrieved = dataSnapshot.child(uId).child("email").getValue(String.class);
                final String phone_retrieved = dataSnapshot.child(uId).child("phone").getValue(String.class);
                final String profile_pix_data = dataSnapshot.child(uId).child("profilePic").getValue(String.class);
                final String address = dataSnapshot.child(uId).child("address").getValue(String.class);

                editTextPhone.setText(phone_retrieved);
                editTextName.setText(name_retrieved);
                editTextEmail.setText(email_retrieved);
                editTextEmail.setEnabled(false);
                editTextAddress.setText(address);
                Picasso.get().load(profile_pix_data).placeholder(R.drawable.images).into(profilePix);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                    //to set it to square
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
