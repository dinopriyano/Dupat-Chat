package com.dupat.dupatchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dupat.dupatchat.adapter.adapterChat;
import com.dupat.dupatchat.function.function;
import com.dupat.dupatchat.model.UserInfo;
import com.dupat.dupatchat.model.modelChat;
import com.dupat.dupatchat.retrofit.APIList;
import com.dupat.dupatchat.retrofit.RetrofitClient;
import com.dupat.dupatchat.service.onlineStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.prefs.Prefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class actChat extends AppCompatActivity {

    TextView namaUser,txtOnlineStatus;
    EditText etChat;
    String uid;
    FirebaseUser myAuth;
    DatabaseReference myRef,refChat;
    RecyclerView recyclerChat;
    RecyclerView.Adapter adapter;
    List<modelChat> list;
    LinearLayoutManager manager;
    SimpleDateFormat dateFormat;
    String dateNow,userPhoto,userName;
    boolean isAfterType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        function.changeStatusBarColor(this);
        recyclerChat = findViewById(R.id.recyclerChat);
        recyclerChat.setHasFixedSize(false);
        manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(manager);
        list = new ArrayList<>();
        myAuth = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("user");
        refChat = FirebaseDatabase.getInstance().getReference("chat");
        refChat.keepSynced(true);
        myRef.keepSynced(true);
        uid = getIntent().getStringExtra("uid");
        namaUser = findViewById(R.id.namaUser);
        txtOnlineStatus = findViewById(R.id.txtOnlineStatus);
        etChat = findViewById(R.id.etChat);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateNow = dateFormat.format(new Date());
        isAfterType = false;

        KeyboardVisibilityEvent.setEventListener(actChat.this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean b) {
                if(!b)
                {
                    function.updateTypingStatus("not typing",uid);
                }
                else
                {
                    function.updateTypingStatus("typing",uid);
                }
            }
        });

        myRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInfo info = dataSnapshot.getValue(UserInfo.class);
                namaUser.setText(info.name);
                userName = info.name;
                userPhoto = info.photo;
                String online;
                if(info.online_status.equalsIgnoreCase("Online"))
                {
                    if(dataSnapshot.child("typing_status").child("uid").getValue().toString().equals(myAuth.getUid()) && dataSnapshot.child("typing_status").child("status").getValue().toString().equals("typing"))
                    {
                        online = "typing...";
                    }
                    else
                    {
                        online = "Online";
                    }

                }
                else if(info.online_status.equalsIgnoreCase("Offline"))
                {
                    if(dateNow.equals(info.last_seen.split(" ")[0]))
                    {
                        online = "Last seen "+info.last_seen.split(" ")[1];
                    }
                    else
                    {
                        online = "Last seen "+info.last_seen;
                    }
                }
                else
                {
                    online = "";
                }

                txtOnlineStatus.setText(online);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.child(uid).child("photo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getMessage(myAuth.getUid(),uid,dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void actionBack(View view) {
        onBackPressed();
    }

    void getMessage(String myuid,String useruid,String imageurl)
    {

        adapter = new adapterChat(list,actChat.this,imageurl);
        recyclerChat.setAdapter(adapter);

        refChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                modelChat chat = dataSnapshot.getValue(modelChat.class);
                Log.d("TAG", "onChildAdded: "+myuid+" "+useruid+" "+chat.receiver+" "+chat.sender+" "+dataSnapshot.getRef());
                if((chat.sender.equals(myuid) && chat.receiver.equals(useruid)) || (chat.receiver.equals(myuid) && chat.sender.equals(useruid)))
                {
                    list.add(chat);
                    adapter.notifyDataSetChanged();
                    manager.scrollToPosition(list.size()-1);

                    if(isAfterType)
                    {
                        Toast.makeText(actChat.this, "update read", Toast.LENGTH_SHORT).show();
                        updateReadChat();
                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void updateReadChat()
    {
        refChat.orderByChild("receiver").equalTo(myAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    modelChat chat = ds.getValue(modelChat.class);
                    if((chat.sender.equals(myAuth.getUid()) && chat.receiver.equals(uid)) || (chat.receiver.equals(myAuth.getUid()) && chat.sender.equals(uid)))
                    {
                        if(chat.sender.equals(uid))
                        {
                            refChat.child(ds.getKey()).child("is_read").setValue(true);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        stopService(new Intent(this, onlineStatus.class));
        function.updateTypingStatus("not typing","");
        Prefs.with(this).write("openChatUID", "");
        super.onPause();
    }

    @Override
    protected void onResume() {
        startService(new Intent(this, onlineStatus.class));
        Prefs.with(this).write("openChatUID", uid);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    public void actionSend(View view) {
        if(!TextUtils.isEmpty(etChat.getText().toString().trim()) || !etChat.getText().toString().trim().equals(""))
        {
            isAfterType = true;
            long timestamp = new Date().getTime();
            SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String dateNow = formatDate.format(new Date());
            modelChat model = new modelChat(timestamp,myAuth.getUid(),uid,etChat.getText().toString().trim(),dateNow,false);
            refChat.push().setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    myRef.child(myAuth.getUid()).child("who_chat").child(uid).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            myRef.child(uid).child("who_chat").child(myAuth.getUid()).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    myRef.child(myAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            UserInfo myInfo = dataSnapshot.getValue(UserInfo.class);
                                            Map<String,String> data = new HashMap<>();
                                            data.put("topic",uid);
                                            data.put("title",myInfo.name);
                                            data.put("body","("+myInfo.username+"): "+etChat.getText().toString().trim());
                                            data.put("uid",myAuth.getUid());
                                            data.put("message","("+myInfo.username+"): "+etChat.getText().toString().trim());
                                            data.put("type","chat");
                                            data.put("photo",userPhoto.equals("")?"https://dinopriyano.my.id/uploads/user_photo/default_person.jpg":userPhoto);
                                            Call<String> call = RetrofitClient.getInstance().getApi().pushNotif(APIList.pushNotif,data);
                                            call.enqueue(new Callback<String>() {
                                                @Override
                                                public void onResponse(Call<String> call, Response<String> response) {

                                                }

                                                @Override
                                                public void onFailure(Call<String> call, Throwable t) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });
                        }
                    });
                }
            });

            etChat.setText("");
        }
    }

    public void actionAttach(View view) {
//        Toast.makeText(this, Environment.getExternalStorageDirectory().getPath().toString()+"/Dupat Chat/Media/Audio/", Toast.LENGTH_SHORT).show();
    }
}
