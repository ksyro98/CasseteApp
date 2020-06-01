package com.syroniko.casseteapp.LogInSignUp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.syroniko.casseteapp.R;
import com.syroniko.casseteapp.firebase.Auth;
import com.syroniko.casseteapp.firebase.AuthCallback;

import org.jetbrains.annotations.Nullable;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText resetEmailAddress;
    TextView sendResetTextView;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetEmailAddress = findViewById(R.id.reset_email_edittext);
        sendResetTextView = findViewById(R.id.send_reset_pass_textview);

        TextView tx = findViewById(R.id.recoveraccounString_resect_act);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/montsextrathic.ttf");
        tx.setTypeface(custom_font);

        resetEmailAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ColorStateList colorStateList = ColorStateList.valueOf(Color.parseColor("#0fa59b"));
                ViewCompat.setBackgroundTintList(resetEmailAddress,colorStateList);
            }
        });

        sendResetTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEmail = resetEmailAddress.getText().toString();
                if (userEmail.length() == 0){
                    Toast.makeText(ResetPasswordActivity.this, "Please fill your email address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Auth.INSTANCE.resetPassword(userEmail,
                        new AuthCallback() {
                            @Override
                            public void onSuccess(@Nullable String uid) {
                                Toast.makeText(ResetPasswordActivity.this, "Email sent. Check your inbox!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(ResetPasswordActivity.this, "A problem occurred while sending your email. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
