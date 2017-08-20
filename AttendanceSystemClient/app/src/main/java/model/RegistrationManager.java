package model;

import android.content.Context;
import android.content.SharedPreferences;

public class RegistrationManager {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;

    private static final String pref_name = "AttendanceServer";
    private static final String IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH";
    private static final String USERNAME = "username";
    private static final String DEVICE_ID = "defaultID";
    private static final String ROLL="defaultRoll";
    private static final String CLASS="defaultClass";

    public RegistrationManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setFirstLaunch(boolean is_first_time) {
        editor.putBoolean(IS_FIRST_LAUNCH, is_first_time);
        editor.commit();
    }

    public boolean isFirstTime() {
        return preferences.getBoolean(IS_FIRST_LAUNCH, true);
    }

    public void setUserName(String userName) {
        editor.putString(USERNAME, userName);
        editor.commit();
    }

    public void setUserDeviceId(String userDeviceId) {
        editor.putString(DEVICE_ID, userDeviceId);
        editor.commit();
    }

    public void setUserRoll(Integer userRoll) {
        editor.putInt(ROLL, userRoll);
        editor.commit();
    }

    public void setUserClass(String userClass) {
        editor.putString(CLASS, userClass);
        editor.commit();
    }


    public String getUsername(){return preferences.getString(USERNAME,"default_user");}
    public String getUserDeviceId(){return preferences.getString(DEVICE_ID,"default_ID");}
    public Integer getUserRoll(){return preferences.getInt(ROLL,0);}
    public String getUserClass(){return preferences.getString(CLASS,"default_class");}

}
