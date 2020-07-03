package com.dupat.dupatchat.function;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.dupat.dupatchat.R;
import com.dupat.dupatchat.model.modelTypingStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class function {

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void updateTypingStatus(String status, String uid)
    {
        FirebaseUser myAuth = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("user");

        modelTypingStatus modelTypingStatus = new modelTypingStatus(status,uid);
        myRef.child(myAuth.getUid()).child("typing_status").setValue(modelTypingStatus);
    }

    public static void updateSeen(String user_uid)
    {

    }

    public static String getDate(String date)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            cal.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String month[] = {"Januari","Februari","Maret","April","Mei","Juni","Juli","Agustus","September","Oktober","November","Desember"};
        String hari[] = {"Minggu","Senin","Selasa","Rabu","Kamis","Jumat","Sabtu"};
        String hari2[] = {"minggu","senin","selasa","rabu","kamis","jumat","sabtu"};
        GregorianCalendar gc = new GregorianCalendar(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        final String bln = month[gc.get(Calendar.MONTH)];
        final String hri = hari[(gc.get(Calendar.DAY_OF_WEEK))-1];
        final int tgl = gc.get(Calendar.DATE);
        final int year = gc.get(Calendar.YEAR);
        final int jam = gc.get(Calendar.HOUR);
        final int mnt = gc.get(Calendar.MINUTE);
        final int dt = gc.get(Calendar.SECOND);
        final int bul = gc.get(Calendar.MONTH);
        final int pmam = gc.get(Calendar.AM_PM);
        final String hariIni = tgl+" "+bln+" "+year;

        return hariIni;
    }

    public static void changeStatusBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimary));
        }
    }

}
