package com.example.isha.myapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import com.example.isha.myapp.main;

/**
 * Created by NISHTHA on 19-Oct-16.
 */

public class AppIntent extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    boolean found=false;
    TextToSpeech tts;
    TextView disp;
    private ImageView mImageView;
    String phoneNo;
    String message;
    private static final int RQS_RECOGNITION = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    //Spinner spinnerResult;
    //String res;
    public String contact;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_intent);
        disp=(TextView) findViewById(R.id.tvIntent);
        //spinnerResult = (Spinner) findViewById(R.id.result);
        mImageView=(ImageView)findViewById(R.id.captured_photo);
        Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        mVibrator.vibrate(600);
        tts=new TextToSpeech(AppIntent.this,new TextToSpeech.OnInitListener(){
           public void onInit(int status){
               if(status!= TextToSpeech.ERROR) {
                tts.setLanguage(Locale.US);
                speak("voice command mode activated");
               }
           }
        });
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to Recognize");
        startActivityForResult(intent, RQS_RECOGNITION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == RQS_RECOGNITION) & (resultCode == RESULT_OK)) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String selectedResult = result.toString();
            Toast.makeText(AppIntent.this, selectedResult, Toast.LENGTH_SHORT).show();
            recognition(result);
           }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
            main obj=new main();
            obj.doOCR(obj.convertColorIntoBlackAndWhiteImage(imageBitmap) );
        }
    }

    private void recognition(ArrayList<String> result){
        Log.e("Speech",""+result);
        String res=result.get(0);
        String[] R1=res.split(" ");
        if(R1[0].equals("call")){
            int n=R1.length-1;
            String[] Res2=new String[n];
            System.arraycopy(R1,1,Res2,0,n);
            String num = Arrays.asList(Res2).toString();
            contact= num.substring(1, num.length()-1).replaceAll(","," ");
            Toast.makeText(AppIntent.this, contact, Toast.LENGTH_SHORT).show();
            getNumber(this.getContentResolver(),contact);
            call();
        }
        else if(R1[0].equals("message")){
            String s=R1[1];
            getNumber(this.getContentResolver(),s);
            Toast.makeText(AppIntent.this, phoneNo, Toast.LENGTH_SHORT).show();
            int n=R1.length-2;
            String[] Res2=new String[n];
            System.arraycopy(R1,2,Res2,0,n);
            String str = Arrays.asList(Res2).toString();
            message=str.substring(1, str.length()-1).replaceAll(",", " ");
            Toast.makeText(AppIntent.this, message, Toast.LENGTH_SHORT).show();
            sendSMSMessage();
        }

        else if(R1[0].equals("new")&&R1[1].equals("call")){
            int n=R1.length-2;
            String[] Res2=new String[n];
            System.arraycopy(R1,2,Res2,0,n);
            String num = Arrays.asList(Res2).toString();
            phoneNo= num.substring(1, num.length()-1).replaceAll(",", "");
            Toast.makeText(AppIntent.this, num, Toast.LENGTH_SHORT).show();
            call();
        }
        else if(R1[0].equals("new")&&R1[1].equals("message")){
            //speak("message");
            int n=R1.length-2;
            String r="";
            String[] Res2=new String[n];
            System.arraycopy(R1,2,Res2,0,n);
            int i=0,j=0;
            while(i<=10)
            {
                int l=Res2[j].length();
                i=i+l;
                r=r.concat(Res2[j]);
                j++;
            }
            phoneNo=r.substring(0,10);
            int n1=Res2.length-(j-1);
            //Toast.makeText(AppIntent.this, phoneNo, Toast.LENGTH_SHORT).show();
            String[] msg=new String[n1];
            System.arraycopy(Res2,j-1,msg,0,n1);
            String str = Arrays.asList(msg).toString();
            message=str.substring(1, str.length()-1).replaceAll(",", " ");
            Toast.makeText(AppIntent.this, str, Toast.LENGTH_SHORT).show();
            sendSMSMessage();

        }
        else if(R1[0].equals("read")){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
        else
            speak("unknown");
    }

    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    protected void sendSMSMessage()
    {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
        speak("message sent");
        //Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
    }
protected void call(){
    if(!found)
        speak("contact not found");
       // Toast.makeText(AppIntent.this, "contact not found", Toast.LENGTH_SHORT).show();
    else {
        speak("calling");
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNo));
        if (ActivityCompat.checkSelfPermission(AppIntent.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }
}
    private void speak(String x){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(x, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(x, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void getNumber(ContentResolver cr,String n)
    {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String ph = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(name.toLowerCase().equals(n)) {
                phoneNo=ph;
                found=true;
                //Toast.makeText(AppIntent.this, phoneNo, Toast.LENGTH_SHORT).show();
                break;
            }
        }
        phones.close();
    }
}
