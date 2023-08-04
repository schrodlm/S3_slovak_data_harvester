package cz.trixi.schrodlm.slovakcompany.controller;

import cz.trixi.schrodlm.slovakcompany.service.BatchServerService;
import cz.trixi.schrodlm.slovakcompany.service.BatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class BatchController {

    @Autowired
    BatchService batchService;

    @Autowired
    BatchServerService batchServerService;

    Logger log = LoggerFactory.getLogger( getClass() );
    /**
     * Downloads batch corresponding to today's date
     */
    @GetMapping("/downloadTodaysBatch")
    public ResponseEntity<Resource> serveTodaysBatch() {
        Resource resource = batchServerService.serveTodaysBatch();

        return ResponseEntity.ok()
                .contentType( MediaType.APPLICATION_JSON )
                .header( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"" )
                .body( resource );
    }

    /**
     * Downloads all batches available from the database
     */
    @GetMapping("/downloadAllBatches")
    public ResponseEntity<ByteArrayResource> serveAllBatches() throws IOException {
        List<Resource> resources = batchServerService.serveAllBatches();

        File zipFile = File.createTempFile( "batches", ".zip" );

        try (ZipOutputStream zipOut = new ZipOutputStream( new FileOutputStream( zipFile ) )) {
            for ( Resource resource : resources ) {
                ZipEntry zipEntry = new ZipEntry( Objects.requireNonNull( resource.getFilename() ) );
                zipOut.putNextEntry( zipEntry );

                InputStream in = resource.getInputStream();
                byte[] buffer = new byte[2048];

                int len;
                while((len = in.read(buffer)) > 0){
                    zipOut.write( buffer, 0, len );
                }
                in.close();
            }
        }

        byte [] zipContent = Files.readAllBytes(zipFile.toPath());
        ByteArrayResource byteArrayResource = new ByteArrayResource( zipContent );

        // Clean up the temporary zip file
        Files.delete( zipFile.toPath() );

        return ResponseEntity.ok(  )
                .contentType( MediaType.APPLICATION_JSON )
                .header( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = batches.zip" )
                .body( byteArrayResource );

    }

    /**
     *  It extracts the 'dateStr' path variable to retrieve batches that have been created since the provided date.
     * @param dateStr - string of date in format "d-M-yyyy"
     */
    @GetMapping("/downloadBatchesSince/{dateStr}")
    public ResponseEntity<ByteArrayResource> serveBatchesSince(@PathVariable String dateStr ) throws IOException {
        log.info( "Starting to download all batches added since {}...", dateStr );
        LocalDate localDate = LocalDate.parse(dateStr,DateTimeFormatter.ofPattern( "d-M-yyyy" ));
        List<Resource> resources = batchServerService.serveBatchesSince( localDate );

        File zipFile = File.createTempFile( "batches", ".zip" );

        try (ZipOutputStream zipOut = new ZipOutputStream( new FileOutputStream( zipFile ) )) {
            for ( Resource resource : resources ) {
                ZipEntry zipEntry = new ZipEntry( Objects.requireNonNull( resource.getFilename() ) );
                zipOut.putNextEntry( zipEntry );

                InputStream in = resource.getInputStream();
                byte[] buffer = new byte[2048];

                int len;
                while((len = in.read(buffer)) > 0){
                    zipOut.write( buffer, 0, len );
                }
                in.close();
            }
        }

        byte [] zipContent = Files.readAllBytes(zipFile.toPath());
        ByteArrayResource byteArrayResource = new ByteArrayResource( zipContent );

        // Clean up the temporary zip file
        Files.delete( zipFile.toPath() );

        return ResponseEntity.ok(  )
                .contentType( MediaType.APPLICATION_JSON )
                .header( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = batches.zip" )
                .body( byteArrayResource );
    }

    /**
     * Daily update downloads today's batch from SRPO and persists it
     */
    @GetMapping("/dailyUpdate")
    public void dailyUpdate()
    {
        log.info( "Starting daily update..." );
        batchService.dailyUpdate();
    }

    /**
     * Gets a daily batch from a specific date and persists it
     * @param dateStr
     */
    @GetMapping("/setBatch/{dateStr}")
    public void setBatch(@PathVariable String dateStr)
    {
        log.info( "Getting a batch from date: {}", dateStr );
        LocalDate date = LocalDate.parse(dateStr,DateTimeFormatter.ofPattern( "d-M-yyyy" ));

        batchService.downloadAndPersistBatchFrom( date );

    }



}
