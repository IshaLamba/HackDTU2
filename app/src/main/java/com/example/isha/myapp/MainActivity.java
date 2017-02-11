package com.example.isha.myapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import static android.R.attr.button;

public class MainActivity extends AppCompatActivity {

    TextToSpeech tts;
    Prefs prefs;
    Button b1;
    public static boolean isServiceRunning = false;
    private static final String SCHEME = "package";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1=(Button) findViewById(R.id.init_service);
        tts=new TextToSpeech(MainActivity.this,new TextToSpeech.OnInitListener(){
            public void onInit(int status){
                if(status!= TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                    speak("Click button to start the service.");
                }
            }
        });
        b1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(),ShakeService.class));
                Toast toast = Toast.makeText(getApplicationContext(), "Service started..!!! please press back button to go back to home screen.", Toast.LENGTH_SHORT);
                toast.show();
                tts=new TextToSpeech(MainActivity.this,new TextToSpeech.OnInitListener(){
                    public void onInit(int status){
                        if(status!= TextToSpeech.ERROR) {
                            tts.setLanguage(Locale.US);
                            speak("Service started. please press back button to go back to home screen");
                        }
                    }
                });
            }
        });

    }

    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void speak(String x){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(x, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(x, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressLint("InlinedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_stop) {
            if (isServiceRunning) {
                stopService(new Intent(this, ShakeService.class));
                finish();
            } else {
                finish();
            }
            return true;
        }
        if (id == R.id.action_notification) {
            Intent intent = new Intent();
            final int apiLevel = Build.VERSION.SDK_INT;
            if (apiLevel >= 9) { // above 2.3
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts(SCHEME, getPackageName(), null);
                intent.setData(uri);
            } else { // below 2.3
                final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                        : APP_PKG_NAME_21);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                        APP_DETAILS_CLASS_NAME);
                intent.putExtra(appPkgName, getPackageName());
            }
            startActivity(intent);
            Toast.makeText(this,
                    "Check 2 times Show notifications and keep this checked",
                    Toast.LENGTH_LONG).show();
        }
        if (id == R.id.action_rate) {
            Intent myIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="
                            + getPackageName()));
            startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);

    }
}
