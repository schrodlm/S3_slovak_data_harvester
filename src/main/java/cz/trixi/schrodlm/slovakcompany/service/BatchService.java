package cz.trixi.schrodlm.slovakcompany.service;

import cz.trixi.schrodlm.slovakcompany.dao.BatchDao;
import cz.trixi.schrodlm.slovakcompany.model.BatchModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        BatchModel todaysBatch = batchFileService.unzipTodaysBatch();
        persistBatches( todaysBatch );
    }

    public void downloadAndPersistBatchFrom(LocalDate date)
    {
        batchS3Handler.downloadBatchFrom( date );
        BatchModel batch = batchFileService.unzipUpdateBatchForDate(date);
        persistBatches( batch );
    }

    //====================== STATIC METHODS =============================

    /**
     * Generates the batch file name for a given date.
     *
     * The generated file name is of the format "batch-daily/actual_yyyy-MM-dd.json",
     * where "yyyy-MM-dd" corresponds to the provided date. This format is consistent
     * with the key naming convention in file storage.
     *
     * @param date The LocalDate object representing the desired date.
     * @return A string representing the file name for the zipped batch for the given date.
     */
    public static String getBatchNameFrom(LocalDate date){
        // Format the date as "yyyy-MM-dd" so it is formatted according to a key on file storage
        String formattedDate = date.format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) );
        String fileName = "batch-daily/actual_" + formattedDate + ".json";

        return fileName;
    }

    /**
     * Generates the zipped batch file name for a given date.
     */
    public static String getZippedBatchNameFrom(LocalDate date){

        return getBatchNameFrom( date ) + ".gz";
    }

}
