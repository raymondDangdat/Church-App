package comq.example.raymond.Utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import comq.example.raymond.Model.Event;
import comq.example.raymond.Model.LeadersModel;

public class FirebaseUtils {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReferenace;
    public static FirebaseStorage mFirebaseStorage;
    public static StorageReference mStorageReference;
    private static FirebaseUtils firebaseUtils;

    public static ArrayList<Event>mEvents;
    public static ArrayList<LeadersModel>mLeadersModels;

    private FirebaseUtils(){}

    public static void openFbReference(String ref){
        if (firebaseUtils == null){
            firebaseUtils = new FirebaseUtils();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseStorage = FirebaseStorage.getInstance();
        }
        mEvents = new ArrayList<Event>();
        mLeadersModels = new ArrayList<LeadersModel>();
        mDatabaseReferenace = mFirebaseDatabase.getReference().child("COCINCHAMBER").child(ref);
        mStorageReference = mFirebaseStorage.getReference().child("COCINCHAMBER").child(ref);

    }
}
