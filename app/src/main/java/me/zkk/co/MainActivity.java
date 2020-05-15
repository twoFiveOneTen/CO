package me.zkk.co;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import me.zkk.kkapp.ExampleService2;
import me.zkk.kkapp.GetCharNum;
import me.zkk.quarkco.call.InstanceMaker;
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

    private Button localExecute;    // 本地执行

    private Button remoteExecute;   // 远程执行

    private TextView localResult;   // 本地执行结果

    private TextView remoteResult;  // 远程执行结果

    private EditText endNum;    //嵌套循环结束数

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
                                Toast.makeText(selfActivity, "同步失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                syncState.setText("已同步");
                                responseView.setText(sync.getResponse());
                                Toast.makeText(selfActivity, "同步成功", Toast.LENGTH_SHORT).show();
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
        localExecute = findViewById(R.id.localExecute);
        localResult = findViewById(R.id.localExecuteResult);
        remoteExecute = findViewById(R.id.RemoteExecute);
        remoteResult = findViewById(R.id.RemoteExecuteResult);
        endNum = findViewById(R.id.endNum);
        sync = new SyncCode(this);
        manager = QuarkManager.getInstance();
    }

    // 终止服务
    private void cancelRemoteService() throws Exception {
        if(!manager.isSyncSuccess()) {
            Toast.makeText(selfActivity, "未同步，没有服务需要终止", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(selfActivity, "终止失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            syncState.setText("未同步");
                            responseView.setText(response);
                            Toast.makeText(selfActivity, "终止成功", Toast.LENGTH_SHORT).show();
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

        localExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String end = endNum.getText().toString().trim();
                            if(end.length() == 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(selfActivity, "请设置参数", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    localResult.setText("运行中...");
                                }
                            });
                            final long endNumber = Long.valueOf(end);
                            final long startTime = System.currentTimeMillis();
//                            ExampleService exampleService = new ExampleServiceImpl();
                            try {
                                ExampleService2 getNum = new GetCharNum();
                                String str = Functions.readTextFromAssets(selfActivity.getAssets().open("content"));
                                for(int i = 0; i < endNumber; ++i) {
                                    getNum.getCharNum(str);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                            exampleService.count(1, endNumber);
                            final long endTime = System.currentTimeMillis();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    localResult.setText("\n运行结束\n耗时" + (endTime - startTime) + "ms");
                                }
                            });
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        remoteExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!manager.isSyncSuccess()) {
                        Toast.makeText(selfActivity, "未同步，运行失败", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    remoteResult.setText("运行中...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String end = endNum.getText().toString().trim();
                            if(end.length() == 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(selfActivity, "请设置参数", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    remoteResult.setText("运行中...");
                                }
                            });
                            final long endNumber = Long.valueOf(end);
                            final long startTime = System.currentTimeMillis();
//                            ExampleService exampleService = null;
                            try {
                                ExampleService2 getNum = (ExampleService2) InstanceMaker.make("me.zkk.kkapp.ExampleService2", "me.zkk.kkapp.GetCharNum");;
                                String str = Functions.readTextFromAssets(selfActivity.getAssets().open("content"));
                                for(int i = 0; i < endNumber; ++i) {
                                    getNum.getCharNum(str);
                                }
//                                exampleService = (ExampleService) InstanceMaker.make("me.zkk.kkapp.ExampleService", "me.zkk.kkapp.ExampleServiceImpl");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                            exampleService.count(1, endNumber);
                            final long endTime = System.currentTimeMillis();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    remoteResult.setText("嵌套循环次数：" + endNumber + "\n运行结束\n耗时" + (endTime - startTime) + "ms");
                                }
                            });
                        }
                    }).start();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
