package cz.trixi.schrodlm.slovakcompany.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CompanyService {

    @Autowired
    CompanyS3Handler companyS3Handler;

    @Autowired
    CompanyFileService companyFileService;

    public void persistAllBatches() {

    }

    public void persistDailyBatch() {

    }


    /**
     * Initialization - download all init and daily batches and unzippes them
     */
    public void downloadAndUnzipAllBatches() {
        companyS3Handler.downloadAllBatches();
        companyFileService.unzipAllBatches();
        companyFileService.unzipTodaysBatch();
    }

    /**
     * Downloads today's update file, which contains
     */
    public void dailyUpdate() {
        companyS3Handler.downloadTodaysBatch();
        companyFileService.unzipTodaysBatch();
    }

    public static String getTodaysBatchName() {
        // Format the date as "yyyy-MM-dd" so it is formatted according to a key on file storage
        String formattedDate = LocalDateTime.now().format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) );
        String fileName = "todays_batch_" + formattedDate + ".json.gz";

        return fileName;
    }

}
