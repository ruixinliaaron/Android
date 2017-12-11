package com.example.bohan.flyingpig;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bohan.flyingpig.entity.Chef;
import com.example.bohan.flyingpig.entity.Order;
import com.example.bohan.flyingpig.entity.OrderItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bohan on 12/4/17.
 */

public class ChefLogActivity extends AppCompatActivity implements View.OnClickListener{
    TextView chefEmail;
    TextView chefPassword;
    Button chefLogin;
    Button chefCancel;
    TextView chefStatus;

    View thisView;

    Chef chef;

    public FirebaseDatabase database;
    public DatabaseReference databaseReference;



    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheflog);
        chefEmail = (TextView) findViewById(R.id.tChefName);
        chefPassword = (TextView) findViewById(R.id.tChefPassword);
        chefLogin = (Button) findViewById(R.id.bChefLogin);
        chefCancel = (Button) findViewById(R.id.bChefCancel);
        chefLogin.setOnClickListener(this);
        chefCancel.setOnClickListener(this);

        thisView = (View) findViewById(R.id.drawer_layout_chef);
        thisView.getBackground().setAlpha(50);

        chefLogin.getBackground().setAlpha(0);
        chefCancel.getBackground().setAlpha(0);

        mAuth = FirebaseAuth.getInstance();

        getChefInfo();
        //chef = new Chef();

        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();

            }
        };


    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bChefLogin:
                signChefIn();
                break;
            case R.id.bChefCancel:
                Intent back = new Intent(ChefLogActivity.this,CheckUserActivity.class);
                startActivity(back);
        }

    }

    private void signChefIn() {
        if (!checkFormFields())
            return;


        final String email = chefEmail.getText().toString();
        String password = chefPassword.getText().toString();


        boolean statusEmail = chef.getEmail().equals(email);
        boolean statusPassword = chef.getPassword().equals(password);
        if(statusEmail && statusPassword) {
            /*
            Snackbar.make(findViewById(R.id.drawer_layout_chef), R.string.chef_Login,
                    Snackbar.LENGTH_LONG)
                    .show();
                    */
            Toast.makeText(ChefLogActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
            Intent chef = new Intent(ChefLogActivity.this, ChefActivity.class);
            startActivity(chef);
        }
        else
         {

             Snackbar.make(findViewById(R.id.drawer_layout_chef), R.string.chef_Login_Failed,
                     Snackbar.LENGTH_LONG)
                     .show();
             /*
             Toast.makeText(ChefLogActivity.this, "Signed In Failed", Toast.LENGTH_SHORT).show();
             */
        }


        /*
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Snackbar.make(findViewById(R.id.drawer_layout_chef), R.string.chef_Login,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                            Toast.makeText(ChefLogActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                            Intent chef = new Intent(ChefLogActivity.this, ChefActivity.class);
                            startActivity(chef);
                        }
                        else
                        {
                            Snackbar.make(findViewById(R.id.drawer_layout_chef), R.string.chef_Login_Failed,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                             Toast.makeText(ChefLogActivity.this, "Signed In Failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                */

    }

    private boolean checkFormFields() {
        String email, password;

        email = chefEmail.getText().toString();
        password = chefPassword.getText().toString();

        if (email.isEmpty()) {
            chefEmail.setError("Email Required");
            return false;
        }
        if (password.isEmpty()){
            chefPassword.setError("Password Required");
            return false;
        }

        return true;
    }


    private void updateStatus(String stat) {
        chefStatus = (TextView)findViewById(R.id.chefSignStatus);
        chefStatus.setText(stat);
    }

    public void getChefInfo() {

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();



        databaseReference.child("ChefInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                chef = new Chef();
                for (DataSnapshot child : snapshot.getChildren()) {
                    chef = child.getValue(Chef.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

                });
            }




}
