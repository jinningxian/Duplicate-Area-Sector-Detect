package com.example.angledetected;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{
    public static int CAMERAANGLERANGE = 120;
    public static int CAMERADISTANCETODETECT = 500; //107 = 5 meters; ensure camera can only view 5 meters
    public Point currentDetectPoint = null;
    private int angle(Point current, Point checkPoint){
        double angle = Math.atan2((current.y-checkPoint.y),(current.x-checkPoint.x));
        double theta = (180 / Math.PI) * angle;
        if(theta < 0.0) theta += 360;
        return (int) theta;
    }
    private double distance(Point current, Point checkPoint){
        return Math.sqrt(Math.pow((current.x-checkPoint.x),2)+Math.pow((current.y-checkPoint.y),2));
    }
    public boolean PointDetectAction(Point p){
        if(currentDetectPoint == null){
            currentDetectPoint = p;
            return true;
        }
        if(distance(p,currentDetectPoint)>CAMERADISTANCETODETECT){
            currentDetectPoint = p;
            return true;
        }else if(distance(p,currentDetectPoint)==0){
            if(angleCompred(currentDetectPoint.angle, p.angle)) {
                return false;
            }else{
                currentDetectPoint = p;
                return true;
            }

        }else{
            double mPointX, mPointY;//, mPointDistance;
            mPointX = (currentDetectPoint.x + p.x)/2;
            mPointY = (currentDetectPoint.y + p.y)/2;
            double hPointX, hPointY;
            hPointX = Math.sqrt(Math.pow(CAMERADISTANCETODETECT,2)-Math.pow(p.x-mPointX,2));
            hPointY = Math.sqrt(Math.pow(CAMERADISTANCETODETECT,2)-Math.pow(p.y-mPointY,2));
            Point interPoint1, interPoint2;
            interPoint1 = new Point(mPointX+hPointX,mPointY+hPointY);
            interPoint2 = new Point(mPointX-hPointX,mPointY-hPointY);
            double angleBetweenIP1, angleBetweenIP2;
            angleBetweenIP1 = angle(currentDetectPoint, interPoint1);
            angleBetweenIP2 = angle(currentDetectPoint, interPoint2);
            if(angleCompred(currentDetectPoint.angle,angleBetweenIP1) || angleCompred(currentDetectPoint.angle,angleBetweenIP2)){
                return false;
            }currentDetectPoint = p; return true;
        }
    }
    public boolean angleCompred(double stock, double newAngle) {
        stock %= 360; newAngle%=360;
        if(newAngle > 240 && stock < 120){
            return !((newAngle+120)%360 < stock);
        }
        double b = Math.abs (((stock - newAngle)+180)%360 - 180);

        Log.d("Test 10", ""+b);
        return b<CAMERAANGLERANGE;
    }
    class detectArea implements Runnable{
        Point p;
        boolean result;
        public detectArea(Point nPoint) {
            this.p = nPoint;
        }

        @Override
        public void run() {
            result = PointDetectAction(p);
        }
        public boolean getResult(){
            return result;
        }
    }
    public void getResult(Point nPoint, TextView result){
        boolean ans;
        detectArea da = new detectArea(nPoint);
        da.run();
        ans = da.getResult();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String rsl = "Current Point: [("+currentDetectPoint.x +","+currentDetectPoint.y+")"+ currentDetectPoint.angle+"] -> "+ans+"\n";
        result.setText(rsl);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button detect = findViewById(R.id.detect);
        final TextView x, y, a, result;
        x = findViewById(R.id.x);
        y = findViewById(R.id.y);
        a = findViewById(R.id.angle);
        result = findViewById(R.id.result);
        boolean n = angleCompred(220,30);
        Log.d("Test 1", ""+n);
        n = angleCompred(20,30);
        Log.d("Test 2", ""+n);
        n = angleCompred(130,120);
        Log.d("Test 3", ""+n);
        n = angleCompred(300,160);
        Log.d("Test 4", ""+n);
        n = angleCompred(350,300);
        Log.d("Test 5", ""+n);
        n = angleCompred(230,130);
        Log.d("Test 6", ""+n);
        n = angleCompred(210,30);
        Log.d("Test 7", ""+n);
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String px1 =x.getText().toString().trim();
                String py1 =y.getText().toString().trim();
                String pa1 =a.getText().toString().trim();
                if(pa1.length()>0&&px1.length()>0&&py1.length()>0){
                    double px = Double.valueOf(px1);
                    double py = Double.valueOf(py1);
                    double rad = Double.valueOf(pa1);
                    Log.d(" Values ", px+" "+py+" "+rad);
                    
                    double pa = rad/Math.PI*180;
                    Point nPoint = new Point(px,py,pa);
                    getResult(nPoint,result);
                }



            }
        });
    }


}


