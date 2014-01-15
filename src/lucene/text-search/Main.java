/*
 * Main.java
 *
 * Created on 6 March 2006, 11:51
 *
 */

package lucene.demo;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import lucene.demo.search.*;
import lucene.demo.business.*;

/**
 *
 * @author John
 */
public class Main {

	/** Creates a new instance of Main */
	public Main() {
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {

		try {
			// build a lucene index
			System.out.println("rebuildIndexes");
			buildIndex();
			System.out.println("rebuildIndexes done");

			// perform search on "Notre Dame museum"
			// and retrieve the result
			System.out.println("performSearch");
			SearchEngine instance = new SearchEngine();
			IndexReader reader = 
					IndexReader.open((FSDirectory.open(new File("index-directory"))));
			IndexSearcher searcher 
		        = new IndexSearcher(reader);
			TopDocs hits = instance.performSearch("Notre-Dame");

			System.out.println("Results found: " + hits.totalHits);
			ScoreDoc[] res = hits.scoreDocs;
	        for (ScoreDoc hit : res) {
	            Document doc = searcher.doc(hit.doc);
	            System.out.printf("%5.3f %sn",
	                              hit.score, doc.get("text"));
	        }
			System.out.println("performSearch done");
		} catch (Exception e) {
			System.out.println("Exception caught.\n");
		}
	}

	public static void buildIndex() throws IOException {
		IndexWriter indexWriter
		= new IndexWriter(FSDirectory.open(new File("index-directory")),
				new IndexWriterConfig(
						Version.LUCENE_40, new StandardAnalyzer(Version.LUCENE_40)));
		String[] texts = new String[] {"hello world",
				"hello sailor",
		"goodnight moon" };
		for (String text : texts) {
			Document doc = new Document();
			doc.add(new Field("text",text,
					Field.Store.YES,Field.Index.ANALYZED));
			indexWriter.addDocument(doc);
		}
		indexWriter.close();
	}
	
	public static void searchIndex(String[] queryStrings) 
		    throws IOException, ParseException {
			IndexReader reader = 
					IndexReader.open((FSDirectory.open(new File("index-directory"))));
			IndexSearcher searcher 
		        = new IndexSearcher(reader);
		    QueryParser parser = new QueryParser(Version.LUCENE_40,
		    		"text",new StandardAnalyzer(Version.LUCENE_40));
		    for (String queryString : queryStrings) {
		        System.out.println("nsearching for: " + queryString);
		        Query query = parser.parse(queryString);
		        TopDocs results = searcher.search(query,10);
		        System.out.println("total hits: " + results.totalHits);
		        ScoreDoc[] hits = results.scoreDocs;
		        for (ScoreDoc hit : hits) {
		            Document doc = searcher.doc(hit.doc);
		            System.out.printf("%5.3f %sn",
		                              hit.score, doc.get("text"));
		        }
		    }
		}

}
