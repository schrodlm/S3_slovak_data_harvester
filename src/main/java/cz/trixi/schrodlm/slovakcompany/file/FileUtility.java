package cz.trixi.schrodlm.slovakcompany.file;


import cz.trixi.schrodlm.slovakcompany.model.CompanyMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

@Service
public class FileUtility {

    Logger log = LoggerFactory.getLogger( getClass() );

    final private String batchMetaDataLink = "https://frkqbrydxwdp.compat.objectstorage.eu-frankfurt-1.oraclecloud.com/susr-rpo";

    private static final int BUFFER_SIZE = 4096;


    public void unzipDirectory(File sourceDir, File destDir)
    {
        if(!sourceDir.isDirectory()) throw new RuntimeException("Unable to unzip since path is not a directory");

        for( File file : sourceDir.listFiles())
        {
            //files should only be GZIP
            if(!isGZIP(file)){
             log.warn( "File " + file.getName() + " is not a GZIP file, skipping it..." );
             continue;
            }

            try {
                unzipGZIPFile(file,destDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Checks if file is a GZIP by reading the "magic bytes", in GZIP it should be hex signature of "1F 8B"
     * @param file - file to check
     * @return
     */
    public boolean isGZIP(File file)
    {
        try(FileInputStream fis = new FileInputStream(file.getPath()))
        {
            byte[] buffer = new byte[2];
            int bytesRead = fis.read(buffer);
            if(buffer[0] == (byte)0x1F && buffer[1] == (byte) 0x8B) return true;
        }
        catch(IOException e)
        {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
        }
        return false;
    }

    /**
     * Recursively unzips all content of the zipped file and saves its structure (paths of directories and files)
     *
     * @param zipFile   - zipped file
     * @param destDirectory - destination directory
     */
    public void unzipGZIPFile(File zipFile, File destDirectory) throws IOException {

        System.out.println("Unzipping file...");

        try{
        GZIPInputStream gis = new GZIPInputStream(new FileInputStream(zipFile.getPath()));
        FileOutputStream fos = new FileOutputStream(destDirectory + File.separator + zipFile.getName().replace("json.gz", "json"));

        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        while ((len = gis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }

        System.out.println("File " + zipFile.getName() + " successfully unzipped");

        //closing resources
        fos.close();
        gis.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

    /**
     * Extracts single provided file from a zipped input stream
     *
     * @param zipIn    - zip input stream
     * @param filePath
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    /**
     *
     * @param directory - deletes directory content
     */
    public void deleteDirectoryContent(File directory) {
        //directory is empty
        if (directory.listFiles() == null) {
            return;
        }

        for (final File fileEntry : directory.listFiles()) {
            if (fileEntry.isDirectory()) {
                deleteDirectoryContent(fileEntry);
            } else {
                fileEntry.delete();
            }
        }
        directory.delete();

    }
}



