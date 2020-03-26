package me.zkk.quarkco.call;

import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

import java.io.IOException;

import me.zkk.quarkco.manager.QuarkManager;
import me.zkk.quarkco.sync.SyncConfig;

public class InstanceMaker {

    /**
     *
     * @param interfaceString 所要实例化的类的接口类的名字
     * @param implString 所要实现类的名字
     * @return 实例化
     * @throws Exception
     */
    public static Object make(String interfaceString, String implString) throws Exception {
        CallHandler callHandler = new CallHandler();
        Object instance = null;
        String remoteHost = SyncConfig.remoteHost;
        String implNameInLine = (interfaceString + "_" + implString).replace('.', '_');
        QuarkManager manager = QuarkManager.getInstance();
        if(manager.getServiceMap().containsKey(implNameInLine)) {
            int portWasBinded = manager.getServiceMap().get(implNameInLine);
            Client client = null;
            try {
                Class interfaceClass = Class.forName(interfaceString);
                client = new Client(remoteHost, portWasBinded, callHandler);
                instance = client.getGlobal(interfaceClass);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            instance = Class.forName(implString).newInstance();
        }
        return instance;
    }
}
