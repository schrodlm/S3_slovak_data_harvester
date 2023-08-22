package cz.trixi.schrodlm.slovakcompany.service;

import cz.trixi.schrodlm.slovakcompany.dao.BatchDao;
import cz.trixi.schrodlm.slovakcompany.file.BatchFileUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class BatchServerService {

    @Value("${unzippedDir}")
    public String jsonDir;

    @Autowired
    BatchFileUtility batchFileUtility;

    @Autowired
    BatchDao batchDao;

    Logger log = LoggerFactory.getLogger( getClass() );

    /**
     * Serves the batch file for today as a Resource.
     *
     * @return Returns the file as a Resource object corresponding to today's batch.
     * @throws RuntimeException if there's an error loading today's batch file or if the file doesn't exist.
     */
    public Resource serveTodaysBatch() {

        String todaysBatch = BatchFileService.getBatchNameFrom( LocalDate.now() );
        Path todaysBatchPath = Paths.get( jsonDir ).resolve( todaysBatch ).normalize();

        return batchFileUtility.serveFile( todaysBatchPath );
    }

    /**
     * Serve all batches available in the database as a byte array resource
     */
    public ByteArrayResource serveAllBatches() {

        List<Path> paths = batchDao.getAllBatches();
        return getByteArrayResource( paths );
    }

    /**
     * Serves batches added since provided date as a byte array resource
     */
    public ByteArrayResource serveBatchesSince( String dateStr ) {

        //date parsing
        LocalDate date = LocalDate.parse( dateStr, DateTimeFormatter.ofPattern( "d-M-yyyy" ) );

        List<Path> paths = batchDao.getBatchesSince( date );
        return getByteArrayResource( paths );
    }

    /**
     * Converts a list of file paths into a single ByteArrayResource.
     * This method first retrieves the resources for each path, then creates
     * a ZIP file containing those resources. If there is any issue during
     * the zipping process, it logs the error and returns null.
     *
     * @param paths The list of file paths to be zipped.
     * @return A ByteArrayResource containing the zipped content or null if there's an error.
     */
    private ByteArrayResource getByteArrayResource( List<Path> paths ) {
        List<Resource> resources = batchFileUtility.serveFiles( paths );
        log.info( "{} batches retrieved, preparing ZIP file...", resources.size() );
        try {
            return prepareZipResources( resources );
        }
        catch ( IOException e ) {
            log.error( "There was an error while serving batches", e );
            return null;
        }
    }

    /**
     * Creates a ZIP file from a list of resources. Each resource corresponds
     * to a file that will be added as an entry in the ZIP file. The method logs
     * the progress of adding entries to the ZIP. After zipping is complete,
     * the temporary ZIP file used during the process is deleted.
     *
     * @param resources The list of resources to be zipped.
     * @return A ByteArrayResource containing the zipped content of the resources.
     * @throws IOException If there's an error during the zipping process or file operations.
     */
    public ByteArrayResource prepareZipResources( List<Resource> resources ) throws IOException {
        File zipFile = File.createTempFile( "batches", ".zip" );

        int cnt = 0;
        try (ZipOutputStream zipOut = new ZipOutputStream( new FileOutputStream( zipFile ) )) {
            for ( Resource resource : resources ) {
                ZipEntry zipEntry = new ZipEntry( Objects.requireNonNull( resource.getFilename() ) );
                zipOut.putNextEntry( zipEntry );

                InputStream in = resource.getInputStream();
                byte[] buffer = new byte[4096];

                int len;
                while ( ( len = in.read( buffer ) ) > 0 ) {
                    zipOut.write( buffer, 0, len );
                }
                in.close();

                log.info( "Prepared {}/{} batches", ++cnt, resources.size() );
            }
        }

        byte[] zipContent = Files.readAllBytes( zipFile.toPath() );
        ByteArrayResource byteArrayResource = new ByteArrayResource( zipContent );

        // Clean up the temporary zip file
        Files.delete( zipFile.toPath() );

        return byteArrayResource;
    }
}
