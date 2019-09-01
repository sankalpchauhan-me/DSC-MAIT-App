package me.sankalpchauhan.dscmait;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import me.sankalpchauhan.dscmait.Base.BaseActivity;
import me.sankalpchauhan.dscmait.Base.dbHelper;
import me.sankalpchauhan.dscmait.Base.transitionHelper;
import me.sankalpchauhan.dscmait.Callbacks.eventsCallback;
import me.sankalpchauhan.dscmait.Callbacks.keyCallback;
import me.sankalpchauhan.dscmait.Callbacks.mobileCallback;
import me.sankalpchauhan.dscmait.Callbacks.announcementCallback;
import me.sankalpchauhan.dscmait.Callbacks.roomCallback;
import me.sankalpchauhan.dscmait.fragments.AnnounceBottomSheet;
import me.sankalpchauhan.dscmait.fragments.RoomBottomSheetDialog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.r0adkll.slidr.Slidr;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventActivity extends BaseActivity {

    //Views
    ImageView eventImage;
    ProgressBar eventProgress;
    TextView eventDescription;
    TextView prizeMoney;
    TextView registeredUsers;
    TextView registrationFee;

    TextView eventDate;
    TextView eventTime;
    TableLayout ll;

    TextView regUserStatus;
    AVLoadingIndicatorView avi;
    CoordinatorLayout parentLayout;
    Button venueDetails;


    HashMap<String, Object> hashMap;
    private String EventName;
    private String EventDescription;
    private String PrizeMoney;
    private HashMap<String, String> RegisteredUsers;
    private String RegistrationFee;
    private Date EventTime;

    String eventsCollection;
    String eventsDocument;

    //DB
    dbHelper dbhelper;
    transitionHelper transhelp;
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    //PayMentGateway
    String userMobile=null;
    private String paytmChecksum = null;
    String setAnnouncement;

    FloatingActionButton fabResult;



    //mAuth
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    HashMap<String, String> userMap;

    //May Cause Null Pointer
    //TODO: Check for null pointer
    String isAdmin=null;

    String OrderID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_event);
        Toolbar eventToolbar = (Toolbar) findViewById(R.id.activity_event_toolbar);
        setSupportActionBar(eventToolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeAsUpIndicator(R.drawable.ic_keyboard);
            bar.setDisplayShowTitleEnabled(true);

            Intent intent = getIntent();

            //Firestore Path to document
            isAdmin = intent.getStringExtra("isAdmin");

            String FirestorePath = intent.getStringExtra("FirestorePath");
            String[] pathsplit = FirestorePath.split("/");

            dbhelper = new dbHelper();


            Log.e("FIRESTOREPATHCHECK", "collection: " + pathsplit[0] + "document: " + pathsplit[1]);

            eventsCollection = pathsplit[0];
            eventsDocument = pathsplit[1];

            hashMap = (HashMap<String, Object>) intent.getSerializableExtra("EventMap");
            EventName = (String) hashMap.get("EventName");
            EventDescription = (String) hashMap.get("Description");
            PrizeMoney = (String) hashMap.get("PrizeMoney");


            RegisteredUsers = (HashMap<String, String>) hashMap.get("RegisteredUsers");

            RegistrationFee = (String) hashMap.get("RegistrationFee");
            EventTime = (Date) hashMap.get("EventTime");


            bar.setTitle(Html.fromHtml("<font color='#ffffff'><B>" + hashMap.get("EventName") + "</B></font>"));



            //Views
            parentLayout = findViewById(R.id.parentLayoutEvent);
            eventImage = findViewById(R.id.eventimage);
            eventProgress = findViewById(R.id.eventProgress);
            eventDescription = findViewById(R.id.EventDescription);
            prizeMoney = findViewById(R.id.PrizeMoney);
            registeredUsers = findViewById(R.id.RegisteredUsers);
            eventDate = findViewById(R.id.EventDate);
            eventTime = findViewById(R.id.EventTime);
            registrationFee = findViewById(R.id.RegistrationFee);
            ll = (TableLayout) findViewById(R.id.usersTable);
            regUserStatus = findViewById(R.id.regUserStatus);
            avi = findViewById(R.id.avi);
            fabResult = findViewById(R.id.fabResult);
            venueDetails = findViewById(R.id.venueDetails);


            userMap = getData("userDetails");
            Log.e("GETTING:",userMap.get("Name"));

            SpeedDialView speedDialView = findViewById(R.id.speedDial);
            speedDialView.addActionItem(
                    new SpeedDialActionItem.Builder(R.id.fab_action1, R.drawable.ic_send)
                            .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, getTheme()))
                            .setLabel("Register")
                            .setLabelColor(Color.WHITE)
                            .setLabelBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.accent, getTheme()))
                            .setLabelClickable(false)
                            .create()
            );

            speedDialView.addActionItem(
                    new SpeedDialActionItem.Builder(R.id.fab_action2, R.drawable.ic_bookmark)
                            .setLabel("BookMark")
                            .setLabelClickable(false)
                            .setTheme(R.style.AppTheme)
                            .create()
            );

            speedDialView.addActionItem(
                    new SpeedDialActionItem.Builder(R.id.fab_action3, R.drawable.ic_share_white)
                            .setLabel("Share")
                            .setLabelClickable(false)
                            .setTheme(R.style.AppTheme)
                            .create()
            );

//            speedDialView.addActionItem(
//                    new SpeedDialActionItem.Builder(R.id.fab_action4, R.drawable.ic_share_white)
//                            .setLabel("Share")
//                            .setLabelClickable(false)
//                            .setTheme(R.style.AppTheme)
//                            .create()
//            );


            speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
                @Override
                public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
                    switch (speedDialActionItem.getId()) {
                        case R.id.fab_action1:
                            //StandardToast(EventActivity.this, "Register Clicked");
                            //InitializeGateway(EventActivity.this,true);
                            Integer size;
                            if(RegisteredUsers==null){
                                size=0;
                            }
                            else{
                                size = RegisteredUsers.size();
                            }
                            if(isConnected(EventActivity.this))
                            if(size<=100) {
                                if (RegistrationFee != null && isAdmin == null) {
                                    if (ContextCompat.checkSelfPermission(EventActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(EventActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                                    }
                                    //generateCheckSum();
                                    avi.show();
                                } else {
                                    addusertoEvent();
                                    StandardToast(EventActivity.this, "You will be added shortly" + Html.fromHtml("<font color='#00574B'><B>" + "\n Any Updates will be listed in the Info section" + "</B></font>"));
                                    addNotification(EventActivity.this, "Get Ready to Learn!", "Registered: DSC MAIT will be glad if you attend the event", R.drawable.logo, 101);
                                    addEventToCalendar();
                                    Snackbar snackbar = Snackbar.make(parentLayout, "Latest Updates about the event is available in Info Box", Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            snackbar.dismiss();
                                            onBackPressed();
                                        }
                                    });
                                    snackbar.show();
                                }
                            }
                            else{
                                StandardToast(EventActivity.this, "Event is already full! Please Check other Events");
                            }
                            else{
                                StandardToast(EventActivity.this, "Error! Check Internet!");
                            }
                            if(!isConnected(EventActivity.this)){
                                avi.hide();
                                Snackbar snackbar = Snackbar.make(parentLayout, "No Internet", Snackbar.LENGTH_LONG)
                                        .setAction("OK", null);
                                snackbar.show();
                            }
                            return true; // true to keep the Speed Dial open
                        case R.id.fab_action2:
                            StandardToast(EventActivity.this, "Bookmark Feature Coming Soon...");
                            return false;
                        case R.id.fab_action3:
                        {
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            String shareBody = "Join me for the "+EventName+" in which we can get "+ prizeMoney.getText()+" with DSC MAIT \n"+"Get it now at \n https://ppl.page.link/Get";
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        }
                        return false;
                        default:
                            return false;
                    }
                }
            });

            dbhelper.getUserPhoneNumber(this, "users", mAuth.getCurrentUser().getEmail(), "Mobile", new mobileCallback() {
                @Override
                public void onCallback(String mobile) {
                    userMobile = mobile;
                    Log.e("GOTMOBILE", String.valueOf(mobile));
                }

                @Override
                public void onNotCallback() {
                    //Do Nothing
                }
            });

            PreviousSetUp();

            dbhelper.getRegUserMapinDB(this, eventsCollection, eventsDocument, this, new eventsCallback() {
                @Override
                public void onCallback(Map<String, Object> reguserMap) {
                    if (reguserMap != null)
                        for (Map.Entry<String, Object> entry : reguserMap.entrySet()) {
                            Log.e("CHECKER", "Key = " + entry.getKey() + ", Value = " + entry.getValue());

                            //Major security concern
                            if(entry.getKey().equals(mAuth.getCurrentUser().getEmail())){
                                venueDetails.setVisibility(View.VISIBLE);
                            }

                            if(!initTable((String) entry.getValue())) {
                                initTable((String) entry.getValue());
                            }
                        }
                    else {
                        Log.e("Checker", "No users Registered");
                        regUserStatus.setText("No Registered Users");
                    }
                }
            });



        }

        Slidr.attach(this);

        triggerResultDetails();
        fabResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setAnnouncement==null) {
                    Snackbar snackbar = Snackbar.make(parentLayout, "No Updates Announced", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else{
                    Bundle bundle = new Bundle();
                    bundle.putString("Announcement", setAnnouncement);
                    AnnounceBottomSheet resultbottomsheet = new AnnounceBottomSheet();
                    resultbottomsheet.setArguments(bundle);
                    resultbottomsheet.show(getSupportFragmentManager(), "announceBottomSheet");
                }
            }
        });



        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    //transhelp.zoomOut(200,0,findViewById(R.id.fabResult));

                } else if (isShow) {
                    isShow = false;
                    //transhelp.zoomIn(200, 0, findViewById(R.id.fabResult));
                }
            }
        });

        venueDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerRoomDetails();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isConnected(EventActivity.this)){
            avi.hide();
            Snackbar snackbar = Snackbar.make(parentLayout, "No Internet", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
            snackbar.show();
        }
        dbhelper.getAPIkeys(this, "keys", "secrets", this, new keyCallback() {
            @Override
            public void onCallback(Map<String, Object> keyMap) {
                if (keyMap != null) {

                    //MID = (String) keyMap.get("MIDTest");

                } else {
                    Log.e("HELLOBRO", "MID is NULL");
                }


            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void addusertoEvent(){
        HashMap<String, Object> user = new HashMap<>();
        String key = userMap.get("Email");
        Object value = userMap.get("Name");
        Object lastname = userMap.get("LastName");
        user.put(key, value+" "+lastname);

        dbhelper.addUsertoEvent(EventActivity.this, eventsCollection, eventsDocument, user);
        regUserStatus.setText("Registered Users:");
    }

    public void triggerResultDetails(){
        avi.show();
        dbhelper.getEventResult(eventsDocument, new announcementCallback() {
            @Override
            public void onCallback(String Result) {
                avi.hide();
                if(Result!=null) {
                    setAnnouncement = Result;
                }
            }
        });

    }

    public boolean initTable(String userName) {

        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        lp.setMargins(10,10,10,10);


        TableRow row = new TableRow(this);
        row.setLayoutParams(lp);
        CheckBox checkBox = new CheckBox(this);
        TextView tv = new TextView(this);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(18);
        tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));


        tv.setText(Html.fromHtml("" + userName + "     "));
        //checkBox.setText(Html.fromHtml(" |  "));
        checkBox.setEnabled(false);
        //tv.setBackgroundResource(R.drawable.rect_background);

        row.addView(tv);
        row.addView(checkBox);

        //Add rows to table

        ll.addView(row);


        return true;


    }

    public void PreviousSetUp() {
        Currency currency = Currency.getInstance("INR");
        String symbol = currency.getSymbol();

        if (RegistrationFee != null) {
            registrationFee.setText(RegistrationFee);
        } else {
            registrationFee.setText("Free");
        }
        //holder.ivViewEventImage
        if (PrizeMoney != null) {
            prizeMoney.setText(PrizeMoney);
        } else {
            prizeMoney.setText("Free");
        }

        if (RegisteredUsers != null) {
            if(RegisteredUsers.size()<=100) {
                HashMap<String, String> hashMap = RegisteredUsers;
                registeredUsers.setText(String.valueOf(hashMap.size()) + "/100");

            }
            else{
                registeredUsers.setText("Full");
            }
        } else {
            registeredUsers.setText("0" + "/100");
        }

        if (EventTime != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
            String eventdate = simpleDateFormat.format(EventTime);

            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String time = simpleTimeFormat.format(EventTime);

            eventDate.setText(String.valueOf(eventdate));
            eventTime.setText(String.valueOf(time));
        } else {
            eventDate.setText("Open All");
            eventTime.setText("Time");
        }

        if (hashMap.get("EventImage") != null || String.valueOf(hashMap.get("EventImage")).isEmpty()) {
            Glide.with(this).load(hashMap.get("EventImage")).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    eventProgress.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    eventProgress.setVisibility(View.GONE);
                    return false;
                }
            }).thumbnail(0.1f).into(eventImage);


        } else {
            eventProgress.setVisibility(View.GONE);
            Glide.with(this).load(R.drawable.profilebackground).into(eventImage);
        }

        if (String.valueOf(EventDescription).trim().isEmpty() || String.valueOf(EventDescription).isEmpty() || EventDescription == null) {
            eventDescription.setText("No Description Provided");
        } else {
            eventDescription.setText(EventDescription);
        }
    }

    public void addEventToCalendar(){
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, EventName);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                EventTime);

        intent.putExtra(CalendarContract.Events.ALL_DAY, false);// periodicity
        intent.putExtra(CalendarContract.Events.DESCRIPTION,EventDescription);
    }

    public void triggerRoomDetails(){
        avi.show();
        dbhelper.getRoomIDAndPassword(this, eventsCollection, eventsDocument, this, new roomCallback() {
            @Override
            public void onCallback(Map<String, Object> reguserMap) {
                avi.hide();
                String ID = String.valueOf(reguserMap.get("Venue"));
                //String pass = String.valueOf(reguserMap.get("Password"));
                if(ID.equals("null")){
                    Snackbar snackbar = Snackbar.make(parentLayout, "Will be updated soon", Snackbar.LENGTH_LONG);
                    snackbar.show();

                }
                else{
                    Bundle bundle = new Bundle();
                    bundle.putString("RoomID", ID);
                    //bundle.putString("Password", pass);
                    RoomBottomSheetDialog roomBottomSheetDialog = new RoomBottomSheetDialog();
                    roomBottomSheetDialog.setArguments(bundle);
                    roomBottomSheetDialog.show(getSupportFragmentManager(), "roomBottomSheet");
                }
            }
        });
    }

}
