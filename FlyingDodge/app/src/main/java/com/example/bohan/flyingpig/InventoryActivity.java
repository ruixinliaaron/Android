package com.example.bohan.flyingpig;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.bohan.flyingpig.clockService.ClockService;
import com.example.bohan.flyingpig.entity.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class InventoryActivity extends AppCompatActivity {

    private TextView tvClock;
    public static final String CLOCK_ACTION= "Clock_Action";
    //set CountDown time into one hour:
    public static int TIME= 60*60*1000;
    private List<Product> productList;
    private ProductAdapter adapter;
    ListView listview;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private Map<String, Integer> inventoryTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_);
        tvClock=(TextView)super.findViewById(R.id.clock);

        //register Broadcast Receiver:
        regReceiver();
        productList = new ArrayList<>();
        //addProduct();
        storeProduct();
        readTxt();
    }

    //read inventory data from inventory txt file:
    private void readTxt() {
        try {
            inventoryTxt = new HashMap<>();
            File file = new File(Environment.getExternalStorageDirectory(), "Inventory.txt");

            FileInputStream fis = new FileInputStream(file);
            DataInputStream dataIO = new DataInputStream(fis);

            String line = null;
            while ((line = dataIO.readLine()) != null) {
                String[] temp = line.split(",");
                String name = temp[0];
                int quantity = Integer.parseInt(temp[1]);
                inventoryTxt.put(name, quantity);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //register Broadcast Receiver:
    private void regReceiver(){
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(CLOCK_ACTION);
        super.registerReceiver(clockReceiver, intentFilter);
    }

    // increase 50 items every hour in inventory listand accordingly decrese products by 50 in inventory txt file.
    public void increaseProduct() {
        readTxt();
        storeProduct();
        for(String name :inventoryTxt.keySet()){
            if(inventoryTxt.get(name) >= 50){
                for(Product p : productList){
                    if(p.getName().equals(name)){
                        p.setQuantity(p.getQuantity() + 50);
                        inventoryTxt.put(name,inventoryTxt.get(name) - 50);
                        break;
                    }
                }
            }else{
                for(Product p:productList){
                    if(p.getName().equals(name)){
                        Toast.makeText(InventoryActivity.this, "The quantity of " + name +" is less than 50" ,Toast.LENGTH_LONG).show();
                        p.setQuantity(p.getQuantity() + inventoryTxt.get(name));
                        inventoryTxt.put(name,0);
                        break;
                    }
                }
            }
        }
        writeProduct();
        writeTxt();
    }

    //write productList data into firebase:
    public void writeProduct() {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference("ProductList");
            databaseReference.setValue(productList);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //need to unregister Receiver:
        super.unregisterReceiver(clockReceiver);

    }

    //update inventory txt file:
    private void writeTxt(){
        try{
            File file = new File(Environment.getExternalStorageDirectory(),
                    "Inventory.txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
            Set<String> key = inventoryTxt.keySet();
            Iterator<String> keyIterate = key.iterator();
            while(keyIterate.hasNext()) {
                String name = keyIterate.next();
                Integer quantity = inventoryTxt.get(name);
                bw.write(name +"," +quantity +",");
                bw.write("\n");
                bw.flush();
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     *BroadcastReceiver，that is used to accept broadcast from ClockService
     *ClockService send broadcast every one second interval
     */
    private BroadcastReceiver clockReceiver=new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            changeTime();
        }
    };

    // That is used to change time displayed on CountDown TextView.
    private void changeTime(){
        String stime="";
        if(TIME == 0){
            stime = "00:00:00";
            //increase every items by 50 in inventory list every hour:
            increaseProduct();
            InventoryActivity.TIME = 60*60*1000;
        }else{
            int hour=TIME/(1000*60*60);
            int minute=TIME%(1000*60*60)/(60*1000);
            int second=(TIME%(1000*60*60))%(60*1000)/1000;
            String shour=""+hour,sminute=""+minute,ssecond=""+second;
            if(hour<=9){
                shour="0"+hour;
            }
            if(minute<=9){
                sminute="0"+minute;
            }
            if (second<=9){
                ssecond="0"+second;
            }
            stime=shour+":"+sminute+":"+ssecond;
        }
        tvClock.setText(stime);
    }

    //listen product change in firebase:
    private void storeProduct(){
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        databaseReference.child("ProductList").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productList = new ArrayList<>();
                //this part get all orders which are stored in the cloud to the local ArrayList:
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Product product = child.getValue(Product.class);
                    productList.add(product);
                }
                populateListView();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void populateListView(){
        adapter = new ProductAdapter(InventoryActivity.this, R.layout.inventory_item,productList);
        listview = (ListView) findViewById(R.id.linear);
        listview.setAdapter(adapter);
    }

    //add product to firebase firstly
    //This method can be only called once:
    private void addProduct(){
        Product p1 = new Product();
        p1.setName("Burger");
        p1.setQuantity(20);

        Product p2 = new Product();
        p2.setName("Chicken");
        p2.setQuantity(25);

        Product p3 = new Product();
        p3.setName("French Fries");
        p3.setQuantity(30);

        Product p4 = new Product();
        p4.setName("Onion Rings");
        p4.setQuantity(10);

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference("ProductList");
            databaseReference.push().setValue(p1);
            databaseReference.push().setValue(p2);
            databaseReference.push().setValue(p3);
            databaseReference.push().setValue(p4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class ProductAdapter extends ArrayAdapter<Product> {
        private int resourceId;
        private Context context;
        private Map<String, Integer> inventoryFile;


        public ProductAdapter(Context context, int textViewResourceId, List<Product> products) {
            super(context, textViewResourceId, products);
            this.context = context;
            this.resourceId = textViewResourceId;
            readFile();
        }


        private void readFile() {
            try {
                inventoryFile = new HashMap<>();
                File file = new File(Environment.getExternalStorageDirectory(), "Inventory.txt");

                FileInputStream fis = new FileInputStream(file);
                DataInputStream dataIO = new DataInputStream(fis);

                String line = null;
                while ((line = dataIO.readLine()) != null) {
                    String[] temp = line.split(",");
                    String name = temp[0];
                    int quantity = Integer.parseInt(temp[1]);
                    inventoryFile.put(name, quantity);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Product p = getItem(position);
            final View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            final TextView productName = (TextView) view.findViewById(R.id.itemName);
            final TextView quantity = (TextView) view.findViewById(R.id.quantity);
            Button forceBtn = (Button) view.findViewById(R.id.force);
            productName.setText(p.getName() + "      ");
            quantity.setText(String.valueOf(p.getQuantity()));

            forceBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    readFile();
                    for (String key : inventoryFile.keySet()) {
                        if (key.equals(p.getName())) {
                            int inventoryQ = inventoryFile.get(key);
                            if (inventoryQ >= 50) {
                                p.setQuantity(p.getQuantity() + 50);
                                quantity.setText(String.valueOf(p.getQuantity()));
                                inventoryFile.put(key, inventoryQ - 50);
                                for(Product product :productList){
                                    if(product.getName().equals(p.getName())){
                                        product.setQuantity(p.getQuantity());
                                    }
                                }
                                Toast.makeText(context, "This item increased successfully!", Toast.LENGTH_LONG).show();
                            } else if (inventoryQ < 50) {
                                p.setQuantity(p.getQuantity() + inventoryQ);
                                quantity.setText(String.valueOf(p.getQuantity()));
                                inventoryFile.put(key, 0);
                                for(Product product :productList){
                                    if(product.getName().equals(p.getName())){
                                        product.setQuantity(p.getQuantity());
                                    }
                                }
                                Toast.makeText(context, "The corresponding quantity in inventory.txt is less than 50", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    writeFile();
                    writeProduct();
                    TIME = 60*60*1000;
                }
            });

            return view;
        }


        private void writeFile(){
            try{
                File file = new File(Environment.getExternalStorageDirectory(),
                        "Inventory.txt");
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
                Set<String> key = inventoryFile.keySet();
                Iterator<String> keyIterate = key.iterator();
                while(keyIterate.hasNext()) {
                    String name = keyIterate.next();
                    Integer quantity = inventoryFile.get(name);
                    bw.write(name +"," +quantity +",");
                    bw.write("\n");
                    bw.flush();
                }
            }catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}