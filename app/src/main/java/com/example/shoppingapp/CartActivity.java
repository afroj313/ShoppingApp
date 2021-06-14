package com.example.shoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppingapp.Model.Cart;
import com.example.shoppingapp.Prevalent.Prevalent;
import com.example.shoppingapp.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProcessBtn;
    private TextView txtTotalAmount,txtmsg1;
    private int overToatalPrice=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        txtTotalAmount = findViewById(R.id.total_price);
        txtmsg1=findViewById(R.id.msg1);
        NextProcessBtn = findViewById(R.id.next_process_btn);
        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent intent =new Intent (CartActivity.this,ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price",String.valueOf(overToatalPrice));
                startActivity(intent);
                finish();
            }
        });


    }
    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();

        checknextbutton();
        cheackOrderState();

       // txtTotalAmount.setText("Total Price : $"+String.valueOf(overToatalPrice));

        final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference().child("CartList");

                    FirebaseRecyclerOptions<Cart>options=
                            new FirebaseRecyclerOptions.Builder<Cart>()
                                    .setQuery(cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products"),Cart.class)
                                    .build();

                    FirebaseRecyclerAdapter<Cart, CartViewHolder>adapter
                            = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {

                        @NonNull

                        @Override
                        protected void onBindViewHolder(@NonNull CartViewHolder holder, int i, @NonNull Cart cart) {
                            holder.txtProductQuantity.setText("Quantity"+cart.getQuantity());
                            holder.txtProductPrice.setText("Price"+cart.getPrice()+"$");
                            holder.txtProductName.setText("ProductName"+cart.getPname());

                            if(cart.getPname().equals(""))
                            {
                                NextProcessBtn.setVisibility(View.GONE);

                            }

                            int oneTypeProductTPrice = ((Integer.parseInt((cart.getPrice()))))*((Integer.parseInt(cart.getQuantity())));

                            overToatalPrice=overToatalPrice + oneTypeProductTPrice;

                            holder.itemView.setOnClickListener(new View.OnClickListener(){


                                @Override
                                public void onClick(View v) {
                                    CharSequence[] options =new CharSequence[]
                                            {

                                                    "Edit",
                                                    "Remove"
                                            };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                    builder.setTitle("Cart Options: ");
                                    builder.setItems(options,new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which==0)
                                            {
                                                Intent intent =new Intent(CartActivity.this,ProductsDetailsActivity.class);
                                                intent.putExtra("pid",cart.getPid());
                                                startActivity(intent);
                                            }
                                            if (which==1)
                                            {

                                                cartListRef.child("User View")
                                                        .child(Prevalent.currentOnlineUser.getPhone())
                                                        .child("Products")
                                                        .child(cart.getPid())
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful())
                                                                {
                                                                    Toast.makeText(CartActivity.this,"Items removed successfully.",Toast.LENGTH_SHORT).show();

                                                                    Intent intent =new Intent(CartActivity.this,HomeActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                        @Override
                        public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                            CartViewHolder holder = new CartViewHolder(view );
                            return holder;

                        }


                    };
                    recyclerView.setAdapter(adapter);
                    adapter.startListening();








    }


    private void checknextbutton() {

        DatabaseReference cartref=FirebaseDatabase.getInstance().getReference().child("CartList").child("User View");
        cartref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue()==null)
                {
                    NextProcessBtn.setVisibility(View.GONE);
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

                    if (snapshot.exists()) {
                        String Shippingstate = snapshot.child("State").getValue().toString();
                        String username = snapshot.child("Name").getValue().toString();
                        if (Shippingstate.equals("shipped")) {
                            txtTotalAmount.setText("Dear " + username + "\n order is shipped successfully.");
                            recyclerView.setVisibility(View.GONE);
                            txtmsg1.setVisibility(View.VISIBLE);
                            txtmsg1.setText("Congratulation your final order has been shipped successfully.Soon it will be verified.!");
                            NextProcessBtn.setVisibility(View.GONE);
                            Toast.makeText(CartActivity.this, "you can purchase more products,once you received your final order confirmation ..! ", Toast.LENGTH_SHORT).show();
                        } else if (Shippingstate.equals("not shipped")) {

                            txtTotalAmount.setText("Shipping State= not Shipped");
                            recyclerView.setVisibility(View.GONE);
                            txtmsg1.setVisibility(View.VISIBLE);
                            NextProcessBtn.setVisibility(View.GONE);
                            Toast.makeText(CartActivity.this, "you can purchase more products,once you received your final order confirmation ..! ", Toast.LENGTH_SHORT).show();

                        }
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });




    }


}