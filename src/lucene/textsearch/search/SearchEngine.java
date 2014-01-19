package lucene.textsearch.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lucene.textsearch.business.PDFIndexItem;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SearchEngine {

	private IndexSearcher searcher = null;
	private QueryParser parser = null;
	private IndexReader reader = null;

	/** Creates a new instance of SearchEngine */
	public SearchEngine() throws IOException {
		reader = IndexReader
				.open(FSDirectory.open(new File("index-directory")));
		searcher = new IndexSearcher(reader);
		// the "title" arg specifies the default field to use
		// when no field is explicitly specified in the query.
		parser = new QueryParser(Version.LUCENE_46, PDFIndexItem.CONTENT,
				new StandardAnalyzer(Version.LUCENE_46));
	}

	public List<ScoreDoc[]> performSearch(List<String> queryString)
			throws IOException, ParseException {
		int hitsPerPage = 10;
		List<ScoreDoc[]> topDocuments = new ArrayList<ScoreDoc[]>();
		for (String str : queryString) {
			Query query = parser.parse(str);
			TopScoreDocCollector collector = TopScoreDocCollector.create(
					hitsPerPage, true);
			searcher.search(query, collector);
			topDocuments.add(collector.topDocs().scoreDocs);
		}
		;
		return topDocuments;
	}

	public IndexSearcher getSearcher() {
		return searcher;
	}

	public void close() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
