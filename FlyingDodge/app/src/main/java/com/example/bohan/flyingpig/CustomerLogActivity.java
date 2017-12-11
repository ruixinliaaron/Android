package com.example.bohan.flyingpig;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bohan.flyingpig.entity.Customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by bohan on 12/4/17.
 */

public class CustomerLogActivity extends AppCompatActivity implements View.OnClickListener{
    Button loginButton;
    Button cancelButton;


    TextView userEmail;
    TextView userPassword;
    TextView userSingUp;

    Customer user;

    View thisView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerlog);

        loginButton = (Button) findViewById(R.id.bUserLogin);
        cancelButton = (Button) findViewById(R.id.bUserCancel);
        //createButton = (Button) findViewById(R.id.bUserCreate);
        userEmail = (TextView) findViewById(R.id.tUserName);
        userPassword = (TextView) findViewById(R.id.tUserPassword);
        userSingUp = (TextView) findViewById(R.id.newUserSignUp);

        thisView = (View) findViewById(R.id.drawer_layout_customer);
        thisView.getBackground().setAlpha(50);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        loginButton.getBackground().setAlpha(0);
        cancelButton.getBackground().setAlpha(0);


        loginButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();

        userSingUp.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(CustomerLogActivity.this, UserSignUpActivity.class);
                startActivity(intent);
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();

            }
        };




    }


    @Override
    public void onStart() {
        super.onStart();
        // TODO: add the AuthListener
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // TODO: Remove the AuthListener
        if(mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }




    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.bUserLogin:
                signUserIn();
                break;



            case R.id.bUserCancel:
                Intent back =new Intent(CustomerLogActivity.this,CheckUserActivity.class);
                startActivity(back);
                break;
        }

    }

    private boolean checkFormFields() {
        String email, password;

        email = userEmail.getText().toString();
        password = userPassword.getText().toString();

        if (email.isEmpty()) {
            userEmail.setError("Email Required");
            return false;
        }
        if (password.isEmpty()){
            userPassword.setError("Password Required");
            return false;
        }

        return true;
    }



    private void signUserIn() {
        if (!checkFormFields())
            return;

        final String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        // TODO: sign the user in with email and password credentials
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            //Toast.makeText(ClientLoginActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(R.id.drawer_layout_customer), R.string.user_signIn,
                                    Snackbar.LENGTH_SHORT)
                                    .show();


                            //mDatabase.child("customer").child(EncodeString(email)).setValue("SignIn");
                            Intent showMenu = new Intent(CustomerLogActivity.this, MenuActivity.class);
                            showMenu.putExtra("userEmail", email);
                            startActivity(showMenu);
                        }
                        else
                        {
                            //Toast.makeText(ClientLoginActivity.this, "Signed In Failed", Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(R.id.drawer_layout_customer), R.string.user_signFailed,
                                    Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                        //updateStatus();
                    }
                });

    }





    private void signUserOut() {

        final String email = userEmail.getText().toString();
        mAuth.signOut();
        mDatabase.child("customers").child(EncodeString(email)).setValue("SignOut");

    }

    public static String EncodeString(String string) {
        return string.replace(".", " ");
    }

}

