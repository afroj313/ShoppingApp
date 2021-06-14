package com.example.shoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.shoppingapp.Model.Products;
import com.example.shoppingapp.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductsDetailsActivity extends AppCompatActivity {


    private Button addtocart;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productName,productPrice,productDescription;
    private String productID="",state="Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_details);


        productID=getIntent().getStringExtra("pid");

        addtocart=(Button)findViewById(R.id.pd_add_to_cart_button);
        productImage=(ImageView)findViewById(R.id.product_image_details);
        productName=(TextView)findViewById(R.id.product_name_details);
        productPrice=(TextView)findViewById(R.id.product_price_details);
        productDescription=(TextView)findViewById(R.id.product_description_details);
        numberButton=(ElegantNumberButton) findViewById(R.id.number_btn);

        getProductDetails(productID);


        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(state.equals("Order Placed")|| state.equals("Order Shipped")){
                   Toast.makeText(ProductsDetailsActivity.this,"you can add purchase more products,once your order is shipped or confirmed",Toast.LENGTH_LONG).show();
                }
                else {


                    addingToCartList();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        cheackOrderState();

    }



    private void addingToCartList(){



        String saveCurrentTime,saveCurrentDate;

        Calendar calfordate= Calendar.getInstance();
        SimpleDateFormat Currentdate=new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate=Currentdate.format(calfordate.getTime());

        SimpleDateFormat CurrentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=Currentdate.format(calfordate.getTime());

        final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("CartList");
        final HashMap<String,Object>cartMap=new HashMap<>();
        cartMap.put("pid",productID);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("discount","");

        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                Toast.makeText(ProductsDetailsActivity.this,"Added to cart List",Toast.LENGTH_SHORT).show();

                                                Intent intent=new Intent(ProductsDetailsActivity.this,HomeActivity.class);
                                                startActivity(intent);

                                            }

                                        }
                                    });



                        }
                    }
                });
    }

    private void getProductDetails(String productID) {

        DatabaseReference productref= FirebaseDatabase.getInstance().getReference().child("Products");
        productref.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                {

                    Products products=snapshot.getValue(Products.class);
                    productName.setText(products.getPname());
                    productPrice.setText(products.getPrice());
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void cheackOrderState() {

        DatabaseReference orderRef;
        orderRef= FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String Shippingstate=snapshot.child("State").getValue().toString();
                    String username=snapshot.child("Name").getValue().toString();
                    if(Shippingstate.equals("shipped"))
                    {
                        state="Order Shipped";

                    }
                    else if(Shippingstate.equals("not shipped"))
                    {
                        state="Order Placed";

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}