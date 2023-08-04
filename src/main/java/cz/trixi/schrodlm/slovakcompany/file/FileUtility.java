package cz.trixi.schrodlm.slovakcompany.file;

import cz.trixi.schrodlm.slovakcompany.model.BatchModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

@Service
public class FileUtility {

    Logger log = LoggerFactory.getLogger( getClass() );

    private static final int BUFFER_SIZE = 4096;

    /**
     * Recursively unzips GZIP batches from a source directory (and its subdirectories) to a destination directory.
     *
     * @param sourceDir The source directory containing GZIP files.
     * @param destDir The destination directory where unzipped files will be saved.
     * @return Information about the unzipped batches
     * @throws RuntimeException If the source path is not a directory or if there's an error during unzipping.
     */
    public List<BatchModel> deepUnzipBatchDirectory( File sourceDir, File destDir ) {
        if ( !sourceDir.isDirectory() )
            throw new RuntimeException( "Unable to unzip since path is not a directory" );

        //Create list of unzipped files and their file system locations
        List<BatchModel> unzippedFiles = new ArrayList<>();

        for ( File file : sourceDir.listFiles() ) {
            if ( file.isDirectory() ) {
                deepUnzipBatchDirectory( file, destDir );
                continue;
            }
            //files should only be GZIP
            if ( !isGZIP( file ) ) {
                log.warn( "File " + file.getName() + " is not a GZIP file, skipping it..." );
                continue;
            }
            try {
                BatchModel unzippedBatchInfo = unzipBatch( file, destDir );
                unzippedFiles.add( unzippedBatchInfo );
            }
            catch ( IOException e ) {
                throw new RuntimeException( e );
            }
        }

        return unzippedFiles;
    }

    /**
     * Checks if file is a GZIP by reading the "magic bytes", in GZIP it should be hex signature of "1F 8B"
     *
     */
    public boolean isGZIP( File file ) {
        try (FileInputStream fis = new FileInputStream( file.getPath() )) {
            byte[] buffer = new byte[2];
            int bytesRead = fis.read( buffer );
            if ( buffer[0] == ( byte ) 0x1F && buffer[1] == ( byte ) 0x8B )
                return true;
        }
        catch ( IOException e ) {
            System.err.println( "An error occurred while reading the file: " + e.getMessage() );
        }
        return false;
    }

    /**
     * Recursively unzips all content of the zipped batch and saves its structure (paths of directories and files)
     *
     *
     * @param zippedBatch - zipped file
     * @param destDirectory - destination directory
     * @return information about the unzipped batch
     */
    public BatchModel unzipBatch( File zippedBatch, File destDirectory ) throws IOException {


        String unzippedBatchName = getUnzippedFileName( zippedBatch.getName() );
        LocalDate exportDate = parseDateFromBatchName( unzippedBatchName );
        Path batchPath = Path.of( destDirectory + File.separator + unzippedBatchName );

        try {
            GZIPInputStream gis = new GZIPInputStream( new FileInputStream( zippedBatch.getPath() ) );
            FileOutputStream fos = new FileOutputStream( batchPath.toString() );

            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ( ( len = gis.read( buffer ) ) != -1 ) {
                fos.write( buffer, 0, len );
            }

            log.info( "File " + zippedBatch.getName() + " successfully unzipped" );

            //closing resources
            fos.close();
            gis.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }

        return new BatchModel( unzippedBatchName, exportDate, batchPath );
    }

    /**
     * Extracts export date from the name of the batch
     */
    private LocalDate parseDateFromBatchName(String batchName){
        // Use regex to extract date
        Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");
        Matcher matcher = pattern.matcher(batchName);

        if (matcher.find()) {
            // Parse the matched date string to LocalDate
            LocalDate date = LocalDate.parse(matcher.group(1));
            return date;
        }

        throw new IllegalArgumentException("No date found in the filename.");
    }

    /**
     * Serves file as a Resource from a given file path
     *
     * @param filePath The path to the file to be served as a resource.
     * @return Returns the file as a `UrlResource` object if it exists
     */
    public Resource serveFile( Path filePath ) {

        Resource resource;

        try {
            resource = new UrlResource( filePath.toUri() );
            if ( !resource.exists() ) {
                throw new Exception( "File not found:" + filePath );
            }
        }
        catch ( Exception ex ) {
            throw new RuntimeException( "File loading error", ex );
        }

        return resource;
    }

    /**
     * @param directory - deletes directory content
     */
    public void deleteDirectoryContent( File directory ) {
        //directory is empty
        if ( directory.listFiles() == null ) {
            return;
        }

        for ( final File fileEntry : directory.listFiles() ) {
            if ( fileEntry.isDirectory() ) {
                deleteDirectoryContent( fileEntry );
            }
            else {
                fileEntry.delete();
            }
        }
        directory.delete();

    }

    public static String getUnzippedFileName(String zippedFileName){
        if(zippedFileName.endsWith( ".gz" )){
            return zippedFileName.substring( 0, zippedFileName.length() - 3 );
        }
        throw new IllegalArgumentException("Provided file name does not have a .gz extension: " + zippedFileName);
    }
}



