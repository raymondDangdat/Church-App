package comq.example.raymond.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import comq.example.raymond.chamber2.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class UploadProfilePix extends AppCompatActivity {
    private Toolbar uploadProfilePixToolbar;
    private CircleImageView profilePix;
    private Button btnUpload;

    private FirebaseAuth mAuth;
    private DatabaseReference members;
    private static final int GALLERY_REQUEST_CODE = 1;
    private Uri mImageUri = null;

    private ProgressDialog progressDialog;

    private StorageReference mStorageImage;

    private String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pix);


        progressDialog = new ProgressDialog(this);
        members = FirebaseDatabase.getInstance().getReference().child("COCINCHAMBER").child("members");
        mAuth = FirebaseAuth.getInstance();
        mStorageImage = FirebaseStorage.getInstance().getReference().child("COCINCHAMBER").child("profilepix");
        uId = mAuth.getCurrentUser().getUid();

        btnUpload = findViewById(R.id.btn_upload);
        profilePix = findViewById(R.id.profile_pix);

        profilePix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickProfilePix();
            }
        });

        //toolbar
        //initialize our toolBar
        uploadProfilePixToolbar = findViewById(R.id.upload_profile_pix_toolbar);
        setSupportActionBar(uploadProfilePixToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Upload Your Profile Picture");

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfilePic();
            }
        });
    }

    private void uploadProfilePic() {
        if (mImageUri == null){
            Toast.makeText(this, "Please click the image above to select your profile picture", Toast.LENGTH_LONG).show();
        }else {
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            StorageReference filepath = mStorageImage.child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String profile_url = taskSnapshot.getDownloadUrl().toString();
                    members.child(uId).child("profilePic").setValue(profile_url).addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    Intent loginIntent = new Intent(UploadProfilePix.this, MainActivity.class);
                                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(loginIntent);
                                    finish();
                                }
                            }
                    ).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UploadProfilePix.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadProfilePix.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void pickProfilePix() {
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
}
