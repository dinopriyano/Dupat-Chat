package com.dupat.dupatchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dupat.dupatchat.adapter.adapterFriends;
import com.dupat.dupatchat.adapter.adapterFriendsRequest;
import com.dupat.dupatchat.helper.popUpProfile;
import com.dupat.dupatchat.model.UserInfo;
import com.dupat.dupatchat.model.modelFriend;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import com.shreyaspatil.MaterialDialog.interfaces.OnCancelListener;
import com.shreyaspatil.MaterialDialog.interfaces.OnDismissListener;
import com.shreyaspatil.MaterialDialog.interfaces.OnShowListener;
import com.theophrast.ui.widget.SquareImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class frgFriendsRequest extends Fragment implements OnShowListener, OnCancelListener, OnDismissListener {

    RecyclerView recyclerFriendRequest,recyclerFriends;
    adapterFriends adapterFriend;
    RecyclerView.Adapter adapterRequest;
    List<modelFriend> listRequest;
    List<modelFriend> listFriend;
    FirebaseUser myAuth;
    DatabaseReference myRef;
    LinearLayout containerFriendRequest,containerFriend;
    RelativeLayout container404;
    private static final String TAG = "frgFriendsRequest";

    public frgFriendsRequest() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends_request, container, false);
        container404 = view.findViewById(R.id.container404);
        containerFriendRequest = view.findViewById(R.id.containerFriendRequest);
        containerFriend = view.findViewById(R.id.containerFriend);
        myAuth = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("user");
        recyclerFriendRequest = view.findViewById(R.id.recyclerFriendRequest);
        recyclerFriendRequest.setHasFixedSize(false);
        recyclerFriendRequest.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerFriends = view.findViewById(R.id.recyclerFriends);
        recyclerFriends.setHasFixedSize(false);
        recyclerFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        listFriend = new ArrayList<>();
        listRequest = new ArrayList<>();

        getFriends();
        getRequest();
        return view;
    }

    void getFriends()
    {
        myRef.child(myAuth.getUid()).child("friends").orderByChild("status").equalTo("friend").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listFriend.clear();
                if(dataSnapshot.exists())
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        if(ds.child("status").getValue().toString().equals("friend"))
                        {
                            Log.d(TAG, "data: "+ds.getValue().toString());
                            modelFriend model = new modelFriend(Integer.parseInt(ds.child("zid").getValue().toString()),ds.child("user_uid").getValue().toString(),ds.child("status").getValue().toString(),ds.child("username").getValue().toString());
                            listFriend.add(model);
                        }
                    }

                    Collections.sort(listFriend,modelFriend.StuNameComparator);
                    adapterFriend = new adapterFriends(listFriend,getContext());
                    recyclerFriends.setAdapter(adapterFriend);

                    adapterFriend.setOnItemClickCallback(new adapterFriends.OnItemClickCallback() {
                        @Override
                        public void onDeleteClicked(modelFriend data) {
                            MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                                    .setTitle("Delete?")
                                    .setMessage("Are you sure want to delete this file?")
                                    .setCancelable(false)
                                    .setPositiveButton("Delete", R.drawable.ic_trash, new MaterialDialog.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {

                                            myRef.child(data.user_uid).child("friends").child(myAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for(DataSnapshot ds : dataSnapshot.getChildren())
                                                    {
                                                        ds.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                myRef.child(myAuth.getUid()).child("friends").child(data.user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        if(dataSnapshot.exists())
                                                                        {
                                                                            for(DataSnapshot dsp : dataSnapshot.getChildren())
                                                                            {
                                                                                dsp.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        dialogInterface.dismiss();
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });

                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        }
                                    })
                                    .setNegativeButton("Cancel", R.drawable.ic_delete, new MaterialDialog.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                                    .build();

                            materialDialog.show();
                        }

                        @Override
                        public void onProfileClicked(modelFriend data) {

                            myRef.child(data.user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                                        popUpProfile pop = new popUpProfile(getActivity(), userInfo);
                                        pop.showPupUp();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

//                            new BlurPopupWindow.Builder(getContext())
//                                    .setContentView(R.layout.layout_pop_up_profile)
//                                    .bindClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                        }
//                                    })
//                                    .bindContentViewClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            v.findViewById(R.id.btnChat).setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    Toast.makeText(getContext(), "chat", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//
//                                        }
//                                    })
//                                    .setGravity(Gravity.CENTER)
//                                    .setScaleRatio(0.2f)
//                                    .setBlurRadius(0)
//                                    .setTintColor(Color.parseColor("#99000000"))
//                                    .build()
//                                    .show();

                        }
                    });
                }
                else
                {
                    adapterFriend = new adapterFriends(listFriend,getContext());
                    recyclerFriends.setAdapter(adapterFriend);
                }

                if(listFriend.size()>0)
                {
                    container404.setVisibility(View.GONE);
                }
                else if(listFriend.size()>0 && listRequest.size() < 1)
                {
                    container404.setVisibility(View.GONE);
                    containerFriendRequest.setVisibility(View.GONE);
                }
                else if(listFriend.size()<1 && listRequest.size()<1)
                {
                    container404.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void getRequest()
    {
        myRef.child(myAuth.getUid()).child("friends").orderByChild("status").equalTo("pending").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listRequest.clear();
                if(dataSnapshot.exists())
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        modelFriend modelFriend = new modelFriend(Integer.parseInt(ds.child("zid").getValue().toString()),ds.child("user_uid").getValue().toString(),ds.child("status").getValue().toString(),ds.child("username").getValue().toString());
                        listRequest.add(modelFriend);
                    }
                }

                if(listRequest.size()>0)
                {
                    container404.setVisibility(View.GONE);
                    containerFriendRequest.setVisibility(View.VISIBLE);
                }
                else if(listRequest.size()<1)
                {
                    containerFriendRequest.setVisibility(View.GONE);
                }
                else if(listRequest.size()<1 && listFriend.size() < 1)
                {
                    container404.setVisibility(View.VISIBLE);
                }

                adapterRequest = new adapterFriendsRequest(listRequest,getContext());
                recyclerFriendRequest.setAdapter(adapterRequest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {

    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {

    }

    @Override
    public void onShow(DialogInterface dialogInterface) {

    }
}
