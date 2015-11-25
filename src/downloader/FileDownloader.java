package downloader;

import java.awt.Container;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractListModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import downloadGui.DownloaderGUI;
import downloadGui.MainGUI;

/** Retrieves and downloads the files from the webpage specified by the user.
 * @author Kyle Allen-Taylor
 *
 */
public class FileDownloader {

	private String url;
	private String saveLoc;
	private int numThreads;
	private String filter;
	private MainGUI gui;
	private static Elements links;
	private static Elements imgs;

	/** Retrieves and downloads the files from the webpage specified.
	 * @param url The URL of the webpage that the user wishes to download the files from.
	 * @param saveLoc The location on the user's computer that they wish to download the files to
	 * @param numThreads The number of threads the user wishes to use in the thread pool to download the files.
	 * @param filter The filter words for the file extension of the files that the user wishes to download.
	 * @param downloadGUI The GUI that will be updated, this should be the main GUI.
	 */
	public FileDownloader(String url, String saveLoc, int numThreads,
			String filter, MainGUI downloadGUI) {
		this.url = url;
		this.saveLoc = saveLoc;
		this.numThreads = numThreads;
		this.filter = filter;
		this.gui = downloadGUI;
		imgs = new Elements();
		links = new Elements();

	}

	/** The class that implements the threads run method.
	 * @author Kyle
	 *
	 */
	class Download implements Runnable {

		private Element file;
		private String urlstr;
		private String fileName;
		private boolean isImage;

		/** Constructor initialises variables.
		 * @param file The element that is being used
		 * @param urlstr The full url string that will be used to download
		 * @param fileName The name for the file, where it will be stored.
		 * @param isImage whether or not the file is an image.
		 */
		public Download(Element file, String urlstr, String fileName, boolean isImage) {
			this.file = file;
			this.urlstr = urlstr;
			this.fileName = fileName;
			this.isImage = isImage;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {

			// Open a URL Stream
			URL url;
			try {
				System.out.println("urlstr: " + urlstr);
				url = new URL(urlstr);
				System.out.println("url: " + url.toString());
				System.out.println("fileName: " + fileName.toString());
				
				InputStream in = url.openStream();
				if(isImage){
					DownloaderGUI.setStatus(file.attr("src"), "DOWNLOADING...");
				}
				else{
					DownloaderGUI.setStatus(file.attr("href"), "DOWNLOADING...");
				}
				OutputStream out = new BufferedOutputStream(
						new FileOutputStream(saveLoc + fileName));
				System.out.println(saveLoc + fileName);
				for (int b; (b = in.read()) != -1;) {
					out.write(b);
				}
				if(isImage){
					DownloaderGUI.setStatus(file.attr("src"), "DONE");
				}
				else{
					DownloaderGUI.setStatus(file.attr("href"), "DONE");
					System.out.println(file.attr("href"));
				}
				out.close();
				in.close();

			}catch (FileNotFoundException e1 ) {
				DownloaderGUI.setStatus(file.attr("href"), "Cannot Download");
				e1.printStackTrace();
			}			
			catch (IOException e1 ) {
				DownloaderGUI.setStatus(file.attr("href"), "Cannot Download");
				e1.printStackTrace();
			}
		}
	}

	/** Retrieves the files from the webpage.
	 * @throws IOException
	 */
	public void getFiles() throws IOException {

		Document doc;
		try {
			// connect to the webpage
			doc = Jsoup.connect(url).get();
			// split the filters using | into an array for multiple filter file
			// types.
			String[] filters = filter.split(",");
			// grab all of the images on the webpage with the file extension the
			// user defined.

			for (int i = 0; i < filters.length; i++) {
				Elements images = doc.select("img[src~=(?i)\\.(" + filters[i]
						+ ")]");
				// for each element check if it is already in the list of
				// elements,
				// if not then add it to the collection of elements.
				for (Element element : images) {
					if (!imgs.contains(element)) {
						imgs.add(element);
					}
				}
			}

			// create an array list to store the image src attribute names.
			ArrayList<String> imgName = new ArrayList<>();
			// add the attribute names to the arraylist and convert it to an
			// array.
			for (Element img : imgs) {
				imgName.add(img.attr("src"));
			}

			Object[] imageArray = imgName.toArray();

			// get all links
			// Note: a hyper link object is defined using <a> tag, the the
			// link is
			// defined using attribute [href]
			// We use selector to search elements a hyper link object with
			// attribute
			// [href],

			for (int i = 0; i < filters.length; i++) {
				Elements links = doc.select("a[href$=\"" + filters[i] + "\"]");
				// for each element check if it is already in the list of
				// elements,
				// if not then add it to the collection of elements.
				for (Element element : links) {
					if (!FileDownloader.links.contains(element)) {
						FileDownloader.links.add(element);
					}
				}
			}

			ArrayList<String> linkAL = new ArrayList<>();
			for (Element link : links) {
				linkAL.add(link.attr("href"));
			}

			Object[] linkArray = linkAL.toArray();

			gui.setList(imageArray, linkArray);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Downloads the files passed to it from the webpage onto the user's computer
	 * @param list The list of files to be downloaded, passed from the main GUI as the user's selection of files.
	 * @throws IOException
	 */
	public void download(List<String> list) throws IOException {
		// Step 1: create an a fix thread pool of size that the user
		// specified.
		ExecutorService pool = Executors.newFixedThreadPool(numThreads);

		Document doc = Jsoup.connect(url).get();
		// used to iterate through to add the elements whose href or
		// image source match the list
		Elements imgs = doc.getElementsByTag("img");

		Elements files = new Elements();
		for (Element img : imgs) {
			if (list.contains(img.attr("src"))) {
				files.add(img);
			}
		}

		Elements lnks = doc.getElementsByTag("a");
		lnks.addAll(doc.getElementsByTag("link"));
		System.out.println(lnks);
		for (Element lnk : lnks) {
			if (list.contains(lnk.attr("href"))) {
				files.add(lnk);
			}
		}

		// for each file selected
		for (Element file : files) {
		
			// get the value from href attribute: link.attr("href")
			System.out.println("\nlink : " + file.attr("href"));
			System.out.println("text : " + file.text());
			// if the file is an image then
			if (file.tagName() == "img") {

				String urlstr = file.attr("abs:src");
				System.out.println(urlstr);
//				if (urlstr.indexOf(url) <= 0)
//					urlstr = url + urlstr;
				
				//get the file name by grabbing a substring of the url
				String fileName = urlstr.substring(urlstr.lastIndexOf('/') + 1,
						urlstr.length()).replace("?", "").replace(">", "").replace("<", "");

				Download download = new Download(file, urlstr, fileName, true);
				pool.submit(download);
			}
			// else it is a link or file
			else {
				String urlstr = file.attr("abs:href");
				System.out.println(urlstr);
//				if (urlstr.indexOf(url) <= 0)
//					urlstr = url + urlstr;
				System.out.println(urlstr);

				String fileName = urlstr.substring(urlstr.lastIndexOf('/') + 1,
						urlstr.length()).replace("?", "").replace(">", "").replace("<", "");;
				Download download = new Download(file, urlstr, fileName, false);
				pool.submit(download);

			}
		}


		pool.shutdown();
	}
}
