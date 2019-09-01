package me.sankalpchauhan.dscmait.Model;

import java.util.HashMap;

import androidx.annotation.Keep;

@Keep
public class Team {
    private String Description;
    private String Image;
    private HashMap<String, String> Links;
    private String Name;
    private String Position;
    private String Role;

    public Team(){
        //Empty Constructor Required by FireStore
    }

    public Team(String description, String image, HashMap<String, String> links, String name, String position, String role) {
        Description = description;
        Image = image;
        Links = links;
        Name = name;
        Position = position;
        Role = role;
    }

    public String getDescription() {
        return Description;
    }

    public String getImage() {
        return Image;
    }

    public HashMap<String, String> getLinks() {
        return Links;
    }

    public String getName() {
        return Name;
    }

    public String getPosition() {
        return Position;
    }

    public String getRole() {
        return Role;
    }
}
