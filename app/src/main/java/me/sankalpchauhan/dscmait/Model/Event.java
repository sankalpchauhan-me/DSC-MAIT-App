package me.sankalpchauhan.dscmait.Model;

import androidx.annotation.Keep;

import java.util.Date;
import java.util.HashMap;

@Keep
public class Event{
    private String EventName;
    private String EventDescription;
    private String PrizeMoney;
    private HashMap<String, String> RegisteredUsers;
    private String RegistrationFee;
    private Date EventTime;
    private String EventImage;

    public Event(){
        //empty constructor needed for DB
    }

    public Event(String eventName, String eventDescription, String prizeMoney, HashMap<String, String> registeredUsers, String registrationFee, Date eventTime, String eventImage) {
        EventName = eventName;
        EventDescription = eventDescription;
        PrizeMoney = prizeMoney;
        RegisteredUsers = registeredUsers;
        RegistrationFee = registrationFee;
        EventTime = eventTime;
        EventImage = eventImage;
    }

    public String getEventName() {
        return EventName;
    }

    public String getEventDescription() {
        return EventDescription;
    }

    public String getPrizeMoney() {
        return PrizeMoney;
    }

    public HashMap<String, String> getRegisteredUsers() {
        return RegisteredUsers;
    }

    public String getRegistrationFee() {
        return RegistrationFee;
    }

    public Date getEventTime() {
        return EventTime;
    }

    public String getEventImage() {
        return EventImage;
    }
}
