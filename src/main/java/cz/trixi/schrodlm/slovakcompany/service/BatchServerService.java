package cz.trixi.schrodlm.slovakcompany.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class BatchServerService {

    @Value("${xmlDir}")
    public String jsonDir;

    public Resource serveTodaysBatch() {

        String todaysBatch = BatchService.getTodaysBatchName();
        Path todaysBatchPath = Paths.get( jsonDir ).resolve( todaysBatch).normalize();
        Resource resource;

        try{
            resource = new UrlResource( todaysBatchPath.toUri() );
            if(!resource.exists()){
                throw new Exception("File not found:" + todaysBatch);
            }
        } catch(Exception ex){
            throw new RuntimeException("File loading error", ex);
        }
        return resource;
    }

}
