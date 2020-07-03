package com.dupat.dupatchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.dupat.dupatchat.ProgressBar.LoadingBar;
import com.dupat.dupatchat.function.function;
import com.dupat.dupatchat.validation.editTextRegister;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.hash.Hashing;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.nio.charset.StandardCharsets;

import es.dmoral.toasty.Toasty;

public class actLogin extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseAuth myAuth;
    EditText editTextEmail,editTextPassword;
    TextInputLayout textInputEmail,textInputPassword;
    LoadingBar loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        changeStatusBarColor();
        myAuth = FirebaseAuth.getInstance();
        firebaseUser = myAuth.getCurrentUser();
        textInputEmail = (TextInputLayout) findViewById(R.id.textInputEmail);
        textInputPassword = (TextInputLayout) findViewById(R.id.textInputPassword);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextEmail.addTextChangedListener(new editTextRegister(textInputEmail).tw);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPassword.addTextChangedListener(new editTextRegister(textInputPassword).tw);
        loadingBar = new LoadingBar(this);

        if(firebaseUser != null)
        {
            startActivity(new Intent(actLogin.this,actHome.class));
            finish();
        }

    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    public void btnRegister(View view)
    {
        startActivity(new Intent(this,actRegister.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }

    public void actionLogin(View view)
    {
        function.hideKeyboard(this);
        if(TextUtils.isEmpty(editTextEmail.getText().toString().trim()))
        {
            textInputEmail.setErrorEnabled(true);
            textInputEmail.setError("please fill email");
        }
        else if(TextUtils.isEmpty(editTextPassword.getText().toString().trim()))
        {
            textInputPassword.setErrorEnabled(true);
            textInputPassword.setError("please fill password");
        }
        else
        {
            loadingBar.startLoading();
            String email = editTextEmail.getText().toString().trim();
            String pass = Hashing.sha256().hashString(editTextPassword.getText().toString(), StandardCharsets.UTF_8).toString();

            myAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        FirebaseMessaging.getInstance().subscribeToTopic(myAuth.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toasty.success(actLogin.this,"Login success",Toasty.LENGTH_LONG,true).show();
                                    loadingBar.stopLoading();
                                    startActivity(new Intent(actLogin.this,actHome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                }
                            }
                        });
                    }
                    else
                    {
                        Toasty.error(actLogin.this,"Account not valid",Toasty.LENGTH_LONG,true).show();
                        loadingBar.stopLoading();
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
