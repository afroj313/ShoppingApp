package com.example.shoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoppingapp.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {


    private String check="";
    private TextView PageTitle,title_quetion;
    private EditText phoneNumber,quetion;
    private Button verifybtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        check=getIntent().getStringExtra("check");


        PageTitle=findViewById(R.id.Reset_Password_Edit_Text);
        phoneNumber=findViewById(R.id.find_phone_number);
        quetion=findViewById(R.id.Quetion_1);
        title_quetion=findViewById(R.id.title_quetion);
        verifybtn=findViewById(R.id.verify_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        phoneNumber.setVisibility(View.GONE);

        if (check.equals("settings"))
        {
            PageTitle.setText("Set Quetion");

            title_quetion.setText("Please Set Answer for the Following Security Quetions");
            verifybtn.setText("Set");

            displayprevousans();
            verifybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setanswers();

                }
            });

        }
        else if(check.equals("login"))
        {
            phoneNumber.setVisibility(View.VISIBLE);
            quetion.setVisibility(View.GONE);
            title_quetion.setVisibility(View.GONE);




            verifybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyUser();
                }
            });

        }
    }



    private void setanswers()
    {
        String answer= quetion.getText().toString().toLowerCase();

        if(quetion.equals(""))
        {
            Toast.makeText(ResetPasswordActivity.this,"Please Answer the Quetion",Toast.LENGTH_SHORT).show();
        }
        else {

            DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(Prevalent.currentOnlineUser.getPhone());


            HashMap<String, Object> userdataMap = new HashMap<>();
            userdataMap.put("answer", answer);
            ref.child("Security Quetion").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful())
                    {


                        Toast.makeText(ResetPasswordActivity.this,"you have answer the quetion successfully ",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }
    private void displayprevousans()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());

        ref.child("Security Quetion").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {

                    String ans=snapshot.child("answer").getValue().toString();

                    quetion.setText(ans);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void verifyUser() {

        String phone=phoneNumber.getText().toString();


        //String answer= quetion.getText().toString().toLowerCase();

        if(!phone.equals(""))
        {

            final DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(phone);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists())
                    {

                        String mPhone=snapshot.child("phone").getValue().toString();

                            if(mPhone.equals(phone)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("Write new Password");
                                final EditText newpassword = new EditText(ResetPasswordActivity.this);
                                newpassword.setHint("Write  New Password here...!");
                                builder.setView(newpassword);

                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (!newpassword.getText().toString().equals("")) {

                                            ref.child("password").setValue(newpassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {

                                                            if (task.isSuccessful()) {


                                                                Toast.makeText(ResetPasswordActivity.this, "Password changed successfully ", Toast.LENGTH_SHORT).show();

                                                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                                                startActivity(intent);
                                                            }

                                                        }
                                                    });
                                        }

                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.cancel();

                                    }
                                });

                                builder.show();


                            }else
                            {
                                Toast.makeText(ResetPasswordActivity.this,"please enter valid phone number ",Toast.LENGTH_SHORT).show();
                            }


                    }

                    else{
                        Toast.makeText(ResetPasswordActivity.this,"please enter specific information ",Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {




                }
            });



        }

        else {
            Toast.makeText(ResetPasswordActivity.this,"Please complete the form...! ",Toast.LENGTH_SHORT).show();

        }





    }


}