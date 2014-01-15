/*
 * Indexer.java
 *
 * Created on 6 March 2006, 13:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package lucene.demo.search;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import lucene.demo.business.Hotel;
import lucene.demo.business.HotelDatabase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author John
 */
public class Indexer {
    
    /** Creates a new instance of Indexer */
    public Indexer() {
    }
 
    private IndexWriter indexWriter = null;
    
    public IndexWriter getIndexWriter(boolean create) throws IOException {
        if (indexWriter == null) {
        	indexWriter
    		= new IndexWriter(FSDirectory.open(new File("index-directory")),
    				new IndexWriterConfig(
    						Version.LUCENE_40, new StandardAnalyzer(Version.LUCENE_40)));
        }
        return indexWriter;
   }    
   
    public void closeIndexWriter() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
   }
    
    public void indexHotel(Hotel hotel) throws IOException {

        System.out.println("Indexing hotel: " + hotel);
        IndexWriter writer = getIndexWriter(false);
        Document doc = new Document();
        doc.add(new Field("id", hotel.getId(), Field.Store.YES, Field.Index.NO));
        doc.add(new Field("name", hotel.getName(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("city", hotel.getCity(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("description", hotel.getDescription(), Field.Store.YES, Field.Index.ANALYZED));
        String fullSearchableText = hotel.getName() + " " + hotel.getCity() + " " + hotel.getDescription();
        doc.add(new Field("content", fullSearchableText, Field.Store.NO, Field.Index.NOT_ANALYZED));
        writer.addDocument(doc);
    }   
    
    public void rebuildIndexes() throws IOException {
          //
          // Erase existing index
          //
          getIndexWriter(true);
          //
          // Index all Accommodation entries
          //
          Hotel[] hotels = HotelDatabase.getHotels();
          for(Hotel hotel : hotels) {
              indexHotel(hotel);              
          }
          //
          // Don't forget to close the index writer when done
          //
          closeIndexWriter();
     }    
    
  
    
    
}
