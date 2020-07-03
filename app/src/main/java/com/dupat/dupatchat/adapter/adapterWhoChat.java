package com.dupat.dupatchat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.dupat.dupatchat.R;
import com.dupat.dupatchat.actChat;
import com.dupat.dupatchat.model.UserInfo;
import com.dupat.dupatchat.model.modelChat;
import com.dupat.dupatchat.service.onlineStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class adapterWhoChat extends RecyclerView.Adapter<adapterWhoChat.ViewHolder> {

    List<modelChat> list;
    Context ctx;
    FirebaseUser myAuth;
    DatabaseReference myRef;

    public adapterWhoChat(List<modelChat> list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
        myAuth = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("user");
    }

    @NonNull
    @Override
    public adapterWhoChat.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_who_chat,parent,false);
        return new adapterWhoChat.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterWhoChat.ViewHolder holder, int position) {

        modelChat modelChat = list.get(position);
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        String dateNow = formatDate.format(new Date());
        holder.messageUser.setText(modelChat.message);

        if(modelChat.date.split(" ")[0].equals(dateNow))
        {
            holder.waktuChat.setText(modelChat.date.split(" ")[1]);
        }
        else
        {
            holder.waktuChat.setText(modelChat.date.split(" ")[0]);
        }

        myRef.keepSynced(true);
        Log.d("s", "onBindViewHolder: "+(modelChat.sender.equals(myAuth.getUid()) ? modelChat.receiver : modelChat.sender));
        myRef.child(modelChat.sender.equals(myAuth.getUid()) ? modelChat.receiver : modelChat.sender).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserInfo info = dataSnapshot.getValue(UserInfo.class);
                if (isValidContextForGlide(ctx)) {
                    if(info.photo.equals(""))
                    {
                        Glide.with(ctx).load(R.drawable.default_person).into(holder.fotoUser);
                    }
                    else
                    {
                        Glide.with(ctx).load(info.photo).override(400,400).into(holder.fotoUser);
                    }
                }

                holder.namaUser.setText(dataSnapshot.child("name").getValue().toString());
                holder.cardOnlineStatus.setCardBackgroundColor(Color.parseColor(info.online_status.equals("Online")?"#38E400":"#C6C6C6"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, actChat.class);
                intent.putExtra("uid",(modelChat.sender.equals(myAuth.getUid()) ? modelChat.receiver : modelChat.sender));
                ctx.startActivity(intent);
                ((Activity) ctx).overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
            }
        });
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView fotoUser;
        TextView namaUser,messageUser,waktuChat;
        SwipeLayout swipeLayout;
        CardView cardOnlineStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fotoUser = itemView.findViewById(R.id.fotoUser);
            namaUser = itemView.findViewById(R.id.namaUser);
            messageUser = itemView.findViewById(R.id.messageUser);
            waktuChat = itemView.findViewById(R.id.waktuChat);
            swipeLayout = itemView.findViewById(R.id.swipeLayout);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, itemView.findViewById(R.id.containerChat));
            cardOnlineStatus = itemView.findViewById(R.id.cardOnlineStatus);
        }
    }
}
