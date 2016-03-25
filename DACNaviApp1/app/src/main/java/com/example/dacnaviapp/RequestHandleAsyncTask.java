package com.example.dacnaviapp;

import android.content.Context;
import android.os.AsyncTask;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dacnaviapp.bean.DA2Response;
import com.example.dacnaviapp.bean.DAObject;
import com.example.dacnaviapp.bean.DAType1;
import com.example.dacnaviapp.bean.DAType3;
import com.example.dacnaviapp.bean.DATypePostal;
import com.example.dacnaviapp.bean.Vertex;
import com.example.dacnaviapp.util.JSONDecorder;
import com.example.dacnaviapp.util.RequestHandler;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;


import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RequestHandleAsyncTask extends AsyncTask<String, Void, String> {


    public static final String HTTP_RESPONSE = "httpResponse";
    public static final String TAG = "RequestHandleAsyncTask";

    private Context mContext;
    private HttpClient mClient;
    private String mAction;
    private TextView digiAddr;

    private GoogleMap googleMap;

    public RequestHandleAsyncTask(Context mContext, String mAction, TextView t, GoogleMap map) {
        this.mContext = mContext;
        this.mAction = mAction;
        this.mClient = new DefaultHttpClient();
        this.digiAddr = t;
        this.googleMap = map;
    }

    public RequestHandleAsyncTask(Context mContext, HttpClient mClient, String mAction) {
        this.mContext = mContext;
        this.mClient = mClient;
        this.mAction = mAction;
    }

    @Override
    protected String doInBackground(String... params) {

        String request = params[0];
        String flag = params[1];
        Log.d(TAG, "params: " + request);

        String daResponse = "";
        if(flag.equals("1"))
            daResponse = RequestHandler.getDAResponse(request);
        else if(flag.equals("0"))
            daResponse = RequestHandler.getMultipleDAResponse(request);

        return daResponse;
    }

    @Override
    protected void onPostExecute(String result) {

        Object obj = JSONDecorder.decodeStatusFromJson(result);
        googleMap.clear();
        centerMapOnMyLocation();

        if (obj instanceof DAType1) {
            DAType1 type1Resp = (DAType1) obj;
            String resultantText = "";

            List<Vertex> vertices = type1Resp.getPolygon().getVertices();
            Log.d(TAG, "DAType1" + type1Resp.getPolygon().getVertices());

            resultantText = "-----------Digital Address--------- \n"+"WAT: "+type1Resp.getTag1()+
                    "\nMAT: "+type1Resp.getTag2()+"\nLAT: "+type1Resp.getTag3()+"\nPloygon: {";

            for (Vertex vertice : vertices) {
                LatLng latLng = new LatLng(vertice.getLatitude(), vertice.getLongitude());
                String title = "Latitude: "+vertice.getLatitude()+"\n"+"Longitude:"+"\n"+vertice.getLongitude();
                googleMap.addMarker(new MarkerOptions().position(latLng).title(title));
                centerMapOnCustomLocation(latLng);

                resultantText+="["+vertice.getLatitude()+","+vertice.getLongitude()+"] ";
            }
            resultantText+="}";

            digiAddr.setMovementMethod(new ScrollingMovementMethod());
            digiAddr.setText(resultantText);

        } else if (obj instanceof DA2Response) {            //lat lon based response

            String resultantText = "";

            DA2Response type2Resp = (DA2Response) obj;
            LatLng latLng = new LatLng(type2Resp.getDAObject().getLocation().getLatitude(),
                    type2Resp.getDAObject().getLocation().getLongtitude());
            googleMap.addMarker(new MarkerOptions().position(latLng));
            centerMapOnCustomLocation(latLng);

            resultantText = "-----------Digital Address--------- \n"+"Status: "+type2Resp.getStatus()+
                    "\nRegistered: "+type2Resp.getRegistered()+"\n"+type2Resp.getDAObject().getDigitalAddress()+"\nWAT: "+type2Resp.getDAObject().getDigitalAddresTag1()+
                    "\nMAT: "+type2Resp.getDAObject().getDigitalAddressTag2()+"\nLAT: "+type2Resp.getDAObject().getDigitalAddressTag3()+"\nLocation: "+
                    type2Resp.getDAObject().getLocation().getLatitude()+","+type2Resp.getDAObject().getLocation().getLongtitude()+"\nType: "+type2Resp.getDAObject().getType()+"\nAttributes: ";

            Iterator it = type2Resp.getDAObject().getAttributes().entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                resultantText+=pair.getKey() + " : " + pair.getValue();
                it.remove();
            }


            digiAddr.setMovementMethod(new ScrollingMovementMethod());
            digiAddr.setText(resultantText);

        } else if(obj instanceof JSONDecorder.MultipleDAResponse){
            String resultantText = "-----------Digital Address--------- \n";

            JSONDecorder.MultipleDAResponse multipleDAResponse = (JSONDecorder.MultipleDAResponse)obj;

            for (DAObject daobj:multipleDAResponse.getDAList()) {
                LatLng latLng = new LatLng(daobj.getLocation().getLatitude(),
                        daobj.getLocation().getLongtitude());
                String title = daobj.getDigitalAddress();
                googleMap.addMarker(new MarkerOptions().position(latLng).title(title));
                centerMapOnCustomLocation(latLng);

                resultantText+=daobj.getDigitalAddress()+"\nWAT: "+daobj.getDigitalAddresTag1()+"\nMAT: "+
                        daobj.getDigitalAddressTag2()+"\nLAT: "+daobj.getDigitalAddressTag3()+"\nLatitude: "+daobj.getLocation().getLatitude()+
                        "\nLongitude"+daobj.getLocation().getLongtitude()+"\nType: "+daobj.getType()+"\n" +
                        "Attributes: ";

                Iterator it = daobj.getAttributes().entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    resultantText+=pair.getKey() + " : " + pair.getValue();
                    it.remove();
                }

                resultantText+="\n\n";
            }
            digiAddr.setMovementMethod(new ScrollingMovementMethod());
            digiAddr.setText(resultantText);


        } else if (obj instanceof DAType3) {
            DAType3 type3Resp = (DAType3) obj;

            String resultantText = "-----------Digital Address--------- \n";
            Log.d(TAG, "DAType3" + type3Resp.getLatitude() + " " + type3Resp.getLongtitude());

            LatLng latLng = new LatLng(type3Resp.getLatitude(),
                    type3Resp.getLongtitude());
            googleMap.addMarker(new MarkerOptions().position(latLng));
            centerMapOnCustomLocation(latLng);

            resultantText += "WAT: "+type3Resp.getTag1()+"\nMAT: "+type3Resp.getTag2()+
                    "\nLAT: "+type3Resp.getTag3()+"\nLatitude: "+type3Resp.getLatitude()+
                    "\nLongitude: "+type3Resp.getLongtitude();
            digiAddr.setMovementMethod(new ScrollingMovementMethod());
            digiAddr.setText(resultantText);

        } else if (obj instanceof DATypePostal) {
            DATypePostal typePostal = (DATypePostal) obj;
            String locType = typePostal.getLocation_type();
            String resultantText = "-----------Digital Address--------- \n";

            if(locType.equals("osm_point_partial") || locType.equals("osm_polygon_partial") || locType.equals("osm_line_partial")){
                LatLng latLng = new LatLng(typePostal.getLatitude(),
                        typePostal.getLongtitude());
                googleMap.addMarker(new MarkerOptions().position(latLng).icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                centerMapOnCustomLocation(latLng);
                resultantText+= "Status: "+typePostal.getStatus()+"\nLocation type: "+typePostal.getLocation_type()+
                        "\nLatitude: "+typePostal.getLatitude()+"\nLongitude: "+typePostal.getLongtitude();

            } else if(locType.equals("no_match")){
                resultantText+="no match";
            } else {
                LatLng latLng = new LatLng(typePostal.getLatitude(),
                        typePostal.getLongtitude());
                googleMap.addMarker(new MarkerOptions().position(latLng).icon(
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                centerMapOnCustomLocation(latLng);

                resultantText+= "Status: "+typePostal.getStatus()+"\nLocation type: "+typePostal.getLocation_type()+
                        "\nLatitude: "+typePostal.getLatitude()+"\nLongitude: "+typePostal.getLongtitude();
            }
            digiAddr.setMovementMethod(new ScrollingMovementMethod());
            digiAddr.setText(result);
        } else {
            if(obj instanceof String){
                if(((String)obj).equals("error")){
                    Toast.makeText(mContext,"Error occured",Toast.LENGTH_SHORT);
                    digiAddr.setText("Error Occured");
                }
                if(((String)obj).equals("noDA")){
                    Toast.makeText(mContext,"no DA",Toast.LENGTH_SHORT);
                    digiAddr.setText("Status: no DA");
                }
            }
        }
    }

    private void centerMapOnMyLocation() {

        googleMap.setMyLocationEnabled(true);

//         Location location = googleMap.getMyLocation();

        LatLng myLocation = null;
        //
        // if (location != null) {
        // myLocation = new LatLng(6.98180484771729,79.76793169992044);
        // }
        myLocation = new LatLng(6.98180484771729, 79.76793169992044);

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        CameraUpdate pos = CameraUpdateFactory.newLatLng(myLocation);
        googleMap.moveCamera(pos);
        googleMap.animateCamera(zoom);
    }

    private void centerMapOnCustomLocation(LatLng loc) {

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        CameraUpdate pos = CameraUpdateFactory.newLatLng(loc);
        googleMap.moveCamera(pos);
        googleMap.animateCamera(zoom);
    }

}
