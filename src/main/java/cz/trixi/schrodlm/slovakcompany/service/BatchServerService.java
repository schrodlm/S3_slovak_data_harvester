package cz.trixi.schrodlm.slovakcompany.service;

import cz.trixi.schrodlm.slovakcompany.dao.BatchDao;
import cz.trixi.schrodlm.slovakcompany.file.FileUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BatchServerService {

    @Value("${xmlDir}")
    public String jsonDir;

    @Autowired
    FileUtility fileUtility;

    @Autowired
    BatchDao batchDao;

    /**
     * Serves the batch file for today as a Resource.
     *
     * @return Returns the file as a Resource object corresponding to today's batch.
     * @throws RuntimeException if there's an error loading today's batch file or if the file doesn't exist.
     */
    public Resource serveTodaysBatch() {

        String todaysBatch = BatchService.getTodaysBatchName();
        Path todaysBatchPath = Paths.get( jsonDir ).resolve( todaysBatch ).normalize();

        return fileUtility.serveFile( todaysBatchPath );
    }

    /**
     * Serve all batches available in the database as a list of Resources
     */
    public List<Resource> serveAllBatches() {

        List<Path> paths = batchDao.getAllBatches();
        List<Resource> resources = new ArrayList<>();

        for ( Path path : paths ) {
            Resource toAdd = fileUtility.serveFile( path );
            resources.add( toAdd );
        }

        return resources;
    }

    /**
     *
     * @param date
     * @return
     */
    public List<Resource> serveBatchesSince( LocalDate date )
    {
        List<Path> paths = batchDao.getBatchesSince(date);
        List<Resource> resources = new ArrayList<>();

        for(Path path : paths) {
            Resource toAdd = fileUtility.serveFile( path );
            resources.add(toAdd);
        }

        return resources;
    }
}
