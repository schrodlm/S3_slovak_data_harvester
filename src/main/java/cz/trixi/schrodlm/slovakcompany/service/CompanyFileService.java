package cz.trixi.schrodlm.slovakcompany.service;

import cz.trixi.schrodlm.slovakcompany.file.FileUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CompanyFileService {

    @Value("${zipDir}")
    public String zipDir;

    @Value("${xmlDir}")
    public String xmlDir;

    @Autowired
    FileUtility fileUtility;

    public void unzipInitBatches() {
        fileUtility.unzipDirectory(
                new File( Paths.get( zipDir ).resolve( "batch-init" ).toUri() ),
                new File( Paths.get( xmlDir ).resolve( "batch-init" ).toUri() ) );
    }

    public void unzipDailyBatches() {
        fileUtility.unzipDirectory(
                new File( Paths.get( zipDir ).resolve( "batch-daily" ).toUri() ),
                new File( Paths.get( xmlDir ).resolve( "batch-daily" ).toUri() ) );
    }

    public void unzipTodaysBatch() {

        String todaysBatchName = getTodaysBatchName();

        try {
            fileUtility.unzipGZIPFile(
                    new File( Paths.get( zipDir ).resolve( todaysBatchName ).toUri() ),
                    new File( Paths.get( xmlDir ).toUri() ) );
        }
        catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public static String getTodaysBatchName() {
        // Format the date as "yyyy-MM-dd" so it is formatted according to a key on file storage
        String formattedDate = LocalDateTime.now().format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) );
        String fileName = "todays_batch_" + formattedDate + ".json.gz";

        return fileName;
    }

    public void unzipAllBatches() {
        unzipInitBatches();
        unzipDailyBatches();
    }

}