package com.imb.jeremy.sensortest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    @BindView(R.id.startbtn)
    Button startbtn;
    @BindView(R.id.linearAcc)
    TextView linearAcc;
    @BindView(R.id.grav)
    TextView grav;
    @BindView(R.id.rotationVector)
    TextView rotationVector;
    @BindView(R.id.accelerometer)
    TextView accelerometer;
    @BindView(R.id.gyro)
    TextView gyro;
    @BindView(R.id.stepCounter)
    TextView stepCounter;
    @BindView(R.id.proximity)
    TextView proximity;
    @BindView(R.id.stepDetector)
    TextView stepDetector;
    @BindView(R.id.stationary)
    TextView stationary;
    @BindView(R.id.sigmotion)
    TextView sigmotion;
    @BindView(R.id.latitutde)
    TextView latitutde;
    @BindView(R.id.longitude)
    TextView longitude;
    @BindView(R.id.mapbtn)
    Button mapbtn;

    //sensors
    private SensorManager mSensorManager;
    private boolean activated;
    private List<Sensor> deviceSensors;
    private final String TAG = "MainActivity**";

    //location
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

        //disengage or engage sensors by click on the button
        startbtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if (isActivated()) {
                    activated = false;
                    startbtn.setText("activate");
                    stop();
                    Toast.makeText(MainActivity.this, "Sensors disengaged.", Toast.LENGTH_SHORT).show();
                } else {
                    activated = true;
                    startbtn.setText("Deactivate");
                    start();
                    Toast.makeText(MainActivity.this, "Sensors engaged.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * Unregisters the sensors
     */
    private void stop() {
        mSensorManager.unregisterListener(this);
    }

    /**
     * Binds listeners and registers the sensors
     * Creates sensor objects to imitate performance loss on an onCreate method
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void start() {
        loadDefaultSensors();
//        getLastKnownLocation();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadDefaultSensors() {
        //creating sensor objects
        //constant sensors
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Sensor mRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        //linear sensors
        Sensor mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        Sensor mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        //event sensors
//        Sensor mStepDetect = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        Sensor mStationaryDetect = mSensorManager.getDefaultSensor(Sensor.TYPE_STATIONARY_DETECT);
//        Sensor mSigmotion = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);

        //registering the sensor listener
        //constant sensors
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mRotationVector, SensorManager.SENSOR_DELAY_NORMAL);
        //linear sensors
        mSensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        //event sensors
//        mSensorManager.registerListener(this, mStepDetect, SensorManager.SENSOR_DELAY_NORMAL);
     mSensorManager.registerListener(this, mStationaryDetect, SensorManager.SENSOR_DELAY_NORMAL);
////        mSensorManager.registerListener(this, mSigmotion, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public boolean isActivated() {
        return activated;
    }

    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            //accelerometer
            case 1:
                updateUI(accelerometer, sensorEvent.values);
                break;
            //gravity sensor
            case 9:
                updateUI(grav, sensorEvent.values);
                break;
            //gyro
            case 4:
                updateUI(gyro, sensorEvent.values);
                break;
            //linear acceleration
            case 10:
                updateUI(linearAcc, sensorEvent.values);
                break;
            //rotation vector sensor
            case 11:
                updateUI(rotationVector, sensorEvent.values);
                break;
            //step counter sensor
            case 19:
                stepLinearUI(stepCounter, sensorEvent.values, sensorEvent.timestamp);
                break;
            //proximity sensor
            case 8:
                updateLinearUI(proximity, sensorEvent.values);
                break;
//            //step detector sensor
//            case 18:
//                updateLinearUI(stepDetector, sensorEvent.values);
//                break;
//            //stationary detect
//            //bug: device is somehow never stable...
            case 29:
                updateLinearUI(stationary, sensorEvent.values);
                break;
//            //significant motion detector
//            //need to bind listener to something
//            case 17:
//                TriggerEventListener listener = new TriggerEventListener() {
//                    @Override
//                    public void onTrigger(TriggerEvent triggerEvent) {
//                        updateLinearUI(sigmotion, sensorEvent.values);
//                    }
//                };
//                break;


        }
    }


    /**
     * for sensors with 1 value
     *
     * @param textView the textview to update
     * @param values   the sensor event float array to grab values from
     */
    private void stepLinearUI(TextView textView, float[] values, long milis) {
        String value = Float.toString(values[0]);
        String text = textView.getTag() + " value: " + value + " last step: " + milis;
        textView.setText(text);
    }

    private void updateLinearUI(TextView textView, float[] values) {
        String value = Float.toString(values[0]);
        Log.d(TAG, "updateLinearUI: " + values[0]);
        String text = textView.getTag() + " value: " + value;
        textView.setText(text);
    }

    /**
     * for sensors with 3 dimensional values
     *
     * @param textView the textview to update
     * @param array    the sensor event float array to grab values from
     */
    private void updateUI(TextView textView, float[] array) {
        //x value
        String x = Float.toString(array[0]);
        //y value
        String y = Float.toString(array[1]);
        //z value
        String z = Float.toString(array[2]);
        //text to be displayed
        String text = textView.getTag() + " values: x = " + x + ", y = " + y + ", z = " + z;
        textView.setText(text);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
