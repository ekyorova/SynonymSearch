package lucene.textsearch.search;

import java.io.File;
import java.io.IOException;

import lucene.textsearch.business.PDFIndexItem;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class Index {

	/** Creates a new instance of Indexer */
	public Index() {
	}

	private IndexWriter indexWriter = null;

	public IndexWriter getIndexWriter() throws IOException {
		if (indexWriter == null) {
			indexWriter = new IndexWriter(FSDirectory.open(new File(
					"index-directory")), new IndexWriterConfig(
					Version.LUCENE_46, new StandardAnalyzer(Version.LUCENE_46)));
		}
		return indexWriter;
	}

	public void closeIndexWriter() throws IOException {
		if (indexWriter != null) {
			indexWriter.close();
		}
	}

	public void indexDocuments(PDFIndexItem indexItem) throws IOException {
		System.out.println("Indexing documents.. ");
		IndexWriter writer = getIndexWriter();
		writer.deleteDocuments(new Term(PDFIndexItem.ID, indexItem.getId()
				.toString()));

		Document doc = new Document();

		doc.add(new StringField(PDFIndexItem.ID, indexItem.getId().toString(),
				Field.Store.YES));
		doc.add(new TextField(PDFIndexItem.TITLE, indexItem.getTitle(),
				Field.Store.YES));
		doc.add(new TextField(PDFIndexItem.CONTENT, indexItem.getContent(),
				Field.Store.YES));

		// add the document to the index
		writer.addDocument(doc);

	}

	public void addDoc(IndexWriter w, String title, String isbn)
			throws IOException {
		Document doc = new Document();
		doc.add(new TextField("title", title, Field.Store.YES));
		// use a string field for isbn because we don't want it tokenized
		doc.add(new StringField("isbn", isbn, Field.Store.YES));
		w.addDocument(doc);
	}

	public void buildIndexes(PDFIndexItem indexItem) throws IOException {
		//
		// Erase existing index
		//
		getIndexWriter();
		//
		// Index all Accommodation entries
		//
		indexDocuments(indexItem);
		//
		// Don't forget to close the index writer when done
		//
	}
}
