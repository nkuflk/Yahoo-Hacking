package com.example.test;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class App extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_listview);
		ListView list = (ListView) findViewById(R.id.listView1);
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		final String Name[] = { "TempleRun2", "QQ2013", "WeChat", "YahooWeather", "QuickPlay", "DaddyWasAThief", "SubwaySurfers", "Chrome", "TTPod", "CarrotFantasy" };
		final String By[] = { "age", "interest", "relationship", "interest", "Gender", "age", "relationship", "interest", "interest", "age" };
		for (int i = 0; i < 10; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			// map.put("ItemImage", R.drawable.checked);
			map.put("ItemTitle", Name[i]);
			map.put("ItemText", "By your friends who are similar to you in " + By[i]);
			listItem.add(map);
		}
		SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
				R.layout.activity_app, new String[] { "ItemImage", "ItemTitle",
						"ItemText" }, new int[] { R.id.ItemImage,
						R.id.ItemTitle, R.id.ItemText });
		list.setAdapter(listItemAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent();
                intent.putExtra("name", Name[arg2]);
                intent.putExtra("desc", "By your friends who are similar to you in " + By[arg2]);
                intent.setClass(App.this, Show.class);
                App.this.startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.app, menu);
		return true;
	}

}
