package com.olabs.mars3d;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


import android.view.View;
import android.widget.Button;
import android.content.Intent;

import android.content.Context;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;



import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.w3c.dom.Text;

import com.olabs.motioncapture.Orientation;




public class CamCapture extends Activity {


    Orientation mOrientationService;
    boolean mBound;
    float[] fusedOrientation;



    static {

        System.loadLibrary("opencv_java3");

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

    }


    @Override
    protected void onStart(){

        super.onStart();

        Intent fusedSensor = new Intent(CamCapture.this,Orientation.class);
        startService(fusedSensor);
        bindService(fusedSensor, mOrientationConnection, Context.BIND_AUTO_CREATE);
        final Button captureButton = (Button)findViewById(R.id.captureButton);
        final TextView azimuth  = (TextView)findViewById(R.id.azView);
        final TextView pitch    = (TextView)findViewById(R.id.piView);
        final TextView roll     = (TextView)findViewById(R.id.roView);


        captureButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Log.w("onClick", "Here");
                final DecimalFormat  decimalFormat = new DecimalFormat("#.###");
                decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                decimalFormat.setMaximumFractionDigits(3);
                decimalFormat.setMinimumFractionDigits(3);


                fusedOrientation = mOrientationService.getFusedOrientation();
                azimuth.setText(decimalFormat.format(fusedOrientation[0]*180/ Math.PI));
                pitch.setText(decimalFormat.format(fusedOrientation[1]*180/Math.PI));
                roll.setText(decimalFormat.format(fusedOrientation[2]*180/Math.PI));


            }
        });



    }
    @Override
    protected void onPause(){
        super.onPause();
        Intent fusedSensor = new Intent(CamCapture.this,Orientation.class);
        stopService(fusedSensor);
    }

    @Override
    protected void onStop(){
        super.onStop();
        Intent fusedSensor = new Intent(CamCapture.this,Orientation.class);
        stopService(fusedSensor);
    }

    private ServiceConnection mOrientationConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected( ComponentName classname, IBinder service){
            Log.i("onServiceConnected", "Connected to service.");
            Orientation.LocalBinder binder = (Orientation.LocalBinder) service;
            mOrientationService = binder.getService();

            if( mOrientationService != null){
                mBound =true;
                Log.i("service-bind", "Service is bonded successfully!");
            }
            else
                Log.i("service-bind", "Service is bonded not successfully!");

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };



}
