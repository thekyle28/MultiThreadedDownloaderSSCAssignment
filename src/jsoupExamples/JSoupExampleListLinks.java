package jsoupExamples;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;

import javax.swing.JOptionPane;

public class JSoupExampleListLinks {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Document doc;
		try {
			String queryValue = JOptionPane.showInputDialog(
			"Enter what you want to search:");
			if ((queryValue != null) && (queryValue.length() > 0)) {
				String SearchStr = "http://search.yahoo.com/search?p=" + URLEncoder.encode(queryValue, "UTF-8" );
				// need http protocol
				doc = Jsoup.connect(SearchStr).get();

				// get page title
				String title = doc.title();
				System.out.println("title : " + title);

				// get all links
				// Note: a hyper link object is defined using <a> tag, the the link is defined using attribute [href]   
				// We use selector to search elements a hyper link object with attribute [href],
				Elements links = doc.select("a[href]");
				for (Element link : links) {

					// get the value from href attribute: link.attr("href")
					System.out.println("\nlink : " + link.attr("href"));
					System.out.println("text : " + link.text());

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}


	}

}
