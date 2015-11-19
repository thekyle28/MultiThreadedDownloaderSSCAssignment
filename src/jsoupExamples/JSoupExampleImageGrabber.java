package jsoupExamples;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class JSoupExampleImageGrabber {

	/**
	 * @param args
	 */

	private void writeFile(InputStream is, OutputStream os) throws IOException {
	    byte[] buf = new byte[512]; // optimize the size of buffer to your need
	    int num;
	    while ((num = is.read(buf)) != -1) {
	      os.write(buf, 0, num);
	    }
	}
	
	public static void main(String[] args) {
		Document doc;
		try {
			String webaddress = "http://www.cs.bham.ac.uk";
			String folderPath = "D:\\Pictures/Saved Pictures/";
			//get all images
			doc = Jsoup.connect(webaddress).get();
			// selector uses CSS selector with regular expression
			Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
			for (Element image : images) {
				String urlstr = image.attr("src");
				System.out.println(urlstr);
				if(urlstr.indexOf(webaddress)<=0)
					urlstr = webaddress + urlstr;
				System.out.println(urlstr);

				String fileName = urlstr.substring( urlstr.lastIndexOf('/')+1, urlstr.length() );
				System.out.println(fileName);
				
				 //Open a URL Stream
				URL url = new URL(urlstr);
				InputStream in = url.openStream();
				OutputStream out = new BufferedOutputStream(new FileOutputStream( folderPath+ fileName));
				System.out.println(folderPath+fileName);
				for (int b; (b = in.read()) != -1;) {
				out.write(b);
				}
				out.close();
				in.close();


	 
			}
	 
		} catch (IOException e) {
			e.printStackTrace();
		}
       
	}

}
