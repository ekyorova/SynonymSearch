package lucene.textsearch;

import info.bliki.api.Page;
import info.bliki.api.User;
import info.bliki.wiki.model.WikiModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lucene.textsearch.business.MongodbConnector;
import lucene.textsearch.business.PDFIndexItem;
import lucene.textsearch.search.Index;
import lucene.textsearch.search.SearchEngine;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class SearchMain {
	public static void main(String[] args) throws IOException, ParseException {

		// The same analyzer should be used for indexing and searching
		File folder = new File("src/resources");
		File[] listOfFiles = folder.listFiles();
		for (File pdfFile : listOfFiles) {
			Index indexer = new Index();
			PDFIndexItem pdfIndexItem = extractText(pdfFile);
			indexer.buildIndexes(pdfIndexItem);
			indexer.closeIndexWriter();
		}

		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader keyboard = new BufferedReader(in);
		System.out.println("Search: ");
		String querystr = keyboard.readLine();

		MongodbConnector con = new MongodbConnector();
		con.connect();

		List<String> derivatives = new ArrayList<String>();
		if (con.find(querystr)) {
			derivatives = con.getElement(querystr);
		} else {
			String[] der = connectToDict(querystr);
			if (der != null) {
				derivatives = new ArrayList<String>(Arrays.asList(der));
			}
			derivatives.add(querystr);
			String root = querystr;
			List<String> list = derivatives;
			con.insertObject(root, list);
		}

		System.out.println("Found derivatives/synonyms:");
		for (String derivative : derivatives) {
			System.out.print(" " + derivative);
		}

		SearchEngine searchEngine = new SearchEngine();
		searchEngine.performSearch(derivatives);

		searchEngine.close();

	}

	public static PDFIndexItem extractText(File file) throws IOException {
		PDDocument doc = PDDocument.load(file);
		String content = new PDFTextStripper().getText(doc);
		doc.close();
		return new PDFIndexItem((long) file.getName().hashCode(),
				file.getName(), content);
	}

	public static String[] connectToDict(String title) {
		String[] listOfTitleStrings = { title };
		User user = new User("", "", " http://en.wiktionary.org//w/api.php");
		user.login();
		List<Page> listOfPages = user.queryContent(listOfTitleStrings);
		String[] allSynonyms = null;
		for (Page page : listOfPages) {
			WikiModel wikiModel = new WikiModel("${image}", "${title}");
			String pageInfo = wikiModel.renderPDF(page.toString());
			pageInfo.trim();
			pageInfo = pageInfo.replace("\n", "").replace("\r", "");
			allSynonyms = getDerivatives(pageInfo);
			// allSynonyms = getSynonyms(pageInfo);
		}

		return (String[]) ((allSynonyms == null) ? null : allSynonyms);
	}

	private static String[] getDerivatives(String pageInfo) {
		boolean flag = false;
		Pattern pattern = Pattern
				.compile("(?i)(<a id=\"Derived_terms\" name=\"Derived_terms\"></a><h4>Derived terms</h4><ul><li>)(.+?)(</li></ul><a id=)");
		Matcher matcher = pattern.matcher(pageInfo);
		String splitted = null;
		if (matcher.find()) {
			splitted = matcher.group(2);
			if (splitted != null)
				flag = true;
		}
		return (flag ? splitted.split("</li><li>") : null);
	}

	private static String[] getSynonyms(String pageInfo) {
		boolean flag = false;
		Pattern pattern = Pattern
				.compile("(?i)(<a id=\"Synonyms\" name=\"Synonyms\"></a><h4>Synonyms</h4><ul><li>)(.+?)(</li></ul><a id=)");
		Matcher matcher = pattern.matcher(pageInfo);
		String splitted = null;
		if (matcher.find()) {
			splitted = matcher.group(2);
			if (splitted != null)
				flag = true;
		}
		return (flag ? splitted.split("</li><li>") : null);
	}

}
