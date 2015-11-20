package downloader;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractListModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import downloadGui.DownloadGUI;

public class FileDownloader {

	private String url;
	private String saveLoc;
	private int numThreads;
	private String filter;

	public FileDownloader(String url, String saveLoc, int numThreads,
			String filter) {
		this.url = url;
		this.saveLoc = saveLoc;
		this.numThreads = numThreads;
		this.filter = filter;
	}

	class Download implements Runnable {

		private Element file;
		private String urlstr;
		private String fileName;

		public Download(Element file, String urlstr, String fileName) {
			this.file = file;
			this.urlstr = urlstr;
			this.fileName = fileName;
		}

		public void run() {

			// Open a URL Stream
			URL url;
			try {
				System.out.println(Thread.currentThread().getName().toString()
						+ ": " + Thread.currentThread().getState().toString());

				url = new URL(urlstr);

				InputStream in = url.openStream();
				OutputStream out = new BufferedOutputStream(
						new FileOutputStream(saveLoc + fileName));
				System.out.println(saveLoc + fileName);
				for (int b; (b = in.read()) != -1;) {
					out.write(b);
				}
				out.close();
				in.close();
				System.out.println(Thread.currentThread().getName().toString()
						+ ": " + Thread.currentThread().getState().toString());

			} catch (IOException e1) {
				e1.printStackTrace();

			}
		}
	}

	public void download() throws IOException {

		Document doc;
		try {
			//connect to the webpage
			doc = Jsoup.connect(url).get();
			//grab all of the images on the webpage with the file extension the user defined.
			Elements imgs = doc.select("img[src~=(?i)\\.(" + filter + ")]");
			// get all links
			// Note: a hyper link object is defined using <a> tag, the the
			// link is
			// defined using attribute [href]
			// We use selector to search elements a hyper link object with
			// attribute
			// [href],
			Elements links = doc.select("a[href$=\"" + filter + "\"]");
			

			ExecutorService pool = Executors.newFixedThreadPool(numThreads);

			//for each link 
			for (Element link : links) {
				System.out.println(Thread.currentThread().getName().toString()
						+ ": " + Thread.currentThread().getState().toString());
				// get the value from href attribute: link.attr("href")
				System.out.println("\nlink : " + link.attr("href"));
				System.out.println("text : " + link.text());

			}
			
			for (Element img : imgs) {
				System.out.println(Thread.currentThread().getName().toString()
						+ ": " + Thread.currentThread().getState().toString());
				String urlstr = img.attr("src");
				System.out.println(urlstr);
				if (urlstr.indexOf(url) <= 0)
					urlstr = url + urlstr;
				System.out.println(urlstr);

				String fileName = urlstr.substring(urlstr.lastIndexOf('/') + 1,
						urlstr.length());

				// Step 1: create an a fix thread pool of size that the user
				// specified.
				
				Download download = new Download(img, urlstr, fileName);
				pool.submit(download);



			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
