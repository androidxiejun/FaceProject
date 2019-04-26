package com.dhht.arcsoftlib;

// ┏┓　　　┏┓
// ┏┛┻━━━┛┻┓
// ┃　　　　　　　┃ 　
// ┃　　　━　　　┃
// ┃　┳┛　┗┳　┃
// ┃　　　　　　　┃
// ┃　　　┻　　　┃
// ┃　　　　　　　┃
// ┗━┓　　　┏━┛
// ┃　　　┃ 神兽保佑　　　　　　　　
// ┃　　　┃ 代码无BUG！
// ┃　　　┗━━━┓
// ┃　　　　　　　┣┓
// ┃　　　　　　　┏┛
// ┗┓┓┏━┳┓┏┛
// ┃┫┫　┃┫┫
// ┗┻┛　┗┻┛

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKFace;
import com.arcsoft.idcardveri.CompareResult;
import com.arcsoft.idcardveri.DetectFaceResult;
import com.arcsoft.idcardveri.IdCardVerifyError;
import com.arcsoft.idcardveri.IdCardVerifyManager;
import com.dhht.arcsoftlib.facedetection.FdManager;
import com.dhht.arcsoftlib.facerecognition.FrManager;
import com.dhht.arcsoftlib.facetrace.FtManager;
import com.dhht.arcsoftlib.util.ArcUtils;

import java.util.List;

/**
 * CreateDate：2018/10/16
 * Creator： VNBear
 * Description:
 **/
public class ArcsoftSDK {

    private final String TAG = "======" + this.getClass().getSimpleName();
    private Context mContext;
    private ArcsoftConfig mConfig;
    private FdManager mFdManager;
    private FrManager mFrManager;
    private FtManager mFtManager;

    private ArcsoftSDK() {

    }

    private static final class InstanceHolder {
        private static final ArcsoftSDK INSTANCE = new ArcsoftSDK();
    }

    public static ArcsoftSDK getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void init(Context context, ArcsoftConfig config) {
        this.mContext = context;
        this.mConfig = config;
        initEngine(config);
    }

    /**
     * 初始化引擎
     *
     * @param config
     */
    private void initEngine(ArcsoftConfig config) {
        //人证SDK
        int result = IdCardVerifyManager.getInstance().init(config.getAppid(), config.getSdk_key());
        if (result == 1000) {
            Log.d(TAG, "人证核验SDK初始化完成");
        } else {
            Log.d(TAG, "人证核验SDK初始化失败,error:" + result);
        }

        //人脸检测

        if (mFdManager == null) {
            mFdManager = FdManager.getInstance();
        }
        mFdManager.initEngine(config.getAppid(), config.getFd_key());

        //人脸识别
        if (mFrManager == null) {
            mFrManager = FrManager.getInstance();
        }
        mFrManager.initEngine(config.getAppid(), config.getSdk_key());

        //人脸追踪
        if (mFtManager == null) {
            mFtManager = FtManager.getInstance();
        }
        mFtManager.initEngine(config.getAppid(), config.getFt_key());

    }

    /**
     * 销毁引擎
     */
    public void uninitEngine() {
        if (mFdManager != null) {
            mFdManager.uninitEngine();
            mFdManager = null;
        }

        if (mFrManager != null) {
            mFrManager.uninitEngine();
            mFrManager = null;
        }

        if (mFtManager != null) {
            mFtManager.uninitEngine();
            mFtManager = null;
        }

        IdCardVerifyManager.getInstance().unInit();
        mContext = null;
    }

    /**
     * 人证核验先录入身份证图片
     * @param idCard
     */
    public boolean inputIdCardData(Bitmap idCard){
        byte[] nv21Data = ArcUtils.bitmapToNV21(idCard.getWidth(), idCard.getHeight(), idCard);
        DetectFaceResult result = IdCardVerifyManager.getInstance().inputIdCardData(nv21Data, idCard.getWidth(), idCard.getHeight());
        if (result.getErrCode() != IdCardVerifyError.OK) {
            Log.d(TAG, "IdCardData  input  error :" + result.getErrCode());
            return false;
        }
        return true;
    }


    /**
     * 设置比对
     * @param previewData
     * @param width
     * @param height
     * @return
     */
    public double idCardVerify( byte[] previewData, int width, int height){
        DetectFaceResult result = IdCardVerifyManager.getInstance().onPreviewData(previewData, width, height, false);
        if (result.getErrCode() != IdCardVerifyError.OK) {
            Log.d(TAG, "PreviewData  input  error :" + result.getErrCode());
        }

        CompareResult compareResult = IdCardVerifyManager.getInstance().compareFeature(0.8);
        Log.d(TAG, "compareFeature: result " + compareResult.getResult() + ", isSuccess "
                + compareResult.isSuccess() + ", errCode " + compareResult.getErrCode());
        return compareResult.getResult();
    }


    /**
     * 人证比对
     *
     * @param idCard
     * @param previewData
     * @param width
     * @param height
     * @return 比对结果
     */
    public double idCardVerify(Bitmap idCard, byte[] previewData, int width, int height) {
        if (mContext == null) {
            throw new RuntimeException("please init ArcsoftSDK at first");
        }

        byte[] nv21Data = ArcUtils.bitmapToNV21(idCard.getWidth(), idCard.getHeight(), idCard);
        DetectFaceResult result = IdCardVerifyManager.getInstance().inputIdCardData(nv21Data, idCard.getWidth(), idCard.getHeight());
        if (result.getErrCode() != IdCardVerifyError.OK) {
            Log.d(TAG, "IdCardData  input  error :" + result.getErrCode());
            return 0;
        }

        result = IdCardVerifyManager.getInstance().onPreviewData(previewData, width, height, false);
        if (result.getErrCode() != IdCardVerifyError.OK) {
            Log.d(TAG, "PreviewData  input  error :" + result.getErrCode());
            return 0;
        }

        CompareResult compareResult = IdCardVerifyManager.getInstance().compareFeature(0.8);
        Log.d(TAG, "compareFeature: result " + compareResult.getResult() + ", isSuccess "
                + compareResult.isSuccess() + ", errCode " + compareResult.getErrCode());
        return compareResult.getResult();
    }

    /**
     * 检测输入的图像中存在的人脸(针对单张图片)
     *
     * @param image
     * @param width
     * @param height
     * @return
     */
    public List<AFD_FSDKFace> imageFaceDetection(byte[] image, int width, int height) {
        if (mFdManager == null) {
            throw new RuntimeException("please init ArcsoftSDK at first");
        }
        return mFdManager.stillImageFaceDetection(image, width, height);
    }

    /**
     * 检测输入图像中的人脸特征信息
     *
     * @param data
     * @param width
     * @param height
     * @return
     */
    public AFR_FSDKFace extractFRFeature(byte[] data, int width, int height, Rect rect, int degree) {
        if (mFrManager == null) {
            throw new RuntimeException("please init ArcsoftSDK at first");
        }
        return mFrManager.extractFRFeature(data, width, height, new Rect(rect), degree);
    }

    /**
     * 对比两张人脸的相似度
     *
     * @param face1
     * @param face2
     * @return
     */
    public float match(AFR_FSDKFace face1, AFR_FSDKFace face2) {
        if (mFrManager == null) {
            throw new RuntimeException("please init ArcsoftSDK at first");
        }
        return mFrManager.match(face1, face2);
    }

    /**
     * 检测输入的图像中存在的人脸(针对视屏流)
     *
     * @param data
     * @param width
     * @param height
     * @return
     */
    public List<AFT_FSDKFace> videoFaceFeatureDetect(byte[] data, int width, int height) {
        if (mFtManager == null) {
            throw new RuntimeException("please init ArcsoftSDK at first");
        }
        return mFtManager.faceFeatureDetect(data, width, height);
    }

    /**
     * 获取一个新的fd引擎
     *
     * @return
     */
    public FdManager getNewInstanceFdManager() {
        if (mConfig == null) {
            throw new RuntimeException("please init ArcsoftSDK at first");
        }
        return FdManager.getNewInstance(mConfig.getAppid(), mConfig.getFd_key());
    }

    /**
     * 获取一个新的ft引擎
     *
     * @return
     */
    public FtManager getNewInstanceFtManager() {
        if (mConfig == null) {
            throw new RuntimeException("please init ArcsoftSDK at first");
        }
        return FtManager.getNewInstance(mConfig.getAppid(), mConfig.getFt_key());
    }

    /**
     * 获取一个新的fr引擎
     *
     * @return
     */
    public FrManager getNewInstanceFrManager() {
        if (mConfig == null) {
            throw new RuntimeException("please init ArcsoftSDK at first");
        }
        return FrManager.getNewInstance(mConfig.getAppid(), mConfig.getSdk_key());
    }
}
