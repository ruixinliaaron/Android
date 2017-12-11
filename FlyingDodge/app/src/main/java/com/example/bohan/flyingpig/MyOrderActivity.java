package com.example.bohan.flyingpig;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import com.example.bohan.flyingpig.adapter.ExpandableMyOrderListAdapter;
import com.example.bohan.flyingpig.entity.Customer;
import com.example.bohan.flyingpig.entity.MessageToCustomer;
import com.example.bohan.flyingpig.entity.Order;
import com.example.bohan.flyingpig.entity.OrderItem;
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


public class MyOrderActivity extends AppCompatActivity {

    private List<Customer> customerList;
    private Customer currCustomer;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String email;
    private FirebaseUser user;
    private List<Order> orderList;
    private List<Order> myOrder;
    private HashMap<Order, List<String>> listHash;
    private ExpandableListView expandableList;
    private ExpandableMyOrderListAdapter adapter;
    private List<MessageToCustomer> messageList;
    private MessageToCustomer currMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        customerList = new ArrayList<>();
        orderList = new ArrayList<>();
        myOrder = new ArrayList<>();
        listHash = new HashMap<>();
        expandableList = (ExpandableListView) findViewById(R.id.my_orderlist);
        messageList = new ArrayList<>();
        storeCustomer();
        storeOrder();
        listenMessage();

    }
    //get current customer:
    private  void storeCustomer(){
        database = FirebaseDatabase.getInstance();
        database.getReference().child("CustomerList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Customer> tempList  = new ArrayList<>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Customer c = child.getValue(Customer.class);
                    tempList.add(c);
                }
                customerList = tempList;
                user = FirebaseAuth.getInstance().getCurrentUser();
                email = user.getEmail();
                currCustomer = new Customer();
                for(Customer c: customerList){
                    if(c.getEmail().equals(email)){
                        currCustomer = c;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


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
                myOrder = new ArrayList<>();
                for(Order o :orderList){
                    if(o.getCustomer().getName().equals(currCustomer.getName())){
                        myOrder.add(o);
                    }
               }
                populateListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void populateListView() {
        //list cannot directly be passed into listview, so we need adapter:
        adapter = new ExpandableMyOrderListAdapter(MyOrderActivity.this,myOrder,listHash);
        expandableList = (ExpandableListView) findViewById(R.id.my_orderlist);
        expandableList.setAdapter(adapter);
    }


    public void listenMessage() {

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        databaseReference.child("MessageToCustomer").addValueEventListener(new ValueEventListener() {

            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList = new ArrayList<>();
                //this part get all orders which are stored in the cloud to the local ArrayList:
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    MessageToCustomer messageToCustomer = child.getValue(MessageToCustomer.class);
                        messageList.add(messageToCustomer);

                }
                currMessage = new MessageToCustomer();
                user = FirebaseAuth.getInstance().getCurrentUser();
                email = user.getEmail();
                for(MessageToCustomer mtc :messageList) {
                   if(mtc.getCustomer().getEmail().equals(email)){
                       currMessage = mtc;
                       if(currMessage.getType().equals("partailly")){
                           populateNotification(currMessage);
                       }else{
                           populateCancel(currMessage);
                       }
                   }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void populateCancel(final MessageToCustomer currMessage){

        String message = currMessage.getMessage();
        Order o = currMessage.getOrder();
        AlertDialog.Builder dialog = new AlertDialog.Builder(MyOrderActivity.this);
        dialog.setTitle("Order Information");
        dialog.setMessage(message + "."+ "Sorry about that and please cancel your order.");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Cancel Order", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //remove this message from firebase:
                messageList.remove(currMessage);
                for(Order o :orderList) {
                    if(o.getOrderID() == currMessage.getOrder().getOrderID()){
                        o.setStatus("Cancelled");
                    }
                }
                try {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    database.getReference("MessageToCustomer").setValue(messageList);
                    database.getReference("Order").setValue(orderList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if(!MyOrderActivity.this.isFinishing())
        {
            dialog.show();
        }

    }


    private void populateNotification(final MessageToCustomer currMessage) {
        String message = currMessage.getMessage();
        Order o = currMessage.getOrder();
        String newOrder = "";
        for(OrderItem oi:o.getOrderItemList()){
            newOrder += oi.getProductName() + ":" +oi.getQuantity() + "\n";
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(MyOrderActivity.this);
        dialog.setTitle("Order Information");
        dialog.setMessage(message + "."+ "Now available: "  +newOrder);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Cancel Order", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //remove this message from firebase:
                messageList.remove(currMessage);
                for(Order o :orderList) {
                    if(o.getOrderID() == currMessage.getOrder().getOrderID()){
                        o.setStatus("Cancelled");
                    }
                }
                try {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    database.getReference("MessageToCustomer").setValue(messageList);
                    database.getReference("Order").setValue(orderList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.setNegativeButton("Partailly Prepared", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //remove this message from firebase:
                messageList.remove(currMessage);
                for(Order o :orderList) {
                    if(o.getOrderID() == currMessage.getOrder().getOrderID()){
                        o.setStatus("PartAccept");
                    }
                }
                try {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    database.getReference("MessageToCustomer").setValue(messageList);
                    database.getReference("Order").setValue(orderList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if(!MyOrderActivity.this.isFinishing())
        {
            dialog.show();
        }

    }
}


