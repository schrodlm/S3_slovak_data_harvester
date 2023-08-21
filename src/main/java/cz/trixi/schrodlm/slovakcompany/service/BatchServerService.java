package cz.trixi.schrodlm.slovakcompany.service;

import cz.trixi.schrodlm.slovakcompany.dao.BatchDao;
import cz.trixi.schrodlm.slovakcompany.file.FileUtility;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class BatchServerService {

    @Value("${unzippedDir}")
    public String jsonDir;

    @Autowired
    FileUtility fileUtility;

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

        return fileUtility.serveFile( todaysBatchPath );
    }

    /**
     * Serve all batches available in the database as a byte array resource
     */
    public ByteArrayResource serveAllBatches() throws IOException {

        List<Path> paths = batchDao.getAllBatches();
        List<Resource> resources = fileUtility.serveFiles( paths );

        log.info( "{} batches retrieved, preparing ZIP file...", resources.size() );
        ByteArrayResource zippedFile = prepareZipResources( resources );
        return zippedFile;
    }

    /**
     * Serves batches added since provided date as a byte array resource
     */
    public ByteArrayResource serveBatchesSince( String dateStr ) throws IOException {

        //date parsing
        LocalDate date = LocalDate.parse( dateStr, DateTimeFormatter.ofPattern( "d-M-yyyy" ) );

        List<Path> paths = batchDao.getBatchesSince( date );
        List<Resource> resources = fileUtility.serveFiles( paths );
        log.info( "{} batches retrieved, preparing ZIP file...", resources.size() );
        ByteArrayResource zippedFile = prepareZipResources( resources );

        return zippedFile;
    }

    public ByteArrayResource prepareZipResources( List<Resource> resources ) throws IOException {
        File zipFile = File.createTempFile( "batches", ".zip" );

        int cnt = 0;
        try (ZipOutputStream zipOut = new ZipOutputStream( new FileOutputStream( zipFile ) )) {
            for ( Resource resource : resources ) {
                ZipEntry zipEntry = new ZipEntry( Objects.requireNonNull( resource.getFilename() ) );
                zipOut.putNextEntry( zipEntry );

                InputStream in = resource.getInputStream();
                byte[] buffer = new byte[2048];

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
