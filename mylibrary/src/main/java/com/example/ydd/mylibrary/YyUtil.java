package com.example.ydd.mylibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.TtsMode;

import java.io.File;
import java.io.IOException;

public class YyUtil {

    @SuppressLint("SdCardPath")
    private static final String TEMP_DIR = "/sdcard/baiduTTS"; // 将assets目录下的3个dat 文件复制到该目录

    // 请确保该PATH下有这个文件
    private static final String TEXT_FILENAME = TEMP_DIR + "/" + "bd_etts_text.dat";

    // 请确保该PATH下有这个文件 ，m15是离线男声
    private static final String MODEL_FILENAME =
            TEMP_DIR + "/" + "bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat";

    private SpeechSynthesizer mSpeechSynthesizer;

    public YyUtil(Context context,String appId,String apiKey,String secretKey) {


        if (!checkOfflineResources()) {
            try {
                new OfflineResource(context.getApplicationContext(), OfflineResource.VOICE_MALE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("DOAING", "离线资源存在并且可读, 目录：" + TEMP_DIR);
        }


        // 1. 获取实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(context.getApplicationContext());

        // 2. 设置appId，appKey.secretKey
        mSpeechSynthesizer.setAppId(appId);
        mSpeechSynthesizer.setApiKey(apiKey, secretKey);
        mSpeechSynthesizer.auth(TtsMode.MIX);

        // 3. 设置listener
        mSpeechSynthesizer.setSpeechSynthesizerListener(new MessageListener());

        Log.e("DOAING","开始启动鉴权");
        if (!checkAuth()) {
            return;
        }
        // 文本模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
        // 声学模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);

        // 5. 以下setParam 参数选填。不填写则默认值生效
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "4");
        // 设置合成的音量，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");

        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_NETWORK);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL);

        //  初始化

         mSpeechSynthesizer.initTts(TtsMode.MIX);

    }

    public void speak(String info) {
        /* 以下参数每次合成时都可以修改
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
         *  设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "5"); 设置合成的音量，0-9 ，默认 5
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5"); 设置合成的语速，0-9 ，默认 5
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5"); 设置合成的语调，0-9 ，默认 5
         *
         *  mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
         *  MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
         *  MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
         *  MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
         *  MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
         */

        if (mSpeechSynthesizer == null) {
            Log.e("DOAING","[ERROR], 初始化失败");
            return;
        }
        int result = mSpeechSynthesizer.speak(info);

        //Log.e("DOAING","合成并播放 按钮已经点击");

        checkResult(result, "speak");
    }
    /**
     * 检查 TEXT_FILENAME, MODEL_FILENAME 这2个文件是否存在，不存在请自行从assets目录里手动复制
     *
     * @return 离线文档是否可用
     */
    private boolean checkOfflineResources() {
        String[] filenames = {TEXT_FILENAME, MODEL_FILENAME};
        for (String path : filenames) {
            File f = new File(path);
            if (!f.canRead()) {
                Log.e("DOAING", "[ERROR] 文件不存在或者不可读取，请从assets目录复制同名文件到：" + path);
                return false;
            }
        }
        return true;
    }

    /**
     * 检查appId ak sk 是否填写正确，另外检查官网应用内设置的包名是否与运行时的包名一致。
     *
     * @return 校验是否成功
     */
    private boolean checkAuth() {
        AuthInfo authInfo = mSpeechSynthesizer.auth(TtsMode.MIX);
        if (!authInfo.isSuccess()) {
            // 离线授权需要网站上的应用填写包名。本demo的包名是com.baidu.tts.sample，定义在build.gradle中
            String errorMsg = authInfo.getTtsError().getDetailMessage();
            Log.e("DOAING","【error】鉴权失败 errorMsg=" + errorMsg);
            return false;
        } else {
            Log.e("DOAING","验证通过，离线正式授权文件存在。");
            return true;
        }
    }


    /**
     * 释放所有资源
     */
    public void close() {

        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stop();
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
            Log.e("DOAING", "释放资源成功");

        }
    }
    private void checkResult(int result, String method) {
        if (result != 0) {
            Log.e("DOAING","error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }

}
