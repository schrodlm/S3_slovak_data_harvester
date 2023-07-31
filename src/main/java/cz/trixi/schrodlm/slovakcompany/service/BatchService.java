package cz.trixi.schrodlm.slovakcompany.service;

import cz.trixi.schrodlm.slovakcompany.dao.BatchDao;
import cz.trixi.schrodlm.slovakcompany.model.BatchModel;
import jakarta.annotation.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BatchService {

    @Autowired
    BatchS3Handler batchS3Handler;

    @Autowired
    BatchFileService batchFileService;

    @Autowired
    BatchDao batchDao;

    /**
     * Initialization - download all init and daily batches and unzips them
     */
    public void downloadAndUnzipAllBatches() {
        batchS3Handler.downloadAllBatches();
        batchFileService.unzipAllBatches();
    }

    /**
     * Initializes the system by downloading, unzipping, and persisting all available batches.
     *
     * 1. Downloads all batches from SRPO. 2. Unzips these batches. 3. Persists the unzipped batches to the database.
     */
    public void initialSetup() {

        batchS3Handler.downloadAllBatches();
        List<BatchModel> unzippedBatches = batchFileService.unzipAllBatches();
        persistBatches( unzippedBatches );
    }

    /**
     * Persists a list of batch models in the database.
     *
     * @param batches - The list of batch models to be persisted.
     */
    public void persistBatches( List<BatchModel> batches ) {
        batchDao.batchInsert( batches );
    }

    /**
     * Persists a single batch model in the database.
     *
     * @param batch - The batch model to be persisted.
     */
    public void persistBatches( BatchModel batch ) {
        batchDao.insert( batch );
    }

    /**
     * Downloads today's update batch and unzips it
     */
    public void dailyUpdate() {
        batchS3Handler.downloadTodaysBatch();
        batchFileService.unzipTodaysBatch();
    }


    //====================== STATIC METHODS =============================

    /**
     * Generates the zipped file name for today's batch based on the current date.
     * The format is "todays_batch_YYYY-MM-DD.json.gz".
     */
    public static String getTodaysBatchZippedName() {
        // Format the date as "yyyy-MM-dd" so it is formatted according to a key on file storage
        String formattedDate = LocalDateTime.now().format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) );
        String fileName = "todays_batch_" + formattedDate + ".json.gz";

        return fileName;
    }


    /**
     * Generates the file name for today's batch based on the current date.
     * The format is "todays_batch_YYYY-MM-DD.json".
     */
    public static String getTodaysBatchName() {
        // Format the date as "yyyy-MM-dd" so it is formatted according to a key on file storage
        String formattedDate = LocalDateTime.now().format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) );
        String fileName = "todays_batch_" + formattedDate + ".json";

        return fileName;
    }

}
