package com.techv.camera1example;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int CAMERA_PERMISSION = 1;
    private int READ_WRITE_PERMISSION = 2;
    /** States for the flash */
    private Context mContext;
    MyApplication myApplication;
    Camera mCamera;
    int mCameraId;
    int used_camera_id;
    RelativeLayout flash_container;
    ImageButton btnCapture, btnFlashToggle, btnSwitchCamera;
    private RelativeLayout cameraPreview;
    private CameraPreview mPreview;
    private AlertDialog alertDialog;
    private boolean isPermission = false;
    private int sensorOrientation = -1;
    private SensorManager mSensorManager;
    private Sensor mSensorOrientation;
    public int mOrientationDeg; //last rotation in degrees
    public int mOrientationRounded; //last orientation int from above
    private static final int _DATA_X = 0;
    private static final int _DATA_Y = 1;
    private static final int _DATA_Z = 2;
    private int ORIENTATION_UNKNOWN = -1;
    private int tempOrientRounded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        isPermission = false;
        if(!checkCameraAvailibility()) {
            showAlert("Error", "Camera is not available on this phone", "finish");
            return;
        }

        // Get sensor manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // Get the default sensor of specified type
        mSensorOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        myApplication = MyApplication.getInstance();
        cameraPreview = findViewById(R.id.cameraPreview);
        btnCapture = findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(this);
        btnSwitchCamera = findViewById(R.id.btnSwitchCamera);
        btnSwitchCamera.setOnClickListener(this);
        initializeFlashUI();
        flash_container = findViewById(R.id.flash_container);
        checkCameraPermission();
    }

    private SensorEventListener mOrientationSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X*X + Y*Y;
            // Don't trust the angle if the magnitude is small compared to the y value
            if (magnitude * 4 >= Z*Z) {
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float)Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int)Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            //now we must figure out which orientation based on the degrees
            //Log.d("Oreination", ""+orientation);
            if (orientation != mOrientationDeg)
            {
                mOrientationDeg = orientation;
                //figure out actual orientation
                if(orientation == -1){//basically flat

                }
                else if(orientation <= 45 || orientation > 315){//round to 0
                    tempOrientRounded = 1;//portrait
                    sensorOrientation = 0;
                }
                else if(orientation > 45 && orientation <= 135){//round to 90
                    tempOrientRounded = 2; //right_landscape
                    sensorOrientation = 90;
                }
                else if(orientation > 135 && orientation <= 225){//round to 180
                    tempOrientRounded = 3; //reverse_portrait
                    sensorOrientation = 180;
                }
                else if(orientation > 225 && orientation <= 315){//round to 270
                    tempOrientRounded = 4;//left_landscape
                    sensorOrientation = 270;
                }
            }

            if(mOrientationRounded != tempOrientRounded){
                //Orientation changed, handle the change here
                mOrientationRounded = tempOrientRounded;

            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (mSensorOrientation != null) {
            mSensorManager.registerListener(mOrientationSensorListener, mSensorOrientation, SensorManager.SENSOR_DELAY_GAME);
        }

        if(mCamera==null && isPermission) {
            if(myApplication.isCameraBackFacing()) {
                if(openCamera(myApplication.getBackCameraId())) {
                    btnFlashToggle.setVisibility(View.VISIBLE);
                    btnFlashToggle.setOnClickListener(this);
                    mPreview.refreshCamera(mCamera);
                } else {
                    showAlert("System Error", "Fail to connect to camera service","finish");
                }
            } else {
                if(openCamera(myApplication.getFrontCameraId())) {
                    btnFlashToggle.setOnClickListener(null);
                    btnFlashToggle.setVisibility(View.INVISIBLE);
                    mPreview.refreshCamera(mCamera);
                } else {
                    showAlert("System Error", "Fail to connect to camera service","finish");
                }
            }
        }
    }

    @Override
    protected void onPause() {
        if (mSensorOrientation != null) {
            mSensorManager.unregisterListener(mOrientationSensorListener);
        }
        if(alertDialog!=null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        releaseCamera();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCapture:
                takePicture();
                break;
            case R.id.btnSwitchCamera:
                releaseCamera();
                switchCamera();
                break;
            case R.id.btnFlashToggle:
                if(hasFlash()) {
                    toggleFlashState();
                }
                break;
        }
    }

    private boolean checkCameraAvailibility(){
        if(mContext == null) {
            return false;
        }
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void initializeFlashUI(){
        btnFlashToggle = findViewById(R.id.btnFlashToggle);
        btnFlashToggle.setOnClickListener(this);
        flash_container = findViewById(R.id.flash_container);
        flash_container.setVisibility(View.VISIBLE);
    }

    private void checkCameraPermission() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
            } else {
                initCamera();
                isPermission = true;
            }
        } else {
            initCamera();
            isPermission = true;
        }
    }

    private void initCamera(){
        findCameraID();
        if(openCamera(mCameraId)) {
            mPreview = new CameraPreview(mContext, mCamera);
            initializeFlashUI();
            cameraPreview.addView(mPreview);
        } else {
            showAlert("System Error", "Fail to connect to camera service","finish");
        }
    }

    private void findCameraID(){
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                myApplication.setBackCameraId(i);
                myApplication.setCameraBackFacing();
                mCameraId = i;
            } else if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                myApplication.setFrontCameraId(i);
            }
        }
    }

    private boolean openCamera(int id){
        try {
            mCamera = Camera.open(id);
            if(hasFlash()) {
                flash_container.setVisibility(View.VISIBLE);
            } else {
                flash_container.setVisibility(View.INVISIBLE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return mCamera != null;
    }

    private void releaseCamera(){
        if(mCamera!=null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private void switchCamera() {
        if(myApplication.isCameraBackFacing()) {
            mCameraId = myApplication.getFrontCameraId();
            if(openCamera(mCameraId)) {
                if(!hasFlash()) {
                    btnFlashToggle.setOnClickListener(null);
                    btnFlashToggle.setVisibility(View.INVISIBLE);
                }
                myApplication.setCameraFrontFacing();
            } else {
                showAlert("System Error", "Fail to connect to camera service","finish");
            }
        } else {
            mCameraId = myApplication.getBackCameraId();
            if(openCamera(mCameraId)){
                btnFlashToggle.setOnClickListener(this);
                btnFlashToggle.setVisibility(View.VISIBLE);
            } else {
                showAlert("System Error", "Fail to connect to camera service","finish");
            }
            myApplication.setCameraBackFacing();
        }
        mPreview.setCamera(mCamera);
        mPreview.surfaceChanged(mPreview.getHolder(), ImageFormat.JPEG, mPreview.getPrev_width(), mPreview.getPre_height());
    }

    private boolean hasFlash(){
        if(mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            if (mCamera != null) {
                Camera.Parameters params = mCamera.getParameters();
                List<String> flashModes = params.getSupportedFlashModes();
                if (flashModes == null) {
                    return false;
                }
                for (String flashMode : flashModes) {
                    if (Camera.Parameters.FLASH_MODE_ON.equals(flashMode)) {
                        return true;
                    } else if (Camera.Parameters.FLASH_MODE_AUTO.equals(flashMode)) {
                        return true;
                    } else if (Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void setAutoFlash() {
        if (hasFlash()) {
            if(mPreview.getFlashState() == CameraPreview.FLASH_STATE_OFF){
                mPreview.setFlash(Camera.Parameters.FLASH_MODE_OFF);
            }
            else if(mPreview.getFlashState() == CameraPreview.FLASH_STATE_ON){
                mPreview.setFlash(Camera.Parameters.FLASH_MODE_ON);
            }
            else if(mPreview.getFlashState() == CameraPreview.FLASH_STATE_AUTO){
                mPreview.setFlash(Camera.Parameters.FLASH_MODE_AUTO);
            }
        }
    }

    private void setFlashIcon(){
        if(mPreview.getFlashState() == CameraPreview.FLASH_STATE_OFF){
            Glide.with(mContext)
                    .load(R.drawable.ic_flash_off)
                    .into(btnFlashToggle);
        }
        else if(mPreview.getFlashState() == CameraPreview.FLASH_STATE_ON){
            Glide.with(mContext)
                    .load(R.drawable.ic_flash_on)
                    .into(btnFlashToggle);
        }
        else if(mPreview.getFlashState() == CameraPreview.FLASH_STATE_AUTO){
            Glide.with(mContext)
                    .load(R.drawable.ic_flash_auto)
                    .into(btnFlashToggle);
        }
        setAutoFlash();
    }

    private void toggleFlashState(){
        if(mPreview.getFlashState() == CameraPreview.FLASH_STATE_OFF){
            mPreview.setFlashState(CameraPreview.FLASH_STATE_ON);
        }
        else if(mPreview.getFlashState() == CameraPreview.FLASH_STATE_ON){
            mPreview.setFlashState(CameraPreview.FLASH_STATE_AUTO);
        }
        else if(mPreview.getFlashState() == CameraPreview.FLASH_STATE_AUTO){
            mPreview.setFlashState(CameraPreview.FLASH_STATE_OFF);
        }
        setFlashIcon();
    }

    private void takePicture(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, READ_WRITE_PERMISSION);
            } else {
                if(mPreview.getSafeFlag()) {
                    mPreview.setSafeFlag(false);
                    used_camera_id = myApplication.getCameraFacing();
                    mCamera.takePicture(null, null, pictureCallback);
                } else {
                    Toast.makeText(mContext, "Previous taken picture is in progress to save, please wait", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if(mPreview.getSafeFlag()) {
                mPreview.setSafeFlag(false);
                used_camera_id = myApplication.getCameraFacing();
                mCamera.takePicture(null, null, pictureCallback);
            } else {
                Toast.makeText(mContext, "Previous taken picture is in progress to save, please wait", Toast.LENGTH_SHORT).show();
            }
        }
    }

    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();
            if (data == null) {
                //no path to picture, return
                mPreview.setSafeFlag(true);
                return;
            }

            Toast.makeText(mContext, "Picture has been captured", Toast.LENGTH_LONG).show();

            new SaveImageTask(mContext, data, sensorOrientation, used_camera_id, new MyApplication.OnPictureSave() {
                @Override
                public void onSave(final String filePath) {
                    Looper looper = mContext.getMainLooper();
                    new Handler(looper).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPreview.setSafeFlag(true);
                            if(filePath!=null) {
                                Toast.makeText(mContext, filePath, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(mContext, "Picture couldn't be saved", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },0);
                }
            }).execute();
        }
    };

    private void showAlert(String title, String message, final String type){
        if(alertDialog!=null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        alertDialog = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(type!=null && !type.equals("") && type.equalsIgnoreCase("finish")) {
                            finish();
                        }
                    }
                })
                .create();
        if(!MainActivity.this.isFinishing()) {
            alertDialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERMISSION) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCamera();
                isPermission = true;
            } else {
                isPermission = false;
                showAlert("Permission Denied", "Camera permission has been denied. You can't access camera without permission", "finish");
            }
        } else if(requestCode == READ_WRITE_PERMISSION) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(mCamera == null) {
                    if(myApplication.isCameraFrontFacing()) {
                        if(openCamera(myApplication.getFrontCameraId())) {
                            mPreview.refreshCamera(mCamera);
                        } else {
                            showAlert("System Error", "Fail to connect to camera service","finish");
                        }
                    } else if(myApplication.isCameraBackFacing()){
                        if(openCamera(myApplication.getBackCameraId())) {
                            mPreview.refreshCamera(mCamera);
                        } else {
                            showAlert("System Error", "Fail to connect to camera service","finish");
                        }
                    }
                }
                takePicture();
            } else {
                showAlert("Permission Denied", "Directory read/write permission has been denied. You can't capture a picture without permission", "");
            }
        }
    }
}
