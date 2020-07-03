package com.dupat.dupatchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.dupat.dupatchat.R;
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
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class adapterFriends extends RecyclerView.Adapter<adapterFriends.ViewHolder> {

    List<modelFriend> list;
    Context ctx;
    DatabaseReference myRef;
    FirebaseUser myAuth;

    private OnItemClickCallback onItemClickCallback;
    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public adapterFriends(List<modelFriend> list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
        myAuth = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("user");
    }

    @NonNull
    @Override
    public adapterFriends.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_friend,parent,false);
        return new adapterFriends.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterFriends.ViewHolder holder, int position) {

        modelFriend model = list.get(position);

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
                holder.btnDeleteFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickCallback.onDeleteClicked(model);
                    }
                });
                holder.fotoUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickCallback.onProfileClicked(model);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public interface OnItemClickCallback {
        void onDeleteClicked(modelFriend data);
        void onProfileClicked(modelFriend data);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView fotoUser;
        TextView namaUser,usernameUser;
        CardView btnDeleteFriend;
        SwipeLayout swipeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoUser = itemView.findViewById(R.id.fotoUser);
            namaUser = itemView.findViewById(R.id.namaUser);
            usernameUser = itemView.findViewById(R.id.usernameUser);
            swipeLayout = itemView.findViewById(R.id.swipeLayout);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, itemView.findViewById(R.id.containerFriend));
            btnDeleteFriend = itemView.findViewById(R.id.btnDeleteFriend);
        }
    }
}
