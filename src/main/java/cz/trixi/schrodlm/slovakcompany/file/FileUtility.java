package cz.trixi.schrodlm.slovakcompany.file;


import cz.trixi.schrodlm.slovakcompany.Info;
import cz.trixi.schrodlm.slovakcompany.model.BatchMetadata;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtility {
    public FileUtility() {
    }


    private static final int BUFFER_SIZE = 4096;

    /**
     * Download a file with a provided link and saves it to the file out
     *
     * @param link - data URL
     * @param out  - file which data will be downloaded into
     */


    public void download(String link, File out) throws IOException {
        try {

            URL url = new URL(link);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            double fileSize = (double) http.getContentLengthLong();
            BufferedInputStream in = new BufferedInputStream(http.getInputStream());
            FileOutputStream fos = new FileOutputStream(out);
            BufferedOutputStream bout = new BufferedOutputStream(fos, BUFFER_SIZE);
            byte[] buffer = new byte[BUFFER_SIZE];
            double downloaded = 0.00;
            int read = 0;
            double percentDownloaded = 0.00;

            int iteration = 0;
            while ((read = in.read(buffer, 0, BUFFER_SIZE)) >= 0) {
                bout.write(buffer, 0, read);
                downloaded += read;
                percentDownloaded = (downloaded * 100) / fileSize;
                if (iteration++ % 15 == 0) {
                    String percent = String.format("%.1f", percentDownloaded);
                    System.out.println("Downloaded " + percent + "% of a file");
                }

            }
            bout.close();
            in.close();
            System.out.println("Download completed.");


        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Download XML file that contains Slovakian Companies Register batches from the last 45 days.
     * That includes init_batches that contain all of registered slovakian companies and also daily update batches that contain
     * new companies registered on specific day
     * @param out
     * @throws IOException
     */
    public void downloadSlovakRegister(File out) throws IOException {
        download(Info.batchMetaDataLink, out);
    }

    /**
     * Download all batches from the provided collection. Every BatchMetadata class should contain download link.
     * It will store them in provided directory
     * @param batchMetadataCollection
     * @param destDir
     * @throws IOException
     */
    public void downloadBatchCollection(Collection<BatchMetadata> batchMetadataCollection, File destDir) throws IOException
    {

        for( BatchMetadata batch : batchMetadataCollection)
        {
            File batchFile = new File(destDir.getPath() + "/" + batch.link.replace("/","-"));
            if(batchFile.exists()) continue;
            download(Info.batchMetaDataLink + "/" +  batch.link, batchFile);
        }
    }

    public void unzipDirectory(File dir)
    {
        if(!dir.isDirectory()) throw new RuntimeException("Unable to unzip since path is not a directory");

        for( File file : dir.listFiles())
        {
            //files should only be GZIP
            if(!isGZIP(file)) throw new RuntimeException("File in provided directory is not a GZIP file");
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
     * @param zipFilePath   - file path to the zipped file
     * @param destDirectory - destination direction
     */
    public void unzip(String zipFilePath, String destDirectory) throws IOException {

        System.out.println("Unzipping downloaded file...");

        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();

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
                continue;
            } else {
                fileEntry.delete();
            }
        }
        directory.delete();

    }
}



