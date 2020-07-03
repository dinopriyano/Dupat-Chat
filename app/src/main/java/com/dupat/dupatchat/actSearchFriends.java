package com.dupat.dupatchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dupat.dupatchat.ProgressBar.LoadingBar;
import com.dupat.dupatchat.adapter.adapterSearchFriends;
import com.dupat.dupatchat.function.function;
import com.dupat.dupatchat.model.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class actSearchFriends extends AppCompatActivity {

    DatabaseReference myRef;
    EditText etSearchPerson;
    List<UserInfo> list;
    RelativeLayout container404;
    RecyclerView recyclerResult;
    RecyclerView.Adapter adapter;
    FirebaseUser myAuth;
    LoadingBar loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        changeStatusBarColor();
        container404 = (RelativeLayout) findViewById(R.id.container404);
        loadingBar = new LoadingBar(this);
        recyclerResult = (RecyclerView) findViewById(R.id.recyclerSearchFriends);
        recyclerResult.setHasFixedSize(false);
        recyclerResult.setLayoutManager(new LinearLayoutManager(this));
        myRef = FirebaseDatabase.getInstance().getReference("user");
        myAuth = FirebaseAuth.getInstance().getCurrentUser();
        etSearchPerson = (EditText) findViewById(R.id.etSearchPerson);
        etSearchPerson.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    Toast.makeText(actSearchFriends.this, "done", Toast.LENGTH_SHORT).show();
                    if(etSearchPerson.getText().toString().trim().length()<3)
                    {
                        Toasty.error(actSearchFriends.this,"Please input username more than 2 character",Toasty.LENGTH_LONG,true).show();
                    }
                    else
                    {
                        getuser(etSearchPerson.getText().toString().trim());
                    }
                    function.hideKeyboard(actSearchFriends.this);
                    return true;
                }
                // Return true if you have consumed the action, else false.
                return false;
            }
        });
    }

    void getuser(final String value)
    {
        loadingBar.startLoading();
        list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if(ds.child("username").getValue().toString().contains(value) && !ds.child("uid").getValue().toString().equals(myAuth.getUid()))
                    {
                        UserInfo userInfo = ds.getValue(UserInfo.class);
                        list.add(userInfo);
                    }
                }

                if(list.size() < 1)
                {
                    container404.setVisibility(View.VISIBLE);
                }
                else
                {
                    container404.setVisibility(View.GONE);
                }

                adapter = new adapterSearchFriends(list,actSearchFriends.this);
                recyclerResult.setAdapter(adapter);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingBar.stopLoading();
                    }
                },1000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    public void actionBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    public void actionClear(View view) {
        etSearchPerson.setText("");
    }
}
