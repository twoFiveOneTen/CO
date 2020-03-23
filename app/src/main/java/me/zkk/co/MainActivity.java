package me.zkk.co;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import me.zkk.quarkco.manager.QuarkManager;
import me.zkk.quarkco.sync.Functions;
import me.zkk.quarkco.sync.HttpCallbackListener;
import me.zkk.quarkco.sync.SyncCode;

public class MainActivity extends AppCompatActivity {

    private Activity selfActivity = this;

    private Button syncButton;  // 同步按钮

    private Button cancelButton;    //终止服务按钮

    private TextView syncState; // 同步状态

    private  TextView responseView; // 服务端的回应

    private SyncCode sync;  //代码同步类

    private QuarkManager manager;   // 管理模块

    private void syncCode() {
        syncState.setText("同步中...");

        try{
            sync.sendPost(new HttpCallbackListener() {

                @Override
                public void onFinish(String response) throws Exception {
                    if(!manager.isSyncSuccess()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                syncState.setText("未同步");
                                responseView.setText(sync.getResponse());
                                Toast.makeText(selfActivity, "同步失败", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                syncState.setText("已同步");
                                responseView.setText(sync.getResponse());
                                Toast.makeText(selfActivity, "同步成功", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 初始化设置
    private void initSetting() {
        syncButton = findViewById(R.id.syncButton);
        cancelButton = findViewById(R.id.cancelButton);
        syncState = findViewById(R.id.syncState);
        responseView = findViewById(R.id.response);
        sync = new SyncCode(this);
        manager = QuarkManager.getInstance();
    }

    // 终止服务
    private void cancelRemoteService() throws Exception {
        if(!manager.isSyncSuccess()) {
            Toast.makeText(selfActivity, "未同步，没有服务需要终止", Toast.LENGTH_LONG).show();
            return;
        }
        manager.stopRemoteService(new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) throws Exception {
                if(!Functions.getJsonResult(response, "result").equals("success")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            responseView.setText(response);
                            Toast.makeText(selfActivity, "终止失败", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            syncState.setText("未同步");
                            responseView.setText(response);
                            Toast.makeText(selfActivity, "终止成功", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setButtonListener() {
        // 同步代码按钮
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncCode();
            }
        });

        // 终止远程服务按钮
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cancelRemoteService();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSetting();
        setButtonListener();

    }
}
