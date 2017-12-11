package com.example.bohan.flyingpig;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bohan.flyingpig.entity.Chef;
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

public class UserSignUpActivity extends AppCompatActivity implements View.OnClickListener{
    Button creationButton;
    Button cancelButton;


    TextView userEmail;
    TextView userPassword;


    Customer user;

    View thisView;

    Chef chef;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        creationButton = (Button) findViewById(R.id.bUserCreation);
        cancelButton = (Button) findViewById(R.id.bUserCancelCreation);

        userEmail = (TextView) findViewById(R.id.tNewUserName);
        userPassword = (TextView) findViewById(R.id.tNewUserPassword);


        thisView = (View) findViewById(R.id.drawer_layout_new);
        thisView.getBackground().setAlpha(50);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        creationButton.getBackground().setAlpha(0);
        cancelButton.getBackground().setAlpha(0);


        creationButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();




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

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        if(mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }




    public void onClick(View v)
    {
        switch (v.getId()){

            case R.id.bUserCreation:
                createUserAccount();
                break;

            case R.id.bUserCancelCreation:
                Intent back =new Intent(UserSignUpActivity.this,CustomerLogActivity.class);
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





    private void createUserAccount() {
        if (!checkFormFields())
            return;

        final String email = userEmail.getText().toString();
        final String password = userPassword.getText().toString();

        // TODO: Create the user account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            //Toast.makeText(ClientLoginActivity.this, "User was created", Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(R.id.drawer_layout_new), R.string.user_aCreate,
                                    Snackbar.LENGTH_SHORT)
                                    .show();


                            user = new Customer();
                            String nameA = email.substring(0,email.indexOf("@"));
                            String userName = EncodeString(nameA);
                            user.setName(userName);
                            user.setEmail(email);
                            mDatabase.child("CustomerList").push().setValue(user);


                            // use for create chef account
                            /*
                            chef = new Chef();
                            String nameA = email.substring(0,email.indexOf("@"));
                            String chefName = EncodeString(nameA);
                            chef.setEmail(email);
                            chef.setPassword(password);
                            mDatabase.child("ChefInfo").child(chefName).setValue(chef);
                            */

                            Intent showMenu = new Intent(UserSignUpActivity.this, CustomerLogActivity.class);

                            startActivity(showMenu);
                        }
                        else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            //Toast.makeText(ClientLoginActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(R.id.drawer_layout_customer), R.string.user_already_exists,
                                    Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                        else
                        {
                            //Toast.makeText(ClientLoginActivity.this, "Account creation failed", Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(R.id.drawer_layout_customer), R.string.user_name_email,
                                    Snackbar.LENGTH_SHORT)
                                    .show();
                        }

                    }
                });


    }



    private void signUserOut() {
        // TODO: sign the user out
        final String email = userEmail.getText().toString();
        mAuth.signOut();
        mDatabase.child("customers").child(EncodeString(email)).setValue("SignOut");

    }

    public static String EncodeString(String string) {
        return string.replace(".", " ");
    }

}

