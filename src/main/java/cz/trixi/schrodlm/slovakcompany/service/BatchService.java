package cz.trixi.schrodlm.slovakcompany.service;

import cz.trixi.schrodlm.slovakcompany.dao.BatchDao;
import cz.trixi.schrodlm.slovakcompany.model.BatchModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BatchService {

    @Autowired
    BatchS3Handler batchS3Handler;

    @Autowired
    BatchFileService batchFileService;

    @Autowired
    BatchDao batchDao;

    @Autowired
    BatchServerService batchServerService;

    Logger log = LoggerFactory.getLogger( getClass() );


    /**
     * Initializes the system by downloading, unzipping, and persisting all available batches.
     *
     * 1. Downloads all batches from SRPO.
     * 2. Unzips these batches.
     * 3. Persists the unzipped batches to the database.
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
        try {
            batchDao.batchInsert( batches );
        }
        catch ( DataIntegrityViolationException e ) {
            log.warn(e.getMessage());
        }
    }

    /**
     * Persists a single batch model in the database.
     *
     * @param batch - The batch model to be persisted.
     */
    public void persistBatches( BatchModel batch ) {
        log.info( "Persisting {} batch...", batch.batchName() );
        batchDao.insert( batch );


    }

    /**
     * Downloads today's update batch and unzips it
     */
    public void dailyUpdate() {
        batchS3Handler.downloadTodaysBatch();
        try {
            BatchModel todaysBatch = batchFileService.unzipTodaysBatch();
            persistBatches( todaysBatch );
        }
        catch ( IllegalStateException|DataIntegrityViolationException e) {
            log.error( "Error while trying to unzip today's batch", e);
        }
    }

    /**
     * Download update batch from specific date.
     * @param date
     */
    public void downloadAndPersistUpdateBatchForDate(LocalDate date)
    {
        batchS3Handler.downloadBatchFrom( date );
        BatchModel batch = batchFileService.unzipUpdateBatchForDate(date);
        persistBatches( batch );
    }

}
