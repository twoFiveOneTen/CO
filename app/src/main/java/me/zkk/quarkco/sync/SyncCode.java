package me.zkk.quarkco.sync;


import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import me.zkk.quarkco.manager.QuarkManager;
import static me.zkk.quarkco.sync.Functions.readTextFromAssets;

// Debug用

public class SyncCode extends AppCompatActivity {

    private Activity currentActivity;

    private String responseStr;

    private QuarkManager manager = QuarkManager.getInstance();

    public SyncCode(Activity activity) {
        currentActivity = activity;
    }

    public String getCode(String classFullName) throws Exception {
        InputStream is = currentActivity.getAssets().open(SyncConfig.codeDir + "/" +classFullName);
        String code = readTextFromAssets(is);
        return handleSymbolInUrl(code);
    }

    /**
     * 将URL请求参数中的符号用转义
     *
     * @param code 需要处理的代码
     * @return
     */
    private String handleSymbolInUrl(String code) {
        code = code.replace("+", "%2B");
        return code;
    }

    /**
     *
     * @return jsonMap post请求的参数
     * @throws Exception
     */
    public String makeParam() throws Exception {
        Map<String, String> myMap = new HashMap<>();
        QuarkManager manager = QuarkManager.getInstance();
        manager.setServiceArray(currentActivity.getAssets().list(SyncConfig.codeDir));
        String[] fileNames = manager.getServiceArray();
        if(fileNames.length > 0) {
            for(String fileName:fileNames) {
                myMap.put(fileName, getCode(fileName));
            }
        }
        String jsonMap = new Gson().toJson(myMap);
        return jsonMap;
    }

    public String getResponse() {
        return this.responseStr;
    }

    public void sendPost(final HttpCallbackListener uiListener) throws Exception {

        SyncConfig.setConfig(currentActivity);  // 设置代码迁移选项

        String param = SyncConfig.postParamName + makeParam();

        HttpCallbackListener listener = new HttpCallbackListener() {    // 回调

            @Override
            public void onFinish(String response) throws Exception {

                //发送成功后的处理，该参数为服务器返回值
                responseStr = response;
                if(manager.pushService(response)) {
                    manager.setSyncSuccess(true);
                }
                uiListener.onFinish(response);
            }

            @Override
            public void onError(Exception e) {

                //http请求异常的处理
                uiListener.onError(e);
            }
        };

        Functions.sendHttpRequest(param, listener); // 发送请求
    }
}
