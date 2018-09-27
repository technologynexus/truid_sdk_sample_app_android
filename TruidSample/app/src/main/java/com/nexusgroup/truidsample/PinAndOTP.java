package com.nexusgroup.truidsample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nexusgroup.truid.Profile;
import com.nexusgroup.truid.ProfileType;


public class PinAndOTP extends DialogFragment {
    Button btnGenerateOtp;
    EditText pinInput;
    EditText challenge;
    TextView otpView;
    Profile profile = null;
    TextView profileTitle;

    public void setCurrentProfile(Profile profileSelected){
        profile = profileSelected;
    }

    public PinAndOTP() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ContextThemeWrapper ctxTw = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Light_Dialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctxTw);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_pin_otp, null);
        builder.setView(dialogView);
        btnGenerateOtp = (Button)dialogView.findViewById(R.id.btnGetOtp);
        profileTitle = (TextView)dialogView.findViewById(R.id.txtProfile);
        pinInput = (EditText) dialogView.findViewById(R.id.txtPin);
        otpView = (TextView) dialogView.findViewById(R.id.txtOTP);
        challenge = (EditText) dialogView.findViewById(R.id.txtChall);
        challenge.setVisibility(profile.getProfileType() == ProfileType.CHALLENGE ? View.VISIBLE : View.INVISIBLE);

        profileTitle.append(" " + profile.getProfileName());
        btnGenerateOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOtp(v);
            }
        });

        builder.setTitle("Pin and OTP dialog");
        AlertDialog dialog = builder.create();
        return dialog;

    }

    private void getOtp(View v) {
        int pin;
        String otp = "";
        try {
            pin = Integer.parseInt(pinInput.getText().toString());
            if(profile != null) {
                if (challenge.getVisibility() == View.VISIBLE) {
                    otp = profile.generateOTP(pin, challenge.getText().toString());
                    challenge.setText("");
                } else {
                    otp = profile.generateOTP(pin);
                }
            }
            else{
                otpView.setText("No Profile found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(pinInput.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), "Enter a valid pin", Toast.LENGTH_SHORT).show();
            }
        }
        pinInput.setText("");
//        Toast.makeText(getApplicationContext(), otp, Toast.LENGTH_LONG);
        Log.d("OTP:", otp);
        otpView.setText(otp);
    }


}
