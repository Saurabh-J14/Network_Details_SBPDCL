package com.techLabs.nbpdcl.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PrefManager {
    private static final String KEY_LIST = "my_list";
    private final Context context;
    private final SharedPreferences.Editor editor;
    private final SharedPreferences pref;
    private final String ISFIRSTTIMEUSER = "isFirstTimeUser";
    private final String IsUserLogin = "isUserLogin";
    private final String UserName = "UserName";
    private final String UserType = "userType";
    private final String AccessToken = "accessToken";
    private final String type = "type";
    private final String editMode = "editMode";
    private final String PREF_DATABASE_NAME = "DatabaseName";
    private final Gson gson;
    private final String Name = "Name";
    private final String Designation = "Designation";
    private final String Email = "Email";
    private final String Mobile = "Mobile";
    private final String Date_Joined = "Date_Joined";
    private final String Place_Of_Work = "Place_Of_Work";
    private final String Region = "Region";
    private final String Zone = "Zone";
    private final String Circle = "Circle";
    private final String lastLogin = "lastLogin";
    private final String lastLogout = "lastLogout";
    private final String cable = "cable";
    private final String overhead = "overhead";
    private final String unBalenced = "unBalenced";
    private final String transformer = "transformer";
    private final String fuse = "fuse";
    private final String Switch = "Switch";
    private final String breaker = "breaker";
    private final String shuntCapacitor = "shuntCapacitor";
    private final String projectName = "projectName";
    private final String Database_Survey = "DATABASE_SURVEY";

    public PrefManager(Context context) {
        this.context = context;
        this.pref = PreferenceManager.getDefaultSharedPreferences(this.context);
        this.editor = this.pref.edit();
        gson = new Gson();
    }

    public Boolean getIsFirstTimeUser() {
        return this.pref.getBoolean(ISFIRSTTIMEUSER, false);
    }

    public void setIsFirstTimeUser(Boolean z) {
        this.editor.putBoolean(ISFIRSTTIMEUSER, z);
        this.editor.commit();
    }

    public Boolean getIsUserLogin() {
        return this.pref.getBoolean(IsUserLogin, false);
    }

    public void setIsUserLogin(Boolean b) {
        this.editor.putBoolean(IsUserLogin, b);
        this.editor.commit();
    }

    public String getUserName() {
        return this.pref.getString(UserName, "");
    }

    public void setUserName(String b) {
        this.editor.putString(UserName, b);
        this.editor.commit();
    }

    public String getUserType() {
        return this.pref.getString(UserType, "");
    }

    public void setUserType(String b) {
        this.editor.putString(UserType, b);
        this.editor.commit();
    }

    public String getAccessToken() {
        return this.pref.getString(AccessToken, "");
    }

    public void setAccessToken(String b) {
        this.editor.putString(AccessToken, b);
        this.editor.commit();
    }

    public void clearSharedPreferences(Context context) {
        editor.clear();
        editor.apply();
    }

    public String getType() {
        return this.pref.getString(type, "");
    }

    public void setType(String b) {
        this.editor.putString(type, b);
        this.editor.commit();
    }

    public String getEditMode() {
        return this.pref.getString(editMode, "");
    }

    public void setEditMode(String b) {
        this.editor.putString(editMode, b);
        this.editor.commit();
    }

    public String getDBName() {
        return pref.getString(PREF_DATABASE_NAME, "");
    }

    public void setDBName(String databaseName) {
        editor.putString(PREF_DATABASE_NAME, databaseName);
        editor.apply();
    }

    public void saveList(List<String> list) {
        String jsonString = gson.toJson(list); // Convert List to JSON String
        editor.putString(KEY_LIST, jsonString);
        editor.apply();
    }

    public ArrayList<String> getList() {
        String jsonString = pref.getString(KEY_LIST, null);
        if (jsonString == null) {
            return new ArrayList<>(); // Return empty list if no data found
        }

        Type type = new TypeToken<List<String>>() {
        }.getType();
        return gson.fromJson(jsonString, type);
    }

    public void clearList() {
        editor.remove(KEY_LIST);
        editor.apply();
    }

    public String getName() {
        return this.pref.getString(Name, "");
    }

    public void setName(String b) {
        this.editor.putString(Name, b);
        this.editor.commit();
    }

    public String getDesignation() {
        return this.pref.getString(Designation, "");
    }

    public void setDesignation(String b) {
        this.editor.putString(Designation, b);
        this.editor.commit();
    }

    public String getEmail() {
        return this.pref.getString(Email, "");
    }

    public void setEmail(String b) {
        this.editor.putString(Email, b);
        this.editor.commit();
    }

    public String getMobile() {
        return this.pref.getString(Mobile, "");
    }

    public void setMobile(String b) {
        this.editor.putString(Mobile, b);
        this.editor.commit();
    }

    public String getDate_Joined() {
        return this.pref.getString(Date_Joined, "");
    }

    public void setDate_Joined(String b) {
        this.editor.putString(Date_Joined, b);
        this.editor.commit();
    }

    public String getPlace_Of_Work() {
        return this.pref.getString(Place_Of_Work, "");
    }

    public void setPlace_Of_Work(String b) {
        this.editor.putString(Place_Of_Work, b);
        this.editor.commit();
    }

    public String getRegion() {
        return this.pref.getString(Region, "");
    }

    public void setRegion(String b) {
        this.editor.putString(Region, b);
        this.editor.commit();
    }

    public String getZone() {
        return this.pref.getString(Zone, "");
    }

    public void setZone(String b) {
        this.editor.putString(Zone, b);
        this.editor.commit();
    }

    public String getCircle() {
        return this.pref.getString(Circle, "");
    }

    public void setCircle(String b) {
        this.editor.putString(Circle, b);
        this.editor.commit();
    }

    public String getLastLogin() {
        return this.pref.getString(lastLogin, "");
    }

    public void setLastLogin(String b) {
        this.editor.putString(lastLogin, b);
        this.editor.commit();
    }

    public String getLastLogout() {
        return this.pref.getString(lastLogout, "");
    }

    public void setLastLogout(String b) {
        this.editor.putString(lastLogout, b);
        this.editor.commit();
    }

    public String getCable() {
        return this.pref.getString(cable, "");
    }

    public void setCable(String b) {
        this.editor.putString(cable, b);
        this.editor.commit();
    }

    public String getOverhead() {
        return this.pref.getString(overhead, "");
    }

    public void setOverhead(String b) {
        this.editor.putString(overhead, b);
        this.editor.commit();
    }

    public String getUnBalenced() {
        return this.pref.getString(unBalenced, "");
    }

    public void setUnBalenced(String b) {
        this.editor.putString(unBalenced, b);
        this.editor.commit();
    }

    public String getTransformer() {
        return this.pref.getString(transformer, "");
    }

    public void setTransformer(String b) {
        this.editor.putString(transformer, b);
        this.editor.commit();
    }

    public String getFuse() {
        return this.pref.getString(fuse, "");
    }

    public void setFuse(String b) {
        this.editor.putString(fuse, b);
        this.editor.commit();
    }

    public String getSwitch() {
        return this.pref.getString(Switch, "");
    }

    public void setSwitch(String b) {
        this.editor.putString(Switch, b);
        this.editor.commit();
    }

    public String getBreaker() {
        return this.pref.getString(breaker, "");
    }

    public void setBreaker(String b) {
        this.editor.putString(breaker, b);
        this.editor.commit();
    }

    public String getShuntCapacitor() {
        return this.pref.getString(shuntCapacitor, "");
    }

    public void setShuntCapacitor(String b) {
        this.editor.putString(shuntCapacitor, b);
        this.editor.commit();
    }

    public String getProjectName() {
        return this.pref.getString(projectName, "");
    }

    public void setProjectName(String b) {
        this.editor.putString(projectName, b);
        this.editor.commit();
    }

    public void saveConsumerClasses(List<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("KEY_CONSUMER_CLASSES", json);
        editor.apply();
    }

    public List<String> getConsumerClasses() {
        String json = pref.getString("KEY_CONSUMER_CLASSES", null);
        if (json == null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public String getDatabaseSurvey() {
        return pref.getString(Database_Survey, null);
    }

    public void setDatabaseSurvey(String databaseSurvey) {
        editor.putString(Database_Survey, databaseSurvey);
        editor.apply();
    }

}
