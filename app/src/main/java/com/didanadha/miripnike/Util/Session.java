package com.didanadha.miripnike.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {
    private SharedPreferences sp;
    public Session(Context context){
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setLoginUsername(String string){
        sp.edit().putString("username",string).commit();
    }
    public String getLoginUsername(){
        return sp.getString("username","");
    }
    public void setLoginId(String string){
        sp.edit().putString("id",string).commit();
    }
    public String getLoginId(){
        return sp.getString("id","");
    }
    public String getIdDomain(){return sp.getString("id_domain",""); }
    public void setIdDomain(String string){sp.edit().putString("id_domain", string).commit();}
    public void clear(){
        sp.edit().clear().commit();
    }
}
