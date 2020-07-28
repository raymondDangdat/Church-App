package comq.example.raymond.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import comq.example.raymond.Model.Event;
import comq.example.raymond.Utils.FirebaseUtils;
import comq.example.raymond.chamber2.R;

public class AddEvent extends AppCompatActivity {
    private Toolbar toolbar;

    private TextView textViewDate, textEventTime;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private EditText editTextTitle, editTextDescription, editTextVenue, editTextEventDate, editTextEventTime;
    private Button btnPublish;

    int hour, minute;
    Event event;
    private String mTitle;
    private String mDescription;
    private String mVenue;
    private String mDate;
    private String mTime;

    private Event eventExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        FirebaseUtils.openFbReference("events");
        mFirebaseDatabase = FirebaseUtils.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtils.mDatabaseReferenace;


        textViewDate = findViewById(R.id.textView_date);
        textEventTime = findViewById(R.id.text_event_time);
        editTextDescription = findViewById(R.id.editText_description);
        editTextTitle = findViewById(R.id.editText_title);
        editTextVenue = findViewById(R.id.editText_venue);
        btnPublish = findViewById(R.id.button_publish);
        editTextEventDate = findViewById(R.id.editText_event_date);
        editTextEventTime = findViewById(R.id.editText_event_time);
        //toolbar
        //initialize our toolBar
        toolbar = findViewById(R.id.add_event_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add Event");

        Intent intent = getIntent();
        eventExtra = (Event) intent.getSerializableExtra("Event");
        if (eventExtra == null){
            editTextEventDate.setVisibility(View.GONE);
            editTextEventTime.setVisibility(View.GONE);
            //eventExtra = new Event();

        }else {
            this.event = eventExtra;
            editTextTitle.setText(eventExtra.getEventTitle());
            editTextDescription.setText(eventExtra.getEventDescription());
            editTextEventTime.setVisibility(View.VISIBLE);
            editTextEventDate.setVisibility(View.VISIBLE);
            editTextEventTime.setText(eventExtra.getEventTime());
            editTextEventDate.setText(eventExtra.getEventDate());
            editTextVenue.setText(eventExtra.getEventVenue());

            textEventTime.setVisibility(View.GONE);
            textViewDate.setVisibility(View.GONE);
            btnPublish.setVisibility(View.GONE);

            editTextEventTime.setEnabled(false);
            editTextEventDate.setEnabled(false);
            editTextTitle.setEnabled(false);
            editTextDescription.setEnabled(false);
            editTextVenue.setEnabled(false);

        }



        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddEvent.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day
                );
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                editTextEventDate.setVisibility(View.VISIBLE);
                editTextEventDate.setText(DateWorker.processDate(dayOfMonth, month, year));
                editTextEventDate.setEnabled(false);
                textViewDate.setText("Change Date");
            }
        };

        textEventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initialize time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddEvent.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                //initialize hour and minute
                                hour = hourOfDay;
                                minute = minute;
                                //initialize calender
                                Calendar calendar = Calendar.getInstance();
                                //set hour and minute
                                calendar.set(0,0,0, hour, minute);
                                //set selected time on textview
                                editTextEventTime.setVisibility(View.VISIBLE);
                                editTextEventTime.setText(DateFormat.format("hh:mm aa", calendar));
                                editTextEventTime.setEnabled(false);
                                textEventTime.setText("Change Time");

                            }
                        },12,0,false
                );
                //display previous selected time
                timePickerDialog.updateTime(hour, minute);
                //show dialog
                timePickerDialog.show();
            }
        });

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitle = editTextTitle.getText().toString().trim();
                mDescription = editTextDescription.getText().toString().trim();
                mVenue = editTextVenue.getText().toString().trim();
                mDate = editTextEventDate.getText().toString().trim();
                mTime = editTextEventTime.getText().toString().trim();

                if (TextUtils.isEmpty(mTitle)){
                    Toast.makeText(AddEvent.this, "Please type a valid title for the event", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(mDescription)){
                    Toast.makeText(AddEvent.this, "Please give a valid description of the event", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(mVenue)){
                    Toast.makeText(AddEvent.this, "Please give a valid venue for the event", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(mDate)){
                    Toast.makeText(AddEvent.this, "Choose a valid date for the event.", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(mTime)){
                    Toast.makeText(AddEvent.this, "Choose a valid time for the event.", Toast.LENGTH_SHORT).show();
                }else {
                    publishEvent();
                    Toast.makeText(AddEvent.this, "Event Published", Toast.LENGTH_LONG).show();
                    clean();
                    backToEventList();
                }
            }
        });
    }

    private void clean() {
       editTextTitle.setText("");
       editTextVenue.setText("");
       editTextDescription.setText("");

        editTextTitle.requestFocus();
        editTextEventDate.setVisibility(View.GONE);
        editTextEventTime.setVisibility(View.GONE);

    }

    private void publishEvent() {
//        event.setEventTitle(editTextTitle.getText().toString());
//        event.setEventDescription(editTextDescription.getText().toString());
//        event.setEventDate(editTextEventDate.getText().toString());
//        event.setEventVenue(editTextVenue.getText().toString());
//        event.setEventTime(editTextEventTime.getText().toString());

//            if (event.getId() == null){
//                mDatabaseReference.push().setValue(event);
//            }else {
//                mDatabaseReference.child(event.getId()).setValue(event);
//            }
        Event newEvent =new Event(mTitle, mDescription, mVenue, mTime, mDate);
        mDatabaseReference.push().setValue(newEvent);

    }

    private void editEvent(){
        this.event = eventExtra;
        editTextTitle.setText(eventExtra.getEventTitle());
        editTextDescription.setText(eventExtra.getEventDescription());
        editTextEventTime.setVisibility(View.VISIBLE);
        editTextEventDate.setVisibility(View.VISIBLE);
        editTextEventTime.setText(eventExtra.getEventTime());
        editTextEventDate.setText(eventExtra.getEventDate());
        editTextVenue.setText(eventExtra.getEventVenue());
        ;

//        textEventTime.setVisibility(View.GONE);
//        textViewDate.setVisibility(View.GONE);
//
//        editTextEventTime.setEnabled(false);
//        editTextEventDate.setEnabled(false);
//        Event edittedEvent = new Event(mTitle, mDescription, mVenue, mTime, mDate);
//        mDatabaseReference.child(event.getId()).setValue(edittedEvent);
    }

    private void deleteEvent(){
        if (event == null){
            Toast.makeText(this, "Please save the event before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabaseReference.child(event.getId()).removeValue();
    }

    private void backToEventList(){
        Intent intent = new Intent(this, UpcomingEventsList.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);

        if (eventExtra == null){
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        else {
            getSupportActionBar().setTitle("Event Detail");
            menu.findItem(R.id.action_delete).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
//            case R.id.action_edit:
//                btnPublish.setText("Update Event");
//                editEvent();
//                //Toast.makeText(this, "Edit in progress", Toast.LENGTH_SHORT).show();
////                clean();
////                backToEventList();
            case R.id.action_delete:
                deleteEvent();
                Toast.makeText(this, "Event deleted!", Toast.LENGTH_SHORT).show();
                backToEventList();
            default:
                return true;

        }
    }
}
