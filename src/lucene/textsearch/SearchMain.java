package lucene.textsearch;

import info.bliki.api.Page;
import info.bliki.api.User;
import info.bliki.wiki.model.WikiModel;
import info.bliki.wiki.tags.TableOfContentTag;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lucene.textsearch.business.PDFIndexItem;
import lucene.textsearch.search.Index;
import lucene.textsearch.search.SearchEngine;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.json.JSONArray;
import org.json.JSONObject;

import fr.idm.sk.publish.api.client.light.SkPublishAPI;

public class SearchMain {
	public static void main(String[] args) throws IOException, ParseException {
		
		
		connectToDict();
		// The same analyzer should be used for indexing and searching
		/*File folder = new File("src/resources");
		File[] listOfFiles = folder.listFiles();
		for (File pdfFile : listOfFiles) {
			Index indexer = new Index();
			PDFIndexItem pdfIndexItem = extractText(pdfFile);

			indexer.buildIndexes(pdfIndexItem);
			indexer.closeIndexWriter();
		}

		String querystr = "Hello";

		SearchEngine searchEngine = new SearchEngine();
		ScoreDoc[] hits = searchEngine.performSearch(querystr).scoreDocs;

		System.out.println("Found " + hits.length + " hits.");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searchEngine.getSearcher().doc(docId);
			System.out.println((i + 1) + " " + d.get(PDFIndexItem.TITLE) + " "
					+ d.get(PDFIndexItem.CONTENT));
		}

		searchEngine.close();
*/
	}
	
	

	public static PDFIndexItem extractText(File file) throws IOException {
		PDDocument doc = PDDocument.load(file);
		String content = new PDFTextStripper().getText(doc);
		doc.close();
		return new PDFIndexItem((long) file.getName().hashCode(),
				file.getName(), content);
	}
	
	public static void connectToDict() {
		String[] listOfTitleStrings = { "resurrection" };
		User user = new User("", "", " http://en.wiktionary.org//w/api.php");
		user.login();
		List<Page> listOfPages = user.queryContent(listOfTitleStrings);
		for (Page page : listOfPages) {
		  WikiModel wikiModel = new WikiModel("${image}", "${title}");
		  String pageInfo = wikiModel.renderPDF(page.toString());
		  
		  System.out.println(pageInfo);
		  pageInfo.trim();
		  pageInfo = pageInfo.replace("\n", "").replace("\r", "");
			  String[] allSynonyms = getDerivatives(pageInfo);
			  System.out.println(allSynonyms);
			  
		}
    }

	private static String[] getDerivatives(String pageInfo) {
		Pattern pattern = Pattern.compile("(?i)(<a id=\"Derived_terms\" name=\"Derived_terms\"></a><h4>Derived terms</h4><ul><li>)(.+?)(</li></ul><a id=\"Antonyms\" name=\"Antonyms\"></a><h4>Antonyms</h4>)");
		Matcher matcher = pattern.matcher(pageInfo);
		String splitted = null;
			if (matcher.find()) {
			   splitted = matcher.group(2);
			}
		return splitted.split("</li><li>");
	}
	
}
