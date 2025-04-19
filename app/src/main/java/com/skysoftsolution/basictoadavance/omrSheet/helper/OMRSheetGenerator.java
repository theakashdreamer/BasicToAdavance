package com.skysoftsolution.basictoadavance.omrSheet.helper;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class OMRSheetGenerator {
    private Context context;

    public OMRSheetGenerator(Context context) {
        this.context = context;
    }

    public void generateOMRSheet() {
        int pageWidth = 1000;
        int pageHeight = 1400;
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        // Header
        paint.setTextSize(36);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Innovative India", pageWidth / 2, 80, paint);
        paint.setTextSize(24);
        canvas.drawText("J.C. Road, Bengaluru - 560 002", pageWidth / 2, 110, paint);

        // Contact Info
        paint.setTextSize(18);
        canvas.drawText("Competitive Exam Books | School | College Books | Children's Books", pageWidth / 2, 140, paint);
        canvas.drawText("Mob: 98865 81618", pageWidth / 2, 170, paint);

        // Draw Instruction Box
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(50, 200, 950, 240, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(16);
        canvas.drawText("THIS IS A SPECIMEN OF OMR ANSWER SHEET THE LAYOUT MAY VARY AS PER ACTUAL ANSWER SHEET IN THE EXAM", pageWidth / 2, 230, paint);

        // Draw Roll Number Section
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(50, 260, 300, 500, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(18);
        canvas.drawText("ROLL NO.", 130, 280, paint);

        drawOMRCircles(canvas, 70, 300, 10, 5, 30, paint);

        // Draw Candidate Info Box
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(320, 260, 950, 360, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("Name: __________________________", 340, 300, paint);
        canvas.drawText("Batch: __________________________", 340, 330, paint);
        canvas.drawText("Mobile No.: __________________________", 340, 360, paint);
        canvas.drawText("Date: ____/____/____", 720, 360, paint);

        // Draw Candidate & Invigilator Sign Box
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(320, 380, 600, 430, paint);
        canvas.drawText("Candidate Sign", 350, 420, paint);
        canvas.drawRect(620, 380, 950, 430, paint);
        canvas.drawText("Invigilator Sign", 650, 420, paint);

        // Draw OMR Answer Bubbles
        drawOMRAnswerSheet(canvas, 50, 500, paint);

        // Finish and save the PDF
        pdfDocument.finishPage(page);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "OMR_Sheet.pdf");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawOMRCircles(Canvas canvas, int startX, int startY, int rows, int columns, int spacing, Paint paint) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                canvas.drawCircle(startX + j * spacing, startY + i * spacing, 10, paint);
            }
        }
    }

    private void drawOMRAnswerSheet(Canvas canvas, int startX, int startY, Paint paint) {
        int cols = 4;  // A, B, C, D options
        int rows = 100; // 100 questions
        int cellWidth = 80;
        int cellHeight = 40;
        int sectionWidth = 400;

        for (int i = 0; i < rows; i++) {
            int x = startX + (i / 20) * sectionWidth;
            int y = startY + (i % 20) * cellHeight;
            canvas.drawText((i + 1) + ".", x - 20, y + 15, paint);
            for (int j = 0; j < cols; j++) {
                canvas.drawCircle(x + j * cellWidth, y, 15, paint);
            }
        }
    }
}




