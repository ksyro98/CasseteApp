package com.syroniko.casseteapp.LogInSignUp;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.syroniko.casseteapp.R;
import com.syroniko.casseteapp.MainClasses.User;

public class SignUpActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST_SIGN_UP = 271;

    private FirebaseAuth mAuth;
    private Place place = null;

    private EditText nameEditText;
    private EditText nicknameEditText;
    private EditText emailET;
    private EditText password2;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        nameEditText = findViewById(R.id.name_et_sign_up);
        emailET = findViewById(R.id.email_et_signup1);
        password = findViewById(R.id.password_et_signup);
        password2 = findViewById(R.id.password_confirm_et_signup);


        TextView tx = (TextView)findViewById(R.id.create_an_account_string);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/montsextrathic.ttf");

        tx.setTypeface(custom_font);

        TextView next=(TextView) findViewById(R.id.nextfromsignup_togenres);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pass=password.getText().toString();
                if(nameEditText.getText().toString().equals("")||emailET.getText().toString().equals("")|| password.getText().toString().equals("")){
                    Toast.makeText(SignUpActivity.this, "Please fill all the info", Toast.LENGTH_SHORT).show();
                }
                else if(!password.getText().toString().equals(password2.getText().toString())){
                    Toast.makeText(SignUpActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                }
                else if (pass.length()<6){
                    Toast.makeText(SignUpActivity.this, "Password must have at least 6 digits", Toast.LENGTH_SHORT).show();
                }
                else {
                    User userInstance = new User(nameEditText.getText().toString(),  emailET.getText().toString(), null, null, null, null, null);
                    Intent intent = new Intent(SignUpActivity.this, PickGenresSignUpActivity.class);
                    intent.putExtra("User", userInstance);
                    intent.putExtra("Password", pass);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });







        //  final String[] resArray = getResources().getStringArray(R.array.genres_array);
      // final List<String> resArrayList = Arrays.asList(resArray);
      //  final ArrayList<String> musicTypes = new ArrayList<>(resArrayList);
//        final ArrayList<CharSequence> musicTypes = new ArrayList<>(R.array.genres_array);
      //  musicTypes.remove(0);
      //  final boolean[] checkedMusicTypes = new boolean[musicTypes.size()];
       // final ArrayList<String> userMusicTypes = new ArrayList<>();



   //    final Activity activity = this;




// ...
// Initialize Firebase Auth
       // mAuth = FirebaseAuth.getInstance();

    }
    }
  /*  @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
      FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
    }
*/
 //   @Override
 /*   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PLACE_PICKER_REQUEST_SIGN_UP && resultCode == RESULT_OK){
            place = PlacePicker.getPlace(this, data);
        }
    }
}
*/
/* signUpBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailET.getText().toString().equals("")|| password.getText().toString().equals("")){
                    Toast.makeText(SignUpActivity.this, "Please fill your account details", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.getText().toString().equals(password2.getText().toString())) {


                    mAuth.createUserWithEmailAndPassword(emailET.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("TAG", "createUserWithEmail:success");
                                        //   updateUI(user);

                                        User userInstance = new User(nameEditText.getText().toString(),  emailET.getText().toString(), null, null, null);
                                        Log.d("STAG", userInstance.toString());

                                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                        String uid = FirebaseAuth.getInstance().getUid();
                                        if(uid != null)
                                            firebaseFirestore.collection("users").document(uid).set(userInstance);
                                    }
                                    else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        //updateUI(null);
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(SignUpActivity.this, "Make sure that passwords match", Toast.LENGTH_SHORT).show();
                }
            }
        });
*/


