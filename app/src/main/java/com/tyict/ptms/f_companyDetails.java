package com.tyict.ptms;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tyict.ptms.dataInfo.DatabaseView;
import com.tyict.ptms.dataInfo.StaticInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by RAYMOND on 7/4/2015.
 */
public class f_companyDetails extends Fragment {
    @Nullable

    private TreeMap<String,String> companies;
    private Spinner spn_companySelection;
    private TextView tv_address, tv_contact;
    private Button btn_callCompany;
    private GoogleMap map;
    private MapView mapView;
    private View _this;
    private Marker marker;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _this = inflater.inflate(R.layout.f_company_details, container, false);
        findView();
        mapView.onCreate(savedInstanceState);
        mapView.setEnabled(false);
        setUpMap();
        initResource();
        ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, companies.keySet().toArray(new String[companies.size()]));
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        btn_callCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + tv_contact.getText().toString()));
                startActivity(intent);
            }
        });

        spn_companySelection.setAdapter(aa);
        spn_companySelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = DatabaseView.query("SELECT comTel, comAddr FROM Company WHERE comNo = '" + companies.get(spn_companySelection.getItemAtPosition(i).toString()) + "'");
                c.moveToNext();
                tv_contact.setText(c.getString(0));
                tv_address.setText(c.getString(1));
                try {
                    final Geocoder coder = new Geocoder(getActivity().getApplicationContext());
                    List<Address> addr = coder.getFromLocationName(c.getString(1), 5);
                    LatLng ll = new LatLng(addr.get(0).getLatitude(), addr.get(0).getLongitude());
                    zoomToHere(ll);
                } catch (Exception e) {
                    new UpdateMapTask().execute(c.getString(1)); //Backup for Geocode Known Issues and Bug (This service needed to reboot)
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return _this;
    }


    private void zoomToHere(LatLng latLng) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(latLng);
        CameraUpdate zoomLv = CameraUpdateFactory.zoomTo(15);
        map.moveCamera(center);
        if (marker != null)
            marker.remove();
        map.animateCamera(zoomLv);
        marker = map.addMarker(new MarkerOptions().position(latLng).title("Here!"));
    }
    public void setUpMap() {
        mapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        map = mapView.getMap();
    }
    public void initResource() {
        Cursor c = DatabaseView.getAllCompany();
        companies = StaticInfo.getAllCompany();
    }
    public void findView() {
        mapView = (MapView) _this.findViewById(R.id.map);
        btn_callCompany = (Button) _this.findViewById(R.id.btn_callCompany);
        spn_companySelection = (Spinner) _this.findViewById(R.id.spn_companySelection);
        tv_contact = (TextView) _this.findViewById(R.id.tv_contact);
        tv_address = (TextView) _this.findViewById(R.id.tv_address);
    }
    public JSONObject getLocationFormGoogle(String placesName) {
        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" + placesName.replace(" ", "%20") + "&ka&sensor=false");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {

            e.printStackTrace();
        }

        return jsonObject;
    }
    public LatLng getLatLng(JSONObject jsonObject) {

        Double lon = new Double(0);
        Double lat = new Double(0);

        try {

            lon = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new LatLng(lat, lon);

    }
    private class UpdateMapTask extends AsyncTask<String, Integer, LatLng> {
        @Override
        protected void onPostExecute(LatLng latLng) {
            zoomToHere(latLng);
        }
        @Override
        protected LatLng doInBackground(String... address) {
            return getLatLng(getLocationFormGoogle(address[0]));
        }

    }
}
