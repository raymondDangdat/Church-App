package comq.example.raymond.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import comq.example.raymond.Model.HistoryModel;
import comq.example.raymond.Utils.ChurchUtility;
import comq.example.raymond.chamber2.R;

public class EditChurchHistory extends AppCompatActivity {
    private Toolbar toolbar;

    private DatabaseReference history;
    private String historyId = "";

    private TextView txt_date_updated;
    private EditText editTextHistory;
    private Button btnUpdateHistory;

    private String retrievedChurchHistory = "";

    private HistoryModel updatedHistory;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_church_history);

        history = FirebaseDatabase.getInstance().getReference().child("COCINCHAMBER").child("history");
        historyId = "-Lzi95jViTMp44fwcHla";

        dialog = new ProgressDialog(this);

        toolbar = findViewById(R.id.edit_church_history_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit and Update History");

        txt_date_updated = findViewById(R.id.txt_date_updated);
        editTextHistory = findViewById(R.id.editText_history);
        btnUpdateHistory = findViewById(R.id.btn_update_history);
    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveHistory();
    }

    private void retrieveHistory() {
        history.child(historyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String dateUpdated = dataSnapshot.child("dateUpdated").getValue().toString();
                    retrievedChurchHistory = dataSnapshot.child("history").getValue().toString();

                    long date = Long.parseLong(dateUpdated);

                    editTextHistory.setText(retrievedChurchHistory);
                    txt_date_updated.setText("Last Updated " +ChurchUtility.dateFromLong(date));

                    btnUpdateHistory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateChurchHistory();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateChurchHistory() {
        String updatedChurchHistory = editTextHistory.getText().toString();
        final long dateUpdated = new Date().getTime();

        if (TextUtils.isEmpty(updatedChurchHistory)){
            Toast.makeText(this, "Please Type a valid church history", Toast.LENGTH_SHORT).show();
        }else if (updatedChurchHistory.equals(retrievedChurchHistory)){
            Toast.makeText(this, "No changes made!", Toast.LENGTH_SHORT).show();
        }else {

            updatedHistory = new HistoryModel(updatedChurchHistory, dateUpdated);
            history.child(historyId).child("history").setValue(updatedChurchHistory);
            history.child(historyId).child("dateUpdated").setValue(dateUpdated).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(EditChurchHistory.this, "Church History updated!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EditChurchHistory.this, ChurchHistoryActivity.class));
                            }
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditChurchHistory.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
