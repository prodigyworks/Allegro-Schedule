package uk.co.allegro.scanner;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.List;

public class Filescan {

    public static void main(String[] args) throws Exception {
        new Filescan().run(new File(args[0]));
    }
    
    public static void main2(String[] args) {
        String charset = "UTF-8";
        File uploadFile1 = new File("e:/Test/PIC1.JPG");
        File uploadFile2 = new File("e:/Test/PIC2.JPG");
        String requestURL = "http://localhost:8080/FileUploadSpringMVC/uploadFile.do";
 
        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
             
            multipart.addHeaderField("User-Agent", "CodeJava");
            multipart.addHeaderField("Test-Header", "Header-Value");
             
            multipart.addFormField("description", "Cool Pictures");
            multipart.addFormField("keywords", "Java,upload,Spring");
             
            multipart.addFilePart("fileUpload", uploadFile1);
            multipart.addFilePart("fileUpload", uploadFile2);
 
            List<String> response = multipart.finish();
             
            System.out.println("SERVER REPLIED:");
             
            for (String line : response) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
    public void run(File rootDir) throws Exception {
        File[] files = rootDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                processInDir(file.getAbsolutePath());
            }
        }
    }

    private static void processInDir(String absolutePath) throws Exception {
        File customerDir = new File(absolutePath + "/IN");
        for (File customerFile : customerDir.listFiles()) {
            if (customerFile.isFile()) {
                processFile(customerFile.getName(), absolutePath);
            }
        }
    }

    private static void processFile(String fileName, String absolutePath) throws Exception {
        uploadFile(fileName, absolutePath);
        moveFile(fileName, absolutePath);
    }

    private static void moveFile(String fileName, String absolutePath) {
    	
        File customerFile = new File(absolutePath + "/IN/" + fileName);
        customerFile.renameTo(new File(absolutePath + "/OUT/" + fileName));
        	
        System.out.println("Moving from " + absolutePath + "/IN/" + fileName + " to " + absolutePath + "/OUT/" + fileName);
    }
    
    private static void uploadFile(String fileName, String absolutePath) throws Exception {
        String charset = "UTF-8";
	    MultipartUtility multipart = new MultipartUtility("http://www.haulageplanner.co.uk/uploadpod.php", charset);
        File uploadFile1 = new File(absolutePath + "/IN/" + fileName);
	    
	    multipart.addHeaderField("User-Agent", "CodeJava");
	    multipart.addHeaderField("Test-Header", "Header-Value");
	     
	    multipart.addFormField("folder", getCustomerIdFromPath(absolutePath));
	    multipart.addFormField("file", fileName);
	     
	    multipart.addFilePart("content", uploadFile1);
	
	    List<String> response = multipart.finish();
	     
        System.out.println("Uploading from: " + getCustomerIdFromPath(absolutePath) + " file :" + absolutePath + "/IN/" + fileName);
	    System.out.println("SERVER REPLIED:");
	     
	    for (String line : response) {
	        System.out.println(line);
	    }
    }

    private static void uploadFile2(String fileName, String absolutePath) throws Exception {
        URL url = new URL("http://www.haulageplanner.co.uk/uploadpod.php");
        URLConnection con = url.openConnection();
        // activate the output
        con.setDoOutput(true);
        PrintStream ps = new PrintStream(con.getOutputStream());
        // send your parameters to your site
        ps.print("folder=" + getCustomerIdFromPath(absolutePath));
        ps.print("&file=" + fileName);
        ps.print("&content=" + getEncodedFile(absolutePath + "/IN/" + fileName));

        // we have to get the input stream in order to actually send the request
        InputStream is = con.getInputStream();
        int cc;
        StringWriter w = new StringWriter();

        while ((cc = is.read()) != -1) {
            w.write(cc);
        }

        System.out.println("Uploading from: " + getCustomerIdFromPath(absolutePath) + " file :" + absolutePath + "/IN/" + fileName + " OUT:" + w.toString());

        // close the print stream
        ps.close();
    }

    private static String getCustomerIdFromPath(String absolutePath) {
        return absolutePath.substring(absolutePath.lastIndexOf("\\") + 1, absolutePath.length());
    }

    private static String getEncodedFile(String absoluteFilePath) throws Exception {
        InputStream is = new FileInputStream(absoluteFilePath);
        int cc;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while ((cc = is.read()) != -1) {
            baos.write(cc);
        }

        baos.close();
        
        System.out.println(Base64.getMimeEncoder().encodeToString(baos.toByteArray()));
        
        return Base64.getMimeEncoder().encodeToString(baos.toByteArray());
    }
}
