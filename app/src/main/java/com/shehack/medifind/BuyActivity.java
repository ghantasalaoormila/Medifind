package com.shehack.medifind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class BuyActivity extends AppCompatActivity {

    String seller;
    String med_name;
    double price;
    double discount;
    int quantity;
    TextView sellerTV;
    TextView medTV;
    TextView PriceTV;
    TextView discountTV ;
    TextView totalPayTV;
    ImageButton minus_btn;
    ImageButton plus_btn;
    TextView quantityTV;
    Button placeorder_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        Intent intent = getIntent();
        seller = intent.getStringExtra("seller");
        med_name = intent.getStringExtra("med_name");
        price = intent.getDoubleExtra("price",0);
        discount = intent.getDoubleExtra("discount",0);
        quantity = 1;

        sellerTV = (TextView) findViewById(R.id.textview_sellername);
        medTV = (TextView) findViewById(R.id.textView_medname);
        PriceTV = (TextView) findViewById(R.id.price);
        discountTV = (TextView) findViewById(R.id.discount);
        totalPayTV = (TextView) findViewById(R.id.total);
        minus_btn = (ImageButton) findViewById(R.id.btnDecrease);
        plus_btn = (ImageButton) findViewById(R.id.btnIncrease);
        quantityTV = (TextView) findViewById(R.id.Quantity);
        placeorder_btn = (Button) findViewById(R.id.Place_Order);


        placeorder_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Order Placed", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(BuyActivity.this,HomeActivity.class);
                startActivity(i);
            }

        });

        minus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity>1) {
                    quantity = quantity-1;
                    quantityTV.setText(""+quantity);
                }
            }
        });

        plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = quantity+1;
                quantityTV.setText(""+quantity);
                setTotalPrice(PriceTV);
                setDiscount(discountTV);
                setNetPayable(totalPayTV);

            }
        });

        sellerTV.setText("Delivered by "+seller);
        medTV.setText(med_name);
        quantityTV.setText(""+quantity);
        setTotalPrice(PriceTV);
        setDiscount(discountTV);
        setNetPayable(totalPayTV);
    }

    void setTotalPrice(TextView view){
        double total_price = this.price*this.quantity;
        view.setText(""+total_price);
    }

    void setDiscount(TextView view){
        double discount = this.discount*this.price*this.quantity*0.01;
        view.setText("-"+discount);
    }

    void setNetPayable(TextView view){
        double net_pay = this.price*this.quantity-this.discount*this.price*this.quantity*0.01;
        view.setText(""+net_pay);
    }
}
