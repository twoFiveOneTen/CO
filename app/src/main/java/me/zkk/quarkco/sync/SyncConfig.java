package me.zkk.quarkco.sync;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import static me.zkk.quarkco.sync.Functions.readTextFromAssets;

public class SyncConfig extends AppCompatActivity {

    public static String postUrl;

    public static String remoteHost;

    public static String postParamName;

    public static String codeDir;

    /**
     * @param activity 当前活动
     * @return void
     * @throws Exception
     */
    public static void setConfig(Activity activity) throws Exception{
        String config = readTextFromAssets(activity.getAssets().open("syncConfig.json"));
        JSONObject object = new JSONObject(config);
        postUrl = object.getString("postUrl");
        remoteHost = object.getString("remoteHost");
        postParamName = object.getString("postParamName") + "=";
        codeDir = object.getString("codeDir");
        String a = codeDir.substring(codeDir.length() - 1);
        while(codeDir.substring(codeDir.length() - 1).equals("/")) {
            codeDir = codeDir.substring(0, codeDir.length() - 1);
        }
    }
}
