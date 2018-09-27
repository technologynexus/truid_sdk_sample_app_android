package com.nexusgroup.truidsample;

import com.nexusgroup.truid.Profile;
import com.nexusgroup.truid.interfaces.IDskppStatusDelegator;


/**
 * Created by Shreyesh on 17-01-2017.
 */
public class StatusDelegatorAndroid implements IDskppStatusDelegator {
    private Profile _profile = null;
    private String _errorMsg = null;
    @Override
    public void onComplete(Profile profile) {
        _profile = profile;
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onStoppedWithError(String s) {
        _errorMsg = s;
        System.out.println(s);
    }

    @Override
    public void onStartNetworking() {

    }

    @Override
    public void onStopNetworking() {

    }

    Profile getProfile(){
        return _profile;
    }

    String getErrorMsg() {return _errorMsg;}
}
