package uk.co.allegro.scanner;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

public class Scanner {

	public static void main(String[] args) throws Exception {
		new Scanner().run();
	}
	
	public void run() throws Exception {
	    // open a connection to the site
	    URL url = new URL("http://www.prodigyworks.org/test/test.php");
	    URLConnection con = url.openConnection();
	    // activate the output
	    con.setDoOutput(true);
	    PrintStream ps = new PrintStream(con.getOutputStream());
	    // send your parameters to your site
	    ps.print("folder=firstValue");
	    ps.print("&secondKey=secondValue");
	 
	    // we have to get the input stream in order to actually send the request
	    InputStream is = con.getInputStream();
	    int cc;
	    StringWriter w = new StringWriter();
	    
	    while ((cc = is.read()) != -1) {
	    	w.write(cc);
	    }
	    
	    System.out.println("STRING:" + w.toString());
	 
	    // close the print stream
	    ps.close();
	}
}
