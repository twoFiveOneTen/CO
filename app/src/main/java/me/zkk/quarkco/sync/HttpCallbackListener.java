package me.zkk.quarkco.sync;

public interface HttpCallbackListener {
    /**
     *请求成功后的处理
     * @param response 服务器返回值
     * */
    void onFinish(String response) throws Exception;

    /**
     * 请求异常的处理
     * @param e Exception
     * */
    void onError(Exception e);
}
