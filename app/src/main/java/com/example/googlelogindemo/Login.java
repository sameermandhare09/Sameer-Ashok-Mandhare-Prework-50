package com.example.googlelogindemo;

import android.annotation.TargetApi;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

public class Login extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN = 9001;
    SignInButton signInButton;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    TextView txtregister;
    Button btnsign;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Set the dimensions of the sign-in button.
         signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        txtregister = findViewById(R.id.txtregister);

        mLoginFormView = findViewById(R.id.login_form);
        btnsign = findViewById(R.id.btnsign);

        mPasswordView = (EditText) findViewById(R.id.password);
        sharedPreferences = getSharedPreferences("user",0);
        editor = sharedPreferences.edit();

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);





        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(Login.this);
                if (alreadyloggedAccount != null) {

                             /*
          Sign-out is initiated by simply calling the googleSignInClient.signOut API. We add a
          listener which will be invoked once the sign out is the successful
           */
                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Login.this, "Log out", Toast.LENGTH_SHORT).show();
                            //On Succesfull signout we navigate the user back to LoginActivity
                      /*      Intent intent=new Intent(ProfileActivity.this,Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);*/
                        }
                    });
                } else {
                    Log.d("GoogleLoginDemo", "Not logged in");
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);

                }

/*
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);*/

            }
        });




        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        btnsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });



        txtregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Registration.class);
                startActivity(intent);

            }
        });



    }
    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (alreadyloggedAccount != null) {

            Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();
            onLoggedIn(alreadyloggedAccount);
        }else if(sharedPreferences.getBoolean("Login",false)){

            Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Login.this,Home.class);
            startActivity(intent);
            finish();

        }
        else {
            Log.d("GoogleLoginDemo", "Not logged in");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Log.e("signIn_requestCode:", "101" );
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {


        Log.e("completedTask",new Gson().toJson(completedTask));

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.e("signInResult account:", "" + account);
            // Signed in successfully, show authenticated UI.
           // updateUI(account);



            Intent intent = new Intent(Login.this,Home.class);
            startActivity(intent);


            finishAffinity();



        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("signInResult failed:", " " + e.getLocalizedMessage());
           // updateUI(null);
        }
    }

    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {
    /*    Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.GOOGLE_ACCOUNT, googleSignInAccount);

        startActivity(intent);
        finish();*/

        Intent intent = new Intent(Login.this,Home.class);
        startActivity(intent);
        finish();

    }



    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email) && !isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        } else  if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);


            UserRepository userRepository = new UserRepository(Login.this);
            userRepository.login(email,password).observe(Login.this, new Observer<User>() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onChanged(@Nullable User user) {


                    if(user!=null){

                        Toast.makeText(Login.this, "Login success", Toast.LENGTH_SHORT).show();
                        Log.e("login data",user.getEmail());

                        editor.putBoolean("Login",true);
                        editor.commit();

                        Intent intent = new Intent(Login.this,Home.class);
                        Bundle bundle = new Bundle();
                        bundle.putLong("userid",user.getUserId());
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finishAffinity();

                    }else {

                        Toast.makeText(Login.this, "Login unsuccessful", Toast.LENGTH_SHORT).show();
                    }


                }
            });



        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.length()>0;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

}
//CFUPM7932N



