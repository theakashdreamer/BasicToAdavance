package com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.mlkit.vision.common.InputImage;
import com.skysoftsolution.basictoadavance.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.camera2.CameraCharacteristics;
import android.media.AudioManager;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.skysoftsolution.basictoadavance.dashBoardScreens.DashBoardScreen;
import com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils.customview.CircleOverlayView;
import com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils.customview.OverlayView;
import com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils.env.BorderedText;
import com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils.env.ImageUtils;
import com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils.tflite.SimilarityClassifier;
import com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils.tflite.TFLiteObjectDetectionAPIModel;
import com.skysoftsolution.basictoadavance.newFaceWork.cameraUtils.tracking.MultiBoxTracker;
import com.skysoftsolution.basictoadavance.sharedpreference.SharedPreferenceData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import butterknife.ButterKnife;

public class DetectorActivity extends CameraActivity implements OnImageAvailableListener{
    private static final int TF_OD_API_INPUT_SIZE = 112;
    private static final boolean TF_OD_API_IS_QUANTIZED = false;
    private static final String TF_OD_API_MODEL_FILE = "mobile_face_net.tflite";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt";
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    private static final boolean MAINTAIN_ASPECT = false;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;
    private OverlayView trackingOverlay;
    private Integer sensorOrientation;
    private SimilarityClassifier detector;
    private long lastProcessingTimeMs;
    private Bitmap rgbFrameBitmap = null, croppedBitmap = null, cropCopyBitmap = null, portraitBmp = null, faceBmp = null;
    private boolean computingDetection = false, addPending = false;
    private long timestamp = 0;
    private Matrix frameToCropTransform, cropToFrameTransform;
    private MultiBoxTracker tracker;
    private BorderedText borderedText;
    private FaceDetector faceDetector;
    private float facedistancemain = Float.MAX_VALUE;
    private boolean profileCreation = false;
    private boolean isPhotoClicked = false;
    CircleOverlayView overlay;

    @Override
    public synchronized void onStart() {
        super.onStart();
    }

    @SuppressLint({"RestrictedApi", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Circle");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }
        try {
         //   overlay = findViewById(R.id.circleOverlay);
        } catch (Exception e) {
            e.printStackTrace();
        }
        facedistancemain = 0.78f;
        FaceDetectorOptions options = new FaceDetectorOptions.Builder().setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST).setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL).setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL).build();
        FaceDetector detector = FaceDetection.getClient(options);
        faceDetector = detector;

    }
    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);
        tracker = new MultiBoxTracker(this);
        try {
            detector = TFLiteObjectDetectionAPIModel.create(getAssets(), TF_OD_API_MODEL_FILE, TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE, TF_OD_API_IS_QUANTIZED);
        } catch (final IOException e) {
            e.printStackTrace();
            finish();
        }
        previewWidth = size.getWidth();
        previewHeight = size.getHeight();
        sensorOrientation = rotation - getScreenOrientation();
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);

        int targetW, targetH;
        if (sensorOrientation == 90 || sensorOrientation == 270) {
            targetH = previewWidth;
            targetW = previewHeight;
        } else {
            targetW = previewWidth;
            targetH = previewHeight;
        }

        int cropW = (int) (targetW / 2.0);
        int cropH = (int) (targetH / 2.0);
        croppedBitmap = Bitmap.createBitmap(cropW, cropH, Config.ARGB_8888);
        if (!isPhotoClicked) {
            portraitBmp = Bitmap.createBitmap(targetW, targetH, Config.ARGB_8888);
        }
        faceBmp = Bitmap.createBitmap(TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, Config.ARGB_8888);
        frameToCropTransform = ImageUtils.getTransformationMatrix(previewWidth, previewHeight, cropW, cropH, sensorOrientation, MAINTAIN_ASPECT);
        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);
        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(new OverlayView.DrawCallback() {
            @Override
            public void drawCallback(final Canvas canvas) {
                tracker.draw(canvas);
                if (isDebug()) {
                    tracker.drawDebug(canvas);
                }
            }
        });
        tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
    }
    private void onFacesDetected(long currTimestamp, List<Face> faces, boolean add) {
        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
        final Canvas canvas = new Canvas(cropCopyBitmap);
        final Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(2.0f);
        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
        switch (MODE) {
            case TF_OD_API:
                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                break;
        }
        final List<SimilarityClassifier.Recognition> mappedRecognitions = new LinkedList<SimilarityClassifier.Recognition>();
        int sourceW = rgbFrameBitmap.getWidth();
        int sourceH = rgbFrameBitmap.getHeight();
        int targetW = portraitBmp.getWidth();
        int targetH = portraitBmp.getHeight();
        Matrix transform = createTransform(sourceW, sourceH, targetW, targetH, sensorOrientation);
        final Canvas cv = new Canvas(portraitBmp);
        cv.drawBitmap(rgbFrameBitmap, transform, null);
        final Canvas cvFace = new Canvas(faceBmp);
        try {
            for (Face face : faces) {
                final RectF boundingBox = new RectF(face.getBoundingBox());
                String LeftEye = "", RightEye = "";
                if (face.getLeftEyeOpenProbability() != null) {
                    String LeftEyeNew = face.getLeftEyeOpenProbability().toString().replace(".", "_");
                    LeftEye = LeftEyeNew.split("_")[1];
                }

                if (face.getRightEyeOpenProbability() != null) {
                    String RightEyeNew = face.getRightEyeOpenProbability().toString().replace(".", "_");
                    RightEye = RightEyeNew.split("_")[1];
                }
                if(LeftEye!=null){
                    if (LeftEye.startsWith("0")) {
                        profileCreation = true;
                        Log.d("LeftEye", LeftEye + "");
                    }
                }
                if(RightEye!=null){
                    if (RightEye.startsWith("0")) {
                        profileCreation = true;
                        Log.d("RightEye", RightEye + "");
                    }
                }
                if (boundingBox != null) {
                    cropToFrameTransform.mapRect(boundingBox);
                    RectF faceBB = new RectF(boundingBox);
                    transform.mapRect(faceBB);
                    float sx = ((float) TF_OD_API_INPUT_SIZE) / faceBB.width();
                    float sy = ((float) TF_OD_API_INPUT_SIZE) / faceBB.height();
                    Matrix matrix = new Matrix();
                    matrix.postTranslate(-faceBB.left, -faceBB.top);
                    matrix.postScale(sx, sy);
                    cvFace.drawBitmap(portraitBmp, matrix, null);
                    String label = "";
                    Integer color = Color.YELLOW;
                    Object extra = null;
                    Bitmap crop = null;
                    if (add) {
                        crop = Bitmap.createBitmap(portraitBmp, (int) faceBB.left, (int) faceBB.top, (int) faceBB.width(), (int) faceBB.height());
                    }

                    final long startTime = SystemClock.uptimeMillis();
                    List<SimilarityClassifier.Recognition> resultsAux = detector.recognizeImage(faceBmp, add);
                    lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
                    if (resultsAux.size() > 0) {
                        SimilarityClassifier.Recognition result = resultsAux.get(0);
                        extra = result.getExtra();
                        if (profileCreation) {
                            float conf = result.getDistance();
                            if (conf < facedistancemain) {
                                label = result.getTitle().split("_")[0];
                                String y = "";
                                String name = "", name1 = "";
                                if (label != null && !label.equalsIgnoreCase("")) {
                                    y = "yes";
                                }
                                if (y.equalsIgnoreCase("yes")) {

                                } else {
                                    label = name;
                                }
                                if (result.getId().equals("0")) {
                                    color = Color.GREEN;
                                } else {
                                    color = Color.RED;
                                }
                            }
                        }
                    }

                    if (getCameraFacing() == CameraCharacteristics.LENS_FACING_FRONT) {
                        Matrix flip = new Matrix();
                        if (sensorOrientation == 90 || sensorOrientation == 270) {
                            flip.postScale(1, -1, previewWidth / 2.0f, previewHeight / 2.0f);
                        } else {
                            flip.postScale(-1, 1, previewWidth / 2.0f, previewHeight / 2.0f);
                        }
                        flip.mapRect(boundingBox);
                    }
                    final SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition("0", label, -1f, boundingBox);
                    result.setColor(color);
                    result.setLocation(boundingBox);
                    result.setExtra(extra);
                    result.setCrop(crop);
                    mappedRecognitions.add(result);
                }
            }

            updateResults(currTimestamp, mappedRecognitions);

        } catch (Exception e) {
            e.getMessage();
        }

    }
    private void updateResults(long currTimestamp, final List<SimilarityClassifier.Recognition> mappedRecognitions) {
        tracker.trackResults(mappedRecognitions, currTimestamp);
        trackingOverlay.postInvalidate();
        computingDetection = false;
        if (mappedRecognitions.size() > 0) {
            SimilarityClassifier.Recognition rec = mappedRecognitions.get(0);

            if (rec.getExtra() != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }
    }
    @Override
    protected void processImage() {
        ++timestamp;
        final long currTimestamp = timestamp;
        trackingOverlay.postInvalidate();
        if (computingDetection) {
            readyForNextImage();
            return;
        }
        computingDetection = true;
        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
        readyForNextImage();
        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap);
        }
        InputImage image = InputImage.fromBitmap(croppedBitmap, 0);
        faceDetector.process(image).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
            @Override
            public void onSuccess(List<Face> faces) {
                if (faces.size() == 0) {
                    profileCreation = false;
                    updateResults(currTimestamp, new LinkedList<>());
                    return;
                }
                runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        onFacesDetected(currTimestamp, faces, addPending);
                        addPending = false;
                    }
                });
            }
        });
    }
    @Override
    protected void setNumThreads(final int numThreads) {
        runInBackground(() -> detector.setNumThreads(numThreads));
    }
    private Matrix createTransform(final int srcWidth, final int srcHeight, final int dstWidth, final int dstHeight, final int applyRotation) {
        Matrix matrix = new Matrix();
        if (applyRotation != 0) {
            if (applyRotation % 90 != 0) {

            }
            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f);
            matrix.postRotate(applyRotation);
        }
        if (applyRotation != 0) {
            matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f);
        }
        return matrix;
    }
    private enum DetectorMode {
        TF_OD_API;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.tfe_od_camera_connection_fragment_tracking;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {

        return DESIRED_PREVIEW_SIZE;
    }

    @Override
    protected void setUseNNAPI(final boolean isChecked) {
        runInBackground(() -> detector.setUseNNAPI(isChecked));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DetectorActivity.this, DashBoardScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }
}