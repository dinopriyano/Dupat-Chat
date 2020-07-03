package com.dupat.dupatchat.helper;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.dupat.dupatchat.R;
import com.dupat.dupatchat.actChat;
import com.dupat.dupatchat.model.UserInfo;
import com.dupat.dupatchat.model.modelFriend;
import com.kyleduo.blurpopupwindow.library.BlurPopupWindow;
import com.theophrast.ui.widget.SquareImageView;

public class popUpProfile{

    Activity activity;
    UserInfo userInfo;
    AlertDialog alertDialog;

    public popUpProfile(Activity activity, UserInfo userInfo) {
        this.activity = activity;
        this.userInfo = userInfo;
    }

    public void showPupUp()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = activity.getLayoutInflater().inflate(R.layout.layout_pop_up_profile, null);
        builder.setView(view);
        builder.setCancelable(true);

        alertDialog = builder.create();
        alertDialog.show();
        SquareImageView squareImageView = (SquareImageView) ((AlertDialog) alertDialog).findViewById(R.id.fotoUser);
        TextView namaUser = (TextView) ((AlertDialog) alertDialog).findViewById(R.id.namaUser);
        namaUser.setText(userInfo.name);
        if(userInfo.photo.equals(""))
        {
            Glide.with(activity).load(R.drawable.default_person).into(squareImageView);
        }
        else
        {
            Glide.with(activity).load(userInfo.photo).override(600,600).centerCrop().into(squareImageView);
        }
        ImageView btnChat = (ImageView) ((AlertDialog) alertDialog).findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, actChat.class);
                intent.putExtra("uid",userInfo.uid);
                activity.startActivity(intent);
            }
        });
    }

    public void stopPopup()
    {
        alertDialog.dismiss();
    }
}
