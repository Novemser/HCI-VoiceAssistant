package com.novemser.voicetest;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
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

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.cloud.util.ContactManager;
import com.novemser.voicetest.actions.BaseAction;
import com.novemser.voicetest.actions.CallAction;
import com.novemser.voicetest.actions.SendSmsAction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    /**
     * 展示消息的listview
     */
    private ListView mChatView;
    /**
     * 文本域
     */
    public static EditText mMsg;
    /**
     * 存储聊天消息
     */
    private List<ChatMessage> mDatas = new ArrayList<ChatMessage>();
    /**
     * 适配器
     */
    private ListMessageAdapter mAdapter;
    private Button mStartVoiceRecord;
    private SpeechSynthesizer speechSynthesizer;

    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();

    private TextUnderstander understander;

    private static final String[] ignorePhrase = {"请", "麻烦", "给", "用"};

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            ChatMessage from = (ChatMessage) msg.obj;
            mDatas.add(from);
            mAdapter.notifyDataSetChanged();
            mChatView.setSelection(mDatas.size() - 1);
            speechSynthesizer.startSpeaking(from.getMsg(), mSynListener);
            // Google TTS cannot be used!!!
//            s = "http://translate.google.cn/translate_tts?ie=UTF-8&q=%E6%88%91%E5%8B%92%E4%B8%AA%E5%8E%BB&tl=zh-CN&total=1&idx=0&textlen=4&tk=743200.877443&client=t&prev=input&ttsspeed=2.24";
//            MediaPlayer mediaPlayer = new MediaPlayer();
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            try {
//                mediaPlayer.setDataSource(s);
//                mediaPlayer.prepare(); // might take long! (for buffering, etc)
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            mediaPlayer.start();
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

        // 设置全局Context
        BaseAction.context = getApplicationContext();

        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=573d5744");
        //1.创建RecognizerDialog对象
        mDialog = new RecognizerDialog(this, null);
        mIat = SpeechRecognizer.createRecognizer(this, null);
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
                Log.d("VoiceResult", recognizerResult.getResultString());
                printResult(recognizerResult);
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

        // 初始化TTS功能
        initTTS();

        // 5.初始化语义理解器
        understander = TextUnderstander.createTextUnderstander(this, null);

        // 6.上传联系人姓名列表
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        
        if (!sharedPreferences.getBoolean("isContactUploaded", false)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            ContactManager manager = ContactManager.createManager(this, contactListener);
            manager.asyncQueryAllContactsName();
            editor.putBoolean("isContactUploaded", true);
        }
    }

    private ContactManager.ContactListener contactListener = new ContactManager.ContactListener() {
        @Override
        public void onContactQueryFinish(String s, boolean b) {
            //指定引擎类型
            mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
            int ret = mIat.updateLexicon("contact", s, new LexiconListener() {
                @Override
                public void onLexiconUpdated(String s, SpeechError speechError) {
                    if (speechError != null) {
                        Log.d("contact", speechError.toString());
                    } else {
                        Log.d("contact", "上传成功！ ");
                    }
                }
            });
            if (ret != ErrorCode.SUCCESS) {
                Log.d("Contact", "上传联系人失败： " + ret);
            }
        }
    };

    private TextUnderstanderListener textUnderstanderListener = new TextUnderstanderListener() {
        @Override
        public void onResult(UnderstanderResult understanderResult) {
            Log.d("Understanding result", understanderResult.getResultString());
            HashMap map;
            map = JsonParser.parseSemanticResult(understanderResult.getResultString());
            if (map != null && map.size() > 0) {
                if (map.containsKey("operation")) {
                    String op = (String) map.get("operation");
                    // 发短信
                    if (op.equals("SEND")) {
                        SmsManager manager = SmsManager.getDefault();
                        if (map.containsKey("code"))
                            SendSmsAction.sendMessage((String) map.get("code"), (String) map.get("content"), manager);
                        else if (map.containsKey("name"))
                            SendSmsAction.sendMessage((String) map.get("name"), (String) map.get("content"), manager);
                        // 没有指定发送的人/内容
                        else {

                        }
                    }
                    // 打电话
                    else if (op.equals("CALL")) {
                        if (map.containsKey("code"))
                            CallAction.makeCallTo((String) map.get("code"));
                        else if (map.containsKey("name"))
                            CallAction.makeCallTo((String) map.get("name"));
                        // 没有指定打给谁
                        else {

                        }
                    }
                } else {
                    return;
                }

            }
        }

        @Override
        public void onError(SpeechError speechError) {

        }
    };

    private void initTTS() {
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(this, null);
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoqi");
        speechSynthesizer.setParameter(SpeechConstant.SPEED, "60");
        speechSynthesizer.setParameter(SpeechConstant.VOLUME, "80");
        speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
    }

    private SynthesizerListener mSynListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

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
                Matcher matcher = CallAction.pattern.matcher(filter(msg));
                if (matcher.matches()) {
                    // 理解语义
                    understander.understandText(msg, textUnderstanderListener);
                    return;
                }
                matcher = SendSmsAction.pattern.matcher(filter(msg));
                if (matcher.matches()) {
                    // 理解语义
                    understander.understandText(msg, textUnderstanderListener);
                    return;
                }


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

        try {
            if (sn.equals("2"))
                return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        mMsg.setText(resultBuffer.toString());
        mMsg.setSelection(mMsg.length());

        // 发送消息给同小基
        sendMessage(mStartVoiceRecord);
    }

    /**
     * 创建网络mp3
     *
     * @return
     */
    public MediaPlayer createNetMp3(String url) {
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(url);
        } catch (IllegalArgumentException e) {
            return null;
        } catch (IllegalStateException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return mp;
    }

    private String filter(String str) {
        String result = str;
        for (String s : ignorePhrase) {
            result = result.replace(s, "");
        }
        return result;
    }

}