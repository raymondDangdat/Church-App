package comq.example.raymond.chamber2;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private Toolbar mainActivityToolbar;
    private Button btn_login, btn_register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //toolbar
        //initialize our toolBar
        mainActivityToolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(mainActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("COCIN LCC Chamber");

        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
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
    private void registerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.registeration_dialog, null);
        final EditText editTextEmail = view.findViewById(R.id.edt_email);
        EditText full_name = view.findViewById(R.id.edt_full_name);
        Spinner gender = view.findViewById(R.id.gender);
        EditText phone = view.findViewById(R.id.edt_phone);
        EditText address = view.findViewById(R.id.edt_address);
        final EditText password = view.findViewById(R.id.edt_password);
        EditText c_password = view.findViewById(R.id.edt_confirm_password);
        Button btn_register = view.findViewById(R.id.btn_register);
        //set onclick listener on login button
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty()){

                }else {
                    //temporarily
                    startActivity(new Intent(MainActivity.this, UploadProfilePix.class));
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
                if (!editTextEmail.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty()){
                    Toast.makeText(MainActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
                }else {
                    startActivity(new Intent(MainActivity.this, AdminHome.class));
                }
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
