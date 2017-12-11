package com.example.bohan.flyingpig;

import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;

/**
 * Created by bohan on 12/4/17.
 */

public class MenuActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{
    int I1=0;
    int I2=0;
    int I3=0;
    int I4=0;
    int I5=0;
    int I6=0;
    double totalprice=0;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_customer);
       navigationView.setNavigationItemSelectedListener(this);
        Button m1=(Button) findViewById(R.id.m1);
        m1.setOnClickListener(this);
        Button p1=(Button) findViewById(R.id.p1);
        p1.setOnClickListener(this);
        Button m2=(Button) findViewById(R.id.m2);
        m2.setOnClickListener(this);
        Button p2=(Button) findViewById(R.id.p2);
        p2.setOnClickListener(this);
        Button m3=(Button) findViewById(R.id.m3);
        m3.setOnClickListener(this);
        Button p3=(Button) findViewById(R.id.p3);
        p3.setOnClickListener(this);
        Button m4=(Button) findViewById(R.id.m4);
        m4.setOnClickListener(this);
        Button p4=(Button) findViewById(R.id.p4);
        p4.setOnClickListener(this);
        Button m5=(Button) findViewById(R.id.m5);
        m5.setOnClickListener(this);
        Button p5=(Button) findViewById(R.id.p5);
        p5.setOnClickListener(this);
        Button m6=(Button) findViewById(R.id.m6);
        m6.setOnClickListener(this);
        Button p6=(Button) findViewById(R.id.p6);
        p6.setOnClickListener(this);
        //高頔------3：58
        ImageButton g1=(ImageButton) findViewById(R.id.D1);
        g1.setOnClickListener(this);
        ImageButton g2=(ImageButton) findViewById(R.id.D2);
        g2.setOnClickListener(this);
        ImageButton g3=(ImageButton) findViewById(R.id.D3);
        g3.setOnClickListener(this);
        ImageButton g4=(ImageButton) findViewById(R.id.D4);
        g4.setOnClickListener(this);
        ImageButton g5=(ImageButton) findViewById(R.id.D5);
        g5.setOnClickListener(this);

        Button deslect=(Button) findViewById(R.id.deslect);
        deslect.setOnClickListener(this);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.client_menu_logout)
        {
            mAuth.signOut();
            Intent turn = new Intent(MenuActivity.this, CheckUserActivity.class);

            startActivity(turn);
        }
//        if(id == R.id.client_menu_viewCart)
//        {
//            Intent turn = new Intent(MenuActivity.this, ViewCartActivity.class);
//            turn.putExtra("Cart","View");
//            startActivity(turn);
//        }
        if(id ==R.id.client_orderHistory) {
            Intent turn = new Intent(MenuActivity.this, MyOrderActivity.class);
            startActivity(turn);

        }


        return true;
    }


    public void onClick(View v){
        TextView picture11=(TextView) findViewById(R.id.amount1);
        TextView picture12=(TextView) findViewById(R.id.amount2);
        TextView picture13=(TextView) findViewById(R.id.amount3);
        TextView picture21=(TextView) findViewById(R.id.amount4);
        TextView picture31=(TextView) findViewById(R.id.amount5);
        TextView picture41=(TextView) findViewById(R.id.amount6);
        TextView wholeprice=(TextView) findViewById(R.id.wholeprice);
        //高頔----3：58
        AlertDialog.Builder dialog=new AlertDialog.Builder(MenuActivity.this);
        switch (v.getId()){
            case R.id.m1:
                if(I1>0){
                    I1--;
                    picture11.setText(""+I1);
                }
                else{
                    Toast.makeText(MenuActivity.this,"Can't Remove",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.p1:
                I1++;
                picture11.setText(""+I1);
                break;


            case R.id.m2:
                if(I2>0){
                    I2--;
                    picture12.setText(""+I2);
                }
                else{
                    Toast.makeText(MenuActivity.this,"Can't Remove",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.p2:
                I2++;
                picture12.setText(""+I2);
                break;


            case R.id.m3:
                if(I3>0){
                    I3--;
                    picture13.setText(""+I3);
                }
                else{
                    Toast.makeText(MenuActivity.this,"Can't Remove",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.p3:
                I3++;
                picture13.setText(""+I3);
                break;


            case R.id.m4:
                if(I4>0){
                    I4--;
                    picture21.setText(""+I4);
                }
                else{
                    Toast.makeText(MenuActivity.this,"Can't Remove",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.p4:
                I4++;
                picture21.setText(""+I4);
                break;


            case R.id.m5:
                if(I5>0){
                    I5--;
                    picture31.setText(""+I5);
                }
                else {
                    Toast.makeText(MenuActivity.this,"Can't Remove",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.p5:
                I5++;
                picture31.setText(""+I5);
                break;

            case R.id.m6:
                if(I6>0){
                    I6--;
                    picture41.setText(""+I6);
                }
                else{
                    Toast.makeText(MenuActivity.this,"Can't Remove",Toast.LENGTH_LONG ).show();
                }
                break;
            case R.id.p6:
                I6++;
                picture41.setText(""+I6);
                break;
            case R.id.deslect:
                I1=0;
                I2=0;
                I3=0;
                I4=0;
                I5=0;
                I6=0;
                picture11.setText(""+I1);
                picture12.setText(""+I2);
                picture13.setText(""+I3);
                picture21.setText(""+I4);
                picture31.setText(""+I5);
                picture41.setText(""+I6);
                break;
                //直接添加----高頔3：57
            case R.id.D1:
                dialog.setTitle("Burger $7.15").setMessage("A juicy, 100% pure beef burger patty topped with a tangy pickle, chopped onions, ketchup and mustard. Our simple, juicy hamburgers are made with absolutely no fillers, additives or preservatives, and seasoned with just a pinch of salt and pepper.").show();
                break;
            case R.id.D2:
                dialog.setTitle("DoubleCheeseBurger $7.15").setMessage("Two quarter pound* Ingredients: 100% Pure USDA Inspected Beef; No Fillers, No Extenders. Prepared with Grill Seasoning (Salt, Black Pepper). Layered with two slices of melty cheese, slivered onions and tangy pickles on sesame seed bun.\n" +
                        " \n" +
                        " *Weight before cooking at least 4 oz.").show();
                break;
            case R.id.D3:
                dialog.setTitle("BBQBurger $7.15").setMessage("Layered with BBQ sauce made with sweet onions, thick-cut Applewood smoked bacon, grilled & crispy onions, and smooth white cheddar*. On a 1/4lb.** of 100% pure beef with absolutely no fillers, additives, or preservatives. Choose an artisan roll or sesame seed bun.").show();
                break;
            case R.id.D4:
                dialog.setTitle("Chicken $7.8").setMessage("Our tender, juicy Chicken McNuggets are made with 100% white meat chicken and no artificial colors, flavors and now no artificial preservatives.").show();
                break;
            case R.id.D5:
                dialog.setTitle("FrenchFries $2.6").setMessage("Our French fries are born from premium potatoes such as the Russet Burbank and the Shepody. With zero grams of trans fat per labeled serving, these epic fries are crispy and golden on the outside and fluffy on the inside.").show();
                break;
            case R.id.D6:
                dialog.setTitle("Onion Rings $3.25").setMessage("Freshly prepared side salad with crisp chopped romaine, baby spinach, baby kale, red leaf lettuce, ribbon cut carrots, tasty grape tomatoes and served with your choice of Newman's Own® Dressing.").show();
                break;
        }

        DecimalFormat df = new DecimalFormat("###.00");
        totalprice=I1*7.15+I2*7.15+I3*7.15+I4*7.8+I5*2.6+I6*3.25;//高頔----改了价钱
        wholeprice.setText("Total Price: "+df.format(totalprice));
    }
    public void onclicksubmit(View view){//add this
        Intent viewCart=new Intent(MenuActivity.this, ViewCartActivity.class);

        viewCart.putExtra("Burger1",I1);
        viewCart.putExtra("Burger2",I2);
        viewCart.putExtra("Burger3",I3);
        viewCart.putExtra("CH",I4);
        viewCart.putExtra("FF",I5);
        viewCart.putExtra("OR",I6);
        startActivity(viewCart);
    }
    public void onclickhistory(View view){
//        Intent historyLayout=new Intent(MenuActivity.this,HistoryActivity.class);
//        startActivity(historyLayout);
    }
}



