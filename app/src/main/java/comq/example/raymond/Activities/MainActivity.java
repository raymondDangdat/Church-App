package comq.example.raymond.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import comq.example.raymond.chamber2.R;

public class MainActivity extends AppCompatActivity {
    private Toolbar mainActivityToolbar;
    private Button btn_login, btn_register;
    private Button btn_forgot_password;

    private ProgressDialog progressDialog;
    private DatabaseReference members;
    private FirebaseAuth mAuth;
    private String uId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        members = FirebaseDatabase.getInstance().getReference().child("COCINCHAMBER").child("members");
        mAuth = FirebaseAuth.getInstance();

        //toolbar
        //initialize our toolBar
        mainActivityToolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(mainActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("COCIN LCC Chamber");

        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        btn_forgot_password = findViewById(R.id.btn_forgot_password);

        btn_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordResetDialog();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAlertDialog();
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerDialog();
            }
        });

    }

    private void passwordResetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.password_reset_dialog, null);

        final EditText editTextEmail = view.findViewById(R.id.edit_email);
        Button btn_submit = view.findViewById(R.id.btn_submit);

        //set onclick listener on login button
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Sending password reset mail ");

                String email = editTextEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(MainActivity.this, "Please provide the email you registered with", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.show();
                    mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Password reset successfully, please check your email", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void registerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.registeration_dialog, null);
        final EditText editTextEmail = view.findViewById(R.id.edt_email);
        final EditText editTextFull_name = view.findViewById(R.id.edt_full_name);
        final Spinner spinnerGender = view.findViewById(R.id.gender);
        final EditText editTextPhone = view.findViewById(R.id.edt_phone);
        final EditText editTextAddress = view.findViewById(R.id.edt_address);
        final EditText editTextPassword = view.findViewById(R.id.edt_password);
        final EditText editText_c_password = view.findViewById(R.id.edt_confirm_password);
        //final TextInputEditText
        Button btn_register = view.findViewById(R.id.btn_register);
        //set onclick listener on login button
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String full_name = editTextFull_name.getText().toString().trim();
                final String gender = spinnerGender.getSelectedItem().toString().trim();
                final String phone  = editTextPhone.getText().toString().trim();
                final String email = editTextEmail.getText().toString().trim();
                final String address = editTextAddress.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String cPassword = editText_c_password.getText().toString().trim();

                if (TextUtils.isEmpty(full_name) || TextUtils.isEmpty(phone) ||TextUtils.isEmpty(email)
                        || TextUtils.isEmpty(address) || TextUtils.isEmpty(password) || TextUtils.isEmpty(cPassword)){
                    Toast.makeText(MainActivity.this, "Make sure you fill all fields", Toast.LENGTH_SHORT).show();
                }else if (gender.equals("Select Gender")){
                    Toast.makeText(MainActivity.this, "Please select a valid gender", Toast.LENGTH_SHORT).show();
                }else if (!password.equals(cPassword)){
                    Toast.makeText(MainActivity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                }else {

                    progressDialog.setTitle("Create Account");
                    progressDialog.setMessage("Registering...");
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    uId = mAuth.getCurrentUser().getUid();
                                    members.child(uId).child("email").setValue(email);
                                    members.child(uId).child("address").setValue(address);
                                    members.child(uId).child("name").setValue(full_name);
                                    members.child(uId).child("gender").setValue(gender);
                                    members.child(uId).child("status").setValue("WAITING");
                                    members.child(uId).child("phone").setValue(phone).addOnCompleteListener(
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        progressDialog.dismiss();
                                                        Toast.makeText(MainActivity.this, "Registered successfully, proceed to upload your profile picture", Toast.LENGTH_LONG).show();
                                                        Intent profileIntent = new Intent(MainActivity.this, UploadProfilePix.class);
                                                        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(profileIntent);
                                                        finish();
                                                    }
                                                }
                                            }
                                    ).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                    ).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void loginAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_login, null);


        final EditText editTextEmail = view.findViewById(R.id.edit_email);
        final EditText password = view.findViewById(R.id.edit_password);
        Button btn_login = view.findViewById(R.id.btn_login);

        //set onclick listener on login button
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().trim().isEmpty() || !password.getText().toString().trim().isEmpty()){
                    progressDialog.setTitle("Login");
                    progressDialog.setMessage("Logging in...");
                    progressDialog.show();
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      mAuth.signInWithEmailAndPassword(editTextEmail.getText().toString().trim(), password.getText().toString().trim())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    if (editTextEmail.getText().toString().equals("churchadmin@gmail.com")){
                                        progressDialog.dismiss();
                                        Intent profileIntent = new Intent(MainActivity.this, AdminHome.class);
                                        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(profileIntent);
                                        finish();
                                    }else{
                                        progressDialog.dismiss();
                                        Intent profileIntent = new Intent(MainActivity.this, MemberHome.class);
                                        profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(profileIntent);
                                        finish();
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    Toast.makeText(MainActivity.this, "Sorry, you can't login with empty fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
