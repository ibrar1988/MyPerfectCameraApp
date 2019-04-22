package com.techv.camera1example;

public interface CameraInterface {
    void setCameraFrontFacing();
    void setCameraBackFacing();
    boolean isCameraFrontFacing();
    boolean isCameraBackFacing();
    void setFrontCameraId(int cameraId);
    void setBackCameraId(int cameraId);
    int getFrontCameraId();
    int getBackCameraId();
    int getCameraFacing();
}
