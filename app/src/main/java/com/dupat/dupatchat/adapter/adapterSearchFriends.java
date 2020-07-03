package com.dupat.dupatchat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dupat.dupatchat.R;
import com.dupat.dupatchat.actRegister;
import com.dupat.dupatchat.model.UserInfo;
import com.dupat.dupatchat.model.modelFriend;
import com.dupat.dupatchat.retrofit.APIList;
import com.dupat.dupatchat.retrofit.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class adapterSearchFriends extends RecyclerView.Adapter<adapterSearchFriends.ViewHolder> {

    List<UserInfo> list;
    Context ctx;
    int MAX_ID = 1;
    FirebaseUser myAuth;
    DatabaseReference myRef;
    String status = "no friend";

    public adapterSearchFriends(List<UserInfo> list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
        myAuth = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("user");
    }

    @NonNull
    @Override
    public adapterSearchFriends.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_search_friends,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterSearchFriends.ViewHolder holder, int position) {

        UserInfo model = list.get(position);
        Toast.makeText(ctx, model.photo, Toast.LENGTH_SHORT).show();

        myRef.child(model.uid).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        if(ds.child("user_uid").getValue().toString().equals(myAuth.getUid()))
                        {
                            status = ds.child("status").getValue().toString();
                        }
                        else
                        {
                            status = "no friend";
                        }
                    }
                }
                else
                {
                    status = "no friend";
                }

                if(status.equals("no friend"))
                {
                    holder.btnRequested.setVisibility(View.INVISIBLE);
                    holder.btnDelete.setVisibility(View.INVISIBLE);
                    holder.btnAdd.setVisibility(View.VISIBLE);
                }
                else if(status.equals("pending"))
                {
                    holder.btnRequested.setVisibility(View.VISIBLE);
                    holder.btnDelete.setVisibility(View.INVISIBLE);
                    holder.btnAdd.setVisibility(View.INVISIBLE);
                }
                else if(status.equals("friend"))
                {
                    holder.btnRequested.setVisibility(View.INVISIBLE);
                    holder.btnDelete.setVisibility(View.VISIBLE);
                    holder.btnAdd.setVisibility(View.INVISIBLE);
                }

                if(model.photo.equals(""))
                {
                    Glide.with(ctx).load(R.drawable.default_person).into(holder.fotoUser);
                }
                else
                {
                    Glide.with(ctx).load(model.photo).override(400,400).into(holder.fotoUser);
                }

                holder.namaUser.setText(model.name);
                holder.usernameUser.setText(model.username);
                holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestFriend(model.uid,myAuth.getUid(),model.username);
                        holder.btnRequested.setVisibility(View.VISIBLE);
                        holder.btnDelete.setVisibility(View.INVISIBLE);
                        holder.btnAdd.setVisibility(View.INVISIBLE);
                    }
                });
                holder.btnRequested.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFriend(model.uid,myAuth.getUid());
                        holder.btnRequested.setVisibility(View.INVISIBLE);
                        holder.btnDelete.setVisibility(View.INVISIBLE);
                        holder.btnAdd.setVisibility(View.VISIBLE);
                    }
                });
                holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFriend(model.uid,myAuth.getUid());
                        holder.btnRequested.setVisibility(View.INVISIBLE);
                        holder.btnDelete.setVisibility(View.INVISIBLE);
                        holder.btnAdd.setVisibility(View.VISIBLE);
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

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView fotoUser;
        TextView namaUser,usernameUser;
        LinearLayout btnAdd,btnRequested,btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoUser = itemView.findViewById(R.id.fotoUser);
            namaUser = itemView.findViewById(R.id.namaUser);
            usernameUser = itemView.findViewById(R.id.usernameUser);
            btnAdd = itemView.findViewById(R.id.btnAddFriend);
            btnDelete = itemView.findViewById(R.id.btnDeleteFriend);
            btnRequested = itemView.findViewById(R.id.btnRequestedFriend);
        }
    }

    void deleteFriend(String user_uid,String uid)
    {
        myRef.child(user_uid).child("friends").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    ds.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            myRef.child(uid).child("friends").child(user_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists())
                                    {
                                        for(DataSnapshot dsp : dataSnapshot.getChildren())
                                        {
                                            dsp.getRef().removeValue();
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

    void requestFriend(String user_uid, String uid, String user_username)
    {
        myRef.child(user_uid).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                        MAX_ID = Integer.parseInt(child.child("zid").getValue().toString())+1;
                    }
                }

                myRef.child(myAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserInfo info = dataSnapshot.getValue(UserInfo.class);
                        modelFriend model = new modelFriend(MAX_ID,uid,"pending",dataSnapshot.child("username").getValue().toString());
                        myRef.child(user_uid).child("friends").child(uid).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Map<String,String> data = new HashMap<>();
                                data.put("topic",user_uid);
                                data.put("title","Dupat Chat");
                                data.put("body","["+user_username+"]: "+info.username+" send friend request");
                                data.put("uid",info.uid);
                                data.put("message","["+user_username+"]: "+info.username+" send friend request");
                                data.put("type","friend request");
                                data.put("photo",info.photo.equals("")?"https://dinopriyano.my.id/uploads/user_photo/default_person.jpg":info.photo);
                                Call<String> call = RetrofitClient.getInstance().getApi().pushNotif(APIList.pushNotif,data);
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if(response.isSuccessful())
                                        {
                                            Toasty.success(ctx,"Success send friend request",Toasty.LENGTH_LONG,true).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {

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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
