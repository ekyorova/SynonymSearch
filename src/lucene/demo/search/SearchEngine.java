/*
 * SearchEngine.java
 *
 * Created on 6 March 2006, 14:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package lucene.demo.search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.TopDocs;
import lucene.demo.business.Hotel;
import lucene.demo.business.HotelDatabase;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author John
 */
public class SearchEngine {
    private IndexSearcher searcher = null;
    private QueryParser parser = null;
    
    /** Creates a new instance of SearchEngine */
    public SearchEngine() throws IOException {
    	IndexReader reader = 
				IndexReader.open((FSDirectory.open(new File("index-directory"))));
		searcher 
	        = new IndexSearcher(reader);
	    parser = new QueryParser(Version.LUCENE_40,
	    		"text",new StandardAnalyzer(Version.LUCENE_40));
    }
    
    public TopDocs performSearch(String queryString)
    throws IOException, ParseException {
    	 Query query = parser.parse(queryString);
	     TopDocs results = searcher.search(query,10);
	     System.out.println("total hits: " + results.totalHits);
	     return results;
    }
}
