package com.haylion.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.haylion.android.R;
import com.haylion.android.constract.PreSignContract;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.bean.OrderDetailBean;
import com.haylion.android.data.bean.PhoneBean;
import com.haylion.android.rxutil.RxBeepTool;
import com.haylion.android.scaner.CameraManager;
import com.haylion.android.scaner.OnRxScanerListener;
import com.haylion.android.scaner.PlanarYUVLuminanceSource;
import com.haylion.android.scaner.decoding.InactivityTimer;
import com.haylion.android.utils.RxAnimationTool;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * 扫描二维码
 */
public class ScannerCodeActivity extends BaseActivity<PreSignContract.Presenter> implements PreSignContract.View {

    @BindView(R.id.capture_preview)
    SurfaceView capturePreview;
    @BindView(R.id.capture_scan_line)
    ImageView captureScanLine;
    @BindView(R.id.capture_crop_layout)
    RelativeLayout captureCropLayout;
    @BindView(R.id.tv_scanner_content)
    TextView tvScannerContent;
    @BindView(R.id.iv_back)
    TextView ivBack;
    @BindView(R.id.capture_constraint_layout)
    ConstraintLayout captureConstraintLayout;

    public static void start(Activity context,OnRxScanerListener listener) {
        mScanerListener = listener;
        Intent intent = new Intent(context, ScannerCodeActivity.class);
        context.startActivityForResult(intent, 317);
    }


    /**
     * 扫描结果监听
     */
    private static OnRxScanerListener mScanerListener;

    private InactivityTimer inactivityTimer;

    /**
     * 扫描处理
     */
    private CaptureActivityHandler handler;


    /**
     * 扫描边界的宽度
     */
    private int mCropWidth = 0;

    /**
     * 扫描边界的高度
     */
    private int mCropHeight = 0;


    /**
     * 扫描成功后是否震动
     */
    private boolean vibrate = true;

    private Handler mHandler = new Handler();
    private int time = 61;

    @Override
    public void showOrderInfo(OrderDetailBean list) {

    }

    @Override
    public void showServicePhoneNum(PhoneBean bean) {

    }

    //枚举
    private enum State {
        //预览
        PREVIEW,
        //成功
        SUCCESS,
        //完成
        DONE
    }

    State state;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_code);
        ButterKnife.bind(this);
        state = State.SUCCESS;
        initDecode();
        initScannerAnimation();
        //初始化 CameraManager
        CameraManager.init(this);
        inactivityTimer = new InactivityTimer(this);
        ivBack.setOnClickListener(v ->
                {
                    onBackPressed();
                }
        );
    }

    //初始化动画
    private void initScannerAnimation() {
        RxAnimationTool.ScaleUpDowm(captureScanLine);
    }

    private void initDecode() {
        multiFormatReader = new MultiFormatReader();

        // 解码的参数
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(2);
        // 可以解析的编码类型
        Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector<BarcodeFormat>();

            Vector<BarcodeFormat> PRODUCT_FORMATS = new Vector<BarcodeFormat>(5);
            PRODUCT_FORMATS.add(BarcodeFormat.UPC_A);
            PRODUCT_FORMATS.add(BarcodeFormat.UPC_E);
            PRODUCT_FORMATS.add(BarcodeFormat.EAN_13);
            PRODUCT_FORMATS.add(BarcodeFormat.EAN_8);
            // PRODUCT_FORMATS.add(BarcodeFormat.RSS14);
            Vector<BarcodeFormat> ONE_D_FORMATS = new Vector<BarcodeFormat>(PRODUCT_FORMATS.size() + 4);
            ONE_D_FORMATS.addAll(PRODUCT_FORMATS);
            ONE_D_FORMATS.add(BarcodeFormat.CODE_39);
            ONE_D_FORMATS.add(BarcodeFormat.CODE_93);
            ONE_D_FORMATS.add(BarcodeFormat.CODE_128);
            ONE_D_FORMATS.add(BarcodeFormat.ITF);
            Vector<BarcodeFormat> QR_CODE_FORMATS = new Vector<BarcodeFormat>(1);
            QR_CODE_FORMATS.add(BarcodeFormat.QR_CODE);
            Vector<BarcodeFormat> DATA_MATRIX_FORMATS = new Vector<BarcodeFormat>(1);
            DATA_MATRIX_FORMATS.add(BarcodeFormat.DATA_MATRIX);

            // 这里设置可扫描的类型，我这里选择了都支持
            decodeFormats.addAll(ONE_D_FORMATS);
            decodeFormats.addAll(QR_CODE_FORMATS);
            decodeFormats.addAll(DATA_MATRIX_FORMATS);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        multiFormatReader.setHints(hints);
    }

    static class CaptureActivityHandler extends Handler {
        // SoftReference<Activity> 也可以使用软应用 只有在内存不足的时候才会被回收
        private final WeakReference<ScannerCodeActivity> mActivity;

        DecodeThread decodeThread = null;

        public CaptureActivityHandler(ScannerCodeActivity activity) {
            mActivity = new WeakReference<>(activity);
            decodeThread = new DecodeThread(activity);
            decodeThread.start();
            CameraManager.get().startPreview();
            restartPreviewAndDecode();
        }

        @Override
        public void handleMessage(Message message) {
            if (mActivity.get() != null) {
                if (message.what == R.id.auto_focus) {
                    if (mActivity.get().state == State.PREVIEW) {
                        CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                    }
                } else if (message.what == R.id.restart_preview) {
                    restartPreviewAndDecode();
                } else if (message.what == R.id.decode_succeeded) {
                    mActivity.get().state = State.SUCCESS;
                    mActivity.get().handleDecode((Result) message.obj);// 解析成功，回调
                } else if (message.what == R.id.decode_failed) {
                    mActivity.get().state = State.PREVIEW;
                    CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                }
            }

        }

        public void quitSynchronously() {
            mActivity.get().state = State.DONE;
            decodeThread.interrupt();
            CameraManager.get().stopPreview();
            removeMessages(R.id.decode_succeeded);
            removeMessages(R.id.decode_failed);
            removeMessages(R.id.decode);
            removeMessages(R.id.auto_focus);
        }

        //更新二维码扫描状态
        private void restartPreviewAndDecode() {
            if (mActivity.get().state == State.SUCCESS) {
                mActivity.get().state = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);//自动聚焦
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    static class DecodeThread extends Thread {
        private final WeakReference<ScannerCodeActivity> mActivity;

        private final CountDownLatch handlerInitLatch;
        private Handler handler;

        DecodeThread(ScannerCodeActivity activity) {
            this.mActivity = new WeakReference<>(activity);
            handlerInitLatch = new CountDownLatch(1);
        }

        Handler getHandler() {
            try {
                handlerInitLatch.await();
            } catch (InterruptedException ie) {
                // continue?
            }
            return handler;
        }

        @Override
        public void run() {
            Looper.prepare();
            handler = new DecodeHandler(mActivity.get());
            handlerInitLatch.countDown();
            Looper.loop();
        }
    }

    //解码的Handler
    static class DecodeHandler extends Handler {
        private final WeakReference<ScannerCodeActivity> mActivity;

        public DecodeHandler(ScannerCodeActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            if (mActivity.get() != null) {
                if (message.what == R.id.decode) {
                    mActivity.get().decode((byte[]) message.obj, message.arg1, message.arg2);
                } else if (message.what == R.id.quit) {
                    Looper.myLooper().quit();
                }
            }
        }
    }

    private MultiFormatReader multiFormatReader;

    //扫描二维码部分，并解码
    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();
        Result rawResult = null;

        //modify here
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rotatedData[x * height + height - y - 1] = data[x + y * width];
            }
        }
        // Here we are swapping, that's the difference to #11
        int tmp = width;
        width = height;
        height = tmp;

        PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(rotatedData, width, height);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            rawResult = multiFormatReader.decodeWithState(bitmap);
        } catch (ReaderException e) {
            // continue
        } finally {
            multiFormatReader.reset();
        }

        if (rawResult != null) {
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.getText());
            Message message = Message.obtain(handler, R.id.decode_succeeded, rawResult);
            Bundle bundle = new Bundle();
            bundle.putParcelable("barcode_bitmap", source.renderCroppedGreyscaleBitmap());
            message.setData(bundle);
            //Log.d(TAG, "Sending decode succeeded message...");
            message.sendToTarget();
        } else {
            try {
                Message message = Message.obtain(handler, R.id.decode_failed);
                message.sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //解码扫描结果
    public void handleDecode(Result result) {
        inactivityTimer.onActivity();
        //扫描成功之后的振动与声音提示
        RxBeepTool.playBeep(ScannerCodeActivity.this, vibrate);

        String result1 = result.getText();
        Log.v("二维码/条形码 扫描结果", result1);
        if (!TextUtils.isEmpty(result1) && isInteger(result1)){
            toast(result1);
            if (mScanerListener != null){
                mScanerListener.onSuccess("From to Camera", result);
                finish();
            }
        }
        /*if (mScanerListener == null) {
            if (handler != null) {
                // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
//                handler.sendEmptyMessage(R.id.restart_preview);
            }
        } else {
            mScanerListener.onSuccess("From to Camera", result);
        }*/
    }

    //使用正则，（推荐）
    public boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SurfaceHolder surfaceHolder = capturePreview.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initCamera(holder);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    //初始化摄像机
    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            Point point = CameraManager.get().getCameraResolution();
            AtomicInteger width = new AtomicInteger(point.y);
            AtomicInteger height = new AtomicInteger(point.x);
            int cropWidth = captureCropLayout.getWidth() * width.get() / captureConstraintLayout.getWidth();//左边控件时需要扫描的部分，右边是整体布局
            int cropHeight = captureCropLayout.getHeight() * height.get() / captureConstraintLayout.getHeight();//左边控件时需要扫描的部分，右边是整体布局
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
        } catch (IOException | RuntimeException ioe) {
            return;
        }
        //初始化完摄像头再打开数据接收器，用于接收Handler
        if (handler == null) {
            handler = new CaptureActivityHandler(this);
        }
    }

    public int getCropWidth() {
        return mCropWidth;
    }

    //赋值，扫描部分的宽度
    public void setCropWidth(int cropWidth) {
        mCropWidth = cropWidth;
        CameraManager.FRAME_WIDTH = mCropWidth;
    }

    public int getCropHeight() {
        return mCropHeight;
    }

    //赋值，扫描部分的高度
    public void setCropHeight(int cropHeight) {
        this.mCropHeight = cropHeight;
        CameraManager.FRAME_HEIGHT = mCropHeight;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        mScanerListener = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
