package com.novemser.voicetest.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.sunflower.FlowerCollector;
import com.novemser.voicetest.R;
import com.novemser.voicetest.utils.Global;
import com.novemser.voicetest.utils.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class VoiceRecActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = VoiceRecActivity.class.getSimpleName();
    private StringBuffer resultBuffer = new StringBuffer();

    private com.iflytek.cloud.SpeechRecognizer mIat;

    private RecognizerDialog mIatDialog;

    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    public static final String PREFER_NAME = "com.iflytek.setting";

    private EditText mResultText;
    private Toast mToast;
    private SharedPreferences mSharedPreferences;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private Socket mSocket;

    private final String socketMsg = "chat message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_rec);
        initLayout();

        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=573d5744");

        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);
        mIatDialog = new RecognizerDialog(this, mInitListener);

        mSharedPreferences = getSharedPreferences(PREFER_NAME,
                Activity.MODE_PRIVATE);
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        mResultText = ((EditText) findViewById(R.id.iat_text));

        mSocket = Global.getSocket();
        mSocket.on(socketMsg, onLogin);
//        attemptLogin();
    }



    private void attemptLogin() {
        mSocket.emit("add user", "Nova");
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(this.getClass().getName(), "Login succeed");
//            JSONObject data = (JSONObject) args[0];
//
//            int numUsers;
//            try {
//                numUsers = data.getInt("numUsers");
//                System.out.println(numUsers);
//            } catch (JSONException e) {
//                return;
//            }
        }
    };

    /**
     * 初始化Layout。
     */
    private void initLayout() {
        findViewById(R.id.iat_recognize).setOnClickListener(VoiceRecActivity.this);
        findViewById(R.id.iat_upload_contacts).setOnClickListener(VoiceRecActivity.this);
        findViewById(R.id.iat_upload_userwords).setOnClickListener(VoiceRecActivity.this);
        findViewById(R.id.iat_stop).setOnClickListener(VoiceRecActivity.this);
    }

    int ret = 0; // 函数调用返回值

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 进入参数设置页面
//            case R.id.image_iat_set:
//                Intent intents = new Intent(this, IatSettings.class);
//                startActivity(intents);
//                break;
            // 开始听写
            // 如何判断一次听写结束：OnResult isLast=true 或者 onError
            case R.id.iat_recognize:
                // 移动数据分析，收集开始听写事件
                FlowerCollector.onEvent(this, "iat_recognize");

                mResultText.setText(null);// 清空显示内容
                mIatResults.clear();
                // 设置参数
                setParam();
                boolean isShowDialog = false;
                if (isShowDialog) {
//                    // 显示听写对话框
//                    mIatDialog.setListener(mRecognizerDialogListener);
//                    mIatDialog.show();
//                    showTip(getString(R.string.text_begin));
                } else {
                    // 不显示听写对话框
                    ret = mIat.startListening(mRecognizerListener);
                    if (ret != ErrorCode.SUCCESS) {
                        showTip("听写失败,错误码：" + ret);
                    } else {
                        showTip(getString(R.string.text_begin));
                    }
                }
                break;
            // 停止听写
            case R.id.iat_stop:
                mIat.cancel();
                showTip("停止听写");
                break;
//            // 取消听写
//            case R.id.iat_cancel:
//                mIat.cancel();
//                showTip("取消听写");
//                break;
//            // 上传联系人
//            case R.id.iat_upload_contacts:
//                showTip(getString(R.string.text_upload_contacts));
//                ContactManager mgr = ContactManager.createManager(this,
//                        mContactListener);
//                mgr.asyncQueryAllContactsName();
//                break;
//            // 上传用户词表
//            case R.id.iat_upload_userwords:
//                showTip(getString(R.string.text_upload_userwords));
//                String contents = FucUtil.readFile(IatDemo.this, "userwords","utf-8");
//                mResultText.setText(contents);
//                // 指定引擎类型
//                mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
//                mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
//                ret = mIat.updateLexicon("userword", contents, mLexiconListener);
//                if (ret != ErrorCode.SUCCESS)
//                    showTip("上传热词失败,错误码：" + ret);
//                break;
            default:
                break;
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };

//    /**
//     * 上传联系人/词表监听器。
//     */
//    private LexiconListener mLexiconListener = new LexiconListener() {
//
//        @Override
//        public void onLexiconUpdated(String lexiconId, SpeechError error) {
//            if (error != null) {
//                showTip(error.toString());
//            } else {
//                showTip(getString(R.string.text_upload_success));
//            }
//        }
//    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            showTip(error.getPlainDescription(true));
            mIat.startListening(mRecognizerListener);
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
//            showTip("结束说话");

        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.e("Is Last " + isLast, results.getResultString());
            printResult(results);

            // 最后的结果
            if (isLast) {
                Log.e(getClass().getName(), resultBuffer.toString());
                // 发送消息
                mSocket.emit(socketMsg, resultBuffer.toString());
                mIat.startListening(mRecognizerListener);
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

//    /**
//     * 听写UI监听器
//     */
//    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
//        public void onResult(RecognizerResult results, boolean isLast) {
//            printResult(results);
//        }
//
//        /**
//         * 识别回调错误.
//         */
//        public void onError(SpeechError error) {
//            showTip(error.getPlainDescription(true));
//        }
//
//    };

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
        resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        mResultText.setText(resultBuffer.toString());
        mResultText.setSelection(mResultText.length());
    }

    /**
     * 参数设置
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
        mIat.cancel();
        mIat.destroy();
        mSocket.off(socketMsg, onLogin);
    }
}
