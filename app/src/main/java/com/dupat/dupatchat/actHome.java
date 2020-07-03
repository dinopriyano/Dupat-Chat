package com.dupat.dupatchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dupat.dupatchat.service.onlineStatus;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.auth.FirebaseAuth;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class actHome extends AppCompatActivity {

    MeowBottomNavigation meowBottomNavigation;
    Fragment active;
    frgHome home;
    frgExplore explore;
    frgProfile profile;
    frgFriendsRequest friendsRequest;
    FragmentManager fm;
    Toolbar toolbar;
    TextView titleFragment;
    LinearLayout btnToSearchFriend;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        changeStatusBarColor();
        btnToSearchFriend = (LinearLayout) findViewById(R.id.btnToSearchFriend);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_menu));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        titleFragment = (TextView) findViewById(R.id.titleFragment);
        fm = getSupportFragmentManager();
        home = new frgHome();
        explore = new frgExplore();
        profile = new frgProfile();
        friendsRequest = new frgFriendsRequest();
        active = home;
        fm.beginTransaction().add(R.id.fragmentContainer, home, "1").commit();
        fm.beginTransaction().add(R.id.fragmentContainer, friendsRequest, "4").hide(friendsRequest).commit();
        fm.beginTransaction().add(R.id.fragmentContainer, profile, "3").hide(profile).commit();
        fm.beginTransaction().add(R.id.fragmentContainer, explore, "2").hide(explore).commit();
        meowBottomNavigation = (MeowBottomNavigation) findViewById(R.id.bottomNav);
        meowBottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.ic_chat));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.ic_explore));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.ic_heart));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(4,R.drawable.ic_user));
        meowBottomNavigation.show(1,true);
        meowBottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                Fragment fragment = null;
                if(model.getId() == 1)
                {
                    fm.beginTransaction().hide(active).show(home).commit();
                    titleFragment.setText("Dupat Chat");
                    active = home;
                    invalidateOptionsMenu();
                }
                else if(model.getId() == 2)
                {
                    fm.beginTransaction().hide(active).show(explore).commit();
                    titleFragment.setText("Explore");
                    active = explore;
                    invalidateOptionsMenu();
                }
                else if(model.getId() == 3)
                {
                    fm.beginTransaction().hide(active).show(friendsRequest).commit();
                    titleFragment.setText("Friends");
                    btnToSearchFriend.setVisibility(View.VISIBLE);
                    active = friendsRequest;
                    invalidateOptionsMenu();
                }
                else if(model.getId() == 4)
                {
                    fm.beginTransaction().hide(active).show(profile).commit();
                    titleFragment.setText("Profile");
                    active = profile;
                    invalidateOptionsMenu();
                }

                return null;
            }
        });
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    protected void onPause() {
        stopService(new Intent(this, onlineStatus.class));
        super.onPause();
    }

    @Override
    protected void onResume() {
        startService(new Intent(this, onlineStatus.class));
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_menu_toolbar, menu);

        if(active == friendsRequest)
        {
            return false;
        }
        else
        {
            btnToSearchFriend.setVisibility(View.GONE);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this,actLogin.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }

    public void actionToFindPerson(View view) {

        startActivity(new Intent(this,actSearchFriends.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);

    }

    void showToolbar()
    {
        toolbar.setVisibility(View.VISIBLE);
        btnToSearchFriend.setVisibility(View.GONE);
    }

    void hideToolbar()
    {
        toolbar.setVisibility(View.GONE);
        btnToSearchFriend.setVisibility(View.VISIBLE);
    }
}
