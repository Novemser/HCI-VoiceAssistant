package com.novemser.voicetest;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    /**
     * 展示消息的listview
     */
    private ListView mChatView;
    /**
     * 文本域
     */
    private EditText mMsg;
    /**
     * 存储聊天消息
     */
    private List<ChatMessage> mDatas = new ArrayList<ChatMessage>();
    /**
     * 适配器
     */
    private ListMessageAdapter mAdapter;
    //
    private Button mStartVoiceRecord;

    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            ChatMessage from = (ChatMessage) msg.obj;
            mDatas.add(from);
            mAdapter.notifyDataSetChanged();
            mChatView.setSelection(mDatas.size() - 1);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_chatting);

        initView();

        mAdapter = new ListMessageAdapter(this, mDatas);
        mChatView.setAdapter(mAdapter);

        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=573d5744");
        //1.创建RecognizerDialog对象
        mDialog = new RecognizerDialog(this, null);
        //2.设置accent、 language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");

        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果

        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                Log.d("Voice", recognizerResult.getResultString());
                printResult(recognizerResult);
                sendMessage(mStartVoiceRecord);
            }

            @Override
            public void onError(SpeechError speechError) {

            }
        });

        // 4.设置语音按钮
        mStartVoiceRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示dialog，接收语音输入
                mDialog.show();
            }
        });

    }

    private void initView() {
        mChatView = (ListView) findViewById(R.id.id_chat_listView);
        mMsg = (EditText) findViewById(R.id.id_chat_msg);
        mStartVoiceRecord = (Button) findViewById(R.id.btn_voice_input);
        mDatas.add(new ChatMessage(ChatMessage.Type.INPUT,
                "我是同小基，您的个人助理，我可是我济最帅的语音小助手~"));
    }

    public void sendMessage(View view) {
        final String msg = mMsg.getText().toString();
        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(this, "您还没有输入呢，小白看不见的呦~.", Toast.LENGTH_LONG).show();
            return;
        }


        ChatMessage to = new ChatMessage(ChatMessage.Type.OUTPUT, msg);
        to.setDate(new Date());
        mDatas.add(to);

        mAdapter.notifyDataSetChanged();
        mChatView.setSelection(mDatas.size() - 1);

        mMsg.setText("");

        // 关闭软键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实例
        if (imm.isActive()) {
            // 如果开启
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);
            // 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
        }

        new Thread() {
            public void run() {
                ChatMessage from = null;
                try {
                    from = HttpUtils.sendMsg(msg);
                } catch (Exception e) {
                    from = new ChatMessage(ChatMessage.Type.INPUT, "服务器正在做俯卧撑，估计累趴了~");
                }
                Message message = Message.obtain();
                message.obj = from;
                mHandler.sendMessage(message);
            }
        }.start();

    }
    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        mMsg.setText(resultBuffer.toString());
        mMsg.setSelection(mMsg.length());
    }

}
