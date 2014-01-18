package lucene.textsearch;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.File;
import java.io.IOException;

import lucene.textsearch.business.PDFIndexItem;
import lucene.textsearch.search.Index;
import lucene.textsearch.search.SearchEngine;

public class SearchMain {
  public static void main(String[] args) throws IOException, ParseException {
    // 0. Specify the analyzer for tokenizing text.
    //    The same analyzer should be used for indexing and searching

    Index indexer = new Index();
    File pdfFile = new File("src/resources/SamplePDF.pdf");
    PDFIndexItem pdfIndexItem = extractText(pdfFile);

    indexer.buildIndexes(pdfIndexItem);
    //indexer.closeIndexWriter();
    
    // 2. query
    String querystr = "Hello";

    // 3. search
    SearchEngine searchEngine = new SearchEngine(indexer);
    ScoreDoc[] hits = searchEngine.performSearch(querystr).scoreDocs;
    
    // 4. display results
    System.out.println("Found " + hits.length + " hits.");
    for(int i=0;i<hits.length;++i) {
      int docId = hits[i].doc;
      Document d = searchEngine.getSearcher().doc(docId);
      System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
    }

    // reader can only be closed when there
    // is no need to access the documents any more.
    indexer.closeIndexWriter();
    searchEngine.close();
  }
  
  public static PDFIndexItem extractText(File file) throws IOException {
      PDDocument doc = PDDocument.load(file);
      String content = new PDFTextStripper().getText(doc);
      doc.close();
      return new PDFIndexItem((long)file.getName().hashCode(), file.getName(), content);
  }
}
