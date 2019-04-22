package com.techv.camera1example;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveImageTask extends AsyncTask<Void, Void, Void> {

    String TAG = "SaveImageTask";

    private Context mContext;
    private int cameraOrientation;
    byte[] mData;
    int used_camera_id;
    MyApplication.OnPictureSave onPictureSaveListener;

    public SaveImageTask(Context context, byte[] data, int cam_orient, int camera_id, MyApplication.OnPictureSave listenerCallback){
        this.mContext = context;
        this.cameraOrientation = cam_orient;
        mData = data;
        used_camera_id = camera_id;
        onPictureSaveListener = listenerCallback;
    }

    private File createDirectory(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "TechVCameraApp");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.e("TechVCameraApp", "failed to create directory");
                return null;
            }
        }
        return mediaStorageDir;
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        mContext.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        FileOutputStream outStream = null;
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = false;
            Bitmap source = BitmapFactory.decodeByteArray(mData, 0, mData.length, bmOptions);
            Matrix matrix = new Matrix();
            if(cameraOrientation == 0 && used_camera_id == 1) {
                cameraOrientation = 180;
            } else if(cameraOrientation == 270 && used_camera_id == 1) {
                cameraOrientation = -90;
            }
            matrix.setRotate(cameraOrientation);
            Bitmap newBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

            File mediaStorageDir = createDirectory();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File filePathToSave = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".png");

            outStream = new FileOutputStream(filePathToSave);

            newBitmap.compress(Bitmap.CompressFormat.PNG,100, outStream);
            outStream.close();
            outStream.flush();
            refreshGallery(filePathToSave);
            newBitmap.recycle();
            newBitmap = null;
            onPictureSaveListener.onSave(filePathToSave.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            onPictureSaveListener.onSave(null);
        }
        return null;
    }
}
