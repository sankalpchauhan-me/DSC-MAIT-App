package me.sankalpchauhan.dscmait;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import me.sankalpchauhan.dscmait.Base.BaseActivity;
import me.sankalpchauhan.dscmait.Base.dbHelper;
import me.sankalpchauhan.dscmait.Callbacks.firebaseCallback;
import me.sankalpchauhan.dscmait.Callbacks.keyCallback;
import me.sankalpchauhan.dscmait.Callbacks.mobileCallback;
import me.sankalpchauhan.dscmait.Callbacks.typeCallback;
import me.sankalpchauhan.dscmait.Model.Event;
import me.sankalpchauhan.dscmait.adapters.EventAdapter;
import me.sankalpchauhan.dscmait.fragments.FeedbackBottomSheet;

public class MainScreenActivity extends BaseActivity {
    private static final String TAG = "MainScreenActivity";
    //Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    // CollectionReference eventRef = database.collection("event");

    private EventAdapter adapter;

    //DataBase
    dbHelper<String> dbhelper;

    //user
    HashMap<String, String> userMap;

    //ID
    TextView ID;

    //user
    String email;
    String name = "Check Internet";
    String lastname = "";
    IProfile userProfileDrawer;

    AccountHeader headerResult;

    CoordinatorLayout parentLayout;

    Date currentTime;

    private ArrayList<HashMap<String, String>> regUserData;

    String isAdmin;

    RecyclerView recyclerView;

    Integer versionCode;

    //Check Version
    String MinimumVersionToRunApp;
    String GoodVersion;
    String UpdateLink = "https://dscmait.page.link/Get";

    AVLoadingIndicatorView avi;

    private static ShimmerFrameLayout mShimmerViewContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        email = mAuth.getCurrentUser().getEmail();

        dbhelper = new dbHelper<>();
        avi = findViewById(R.id.avimain);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = token.toString();
                        Log.e(TAG, msg);
                       // Toast.makeText(MainScreenActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });




        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (android.os.Build.VERSION.SDK_INT >= 28) {
                versionCode = (int) pInfo.getLongVersionCode(); // avoid huge version numbers and you will be ok
            } else {
                //noinspection deprecation
                versionCode = pInfo.versionCode;
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Log.e("TAG", "" + versionCode);



        setUpRecyclerView();
        adapter.startListening();

                userProfileDrawer = new ProfileDrawerItem().withIdentifier(0).withName(name).withEmail(email).withIcon(getResources().getDrawable(R.drawable.logo));

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.loginbackground)
                .addProfiles(
                            userProfileDrawer
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Profile");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName("Info");
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName("Team");



        new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        item3
//                        new SecondaryDrawerItem().withName("Settings"),
//                        item3
                ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position){
                            case 1: Intent i = new Intent(MainScreenActivity.this, ProfileActivity.class);
                                    startActivity(i);
                                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);


                            break;
                            case 3: Intent i1 = new Intent(MainScreenActivity.this, TeamActivity.class);
                                startActivity(i1);
                                break;
//                            case 4: StandardToast(MainScreenActivity.this, "unimplemented");
//                                break;
//                            case 5: StandardToast(MainScreenActivity.this, "unimplemented");
//                                break;


                        }

                        return false;
                    }
                })
                .build();


        //Check for email verfication
        if (!mAuth.getCurrentUser().isEmailVerified()) {
            sendEmailVerification();
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setMessage("You have been sent an email verification on the registered account, please verify to continue")
                    .setCancelable(false)
                    .setPositiveButton("Verify Now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ProperSignOut(FirebaseAuth.getInstance(), mAuth.getCurrentUser().getEmail());
                            startGmail();

                        }
                    })
                    .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DisplayDialog(MainScreenActivity.this, " ", "Sign In again after verification", "OK");
                            ProperSignOut(FirebaseAuth.getInstance(), mAuth.getCurrentUser().getEmail());
                        }
                    });
            androidx.appcompat.app.AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            //Update verified value in database
            dbhelper.updateDataForUser("users", mAuth.getCurrentUser().getEmail(),"EmailVerified", "Yes", 0);

        }

         dbhelper.updateDataForUser("users", mAuth.getCurrentUser().getEmail(),"Logged In", "Yes", 0);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_feedback);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Want to provide Feedback?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FeedbackBottomSheet feedback = new FeedbackBottomSheet();
                                feedback.show(getSupportFragmentManager(), "feedbackfragment");
                            }
                        }).show();
            }
        });
        parentLayout = findViewById(R.id.cordinatorMain);

        if(!isConnected(this)){
            Snackbar snackbar = Snackbar.make(parentLayout, "No Internet", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", null);
            snackbar.show();
        }


        //Admin Check
        dbhelper.getAccountType(mAuth.getCurrentUser().getEmail(), new typeCallback() {
            @Override
            public void onCallback(String type) {
                if(type!=null){
                    isAdmin = type;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_settings: ProperSignOut(FirebaseAuth.getInstance(), mAuth.getCurrentUser().getEmail());
                break;
            case R.id.updatePhoneUser: phonePrompt(this, new mobileCallback() {
                @Override
                public void onCallback(String mobile) {
                    //userUpdatephone = mobile;
                    dbhelper.upDatePhoneNumber(MainScreenActivity.this,"users", mAuth.getCurrentUser().getEmail(), "Mobile", mobile);
                }

                @Override
                public void onNotCallback() {
                    //Do Nothing
                }
            });
                break;

            case R.id.updateEmailUser: StandardToast(MainScreenActivity.this, "Will implement in future update");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStart() {
        super.onStart();

        dbhelper.getDataFromFirestore(this,"users",mAuth.getCurrentUser().getEmail(),this,new firebaseCallback() {
            @Override
            public void onCallback(HashMap<String, String> value) {
                saveDataToSharedPrefs("userDetails", value);
                name = value.get("Name");
                lastname = value.get("LastName");
                //   Log.e("SUCCESS", name);


                userProfileDrawer.withName(name+" "+lastname);
                headerResult.updateProfile(userProfileDrawer);
            }
        });

        headerResult.updateProfile(userProfileDrawer);

        //Fetch Versions of the app
        dbhelper.getAPIkeys(this, "keys", "secrets", this, new keyCallback() {
            @Override
            public void onCallback(Map<String, Object> keyMap) {
                if(keyMap !=null) {
                    MinimumVersionToRunApp = String.valueOf(keyMap.get("RequiredVersion"));
                    GoodVersion = String.valueOf(keyMap.get("LatestVersion"));
                    UpdateLink = String.valueOf(keyMap.get("UpdateLink"));
                    Log.e("HELLOBRO", String.valueOf(MinimumVersionToRunApp));
                    versionCheck(Integer.parseInt(MinimumVersionToRunApp), Integer.parseInt(GoodVersion));


                }
                else{
                    Log.e("HELLOBRO", "Version Is Null");
                }


            }
        });



    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isConnected(this)){
            Snackbar snackbar = Snackbar.make(parentLayout, "No Internet", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
            snackbar.show();
        }
    }


    private void sendEmailVerification(){
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        //findViewById(R.id.verifyEmailButton).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(MainScreenActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("RegisterActivity", "sendEmailVerification", task.getException());
                            Toast.makeText(MainScreenActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    //start gmail app or otherwise
    void startGmail() {

        String[] parts = email.split("@");

        Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
        if (intent != null && email.contains("gmail.com")) {
            // We found the activity now start the activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            // Go to browser
            String url = "http://www." + parts[1];
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }


    public void versionCheck(Integer MinimumVersionToRunApp, Integer GoodVersion){
        if(MinimumVersionToRunApp!=null && GoodVersion!=null) {

            if(versionCode<GoodVersion){
                Snackbar snackbar = Snackbar.make(parentLayout, "You are running an old version of the app", Snackbar.LENGTH_LONG);
                snackbar.setAction("Update", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = UpdateLink;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.urgent));
                snackbar.setActionTextColor(getResources().getColor(R.color.material_drawer_primary_light));
                snackbar.show();
            }

            if (versionCode < MinimumVersionToRunApp) {
                AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);

                builder.setTitle("Update App")
                        .setMessage("You are running an app version that is too old and is missing some necessary features. The app won't run without an update")
                        .setCancelable(false)
                        .setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String url = UpdateLink;
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                                ProperSignOut(mAuth, email);
                                finishAndRemoveTask();

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }


        }
        else{
            Log.e("HELLOBROINT", "NOT GETTING VALUe");
        }
    }


    public void setUpRecyclerView() {
        Query query = database.collection("events").orderBy("EventTime", Query.Direction.ASCENDING);


        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();



        adapter = new EventAdapter(options);
        //Animtions
        SlideInLeftAnimationAdapter alphaAdapter = new SlideInLeftAnimationAdapter(adapter);
        alphaAdapter.setDuration(400);
        //alphaAdapter.setFirstOnly(false);           //Sets the animation when going up also

        RecyclerView recyclerView = findViewById(R.id.eventRecyclerView);

        //Animations
        recyclerView.setItemAnimator(new LandingAnimator());
        recyclerView.getItemAnimator().setAddDuration(500);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(alphaAdapter);
        avi.hide();

        adapter.setOnItemClickListner(new EventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Event event = documentSnapshot.toObject(Event.class);
                String id = documentSnapshot.getId();

                //Since document Snapshots are non-serilizable they cannot be sent to an intent so we convert it to string and will send the string
                String path = documentSnapshot.getReference().getPath();
                //{Returns something like ("events/{$documentID}") which can be used in next activity
                Map<String, Object> eventDetail =  documentSnapshot.getData();
                Log.e("TimeStamp2", eventDetail.get("EventTime").toString());

                //Toast.makeText(MainScreenActivity.this, "Position: " + position + "ID: " + id, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainScreenActivity.this, EventActivity.class);
                i.putExtra("isAdmin", isAdmin);
                i.putExtra("FirestorePath", path);
                i.putExtra("EventMap", (Serializable) eventDetail);
                startActivity(i);
            }
        });
    }


    //Method to be passed to any other activity
    public static void shimmering(){
        mShimmerViewContainer.stopShimmer();
        mShimmerViewContainer.setVisibility(View.GONE);
    }



    }
