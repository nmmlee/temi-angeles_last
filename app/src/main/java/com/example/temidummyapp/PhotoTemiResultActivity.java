package com.example.temidummyapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.ArrayList;

public class PhotoTemiResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phototemi_result);

        ImageView resultImage = findViewById(R.id.result_image);
        Button homeButton = findViewById(R.id.home_button);

        ArrayList<String> selectedImageUris = getIntent().getStringArrayListExtra("selected_images");

        if (selectedImageUris != null && selectedImageUris.size() == 2) {
            try {
                // 1. 템플릿 이미지를 로드하고, 수정 가능한 복사본을 만듭니다.
                Bitmap templateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo_template_black);
                Bitmap mutableBitmap = templateBitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(mutableBitmap);

                // 2. 사용자가 제공한 정확한 픽셀 값으로 사진이 들어갈 영역을 정의합니다.
                final int frameWidth = 250;
                final int frameHeight = 188;

                // 첫 번째 이미지 영역: (24, 68) 위치, 250x188 크기
                final int frame1_left = 24;
                final int frame1_top = 68;
                Rect destRect1 = new Rect(frame1_left, frame1_top, frame1_left + frameWidth, frame1_top + frameHeight);

                // 두 번째 이미지 영역: 첫 번째 이미지 하단에서 10px 아래
                final int frame2_left = 24;
                final int frame2_top = destRect1.bottom + 10;
                Rect destRect2 = new Rect(frame2_left, frame2_top, frame2_left + frameWidth, frame2_top + frameHeight);

                // 3. 선택된 사진들을 로드하여 캔버스에 그립니다.
                Bitmap photo1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(selectedImageUris.get(0)));
                Bitmap photo2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(selectedImageUris.get(1)));

                drawBitmapToCanvas(canvas, photo1, destRect1);
                drawBitmapToCanvas(canvas, photo2, destRect2);

                resultImage.setImageBitmap(mutableBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(PhotoTemiResultActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    /**
     * 지정된 사각 영역(destination)에 비트맵을 비율을 유지하며 꽉 채워 그립니다. (Center Crop)
     */
    private void drawBitmapToCanvas(Canvas canvas, Bitmap bitmap, Rect destination) {
        Matrix matrix = new Matrix();
        float scale;
        float dx = 0;
        float dy = 0;

        if (bitmap.getWidth() * destination.height() > destination.width() * bitmap.getHeight()) {
            scale = (float) destination.height() / (float) bitmap.getHeight();
            dx = (destination.width() - bitmap.getWidth() * scale) * 0.5f;
        } else {
            scale = (float) destination.width() / (float) bitmap.getWidth();
            dy = (destination.height() - bitmap.getHeight() * scale) * 0.5f;
        }

        matrix.setScale(scale, scale);
        matrix.postTranslate((int) (dx + 0.5f) + destination.left, (int) (dy + 0.5f) + destination.top);
        canvas.drawBitmap(bitmap, matrix, null);
    }
}
