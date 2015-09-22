package com.lr.ghp.appupdata.constants;

import android.os.Environment;

import java.io.File;

/**
 * Created by ghp on 15/9/14.
 * 类说明：  常量类
 */
public class Constants {
    //用于测试的packageName
    public static final String TEST_PACKAGENAME = "com.lr.ghp.appupdata";

    public static final String PATH = Environment.getExternalStorageDirectory() + File.separator;

    //合成得到的新版app
    public static final String NEW_APK_PATH = PATH + "appOldtoNew.apk";

    //从服务器下载来的查分包
    public static final String PATCH_PATH = PATH + "app.patch";
}
