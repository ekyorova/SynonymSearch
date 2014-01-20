package lucene.textsearch.search;

import java.io.File;
import java.io.IOException;
import java.util.List;

import lucene.textsearch.business.PDFIndexItem;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.GradientFormatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SearchEngine {

	private IndexSearcher searcher = null;
	private QueryParser parser = null;
	private IndexReader reader = null;
	private final StandardAnalyzer analyzer = new StandardAnalyzer(
			Version.LUCENE_46);

	/** Creates a new instance of SearchEngine */
	public SearchEngine() throws IOException {
		reader = IndexReader
				.open(FSDirectory.open(new File("index-directory")));
		searcher = new IndexSearcher(reader);
		// the "title" arg specifies the default field to use
		// when no field is explicitly specified in the query.
		parser = new QueryParser(Version.LUCENE_46, PDFIndexItem.CONTENT,
				analyzer);
	}

	public void performSearch(List<String> queryString) throws IOException,
			ParseException {
		int hitsPerPage = 10;
		ScoreDoc[] topDocuments = null;
		for (String str : queryString) {
			Query query = parser.parse(str);
			TopScoreDocCollector collector = TopScoreDocCollector.create(
					hitsPerPage, true);
			searcher.search(query, collector);
			topDocuments = collector.topDocs().scoreDocs;

			System.out.println("Found " + topDocuments.length + " hits.");
			for (int i = 0; i < topDocuments.length; ++i) {
				int docId = topDocuments[i].doc;
				Document d = searcher.doc(docId);
				String fileName = d.get(PDFIndexItem.TITLE);
				String content = d.get(PDFIndexItem.CONTENT);
				String fragments = null;
				try {
					fragments = getHighlightedField(query,
							PDFIndexItem.CONTENT, content);
				} catch (InvalidTokenOffsetsException e) {
					e.printStackTrace();
				}
				System.out.println((i + 1) + " " + d.get(PDFIndexItem.TITLE)
						+ "\n" + fragments);
			}
		}
	}

	public IndexSearcher getSearcher() {
		return searcher;
	}

	private String getHighlightedField(Query query, String fieldName,
			String fieldValue) throws IOException, InvalidTokenOffsetsException {
		QueryScorer queryScorer = new QueryScorer(query);
		Formatter formatter = new GradientFormatter(
				queryScorer.getMaxTermWeight(), null, null, null, null);
		Highlighter highlighter = new Highlighter(formatter, queryScorer);
		highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer,
				Integer.MAX_VALUE));
		highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
		return highlighter
				.getBestFragment(this.analyzer, fieldName, fieldValue);
	}

	public void close() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
