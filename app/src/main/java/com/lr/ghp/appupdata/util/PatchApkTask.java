package com.lr.ghp.appupdata.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.lr.ghp.appupdata.constants.Constants;
import com.lr.ghp.appupdata.constants.StatusConstants;
import com.lr.ghp.utils.PatchUtils;

/**
 * Created by ghp on 15/9/22.
 */
public class PatchApkTask extends AsyncTask<String, Void, Integer> {

    private long mBeginTime, mEndTime;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private TextView mResultView;

    public PatchApkTask(Context mContext, ProgressDialog mProgressDialog, TextView mResultView) {
        this.mContext = mContext;
        this.mProgressDialog = mProgressDialog;
        this.mResultView = mResultView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.show();
        mResultView.setText("");
        mBeginTime = System.currentTimeMillis();
    }

    @Override
    protected Integer doInBackground(String... params) {
        String oldApkSource = ApkUtils.getSourceApkPath(mContext, Constants.TEST_PACKAGENAME);

        if (!TextUtils.isEmpty(oldApkSource)) {

            int patchResult = PatchUtils.patch(oldApkSource,
                    Constants.NEW_APK_PATH, Constants.PATCH_PATH);

            if (patchResult == 0) {

                String signatureNew = SignUtils
                        .getUnInstalledApkSignature(Constants.NEW_APK_PATH);

                String signatureSource = SignUtils
                        .getInstalledApkSignature(mContext,
                                Constants.TEST_PACKAGENAME);

                if (!TextUtils.isEmpty(signatureNew)
                        && !TextUtils.isEmpty(signatureSource)
                        && signatureNew.equals(signatureSource)) {
                    return StatusConstants.WHAT_SUCCESS;
                } else {
                    return StatusConstants.WHAT_FAIL_SING;
                }
            } else {
                return StatusConstants.WHAT_FAIL_ERROR;
            }
        } else {
            return StatusConstants.WHAT_FAIL_GET_SOURCE;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        mEndTime = System.currentTimeMillis();
        mResultView.setText("耗时: " + (mEndTime - mBeginTime) + "ms");

        switch (result) {
            case StatusConstants.WHAT_SUCCESS: {

                String text = "新apk已合成成功：" + Constants.NEW_APK_PATH;
                showShortToast(text);

                ApkUtils.installApk(mContext, Constants.NEW_APK_PATH);
                break;
            }
            case StatusConstants.WHAT_FAIL_SING: {
                String text = "新apk已合成失败，签名不一致";
                showShortToast(text);
                break;
            }
            case StatusConstants.WHAT_FAIL_ERROR: {
                String text = "新apk已合成失败";
                showShortToast(text);
                break;
            }
            case StatusConstants.WHAT_FAIL_GET_SOURCE: {
                String text = "无法获取packageName为" + Constants.TEST_PACKAGENAME
                        + "的源apk文件，只能整包更新了！";
                showShortToast(text);
                break;
            }
        }
    }
    private void showShortToast(final String text) {

        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    static {
        System.loadLibrary("ApkPatchLibrary");
    }
}
