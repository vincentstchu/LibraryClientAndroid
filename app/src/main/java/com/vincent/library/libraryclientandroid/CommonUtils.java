package com.vincent.library.libraryclientandroid;
import android.app.Activity;
import android.hardware.Camera;
import android.widget.Toast;

/**
 * Created by vincent on 17-12-29.
 */

/*
* 一些通用的函数
* */
public class CommonUtils {
    public static boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            canUse = false;
        }
        if (canUse) {
            if (mCamera != null)
                mCamera.release();
            mCamera = null;
        }
        return canUse;
    }

    /*
    * 在子线程中使用Toast发送消息
    * */
    public static void Toastinthread(final String str, final Activity activity) {
        final String s = str;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity,str, Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*
    * 用户howe所写的服务器端的错误码
    * */
    public static void ShowError(String res,Activity activity) {
        String str = null;
        switch(res) {
            case "200":
                str = "成功";
                break;
            case "400":
                str = "失败";
                break;
            case "401":
                str = "未认证";
                break;
            case "404":
                str = "接口不存在";
                break;
            case "505":
                str = "服务器内部错误";
                break;
            default:
                str = "未知错误";
                break;
        }
        Toastinthread(str,activity);
    }


}
