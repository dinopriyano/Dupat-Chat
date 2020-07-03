package com.dupat.dupatchat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dupat.dupatchat.adapter.adapterWhoChat;
import com.dupat.dupatchat.model.modelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class frgHome extends Fragment {

    RecyclerView recyclerWhoChat;
    FirebaseUser myAuth;
    DatabaseReference myRef,chatRef;
    List<modelChat> list;
    RecyclerView.Adapter adapter;
    RelativeLayout container404;

    public frgHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        container404 = view.findViewById(R.id.container404);
        myAuth = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("user");
        chatRef = FirebaseDatabase.getInstance().getReference("chat");
        recyclerWhoChat = view.findViewById(R.id.recyclerWhoChat);
        recyclerWhoChat.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setReverseLayout(true);
        recyclerWhoChat.setLayoutManager(manager);
        list = new ArrayList<>();
//        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds : dataSnapshot.getChildren())
//                {
//                    chatRef.child(ds.getKey()).child("is_read").setValue(true);
//                    chatRef.child(ds.getKey()).child("isSeen").removeValue();
//                    chatRef.child(ds.getKey()).child("seen").removeValue();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        getWhoChat();

        return view;
    }

    void getWhoChat()
    {
        myRef.child(myAuth.getUid()).child("who_chat").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if(dataSnapshot.exists())
                {
                    container404.setVisibility(View.GONE);
                    for(DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        modelChat mod = ds.getValue(modelChat.class);
                        list.add(mod);
                    }

                    adapter = new adapterWhoChat(list,getContext());
                    recyclerWhoChat.setAdapter(adapter);
                }
                else
                {
                    container404.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
