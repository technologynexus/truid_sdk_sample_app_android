package com.nexusgroup.truidsample;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.nexusgroup.truid.Profile;
import com.nexusgroup.truid.ProfileManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    public static ProfileManager ProfileManager = null;
    private Map<String, Profile> mapOfStringToProfileType = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context ctx = getApplicationContext();
        try {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                        0);
            }
            ProfileManager = ProfileManager.getProfileManagerInstance(ctx);
            populateList();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateList();
    }

    private void populateList() {
        final List<Profile> list = MainActivity.ProfileManager.getProfiles();
        List<Map<String, String>> data = new ArrayList<>();
        if(list != null) {
            ListView lst = (ListView) findViewById(R.id.list123);
            String[] values = new String[list.size()];
            mapOfStringToProfileType.clear();
            int i = 0;
            for (Profile e : list) {
                Map<String, String> datum = new HashMap<>();
                System.out.println(e.getProfileName());
                values[i] = (i + 1) + " - " + e.getProfileName();
                mapOfStringToProfileType.put(values[i], e);
                datum.put("name", values[i]);
                datum.put("imageToDelete", Integer.toString(android.R.drawable.ic_menu_delete));
                i++;
                data.add(datum);
            }

            SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.layout_list_profiles,
                    new String[]{"name", "imageToDelete"}, new int[]{R.id.txtProfileName, R.id.imgDelete});
            lst.setAdapter(adapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 0:
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }
        }
    }


    public void handleAddProfile(View view) {
        Intent intent = new Intent(getBaseContext(), AddProfileActivity.class);
        startActivity(intent);
    }

    public void handleDeletion(View view) {
        AlertDialog.Builder deletePromtp = new AlertDialog.Builder(this);
        deletePromtp.setTitle("Delete Profile Dialog");
        final TextView itemSelected = (TextView)((RelativeLayout)view.getParent()).getChildAt(0);
        deletePromtp.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    ProfileManager.deleteProfile(mapOfStringToProfileType.get(itemSelected.getText().toString()));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                mapOfStringToProfileType.remove(itemSelected.getText().toString());
                populateList();
            }
        });

        deletePromtp.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });
        deletePromtp.setMessage("Do you wish to delete this profile?");
        deletePromtp.create().show();

    }

    public void handleShowPinOTPDialog(View view) {
        PinAndOTP pinAndOtpDlg = new PinAndOTP();
        final TextView itemSelected = (TextView)((RelativeLayout)view.getParent()).getChildAt(0);
        Profile profile = mapOfStringToProfileType.get(itemSelected.getText().toString());
        pinAndOtpDlg.setCurrentProfile(profile);
        pinAndOtpDlg.show(getFragmentManager(), "pin_otp_dialog");
    }
}
