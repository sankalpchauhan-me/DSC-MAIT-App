package me.sankalpchauhan.dscmait;

import androidx.appcompat.app.AppCompatActivity;
import me.sankalpchauhan.dscmait.Base.BaseActivity;
import me.sankalpchauhan.dscmait.Base.dbHelper;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class RegisterActivity extends BaseActivity {
    EditText email;
    EditText firstname;
    EditText lastname;
    EditText college;
    EditText semester;
    EditText mobile;
    EditText description;
    Button requestButton;
    TextView declaration;
    CheckBox consent;

    HashMap<String, String> userMap1 = new HashMap<>();

    dbHelper<String> dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.regemail);
        firstname = findViewById(R.id.regfirstname);
        lastname = findViewById(R.id.reglastname);
        college = findViewById(R.id.regcollege);
        semester = findViewById(R.id.regsemester);
        description = findViewById(R.id.regaccountBox);
        mobile = findViewById(R.id.regmobile);
        requestButton = findViewById(R.id.regBTN);
        consent = findViewById(R.id.consentCheck);
        declaration = findViewById(R.id.requestDeclaration);
        declaration.setText(fromHtml("<span style=\"color:#ffffff;\">By clicking Submit Request, <br> you agree to our</span> <strong> <a href=\"http://sankalpchauhan.me/projects/DSCMait/privacypolicy.html\"> Privacy Policy </a> </strong> <p><strong><span style=\"color:#000080;\">DSC</span> <span style=\"color:#b22222;\">MAIT</span></strong> <span style=\"color:#ffffff;\">will review your application. Based on your response we may or may not grant you the access.<br> <strong>Best Of Luck!</strong></span></p>\n"));
        declaration.setMovementMethod(LinkMovementMethod.getInstance());

        dbhelper = new dbHelper<>();



        /**
         * Checking for a valid phone number string and making +91 non-erasablee
         */
       validPhoneNumbercheck(mobile);


       requestButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(!validateForm()){
                   StandardToast(RegisterActivity.this, "Please correct the errors");
               }

               else{
                   if(consent.isChecked()) {
                       RequestAccount();
                   }
                   else{
                       DisplayDialog(RegisterActivity.this, "Note", "Cannot Proceed: Please Agree To The Declaration", "OK");
                   }
               }
           }
       });
    }


    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    //FireBase
    private boolean validateForm() {
        boolean valid = true;

        String useremail = email.getText().toString();
        if (TextUtils.isEmpty(useremail.trim())) {
            email.setError("Fill Details");
            valid = false;
        } else {
            email.setError(null);
        }

        String userfname = firstname.getText().toString();
        if (TextUtils.isEmpty(userfname.trim())) {
            firstname.setError("Required.");
            valid = false;
        } else {
            firstname.setError(null);
        }

        String userlname = lastname.getText().toString();
        if (TextUtils.isEmpty(userlname.trim())) {
            lastname.setError("Required.");
            valid = false;
        } else {
            lastname.setError(null);
        }

        String usercollege = college.getText().toString();
        if (TextUtils.isEmpty(usercollege.trim())) {
            college.setError("Required.");
            valid = false;
        } else {
            college.setError(null);
        }

        String usersemester = semester.getText().toString();
        if (TextUtils.isEmpty(usersemester.trim())) {
            semester.setError("Required.");
            valid = false;
        } else {
            semester.setError(null);
        }


        if(!isEmailValid(useremail)){
            email.setError("Email Not Valid");
            valid = false;
        }


        return valid;
    }


    public void GetUserDetailsForDB(){
        userMap1.put("Email", email.getText().toString());
        userMap1.put("Name", firstname.getText().toString());
        userMap1.put("LastName", lastname.getText().toString());
        userMap1.put("College", college.getText().toString());
        userMap1.put("Semester", semester.getText().toString());
        if(mobile.getText().length()<13 && !(mobile.getText().toString().equals("+91")) || mobile.getText().length()>13){
            StandardToast(this, "Wrong Number");
            mobile.setError("Not Submitted");
            userMap1.put("Mobile", "No");
        }
        else{
            if(mobile.getText().toString().equals("+91")){
                userMap1.put("Mobile", "No");
            }
            else {
                userMap1.put("Mobile", mobile.getText().toString());
            }
        }

        if(description.getText().toString().trim().isEmpty()){
            userMap1.put("Description", "N/A");
        }
        else{
            userMap1.put("Description", description.getText().toString());
        }
    }

    public void RequestAccount(){
        GetUserDetailsForDB();
        dbhelper.AddDatatoDB(userMap1, "accountrequests", email.getText().toString(), this, "We will Review Your Application");

    }


    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }

}
