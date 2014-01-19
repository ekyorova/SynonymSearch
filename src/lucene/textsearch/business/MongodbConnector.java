package lucene.textsearch.business;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongodbConnector {

	final static String DATABASENAME = "myderivatives";
	MongoClient mongoClient = null;

	public void connect() {
		try {
			if (mongoClient == null) {
				mongoClient = new MongoClient("localhost", 27017);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public DB getDatabase() {
		return mongoClient.getDB(DATABASENAME);
	}

	public DBCollection getCollection() {
		return getDatabase().getCollection(DATABASENAME);
	}

	public DBObject getObject() {
		return getCollection().findOne();
	}
	
	public void insertObject(String key, List<String> searchWords){
		BasicDBObject doc = new BasicDBObject(key, searchWords);
		getCollection().insert(doc);
	}

	public boolean find(String querystr) {
		return !isAllNulls(getElement(querystr));
	}

	public List<String> getElement(String querystr) {
		List<String> result = new ArrayList<String>();
		BasicDBObject query = new BasicDBObject();
		BasicDBObject field = new BasicDBObject();
		field.put(querystr, 1);
		DBCursor cursor = getCollection().find(query,field);
		while (cursor.hasNext()) {
		    BasicDBObject obj = (BasicDBObject) cursor.next();
		    if(obj.getString(querystr)!= null){
		    	result.addAll((Collection<? extends String>) obj.get(querystr));
		    }
		}
		return result;
	}
	
	private boolean isAllNulls(Iterable<?> array) {
	    for (Object element : array)
	        if (element != null) return false;
	    return true;
	}

}
