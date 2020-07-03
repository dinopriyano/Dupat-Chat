package com.dupat.dupatchat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dupat.dupatchat.R;
import com.dupat.dupatchat.model.modelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.dmoral.prefs.Prefs;

public class adapterChat extends RecyclerView.Adapter<adapterChat.ViewHolder> {

    List<modelChat> list;
    Context ctx;
    String image_url;
    FirebaseUser myAuth;
    DatabaseReference refChat;
    int MSG_TYPE_RIGHT = 1;
    int MSG_TYPE_LEFT = 2;

    public adapterChat(List<modelChat> list, Context ctx, String image_url) {
        this.list = list;
        this.ctx = ctx;
        this.image_url = image_url;
        refChat = FirebaseDatabase.getInstance().getReference("chat");
    }

    @NonNull
    @Override
    public adapterChat.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_chat_right,parent,false);
            return new adapterChat.ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_chat_left,parent,false);
            return new adapterChat.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull adapterChat.ViewHolder holder, int position) {

        modelChat model = list.get(position);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateNow = dateFormat.format(new Date());
        String dateChat;
        if(dateNow.equals(model.date.split(" ")[0]))
        {
            dateChat = model.date.split(" ")[1];
        }
        else
        {
            dateChat = model.date;
        }

        holder.txtDate.setText(dateChat);

        if(position>0)
        {
            modelChat model2 = list.get(position-1);
            String dateChat2;
            if(dateNow.equals(model2.date.split(" ")[0]))
            {
                dateChat2 = model2.date.split(" ")[1];
            }
            else
            {
                dateChat2 = model2.date;
            }

            if(dateChat.equals(dateChat2))
            {
                holder.containerDate.setVisibility(View.GONE);
            }
            else
            {
                holder.containerDate.setVisibility(View.VISIBLE);
            }

            if(model.receiver.equals(myAuth.getUid()))
            {
                if(!model.is_read && !model2.is_read)
                {
                    holder.containerNotSeen.setVisibility(View.GONE);
                }
                else if(!model.is_read)
                {
                    holder.containerNotSeen.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.containerNotSeen.setVisibility(View.GONE);
                }

            }
        }
        else
        {
            holder.containerDate.setVisibility(View.VISIBLE);
            holder.containerNotSeen.setVisibility(View.GONE);
        }

        if(holder.getItemViewType() == MSG_TYPE_LEFT)
        {
            if(position > 0)
            {
                modelChat model2 = list.get(position-1);
                if(model.receiver.equals(myAuth.getUid()) && model2.receiver.equals(myAuth.getUid()))
                {
                    holder.fotoUser.setVisibility(View.INVISIBLE);
                }
                else
                {
                    holder.fotoUser.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                holder.fotoUser.setVisibility(View.VISIBLE);
            }
        }

        if(image_url.equals(""))
        {
            Glide.with(ctx).load(R.drawable.default_person).into(holder.fotoUser);
        }
        else
        {
            Glide.with(ctx).load(image_url).override(300,300).into(holder.fotoUser);
        }

        holder.txtMessage.setText(model.message);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView fotoUser;
        TextView txtMessage,txtDate;
        CardView containerDate;
        LinearLayout containerNotSeen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoUser = itemView.findViewById(R.id.fotoUser);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtDate = itemView.findViewById(R.id.txtDate);
            containerDate = itemView.findViewById(R.id.containerDate);
            containerNotSeen = itemView.findViewById(R.id.containerNotSeen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        myAuth = FirebaseAuth.getInstance().getCurrentUser();
        if(list.get(position).sender.equals(myAuth.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }
}
