package com.shehack.medifind;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResultsAdapter extends ArrayAdapter<Medicine> {

    private final Activity context;
    private List<Medicine> medicines = new ArrayList<>();
    private static DecimalFormat  df = new DecimalFormat("0.00");
    SharedPreferences pref;

    public ResultsAdapter(Activity context, ArrayList<Medicine> medicines) {
        super(context, R.layout.results_list,medicines);
        // TODO Auto-generated constructor stub
        this.medicines = medicines;
        this.context=context;
        pref = context.getSharedPreferences("MyPref",0);
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
        TextView discountText = (TextView) rowView.findViewById(R.id.dis_price);
        TextView ratingText = (TextView) rowView.findViewById(R.id.rating);
        TextView distanceText = (TextView) rowView.findViewById(R.id.distance);
        TextView closeTimeText = (TextView) rowView.findViewById(R.id.closeTimeTV);

        ImageButton dir_btn = (ImageButton) rowView.findViewById(R.id.dir_btn);

        Button buy_btn = (Button) rowView.findViewById(R.id.buy_button);
        final Medicine m = medicines.get(position);

        buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,BuyActivity.class);
                String med_name = m.med_name;
                med_name = med_name.substring(0,1).toUpperCase() + med_name.substring(1);
                i.putExtra("med_name",med_name);
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
        String med_name = m.med_name;
        med_name = med_name.substring(0,1).toUpperCase() + med_name.substring(1);
        medNameText.setText(med_name);
        manByText.setText("Manufactured By " + m.manufactured_by);
        String contents = m.contents;

        contents = contents.substring(0,1).toUpperCase() + contents.substring(1);
        contentsText.setText("Contains " + contents);
        dosageText.setText(m.dosage);
        priceText.setText(context.getString(R.string.Rs)+" "+m.price);
        priceText.setPaintFlags(priceText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        ratingText.setText(""+m.rating);
        discountText.setText(context.getString(R.string.Rs)+" "+df.format(m.price-(m.price*m.discount)*0.01));
        distanceText.setText(""+df.format(ResultsActivity.compute_distance(m))+" km");

        boolean isOpen = getIsOpen(m.open_time,m.close_time);

        if(!isOpen){
            closeTimeText.setText("Opens At "+m.open_time);
        }

        else {
            closeTimeText.setText("Closes At "+m.close_time);
            closeTimeText.setTextColor(Color.RED);
        }

        dir_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String lat_str = pref.getString("location_lat",null);
                String lng_str = pref.getString("location_lng",null);
                Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr="+lat_str+","+lng_str+"&daddr="+m.location.getLatitude()+","+m.location.getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if(mapIntent.resolveActivity(context.getPackageManager())!=null)
                    context.startActivity(mapIntent);
            }
        });

        return rowView;
    }

    boolean getIsOpen(String open_time,String close_time){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String current_time = sdf.format(new Date());
        Log.d("ResAdapAct"," curr " +current_time);
        Log.d("ResAdapAct","open " +open_time);
        Log.d("ResAdapAct","Close " +close_time);

        current_time.replace(':','.');
        open_time.replace(':','.');
        close_time.replace(':','.');

        int comp1 = current_time.compareTo(open_time);
        int comp2 = close_time.compareTo(current_time);

        if(comp1>=0 && comp2>=0)
            return true;

        return false;
    }

}