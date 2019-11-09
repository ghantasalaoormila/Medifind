package com.shehack.medifind;
import com.google.firebase.firestore.GeoPoint;

public class Medicine {

    String seller_id;
    String med_id;
    String med_name;
    String seller;
    String manufactured_by;
    String contents;
    String dosage;
    double price;
    String type;
    double rating;
    GeoPoint location;
    double discount;
    String close_time;
    String open_time;
    String uses;

    Medicine(String seller_id, String med_id, String med_name, String seller, String manufactured_by, String contents, String dosage, double price, String type, double rating, GeoPoint location, double discount, String open_time, String close_time,String uses){
        this.seller_id = seller_id;
        this.med_id = med_id;
        this.med_name = med_name;
        this.seller = seller;
        this.manufactured_by = manufactured_by;
        this.contents = contents;
        this.dosage = dosage;
        this.price = price;
        this.type = type;
        this.rating = rating;
        this.location = location;
        this.discount = discount;
        this.open_time = open_time;
        this.close_time = close_time;
        this.uses = uses;
    }

}