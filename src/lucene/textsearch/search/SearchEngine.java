package lucene.textsearch.search;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.util.Version;

public class SearchEngine {

	private IndexSearcher searcher = null;
	private QueryParser parser = null;
	private IndexReader reader= null;

	/** Creates a new instance of SearchEngine */
	public SearchEngine(Index index) throws IOException {
		reader = IndexReader.open(index.getIndexWriter(), true);
		searcher = new IndexSearcher(reader);
		// the "title" arg specifies the default field to use
	    // when no field is explicitly specified in the query.
		parser = new QueryParser(Version.LUCENE_40, "title",
				new StandardAnalyzer(Version.LUCENE_40));
	}

	public TopDocs performSearch(String queryString) throws IOException,
			ParseException {
		int hitsPerPage = 10;
		Query query = parser.parse(queryString);
	    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
	    searcher.search(query, collector);
	    return collector.topDocs();
	}

	public IndexSearcher getSearcher() {
		return searcher;
	}
	

	public void close() {
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
