package com.example.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Show extends Activity {

	private TextView Name;
	private TextView Desc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show);
		
		Intent intent = getIntent();
	    String name = intent.getStringExtra("name");
	    String desc = intent.getStringExtra("desc");
	
	    String url = "http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=da4fadd0084ea1799ad33048f0d6a5c5&text=TempleRun2";
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()       
        .detectDiskReads()       
        .detectDiskWrites()       
        .detectNetwork()    
        .penaltyLog()       
        .build());       
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()       
        .detectLeakedSqlLiteObjects()    
        .penaltyLog()       
        .penaltyDeath()       
        .build());
 		String pic_url = "http://farm";
 		String[] urlList = null;
 		try {
 		    HttpClient httpClient = new DefaultHttpClient();
 		    HttpContext localContext = new BasicHttpContext();
 		    HttpPost httpPost = new HttpPost(url);
 		    HttpResponse response = httpClient.execute(httpPost, localContext);
 			InputStream in = response.getEntity().getContent();
            
            BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            int count = 0;
            while( (line = bReader.readLine()) != null){
            	String res[] = new String[4];
                if(line.contains("photo ")){
                    res[0] = line.substring(line.indexOf("id=\"")+4, line.indexOf("\" owner="));
                    res[1] = line.substring(line.indexOf("secret=\"")+8, line.indexOf("\" server"));
                    res[2] = line.substring(line.indexOf("server=\"")+8, line.indexOf("\" farm"));
                    res[3] = line.substring(line.indexOf("farm=\"")+6, line.indexOf("\" title"));
                    pic_url = pic_url + res[3] + ".staticflickr.com/" + res[2] + "/" + res[0] + "_" + res[1] + ".jpg";
                    urlList[count++] = pic_url;
                }
            }
  		} catch (Exception e) {
 		    e.printStackTrace();
 		}
		ListView list = (ListView) findViewById(R.id.listView2);
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.templerun2);
		map.put("Name", name);
		map.put("Desc", desc);
		listItem.add(map);
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.activity_connect, new String[] { "ItemImage" , "Name" , "Desc"}, new int[] { R.id.ItemImage , R.id.Name , R.id.Desc });
		list.setAdapter(listItemAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.show, menu);
		return true;
	}

}
