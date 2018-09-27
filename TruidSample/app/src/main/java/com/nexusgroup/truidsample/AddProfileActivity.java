package com.nexusgroup.truidsample;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.nexusgroup.truid.ActivationRequest;
import com.nexusgroup.truid.ActivationRequestBuilder;
import com.nexusgroup.truid.DskppCancelToken;
import com.nexusgroup.truid.ProfileType;
import com.portwise.mid.clients.mobileid.profile.ProfilesCorruptException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AddProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);
    }

    public void handleActivation(View view){
        try {
            EditText uri = (EditText)findViewById(R.id.editText);
            boolean dskpp = false;
            ActivationRequestBuilder reqBuilder = new ActivationRequestBuilder();
            String strUri = "";
            if(!uri.getText().toString().isEmpty()){
                strUri = uri.getText().toString();
                dskpp = true;
                uri.setText("");
            }
            else{
                //strUri = "truid://setup/";
                EditText seed = (EditText)findViewById(R.id.textSeed);
                if(!seed.getText().toString().isEmpty()){
                    //strUri += seed.getText().toString();
                    reqBuilder.setSeed(seed.getText().toString());
                    seed.setText("");
                }
                RadioButton btn = (RadioButton)findViewById(R.id.sync);
                if(btn != null){
                    if(btn.isChecked()){
                        reqBuilder.setProfileType(ProfileType.SYNCHRONIZED);
                        //strUri += "/Synchronized";
                    }
                    else{
                        //strUri += "/Challenge";
                        reqBuilder.setProfileType(ProfileType.CHALLENGE);
                    }
                }

            }
            reqBuilder.setURL(strUri);
            ActivationRequest req = reqBuilder.build();
            if(!dskpp) {
                EditText prName = (EditText) findViewById(R.id.textView);
                req.setProfileName(prName.getText().toString());
                prName.setText("");
            }
            StatusDelegatorAndroid del = new StatusDelegatorAndroid();
            final DskppCancelToken cancelToken = new DskppCancelToken();
            class AsyncGetProfile extends AsyncTask<ActivationRequest, Integer, Boolean>{
                private StatusDelegatorAndroid _del;
                private ProgressDialog _progressDialog;
                private DskppCancelToken _canceltoken;

                public AsyncGetProfile(StatusDelegatorAndroid delegatorAndroid, DskppCancelToken cancelToken){
                    _del = delegatorAndroid;
                    _canceltoken = cancelToken;
                }

                @Override
                protected void onPreExecute() {
                    _progressDialog = new ProgressDialog(AddProfileActivity.this);
//                    _progressDialog  = ProgressDialog.show(AddProfileActivity.this, "Activation in Progress", "Please wait while profile activation is in progress...", false);
                    _progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(_canceltoken != null){
                                _canceltoken.cancel();
                            }
                        }
                    });
                    _progressDialog.setMessage("Please wait while profile activation is in progress...");
                    _progressDialog.setTitle("Activation in Progress");
                    _progressDialog.setIndeterminate(false);
                    _progressDialog.show();
                }

                @Override
                protected Boolean doInBackground(ActivationRequest... params) {
                    try {
                        MainActivity.ProfileManager.activateProfile(params[0], _del, _canceltoken);
                        while(_del.getProfile() == null && _del.getErrorMsg() == null)
                            Thread.sleep(100);

                        if(_del.getErrorMsg() != null)
                            return false;
                        return true;
                    }
                    catch (ProfilesCorruptException e){
                        e.printStackTrace();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    _progressDialog.dismiss();
                    if(result){
                        Toast.makeText(AddProfileActivity.this, "Profile created " + _del.getProfile().getProfileName(), Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(AddProfileActivity.this, "Failed to create profile : " + _del.getErrorMsg(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            AsyncGetProfile getProfile = new AsyncGetProfile(del, cancelToken);
            getProfile.execute(req);

        }
        catch (Exception e1){
            Logger.getLogger("APP").log(Level.SEVERE,e1.getMessage());
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Alert");
            alert.setMessage(e1.getMessage());
            alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //
                }
            });
            alert.create().show();
        }


    }

}
