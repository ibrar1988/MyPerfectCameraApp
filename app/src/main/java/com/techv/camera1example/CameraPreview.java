package com.techv.camera1example;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private Context mContext;
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private boolean safeToTakePicture = false;
    private int prev_width, pre_height;
    private Camera.Parameters parameters;
    MyApplication myApplication;
    private int mFlashState = 0;
    public static final int FLASH_STATE_OFF = 0;
    public static final int FLASH_STATE_ON = 1;
    public static final int FLASH_STATE_AUTO = 2;

    public CameraPreview(Context context){
        super(context);
    }

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mContext = context;
        this.mCamera = camera;
        this.mHolder = getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.mHolder.addCallback(this);
        myApplication = MyApplication.getInstance();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //if(parameters==null) {
        parameters = mCamera.getParameters();
        //}
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.prev_width = width;
        this.pre_height = height;
        setCameraParameters(this.prev_width, this.pre_height);

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            safeToTakePicture = true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    public void setFlashState(int state){
        mFlashState = state;
    }

    public int getFlashState(){
        return mFlashState;
    }

    public boolean getSafeFlag(){
        return safeToTakePicture;
    }

    public void setSafeFlag(boolean flag){
        safeToTakePicture = flag;
    }

    public int getPrev_width() {
        return prev_width;
    }

    public int getPre_height() {
        return pre_height;
    }

    public void setFlash(String flashType) {
        switch (flashType) {
            case Camera.Parameters.FLASH_MODE_ON:
                if(parameters!=null) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    mCamera.setParameters(parameters);
                    //refreshCamera(mCamera);
                    try {
                        mCamera.setPreviewDisplay(mHolder);
                        mCamera.startPreview();
                        safeToTakePicture = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Camera.Parameters.FLASH_MODE_AUTO:
                if(parameters!=null) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    mCamera.setParameters(parameters);
                    //refreshCamera(mCamera);
                    try {
                        mCamera.setPreviewDisplay(mHolder);
                        mCamera.startPreview();
                        safeToTakePicture = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Camera.Parameters.FLASH_MODE_OFF:
                if(parameters!=null) {
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(parameters);
                    //refreshCamera(mCamera);
                    try {
                        mCamera.setPreviewDisplay(mHolder);
                        mCamera.startPreview();
                        safeToTakePicture = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void refreshCamera(Camera camera){
        if(mHolder.getSurface()==null) {
            return;
        }
        setCamera(camera);
        setCameraParameters(this.prev_width, this.pre_height);
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            safeToTakePicture = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCameraParameters(int width, int height){
        parameters = mCamera.getParameters();
        if(getFlashState() == CameraPreview.FLASH_STATE_OFF){
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        else if(getFlashState() == CameraPreview.FLASH_STATE_ON){
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        }
        else if(getFlashState() == CameraPreview.FLASH_STATE_AUTO){
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        }
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
        //parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_FLUORESCENT);
        //parameters.setColorEffect(Camera.Parameters.EFFECT_NEGATIVE);
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setRotation(90);
        setCameraDisplayOrientation(myApplication.getCameraFacing());
        /*
        Camera.Size prev_size = getBestPreviewSize(width, height, parameters);
        if (prev_size != null) {
            parameters.setPreviewSize(width, height);
        }
        */
        setPictureSize(parameters);
        mCamera.setParameters(parameters);
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Log.e("Holder_Width : " + width , "Holder_ Height : " + height);
        Camera.Size result = null;
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size size: sizes) {
            Log.e("Prev_Width : " + size.width , "Prev_Height : " + size.height);
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }
        return (result);
    }

    private void setPictureSize(Camera.Parameters parameters){
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        Camera.Size size = sizes.get(0);
        for(int i=0;i<sizes.size();i++)
        {
            if(sizes.get(i).width > size.width)
                size = sizes.get(i);
        }
        parameters.setPictureSize(size.width, size.height);
    }

    void setCameraDisplayOrientation(int cameraId) {
        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = ((MainActivity)mContext).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
    }
}
