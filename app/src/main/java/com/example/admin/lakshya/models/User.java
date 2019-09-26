package com.example.admin.lakshya.models;

/**
 * Created by VSES012 on 03-10-2016.
 */
import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String sno;
    public String local;
    public String cat;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String sno, String local,String cat) {
        this.username = username;
        this.sno = sno;
        this.local = local;
        this.cat = cat;
    }

}
// [END blog_user_class]