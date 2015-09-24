package com.lr.ghp.appupdata.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lr.ghp.appupdata.R;
import com.lr.ghp.appupdata.constants.Constants;
import com.lr.ghp.appupdata.util.ApkUtils;
import com.lr.ghp.appupdata.util.PatchApkTask;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{
    @InjectView(R.id.start_btn)Button start_btn;
    @InjectView(R.id.resultTxt)TextView resultTxt;
    private Context mContext=null;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mContext=getApplicationContext();
        initView();
    }

    private void initView(){
        start_btn.setOnClickListener(this);
        mProgressDialog=new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("doing..");
        mProgressDialog.setCancelable(false);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_btn:
                File patchFile = new File(Constants.PATCH_PATH);
                patchFile.setReadable(true);
                patchFile.setWritable(true);
                if (!ApkUtils.isInstalled(mContext, Constants.TEST_PACKAGENAME)) {
                    Toast.makeText(mContext, getString(R.string.demo_info1),
                            Toast.LENGTH_LONG).show();
                }
                else if (!patchFile.exists()) {
                    Toast.makeText(mContext, getString(R.string.demo_info2),
                            Toast.LENGTH_LONG).show();
                }
                else {
                    PatchApkTask patchApkTask=new PatchApkTask(MainActivity.this,mProgressDialog,resultTxt);
                    patchApkTask.execute();
                }
                break;
            default:
                break;
        }
    }
    static {
        System.loadLibrary("ApkPatchLibrary");
    }
}
