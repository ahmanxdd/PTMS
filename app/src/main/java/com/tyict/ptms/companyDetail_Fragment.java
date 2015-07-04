package com.tyict.ptms;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tyict.ptms.dataInfo.DatabaseView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by RAYMOND on 7/4/2015.
 */
public class companyDetail_Fragment extends Fragment {
    @Nullable
    private DatabaseView dbv = new DatabaseView();
    private String[] companyName;
    private String[] companyID;
    private Spinner spn_companySelection;
    private TextView tv_address, tv_contact;
    private Button btn_callCompany;
    private GoogleMap map;
    private MapView mapView;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.f_company_details, container, false);
        findView();
        initResource();
        mapView.onCreate(savedInstanceState);
        dbv.refreshJobList("1001");
        setUpMap();
        ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, companyName);
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
                Cursor c = dbv.query("SELECT comTel, comAddr FROM Company WHERE comNo = '" + companyID[i] + "'");
                c.moveToNext();
                tv_contact.setText(c.getString(0));
                tv_address.setText(c.getString(1));
                CameraUpdate center = CameraUpdateFactory.newLatLng(getLocationFromAddress(c.getString(1)));
                CameraUpdate zoomLv = CameraUpdateFactory.zoomTo(15);
                map.moveCamera(center);
                map.animateCamera(zoomLv);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
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
    public LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        LatLng p1;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng((int) (location.getLatitude() * 1E6),
                    (int) (location.getLongitude() * 1E6));

            return p1;
        } catch (Exception e) {
            return null;
        }
    }
    public void findView() {
        mapView = (MapView) view.findViewById(R.id.map);
        btn_callCompany = (Button) view.findViewById(R.id.btn_callCompany);
        spn_companySelection = (Spinner) view.findViewById(R.id.spn_companySelection);
        tv_contact = (TextView) view.findViewById(R.id.tv_contact);
        tv_address = (TextView) view.findViewById(R.id.tv_address);
    }
    public void initResource() {
        Cursor c = dbv.getAllCompany();
        companyName = new String[c.getCount()];
        companyID = new String[c.getCount()];

        for (int i = 0; i < c.getCount(); i++) {
            c.moveToNext();
            companyID[i] = c.getString(0);
            companyName[i] = c.getString(1);
        }
    }
}
