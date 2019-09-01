package me.sankalpchauhan.dscmait.Base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import me.sankalpchauhan.dscmait.Callbacks.amountCallback;
import me.sankalpchauhan.dscmait.Callbacks.eventsCallback;
import me.sankalpchauhan.dscmait.Callbacks.firebaseCallback;
import me.sankalpchauhan.dscmait.Callbacks.keyCallback;
import me.sankalpchauhan.dscmait.Callbacks.mobileCallback;
import me.sankalpchauhan.dscmait.Callbacks.announcementCallback;
import me.sankalpchauhan.dscmait.Callbacks.roomCallback;
import me.sankalpchauhan.dscmait.Callbacks.typeCallback;
import me.sankalpchauhan.dscmait.Callbacks.userExistsCallback;

public class dbHelper<T> extends AppCompatActivity {

    FirebaseFirestore database = FirebaseFirestore.getInstance();
    HashMap<String, String> userDetails = new HashMap<>();

    AVLoadingIndicatorView avi;
    ProgressDialog pd;


    //private CollectionReference eventRef = database.collection("events");


    //TO add a info to database
    public void AddDatatoDB(HashMap<T,T> user, String collectionName, String documentName, Context context, String SuccessMessage){
        // Create progress dialog
        pd = new ProgressDialog(context);
        pd.setMessage("Submitting");

        pd.show();

        // Add a new document with a generated ID
        database.collection(collectionName).document(documentName).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.w(context.getClass().getName(), "SettingSuccessful");
                        Toast.makeText(context, SuccessMessage, Toast.LENGTH_SHORT).show();
                        pd.hide();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(context.getClass().getName(), "Error setting documents.");
                        Toast.makeText(context, "Error!!: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.hide();
                    }
                });
    }

    //get All data from the database
    public void GetAllDataFromDB(Context context) {
        database.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(context.getClass().getName(), document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(context.getClass().getName(), "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void updateDataForUser(String collectionName, String documentName, String key, String value, int id){

        switch (id) {
            case 0:  database.collection(collectionName).document(documentName).update(key,value);
        }
    }



    public void getDataFromFirestore(Activity activity, String collectionName, String documentName, Context context, firebaseCallback firebasecallback){
        HashMap<String, String> userMap = new HashMap<>();
        database.collection(collectionName).document(documentName).addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(context, "Error while loading!", Toast.LENGTH_SHORT).show();
                    Log.d("MainScreenActivity", e.toString());
                    return;
                }

                if (documentSnapshot.exists()) {
                    userMap.put("Name", documentSnapshot.getString("Name"));
                    userMap.put("LastName", documentSnapshot.getString("LastName"));
                    userMap.put("Email", documentSnapshot.getString("Email"));
                    userMap.put("Password", documentSnapshot.getString("Password"));
                    userMap.put("Logged In", documentSnapshot.getString("Logged In"));
                    userMap.put("Mobile", documentSnapshot.getString("Mobile"));
                    userMap.put("EmailVerified", documentSnapshot.getString("EmailVerified"));
                    //if(documentSnapshot.getString("Interests").trim().isEmpty()) {
                        userMap.put("Interests", documentSnapshot.getString("Interests"));
                    //}
                    userMap.put("Rating", documentSnapshot.getString("Rating"));
                    firebasecallback.onCallback(userMap);

                }
            }
        });

    }

    //TODO: Implement this method here instead of events activity

    public void getRegUserMapinDB(Activity activity, String collectionName, String documentName, Context context, eventsCallback eventscallback){

        database.collection(collectionName).document(documentName).addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(context, "Error while loading!", Toast.LENGTH_SHORT).show();
                    Log.d("MainScreenActivity", e.toString());
                    return;
                }

                if (documentSnapshot.exists()) {

                    Map<String, Object> regusers = (HashMap<String, Object>) documentSnapshot.getData().get("RegisteredUsers");

                    eventscallback.onCallback(regusers);


                }
            }
        });
    }

    public void getAPIkeys(Activity activity, String collectionName, String documentName, Context context, keyCallback keycallback){
        database.collection(collectionName).document(documentName).addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Toast.makeText(context, "Error while loading!", Toast.LENGTH_SHORT).show();
                    Log.d("MainScreenActivity", e.toString());
                    return;
                }

                if(documentSnapshot.exists()){
                    Map<String, Object> keysMap = (HashMap<String, Object>) documentSnapshot.getData();
                    //Log.e("CHECK2", String.valueOf(keysMap.get("MID")));
                    keycallback.onCallback(keysMap);

                }
            }
        });
    }

    public void upDatePhoneNumber(Context context, String collectionName, String documentName, String key, String value){
        database.collection(collectionName).document(documentName).update(key, value).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Phone Number Registered", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "ERROR!! Phone Number Not Registered", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getUserPhoneNumber(Context context, String collectionName, String documentName, String key, mobileCallback mobilecallback){
        database.collection(collectionName).document(documentName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userMobile = documentSnapshot.getString(key);
                mobilecallback.onCallback(userMobile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("getPhoneNumberFAIL", "NO DATA RECIEVED");
            }
        });
    }

    public void addUsertoEvent(Context context, String collectionName, String documentName, Object key){
        Map<String, Object> userMapUpdate = new HashMap<>();
        userMapUpdate.put("RegisteredUsers", key);
        database.collection(collectionName).document(documentName).set(userMapUpdate, SetOptions.merge());
    }

    public void getRoomIDAndPassword(Activity activity, String collectionName, String documentName, Context context, roomCallback roomcallback){
        database.collection(collectionName).document(documentName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> passMap = (HashMap<String, Object>) documentSnapshot.getData();
                roomcallback.onCallback(passMap);

            }
        });
    }

    // Not implemented here it is in feedback bottom sheet

//    public void sendFeedback(String documentName, Map<String, Object> feedbackParams, Context context){
//        database.collection("feedback").document(documentName).set(feedbackParams)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(context, "Thanks For Feedback", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(context, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

    //Getting Amount Sensitive Operation Requires it's own method separate from user

    public void getUserAmount(Activity activity, String documentName, amountCallback amountCallback){

        database.collection("users").document(documentName).addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.d("MainScreenActivity", e.toString());
                    return;
                }

                if(documentSnapshot.exists()){
                    if(documentSnapshot.get("Amount")!=null) {
                        amountCallback.onCallback(documentSnapshot.get("Amount"));
                        Log.e("AMOUNTSENSITIVE", (String) documentSnapshot.get("Amount"));
                    }
                }
            }
        });

    }

    public void getAccountType(String documentName, typeCallback typecallback){
        database.collection("users").document(documentName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.get("Type")!=null) {
                    typecallback.onCallback((String) documentSnapshot.get("Type"));
                    Log.e("ADMINCHECK", documentSnapshot.get("Type").toString());
                }
                else{
                    Log.e("ADMINCHECK", "Not a Admin");
                }
            }
        });
    }

    public void getEventResult(String documentName, announcementCallback resultcallback){
        database.collection("events").document(documentName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.get("Announcement")!=null) {
                    resultcallback.onCallback((String) documentSnapshot.get("Announcement"));
                }
            }
        });
    }


    //On Name Change Request
    public void moveDBuserDocument(String collectionName, String documentName, final String newdocumentame) {
        database.collection(collectionName).document(documentName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        database.collection(collectionName).document(newdocumentame).set(document.getData())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Success Data Copy", "DocumentSnapshot successfully written!");
                                        database.collection(collectionName).document(documentName).delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("Success Data Copy", "DocumentSnapshot successfully deleted!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("Failiure Data Copy", "Error deleting document", e);
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Failiure Data Copy", "Error writing document", e);
                                    }
                                });
                    } else {
                        Log.d("Failiure Data Copy", "No such document");
                    }
                } else {
                    Log.d("Failiure Data Copy", "get failed with ", task.getException());
                }
            }
        });
    }

    public void doUserExist(String collectionName, String documentName, Context context, userExistsCallback userexistscallback){

        DocumentReference docIdRef = database.collection(collectionName).document(documentName);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userexistscallback.onCallback(true);
                    } else {
                        userexistscallback.onCallback(false);
                    }
                } else {
                    Log.d("FIRESTORE", "Failed with: ", task.getException());
                }
            }
        });
    }


    public void withdrawalRequest(Map<String, Object> withdrawmap){
        database.collection("withdrawals").document().set(withdrawmap);
    }






//    public void getRegUserMapinDB(Activity activity, String collectionName, String documentName, Context context, eventsCallback eventscallback){
//
//
//        database.collection(collectionName).document(documentName).addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Toast.makeText(context, "Error while loading!", Toast.LENGTH_SHORT).show();
//                    Log.d("MainScreenActivity", e.toString());
//                    return;
//                }
//
//                if (documentSnapshot.exists()) {
//
//                    Map<String, Object> regusers = new HashMap<>();
//                     regusers= (HashMap<String, Object>) documentSnapshot.getData().get("RegisteredUsers");
//
//                    for(Map.Entry<String, Object> entry : regusers.entrySet()) {
//                        Log.e("CHECKER","Key = " + entry.getKey() + ", Value = " + entry.getValue());
//                    }
//
//                    eventscallback.onCallback(regusers);
//
//
//                }
//            }
//        });
//    }





//    public void setUpRecyclerView(Context context, EventAdapter adapter, int id){
//        //This might create a problem
//        Query query = eventRef.orderBy("Time", Query.Direction.DESCENDING);
//        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
//                .setQuery(query, Event.class)
//                .build();
//
//        adapter = new EventAdapter(options);
//
//        RecyclerView recyclerView = findViewById(id);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        recyclerView.setAdapter(adapter);
//
//    }


//    public void loadUser(Context context, String collectionName,String documentName) {
//       database.collection(collectionName).document(documentName).get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if (documentSnapshot.exists()) {
//                           // String title = documentSnapshot.getString(KEY_TITLE);
//                           // String description = documentSnapshot.getString(KEY_DESCRIPTION);
//                            userDetails.put("Name", documentSnapshot.getString("Name"));
//                            userDetails.put("GamerID", documentSnapshot.getString("GamerID"));
//                            userDetails.put("Email", documentSnapshot.getString("Email"));
//                            userDetails.put("Password", documentSnapshot.getString("Password"));
//                            userDetails.put("Logged In", documentSnapshot.getString("Logged In"));
//                            userDetails.put("Mobile", documentSnapshot.getString("Mobile"));
//                            userDetails.put("EmailVerified", documentSnapshot.getString("EmailVerified"));
//
//                            //Map<String, Object> note = documentSnapshot.getData();
//
//                            //textViewData.setText("Title: " + title + "\n" + "Description: " + description);
//                        } else {
//                            Toast.makeText(context, "Document does not exist", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
//                        Log.d("TAG", e.toString());
//                    }
//                });
//    }


}
