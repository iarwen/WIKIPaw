import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bson.types.ObjectId;

import com.alibaba.fastjson.JSON;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class MongoDBUtil {
	// ------------------------初始化------------连接--------------开始----------------
	private static Properties proties = null;
	public static DB db = null;
	public static GridFS myFS =null;
	static {
		proties = new Properties();
		try {
			proties.load(MongoDBUtil.class.getClassLoader()
					.getResourceAsStream("sysconfig.properties"));
			Mongo mongo = null;
			try {
				String host = proties.getProperty("db.host");
				int port = Integer.parseInt(proties.getProperty("db.port"));
				mongo = new MongoClient(host, port);
			} catch (UnknownHostException e) {
				System.err.println(("error1:数据库连接失败！"));
				e.printStackTrace();
			}
			db = mongo.getDB(proties.getProperty("db.dbname"));
			String username = proties.getProperty("db.username");
			String password = proties.getProperty("db.password");
			// 授权
			db.authenticate(username, password.toCharArray());
			myFS = new GridFS(db);
		} catch (MongoException e) {
			System.err.println(("error2:数据库连接失败！"));
		} catch (NullPointerException e) {
			System.err.println(("error3:sysconfig.properties 文件不存在！"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// ------------------------初始化------------连接--------------结束----------------
	public static <T> void saveList(List<T> list) {
		if (list == null || list.size() == 0) {
			return;
		}
		T o = list.get(0);
		String col = o.getClass().getSimpleName().toLowerCase();
		BasicDBList json = (BasicDBList) transToDBObject(list);
		DBObject[] li = new BasicDBObject[json.size()];
		for (int index = 0; index < json.size(); index++) {
			BasicDBObject bo = (BasicDBObject) json.get(index);
			li[index] = bo;
		}
		db.getCollection(col).insert(li);
	}
	
	public static <T> String save(T o) {
		if (o == null) {
			return null;
		}
		String col = o.getClass().getSimpleName().toLowerCase();
		DBObject json = transToDBObject(o);
		db.getCollection(col).save(json);
		return json.get("_id").toString();
	}

	/**
	 * 用mongoDB的_id来更新<br>
	 * 使用场景：<br>
	 * 从MongoDB中获取到的单条数据， _id不允许修改，其他字段随便修改<br>
	 * 数据库中的值按_id更新<br>
	 * p.s. 如果这条数据被删除 将不会更新成功 也不会被重新插入
	 * 
	 * @return int i 返回更新条数 这里最多更新1条
	 */
	public static <T extends AbstractDBObject> int updateSingleByDBID(T o) {
		if (o == null) {
			return 0;
		}
		if(o.get_id()==null||"".equals(o.get_id())){
			return 0;
		}
		String col = o.getClass().getSimpleName().toLowerCase();

		DBObject newDBObject = transToDBObject(o);
		// 非多条更新 使用_id只能更新一条
		WriteResult wrs = db.getCollection(col).update(
				new BasicDBObject().append("_id", new ObjectId(o.get_id())),
				newDBObject);
		Integer i = new Integer(wrs.getField("n").toString());
		return i;
	}

	/**
	 * 自定义字段更新<br>
	 * 符合条件的全部更新,但只更新指定的字段<br>
	 * 条件值使用对象传过来  条件字段有值即可<br>
	 * 更新值使用对象传过来  更新字段有值即可<br>
	 * ps. 注意：不允许使用_id字段
	 * @param cfields 需要更新的条件字段
	 * @param c 条件值的对象
	 * @param nfields 需要更新的值字段
	 * @param n 更新值的对象
	 * @return 返回更新的条数
	 */
	public static <T extends AbstractDBObject> int updateSpecifyFieldByCondition(
			String[] cfields,T c,String[] nfields,T n) {
		if (cfields == null || cfields.length == 0){
			return 0;
		}
		if (nfields == null || nfields.length == 0){
			return 0;
		}
		String col = n.getClass().getSimpleName().toLowerCase();
		DBObject con= transToDBObject(c);
		
		BasicDBObject consDBObject=new BasicDBObject();
		for(String field:cfields){
			consDBObject.put(field, con.get(field));
		}
		DBObject news=transToDBObject(n);
		BasicDBObject newDBObject=new BasicDBObject();
		for(String field:nfields){
			newDBObject.put(field, news.get(field));
		}
		
		BasicDBObject newbasicDBObject = new BasicDBObject();
		newbasicDBObject.append("$set",newDBObject );

		WriteResult wrs = db.getCollection(col).updateMulti(consDBObject,
				newbasicDBObject);
		Integer i = new Integer(wrs.getField("n").toString());
		return i;
	}
	/**
	 * 使用mongoDB的_id来更新  可能会有性能问题
	 * @param <T>
	 */
	public static <T extends AbstractDBObject> int updateListByDBID(List<T> list) {
		if (list == null || list.size() == 0) {
			return 0;
		}
		int count=0;
		for(T t:list){
			count+=updateSingleByDBID(t);
		}
		return count;
	}
	/**
	 * 使用_id(必须是系统生成的_id)来查找一个对象
	 * @param <T>
	 */
	public static <T extends AbstractDBObject> T findByDBID(T o){
		BasicDBObject ido=new BasicDBObject().append("_id", new ObjectId(o.get_id()));
		String col = o.getClass().getSimpleName().toLowerCase();
		DBObject dbo = db.getCollection(col).findOne(ido);
	
		return 	transDBObjectToJavaObj(dbo,o);
	}
	/**
	 * 查找全部的对象
	 * @param <T>
	 * @param <T>
	 */
	public static <T> List<T>  findAll(T o){
		String col = o.getClass().getSimpleName().toLowerCase();
		DBCursor dbc = db.getCollection(col).find();
		return 	transDBCursorToJavaObjList(dbc,o);
	}
	private static <T> List<T> transDBCursorToJavaObjList(DBCursor dbc , T o){
	
		List<T> list = new ArrayList<T>();
		DBObject dbo=null;
		while(  dbc.hasNext() ){
			dbo=dbc.next();
			JSON json = (JSON)JSON.toJSON(dbo);
			@SuppressWarnings("unchecked")
			T t1 = (T) JSON.toJavaObject(json,o.getClass());
			list.add(t1);
		}
		return list;
	}
	/**
	 *  保存文件到gridfs 不允许空文件名 会帮你自动关闭in的流，请勿再处理
	 * @param in
	 * @param id
	 */
	public static FileSaveEnum saveFile(InputStream in, String fileName,String contentType){
		if(fileName==null||"".equals(fileName)){
			return FileSaveEnum.FILENAMENULL;
		}
		GridFSDBFile gridFSDBFile = getFileByName(fileName);
		if(gridFSDBFile != null)
			return FileSaveEnum.EXIST;
		
		GridFSInputFile gridFSInputFile = myFS.createFile(in,fileName,true);
		gridFSInputFile.setContentType(contentType);
		gridFSInputFile.save();
		return FileSaveEnum.SUCCESS;
	}
	/**
	 *  按文件名获取数据到GridFSDBFile 不允许空文件名
	 * @param fileName
	 */
	public static GridFSDBFile getFileByName(String fileName){
		if(fileName==null||"".equals(fileName)){
			return null;
		}
		DBObject query  = new BasicDBObject("filename", fileName);
		GridFSDBFile gridFSDBFile = myFS.findOne(query);
		return gridFSDBFile;
	}
	/**
	 *  按文件名删除文件
	 * @param fileName
	 */
	public static boolean deleteFileByName(String fileName){
		if(fileName==null||"".equals(fileName)){
			return false;
		}
		myFS.remove(fileName);
		return true;
	}
	// JAVA对象转换成DBObject值
	private static DBObject transToDBObject(Object obj) {
		// fastJson
		String json = JSON.toJSONString(obj);
		// mongoJson
		DBObject dbObject = (DBObject) com.mongodb.util.JSON.parse(json);
		return dbObject;
	}
	// DBObject转换成JAVA对象
	@SuppressWarnings("unchecked")
	private static <T> T transDBObjectToJavaObj(DBObject obj,T t) {
		// fastJson
		JSON json = (JSON)JSON.toJSON(obj);
		T t1 = (T) JSON.toJavaObject(json,t.getClass());
		return t1;
	}
	
}

enum FileSaveEnum{
	EXIST,SUCCESS,FILENAMENULL
}
