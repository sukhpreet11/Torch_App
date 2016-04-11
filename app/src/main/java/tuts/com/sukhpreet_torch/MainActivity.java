package tuts.com.sukhpreet_torch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    Button strt;
    Button stp;
    private android.hardware.Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    private android.hardware.Camera.Parameters params;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        strt = (Button) findViewById(R.id.button); //start button
        stp = (Button) findViewById(R.id.button2); //stop button


        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasFlash) {
            // device doesn't support flash
            // Shows alert message and closes the application
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
            // closing the application
                    finish();
                }
            });
            alert.show();
            return;
        }
        //accessing the camera
            if (camera == null) {
                try {
                    camera = camera.open();
                    params = camera.getParameters();
                } catch (RuntimeException e) {
                    Log.e("Failed to open camera", e.getMessage());
                }
            }

        //turning on the flash
        strt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (!isFlashOn) {
                        if (camera == null || params == null) {
                            return;
                        }
                        params = camera.getParameters();
                        params.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(params);
                        camera.startPreview();
                        isFlashOn = true;
                    }
                }

        });

        //turning off the flash
        stp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    if (camera == null || params == null) {
                        return;
                    }
                    params = camera.getParameters();
                    params.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(params);
                    camera.stopPreview();
                    isFlashOn = false;

                }
            }

        });

        //Creating the alert message which will be displayed after every one minute of use
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(60000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //displaying the alert mesaage to tge user
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                                builder.setTitle("Confirm");
                                builder.setMessage("Do you want to keep using the torch?");

                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        // Closes the dialog
                                        dialog.dismiss();
                                    }

                                });

                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    //Turns of the torch if it is running
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (isFlashOn) {
                                            if (camera == null || params == null) {
                                                return;
                                            }
                                            params = camera.getParameters();
                                            params.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);
                                            camera.setParameters(params);
                                            camera.stopPreview();
                                            isFlashOn = false;

                                        }
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };t.start();

    }
}