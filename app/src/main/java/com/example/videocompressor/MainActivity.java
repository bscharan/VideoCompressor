package com.example.videocompressor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.madhavanmalolan.ffmpegandroidlibrary.Controller;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    VideoView mVideoView;
    TextView mtextView;

    FFmpeg ffmpeg;
    String fpathname;
    String inputfile;
    String outputfile;
    String cmd_trim;
    String cmd_frameCompress;


    private static final int PICK_FROM_FILE = 1;


    static int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);
        CheckforPermission();





    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        Uri vidUri= null;
        if (requestCode == PICK_FROM_FILE) {
            vidUri = data.getData();
        }
        //set the video path
        mainexec(vidUri);

    }
        public void mainexec(Uri uri){
        //uri=Uri.parse("android.resource://" + getPackageName() +"/"+R.raw.sample);

        fpathname=Environment.getExternalStorageDirectory()+"/"+"MYMovies";

//        File tempfile= new File(uri.getPath());
        String inputfile = getRealPathFromURI(uri);
            Log.e(TAG, "mainexec: "+inputfile);
        File mediaStorageDir = new File(fpathname);

        if (!mediaStorageDir.exists()) {
            Log.e("App", "MYMovies directory doesnt exist creating one");
            if (!mediaStorageDir.mkdirs()) {
                Log.e("App", "failed to create directory");
            }else{
                Log.e("APP", "created succesful");
            }
        } else {
            Log.e("APP", "Folder Already exists");
        }
        ffmpeg = FFmpeg.getInstance(getApplicationContext());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String vtime = sdf.format(new Date());

        String outputfile = fpathname+"/vidhigh"+vtime+".mp4";
        initialize(this);
        String[] cmd= {"-ss","4","-i",inputfile,"-c","copy","-t", "2", outputfile};


        //cmd={"-i ",fpathname+"/vidhigh.mp4 ","-t", "00:00:02", "-c", "copy ",fpathname+"/small-1.mp4", -ss 00:00:02 -codec copy "+fpathname+"/small-2.mp4"};

      executeCmd(cmd);

//            executeCutVideoCommand( 1000, 3000,1);

        Log.e(TAG, "onCreate: execution completed");
//        try {
//            Log.e(TAG, "onCreate: FFmpeg loadbinary");
//            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
//
//                @Override
//                public void onStart() {
//                    Log.e(TAG, "onCreate: FFmpeg loadbinary onstart");
//                }
//
//                @Override
//                public void onFailure() {
//                    Log.e(TAG, "onCreate: FFmpeg loadbinary onfailure");
//                }
//
//                @Override
//                public void onSuccess() {
//                    Log.e(TAG, "onCreate: FFmpeg loadbinary onsuccess");
//                }
//
//                @Override
//                public void onFinish() {
//                    Log.e(TAG, "onCreate: FFmpeg loadbinary onfinish");
//                }
//            });
//        } catch (FFmpegNotSupportedException e) {
//            Log.e(TAG, "onCreate: FFmpeg loadbinary error"+e);
//            // Handle if FFmpeg is not supported by device
//        }


        //todo execute command shift it to the new function if possible.

//        try {
//            // to execute "ffmpeg -version" command you just need to pass "-version"
//            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {
//
//                @Override
//                public void onStart() {}
//
//                @Override
//                public void onProgress(String message) {}
//
//                @Override
//                public void onFailure(String message) {}
//
//                @Override
//                public void onSuccess(String message) {}
//
//                @Override
//                public void onFinish() {}
//            });
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            // Handle if FFmpeg is already running
//        }

    }
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    public void playbutton(View v){
        i=setup(1);

        MediaPlayer.OnCompletionListener completionListener=new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                i= setup(i);
            }
        };
        mVideoView.setOnCompletionListener(completionListener);

    }

    public void loadvideo(View V){
        setContentView(R.layout.activity_main);
        mVideoView = (VideoView) findViewById(R.id.videoView);
        mtextView= (TextView) findViewById(R.id.textviewlogs);
        mtextView.setMovementMethod(new ScrollingMovementMethod());;
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("video/*");
        startActivityForResult(i, PICK_FROM_FILE);


    }
    public void workonsamplevideo(View V){

    }
    public int setup(int i){

        if(i==1){
        mVideoView.setVideoURI(Uri.parse(fpathname+"/sample.mp4"));
       // mVideoView.setMediaController(new MediaController(this));
        mVideoView.requestFocus();
        mVideoView.start();
        return 0;}
        if(i==0){
            mVideoView.setVideoURI(Uri.parse(fpathname+"/sample.mp4"));
            //mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();
            mVideoView.start();
        }
        return 1;
        }
    public static void initialize(Context context) {
        final FFmpeg ffmpeg = FFmpeg.getInstance(context.getApplicationContext());
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onSuccess() {
                    Log.e(TAG, "initialize: onSuccess: FFmpeg Initialization succesful");
                    // FFmpeg is supported by device
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
            Log.e(TAG, "initialize: FFmpeg unsupported ");
        }
    }

    /**
     * Executing ffmpeg binary
     */
    private void executeCutVideoCommand(int startMs, int endMs,int a) {
        File moviesDir = new File(fpathname);

        String filePrefix = "vidhigh";
        String fileExtn = ".mp4";
        String yourRealPath = fpathname+"/vidhigh.mp4";
                File dest = new File(moviesDir, filePrefix+a + fileExtn);
//        int fileNo = 0;
//        while (dest.exists()) {
//            fileNo++;
//            dest = new File(moviesDir, filePrefix + fileNo + fileExtn);
//        }
        String filePath = dest.getAbsolutePath();
//        String[] complexCommand = {"-ss", "" + startMs / 1000, "-y", "-i", yourRealPath, "-t", "" + (endMs - startMs) / 1000,"-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};

        String[] complexCommand = {"-y", "-i", yourRealPath, "-ss",""+2 , "-to",""+ 4, "-c", "copy", filePath};

        execFFmpegBinary(complexCommand);
        MediaScannerConnection.scanFile(MainActivity.this,
                new String[] { filePath },
                null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {

                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });


    }
    private void execFFmpegBinary(final String[] command) {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                     Log.e(TAG, "FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) {
                    Log.e(TAG, "SUCCESS with output : " + s);

                }

                @Override
                public void onProgress(String s) {
                    progressDialog.setMessage("progress : " + s);
                }

                @Override
                public void onStart() {
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    progressDialog.dismiss();


                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }

    private void executeCmd(final String[] command) {
        Log.e(TAG, "executecmd started");
        final String tag2=" executecmd";
       // final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        try {
                ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                    @Override
                    public void onFailure(String s) {
                        Log.e(TAG, "FAILED with output : " + s);
                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "SUCCESS with output : " + s);
                        mtextView.setText("SUCCESS with output : "+s);

                    }

                    @Override
                    public void onProgress(String s) {
                            mtextView.setText("on progress :" + s);
//                        progressDialog.setMessage("on progress : " + s);
                    }

                    @Override
                    public void onStart() {
                        mtextView.setText("on Start: processing :");
//                        progressDialog.setMessage("on start:Processing...");
//                        progressDialog.show();
                    }

                    @Override
                    public void onFinish() {
//                        progressDialog.dismiss();


                    }
                });
            }
        catch (FFmpegCommandAlreadyRunningException e) {
        // do nothing for now
        }
    }


//    private void newexecute(){
//        Controller.getInstance().run(new String[]{"-y",
//                    "-i",
//                    inputfile,
//                    "-vcodec",
//                    "copy",
//                    "-an",
//                    outputfile
//        });
//
//    }
    private void CheckforPermission(){
        Log.e(TAG, "onCreate: checking for permissions");
        //todo permission needed :read storage, write to storage
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    106);
            Log.e(TAG, "onCreate: permissions granted");
        }else {
            Log.e(TAG, "onCreate: permissions granted");
        }

    }

}

