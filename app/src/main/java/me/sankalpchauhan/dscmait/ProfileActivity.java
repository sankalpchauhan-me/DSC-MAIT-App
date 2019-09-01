package me.sankalpchauhan.dscmait;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import me.sankalpchauhan.dscmait.Animations.OnRevealAnimationListener;
import me.sankalpchauhan.dscmait.Base.BaseActivity;
import me.sankalpchauhan.dscmait.Base.dbHelper;
import me.sankalpchauhan.dscmait.Base.transitionHelper;
import me.sankalpchauhan.dscmait.fragments.ContactusBottomSheet;

import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.leinardi.android.speeddial.SpeedDialView;
import com.r0adkll.slidr.Slidr;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;

public class ProfileActivity extends BaseActivity {

    //user
    HashMap<String, String> userMap;

    transitionHelper transhelp;

    dbHelper dbhelper;

    //Views
    CoordinatorLayout root;
    TextView nameTB;
    TextView LastNameTB;
    TextView rateTB;
    AVLoadingIndicatorView aviLoader;
    ImageView profileBackground;
    TextView statusTB;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Explode());
        setContentView(R.layout.activity_profile);
        dbhelper = new dbHelper();

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
                    transhelp.zoomOut(200,0,findViewById(R.id.fab));

                } else if (isShow) {
                    isShow = false;
                    transhelp.zoomIn(200, 0, findViewById(R.id.fab));
                }
            }
        });


        root = findViewById(R.id.rootContainerMAinApp);
        nameTB = findViewById(R.id.profileName);
        LastNameTB = findViewById(R.id.profileLastName);
        rateTB = findViewById(R.id.IDRating);
        aviLoader = findViewById(R.id.aviProfile);
        profileBackground = findViewById(R.id.profileImage);
        statusTB = findViewById(R.id.statusProfile);
        rateTB.setText("");

        userMap = new HashMap<>();

        transhelp = new transitionHelper();
        userMap=getData("userDetails");

        nameTB.setText(userMap.get("Name"));
        LastNameTB.setText(userMap.get("LastName"));
        if(userMap.get("Interests")!=null){
            statusTB.setText(userMap.get("Interests"));
        }
        else {
            statusTB.setVisibility(View.GONE);
        }
        if(userMap.get("Rating")!=null){
            rateTB.setText(userMap.get("Rating")+"/5.0");
        }
        else{
            rateTB.setText("Not Rated");
        }


        transhelp.slideInLeft(1000,0,findViewById(R.id.IDProfile));
        transhelp.slideInLeft(1000,0,findViewById(R.id.IDRating));
        transhelp.slideInLeft(1000,0,findViewById(R.id.IDParticipation));
        transhelp.tadaHi(1500,0,findViewById(R.id.userProfilePic));

        Slidr.attach(this);
        aviLoader.show();

        Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/dscmait.appspot.com/o/Text%20placeholder.png?alt=media&token=6ceac043-d82e-4f3c-9503-af2861f5f74d").into(profileBackground);

        //Edit FAB
        SpeedDialView fab =  findViewById(R.id.fab);
        fab.setOnChangeListener(new SpeedDialView.OnChangeListener() {
            @Override
            public boolean onMainActionSelected() {
                Snackbar.make(root, "Do you want to edit your profile?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("Name", userMap.get("Name"));
                                bundle.putString("ID", userMap.get("LastName"));
                                bundle.putString("Email", userMap.get("Email"));
                                ContactusBottomSheet contactusBottomSheet = new ContactusBottomSheet();
                                contactusBottomSheet.setArguments(bundle);
                                contactusBottomSheet.show(getSupportFragmentManager(), "contactBottomSheet");
                            }
                        }).show();
                return false;
            }

            @Override
            public void onToggleChanged(boolean isOpen) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        transhelp.animateRevealHide(this, findViewById(R.id.rootContainerMAinApp), R.color.colorPrimary, 40,
                new OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() {
                        ProfileActivity.super.onBackPressed();
                    }

                    @Override
                    public void onRevealShow() {

                    }
                });
    }
}
