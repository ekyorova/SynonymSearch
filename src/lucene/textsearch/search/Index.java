package lucene.textsearch.search;

import java.io.File;
import java.io.IOException;

import lucene.textsearch.business.PDFIndexItem;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Index {

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
		IndexWriter writer = getIndexWriter();
		writer.deleteDocuments(new Term(PDFIndexItem.ID, indexItem.getId()
				.toString()));

		Document doc = new Document();

		doc.add(new StringField(PDFIndexItem.ID, indexItem.getId().toString(),
				Field.Store.YES));
		doc.add(new TextField(PDFIndexItem.TITLE, indexItem.getTitle(),
				Field.Store.YES));
		FieldType type = new FieldType();
		type.setIndexed(true);
		type.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		type.setStored(true);
		type.setStoreTermVectors(true);
		type.setTokenized(true);
		type.setStoreTermVectorOffsets(true);
		doc.add(new TextField(PDFIndexItem.CONTENT, indexItem.getContent(),
				Field.Store.YES));
		doc.add(new Field(PDFIndexItem.NCONTENT, indexItem.getContent(), type));
		writer.addDocument(doc);

	}

	public void buildIndexes(PDFIndexItem indexItem) throws IOException {
		getIndexWriter();
		indexDocuments(indexItem);
	}
}