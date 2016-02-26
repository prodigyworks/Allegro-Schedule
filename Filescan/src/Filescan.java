import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Base64;

public class Filescan {

    public static void main(String[] args) throws Exception {
        new Filescan().run(new File(args[0]));
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

    private static void moveFile(String fileName, String absolutePath) throws Exception {
        // renameTo fails on windows...
        //File customerFile = new File(absolutePath + "/IN/" + fileName);
        //customerFile.renameTo(new File(absolutePath + "/OUT/" + fileName));
        Files.move(new File(absolutePath + "/IN/" + fileName).toPath(),
                   new File(absolutePath + "/OUT/" + fileName).toPath(),
                   java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    private static void uploadFile(String fileName, String absolutePath) throws Exception {
        URL url = new URL("http://www.prodigyworks.org/test/test.php");
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

        //System.out.println("STRING:" + w.toString());

        // close the print stream
        ps.close();
    }

    private static String getCustomerIdFromPath(String absolutePath) {
        return absolutePath.substring(absolutePath.lastIndexOf("/") + 1, absolutePath.length());
    }

    private static String getEncodedFile(String absoluteFilePath) throws Exception {
        InputStream is = new FileInputStream(absoluteFilePath);
        int cc;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while ((cc = is.read()) != -1) {
            baos.write(cc);
        }

        baos.close();

        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
