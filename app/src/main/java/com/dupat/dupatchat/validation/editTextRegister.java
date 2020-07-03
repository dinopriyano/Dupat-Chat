package com.dupat.dupatchat.validation;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class editTextRegister {

    TextInputLayout textInputLayout;

    public editTextRegister(TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }

    public TextWatcher tw = new TextWatcher()
    {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            textInputLayout.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
