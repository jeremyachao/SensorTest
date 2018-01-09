package com.imb.jeremy.sensortest;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Created by Reven on 07/01/2018.
 */

public class ActivityRecognisedService extends IntentService
{
    public static int run, walk, tilt, unknown, vehicle, bike, still, on_foot;
    public static final String BROADCASTPERM = "";

    public ActivityRecognisedService() {
        super("ActivityRecognisedService");
    }



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities(result.getProbableActivities());
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for( DetectedActivity activity : probableActivities ) {
            switch( activity.getType() ) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.d( "ActivityRecogition*", "In Vehicle: " + activity.getConfidence() );
                    vehicle = activity.getConfidence();
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.d( "ActivityRecogition*", "On Bicycle: " + activity.getConfidence() );
                    bike = activity.getConfidence();
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.d( "ActivityRecogition*", "On Foot: " + activity.getConfidence() );
                    on_foot = activity.getConfidence();
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.d( "ActivityRecogition*", "Running: " + activity.getConfidence() );
                    run = activity.getConfidence();
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.d( "ActivityRecogition*", "Still: " + activity.getConfidence() );
                    still = activity.getConfidence();
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.d( "ActivityRecogition*", "Tilting: " + activity.getConfidence() );
                    tilt = activity.getConfidence();
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.d( "ActivityRecogition*", "Walking: " + activity.getConfidence() );
                    walk = activity.getConfidence();
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.d( "ActivityRecogition*", "Unknown: " + activity.getConfidence() );
                    unknown = activity.getConfidence();
                    break;
                }
            }
        }
    }
}
