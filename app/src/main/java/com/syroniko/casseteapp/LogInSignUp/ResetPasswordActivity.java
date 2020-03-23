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

public class ResetPasswordActivity extends AppCompatActivity {

    EditText resetEmailAddress;
    TextView sendResetTextView;
    String userEmail;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        auth = FirebaseAuth.getInstance();
        TextView tx = (TextView)findViewById(R.id.recoveraccounString_resect_act);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/montsextrathic.ttf");

        tx.setTypeface(custom_font);
        resetEmailAddress=(EditText)findViewById(R.id.reset_email_edittext);
        sendResetTextView=findViewById(R.id.send_reset_pass_textview);
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
                                userEmail=resetEmailAddress.getText().toString();
                auth.sendPasswordResetEmail(userEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TAG", "Email sent.");
                                }
                                else{
                                    Toast.makeText(ResetPasswordActivity.this, "prob", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }
}
