package cz.trixi.schrodlm.slovakcompany.service;

import cz.trixi.schrodlm.slovakcompany.file.FileUtility;
import cz.trixi.schrodlm.slovakcompany.model.BatchModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class BatchFileService {

    @Value("${zipDir}")
    public String zipDir;

    @Value("${xmlDir}")
    public String xmlDir;

    @Autowired
    FileUtility fileUtility;

    /**
     * Unzips all initial batch files located in the 'batch-init' subdirectory and saves them to the corresponding 'batch-init'
     * subdirectory in the XML directory.
     *
     * @return A list of pairs where each pair consists of the unzipped batch file's name and the unzipped file's path.
     */
    public List<BatchModel> unzipInitBatches() {
        return fileUtility.deepUnzipBatchDirectory(
                new File( Paths.get( zipDir ).resolve( "batch-init" ).toUri() ),
                new File( Paths.get( xmlDir ).resolve( "batch-init" ).toUri() ) );
    }

    /**
     * Unzips all daily batch files located in the 'batch-daily' subdirectory
     * and saves them to the corresponding 'batch-daily' subdirectory in the XML directory.
     *
     * @return A list of pairs where each pair consists of the unzipped daily batch file's name and the unzipped file's path.
     */
    public List<BatchModel> unzipDailyBatches() {
        return fileUtility.deepUnzipBatchDirectory(
                new File( Paths.get( zipDir ).resolve( "batch-daily" ).toUri() ),
                new File( Paths.get( xmlDir ).resolve( "batch-daily" ).toUri() ) );
    }

    /**
     * Unzips the batch file corresponding to today's date.
     * The method uses the BatchService to determine the name of today's zipped batch file
     * and then unzips it to the main XML directory.
     *
     * @return A pair consisting of today's unzipped batch file name and its unzipped file path.
     * @throws RuntimeException If there's an error during the unzipping process.
     */
    public BatchModel unzipTodaysBatch() {

        String todaysBatchName = BatchService.getTodaysBatchZippedName();

        try {
            return fileUtility.unzipBatch(
                    new File( Paths.get( zipDir ).resolve( todaysBatchName ).toUri() ),
                    new File( Paths.get( xmlDir ).toUri() ) );
        }
        catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public List<BatchModel> unzipAllBatches() {
        List<BatchModel> unzippedBatches = new ArrayList<>();

        unzippedBatches.addAll(unzipInitBatches());
        unzippedBatches.addAll(unzipDailyBatches());

        return unzippedBatches;
    }

}
