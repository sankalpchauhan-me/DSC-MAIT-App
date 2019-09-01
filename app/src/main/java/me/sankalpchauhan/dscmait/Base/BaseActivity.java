package me.sankalpchauhan.dscmait.Base;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import me.sankalpchauhan.dscmait.Callbacks.mobileCallback;

public class BaseActivity extends AppCompatActivity {
    dbHelper<String> dbhelper = new dbHelper<>();
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm@$#";


    //GENERIC DISPLAY DIALOG
    public void DisplayDialog(Context context, String title, String message, String posText) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title).setMessage(message).setPositiveButton(posText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    //GENERIC TOAST
    public void StandardToast(Context context, String Message) {
        Toast.makeText(context, Message, Toast.LENGTH_SHORT).show();
    }

    //Custom Toast with Image
    public void ImageInToast(Context context, String Message, int ImageId) {
        Toast toast = Toast.makeText(context, Message, Toast.LENGTH_SHORT);
        LinearLayout toastContentView = (LinearLayout) toast.getView();
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(ImageId);
        toastContentView.addView(imageView, 0);
        toast.show();

    }


    //CHECK NETWORK STATE----------------------------------------------------------------------------------------------------------------------
    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        Log.i("NetworkState", "isConnected checking network state.......");
        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else
            return false;
    }

    public androidx.appcompat.app.AlertDialog.Builder buildNetworkDialog(Context c) {
        Log.i("NetworkState", "Network Failed......... Building Dialog");
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or WIFI to access this app. Press Enable to access network settings");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in = new Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
                c.startActivity(in);
            }
        });

        return builder;
    }

    //Notification Builder
    public void addNotification(Context mContext, String title, String subject, int Imageid, int ID) {
        Notification noti = new Notification.Builder(mContext)
                .setContentTitle(title)
                .setContentText(subject)
                .setSmallIcon(Imageid)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setCategory(Notification.CATEGORY_REMINDER)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(ID, noti);
    }

    public ProgressDialog mProgressDialog;

    public void showProgressDialog(Context context, String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }


    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    //Shared Preferences
    public void saveDataToSharedPrefs(String nameOfSharedPreference, HashMap<String, String> userDetails){
        SharedPreferences sharedPref = getSharedPreferences(nameOfSharedPreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor shareEdit = sharedPref.edit();
        for (Map.Entry<String, String> entry : userDetails.entrySet()) {
            shareEdit.putString(entry.getKey(), entry.getValue());
            Log.e("SHAREPREF", entry.getKey()+" "+entry.getValue());
        }
        Log.e("SHAREPREF", "Save Successful");
        shareEdit.apply();


    }


//    public void saveDataToShared(){
//        SharedPreferences sharedPref = getSharedPreferences("test", Context.MODE_PRIVATE);
//        SharedPreferences.Editor shareEdit = sharedPref.edit();
//
//        shareEdit.putString("Hello", "Bro");
//        shareEdit.commit();
//
//    }

    public HashMap<String, String> getData(String nameOfSharedPreference){
        SharedPreferences sharedPref = getSharedPreferences(nameOfSharedPreference, Context.MODE_PRIVATE);
        Map<String, ?> s1 = sharedPref.getAll();

        return (HashMap<String, String>) s1;
    }



    public void ProperSignOut(FirebaseAuth mAuth, String documentName){
        //Update LoggedIn value in DB
        dbhelper.updateDataForUser("users", documentName,"Logged In", "No", 0);
        mAuth.signOut();
        SharedPreferences preferences =getSharedPreferences("userDetails",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        finish();

    }

    public void requestPermissions(Context context, Activity activity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }
    }


    public void phonePrompt(Context context, mobileCallback mobilecallback){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Phone Number (Optional)");
        builder.setMessage(Html.fromHtml("<B>Phone number will be used to give you an update about latest events</B>. You can enter the number at the time of registration with the team of <B><font color='#51B8ED'>DSC</font><font color='#8e0000'>MAIT</font> </B>"));
        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        validPhoneNumbercheck(input);

        // Set up the buttons
        builder.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Intentionally left blank did nothing here
            }
        });
        builder.setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StandardToast(context, "Phone Number Not Provided");
                mobilecallback.onNotCallback();
                dialog.cancel();
            }
        });

        //Overriding the positive onclick so that user cannot input wrong number

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Boolean wantToCloseDialog = false;
                //Do stuff, possibly set wantToCloseDialog to true then...
                {
                    //Implemented callback listner due to asynchronus nature of activity
                    if (input.getText().length() != 13) {
                        input.setError("Invalid Number");
                    } else {
                        mobilecallback.onCallback(input.getText().toString());
                        wantToCloseDialog = true;
                    }
                }
                if(wantToCloseDialog)
                    dialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });



    }

    //Overloading Phone Number so that it can be used for withdrawal
    public void phonePrompt(Context context, int id, mobileCallback mobilecallback){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Phone Number (PayTM)");
        builder.setCancelable(false);
        builder.setMessage(Html.fromHtml("<B>Enter the <B><font color='#0F2E6B'>Pay</font><font color='#51B8ED'>TM</font> </B> phone number you wish to receive your withdrawal on</B>. <br> If you share your request on Social Media Platforms with hashtag <br> <B> #PPLiveWon </B> <br> we will be able to process it faster. If you do not provide a number we will contact you on your email for withdrawal details, It can take upto 4-5 business days."));
        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        validPhoneNumbercheck(input);

        // Set up the buttons
        builder.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Intentionally left blank did nothing here
            }
        });
        builder.setNegativeButton("I Will Wait", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StandardToast(context, "Phone Number Not Provided");
                mobilecallback.onNotCallback();
                dialog.cancel();
            }
        });

        //Overriding the positive onclick so that user cannot input wrong number

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Boolean wantToCloseDialog = false;
                //Do stuff, possibly set wantToCloseDialog to true then...
                {
                    //Implemented callback listner due to asynchronus nature of activity
                    if (input.getText().length() != 13) {
                        input.setError("Invalid Number");
                    } else {
                        mobilecallback.onCallback(input.getText().toString());
                        wantToCloseDialog = true;
                    }
                }
                if(wantToCloseDialog)
                    dialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });



    }

    public void validPhoneNumbercheck(EditText editText){
        editText.setText("+91");
        Selection.setSelection(editText.getText(), editText.getText().length());


        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("+91")) {
                    editText.setText("+91");
                    Selection.setSelection(editText.getText(), editText.getText().length());

                }

            }
        });
    }

    public void CustomSnackbar(String message,int id){
        FloatingActionButton fab = (FloatingActionButton) findViewById(id);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    public void setSnackBar(View root, String snackTitle) {
        Snackbar snackbar = Snackbar.make(root, snackTitle, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        View view = snackbar.getView();
        TextView txtv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }


    public static Spanned fromHtml(String html){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }


}
