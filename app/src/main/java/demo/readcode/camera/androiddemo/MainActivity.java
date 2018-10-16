package demo.readcode.camera.androiddemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.zxing.WriterException;
import com.yanzhenjie.permission.AndPermission;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.encode.CodeCreator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_CODE_CODE = 1;
    @BindView(R.id.tv_scanResult)
    AppCompatTextView tvScanResult;
    @BindView(R.id.btn_scanCode)
    AppCompatButton btnScanCode;
    @BindView(R.id.edit_code)
    AppCompatEditText editCode;
    @BindView(R.id.btn_createCode)
    AppCompatButton btnCreateCode;
    @BindView(R.id.iv_myCode)
    AppCompatImageView ivMyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        AndPermission.with(this).runtime().permission(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .onDenied(permission->{
                    for (int i=0;i<permission.size();i++)
                        Log.d("Permission",permission.get(i).trim());
                }).start();

        btnScanCode.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
            ZxingConfig config = new ZxingConfig();
            config.setPlayBeep(true);//是否播放扫描声音 默认为true
            config.setShake(true);//是否震动  默认为true
            config.setDecodeBarCode(false);//是否扫描条形码 默认为true
            config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为淡蓝色
            config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
            config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
            intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
            startActivityForResult(intent, REQUEST_CODE_CODE);
        });
        btnCreateCode.setOnClickListener(v -> {
            String strCode = editCode.getText().toString().trim();
            if (!strCode.isEmpty()) {
                Bitmap bitmap;
                try {
                    Bitmap logo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    bitmap = CodeCreator.createQRCode(strCode, 200, 200, logo);
                    Glide.with(this).load(bitmap).into(ivMyCode);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            } else {
                Toasty.error(this, "二维码源字符串不能为空！").show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String resultcode = data.getStringExtra(Constant.CODED_CONTENT);
                tvScanResult.setText(resultcode);
            }
        }

    }

}
