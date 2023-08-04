package cz.trixi.schrodlm.slovakcompany.service;

import cz.trixi.schrodlm.slovakcompany.file.FileUtility;
import cz.trixi.schrodlm.slovakcompany.model.BatchModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BatchFileService {

    @Value("${zipDir}")
    public String zipDir;

    @Value("${unzippedDir}")
    public String unzippedDir;

    @Autowired
    FileUtility fileUtility;

    Logger log = LoggerFactory.getLogger( getClass() );

    /**
     * Unzips all initial batch files located in the 'batch-init' subdirectory and saves them to the corresponding 'batch-init'
     * subdirectory in the XML directory.
     *
     * @return A list of pairs where each pair consists of the unzipped batch file's name and the unzipped file's path.
     */
    public List<BatchModel> unzipInitBatches() {
        return fileUtility.deepUnzipBatchDirectory(
                new File( Paths.get( zipDir ).resolve( "batch-init" ).toUri() ),
                new File( Paths.get( unzippedDir ).resolve( "batch-init" ).toUri() ) );
    }

    /**
     * Unzips all daily batch files located in the 'batch-daily' subdirectory and saves them to the corresponding 'batch-daily'
     * subdirectory in the XML directory.
     *
     * @return A list of pairs where each pair consists of the unzipped daily batch file's name and the unzipped file's path.
     */
    public List<BatchModel> unzipDailyBatches() {
        return fileUtility.deepUnzipBatchDirectory(
                new File( Paths.get( zipDir ).resolve( "batch-daily" ).toUri() ),
                new File( Paths.get( unzippedDir ).resolve( "batch-daily" ).toUri() ) );
    }

    public List<BatchModel> unzipAllBatches() {
        List<BatchModel> unzippedBatches = new ArrayList<>();

        unzippedBatches.addAll( unzipInitBatches() );
        unzippedBatches.addAll( unzipDailyBatches() );

        return unzippedBatches;
    }

    /**
     * Unzips the batch file corresponding to specified date. The method uses the BatchService to determine the name of today's
     * zipped batch file and then unzips it to the main XML directory.
     *
     * @return A pair consisting of today's unzipped batch file name and its unzipped file path.
     * @throws RuntimeException If there's an error during the unzipping process.
     */
    public BatchModel unzipUpdateBatchForDate( LocalDate date ) {
        log.info( "Unzipping {} batch...", date.toString() );

        String batchName = getZippedBatchNameFrom( date );

        if ( Files.exists( Paths.get(unzippedDir).resolve( FileUtility.getUnzippedFileName( batchName )) ) ) {
            throw new IllegalStateException("Unzipped file " + batchName + " already exists.");
        }

        try {
            return fileUtility.unzipBatch(
                    new File( Paths.get( zipDir ).resolve( batchName ).toUri() ),
                    new File( Paths.get( unzippedDir ).resolve("batch-daily").toUri() )
            );
        }
        catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public BatchModel unzipTodaysBatch() {
        return unzipUpdateBatchForDate( LocalDate.now() );
    }

    //====================== STATIC METHODS =============================

    /**
     * Generates the batch file name for a given date.
     *
     * The generated file name is of the format "batch-daily/actual_yyyy-MM-dd.json", where "yyyy-MM-dd" corresponds to the
     * provided date. This format is consistent with the key naming convention in file storage.
     *
     * @param date The LocalDate object representing the desired date.
     * @return A string representing the file name for the zipped batch for the given date.
     */
    public static String getBatchNameFrom( LocalDate date ) {
        // Format the date as "yyyy-MM-dd" so it is formatted according to a key on file storage
        String formattedDate = date.format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) );
        String fileName = "batch-daily/actual_" + formattedDate + ".json";

        return fileName;
    }

    /**
     * Generates the zipped batch file name for a given date.
     */
    public static String getZippedBatchNameFrom( LocalDate date ) {

        return getBatchNameFrom( date ) + ".gz";
    }



}
