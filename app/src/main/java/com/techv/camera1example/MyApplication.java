package com.techv.camera1example;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application implements CameraInterface {
    private String TAG = MyApplication.class.getSimpleName();
    private static MyApplication mInstance = null;
    public static int CAMERA_POSITION_FRONT;
    public static int CAMERA_POSITION_BACK;
    public int mCameraOrientation = -1; // Front-facing or back-facing

    private MyApplication(){
        // Can not be instantiate
    }

    public static MyApplication getInstance() {
        if(mInstance == null) {
            mInstance = new MyApplication();
        }

        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void setCameraFrontFacing() {
        Log.d(TAG, "setCameraFrontFacing: setting camera to front facing.");
        mCameraOrientation = CAMERA_POSITION_FRONT;
    }

    @Override
    public void setCameraBackFacing() {
        Log.d(TAG, "setCameraBackFacing: setting camera to back facing.");
        mCameraOrientation = CAMERA_POSITION_BACK;
    }

    @Override
    public void setFrontCameraId(int cameraId){
        CAMERA_POSITION_FRONT = cameraId;
    }


    @Override
    public void setBackCameraId(int cameraId){
        CAMERA_POSITION_BACK = cameraId;
    }

    @Override
    public boolean isCameraFrontFacing() {
        if(mCameraOrientation == CAMERA_POSITION_FRONT){
            return true;
        }
        return false;
    }

    @Override
    public boolean isCameraBackFacing() {
        if(mCameraOrientation == CAMERA_POSITION_BACK) {
            return true;
        }
        return false;
    }

    @Override
    public int getBackCameraId(){
        return CAMERA_POSITION_BACK;
    }

    @Override
    public int getFrontCameraId(){
        return CAMERA_POSITION_FRONT;
    }

    @Override
    public int getCameraFacing(){
        return mCameraOrientation;
    }

    interface OnPictureSave {
        void onSave(String filePath);
    }
}
