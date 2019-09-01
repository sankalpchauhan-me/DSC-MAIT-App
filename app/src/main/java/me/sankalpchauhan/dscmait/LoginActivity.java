package me.sankalpchauhan.dscmait;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import me.sankalpchauhan.dscmait.Base.BaseActivity;
import me.sankalpchauhan.dscmait.Base.transitionHelper;
import me.sankalpchauhan.dscmait.fragments.FeedbackBottomSheet;

public class LoginActivity extends BaseActivity {

    EditText emailTB;
    EditText passTB;
    Button loginBTN;
    Button registerBTN;
    Button passForget;
    ImageView logoem;

    //Transitions
    transitionHelper transhelp;

    //Firebase
    FirebaseAuth mAuth;

    //Temp
    Button requestBTN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();


            FirebaseUser currentUser = mAuth.getCurrentUser();
            SendToManinApp(currentUser);


        emailTB = findViewById(R.id.email);
        passTB = findViewById(R.id.password);
        loginBTN = findViewById(R.id.logBTN);
        passForget = findViewById(R.id.passForgot);
        requestBTN = findViewById(R.id.requestAccount);

        logoem = findViewById(R.id.logo);

        transhelp = new transitionHelper();

        //Transitions
        transhelp.rollInUpLeft(700, 0, logoem);
        transhelp.standUp(1000, 0, loginBTN);
        transhelp.rollInUpLeft(700, 0, passForget);
        transhelp.rollInUpLeft(1000, 0, requestBTN);








        //BUTTON CLICKS :

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //DATA INTEGRITY CHECKS
                if (emailTB.getText().toString().equals("") || passTB.getText().toString().equals("")) {
                    StandardToast(LoginActivity.this, "Please fill in all details");
                    WrongInfoShake();
                } else {
                    // Toast.makeText(MainActivity.this, "Authenticating...", Toast.LENGTH_SHORT).show();
                    signIn(emailTB.getText().toString(), passTB.getText().toString());

                }
            }
        });

        logoem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transhelp.tadaHi(700, 0, logoem);
            }
        });

        passForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Provide Functionality
                if (!emailTB.getText().toString().equals("")){
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailTB.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("MainActivity", "Email sent.");
                                        StandardToast(LoginActivity.this, "Password reset link is sent to the registered email");
                                    }
                                    else{
                                        StandardToast(LoginActivity.this, task.getException().getMessage());
                                    }
                                }
                            });
                }
                else{
                    StandardToast(LoginActivity.this, "Email is empty");
                    WrongInfoShake();
                }
            }
        });


        requestBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isConnected(LoginActivity.this))
            buildNetworkDialog(LoginActivity.this).show();

    }

    //A simple back dialog as is used in MainActivity
    @Override
    public void onBackPressed() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("This app requires sign in to work, Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginActivity.this.finish();
                        finish();
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



    //SHAKE ANIMATION IN CASE OF DATA INTEGRITY FAILIURE
    public void WrongInfoShake() {
        transhelp.shakeAnimation(400, 1, emailTB);
        emailTB.setError("Cannot Be Empty");
        transhelp.shakeAnimation(400, 1, passTB);
        passTB.setError("Cannot Be Empty");
    }


    private void signIn(String email, String password) {
        Log.d("MainActivity", "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog(this, "Logging In");

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignInSuccess", "signInWithEmail:success");
                            addNotification(LoginActivity.this, "New login from " + android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL, "Login Success on " + Calendar.getInstance().getTime() + " at " + "New Delhi,India", R.drawable.logo, 0);
                            FirebaseUser user = mAuth.getCurrentUser();
                            SendToManinApp(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SignInFail", "signInWithEmail:failure", task.getException());
                            addNotification(LoginActivity.this, "Invalid Login Attempt from " + android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL, "Tried to log in on " + Calendar.getInstance().getTime() + " from " + "New Delhi, India", R.drawable.logo, 0);
                            StandardToast(LoginActivity.this, "Authentication failed: "+task.getException().getMessage());
                            SendToManinApp(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            //mStatusTextView.setText(R.string.auth_failed);
                            StandardToast(LoginActivity.this, task.getException().getMessage());
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }


    private boolean validateForm() {
        boolean valid = true;

        String email =emailTB.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailTB.setError("Required.");
            WrongInfoShake();
            valid = false;
        } else {
            emailTB.setError(null);
        }

        String password = passTB.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passTB.setError("Required.");
            WrongInfoShake();
            valid = false;
        } else {
            passTB.setError(null);
        }

        return valid;
    }


    void SendToManinApp(FirebaseUser user){
        if (user!=null){
            Intent i = new Intent(LoginActivity.this, MainScreenActivity.class);
            startActivity(i);
        }
    }
}
