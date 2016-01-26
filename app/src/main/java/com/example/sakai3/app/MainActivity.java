package com.example.sakai3.app;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private Button btn_signin, btn_announce_main,btn_resource,btn_help,btn_dues,btn_ann_list_1,btn_ann_list_2,btn_ann_list_3,btn_ann_list_4,btn_vv255_ann,btn_vv285_ann,btn_back;
    private View web_view,login_view,main_view,announce_main,announce_sub,announce_list,due_view,help_view,resources_view,fail_view,ass_view,ass_list_view;
    public TextView announce_cont,announce_title,account,password,ass_tt,ass_ist,ass_dt;
    public String acc="0",pass="0";
    public   String[] AnnTitle, AnnBody,AssTitle,AssInstructions, AssDue,AssTime;
    public      int lengthAnn,lengthAss,a;
    public Button btn_setalarm,btn_d1,btn_d2,btn_only,s_web;
    public WebView wv;


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        login_view=View.inflate(this,R.layout.login,null);
        setContentView(login_view);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try
        {
            String JsonFileAnn=getAnnouncement();
            String JsonFileAss=getAssignment();
            JSONObject objectAnn;
            JSONObject objectAss;


            String result="";
            try
            {objectAnn = new JSONObject(JsonFileAnn);
                 lengthAnn=objectAnn.getJSONArray("announcement_collection").length();
                 AnnTitle = new String[lengthAnn];
                 AnnBody = new String[lengthAnn];
                for(int i=0;i<lengthAnn;i++)
                { AnnTitle[i]=objectAnn.getJSONArray("announcement_collection").getJSONObject(i).getString("title");}
                for(int i=0;i<lengthAnn;i++)
                { AnnBody[i]=objectAnn.getJSONArray("announcement_collection").getJSONObject(i).getString("body");
                    AnnBody[i]=AnnBody[i].substring(3,AnnBody[i].length()-4);}
            objectAss = new JSONObject(JsonFileAss);
                lengthAss=objectAss.getJSONArray("assignment_collection").length();
                AssTitle = new String[lengthAss];
                AssInstructions = new String[lengthAss];
                AssDue = new String[lengthAss];
                AssTime=new String[lengthAss];
                for(int i=0;i<lengthAss;i++)
                { AssTitle[i]=objectAss.getJSONArray("assignment_collection").getJSONObject(i).getString("title");}
                for(int i=0;i<lengthAss;i++)
                { AssInstructions[i]=objectAss.getJSONArray("assignment_collection").getJSONObject(i).getString("instructions");
                    AssInstructions[i]=AssInstructions[i].substring(3,AssInstructions[i].length()-4);}
                for(int i=0;i<lengthAss;i++)
                { AssDue[i]=objectAss.getJSONArray("assignment_collection").getJSONObject(i).getJSONObject("dueTime").getString("display");}
                for(int i=0;i<lengthAss;i++)
                { AssTime[i]=objectAss.getJSONArray("assignment_collection").getJSONObject(i).getJSONObject("dueTime").getString("time");}
                result="Announcement:"+"\r\n";
                for(int i=0;i<lengthAnn;i++)
                {result=result+AnnTitle[i]+"\r\n"+AnnBody[i]+"\r\n";}
                result=result+"Assignment:"+"\r\n";
                for(int i=0;i<lengthAss;i++)
                {result=result+"\r\n"+AssTitle[i]+"\r\n"+AssInstructions[i]+"\r\n"+AssDue[i]+"\r\n";}
            }
            catch (JSONException e) {e.printStackTrace();}

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(MainActivity.this, ActionBroadCast.class), Intent.FLAG_ACTIVITY_NEW_TASK);
        for(int i=0;i<lengthAss;i++) {
            long time=Long.parseLong(AssTime[i]);
            if(time>System.currentTimeMillis()+10000){
                am.set(AlarmManager.RTC_WAKEUP, time, pi);
            }
        }

        findmyview();

    }

    public void findmyview(){
        web_view=View.inflate(this,R.layout.sakai_web,null);
        ass_view=View.inflate(this,R.layout.assignment,null);
        main_view=View.inflate(this,R.layout.display,null);
        announce_main=View.inflate(this,R.layout.annoucement,null);
        due_view=View.inflate(this,R.layout.dues,null);
        help_view=View.inflate(this,R.layout.resouces,null);
        ass_list_view=View.inflate(this,R
                .layout.ass_list,null);
        announce_sub=View.inflate(this,R.layout.announ_sub,null);
        announce_list=View.inflate(this,R.layout.ann_list,null);
        fail_view=View.inflate(this,R.layout.failtologin,null);
        wv=(WebView)web_view.findViewById(R.id.sakai_web);
        s_web=(Button)main_view.findViewById(R.id.turning);
        s_web.setOnClickListener(new ClickEvent());
        btn_signin =(Button)login_view.findViewById(R.id.signin_button);
        btn_signin.setOnClickListener(new ClickEvent());
        btn_announce_main=(Button)main_view.findViewById(R.id.announcement);
        btn_announce_main.setOnClickListener(new ClickEvent());
        btn_dues=(Button)main_view.findViewById(R.id.dues);
        btn_dues.setOnClickListener(new ClickEvent());
        btn_help=(Button)main_view.findViewById(R.id.help);
        btn_help.setOnClickListener(new ClickEvent());
        btn_back=(Button)fail_view.findViewById(R.id.fail_login);
        btn_back.setOnClickListener(new ClickEvent());
        btn_ann_list_1=(Button)announce_list.findViewById(R.id.first_ann);
        btn_ann_list_1.setOnClickListener(new ClickEvent());
        btn_ann_list_2=(Button)announce_list.findViewById(R.id.second_ann);
        btn_ann_list_2.setOnClickListener(new ClickEvent());
        btn_ann_list_3=(Button)announce_list.findViewById(R.id.third_ann);
        btn_ann_list_3.setOnClickListener(new ClickEvent());
        btn_ann_list_4=(Button)announce_list.findViewById(R.id.fourth_ann);
        btn_ann_list_4.setOnClickListener(new ClickEvent());
    if(lengthAnn<4){
    if(lengthAnn==0){
        btn_ann_list_1.setVisibility(View.GONE);
        btn_ann_list_2.setVisibility(View.GONE);
        btn_ann_list_3.setVisibility(View.GONE);
        btn_ann_list_4.setVisibility(View.GONE);
    }
        if(lengthAnn==1){
            btn_ann_list_1.setText(AnnTitle[0]);
            btn_ann_list_2.setVisibility(View.GONE);
            btn_ann_list_3.setVisibility(View.GONE);
            btn_ann_list_4.setVisibility(View.GONE);}

        if(lengthAnn==2){
            btn_ann_list_1.setText(AnnTitle[0]);
            btn_ann_list_2.setText(AnnTitle[1]);
            btn_ann_list_3.setVisibility(View.GONE);
            btn_ann_list_4.setVisibility(View.GONE);
        }
        if(lengthAnn==3){            btn_ann_list_1.setText(AnnTitle[0]);
            btn_ann_list_2.setText(AnnTitle[1]);
            btn_ann_list_3.setText(AnnTitle[2]);
            btn_ann_list_4.setVisibility(View.GONE);
        }
        if(lengthAnn==3){
            btn_ann_list_1.setText(AnnTitle[0]);
            btn_ann_list_2.setText(AnnTitle[1]);
            btn_ann_list_3.setText(AnnTitle[2]);
            btn_ann_list_4.setText(AnnTitle[3]);

    }}
        btn_vv255_ann=(Button)announce_main.findViewById(R.id.first);
        btn_vv255_ann.setOnClickListener(new ClickEvent());
        btn_vv285_ann=(Button)announce_main.findViewById(R.id.second);
        btn_vv285_ann.setOnClickListener(new ClickEvent());
        account=(TextView)login_view.findViewById(R.id.username_edit);
        btn_setalarm=(Button)ass_view.findViewById(R.id.set_alarm);
        btn_setalarm.setOnClickListener(new ClickEvent());
        btn_d1=(Button)due_view.findViewById(R.id.first_d);
        btn_d1.setOnClickListener(new ClickEvent());
        btn_d2=(Button)due_view.findViewById(R.id.second_d);
        btn_d2.setOnClickListener(new ClickEvent());
        btn_only=(Button)ass_list_view.findViewById(R.id.only_due);
        btn_only.setOnClickListener(new ClickEvent());
        password=(TextView)login_view.findViewById(R.id.password_edit);
        announce_cont=(TextView)announce_sub.findViewById(R.id.annouce_context);
        announce_title=(TextView)announce_sub.findViewById(R.id.annouce_title);
        ass_tt=(TextView)ass_view.findViewById(R.id.ass_title);
        ass_ist=(TextView)ass_view.findViewById(R.id.ass_context);
        ass_dt=(TextView)ass_view.findViewById(R.id.ass_time);
        if(lengthAss==0){
            btn_d1.setVisibility(8);
            btn_d2.setVisibility(8);
        }
        if(lengthAss==1){
            btn_d1.setText(AssTitle[lengthAss-1]);
            btn_d2.setVisibility(8);
        }
        if(lengthAss==2){
            btn_d1.setText(AssTitle[lengthAss-1]);
            btn_d2.setText(AssTitle[lengthAss-2]);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ClickEvent implements OnClickListener{
        @Override
        public void onClick(View v){
            if(v==btn_back){
                setContentView(login_view);
            }
            if(v==btn_signin){
                acc=account.getText().toString();
                pass=password.getText().toString();
                try {
                   a= VerifyID(acc,pass);
                } catch (IOException e) {
                    e.printStackTrace();
                }if(a==1){
                setContentView(main_view);}
                else if(a==0){
                    setContentView(fail_view);
                }
            }
            if(v==btn_announce_main){
                setContentView(announce_main);
            }
            if(v==btn_vv255_ann){
                setContentView(announce_list);

            }
            if(v==btn_vv285_ann){
                setContentView(announce_main);
            }
            if(v==btn_resource){
                setContentView(help_view);
            }
            if(v==btn_help){
                setContentView(help_view);
            }
            if (v==btn_dues){
                setContentView(ass_list_view);
            }
            if(v==btn_ann_list_1&&0<lengthAnn){
                setContentView(announce_sub);
                announce_cont.setText(AnnBody[0]);
                announce_title.setText(AnnTitle[0]);
            }
            if(v==btn_ann_list_2&&1<lengthAnn){
                setContentView(announce_sub);
                announce_cont.setText(AnnBody[1]);
                announce_title.setText(AnnTitle[1]);
            }
            if(v==btn_ann_list_3&&2<lengthAnn){
                setContentView(announce_sub);
                announce_cont.setText(AnnBody[2]);
                announce_title.setText(AnnTitle[2]);
            }
            if(v==btn_ann_list_4&&3<lengthAnn){
                setContentView(announce_sub);
                announce_cont.setText(AnnBody[3]);
                announce_title.setText(AnnTitle[3]);
            }
            if(v==btn_setalarm){

            }

            if(v==btn_d1&&lengthAss>0){
                setContentView(ass_view);
                ass_tt.setText(AssTitle[lengthAss-1]);
                ass_ist.setText(AssInstructions[lengthAss-1]);
                ass_dt.setText(AssDue[lengthAss-1]);
            }
            if(v==btn_d2&&lengthAss>1){
                setContentView(ass_view);
                ass_tt.setText(AssTitle[lengthAss-2]);
                ass_ist.setText(AssInstructions[lengthAss-2]);
                ass_dt.setText(AssDue[lengthAss-2]);
            }
            if(v==btn_only){
                setContentView(due_view);
            }
            if(v==s_web){
                setContentView(web_view);
                wv = (WebView) web_view.findViewById(R.id.sakai_web);
                wv.getSettings().setJavaScriptEnabled(true);
                wv.setScrollBarStyle(0);
                WebSettings webSettings = wv.getSettings();
                webSettings.setAllowFileAccess(true);
                webSettings.setBuiltInZoomControls(true);
                wv.loadUrl("http://192.168.1.116:8080");
//加载数据
                wv.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        if (newProgress == 100) {
                            MainActivity.this.setTitle("loaded");
                        } else {
                            MainActivity.this.setTitle("loading");

                        }
                    }
                });
//这个是当网页上的连接被点击的时候
                wv.setWebViewClient(new WebViewClient() {
                    public boolean shouldOverrideUrlLoading(final WebView view,
                                                            final String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
                // goBack()表示返回webView的上一页面

            }

        }
    }
    @Override
    public void onBackPressed(){
        setContentView(main_view);
    }

    public String getAnnouncement() throws ClientProtocolException, IOException {
        //服务器  ：服务器项目  ：servlet名称
        String path = "http://192.168.1.116:8080/portal/relogin";
        HttpPost httpPost = new HttpPost(path);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("eid","Shane"));
        list.add(new BasicNameValuePair("pw","123456" ));
        list.add(new BasicNameValuePair("submit","登录"));
        httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
        String result = "";
        HttpClient a = new DefaultHttpClient();
        HttpResponse response1 = a.execute(httpPost);

        HttpGet request = new HttpGet("http://192.168.1.116:8080/direct/announcement/user.json");
        HttpResponse response2 =a.execute(request);

        if (response2.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response2.getEntity();
            result = EntityUtils.toString(entity, HTTP.UTF_8);
        }
        return result;
    }
    public String getAssignment() throws ClientProtocolException, IOException {
        //服务器  ：服务器项目  ：servlet名称
        String path = "http://192.168.1.116:8080/portal/relogin";
        HttpPost httpPost = new HttpPost(path);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("eid","Shane"));
        list.add(new BasicNameValuePair("pw","123456" ));
        list.add(new BasicNameValuePair("submit","登录"));
        httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
        String result = "";
        HttpClient a = new DefaultHttpClient();
        HttpResponse response1 = a.execute(httpPost);

        HttpGet request = new HttpGet("http://192.168.1.116:8080/direct/assignment/my.json");
        HttpResponse response2 =a.execute(request);

        if (response2.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response2.getEntity();
            result = EntityUtils.toString(entity, HTTP.UTF_8);
        }
        return result;
    }
    public int VerifyID(String studentid,String studentpass) throws ClientProtocolException, IOException {
        //服务器  ：服务器项目  ：servlet名称
        String path = "http://192.168.1.116:8080/portal/xlogin";
        HttpPost httpPost = new HttpPost(path);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("eid", studentid));
        list.add(new BasicNameValuePair("pw", studentpass));
        list.add(new BasicNameValuePair("submit", "登录"));
        httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
        int result=0;
        HttpClient a = new DefaultHttpClient();
        HttpResponse response1 = a.execute(httpPost);
        if (response1.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response1.getEntity();
            String content = EntityUtils.toString(entity, HTTP.UTF_8);
            if (content.contains("登录信息不正确")) {
                result = 0;
            } else {
                result = 1;
            }}
            return result;
        }
}
