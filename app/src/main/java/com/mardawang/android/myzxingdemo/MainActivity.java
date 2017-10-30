package com.mardawang.android.myzxingdemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.google.zxing.activity.CaptureActivity;
import com.google.zxing.common.BitmapUtils;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtn1;
    private EditText mEt_content;
    private Button mBtn2;
    private ImageView mImage;
    private final static int REQ_CODE = 1028;
    private Context mContext;
    private TextView mTvResult;
    private ImageView mImageCallback;
    private long firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mContext = this;
    }

    private void initView() {
        mBtn1 = (Button) findViewById(R.id.btn1);

        mBtn1.setOnClickListener(this);
        mEt_content = (EditText) findViewById(R.id.et_content);
        mBtn2 = (Button) findViewById(R.id.btn2);
        mBtn2.setOnClickListener(this);
        mImage = (ImageView) findViewById(R.id.image);
        mImage.setOnClickListener(this);
        mTvResult = (TextView) findViewById(R.id.tv_result);
        mTvResult.setOnClickListener(this);
        mImageCallback = (ImageView) findViewById(R.id.image_callback);
        mImageCallback.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
//                startActivity(new Intent(MainActivity.this, CaptureActivity.class));
                Intent intent = new Intent(mContext, CaptureActivity.class);
                startActivityForResult(intent, REQ_CODE);
                break;
            case R.id.btn2:
                String content = mEt_content.getText().toString().trim();
                if(content==null){
                    return;
                }
                mImage.setVisibility(View.VISIBLE);
                //隐藏扫码结果view
                mImageCallback.setVisibility(View.GONE);
                mTvResult.setVisibility(View.GONE);

                Bitmap bitmap = null;
                try {
                    bitmap = BitmapUtils.create2DCode(content);//根据内容生成二维码
                    mTvResult.setVisibility(View.GONE);
                    mImage.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE && data != null) {
            mImage.setVisibility(View.GONE);
            mTvResult.setVisibility(View.VISIBLE);
            mImageCallback.setVisibility(View.VISIBLE);

            String result = data.getStringExtra(CaptureActivity.SCAN_QRCODE_RESULT);
            Bitmap bitmap = data.getParcelableExtra(CaptureActivity.SCAN_QRCODE_BITMAP);

            mTvResult.setText("扫码结果：" + result);
            showToast(result);
            if (bitmap != null) {
                mImageCallback.setImageBitmap(bitmap);//现实扫码图片
            }
        }


    }

    private void showToast(String msg) {
        String regex = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
        if (Pattern.matches(regex, msg)) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(msg);
            intent.setData(content_url);
            startActivity(intent);
        } else {
            Toast.makeText(mContext, "扫码结果：" + msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {     //KEYCODE_BACK：回退键
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(MainActivity.this, "再按就退出了哦", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
