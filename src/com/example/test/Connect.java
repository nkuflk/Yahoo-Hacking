package com.example.test;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class Connect extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_listview);
		ListView list = (ListView) findViewById(R.id.listView1);
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		String str[] = { "facebook", "twiter", "google+", "新浪微博", "人人网" };
		for (int i = 0; i < 5; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			if( i==0 ) map.put("ItemImage", R.drawable.facebook);
			else if( i==1 ) map.put("ItemImage", R.drawable.twitter);
			else if( i==2 ) map.put("ItemImage", R.drawable.google);
			else if( i==3 ) map.put("ItemImage", R.drawable.weibo);
			else map.put("ItemImage", R.drawable.renren);
			map.put("ItemTitle", "与" + str[i] + "连接");
			listItem.add(map);
		}
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.activity_connect, new String[] { "ItemImage",
						"ItemTitle" }, new int[] { R.id.ItemImage,
						R.id.ItemTitle });
		list.setAdapter(listItemAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.connect, menu);
		return true;
	}

}
