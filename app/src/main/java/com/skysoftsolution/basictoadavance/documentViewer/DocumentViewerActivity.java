package com.skysoftsolution.basictoadavance.documentViewer;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.skysoftsolution.basictoadavance.R;
import com.skysoftsolution.basictoadavance.dashBoardScreens.DashBoardScreen;
import com.skysoftsolution.basictoadavance.databinding.ActivityDocumentViewerBinding;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
public class DocumentViewerActivity extends AppCompatActivity {
    private ActivityDocumentViewerBinding binding;
    public static final String EXTRA_PDF_URI = "extra_pdf_uri";
    public static final String EXTRA_PDF_STRING = "extra_pdf_string";

    private ParcelFileDescriptor pfd;
    private PdfRenderer renderer;
    private PdfRenderer.Page currentPage;
    private int pageIndex = 0;
    private int pageCount = 0;

    private Uri uri;
    private String input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDocumentViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= 35) {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
            ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (view, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                view.setPadding(0, 100, 0, 0);
                return WindowInsetsCompat.CONSUMED;
            });
        } else {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Document Reader");
        }

        // Resolve input
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            uri = bundle.getParcelable(EXTRA_PDF_URI);
            input = bundle.getString(EXTRA_PDF_STRING);
        }
        if (uri == null && input == null) {
            Intent i = getIntent();
            if (Intent.ACTION_VIEW.equals(i.getAction()) || Intent.ACTION_SEND.equals(i.getAction())) {
                uri = i.getData();
                if (uri == null && i.getClipData() != null && i.getClipData().getItemCount() > 0) {
                    uri = i.getClipData().getItemAt(0).getUri();
                }
            }
        }

        if (uri != null) {
            openFromUri(uri);
        } else if (input != null) {
            if (isHttp(input)) {
                // New reliable path: direct download -> open from file path
                downloadAndOpen(input);
            } else if (isAbsoluteFilePath(input)) {
                openFromFilePath(input);
            } else {
                toast("Unsupported input");
                finish();
                return;
            }
        } else {
            toast("No file provided");
            finish();
            return;
        }

        binding.btnPrev.setOnClickListener(v -> { if (pageIndex > 0) renderPage(pageIndex - 1); });
        binding.btnNext.setOnClickListener(v -> { if (pageIndex < pageCount - 1) renderPage(pageIndex + 1); });

        binding.seekPage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) renderPage(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DocumentViewerActivity.this, DashBoardScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /* ================= helpers ================= */

    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_LONG).show(); }
    private boolean isHttp(String s) { return s.startsWith("http://") || s.startsWith("https://"); }
    private boolean isAbsoluteFilePath(String s) { return s.startsWith("/") || s.startsWith("file:/"); }
    private boolean isPdfMime(String mime) { return mime != null && mime.equalsIgnoreCase("application/pdf"); }
    private boolean isImageMime(String mime) { return mime != null && mime.startsWith("image/"); }

    private String guessMimeFromName(String name) {
        if (name == null) return null;
        String ext = android.webkit.MimeTypeMap.getFileExtensionFromUrl(name);
        if (ext == null || ext.isEmpty()) {
            int dot = name.lastIndexOf('.');
            if (dot != -1 && dot < name.length() - 1) ext = name.substring(dot + 1);
        }
        if (ext == null || ext.isEmpty()) return null;
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase(Locale.US));
        if (mime == null && "pdf".equalsIgnoreCase(ext)) mime = "application/pdf";
        return mime;
    }

    private String getMimeFromUri(Uri uri) {
        String type = null;
        try { type = getContentResolver().getType(uri); } catch (Exception ignored) {}
        if (type == null) type = guessMimeFromName(uri.toString());
        return type;
    }

    private boolean isPdfHeader(InputStream is) throws IOException {
        byte[] head = new byte[5];
        int n = is.read(head);
        if (n < 5) return false;
        return head[0] == 0x25 && head[1] == 0x50 && head[2] == 0x44 && head[3] == 0x46 && head[4] == 0x2D; // %PDF-
    }

    /* ================= open: Uri ================= */

    private void openFromUri(Uri uri) {
        binding.tvTitle.setText(getDisplayName(uri));

        String mime = getMimeFromUri(uri);
        if (isImageMime(mime)) { openImageFromUri(uri); return; }
        if (isPdfMime(mime))   { openPdfFromUri(uri);   return; }

        // Sniff unknown
        try (InputStream is = getContentResolver().openInputStream(uri)) {
            if (is != null && isPdfHeader(is)) { openPdfFromUri(uri); return; }
        } catch (Exception ignored) {}

        // Try image bounds
        try (InputStream is2 = getContentResolver().openInputStream(uri)) {
            if (is2 != null) {
                BitmapFactory.Options probe = new BitmapFactory.Options();
                probe.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is2, null, probe);
                if (probe.outWidth > 0 && probe.outHeight > 0) { openImageFromUri(uri); return; }
            }
        } catch (Exception ignored) {}

        toast("Unsupported file type");
        finish();
    }

    private void openPdfFromUri(Uri uri) {
        try {
            closeRendererIfAny();
            pfd = getContentResolver().openFileDescriptor(uri, "r");
            if (pfd == null) throw new IOException("Descriptor null");
            renderer = new PdfRenderer(pfd);
            afterRendererReady();
        } catch (Exception e) {
            toast("Open PDF failed: " + e.getMessage());
            finish();
        }
    }

    /* ================= open: absolute file path ================= */

    private void openFromFilePath(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) { toast("File not found:\n" + path); finish(); return; }
            binding.tvTitle.setText(file.getName());

            String mime = guessMimeFromName(file.getName());
            if (isImageMime(mime)) {
                Bitmap bmp = decodeSampledBitmapFromFile(path);
                if (bmp == null) throw new IOException("Failed to decode image");
                showImage(bmp);
                return;
            }

            // sniff PDF header
            try (FileInputStream fis = new FileInputStream(file)) {
                if (isPdfHeader(fis)) {
                    closeRendererIfAny();
                    pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                    renderer = new PdfRenderer(pfd);
                    afterRendererReady();
                    return;
                }
            }

            // try image anyway
            Bitmap bmp2 = decodeSampledBitmapFromFile(path);
            if (bmp2 != null) { showImage(bmp2); return; }

            toast("Unsupported file type");
            finish();
        } catch (Exception e) {
            toast("Open failed: " + e.getMessage());
            finish();
        }
    }

    /* ================= reliable download (no DownloadManager) ================= */

    private void downloadAndOpen(String url) {
        binding.tvTitle.setText("Downloading…");
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL u = new URL(url);
                conn = (HttpURLConnection) u.openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(60000);
                conn.setInstanceFollowRedirects(true);
                conn.connect();

                int code = conn.getResponseCode();
                if (code / 100 != 2) throw new IOException("HTTP " + code);

                // Best-effort filename
                String name = u.getPath();
                int idx = name.lastIndexOf('/');
                name = (idx >= 0 && idx < name.length() - 1) ? name.substring(idx + 1) : "download.bin";
                if (!name.contains(".")) {
                    // try by content-type
                    String ct = conn.getContentType();
                    if (ct != null && ct.contains("pdf")) name = name + ".pdf";
                }

                File out = new File(getCacheDir(), name);
                try (InputStream in = conn.getInputStream();
                     java.io.BufferedInputStream bin = new java.io.BufferedInputStream(in);
                     java.io.FileOutputStream fos = new java.io.FileOutputStream(out)) {
                    byte[] buf = new byte[8192];
                    int n;
                    while ((n = bin.read(buf)) != -1) fos.write(buf, 0, n);
                }

                String absPath = out.getAbsolutePath();
                runOnUiThread(() -> openFromFilePath(absPath));

            } catch (Exception e) {
                runOnUiThread(() -> { toast("Download failed: " + e.getMessage()); finish(); });
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    /* ================= after renderer ready ================= */

    private void afterRendererReady() {
        pageCount = renderer.getPageCount();
        binding.seekPage.setMax(Math.max(0, pageCount - 1));
        // Post to ensure view has a size; still uses screen fallback if 0
        binding.imageView.post(() -> renderPage(0));

        binding.seekPage.setEnabled(true);
        binding.btnPrev.setEnabled(true);
        binding.btnNext.setEnabled(true);
    }

    private void renderPage(int index) {
        try {
            if (renderer == null) return;
            if (index < 0 || index >= pageCount) return;

            if (currentPage != null) currentPage.close();
            currentPage = renderer.openPage(index);
            pageIndex = index;

            // Fit-to-width rendering to avoid giant bitmaps / OOM
            int viewW = binding.imageView.getWidth();
            if (viewW <= 0) viewW = getResources().getDisplayMetrics().widthPixels;
            float scale = Math.max(1f, viewW / (float) currentPage.getWidth());

            int bmpW = Math.max(1, (int) (currentPage.getWidth() * scale));
            int bmpH = Math.max(1, (int) (currentPage.getHeight() * scale));
            Bitmap bitmap = Bitmap.createBitmap(bmpW, bmpH, Bitmap.Config.ARGB_8888);
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            binding.imageView.setImageBitmap(bitmap);
            binding.imageView.invalidate();
            binding.imageView.resetToFit();
            updatePageUi();
        } catch (Exception e) {
            toast("Render error: " + e.getMessage());
        }
    }

    private void updatePageUi() {
        binding.tvPage.setText((pageIndex + 1) + "/" + pageCount);
        binding.seekPage.setProgress(pageIndex);
        binding.btnPrev.setEnabled(pageIndex > 0);
        binding.btnNext.setEnabled(pageIndex < pageCount - 1);
    }

    /* ================= image handling ================= */

    private void openImageFromUri(Uri uri) {
        try (InputStream is = getContentResolver().openInputStream(uri)) {
            if (is == null) throw new IOException("Null stream");
            Bitmap bmp = decodeSampledBitmapFromStream(is);
            if (bmp == null) throw new IOException("Failed to decode image");
            showImage(bmp);
        } catch (Exception e) {
            toast("Image open failed: " + e.getMessage());
            finish();
        }
    }

    private void showImage(Bitmap bmp) {
        closeRendererIfAny(); // switch away from PDF mode if any

        binding.imageView.setImageBitmap(bmp);
        binding.imageView.invalidate();
        binding.imageView.resetToFit();

        pageIndex = 0; pageCount = 1;
        binding.tvPage.setText("1/1");
        binding.seekPage.setMax(0);
        binding.seekPage.setEnabled(false);
        binding.btnPrev.setEnabled(false);
        binding.btnNext.setEnabled(false);
    }

    /* ---- sampled decode utilities (avoid OOM on large images) ---- */

    private Bitmap decodeSampledBitmapFromFile(String path) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int reqW = Math.max(dm.widthPixels, 720);
        int reqH = Math.max(dm.heightPixels, 1280);

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, o);

        o.inSampleSize = calcInSampleSize(o, reqW, reqH);
        o.inJustDecodeBounds = false;
        o.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(path, o);
    }

    private Bitmap decodeSampledBitmapFromStream(InputStream is) throws IOException {
        byte[] data = readAllBytes(is);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int reqW = Math.max(dm.widthPixels, 720);
        int reqH = Math.max(dm.heightPixels, 1280);

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, o);

        o.inSampleSize = calcInSampleSize(o, reqW, reqH);
        o.inJustDecodeBounds = false;
        o.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeByteArray(data, 0, data.length, o);
    }

    private byte[] readAllBytes(InputStream is) throws IOException {
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = is.read(buf)) != -1) bos.write(buf, 0, n);
        return bos.toByteArray();
    }

    private int calcInSampleSize(BitmapFactory.Options o, int reqW, int reqH) {
        int h = o.outHeight, w = o.outWidth, inSample = 1;
        if (h > reqH || w > reqW) {
            int halfH = h / 2, halfW = w / 2;
            while ((halfH / inSample) >= reqH && (halfW / inSample) >= reqW) inSample *= 2;
        }
        return Math.max(1, inSample);
    }

    /* ================= misc ================= */

    private String getDisplayName(Uri uri) {
        String name = null;
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null) {
                int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx != -1 && cursor.moveToFirst()) name = cursor.getString(idx);
            }
        } catch (Exception ignored) {}
        if (name == null) name = uri.getLastPathSegment();
        return Objects.requireNonNullElse(name, "Document");
    }

    private void closeRendererIfAny() {
        try {
            if (currentPage != null) { currentPage.close(); currentPage = null; }
            if (renderer != null)    { renderer.close();    renderer = null; }
            if (pfd != null)         { pfd.close();         pfd = null; }
        } catch (IOException ignored) {}
    }

    @Override
    protected void onDestroy() {
        closeRendererIfAny();
        super.onDestroy();
    }
}

