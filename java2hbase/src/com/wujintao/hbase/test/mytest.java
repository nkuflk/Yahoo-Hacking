package com.wujintao.hbase.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;

class myinfo {
	String appname;
	float weight;
	int count;

	myinfo(String arg1, float arg2, int arg3) {
		appname = arg1;
		weight = arg2;
		count = 0;
	}
}

public class mytest {

	public static Configuration configuration;
	static {
		configuration = HBaseConfiguration.create();
	}

	public static void main(String[] args) throws IOException {
		
		// QurryByCondition1("KeyTable", "mike");
		 findapp("KeyTable","Mike");

		//file2hbase();
		
		// createTable("wujintao1",a);
		// QurryByCondition1("wujintao1","rows1");
		// selectByFilter("KeyTable,arr);
		// QueryByCondition1("wujintao");
		// QueryByCondition2("wujintao");
		// QueryByCondition3("wujintao");
		// /deleteRow("wujintao","abcdef"); //
		// deleteByCondition("wujintao","abcdef");

	}
	public static void file2hbase() throws IOException{
		List<String> arr = new ArrayList<String>();
		String files = "/home/xiaogan/workspace/java2hbase/bin/com/wujintao/hbase/test/data.txt";
		BufferedReader reader = null;
		String tempString = null;
		try {
			File file = new File(files);
			reader = new BufferedReader(new FileReader(file));
			while ((tempString = reader.readLine()) != null) {
				String a[] = tempString.split(" "); 
				// arr.add(a[0]); //
				arr.add(a[1]); // arr.add(a[2]); // arr.add(a[3]);
				insertData("KeyTable", a);
			}
			System.out.println("insert all complete");
		} catch (Exception e) {
			e.toString();
		}
		reader.close();
	}

	/**
	 * 查询表中所有行
	 * 
	 * @param tablename
	 */
	public static void findapp(String tablename, String username) {
		int i1 = 0, i2 = 0, i3 = 0, count = 0, index = 0;
		float[] datas = new float[1000];
		float w = 0;
		String s1 = "", s4 = "", s5 = "";
		HashMap<String, myinfo> map = new HashMap<String, myinfo>();
		try {
			HTable table = new HTable(configuration, tablename);
			Result dest = QueryByCondition2(tablename, "Mike");
			s1 = new String(dest.getValue("info".getBytes(), "sex".getBytes()));
			s4 = new String(dest.getValue("info".getBytes(),"relationship".getBytes()));
			s5 = new String(dest.getValue("info".getBytes(), "age".getBytes()));
			i2 = Integer.valueOf(s5);
			s5 = new String(dest.getValue("info".getBytes(),"hobbies".getBytes()));
			i3 = Integer.valueOf(s5);
			Scan s = new Scan();
			
			myinfo mi = new myinfo("appdemo", 0.3f, 0);
			myinfo mi1 = new myinfo("appdemo1", 0.4f, 0);
			map.put(s5, new myinfo("stest", 0.2f, 0));
			ResultScanner rs = table.getScanner(s);
			myinfo mi2 = map.get(s5);
			Get g = new Get();

			for (Result r : rs) {
				count = 0;
				w = 0;
				s5 = new String(r.getRow());
				if (s5.equals("Mike"))
					continue;

				KeyValue[] kv = r.raw();

				s5 = new String(r.getValue("info".getBytes(), "sex".getBytes()));
				if (s1 == s5)
					w += 0.12f;
				else
					w += 0.06;
				s5 = new String(r.getValue("info".getBytes(),
						"relationship".getBytes()));
				if (s5.equals("r1"))
					w += 0.12f;
				else if (s5.equals("r2"))
					w += 0.08f;
				else if (s5.equals("r3"))
					w += 0.04f;
				s5 = new String(dest.getValue("info".getBytes(),
						"age".getBytes()));
				i1 = Integer.valueOf(s5);
				if (i2 > 10 && i2 < 20) {
					if (Math.abs(i1 - i2) < 2)
						w += 0.15;
					else if (Math.abs(i1 - i2) < 5)
						w += 0.10;
					else
						w += 0.05;
				} else if (i2 > 20 && i2 < 30) {
					if (Math.abs(i1 - i2) < 2)
						w += 0.15;
					else if (Math.abs(i1 - i2) < 5)
						w += 0.05;
					else
						w += 0.02;
				} else {
					if (Math.abs(i1 - i2) < 10)
						w += 0.15;
					else if (Math.abs(i1 - i2) < 20)
						w += 0.05;
				}
				s5 = new String(dest.getValue("info".getBytes(),
						"hobbies".getBytes()));
				i1 = Integer.valueOf(s5);
				i3 = i3 & i1;
				while (i3 != 0) {
					if ((i3 % 2) != 0)
						count++;
					i3 /= 2;
				}
				w += (count * 0.04);
				for (int i = 0; i < kv.length; i++) {
					if (new String(kv[i].getFamily()).equals("info"))
						continue;
					s5 = (new String(kv[i].getValue()));
					if (map.get(s5) == null)
						map.put(s5, new myinfo(s5, w, 0));
					else
						map.get(s5).weight += w;
				}
			}
			Collection<myinfo> myinfos = map.values();
			for (myinfo infos : myinfos) {
				for(int i=0;i<10;i++){ 
					if(new String(dest.getValue("apps".getBytes(), new String("app"+i).getBytes())).equals(infos.appname))
					{
						infos.weight=0;
						continue;
					}
				}
				//if(infos.appname.endsWith(arg0))
				datas[index] = 0 - infos.weight;
				index++;
			}
			Arrays.sort(datas);
			for (myinfo infos : myinfos){
				if(infos.weight==(0 - datas[0]))      System.out.println(infos.appname+infos.weight);
				else if(infos.weight==(0 - datas[1])) System.out.println(infos.appname+infos.weight);
				else if(infos.weight==(0 - datas[2])) System.out.println(infos.appname+infos.weight);
				else if(infos.weight==(0 - datas[3])) System.out.println(infos.appname+infos.weight);
				else if(infos.weight==(0 - datas[4])) System.out.println(infos.appname+infos.weight);
				else if(infos.weight==(0 - datas[5])) System.out.println(infos.appname+infos.weight);
				else if(infos.weight==(0 - datas[6])) System.out.println(infos.appname+infos.weight);
				else if(infos.weight==(0 - datas[7])) System.out.println(infos.appname+infos.weight);
				else if(infos.weight==(0 - datas[8])) System.out.println(infos.appname+infos.weight);
				else if(infos.weight==(0 - datas[9])) System.out.println(infos.appname+infos.weight);
			}
		} catch (IOException e) {
			e.printStackTrace();
			
		}
	}

	/**
	 * 插入一行记录
	 * 
	 * @param tablename
	 * @param cfs
	 */
	public static void insertData(String tablename, String[] cfs) {
		try {
			HTable table = new HTable(configuration, tablename);
			Put put = new Put(Bytes.toBytes(cfs[0]));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("sex"),
					Bytes.toBytes(cfs[1]));
			table.put(put);
			put = new Put(Bytes.toBytes(cfs[0]));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("age"),
					Bytes.toBytes(cfs[2]));
			table.put(put);
			put = new Put(Bytes.toBytes(cfs[0]));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("hobbies"),
					Bytes.toBytes(cfs[3]));
			table.put(put);
			put = new Put(Bytes.toBytes(cfs[0]));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("relationship"),
					Bytes.toBytes(cfs[4]));
			table.put(put);
			put = new Put(Bytes.toBytes(cfs[0]));
			for (int i = 0; i < 10; i++) {
				put.add(Bytes.toBytes("apps"), Bytes.toBytes("app" + i),
						Bytes.toBytes(cfs[5 + i]));
				table.put(put);
			}

			System.out.println("end inserting one record to table ......");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询一行
	 * 
	 * @param tableName
	 */
	public static void QurryByCondition1(String tablename, String rowKey)// tested
																			// ok
			throws IOException {
		HTable table = new HTable(configuration, tablename);
		Get g = new Get(rowKey.getBytes());
		Result rs = table.get(g);
		for (KeyValue kv : rs.raw()) {
			System.out.print(new String(kv.getRow()) + "");
			System.out.print(new String(kv.getFamily()) + ":");
			System.out.print(new String(kv.getQualifier()) + "");
			System.out.print(kv.getTimestamp() + "");
			System.out.println(new String(kv.getValue()));
		}
		System.out.println("end qurrying table ......");
	}

	/**
	 * 单条件按查询，查询多条记录
	 * 
	 * @param tableName
	 */
	public static Result QueryByCondition2(String tablename, String username)
			throws IOException {
		HTable table = new HTable(configuration, tablename);
		Get g = new Get(username.getBytes());
		Result rs = table.get(g);
		return rs;
	}

	/**
	 * 精准查询
	 */
	public static void selectByFilter(String tablename, List<String> arr)
			throws IOException {
		HTable table = new HTable(configuration, tablename);
		FilterList filterList = new FilterList();
		Scan s1 = new Scan();
		for (String v : arr) { // 各个条件之间是“与”的关系
			String[] s = v.split(",");
			filterList.addFilter(new SingleColumnValueFilter(Bytes
					.toBytes(s[0]), Bytes.toBytes(s[1]), CompareOp.EQUAL, Bytes
					.toBytes(s[2])));
			// 添加下面这一行后，则只返回指定的cell，同一行中的其他cell不返回
			// s1.addColumn(Bytes.toBytes(s[0]), Bytes.toBytes(s[1]));
		}
		s1.setFilter(filterList);
		ResultScanner ResultScannerFilterList = table.getScanner(s1);
		for (Result rr = ResultScannerFilterList.next(); rr != null; rr = ResultScannerFilterList
				.next()) {
			for (KeyValue kv : rr.list()) {
				System.out.println("row : " + new String(kv.getRow()));
				System.out.println("column : " + new String(kv.getFamily()));
				System.out.println("value : " + new String(kv.getValue()));
			}
		}
		System.out.println("end qurrying!");
	}

	/**
	 * 创建表操作
	 * 
	 * @throws IOException
	 */
	public static void createTable(String tablename, String[] cfs)
			throws IOException {
		HBaseAdmin admin = new HBaseAdmin(configuration);
		if (admin.tableExists(tablename)) {
			System.out.println("表已经存在！");
		} else {
			HTableDescriptor tableDesc = new HTableDescriptor(tablename);
			for (int i = 0; i < cfs.length; i++) {
				tableDesc.addFamily(new HColumnDescriptor(cfs[i]));
			}
			admin.createTable(tableDesc);
			System.out.println("表创建成功！");
		}
		System.out.println("end create table ......");
	}

	/**
	 * 删除一张表
	 * 
	 * @param tableName
	 */
	public static void dropTable(String tableName) {
		try {
			HBaseAdmin admin = new HBaseAdmin(configuration);
			admin.disableTable(tableName);
			admin.deleteTable(tableName);
		} catch (MasterNotRunningException e) {
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 根据 rowkey删除一条记录
	 * 
	 * @param tablename
	 * @param rowkey
	 */
	public static void deleteRow(String tablename, String rowkey) {
		try {
			HTable table = new HTable(configuration, tablename);
			List list = new ArrayList();
			Delete d1 = new Delete(rowkey.getBytes());
			list.add(d1);

			table.delete(list);
			System.out.println("删除行成功!");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 组合条件删除
	 * 
	 * @param tablename
	 * @param rowkey
	 */
	public static void deleteByCondition(String tablename, String rowkey) {
		// 目前还没有发现有效的API能够实现 根据非rowkey的条件删除 这个功能能，还有清空表全部数据的API操作

	}

	/**
	 * 单条件查询,根据rowkey查询唯一一条记录
	 * 
	 * @param tableName
	 */
	public static void QueryAll(String tableName) {

		HTablePool pool = new HTablePool(configuration, 1000);
		HTable table = (HTable) pool.getTable(tableName);
		try {
			Get scan = new Get("abcdef".getBytes());// 根据rowkey查询
			Result r = table.get(scan);
			System.out.println("获得到rowkey:" + new String(r.getRow()));
			for (KeyValue keyValue : r.raw()) {
				System.out.println("列：" + new String(keyValue.getFamily())
						+ "====值:" + new String(keyValue.getValue()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 组合条件查询
	 * 
	 * @param tableName
	 */
	public static void QueryByCondition3(String tableName) {

		try {
			HTablePool pool = new HTablePool(configuration, 1000);
			HTable table = (HTable) pool.getTable(tableName);

			List<Filter> filters = new ArrayList<Filter>();

			Filter filter1 = new SingleColumnValueFilter(
					Bytes.toBytes("column1"), null, CompareOp.EQUAL,
					Bytes.toBytes("aaa"));
			filters.add(filter1);

			Filter filter2 = new SingleColumnValueFilter(
					Bytes.toBytes("column2"), null, CompareOp.EQUAL,
					Bytes.toBytes("bbb"));
			filters.add(filter2);

			Filter filter3 = new SingleColumnValueFilter(
					Bytes.toBytes("column3"), null, CompareOp.EQUAL,
					Bytes.toBytes("ccc"));
			filters.add(filter3);

			FilterList filterList1 = new FilterList(filters);

			Scan scan = new Scan();
			scan.setFilter(filterList1);
			ResultScanner rs = table.getScanner(scan);
			for (Result r : rs) {
				System.out.println("获得到rowkey:" + new String(r.getRow()));
				for (KeyValue keyValue : r.raw()) {
					System.out.println("列：" + new String(keyValue.getFamily())
							+ "====值:" + new String(keyValue.getValue()));
				}
			}
			rs.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}