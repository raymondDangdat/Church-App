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

import java.util.ArrayList;

import comq.example.raymond.Activities.AddEvent;
import comq.example.raymond.Model.Event;
import comq.example.raymond.Utils.FirebaseUtils;
import comq.example.raymond.chamber2.R;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    ArrayList<Event> events;

    public EventAdapter() {
        FirebaseUtils.openFbReference("events");
        mFirebaseDatabase = FirebaseUtils.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtils.mDatabaseReferenace;

        events = FirebaseUtils.mEvents;

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Event event = dataSnapshot.getValue(Event.class);
                Log.d("event: ", event.getEventTitle());
                event.setId(dataSnapshot.getKey());
                events.add(event);
                notifyItemInserted(events.size() - 1);



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
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.upcoming_events_layout, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int position) {
        Event event = events.get(position);
        eventViewHolder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView eventTitle, eventDescription, eventVenue, eventDate, eventTime;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            eventTitle = itemView.findViewById(R.id.text_event_title);
            eventDescription = itemView.findViewById(R.id.text_event_description);
            eventDate = itemView.findViewById(R.id.text_even_date);
            eventVenue = itemView.findViewById(R.id.text_event_venue);
            eventTime = itemView.findViewById(R.id.text_event_time);

            itemView.setOnClickListener(this);
        }

        public void bind(Event event){
            eventTitle.setText(event.getEventTitle());
            eventDescription.setText(event.getEventDescription());
            eventDate.setText(event.getEventDate());
            eventTime.setText(event.getEventTime());
            eventVenue.setText(event.getEventVenue());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d("Click", String.valueOf(position));

            Event selectedEvent = events.get(position);
            Intent intent = new Intent(view.getContext(), AddEvent.class);
            intent.putExtra("Event", selectedEvent);
            view.getContext().startActivity(intent);
        }
    }

}
