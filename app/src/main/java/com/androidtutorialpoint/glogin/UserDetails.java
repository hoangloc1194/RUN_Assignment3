package com.androidtutorialpoint.glogin;

import java.sql.Date;

/**
 * Created by Jessie on 20/12/2017.
 */

public class UserDetails {
    static String username = "";
    static String password = "";
    static String chatWith = "";
    static Date messageTime;
    public static Date getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }
}
