package me.zkk.quarkco.manager;

import org.json.JSONObject;

import java.util.HashMap;

import me.zkk.quarkco.sync.Functions;
import me.zkk.quarkco.sync.HttpCallbackListener;

public class QuarkManager {

    private static QuarkManager instance = null;

    private HashMap<String, Integer> serviceMap = new HashMap<>();

    private boolean syncSuccess = false;   //是否同步完成

    private String[] serviceArray;  //参与同步的代码

    private String serviceLabel;    // 服务标识码，由服务端给出

    private QuarkManager() {}

    public boolean isSyncSuccess() {
        return syncSuccess;
    }

    public void setSyncSuccess(boolean syncFinished) {
        this.syncSuccess = syncFinished;
    }

    public String getServiceLabel() {
        return serviceLabel;
    }

    public static QuarkManager getInstance() {
        if(QuarkManager.instance == null) {
            QuarkManager.instance = new QuarkManager();
        }
        return QuarkManager.instance;
    }

    public String[] getServiceArray() {
        return this.serviceArray;
    }

    public void setServiceArray(String[] serviceArray) {
        this.serviceArray = serviceArray;
    }

    public HashMap<String, Integer> getServiceMap() {
        return this.serviceMap;
    }

    /**
     * 解析服务端返回的结果并存在管理模块的字典中
     * @param jsonResult 服务端返回的结果
     * @return
     * @throws Exception
     */
    public boolean pushService(String jsonResult) throws Exception{
        JSONObject object = new JSONObject(jsonResult);
        if(object.has("errorCode")) {
            return false;
        }
        if(object.has("serviceLabel")) {
            this.serviceLabel = object.getString("serviceLabel");
        }
        if(this.serviceArray.length > 0) {
            for(String className:this.serviceArray) {
                if(object.has(className)) {
                    this.serviceMap.put(className, Integer.valueOf(object.getString(className)));
                }
            }
        }
        return true;
    }

    /**
     * 关闭远程服务器的服务
     * @param uiListener 回调
     * @throws Exception
     */
    public void stopRemoteService(final HttpCallbackListener uiListener) throws Exception {
        String param = "stop=" + this.serviceLabel;
        Functions.sendHttpRequest(param, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) throws Exception {
                uiListener.onFinish(response);
            }

            @Override
            public void onError(Exception e) {
                uiListener.onError(e);
            }
        });
    }
}
