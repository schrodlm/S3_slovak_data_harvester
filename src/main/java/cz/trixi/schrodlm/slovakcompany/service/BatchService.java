package cz.trixi.schrodlm.slovakcompany.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        batchFileService.unzipTodaysBatch();
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
