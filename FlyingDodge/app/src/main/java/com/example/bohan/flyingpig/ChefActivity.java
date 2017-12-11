package com.example.bohan.flyingpig;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.bohan.flyingpig.adapter.ExpandableListAdapter;
import com.example.bohan.flyingpig.entity.MessageToCustomer;
import com.example.bohan.flyingpig.entity.Order;
import com.example.bohan.flyingpig.entity.OrderItem;
import com.example.bohan.flyingpig.entity.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChefActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ExpandableListView expandableList;
    private ExpandableListAdapter adapter;
    private List<Order> orderList;
    private HashMap<Order, List<String>> listHash;
    private static HashMap<Integer, Boolean> isSelected;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private boolean flag = true;
    private Spinner spinner;
    private List<Product> productList;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        //blur the background:
//        LinearLayout mContainerView = (LinearLayout) findViewById(R.id.container);
//        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.burger);
//        Bitmap blurredBitmap = BlurBuilder.blur( this, originalBitmap );
//        mContainerView.setBackground(new BitmapDrawable(getResources(), blurredBitmap));

        //populate spinner's content:
        spinner = (Spinner) findViewById(R.id.spinner);
        String[] curs = getResources().getStringArray(R.array.response_arrays);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.personal_spinner,curs);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);


        isSelected = new HashMap<>();
        orderList = new ArrayList<>();
        listHash = new HashMap<>();

        storeOrder();
        populateListView();

        expandableList = (ExpandableListView) findViewById(R.id.orderlist);
        expandableList
                .setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        // only expand one group:
                        for (int i = 0, count = expandableList.getCount(); i < count; i++) {
                            if (groupPosition != i) {
                                expandableList.collapseGroup(i);
                            }
                        }
                    }
                });

        productList = new ArrayList<>();
        storeProduct();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.inventory) {
            Intent intent = new Intent(ChefActivity.this, InventoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.logout) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Intent turn = new Intent(ChefActivity.this,CheckUserActivity.class);
            startActivity(turn);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //store order from firebase:
    public void storeOrder() {

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();


        databaseReference.child("Order").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderList = new ArrayList<>();
                listHash = new HashMap<>();
                //this part get all orders which are stored in the cloud to the local ArrayList:
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Order or = child.getValue(Order.class);
                    orderList.add(or);
                    List<String> orderItems = new ArrayList<>();
                    for (OrderItem oi : or.getOrderItemList()) {
                        String item = oi.getProductName() + "   " + "Quantity:" + oi.getQuantity() + "   " + "Unit Price:" + oi.getActualPrice() + "$";
                        orderItems.add(item);
                    }
                    listHash.put(or, orderItems);
                }
                populateListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //populate expandablelistview:
    public void populateListView() {
        //list cannot directly be passed into listview, so we need adapter:
        adapter = new ExpandableListAdapter(ChefActivity.this, orderList, listHash, isSelected);
        expandableList = (ExpandableListView) findViewById(R.id.orderlist);
        expandableList.setAdapter(adapter);
    }
    //Receive Button's method:
    public void receiveOrder(View view) {
        boolean flag = false;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < adapter.getIsSelected().size(); i++) {
            if (adapter.getIsSelected().get(i) == true) {
                flag = true;
                if(i != 0){
                    for(int j = 0;j < i ;j++){
                        if(orderList.get(j).getStatus().equals("Submitting")){
                            ChefActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ChefActivity.this, "Please process the order firstly which was submitted earlier", Toast.LENGTH_LONG).show();
                                }
                            });
                            return;
                        }
                    }
                }
                if(orderList.get(i).getStatus().equals("Food Ready")){
                    ChefActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChefActivity.this, "This order has been finished", Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }else if(orderList.get(i).getStatus().equals("PartAccept") || orderList.get(i).getStatus().equals("NotAvailable") || orderList.get(i).getStatus().equals("Partially") ){
                    ChefActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChefActivity.this, "This order has been received", Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }else if(orderList.get(i).getStatus().equals("Cancelled")){
                    ChefActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChefActivity.this, "This order has been cancelled", Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                orderList.get(i).setStatus("Receiving");
                result.append(orderList.get(i).getOrderID());
                try {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = database.getReference("Order");
                    databaseReference.setValue(orderList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                populateListView();
                result.append(" order has been received.");
                Toast.makeText(ChefActivity.this, result, Toast.LENGTH_LONG).show();
            }
        }
        if (flag != true) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ChefActivity.this);
            dialog.setTitle("Warning");
            dialog.setMessage("Please select any order to receive!");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.show();
        }

    }

    //Submit Button's method:
    public void submitResponse(View view) {
        boolean flag = false;
        Order order = null;
        StringBuilder result = new StringBuilder();
        int position = 0;
        for (int i = 0; i < adapter.getIsSelected().size(); i++) {
            if (adapter.getIsSelected().get(i) == true) {
                flag = true;
                order = orderList.get(i);
                position = i;
            }
        }
        if(position != 0){
            for(int j = 0 ; j < position;j++) {
                if (orderList.get(j).getStatus().equals("Receiving")) {
                    ChefActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChefActivity.this, "Please process the order firstly which was submitted earlier", Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
            }
        }
        if (flag == true) {
            String response = spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
            if (order.getStatus().equals("Submitting")) {
                ChefActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChefActivity.this, "Please receive this order firstly", Toast.LENGTH_LONG).show();
                    }
                });
                return;
            } else if (order.getStatus().equals("Food Ready")) {
                ChefActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChefActivity.this, "This order has been finished", Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }else if(order.getStatus().equals("Cancelled")){
                ChefActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChefActivity.this, "This order has been cancelled", Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }else if(order.getStatus().equals("NotAvailable")){
                ChefActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChefActivity.this, "This order has been processed", Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }else if(order.getStatus().equals("Partially")){
                ChefActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChefActivity.this, "This order is waiting for response from customer", Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }
            if (response.equals("Accept")) {
                AcceptOrderThread acceptOrderThread = new AcceptOrderThread(order, position);
                acceptOrderThread.start();

            } else if (response.equals("Partially Available")) {

                changeOrder(order);
                SendMessageThread sendMessageThread = new SendMessageThread(order, position);
                sendMessageThread.start();
            }else if (response.equals("Can not be Prepared")){
                NotifyNotAvailableThread notifyNotAvailableThread = new NotifyNotAvailableThread(order,position);
                notifyNotAvailableThread.start();
            }
        } else if (flag != true) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ChefActivity.this);
            dialog.setTitle("Warning");
            dialog.setMessage("Please select any order to response!");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.show();
        }
    }

    //if order is prepared partially, chef should generate new order with available products:
    public void changeOrder(Order o) {
        for (OrderItem oi : o.getOrderItemList()) {
            if (oi.getProductName().equals("Chicken")) {
                for (Product p : productList) {
                    if (p.getName().equals("Chicken")) {
                        if (oi.getQuantity() > p.getQuantity()) {
                            oi.setQuantity(p.getQuantity());
                        } else {
                            oi.setQuantity(oi.getQuantity());
                        }
                    }
                }

            } else if (oi.getProductName().equals("French Fries")) {
                for (Product p : productList) {
                    if (p.getName().equals("French Fries")) {
                        if (oi.getQuantity() > p.getQuantity()) {
                            oi.setQuantity(p.getQuantity());
                        } else {
                            oi.setQuantity(oi.getQuantity());
                        }
                    }
                }
            } else if (oi.getProductName().equals("Onion Rings")) {
                for (Product p : productList) {
                    if (p.getName().equals("Onion Rings")) {
                        if (oi.getQuantity() > p.getQuantity()) {
                            oi.setQuantity(p.getQuantity());
                        } else {
                            oi.setQuantity(oi.getQuantity());
                        }
                    }
                }
            } else {
                for (Product p : productList) {
                    if (p.getName().equals("Burger")) {
                        if (oi.getQuantity() > p.getQuantity()) {
                            oi.setQuantity(p.getQuantity());
                        } else {
                            oi.setQuantity(oi.getQuantity());
                        }
                    }
                }
            }
        }
        List<OrderItem> oiList = new ArrayList<>();
        for(OrderItem oi :o.getOrderItemList()){
            if(oi.getQuantity() != 0){
                oiList.add(oi);
            }
        }
        o.setOrderItemList(oiList);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Order");
        databaseReference.setValue(orderList);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    // store product from firebase:
    public void storeProduct(){
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        databaseReference.child("ProductList").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productList = new ArrayList<>();
                //this part get all orders which are stored in the cloud to the local ArrayList:
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Product p = child.getValue(Product.class);
                    productList.add(p);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //If accept the order, inventory list will be changed correspondingly:
    public void decreaseInventory(Order order){
        for(OrderItem oi: order.getOrderItemList()){
            if(oi.getProductName().equals("Chicken")) {
                for (Product p : productList){
                    if(p.getName().equals("Chicken")){
                        if(oi.getQuantity() > p.getQuantity()){
                            ChefActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ChefActivity.this, "Chicken is not enough in inventory list", Toast.LENGTH_LONG).show();
                                }
                            });
                            flag = false;
                            return;
                        }else{
                            p.setQuantity(p.getQuantity() - oi.getQuantity());
                        }
                    }
                }

            }else if(oi.getProductName().equals("French Fries")){
                for (Product p : productList){
                    if(p.getName().equals("French Fries")){
                        if(oi.getQuantity() > p.getQuantity()){
                            ChefActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ChefActivity.this, "French Fries is not enough in inventory list", Toast.LENGTH_LONG).show();
                                }
                            });
                            flag = false;
                            return;
                        }else{
                            p.setQuantity(p.getQuantity() - oi.getQuantity());
                        }
                    }
                }
            }else if(oi.getProductName().equals("Onion Rings")){
                for (Product p : productList){
                    if(p.getName().equals("Onion Rings")){
                        if(oi.getQuantity() > p.getQuantity()){
                            ChefActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ChefActivity.this, "Onion Rings is not enough in inventory list", Toast.LENGTH_LONG).show();
                                }
                            });
                            flag = false;
                            return;
                        }else{
                            p.setQuantity(p.getQuantity() - oi.getQuantity());
                        }
                    }
                }
            }else{
                for (Product p : productList){
                    if(p.getName().equals("Burger")){
                        if(oi.getQuantity() > p.getQuantity()){
                            ChefActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ChefActivity.this, "Burger is not enough in inventory list", Toast.LENGTH_LONG).show();
                                }
                            });
                            flag = false;
                            return;
                        }else{
                            p.setQuantity(p.getQuantity() - oi.getQuantity());
                        }
                    }
                }
            }
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("ProductList");
        databaseReference.setValue(productList);
    }

    //Thread that is used to deal with the condition of accepting order
    class AcceptOrderThread extends Thread {
        Order order;
        int position;

        public AcceptOrderThread(Order o, int position) {
            this.order = o;
            this.position = position;
        }

        @Override
        public void run() {
            decreaseInventory(order);
            if(!flag){
                ChefActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChefActivity.this, "This order cannot be completely prepared,please notify customer", Toast.LENGTH_LONG).show();
                    }
                });
            }
            order.setStatus("Preparing");
            try {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference("Order");
                databaseReference.setValue(orderList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ChefActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int id = order.getOrderID();
                    Toast.makeText(ChefActivity.this, id + " order comes into preparing state", Toast.LENGTH_LONG).show();
                }
            });
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            order = orderList.get(position);
            order.setStatus("Packaging");
            try {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference("Order");
                databaseReference.setValue(orderList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ChefActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int id = order.getOrderID();
                    Toast.makeText(ChefActivity.this, id + " order comes into packaging state", Toast.LENGTH_LONG).show();
                }
            });
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            order = orderList.get(position);
            order.setStatus("Food Ready");
            try {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference("Order");
                databaseReference.setValue(orderList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ChefActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int id = order.getOrderID();
                    Toast.makeText(ChefActivity.this, id + " order is ready for pickup", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    // Thread that is used to send message of partially prepared to customers;
    class SendMessageThread extends Thread{
        Order order;
        int position;
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        public SendMessageThread(Order o, int position) {
            this.order = o;
            this.position = position;
        }

        @Override
        public void run() {
            MessageToCustomer messageToCustomer = new MessageToCustomer();
            String time = String.valueOf(dateFormat.format(order.getCreationDate()));
            messageToCustomer.setMessage("Your" + " " +time + " " + "order can only be prepared partially because of insufficient inventory" );
            messageToCustomer.setCustomer(order.getCustomer());
            messageToCustomer.setOrder(order);
            messageToCustomer.setType("partailly");
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference();
            databaseReference.child("MessageToCustomer").push().setValue(messageToCustomer);
            order.setStatus("Partially");
            try {
                database = FirebaseDatabase.getInstance();
                databaseReference = database.getReference("Order");
                databaseReference.setValue(orderList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ChefActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChefActivity.this, "Message has been sent", Toast.LENGTH_LONG).show();
                }
            });
        }
     }

     //Thread that is used to send NotAvailable message to customer:
    class NotifyNotAvailableThread extends Thread{
        Order order;
        int position;
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        public NotifyNotAvailableThread(Order o, int position) {
            this.order = o;
            this.position = position;
        }

        @Override
        public void run() {
            MessageToCustomer messageToCustomer = new MessageToCustomer();
            String time = String.valueOf(dateFormat.format(order.getCreationDate()));
            messageToCustomer.setMessage("Your" + " " +time + " " + "order can not be prepared because inventory is Not Available" );
            messageToCustomer.setCustomer(order.getCustomer());
            messageToCustomer.setOrder(order);
            messageToCustomer.setType("cancel");
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference();
            databaseReference.child("MessageToCustomer").push().setValue(messageToCustomer);
            order.setStatus("NotAvailable");
            try {
                database = FirebaseDatabase.getInstance();
                databaseReference = database.getReference("Order");
                databaseReference.setValue(orderList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ChefActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChefActivity.this, "Message has been sent", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


}