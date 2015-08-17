package org.androidtown.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;


public class MainActivity extends Activity implements TextToSpeech.OnInitListener {

    private static final int GET_CAP_CODE = 0;
    private static final int GET_PIC_CODE = 1;

    private String imgPath;

    public static TextToSpeech myTTS;
    boolean myTTSactive = false;
    private ScrollView scrollView;
    private ScrollView scrollView2;
    private TextView tv_str;
    private TextView tv_str2;
    private ImageButton btn_start;
    private ImageButton btn_stop;
    private ImageButton btn_setting;
    private String str = "Please select image!!";

    SCDialog scDialog;
    Dialog dialog;
    android.app.Dialog dialogCG;


    Gson gson = new Gson();
    Result r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scDialog= new SCDialog(MainActivity.this);
        scDialog.setCanceledOnTouchOutside(false);   //dialog실행중 다른 view 막아놓기
        dialog= new Dialog(MainActivity.this,"SPIC","다른 이미지를 선택해주세요!");

        dialogCG = new android.app.Dialog(MainActivity.this);
        dialogCG.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogCG.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCG.setContentView(R.layout.cg_dialog);
        ImageButton ivc = (ImageButton) dialogCG.findViewById(R.id.imageC);
        ImageButton ivg = (ImageButton) dialogCG.findViewById(R.id.imageG);

        ivc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCG.dismiss();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, GET_CAP_CODE);
                overridePendingTransition(0, 0);
            }
        });

        ivg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCG.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, GET_PIC_CODE);
                overridePendingTransition(0, 0);
            }
        });



        myTTS = new TextToSpeech(this,this);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView2 = (ScrollView) findViewById(R.id.scrollView2);
        tv_str = (TextView) findViewById(R.id.tv_str);
        tv_str2 = (TextView) findViewById(R.id.tv_str2);
        btn_start = (ImageButton) findViewById(R.id.btn_read);
        btn_stop = (ImageButton) findViewById(R.id.btn_stop);
        btn_setting = (ImageButton) findViewById(R.id.btn_setting);

        tv_str.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCG.show();
            }
        });

        tv_str2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCG.show();
            }
        });


        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(str.equals("Please select image!!")){
                    Toast.makeText(getApplicationContext(),"이미지를 선택해주세요 !",Toast.LENGTH_LONG).show();
                }
                else {
                    myTTS.setLanguage(Locale.US);
                    myTTSactive = true;
                    myTTS.speak(str, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTTS.stop();
            }
        });

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GET_PIC_CODE && resultCode == Activity.RESULT_OK){
            str = "Please select image!!";
            Uri imgUri =  data.getData();
            imgPath = getPath(imgUri);
            Log.d("DEBUG", "Choose: " + imgPath);

            new Async().execute();
        }
        else if(requestCode == GET_CAP_CODE && resultCode == Activity.RESULT_OK){
            str = "Please select image!!";
            //Uri imgUri =  data.getData();
            //imgPath = getPath(imgUri);
            imgPath = getPath1().toString();
            Log.d("경로", "Choose: " + imgPath);

            new Async().execute();
        }
    }


    //Get the path from Uri
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public Uri getPath1() {
        Uri uri = null;
        String[] projection = { MediaStore.Images.Media.DATA,MediaStore.Images.ImageColumns._ID };
        try {
            Cursor cursorImages = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);
            if(cursorImages != null && cursorImages.moveToLast()){
                uri = Uri.parse(cursorImages.getString(0));
                int id = cursorImages.getInt(1);
                cursorImages.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return uri;
    }


    @Override
    public void onInit(int status) {
        //처음시작할때 바로 음성으로 알려줌

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTTS.shutdown();
    }

    private class Async extends AsyncTask<String, Integer, String> {
        //ProgressDialog asyncDialog = new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //asyncDialog.show();
            scDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            System.out.print("test");

            //Multipart 객체를 선언한다.

            //File file = new File("/storage/emulated/0/Download/chinese-typesetting_english-original.png");
            File file = new File(imgPath);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create() //객체 생성...
                    .setCharset(Charset.forName("UTF-8")) //인코딩을 UTF-8로...
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart("myfile", new FileBody(file)); //빌더에 FileBody 객체에 인자로 File 객체를 넣어준다.
            //builder.addPart("testKey",  "test" ); //스트링 데이터..

            HttpClient client = new DefaultHttpClient();

            //HttpPost post = new HttpPost("http://ec2-54-199-201-110.ap-northeast-1.compute.amazonaws.com:8000/ocr/mem/upload/"); //전송할 URL
            HttpPost post = new HttpPost("http://ec2-54-199-201-110.ap-northeast-1.compute.amazonaws.com:8000/ocr/best/upload/"); //전송할 URL
            try {
                post.setEntity(builder.build()); //builder.build() 메쏘드를 사용하여 httpEntity 객체를 얻는다.
                HttpResponse httpRes;
                httpRes = client.execute(post);
                HttpEntity httpEntity = httpRes.getEntity();
                if (httpEntity != null) {
                    String response = EntityUtils.toString(httpEntity);
                    r = gson.fromJson(response, Result.class);
                    Log.d("response", response);
                    //System.out.print(response);
                }
            } catch (UnsupportedEncodingException e) {
            } catch (ClientProtocolException e1) {
            } catch (IOException e1) {
            } catch (org.apache.http.ParseException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //asyncDialog.dismiss();
            scDialog.dismiss();
            if(!r.getResult().equals("null")) {
                scrollView.setVisibility(View.GONE);
                scrollView2.setVisibility(View.VISIBLE);
                str = r.getResult();
                tv_str2.setText(str);
            }
            else{
                dialog.show();
                //snackbar.show();
                //Toast.makeText(getApplicationContext(),"Impossible!! please select different image..",Toast.LENGTH_LONG).show();
            }
        }
    }
}





