package com.shehack.medifind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BuyActivity extends AppCompatActivity {

    int quantity;
    String seller;
    String med_name;
    double price;
    double discount;

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

        TextView sellerTV = (TextView) findViewById(R.id.sellerNameTxt);
        TextView medTV = (TextView) findViewById(R.id.medNameTxt);
        TextView totalFareTV = (TextView) findViewById(R.id.totalFareTxt);
        TextView discountTV = (TextView) findViewById(R.id.discountPTxt);
        final TextView quantityTV = (TextView) findViewById(R.id.quantityTxt);
        TextView totalPayTV = (TextView) findViewById(R.id.totalFareTxt);
        Button minus_btn = (Button) findViewById(R.id.minus_button);
        Button plus_btn = (Button) findViewById(R.id.plus_button);

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
            }
        });

        sellerTV.setText("delivered by "+seller);
        medTV.setText(med_name);
        quantityTV.setText(""+quantity);
        setTotalPrice(totalFareTV);
        setDiscount(discountTV);
        setNetPayable(totalPayTV);
    }

    void setTotalPrice(TextView view){
        double total_price = this.price*this.quantity;
        view.setText(""+total_price);
    }

    void setDiscount(TextView view){
        double discount = this.discount*this.price*this.quantity;
        view.setText("-"+discount);
    }

    void setNetPayable(TextView view){
        double net_pay = this.price*this.quantity-this.discount*this.price*this.quantity;
    }
}
