package com.example.shoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppingapp.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private TextView nameEditText,phoneEditText,addressEditText,cityEditText;
    private Button confirmorderbtn;
    private String totalAmount="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);


        nameEditText=(TextView)findViewById(R.id.shippment_name);
        phoneEditText=(TextView)findViewById(R.id.shippment_phone_number);
        addressEditText=(TextView)findViewById(R.id.shippment_address);
        cityEditText=(TextView)findViewById(R.id.shippment_city);
        confirmorderbtn=(Button)findViewById(R.id.confirm_final_order_btn);

        totalAmount=getIntent().getStringExtra("Total Price");
       Toast.makeText(this,"Total Price : $" + totalAmount,Toast.LENGTH_SHORT).show();


        confirmorderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cheack();
            }
        });




    }

    private void Cheack() {

        if(TextUtils.isEmpty(nameEditText.getText().toString()))
        {
            Toast.makeText(this,"Please provide your full name",Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(this,"Please provide your phone number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this,"Please provide your address",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cityEditText.getText().toString())){
            Toast.makeText(this,"Please provide your city name",Toast.LENGTH_SHORT).show();
        }
        else{

            int len=phoneEditText.length();
            if(len==0) {
                ConfirmOrder();
            }
            else {
                Toast.makeText(this,"Please provide 10 digit valid number",Toast.LENGTH_SHORT).show();

            }
        }

    }

    private void ConfirmOrder() {


        String saveCurrentTime,saveCurrentDate;

        Calendar calfordate= Calendar.getInstance();
        SimpleDateFormat Currentdate=new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate=Currentdate.format(calfordate.getTime());

        SimpleDateFormat CurrentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=Currentdate.format(calfordate.getTime());

        final DatabaseReference orderRef= FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());
        final HashMap<String,Object> ordersMap=new HashMap<>();

        ordersMap.put("TotalAmount",totalAmount);
        ordersMap.put("Name",nameEditText.getText().toString());
        ordersMap.put("Phone",phoneEditText.getText().toString());
        ordersMap.put("Address",addressEditText.getText().toString());
        ordersMap.put("City",cityEditText.getText().toString());
        ordersMap.put("Date",saveCurrentDate);
        ordersMap.put("Time",saveCurrentTime);
        ordersMap.put("State","not shipped");

        orderRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if(task.isSuccessful()){

                    FirebaseDatabase.getInstance().getReference().child("CartList")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrderActivity.this,"Your final order has been placed successfully",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this,HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }

            }
        });



    }


}