package com.dhht.arcsoftlib.util;

// ┏┓　　　┏┓
// ┏┛┻━━━┛┻┓
// ┃　　　　　　　┃ 　
// ┃　　　━　　　┃
// ┃　┳┛　┗┳　┃
// ┃　　　　　　　┃
// ┃　　　┻　　　┃
// ┃　　　　　　　┃
// ┗━┓　　　┏━┛
// ┃　　　┃ 神兽保佑　　　　　　　　
// ┃　　　┃ 代码无BUG！
// ┃　　　┗━━━┓
// ┃　　　　　　　┣┓
// ┃　　　　　　　┏┛
// ┗┓┓┏━┳┓┏┛
// ┃┫┫　┃┫┫
// ┗┻┛　┗┻┛

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;

import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facetracking.AFT_FSDKFace;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * CreateDate：2018/10/23
 * Creator： VNBear
 * Description:
 **/
public class ArcUtils {

    /**
     * @param ftRect                   FT人脸框
     * @param previewWidth             相机预览的宽度
     * @param previewHeight            相机预览高度
     * @param canvasWidth              画布的宽度
     * @param canvasHeight             画布的高度
     * @param cameraDisplayOrientation 相机预览方向
     * @param cameraId                 相机ID
     * @return 调整后的需要被绘制到View上的rect
     */
    public static Rect adjustRect(Rect ftRect, int previewWidth, int previewHeight, int canvasWidth, int canvasHeight, int cameraDisplayOrientation, int cameraId, boolean isMirror) {
        if (ftRect == null) {
            return null;
        }
        Rect rect = new Rect(ftRect);
        if (canvasWidth < canvasHeight) {
            int t = previewHeight;
            previewHeight = previewWidth;
            previewWidth = t;
        }
        float horizontalRatio;
        float verticalRatio;
        if (cameraDisplayOrientation == 0 || cameraDisplayOrientation == 180) {
            horizontalRatio = (float) canvasWidth / (float) previewWidth;
            verticalRatio = (float) canvasHeight / (float) previewHeight;
        } else {
            horizontalRatio = (float) canvasHeight / (float) previewHeight;
            verticalRatio = (float) canvasWidth / (float) previewWidth;
        }
        rect.left *= horizontalRatio;
        rect.right *= horizontalRatio;
        rect.top *= verticalRatio;
        rect.bottom *= verticalRatio;
        Rect newRect = new Rect();
        switch (cameraDisplayOrientation) {
            case 0:
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    newRect.left = canvasWidth - rect.right;
                    newRect.right = canvasWidth - rect.left;
                } else {
                    newRect.left = rect.left;
                    newRect.right = rect.right;
                }
                newRect.top = rect.top;
                newRect.bottom = rect.bottom;
                break;
            case 90:
                newRect.right = canvasWidth - rect.top;
                newRect.left = canvasWidth - rect.bottom;
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    newRect.top = canvasHeight - rect.right;
                    newRect.bottom = canvasHeight - rect.left;
                } else {
                    newRect.top = rect.left;
                    newRect.bottom = rect.right;
                }
                break;
            case 180:
                newRect.top = canvasHeight - rect.bottom;
                newRect.bottom = canvasHeight - rect.top;
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    newRect.left = rect.left;
                    newRect.right = rect.right;
                } else {
                    newRect.left = canvasWidth - rect.right;
                    newRect.right = canvasWidth - rect.left;
                }
                break;
            case 270:
                newRect.left = rect.top;
                newRect.right = rect.bottom;
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    newRect.top = rect.left;
                    newRect.bottom = rect.right;
                } else {
                    newRect.top = canvasHeight - rect.right;
                    newRect.bottom = canvasHeight - rect.left;
                }
                break;
            default:
                break;
        }
        if (isMirror) {
            int left = newRect.left;
            int right = newRect.right;
            newRect.left = canvasWidth - right;
            newRect.right = canvasWidth - left;
        }
        return newRect;
    }

    /**
     * 绘制人脸框
     *
     * @param canvas
     * @param rect
     * @param color
     * @param faceRectThickness
     */
    public static void drawFaceRect(Canvas canvas, Rect rect, int color, int faceRectThickness) {
        if (canvas == null || rect == null) {
            return;
        }
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(faceRectThickness);
        paint.setColor(color);
        Path mPath = new Path();
        mPath.moveTo(rect.left, rect.top + rect.height() / 4);
        mPath.lineTo(rect.left, rect.top);
        mPath.lineTo(rect.left + rect.width() / 4, rect.top);
        mPath.moveTo(rect.right - rect.width() / 4, rect.top);
        mPath.lineTo(rect.right, rect.top);
        mPath.lineTo(rect.right, rect.top + rect.height() / 4);
        mPath.moveTo(rect.right, rect.bottom - rect.height() / 4);
        mPath.lineTo(rect.right, rect.bottom);
        mPath.lineTo(rect.right - rect.width() / 4, rect.bottom);
        mPath.moveTo(rect.left + rect.width() / 4, rect.bottom);
        mPath.lineTo(rect.left, rect.bottom);
        mPath.lineTo(rect.left, rect.bottom - rect.height() / 4);
        canvas.drawPath(mPath, paint);
    }

    /**
     * 将bitmap转成NV21格式数据
     *
     * @param inputWidth
     * @param inputHeight
     * @param scaled
     * @return
     */
    public static byte[] bitmapToNV21(int inputWidth, int inputHeight, Bitmap scaled) {
        int[] argb = new int[inputWidth * inputHeight];
        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);
        byte[] yuv = new byte[inputWidth * inputHeight * 3 / 2];
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);
        return yuv;
    }

    private static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        int frameSize = width * height;
        int yIndex = 0;
        int uvIndex = frameSize;
        int R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff);

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));


                if (j % 2 == 0 && index % 2 == 0 && uvIndex < yuv420sp.length - 2) {
                    yuv420sp[uvIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                }


                index++;
            }
        }
    }

    /**
     * 查找集合里最大的人脸位置
     *
     * @param fdFaceList
     * @return
     */
    public static int findFDMaxAreaFace(List<AFD_FSDKFace> fdFaceList) {
        if (fdFaceList.size() == 0) {
            return -1;
        }
        int index = 0;
        int maxArea = 0;
        int area;
        for (int i = 0; i < fdFaceList.size(); i++) {
            area = fdFaceList.get(i).getRect().width() * fdFaceList.get(i).getRect().height();
            if (area > maxArea) {
                maxArea = area;
                index = i;
            }
        }
        return index;
    }

    /**
     * 查找集合里最大的人脸位置
     *
     * @param ftFaceList
     * @return
     */
    public static int findFTMaxAreaFace(List<AFT_FSDKFace> ftFaceList) {
        if (ftFaceList.size() == 0) {
            return -1;
        }
        int index = 0;
        int maxArea = 0;
        int area;
        for (int i = 0; i < ftFaceList.size(); i++) {
            area = ftFaceList.get(i).getRect().width() * ftFaceList.get(i).getRect().height();
            if (area > maxArea) {
                maxArea = area;
                index = i;
            }
        }
        return index;
    }

    /**
     * 截取bitmap中指定区域的图片
     *
     * @param bitmap
     * @param rect
     * @return
     */
    public static Bitmap imageCrop(Bitmap bitmap, Rect rect) {
        return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height(), null, false);
    }


    public static Bitmap cropFace(byte[] faceData, Rect rect, int imageWidth, int imageHeight, int angle) {
        Bitmap bitmap;
        YuvImage yuv = new YuvImage(faceData, ImageFormat.NV21, imageWidth, imageHeight, null);
        ByteArrayOutputStream ops = new ByteArrayOutputStream();
        try {
            yuv.compressToJpeg(rect, 100, ops);
            byte[] bytes = ops.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            bitmap = null;
            return bitmap;
        } finally {
            try {
                ops.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rotateBitmap(bitmap, -angle);
    }


    /**
     * 旋转bitmap
     *
     * @param bitmap
     * @param angle
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    /**
     * 深度复制
     *
     * @param src
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }
}
