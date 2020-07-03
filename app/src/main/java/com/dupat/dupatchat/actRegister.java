package com.dupat.dupatchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.dupat.dupatchat.ProgressBar.LoadingBar;
import com.dupat.dupatchat.function.function;
import com.dupat.dupatchat.model.UserInfo;
import com.dupat.dupatchat.validation.editTextRegister;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.hash.Hashing;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;

import es.dmoral.toasty.Toasty;

public class actRegister extends AppCompatActivity {

    EditText editTextName,editTextEmail,editTextMobile,editTextPassword,editTextUserName;
    TextInputLayout textInputName,textInputEmail,textInputMobile,textInputPassword,textInputUserName;
    DatabaseReference myRef;
    FirebaseAuth myAuth;
    LoadingBar loadingBar;
    int MAX_ID = 1;
    private static final String TAG = "actRegister";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        myRef = FirebaseDatabase.getInstance().getReference("user");
        myAuth = FirebaseAuth.getInstance();
        textInputEmail = (TextInputLayout) findViewById(R.id.textInputEmail);
        textInputUserName = (TextInputLayout) findViewById(R.id.textInputUserName);
        textInputMobile = (TextInputLayout) findViewById(R.id.textInputMobile);
        textInputName = (TextInputLayout) findViewById(R.id.textInputName);
        textInputPassword = (TextInputLayout) findViewById(R.id.textInputPassword);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextEmail.addTextChangedListener(new editTextRegister(textInputEmail).tw);
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextUserName.addTextChangedListener(new editTextRegister(textInputUserName).tw);
        editTextMobile = (EditText) findViewById(R.id.editTextMobile);
        editTextMobile.addTextChangedListener(new editTextRegister(textInputMobile).tw);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextName.addTextChangedListener(new editTextRegister(textInputName).tw);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPassword.addTextChangedListener(new editTextRegister(textInputPassword).tw);
        loadingBar = new LoadingBar(this);
        changeStatusBarColor();
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void onLoginClick(View view){
        startActivity(new Intent(this,actLogin.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    public void actionRegister(View view)
    {
        function.hideKeyboard(this);
        if(TextUtils.isEmpty(editTextName.getText()))
        {
            textInputName.setErrorEnabled(true);
            textInputName.setError("please fill name");
        }
        else if(TextUtils.isEmpty(editTextUserName.getText().toString().trim()))
        {
            textInputUserName.setErrorEnabled(true);
            textInputUserName.setError("please fill username");
        }
        else if(editTextUserName.getText().toString().trim().length() < 6)
        {
            textInputUserName.setErrorEnabled(true);
            textInputUserName.setError("username minimal 6 character");
        }
        else if(TextUtils.isEmpty(editTextEmail.getText()))
        {
            textInputEmail.setErrorEnabled(true);
            textInputEmail.setError("please fill email");
        }
        else if(TextUtils.isEmpty(editTextMobile.getText()))
        {
            textInputMobile.setErrorEnabled(true);
            textInputMobile.setError("please fill phone");
        }
        else if(TextUtils.isEmpty(editTextPassword.getText()))
        {
            textInputPassword.setErrorEnabled(true);
            textInputPassword.setError("please fill password");
        }
        else if(editTextPassword.getText().toString().trim().length() < 6)
        {
            textInputPassword.setErrorEnabled(true);
            textInputPassword.setError("password minimal 6 character");
        }
        else
        {
            final String email = editTextEmail.getText().toString();
            final String name = editTextName.getText().toString();
            final String mobile = editTextMobile.getText().toString();
            final String username = editTextUserName.getText().toString();
            final String password = Hashing.sha256().hashString(editTextPassword.getText().toString(), StandardCharsets.UTF_8).toString();
            loadingBar.startLoading();

            myAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful())
                    {
                        Toast.makeText(actRegister.this, "error "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                        try
                        {
                            throw task.getException();
                        }
                        catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                        {
                            textInputEmail.setError("email not valid");
                            loadingBar.stopLoading();
                        }
                        catch (FirebaseAuthUserCollisionException existEmail)
                        {
                            textInputEmail.setError("email already exist");
                            loadingBar.stopLoading();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            loadingBar.stopLoading();
                        }
                    }
                    else
                    {
                        myRef.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                                        Log.d(TAG, "onDataChange: "+child.getValue());
                                        MAX_ID = Integer.parseInt(child.child("id").getValue().toString())+1;
                                    }
                                }
                                Log.d(TAG, "MAX_ID: "+MAX_ID);
                                UserInfo userInfo = new UserInfo(MAX_ID,name,email,password,mobile,"","",myAuth.getUid(),username,"offline","");
                                myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(userInfo)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toasty.success(actRegister.this,"Success create account",Toasty.LENGTH_LONG,true).show();
                                                editTextEmail.setText("");
                                                editTextMobile.setText("");
                                                editTextName.setText("");
                                                editTextPassword.setText("");
                                                editTextUserName.setText("");
                                                loadingBar.stopLoading();
                                                FirebaseAuth.getInstance().signOut();
                                                onBackPressed();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(actRegister.this, e.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(actRegister.this, "cancel", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,actLogin.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
}

