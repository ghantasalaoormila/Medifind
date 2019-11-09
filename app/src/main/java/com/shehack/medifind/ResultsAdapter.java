package com.shehack.medifind;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ResultsAdapter extends ArrayAdapter<Medicine> {

    private final Activity context;
    private List<Medicine> medicines = new ArrayList<>();
    private static DecimalFormat  df = new DecimalFormat("0.00");

    public ResultsAdapter(Activity context, ArrayList<Medicine> medicines) {
        super(context, R.layout.results_list,medicines);
        // TODO Auto-generated constructor stub
        this.medicines = medicines;
        this.context=context;
  //      pref = context.getApplication().getApplicationContext().getSharedPreferences("MyPref",0);

    }

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.results_list, null,true);

        TextView sellerText = (TextView) rowView.findViewById(R.id.seller);
        TextView medNameText = (TextView) rowView.findViewById(R.id.med_name);
        TextView manByText = (TextView) rowView.findViewById(R.id.manufactured_by);
        TextView contentsText = (TextView) rowView.findViewById(R.id.contents);
        TextView dosageText = (TextView) rowView.findViewById(R.id.dosage);
        TextView priceText = (TextView) rowView.findViewById(R.id.price);
//      TextView discountText = (TextView) rowView.findViewById(R.id.discount);
        TextView ratingText = (TextView) rowView.findViewById(R.id.rating);
        TextView distanceText = (TextView) rowView.findViewById(R.id.distance);

        Button buy_btn = (Button) rowView.findViewById(R.id.buy_button);
        final Medicine m = medicines.get(position);

        buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,BuyActivity.class);
                i.putExtra("med_name",m.med_name);
                i.putExtra("seller",m.seller);
                i.putExtra("price",m.price);
                i.putExtra("discount",m.discount);
                context.startActivity(i);
            }
        });

        if(m.type.equals("Offline")){
            buy_btn.setVisibility(View.INVISIBLE);
        }

        else{
            buy_btn.setVisibility(View.VISIBLE);
        }

        sellerText.setText(m.seller);
        medNameText.setText(m.med_name);
        manByText.setText("Manufactured By " + m.manufactured_by);
        contentsText.setText("Contains " + m.contents);
        dosageText.setText(m.dosage);
        priceText.setText(""+m.price);
        priceText.setPaintFlags(priceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        ratingText.setText(""+m.rating);

        distanceText.setText(""+df.format(ResultsActivity.compute_distance(m))+" km");

        return rowView;

    };
}