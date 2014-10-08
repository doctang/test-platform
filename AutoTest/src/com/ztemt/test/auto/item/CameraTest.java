package com.ztemt.test.auto.item;

import java.io.File;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;

import com.ztemt.test.auto.R;

public class CameraTest extends BaseTest {

    public CameraTest(Context context) {
        super(context);
    }

    @Override
    public void onRun() {
        int number = Camera.getNumberOfCameras();
        if (number <= 0) {
            sleep(1000);
            setFailure();
            return;
        }

        for (int i = 0; i < number; i++) {
            captureImage(i);
            sleep(3000); // preview
            Instrumentation inst = new Instrumentation();
            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_FOCUS);
            sleep(1000); // focus
            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_CAMERA);
            sleep(3000); // camera
            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            sleep(1000);
        }
        setSuccess();
    }

    @Override
    public String getTitle() {
        return mContext.getString(R.string.camera_test);
    }

    private void captureImage(int cameraId) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                Environment.getExternalStorageDirectory(), "test.jpg")));
        intent.putExtra("android.intent.extras.CAMERA_FACING", cameraId);
        mContext.startActivity(intent);
    }
}
