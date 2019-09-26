package com.example.admin.lakshya.models;

/**
 * Created by VSES012 on 03-10-2016.
 */

/**
 * Created by VSES012 on 03-10-2016.
 */
import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class Post {

    public String bankname;
    public String purpose;
    public String counter;
    public String uid;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Post(String bankname, String purpose, String counter,String uid) {
        this.counter = uid;
        this.purpose = purpose;
        this.bankname = bankname;
        this.uid = counter;
    }

}