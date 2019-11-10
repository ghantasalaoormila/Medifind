package com.shehack.medifind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class ResultsActivity extends AppCompatActivity {

    ListView list_main;
    ListView list_alt;
    FirebaseFirestore db;
    ArrayList<Medicine> main_meds;
    ArrayList<Medicine> alt_meds;
    Spinner sort_spinner;
    static SharedPreferences pref;
    TextView search_results;
    TextView other_results;
    View line_view;
    String use;
    TextView usesTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        list_alt = (ListView) findViewById(R.id.list_alt);
        list_main = (ListView) findViewById(R.id.list_main);
        search_results = (TextView) findViewById(R.id.med_head);
        other_results = (TextView) findViewById(R.id.alt_med_head);
        sort_spinner = (Spinner) findViewById(R.id.spinner_sort);
        line_view = (View) findViewById(R.id.line_view);
        usesTV = (TextView) findViewById(R.id.usesTV);
        use = "";


        search_results.setVisibility(View.INVISIBLE);
        other_results.setVisibility(View.INVISIBLE);
        sort_spinner.setVisibility(View.INVISIBLE);
        line_view.setVisibility(View.INVISIBLE);
        usesTV.setVisibility(View.INVISIBLE);


        Intent i = getIntent();
        final String med_name = i.getStringExtra("res2");
        final String contains = i.getStringExtra("res1");

        if(med_name==null && contains==null){
            search_results.setText("No Results Found");
            search_results.setVisibility(View.VISIBLE);
        }

        //final String med_name = "crocin";
        //final String contains = "paracetamol";

        else{
        addListenerOnSpinnerItemSelection();

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        Log.d("ResAct", med_name + " " + contains);

        db = FirebaseFirestore.getInstance();
        main_meds = new ArrayList<Medicine>();
        alt_meds = new ArrayList<Medicine>();

        db.collection("Medicines")
                .whereEqualTo("Name", med_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                ArrayList<String> Sellers = (ArrayList<String>) document.get("Sellers");
                                for (int i = 0; i < Sellers.size(); i++) {

                                    DocumentReference docRef = db.collection("Sellers").document(Sellers.get(i));
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot seller_document = task.getResult();
                                                if (seller_document.exists()) {

                                                    String seller_id = seller_document.getId();
                                                    String med_id = document.getId();
                                                    String med_name = document.getString("Name");
                                                    String seller = seller_document.getString("Name");
                                                    String manufactured_by = document.getString("ManufacturedBy");
                                                    String contents = document.getString("Contains");
                                                    String dosage = document.getString("Dosage");
                                                    double price = document.getDouble("Price");
                                                    String type = seller_document.getString("Type");
                                                    double rating = seller_document.getDouble("Rating");
                                                    GeoPoint location = seller_document.getGeoPoint("Location");
                                                    double discount = seller_document.getDouble("Discount");
                                                    String close_time = seller_document.getString("CloseTime");
                                                    String open_time = seller_document.getString("OpenTime");
                                                    String uses = document.getString("Uses");

                                                    main_meds.add(new Medicine(seller_id, med_id, med_name, seller, manufactured_by, contents, dosage, price, type, rating, location, discount, open_time, close_time, uses));
                                                    sort_spinner.setVisibility(View.VISIBLE);
                                                    search_results.setVisibility(View.VISIBLE);
                                                    line_view.setVisibility(View.VISIBLE);
                                                    use = uses;
                                                    usesTV.setText("Uses : " + use);
                                                    usesTV.setVisibility(View.VISIBLE);
                                                    Log.d("ResultsActMeds", main_meds.toString());
                                                    createMainListAdapter();


                                                    Log.d("Results Activity", "DocumentSnapshot data: " + seller_document.getData());
                                                } else {
                                                    Log.d("Results Activity", "No such document");
                                                }
                                            } else {
                                                Log.d("Results Activity", "get failed with ", task.getException());
                                            }
                                        }
                                    });
                                }

                                Log.d("Results Activity", document.getId() + " => " + document.getData());
                            }

                        } else {
                            Log.d("Results Activity", "Error getting documents: ", task.getException());
                        }

                        db.collection("Medicines")
                                .whereEqualTo("Contains", contains).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                                if (!document.get("Name").equals(med_name)){
                                                    ArrayList<String> Sellers = (ArrayList<String>) document.get("Sellers");
                                                    for (int i = 0; i < Sellers.size(); i++) {

                                                        DocumentReference docRef = db.collection("Sellers").document(Sellers.get(i));
                                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    DocumentSnapshot seller_document = task.getResult();
                                                                    if (seller_document.exists()) {

                                                                        String seller_id = seller_document.getId();
                                                                        String med_id = document.getId();
                                                                        String med_name = document.getString("Name");
                                                                        String seller = seller_document.getString("Name");
                                                                        String manufactured_by = document.getString("ManufacturedBy");
                                                                        String contents = document.getString("Contains");
                                                                        String dosage = document.getString("Dosage");
                                                                        double price = document.getDouble("Price");
                                                                        String type = seller_document.getString("Type");
                                                                        double rating = seller_document.getDouble("Rating");
                                                                        GeoPoint location = seller_document.getGeoPoint("Location");
                                                                        double discount = seller_document.getDouble("Discount");
                                                                        String close_time = seller_document.getString("CloseTime");
                                                                        String open_time = seller_document.getString("OpenTime");
                                                                        String uses = document.getString("Uses");

                                                                        alt_meds.add(new Medicine(seller_id, med_id, med_name, seller, manufactured_by, contents, dosage, price, type, rating, location, discount, open_time, close_time, uses));
                                                                        other_results.setVisibility(View.VISIBLE);
                                                                        sort_spinner.setVisibility(View.VISIBLE);
                                                                        if(use.equals("")){
                                                                            use = uses;
                                                                            usesTV.setText("Uses : " + use);
                                                                            usesTV.setVisibility(View.VISIBLE);
                                                                        }
                                                                        createAltListAdapter();

                                                                        Log.d("Results Activity", "DocumentSnapshot data: " + seller_document.getData());
                                                                    } else {
                                                                        Log.d("Results Activity", "No such document");
                                                                    }
                                                                } else {
                                                                    Log.d("Results Activity", "get failed with ", task.getException());
                                                                }
                                                            }
                                                        });
                                                    }
                                                    Log.d("Results Activity", document.getId() + " => " + document.getData());
                                                }
                                            }
                                        }
                                    }
                                });

                    }
                });
    }

    }

    void createMainListAdapter() {
        ResultsAdapter adapter = new ResultsAdapter(this, main_meds);
        list_main.setAdapter(adapter);
        Utility.setListViewHeightBasedOnChildren(list_main);
    }

    void createAltListAdapter(){
        ResultsAdapter adapter = new ResultsAdapter(this,alt_meds);
        list_alt.setAdapter(adapter);
        Utility.setListViewHeightBasedOnChildren(list_alt);
    }

    void addListenerOnSpinnerItemSelection(){
        sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3){

                int index = arg0.getSelectedItemPosition();
                if(index==1){
                    sortByPrice();
                }
                else if(index==2){
                    sortByDistance();
                }
                else if(index==3){
                    sortByRating();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public static double compute_distance(Medicine m){

        String lat_str = pref.getString("location_lat",null); // Storing float
        String lng_str = pref.getString("location_lng",null);
        double lat = Double.parseDouble(lat_str);
        double lng = Double.parseDouble(lng_str);

        double m_lat = m.location.getLatitude();
        double m_lng = m.location.getLongitude();

        return Utility.distance(lat,lng,m_lat,m_lng);
    }

    public static Comparator<Medicine> MedPriceComparator = new Comparator<Medicine>() {

        public int compare(Medicine m1, Medicine m2) {
            double p = m1.price-(m1.price*m1.discount*0.01) - m2.price-(m2.price*m2.discount*0.01);
            return (int)(p*100);
        }};

    public static Comparator<Medicine> MedDistComparator = new Comparator<Medicine>() {

        public int compare(Medicine m1, Medicine m2) {
            //ascending
            double m1_dist = compute_distance(m1);
            double m2_dist = compute_distance(m2);
            return (int)((m1_dist-m2_dist)*100);

        }};

    public static Comparator<Medicine> MedRatingComparator = new Comparator<Medicine>() {

        public int compare(Medicine m1, Medicine m2) {
            //ascending
            double m1_rat = m1.rating;
            double m2_rat = m2.rating;
            return (int)((m2_rat-m1_rat)*100);
        }};



    void sortByPrice(){
        Collections.sort(main_meds,MedPriceComparator);
        Collections.sort(alt_meds,MedPriceComparator);
        createMainListAdapter();
        createAltListAdapter();
    }

    void sortByDistance(){
        Collections.sort(main_meds,MedDistComparator);
        Collections.sort(alt_meds,MedDistComparator);
        createMainListAdapter();
        createAltListAdapter();
    }

    void sortByRating(){
        Collections.sort(main_meds,MedRatingComparator);
        Collections.sort(alt_meds,MedRatingComparator);
        createMainListAdapter();
        createAltListAdapter();
    }

}

class Utility{

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {

        Location locationA = new Location("point A");

        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);
        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);

        double distance = locationA.distanceTo(locationB);
        return distance/1000;
    }
}