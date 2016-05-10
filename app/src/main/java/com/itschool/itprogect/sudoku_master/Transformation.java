package com.itschool.itprogect.sudoku_master;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Transformation {

    public Bitmap transform(Bitmap bitmap){
        //матрица полностью совпадающая с исходным изображением
        Mat original=new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, original);
        //бинарная матрица
        Mat prepared=prepare(original);
        //контур сетки судоку
        MatOfPoint contour=findBiggestContour(prepared);
        //сглаженный к прямоугольнику контур
        MatOfPoint2f polygon=aproxPolygon(contour);
        //размер выправленной матрицы
        int size = distance(polygon);
        //обрезанный по контуру оригинал
        Mat cut = applyMask(original, contour);
        //матрица с выправленной ориентацией
        Mat wrapped = wrapPerspective(size, orderPoints(polygon), cut);
        Mat result=prepare(wrapped);
        result=cleanLines(result);
        bitmap=Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(result, bitmap);
        return bitmap;
    }

    public boolean isEmpty(Bitmap bitmap){
        Mat mat=new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
        MatOfPoint contour=findBiggestContour(mat);
        if (contour==null || contour.rows()<mat.rows()/2){
            return true;
        }
        return false;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public Bitmap rotate(Bitmap bitmap, int rotation){
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return rotated;
    }

    public Bitmap flip(Bitmap bitmap, int x, int y) {
        Matrix matrix = new Matrix();
        matrix.preScale(x, y);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Mat prepare(Mat mat){
        Mat mask=new Mat();
        //перевод в черно-белый спектр
        Imgproc.cvtColor(mat, mask, Imgproc.COLOR_RGB2GRAY);
        //размытие
        Imgproc.GaussianBlur(mask, mask, new Size(11, 11), 0);
        //перевод в бинарную матрицу
        Imgproc.adaptiveThreshold(mask, mask, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5, 2);
        return mask;
    }

    private MatOfPoint findBiggestContour(Mat mat){
        try {
            //список матриц размером n:1. каждая найденный белый контур
            List<MatOfPoint> contour = new ArrayList<MatOfPoint>();
            //поиск контуров. Контуром считается непрерывная последовательность белых пикселей.
            Imgproc.findContours(mat.clone(), contour, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            //длина максимального контура
            double max = Imgproc.contourArea(contour.get(0));
            //номер максимального контура
            int index = 0;
            //получает длину каждого контура и ищет максимальный
            for (int i = 0; i < contour.size(); i++) {
                if (max < Imgproc.contourArea(contour.get(i))) {
                    max = Imgproc.contourArea(contour.get(i));
                    index = i;
                }
            }
            //максимальный контур-сетка судоку.
            return contour.get(index);
        }
        catch(Exception e){
            //изображение пустое
        }
        return null;
    }

    private MatOfPoint2f aproxPolygon(MatOfPoint contour) {
        MatOfPoint2f dst = new MatOfPoint2f();
        MatOfPoint2f src = new MatOfPoint2f();
        //приведение типов
        contour.convertTo(src, CvType.CV_32FC2);
        //функция вычисляет длину кривой или замкнутого контура по периметру
        double arcLength = Imgproc.arcLength(src, true);
        //Суть алгоритма состоит в том, чтобы по данной ломаной построить ломаную с меньшим числом точек.
        //Алгоритм определяет расхождение, которое вычисляется по максимальному расстоянию между исходной и упрощённой кривыми.
        //Упрощенная кривая состоит из подмножества точек, которые определяются из исходной кривой.
        Imgproc.approxPolyDP(src, dst, 0.02 * arcLength, true);
        return dst;
    }

    private int distance(MatOfPoint2f contour){
        Point[] point=contour.toArray();
        //длина вектора, соединяющего первую и последнюю точку контура.
        return (int)Math.sqrt((point[0].x-point[1].x)*(point[0].x-point[1].x)+(point[0].y-point[1].y)*(point[0].y-point[1].y));
    }

    private Mat applyMask(Mat mat, MatOfPoint contour) {
        Mat mask = Mat.zeros(mat.size(), CvType.CV_8UC1);
        ArrayList<MatOfPoint> line=new ArrayList<MatOfPoint>();
        line.add(contour);
        Imgproc.drawContours(mask, line, 0, Scalar.all(255), -1);
        Imgproc.drawContours(mask, line, 0, Scalar.all(0), 2);
        Mat dst=new Mat();
        mat.copyTo(dst, mask);
        return dst;
    }

    private Mat wrapPerspective(int size, MatOfPoint2f src, Mat mat) {
        try {
            Size reshape = new Size(size, size);
            Mat undistorted = new Mat(reshape, CvType.CV_8UC1);
            MatOfPoint2f d = new MatOfPoint2f();
            d.fromArray(new Point(0, 0), new Point(0, reshape.width), new Point(reshape.height, 0),
                    new Point(reshape.width, reshape.height));
            Imgproc.warpPerspective(mat, undistorted, Imgproc.getPerspectiveTransform(src, d), reshape);
            return undistorted;
        }
        catch(Exception e){

        }
        return mat;
    }

    private MatOfPoint2f orderPoints(MatOfPoint2f mat) {
        //сортировка угловых точек
        List<Point> pointList = mat.toList();
        for (int i=0; i<pointList.size(); i++){
            for (int j=i; j<pointList.size(); j++){
                //если координаты 1 точки > координат 2, меняем их местами
                if ((pointList.get(i).x+pointList.get(i).y)>(pointList.get(j).x+pointList.get(j).y)){
                    Collections.swap(pointList, i, j);
                }
            }
        }
        MatOfPoint2f corners = new MatOfPoint2f();
        corners.fromList(pointList);
        return corners;
    }

    private Mat cleanLines(Mat image) {
        Mat mat = image.clone();
        Mat lines = new Mat();
        Imgproc.HoughLinesP(mat, lines, 1, Math.PI / 180, 50, 200, 20);
        for (int x = 0; x < lines.rows(); x++) {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0];
            double y1 = vec[1];
            double x2 = vec[2];
            double y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            Imgproc.line(mat, start, end, Scalar.all(0), 5);

        }
        return mat;
    }
}
