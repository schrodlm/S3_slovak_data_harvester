package cz.trixi.schrodlm.slovakcompany.controller;

import cz.trixi.schrodlm.slovakcompany.service.BatchServerService;
import cz.trixi.schrodlm.slovakcompany.service.BatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import java.util.concurrent.CompletableFuture;
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
     * Starts a process of downloading, unzipping, and persisting all available batches
     * from Slovakian register.
     */
    @GetMapping("/init")
    public void initialSetup() {
        batchService.initialSetup();
    }

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
     * Asynchronously serves all batches available from the database
     */
    @GetMapping("/downloadAllBatches")
    public CompletableFuture<ResponseEntity<ByteArrayResource>> serveAllBatches(){

        log.info( "----==============================----" );
        log.info( "Downloading all batches... (runs on a custom thread)" );

        return CompletableFuture.supplyAsync( () -> {
            ByteArrayResource byteArrayResource = batchServerService.serveAllBatches();
            log.info( "All batches served..." );
            return ResponseEntity.ok()
                    .contentType( MediaType.APPLICATION_OCTET_STREAM )
                    .header( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = batches.zip" )
                    .body( byteArrayResource );
        }).exceptionally( ex -> {
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR ).
                    body( null );
        });

    }

    /**
     * Asynchronously extracts the 'dateStr' path variable to retrieve batches that have been created since the provided date.
     *
     * @param dateStr - string of date in format "d-M-yyyy"
     */
    @GetMapping("/downloadBatchesSince/{dateStr}")
    public CompletableFuture<ResponseEntity<ByteArrayResource>> serveBatchesSince( @PathVariable String dateStr ){

        log.info( "----==============================----" );
        log.info( "Starting to download all batches added since {}... (runs on a custom thread)", dateStr );

        return CompletableFuture.supplyAsync( () -> {
            ByteArrayResource byteArrayResource = batchServerService.serveBatchesSince( dateStr );
            log.info( "All batches served..." );
            return ResponseEntity.ok()
                    .contentType( MediaType.APPLICATION_OCTET_STREAM )
                    .header( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = batches.zip" )
                    .body( byteArrayResource );
        }).exceptionally( ex -> {
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR ).
                    body( null );
        } );

    }

    /**
     * Daily update downloads today's batch from SRPO and persists it.
     */
    @GetMapping("/dailyUpdate")
    public void dailyUpdate() {
        log.info( "Starting daily update..." );
        batchService.dailyUpdate();
        log.info( "Daily update was successful!" );
    }

    /**
     * Downloads and persists a batch based on the provided date.
     *
     * @param dateStr The date string in the format "d-M-yyyy"
     * indicating the batch's date. e.g., "5-1-2023" for January 5, 2023.
     */
    @GetMapping("/saveBatch/{dateStr}")
    public void downloadBatchForDate( @PathVariable String dateStr ) {
        log.info( "Getting a batch from date: {}", dateStr );
        LocalDate date = LocalDate.parse( dateStr, DateTimeFormatter.ofPattern( "d-M-yyyy" ) );

        batchService.downloadAndPersistUpdateBatchForDate( date );
        log.info( "Save was successful!" );
    }

    @GetMapping("/health")
    public String health() {
        return "Ještě žiju...";
    }


}
