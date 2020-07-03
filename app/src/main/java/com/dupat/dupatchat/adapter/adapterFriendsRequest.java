package com.dupat.dupatchat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dupat.dupatchat.R;
import com.dupat.dupatchat.frgFriendsRequest;
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

import java.util.List;

import es.dmoral.toasty.Toasty;

public class adapterFriendsRequest extends RecyclerView.Adapter<adapterFriendsRequest.ViewHolder> {

    List<modelFriend> list;
    Context ctx;
    DatabaseReference myRef;
    FirebaseUser myAuth;
    int MAX_ID = 1;

    public adapterFriendsRequest(List<modelFriend> list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
        myAuth = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("user");
    }

    @NonNull
    @Override
    public adapterFriendsRequest.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_friend_request,parent,false);
        return new adapterFriendsRequest.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterFriendsRequest.ViewHolder holder, int position) {

        final modelFriend model = list.get(position);
        String us_uid = model.user_uid;

        myRef.child(model.user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("photo").getValue().toString().equals(""))
                {
                    Glide.with(ctx).load(R.drawable.default_person).into(holder.fotoUser);
                }
                else
                {
                    Glide.with(ctx).load(dataSnapshot.child("photo").getValue().toString()).override(400,400).into(holder.fotoUser);
                }

                holder.namaUser.setText(dataSnapshot.child("name").getValue().toString());
                holder.usernameUser.setText(dataSnapshot.child("username").getValue().toString());
                holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myRef.child(myAuth.getUid()).child("friends").child(model.user_uid).child("status").setValue("friend").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                myRef.child(model.user_uid).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                MAX_ID = Integer.parseInt(child.child("zid").getValue().toString()) + 1;
                                            }
                                        }

                                        myRef.child(myAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                modelFriend modelF = new modelFriend(MAX_ID, myAuth.getUid(), "friend",dataSnapshot.child("username").getValue().toString());
                                                myRef.child(model.user_uid).child("friends").child(myAuth.getUid()).setValue(modelF).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

//                                                        Toasty.success(ctx, "Success accept new friend", Toasty.LENGTH_LONG, true).show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView fotoUser;
        TextView namaUser,usernameUser;
        CardView btnAccept,btnReject;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fotoUser = itemView.findViewById(R.id.fotoUser);
            namaUser = itemView.findViewById(R.id.namaUser);
            usernameUser = itemView.findViewById(R.id.usernameUser);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
