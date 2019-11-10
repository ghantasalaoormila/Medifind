package com.shehack.medifind;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SearchView simpleSearchView = (SearchView) findViewById(R.id.search_label); // inititate a search view
        CharSequence query = simpleSearchView.getQuery(); // get the query string currently in the text field
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_LONG).show();
                FirebaseFirestore db;
                final ArrayList<String> med_names;
                final ArrayList<String> med_contents;
                final String string_query = query;
                db = FirebaseFirestore.getInstance();
                med_names = new ArrayList<String>();
                med_contents = new ArrayList<String>();

                db.collection("Medicines")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("HomeActivity", document.getId() + " => " + document.getData());
                                        med_names.add(document.getString("Name"));
                                        med_contents.add(document.getString("Contains"));
                                    }
                                    double min_med_dist = Integer.MIN_VALUE;
                                    String min_med_name = null;
                                    Log.d("HomeActivity", "Getting dist "+med_names);
                                    for(int i=0;i<med_names.size();i++){
                                        double dist = StringUtils.getJaroWinklerDistance(string_query,med_names.get(i));
                                        Log.d("HomeActivity", "Getting dist "+dist);
                                        if(dist>min_med_dist){
                                            min_med_dist = dist;
                                            min_med_name = med_names.get(i);
                                        }
                                    }
                                    double min_content_dist = Integer.MIN_VALUE;
                                    String min_content_name = null;
                                    for(int i=0;i<med_contents.size();i++){
                                        double dist = StringUtils.getJaroWinklerDistance(string_query,med_contents.get(i));
                                        Log.d("HomeActivity", "Getting dist "+dist);
                                        if(dist>min_content_dist){
                                            min_content_dist = dist;
                                            min_content_name = med_contents.get(i);
                                        }
                                    }
                                    //Toast.makeText(getApplicationContext(), min_content_name +" "+ min_content_dist+ " , " + min_med_name + " " + min_med_dist, Toast.LENGTH_SHORT).show();
                                    String res = null;
                                    if(min_content_dist>min_med_dist && min_content_dist>0.8){
                                        res = min_content_name;
                                    }
                                    else if(min_med_dist>0.8) res = min_med_name;
                                    Intent intent = new Intent(HomeActivity.this,ResultsActivity.class);
                                    if(res!=null) intent.putExtra("res1",res.toLowerCase());
                                    if(res!=null) intent.putExtra("res2",res.toLowerCase());
                                    startActivity(intent);

                                } else {
                                    Log.d("HomeActivity", "Error getting documents: ", task.getException());
                                }
                            }
                        });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this,OcrCaptureActivity.class);
                startActivity(i);
            }
        });



    }

}
