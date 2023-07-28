package cz.trixi.schrodlm.slovakcompany.service;

import cz.trixi.schrodlm.slovakcompany.model.BatchModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BatchService {

    @Autowired
    BatchS3Handler batchS3Handler;

    @Autowired
    BatchFileService batchFileService;

    public void persistAllBatches() {

    }

    public void persistDailyBatch() {

    }


    /**
     * Initialization - download all init and daily batches and unzippes them
     */
    public void downloadAndUnzipAllBatches() {
        batchS3Handler.downloadAllBatches();
        batchFileService.unzipAllBatches();
    }


    public void initialSetup() {
        //downloads all available batches from the cloud
        batchS3Handler.downloadAllBatches();

        //unzips all these batches
        List<BatchModel> unzippedBatches = batchFileService.unzipAllBatches();

        //persist these batches
        persistBatches(unzippedBatches);
    }

    public void persistBatches(List<BatchModel> batches)
    {

        BatchModel batchModel;
    }

    /**
     * Downloads today's update file, which contains
     */
    public void dailyUpdate() {
        batchS3Handler.downloadTodaysBatch();
        batchFileService.unzipTodaysBatch();
    }

    public static String getTodaysBatchZippedName() {
        // Format the date as "yyyy-MM-dd" so it is formatted according to a key on file storage
        String formattedDate = LocalDateTime.now().format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) );
        String fileName = "todays_batch_" + formattedDate + ".json.gz";

        return fileName;
    }


    public static String getTodaysBatchName() {
        // Format the date as "yyyy-MM-dd" so it is formatted according to a key on file storage
        String formattedDate = LocalDateTime.now().format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) );
        String fileName = "todays_batch_" + formattedDate + ".json";

        return fileName;
    }

}
