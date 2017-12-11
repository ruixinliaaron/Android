package com.example.bohan.flyingpig;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;

/**
 * Created by bohan on 11/29/17.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bohan.flyingpig.adapter.cartAdapter;
import com.example.bohan.flyingpig.entity.Customer;
import com.example.bohan.flyingpig.entity.GoodsInfo;
import com.example.bohan.flyingpig.entity.Order;
import com.example.bohan.flyingpig.entity.OrderItem;
import com.example.bohan.flyingpig.entity.StoreInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.file.attribute.GroupPrincipal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewCartActivity extends AppCompatActivity implements cartAdapter.CheckInterface, NavigationView.OnNavigationItemSelectedListener{
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.editbutton)
    TextView editbutton;
    @BindView(R.id.top_bar)
    LinearLayout topBar;
    @BindView(R.id.exListView)
    ExpandableListView exListView;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.all_chekbox)
    CheckBox allChekbox;
    @BindView(R.id.delete)
    TextView delete;
    @BindView(R.id.checkout)
    TextView checkout;

    @BindView(R.id.operations)
    LinearLayout operations;
    @BindView(R.id.checkoutinfo)
    LinearLayout checkoutinfo;

    @BindView(R.id.shareitem)
    TextView shareitem;
    @BindView(R.id.favoratebutton)
    TextView favoritebutton;
    @BindView(R.id.itemsview)
    LinearLayout itemsview;
    @BindView(R.id.layout_cart_empty)
    LinearLayout cart_empty;
    private Context context;
    private List<StoreInfo> groups = new ArrayList<StoreInfo>();// Group list
    private Map<String, List<GoodsInfo>> children = new HashMap<String, List<GoodsInfo>>();// child list
    private cartAdapter cartadapter;
    private double totalPrice = 0.00;// total price of items
    private int totalCount = 0;// quantity of items
    public int flag = 0;//
    private DatabaseReference mDatabase;//use firebase
    private int orderId;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseUser user;
    private List<Customer> customerList;
    private String email;
    private Customer currCustomer;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_cart);
        List<Integer> burger_list =new ArrayList<>();
        burger_list.add(getIntent().getExtras().getInt("Burger1"));//receive data in intent(items chose by customer
        burger_list.add(getIntent().getExtras().getInt("Burger2"));
        burger_list.add(getIntent().getExtras().getInt("Burger3"));
        int frenchfries=getIntent().getExtras().getInt("FF");
        int onionring=getIntent().getExtras().getInt("OR");
        int chicken=getIntent().getExtras().getInt("CH");
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        NavigationView navigationView = (NavigationView) findViewById(R.id.cart_customer);
        navigationView.setNavigationItemSelectedListener(this);
        context = this;
        initData(burger_list,frenchfries,onionring,chicken);
        ButterKnife.bind(this);
        initEvents();
        readAndStoreOrderId();
        customerList=new ArrayList<>();
        storeCustomer();
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.client_menu_logout)
        {
            mAuth.signOut();
            Intent turn = new Intent(ViewCartActivity.this, CheckUserActivity.class);
            startActivity(turn);
        }
//        if(id == R.id.client_menu_viewCart)
//        {
//            Intent turn = new Intent(ViewCartActivity.this, ViewCartActivity.class);
//            turn.putExtra("View","Start");
//            startActivity(turn);
//        }
        if(id ==R.id.client_orderHistory) {
            Intent turn = new Intent(ViewCartActivity.this, MyOrderActivity.class);
            startActivity(turn);
        }
        return true;
    }

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

    private void initData(List<Integer> burger_list,int frenchfries,int onionring,int chicken){
        Double[] price=new Double[]{8.268,2.756,3.445};
        Integer[] food_list=new Integer[]{chicken,frenchfries,onionring};
        int[] img = { R.drawable.chickens,R.drawable.frenchfries,R.drawable.onionrings};
        int[] img_burger={R.drawable.burgers, R.drawable.doublecheeseburger, R.drawable.bbqburger};
        String[] groupnames={"Chicken","French Fries","Onion Rings","Burgers"};
        String[] burgernames={"Burger","Double Cheese Burger","BBQ Burger"};
        boolean ifburger=false;

        int[] otherfoods=new int[]{chicken,frenchfries,onionring};
        int groupcount=0;
        int groupnumber=0;
        for (int i = 0; i < 3; i++) {

            if(otherfoods[i]!=0) {

                groupcount+=1;
                groups.add(new StoreInfo(groupnumber + "", (groupnames[i]) + ":"));
                List<GoodsInfo> products = new ArrayList<GoodsInfo>();
                products.add(new GoodsInfo(
                        i + "",
                        groupnames[i] + "",
                        groups.get(groupnumber).getName(),
                        price[i],
                        food_list[i],
                        img[i]));
                children.put(groups.get(groupnumber).getId(), products);// using id as key of map "children" Id
                groupnumber++;
            }
        }
        List<GoodsInfo> products_burger = new ArrayList<GoodsInfo>();

        for(int i=0;i<burger_list.size();i++){
            if(burger_list.get(i)!=0){
                ifburger=true;
            }
        }
        if(ifburger) {
            groups.add(new StoreInfo(groupcount + "", "Burgers" + ":"));
            for (int m = 0; m < 3; m++) {
                if(burger_list.get(m)!=0) {
                    products_burger.add(new GoodsInfo(m + "", burgernames[m],"Burgers",  7.579, burger_list.get(m), img_burger[m]));
                }
                }
            children.put(groups.get(groupcount).getId(), products_burger);
        }

    }
    private void initEvents(){
        cartadapter = new cartAdapter(groups, children, this);
        cartadapter.setCheckInterface(this);// setting checkbox interface
        exListView.setAdapter(cartadapter);
        for (int i = 0; i < cartadapter.getGroupCount(); i++) {
            exListView.expandGroup(i);// expand the ExpandableListView
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        setCartNum();
    }
    private void setCartNum() {
        int count = 0;
        for (int i = 0; i < groups.size(); i++) {//iterating group elements,checking if the element is chose
            groups.get(i).setChoosed(allChekbox.isChecked());
            StoreInfo group = groups.get(i);
            List<GoodsInfo> childs = children.get(group.getId());
            for (GoodsInfo goodsInfo : childs) {//count elements
                count += 1;
            }
        }

        if(count==0){//if there is no element, clear cart
            clearCart();
        } else{
            title.setText("Shopping Cart" + "(" + count + ")");
        }
    }
    private void clearCart() {
        title.setText("Shopping Cart" + "(" + 0 + ")");
        editbutton.setVisibility(View.GONE);
        itemsview.setVisibility(View.GONE);
        cart_empty.setVisibility(View.VISIBLE);
    }
    private void doCheckAll() {//select all
        for (int i = 0; i < groups.size(); i++) {
            groups.get(i).setChoosed(allChekbox.isChecked());
            StoreInfo group = groups.get(i);
            List<GoodsInfo> childs = children.get(group.getId());
            for (int j = 0; j < childs.size(); j++) {
                childs.get(j).setChoosed(allChekbox.isChecked());
            }
        }
        cartadapter.notifyDataSetChanged();
        calculate();
    }
    private void calculate() {
        totalCount = 0;
        totalPrice = 0.00;
        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            List<GoodsInfo> childs = children.get(group.getId());
            for (int j = 0; j < childs.size(); j++) {
                GoodsInfo product = childs.get(j);
                if (product.isChoosed()) {
                    totalCount++;
                    totalPrice += product.getPrice() * product.getCount();
                }
            }
        }
        DecimalFormat df = new DecimalFormat("###.00");//correct to two digit
        total.setText("$" + df.format(totalPrice));
        checkout.setText("Checkout(" + totalCount + ")");
        if(totalCount==0){
            setCartNum();
        } else{
            title.setText("Shopping Cart" + "(" + totalCount + ")");
        }
    }


    protected void doDelete() {   //iterate all child elements,put element that should be delete in a list and remove them
        List<StoreInfo> toBeDeleteGroups = new ArrayList<StoreInfo>();// group elements that should be delete
        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            if (group.isChoosed()) {
                toBeDeleteGroups.add(group);
            }
            List<GoodsInfo> toBeDeleteProducts = new ArrayList<GoodsInfo>();// child elements that should be delete
            List<GoodsInfo> childs = children.get(group.getId());
            for (int j = 0; j < childs.size(); j++) {
                if (childs.get(j).isChoosed()) {
                    toBeDeleteProducts.add(childs.get(j));
                }
            }
            childs.removeAll(toBeDeleteProducts);
        }
        groups.removeAll(toBeDeleteGroups);
        setCartNum();//reset cart
        cartadapter.notifyDataSetChanged();
    }

    @Override
    public void checkChild(int groupPosition, int childPosition,
                           boolean isChecked) {
        boolean allChildSameState = true;// check if all the child elements are in same status
        StoreInfo group = groups.get(groupPosition);
        List<GoodsInfo> childs = children.get(group.getId());
        for (int i = 0; i < childs.size(); i++) {
            if (childs.get(i).isChoosed() != isChecked) {//if anyone is not chose,set allChildSameState as false
                allChildSameState = false;
                break;
            }
        }
        if (allChildSameState) {
            group.setChoosed(isChecked);//if child elements are in same status,set group element as this status
        } else {
            group.setChoosed(false);// otherwise,set group element as not being chose
        }

        if (isAllCheck()) {
            allChekbox.setChecked(true);// select all
        } else {
            allChekbox.setChecked(false);// cancel all
        }
        cartadapter.notifyDataSetChanged();
        calculate();

    }
    private boolean isAllCheck() {//check if all the group elements are selected

        for (StoreInfo group : groups) {
            if (!group.isChoosed())
                return false;
        }
        return true;
    }

    @Override
    public void checkGroup(int groupPosition, boolean isChecked) {//if a group element is selected, all it's children are selected
        StoreInfo group = groups.get(groupPosition);
        List<GoodsInfo> childs = children.get(group.getId());
        for (int i = 0; i < childs.size(); i++) {
            childs.get(i).setChoosed(isChecked);
        }
        if (isAllCheck())
            allChekbox.setChecked(true);
        else
            allChekbox.setChecked(false);
        cartadapter.notifyDataSetChanged();
        calculate();
    }

    public void readAndStoreOrderId(){
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        databaseReference.child("OrderId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String id = dataSnapshot.getValue(String.class);
                orderId = Integer.parseInt(id);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void sendOrder(){
        final Order order = new Order();
        order.setOrderID(orderId++);
        databaseReference.child("OrderId").setValue(String.valueOf(orderId));
        order.setCustomer(currCustomer);

        for (int i = 0; i < groups.size(); i++) {
            StoreInfo group = groups.get(i);
            List<GoodsInfo> childs = children.get(group.getId());
            for (int j = 0; j < childs.size(); j++) {
                GoodsInfo product = childs.get(j);
                if (product.isChoosed()) {

                    OrderItem oi = new OrderItem();
                    oi.setProductName(product.getName());
                    oi.setQuantity(product.getCount());
                    oi.setActualPrice(product.getPrice());
                    order.getOrderItemList().add(oi);

                    totalCount++;
                    totalPrice += product.getPrice() * product.getCount();
                }
            }
        }

        order.setStatus("Submitting");
        try{
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference("Order");
            databaseReference.push().setValue(order);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @OnClick({R.id.all_chekbox, R.id.delete, R.id.checkout,R.id.editbutton, R.id.favoratebutton, R.id.shareitem})
    public void onClick(View view) {
        AlertDialog alert;
        switch (view.getId()) {
            case R.id.all_chekbox:
                doCheckAll();
                break;
            case R.id.delete:
                if (totalCount == 0) {
                    Toast.makeText(context, "Select item to delete", Toast.LENGTH_LONG).show();
                    return;
                }
                alert = new AlertDialog.Builder(context).create();
                alert.setTitle("Seriously?It's yummy");
                alert.setMessage("Are you sure you wanna delete？");
                alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//??
                                return;
                            }
                        });
                alert.setButton(DialogInterface.BUTTON_POSITIVE, "Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                doDelete();
                            }
                        });
                alert.show();
                break;
            case R.id.checkout:
                if (totalCount == 0) {
                    Toast.makeText(context, "Select item to checkout", Toast.LENGTH_LONG).show();
                    return;
                }
                alert = new AlertDialog.Builder(context).create();
                alert.setTitle("Checkout");
                DecimalFormat df = new DecimalFormat("###.00");//correct to two digit
                alert.setMessage("Total:\n" + totalCount + "items\n$" + df.format(totalPrice) + "");
                alert.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                alert.setButton(DialogInterface.BUTTON_POSITIVE, "Checkout",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                                sendOrder();//ready to send order
                                return;
                            }
                        });
                alert.show();
                break;
            case R.id.editbutton:
                if (flag == 0) {
                    checkoutinfo.setVisibility(View.GONE);
                    checkout.setVisibility(View.GONE);
                    operations.setVisibility(View.VISIBLE);
                    editbutton.setText("Done");
                } else if (flag == 1) {
                    checkoutinfo.setVisibility(View.VISIBLE);
                    checkout.setVisibility(View.VISIBLE);
                    operations.setVisibility(View.GONE);
                    editbutton.setText("Edit");
                }
                flag = (flag + 1) % 2;//opposite situation first time it's 1,next time must be 0
                break;
            case R.id.shareitem:
                if (totalCount == 0) {
                    Toast.makeText(context, "Select item to share", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ViewCartActivity.this, "Success", Toast.LENGTH_SHORT).show();
                break;
            case R.id.favoratebutton:
                if (totalCount == 0) {
                    Toast.makeText(context, "Select item to mark as favorite", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ViewCartActivity.this, "Success", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
