package me.sankalpchauhan.dscmait.Model;

import androidx.annotation.Keep;

@Keep
public class User {
    private String mEmail;
    private String mEmailVerified;
    private String mPhone;
    private String mFirstName;
    private String mLastName;
    private String mPassword;
    private String mLoggedIn;

    public User(){
        //empty constructor needed for DB
    }

    public User(String mEmail, String mEmailVerified, String mPhone, String mFirstName, String mLastName, String mPassword, String mLoggedIn) {
        this.mEmail = mEmail;
        this.mEmailVerified = mEmailVerified;
        this.mPhone = mPhone;
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mPassword = mPassword;
        this.mLoggedIn = mLoggedIn;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getEmailVerified() {
        return mEmailVerified;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getLoggedIn() {
        return mLoggedIn;
    }
}
