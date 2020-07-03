package com.dupat.dupatchat.model;

import java.util.Comparator;

public class modelFriend{
    public int zid;
    public String user_uid,status,username;

    public modelFriend(int zid, String user_uid, String status, String username) {
        this.zid = zid;
        this.user_uid = user_uid;
        this.status = status;
        this.username = username;
    }

    public static Comparator<modelFriend> StuNameComparator = new Comparator<modelFriend>() {

        @Override
        public int compare(modelFriend o1, modelFriend o2) {
            String username1 = o1.username.toUpperCase();
            String username2 = o2.username.toUpperCase();
            return username1.compareTo(username2);
        }
    };

}
