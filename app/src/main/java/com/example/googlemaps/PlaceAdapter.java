package com.example.googlemaps;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.Viewholder> {


    ArrayList<PlacesModel> placeInfoList = new ArrayList<>();
   private Context context1;
   private GoogleMap GoogleMap;
    public static Dialog Dialog;

    public PlaceAdapter(ArrayList<PlacesModel> placeInfoList, Context context, GoogleMap googleMap,Dialog dialog){

        this.placeInfoList = placeInfoList;
        context1 = context;
        GoogleMap = googleMap;
        Dialog =dialog;
    }




    @Override
    public PlaceAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_placedetails, null);
        Viewholder viewHolder1 = new Viewholder(view1);
        viewHolder1.isRecyclable();
        return viewHolder1;    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, final int position) {
       // Log.e("ADDsdd", String.valueOf(placeInfoList.size()));

        holder.place.setText(placeInfoList.get(position).getStrAddress());
        holder.lat.setText(placeInfoList.get(position).getStrLat());
        holder.lng.setText(placeInfoList.get(position).getStrLng());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            placeInfoList.get(position);

               // Log.e("placeLat",placeInfoList.get(position).getStrLat()+"<><>"+placeInfoList.get(position).getStrLng());
                Dialog.dismiss();
                GoogleMap.clear();
                Double Lat=null;
                Lat= Double.valueOf(placeInfoList.get(position).strLat);
                Double Lng=null;
                Lng= Double.valueOf(placeInfoList.get(position).strLng);
                LatLng latLng=new LatLng(Lat,Lng);
                GoogleMap.addMarker(new MarkerOptions().position(latLng).title(placeInfoList.get(position).strAddress).icon(bitmapDescriptorFromVector(context1, R.drawable.ic_location_pin)));
                GoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                GoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
               // Toast.makeText(context1,"<<>>><<>>"+placeInfoList.get(position).getStrAddress()+placeInfoList.get(position).getStrLat()+placeInfoList.get(position).getStrLng(),Toast.LENGTH_LONG).show();
                return ;

            }
        });


    }



    @Override
    public int getItemCount() {
        return placeInfoList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView place,lat,lng;
        CardView card;

        public Viewholder(View itemView) {
            super(itemView);

            place = (TextView)itemView.findViewById(R.id.place);
            lat = (TextView)itemView.findViewById(R.id.lat);
            lng = (TextView)itemView.findViewById(R.id.lng);
            card = (CardView)itemView.findViewById(R.id.card);
        }
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}

