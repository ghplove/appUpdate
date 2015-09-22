package com.lr.ghp.appupdata.constants;

/**
 * Created by ghp on 15/9/21.
 */
public class StatusConstants {
    // 合成成功
    public static final int WHAT_SUCCESS = 1;

    // 合成的APK签名和已安装的签名不一致
    public static final int WHAT_FAIL_SING = -1;

    // 合成失败
    public static final int WHAT_FAIL_ERROR = -2;

    // 获取源文件失败
    public static final int WHAT_FAIL_GET_SOURCE = -3;
}
