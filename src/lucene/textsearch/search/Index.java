package lucene.textsearch.search;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
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
					Version.LUCENE_40, new StandardAnalyzer(Version.LUCENE_40)));
		}
		return indexWriter;
	}

	public void closeIndexWriter() throws IOException {
		if (indexWriter != null) {
			indexWriter.close();
		}
	}

	public void indexDocuments() throws IOException {

		System.out.println("Indexing documents.. ");
		IndexWriter writer = getIndexWriter();
		addDoc(writer, "Lucene in Action", "193398817");
		addDoc(writer, "Lucene for Dummies", "55320055Z");
		addDoc(writer, "Managing Gigabytes", "55063554A");
		addDoc(writer, "The Art of Computer Science", "9900333X");

	}

	public void addDoc(IndexWriter w, String title, String isbn) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("title", title, Field.Store.YES));
		// use a string field for isbn because we don't want it tokenized
		doc.add(new StringField("isbn", isbn, Field.Store.YES));
		w.addDocument(doc);
	}

	public void rebuildIndexes() throws IOException {
		//
		// Erase existing index
		//
		getIndexWriter();
		//
		// Index all Accommodation entries
		//
		indexDocuments();
		//
		// Don't forget to close the index writer when done
		//
	}
}
