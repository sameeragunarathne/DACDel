package com.example.dacnaviapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dacnaviapp.bean.DA2Response;
import com.example.dacnaviapp.util.JSONConverter;
import com.example.dacnaviapp.util.JSONDecorder;
import com.example.dacnaviapp.util.RequestHandler;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConverterActivity extends AppCompatActivity {

    private GoogleMap googleMap;

    private final String TAG = "com.example.dacnaviapp";
    private static final String ACTION_FOR_INTENT_CALLBACK = "UNIQUE_KEY";

    private Button convertBtn;
    private TextView digiAddr;

    //textviews for search da
    private TextView tag1, tag2, tag3;

    //textviews for search da2
    private TextView lon, lat;

    private LatLng curLatLng;

    private static AlertDialog dialog;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.search_da:
                dialog = (AlertDialog) createDialog(0);
                dialog.setTitle("Search by DA");
                dialog.show();
                return true;

            case R.id.search_da2:
                dialog = (AlertDialog) createDialog(1);
                dialog.setTitle("Search by GPS");
                dialog.show();

                if (curLatLng != null) {
                    ((EditText) dialog.findViewById(R.id.lat)).setEnabled(false);
                    ((EditText) dialog.findViewById(R.id.lon)).setEnabled(false);
                    ((EditText) dialog.findViewById(R.id.lat)).setText(String.valueOf(curLatLng.latitude));
                    ((EditText) dialog.findViewById(R.id.lon)).setText(String.valueOf(curLatLng.longitude));
                }

                return true;

            case R.id.register_da:
                // call search_da
                try {
                    AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
                        @Override
                        protected String doInBackground(String... params) {
                            String request = params[0];
                            Log.d(TAG, "params: " + request);
                            String daResponse = RequestHandler.getDAResponse(request);

                            return daResponse;
                        }

                        @Override
                        protected void onPostExecute(String result) {
                            super.onPostExecute(result);

                            Object obj = JSONDecorder.decodeStatusFromJson(result);
                            if (obj instanceof DA2Response) {
                                DA2Response type2Resp = (DA2Response) obj;

                                HashMap hashmap = type2Resp.getDAObject().getAttributes();
                                for (Object key : hashmap.keySet()) {
                                    try {
                                        int resId = getResources().getIdentifier((String) key, "id", getPackageName());
                                        ((EditText) dialog.findViewById(resId)).setText(hashmap.get((String) key).toString());
                                    } catch (Exception e) {
                                        Log.e(TAG, "EditText.setText() error.");
                                    }
                                }
                            }
                        }
                    };

                    dialog = (AlertDialog) createDialog(2);
                    dialog.setTitle("Register DA");
                    dialog.show();

                    if (curLatLng != null) {
                        String inputJson = setInputsType1(1, String.valueOf(curLatLng.latitude), String.valueOf(curLatLng.longitude));
                        task.execute(inputJson);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Main catch: " + e.toString());
                }
                return true;

            case R.id.search_postal:
                dialog = (AlertDialog) createDialog(3);
                dialog.setTitle("Search by Postal Address");
                dialog.show();
                return true;

            case R.id.search_da3:
                dialog = (AlertDialog) createDialog(4);
                dialog.setTitle("Search by Neighbourhood");
                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private Dialog createDialog(final int type) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                ConverterActivity.this);
        // Get the layout inflater
        final LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        int resrc = R.layout.searchda_layout;

        if (type == 0) {
            resrc = R.layout.searchda_layout;
        }
        if (type == 1) {
            resrc = R.layout.searchda2_layout;
        }
        if (type == 2) {
            resrc = R.layout.registerda_layout;
        }
        if (type == 3) {
            resrc = R.layout.search_from_postal;
        }
        if (type == 4) {
            resrc = R.layout.searchda3_layout;
        }

        final View dialogView = inflater.inflate(resrc, null);

        if (type == 2) {
            Spinner buildingTypeSpinner = (Spinner) dialogView.findViewById(R.id.type);
            buildingTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    changeDialogLayout(dialogView, selectedItem);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        builder.setView(dialogView)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                tag1 = (EditText) dialogView.findViewById(R.id.tag1);
                                tag2 = (EditText) dialogView.findViewById(R.id.tag2);
                                tag3 = (EditText) dialogView.findViewById(R.id.tag3);

                                String inputJson = "";

                                if (type == 0) {
                                    // search by DA
                                    inputJson = setInputsType0(type, tag1.getText().toString().toUpperCase(),
                                            tag2.getText().toString(), tag3.getText().toString().toUpperCase());
                                    Log.d(TAG, "TAG1: " + tag1.getText().toString());

                                    startAsyncTask(inputJson,"1");
                                } else if (type == 1) {
                                    // search by GPS

                                    lon = (EditText) dialogView.findViewById(R.id.lon);
                                    lat = (EditText) dialogView.findViewById(R.id.lat);

                                    /*lat.setText(String.valueOf(curLatLng.latitude));
                                    lon.setText(String.valueOf(curLatLng.longitude));*/

                                    inputJson = setInputsType1(type, lat.getText().toString(), lon.getText().toString());

                                    startAsyncTask(inputJson,"1");
                                } else if (type == 2) {
                                    Spinner buildingTypeSpinner = (Spinner) dialogView.findViewById(R.id.type);
                                    String selectedType = buildingTypeSpinner.getSelectedItem().toString();

                                    Log.d(TAG, "type: " + selectedType);

//                                    String name = ((EditText) dialogView.findViewById(R.id.name)).getText().toString();
//                                    String street1 = ((EditText) dialogView.findViewById(R.id.street1)).getText().toString();
//                                    String street2 = ((EditText) dialogView.findViewById(R.id.street2)).getText().toString();
//                                    String town = ((EditText) dialogView.findViewById(R.id.town)).getText().toString();
//                                    String lecturerCount = ((EditText) dialogView.findViewById(R.id.lecturer_count)).getText().toString();
//                                    String studentCount = ((EditText) dialogView.findViewById(R.id.student_count)).getText().toString();
//                                    String faculties = ((EditText) dialogView.findViewById(R.id.faculties)).getText().toString();
                                    inputJson = setInputsType2(selectedType, dialogView);


                                    AsyncTask<String, Void, String> registerTask = new AsyncTask<String, Void, String>() {
                                        @Override
                                        protected String doInBackground(String... params) {
                                            String request = params[0];
                                            Log.d(TAG, "params: " + request);
                                            String daResponse = RequestHandler.getRegisterDAResponse(request);

                                            Log.d(TAG, "RESPONSE: " + daResponse);

                                            return daResponse;
                                        }

                                        @Override
                                        protected void onPostExecute(String result) {
                                            super.onPostExecute(result);

                                            Object obj = JSONDecorder.decodeStatusFromJson(result);

                                            Toast.makeText(getApplicationContext(), obj.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    };

                                    registerTask.execute(inputJson);

                                } else if (type == 3) {
                                    String houseNum = ((EditText) dialogView.findViewById(R.id.house_num)).getText().toString();
                                    String street1 = ((EditText) dialogView.findViewById(R.id.strt1)).getText().toString();
                                    String street2 = ((EditText) dialogView.findViewById(R.id.strt2)).getText().toString();
                                    String town = ((EditText) dialogView.findViewById(R.id.twn)).getText().toString();
                                    String city = ((EditText) dialogView.findViewById(R.id.cty)).getText().toString();
                                    String postalCode = ((EditText) dialogView.findViewById(R.id.postal)).getText().toString();

                                    inputJson = setInputsType3(houseNum, street1, street2, town, city, postalCode);
                                    startAsyncTask(inputJson,"1");
                                } else if (type == 4) {
                                    Spinner type1Spinner = (Spinner) dialogView.findViewById(R.id.type1_spinner);
//                                    Spinner type2Spinner = (Spinner) dialogView.findViewById(R.id.type2_spinner);

                                    String tag1 = ((EditText) dialogView.findViewById(R.id.tag_one)).getText().toString().toUpperCase();
                                    String tag2 = ((EditText) dialogView.findViewById(R.id.tag_two)).getText().toString().toUpperCase();
                                    String boundary = ((EditText) dialogView.findViewById(R.id.boundary)).getText().toString();
                                    String type1 = type1Spinner.getSelectedItem().toString();

                                    if(boundary.equals("")){
                                        boundary="50";
                                    }

                                    inputJson = setInputsType4(tag1,tag2,type1,boundary);
                                    startAsyncTask(inputJson,"0");
                                }


                                /*
                                moved this block inside conditional statement
                                try {
                                    RequestHandleAsyncTask task = new RequestHandleAsyncTask(
                                            getApplicationContext(), ACTION_FOR_INTENT_CALLBACK, digiAddr,googleMap);
//                                    if (type == 0) {
//                                        task.execute(inputJson);
//                                    }

                                    Log.d(TAG,"inputJson: "+inputJson);
                                    task.execute(inputJson);

                                } catch (Exception e) {
                                    Log.e(TAG, e.toString());
                                }*/

                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


        return builder.create();
    }

    private void changeDialogLayout(View dialogView, String selectedItem) {

        Log.d(TAG, "change layout");
        initializeDialogLayout(dialogView);

        switch (selectedItem) {
            case "University":
                Log.d(TAG, "University");

                ((EditText) dialogView.findViewById(R.id.name)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.street1)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.street2)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.town)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.lecturer_count)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.student_count)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.age)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.faculties)).setVisibility(View.VISIBLE);
                break;
            case "Ground":
                Log.d(TAG, "Ground");
                ((EditText) dialogView.findViewById(R.id.name)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.street1)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.street2)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.town)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.sport_type)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.area)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.entrance)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.capacity)).setVisibility(View.VISIBLE);
                break;
            case "School":
                ((EditText) dialogView.findViewById(R.id.name)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.street1)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.street2)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.town)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.no_of_teachers)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.student_count)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.school_category)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.date_of_establishment)).setVisibility(View.VISIBLE);
                break;
            case "Hostel":
                ((EditText) dialogView.findViewById(R.id.name)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.street1)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.street2)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.town)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.levels)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.no_of_rooms)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.no_of_bathrooms)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.capacity)).setVisibility(View.VISIBLE);
                break;
            case "Building":
                ((EditText) dialogView.findViewById(R.id.name)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.levels)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.gndivision)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.province)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.district)).setVisibility(View.VISIBLE);

                break;
            case "river":
                ((EditText) dialogView.findViewById(R.id.name)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.street1)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.street2)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.nearest_bridge)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.max_depth)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.min_depth)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.average_depth)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.bathing_places)).setVisibility(View.VISIBLE);
                break;
            case "fillingStation":
                ((EditText) dialogView.findViewById(R.id.name)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.street1)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.street2)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.distance_ToNext_Station)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.fuel_types)).setVisibility(View.VISIBLE);
                ((EditText) dialogView.findViewById(R.id.no_of_tanks)).setVisibility(View.VISIBLE);

                break;
            default:
                break;
        }
    }


    private void initializeDialogLayout(View dialogView) {

        ((EditText) dialogView.findViewById(R.id.name)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.street1)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.street2)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.town)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.lecturer_count)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.student_count)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.faculties)).setVisibility(View.GONE);

        ((EditText) dialogView.findViewById(R.id.age)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.levels)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.gndivision)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.province)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.district)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.no_of_teachers)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.school_category)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.date_of_establishment)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.sport_type)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.area)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.entrance)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.no_of_rooms)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.no_of_bathrooms)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.capacity)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.nearest_bridge)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.max_depth)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.min_depth)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.average_depth)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.bathing_places)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.distance_ToNext_Station)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.fuel_types)).setVisibility(View.GONE);
        ((EditText) dialogView.findViewById(R.id.no_of_tanks)).setVisibility(View.GONE);

    }


    private void startAsyncTask(String inputJson,String flag) {
        try {
            RequestHandleAsyncTask task = new RequestHandleAsyncTask(
                    getApplicationContext(), ACTION_FOR_INTENT_CALLBACK, digiAddr, googleMap);

            Log.d(TAG, "inputJson: " + inputJson);
            task.execute(inputJson,flag);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_main);
        digiAddr = (TextView) findViewById(R.id.txtDigiAddr);

        try {
            initializeMap();
        } catch (Exception e) {
            Log.e(TAG,
                    "error while initializing google maps" + e.getStackTrace());
        }

        readingIP();

    }

    private void readingIP() {
        File sdcard = Environment.getExternalStorageDirectory();

        File file = new File(sdcard, "ip.txt");
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        //writing to shared preferences
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("ipAddr", text.toString());
        editor.commit();
    }

    private void initializeMap() {
        googleMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment)).getMap();
        centerMapOnMyLocation();

        if (googleMap == null) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            Marker markr = null;

            @Override
            public void onMapClick(LatLng point) {
                Toast.makeText(getApplicationContext(), point.toString(),
                        Toast.LENGTH_SHORT).show();
                curLatLng = point;

                MarkerOptions marker = new MarkerOptions().position(point)
                        .title("location");
                if (markr != null)
                    markr.remove();
                markr = googleMap.addMarker(marker);
            }
        });

    }

    private void centerMapOnMyLocation() {

        googleMap.setMyLocationEnabled(true);

        // Location location = googleMap.getMyLocation();

        LatLng myLocation = null;
        //
        // if (location != null) {
        // myLocation = new LatLng(6.98180484771729,79.76793169992044);
        // }
        myLocation = new LatLng(6.98180484771729, 79.76793169992044);

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
        CameraUpdate pos = CameraUpdateFactory.newLatLng(myLocation);
        googleMap.moveCamera(pos);
        googleMap.animateCamera(zoom);
    }

    //input for tag based search da
    private String setInputsType0(int type, String tag1, String tag2, String tag3) {

        HashMap inputMap = new HashMap();

        Log.d(TAG, "got here" + type);

        inputMap.put(1, "1");
        inputMap.put(2, tag1);
        inputMap.put(3, tag2);
        inputMap.put(4, tag3);

        return JSONConverter.getSearchDARequestAsJson(inputMap);
    }

    //input for lat lon based search da
    private String setInputsType1(int type, String lat, String lon) {

        HashMap inputMap = new HashMap();

        Log.d(TAG, "got here" + type);

        inputMap.put(1, "2");
        inputMap.put(2, lat);
        inputMap.put(3, lon);

        return JSONConverter.getSearchDARequestAsJson(inputMap);
    }

    private String setInputsType2(
            String type, View dialogView) {

//
//        ((EditText) dialogView.findViewById(R.id.name)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.street1)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.street2)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.town)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.lecturer_count)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.student_count)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.faculties)).getText().toString();
//
//        ((EditText) dialogView.findViewById(R.id.age)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.levels)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.gndivision)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.province)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.district)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.no_of_teachers)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.school_category)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.date_of_establishment)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.sport_type)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.area)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.entrance)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.no_of_rooms)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.no_of_bathrooms)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.capacity)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.nearest_bridge)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.max_depth)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.min_depth)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.average_depth)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.bathing_places)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.distance_ToNext_Station)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.fuel_types)).getText().toString();
//        ((EditText) dialogView.findViewById(R.id.no_of_tanks)).getText().toString();


        HashMap map = new HashMap();
        HashMap attributes = new HashMap();


        map.put("rType", "1");
        map.put("type", type);
        map.put("latitude", curLatLng.latitude);
        map.put("longtitude", curLatLng.longitude);

        switch (type) {
            case "University":
                attributes.put("name", ((EditText) dialogView.findViewById(R.id.name)).getText().toString());
                attributes.put("street1", ((EditText) dialogView.findViewById(R.id.street1)).getText().toString());
                attributes.put("street2", ((EditText) dialogView.findViewById(R.id.street2)).getText().toString());
                attributes.put("town", ((EditText) dialogView.findViewById(R.id.town)).getText().toString());
                attributes.put("age", String.valueOf(((EditText) dialogView.findViewById(R.id.age)).getText().toString()));
                attributes.put("student_count", String.valueOf(((EditText) dialogView.findViewById(R.id.student_count)).getText().toString()));
                attributes.put("lecturer_count", String.valueOf(((EditText) dialogView.findViewById(R.id.lecturer_count)).getText().toString()));
                attributes.put("faculties", ((EditText) dialogView.findViewById(R.id.faculties)).getText().toString());
                break;
            case "Ground":
                attributes.put("name", ((EditText) dialogView.findViewById(R.id.name)).getText().toString());
                attributes.put("street1", ((EditText) dialogView.findViewById(R.id.street1)).getText().toString());
                attributes.put("street2", ((EditText) dialogView.findViewById(R.id.street2)).getText().toString());
                attributes.put("town", ((EditText) dialogView.findViewById(R.id.town)).getText().toString());
                attributes.put("sport_types", ((EditText) dialogView.findViewById(R.id.sport_type)).getText().toString());
                attributes.put("area", String.valueOf(((EditText) dialogView.findViewById(R.id.area)).getText().toString()));
                attributes.put("entrance", ((EditText) dialogView.findViewById(R.id.entrance)).getText().toString());
                attributes.put("capacity", String.valueOf(((EditText) dialogView.findViewById(R.id.capacity)).getText().toString()));
                break;
            case "School":
                attributes.put("name", ((EditText) dialogView.findViewById(R.id.name)).getText().toString());
                attributes.put("street1", ((EditText) dialogView.findViewById(R.id.street1)).getText().toString());
                attributes.put("street2", ((EditText) dialogView.findViewById(R.id.street2)).getText().toString());
                attributes.put("town", ((EditText) dialogView.findViewById(R.id.town)).getText().toString());
                attributes.put("no_of_teachers", String.valueOf(((EditText) dialogView.findViewById(R.id.no_of_teachers)).getText().toString()));
                attributes.put("student_count", String.valueOf(((EditText) dialogView.findViewById(R.id.student_count)).getText().toString()));
                attributes.put("school_category", ((EditText) dialogView.findViewById(R.id.school_category)).getText().toString());
                attributes.put("date_of_establishment", ((EditText) dialogView.findViewById(R.id.date_of_establishment)).getText().toString());
                break;
            case "river":
                attributes.put("name", ((EditText) dialogView.findViewById(R.id.name)).getText().toString());
                attributes.put("street1", ((EditText) dialogView.findViewById(R.id.street1)).getText().toString());
                attributes.put("street2", ((EditText) dialogView.findViewById(R.id.street2)).getText().toString());
                attributes.put("nearest_bridge", ((EditText) dialogView.findViewById(R.id.town)).getText().toString());
                attributes.put("max_depth", String.valueOf(((EditText) dialogView.findViewById(R.id.max_depth)).getText().toString()));
                attributes.put("min_depth", String.valueOf(((EditText) dialogView.findViewById(R.id.min_depth)).getText().toString()));
                attributes.put("average_depth", String.valueOf(((EditText) dialogView.findViewById(R.id.average_depth)).getText().toString()));
                attributes.put("bathing_places", ((EditText) dialogView.findViewById(R.id.bathing_places)).getText().toString());
                break;
            case "Hostel":
                attributes.put("name", ((EditText) dialogView.findViewById(R.id.name)).getText().toString());
                attributes.put("street1", ((EditText) dialogView.findViewById(R.id.street1)).getText().toString());
                attributes.put("street2", ((EditText) dialogView.findViewById(R.id.street2)).getText().toString());
                attributes.put("town", ((EditText) dialogView.findViewById(R.id.town)).getText().toString());
                attributes.put("levels", ((EditText) dialogView.findViewById(R.id.levels)).getText().toString());
                attributes.put("no_of_rooms", String.valueOf(((EditText) dialogView.findViewById(R.id.no_of_rooms)).getText().toString()));
                attributes.put("no_of_bathrooms", String.valueOf(((EditText) dialogView.findViewById(R.id.no_of_bathrooms)).getText().toString()));
                attributes.put("capacity", String.valueOf(((EditText) dialogView.findViewById(R.id.capacity)).getText().toString()));
                break;
            case "Building":
                attributes.put("name", ((EditText) dialogView.findViewById(R.id.name)).getText().toString());
                attributes.put("levels", ((EditText) dialogView.findViewById(R.id.levels)).getText().toString());
                attributes.put("gndivision", ((EditText) dialogView.findViewById(R.id.gndivision)).getText().toString());
                attributes.put("Province", ((EditText) dialogView.findViewById(R.id.town)).getText().toString());
                attributes.put("District", ((EditText) dialogView.findViewById(R.id.district)).getText().toString());
                break;
            case "fillingStation":
                attributes.put("name", ((EditText) dialogView.findViewById(R.id.name)).getText().toString());
                attributes.put("street1", ((EditText) dialogView.findViewById(R.id.street1)).getText().toString());
                attributes.put("street2", ((EditText) dialogView.findViewById(R.id.street2)).getText().toString());
                attributes.put("town", ((EditText) dialogView.findViewById(R.id.town)).getText().toString());
                attributes.put("distance_ToNext_Station", String.valueOf(((EditText) dialogView.findViewById(R.id.distance_ToNext_Station)).getText().toString()));
                attributes.put("fuel_types", ((EditText) dialogView.findViewById(R.id.fuel_types)).getText().toString());
                attributes.put("no_of_tanks", String.valueOf(((EditText) dialogView.findViewById(R.id.no_of_tanks)).getText().toString()));
                break;

        }
        map.put("attributes", attributes);

        JSONObject json = new JSONObject(map);

        //return JSONConverter.getSearchDARequestAsJson(map);
        return json.toString();
    }


    private String setInputsType3(String houseNum, String street1, String street2,
                                  String town, String city, String postal) {
        HashMap map = new HashMap();
        map.put("rType", "3");
        if (!houseNum.equals(""))
            map.put("house_number", houseNum);

        if (!street1.equals(""))
            map.put("street1", street1);

        if (!street2.equals(""))
            map.put("street2", street2);

        if (!town.equals(""))
            map.put("town", town);

        if (!city.equals(""))
            map.put("city", city);

        if (!postal.equals(""))
            map.put("postal", postal);

        JSONObject json = new JSONObject(map);

        return json.toString();
    }

    private String setInputsType4(String tag1, String tag2, String type1, String boundary){
        HashMap map = new HashMap();
        map.put("tag1",tag1);
        map.put("tag2",tag2);
        map.put("type",type1);
        map.put("boundary",String.valueOf(boundary));

        JSONObject json = new JSONObject(map);

        return json.toString();
    }


    // convert coordinates to namevalue pairs
    private List<NameValuePair> setCordinates(HashMap<String, String> details) {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
        nameValuePair.add(new BasicNameValuePair("longitude",
                curLatLng.longitude + ""));
        nameValuePair.add(new BasicNameValuePair("latitude", curLatLng.latitude
                + ""));
        nameValuePair.add(new BasicNameValuePair("name", details.get("name")));
        nameValuePair.add(new BasicNameValuePair("city", details.get("city")));
        nameValuePair.add(new BasicNameValuePair("street", details
                .get("street")));
        nameValuePair
                .add(new BasicNameValuePair("other", details.get("other")));
        return nameValuePair;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeMap();
    }

}
