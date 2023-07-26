package cz.trixi.schrodlm.slovakcompany.controller;

import cz.trixi.schrodlm.slovakcompany.service.BatchServerService;
import cz.trixi.schrodlm.slovakcompany.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompanyController {



    @Autowired
    CompanyService companyService;

    @Autowired
    BatchServerService batchServerService;

    /**
     * Returns path to
     */
    @GetMapping("/downloadTodaysBatch")
    public ResponseEntity<Resource> serveTodaysBatch()
    {
        Resource resource = batchServerService.serveTodaysBatch();

        return ResponseEntity.ok()
                .contentType( MediaType.APPLICATION_JSON )
                .header( HttpHeaders.CONTENT_DISPOSITION , "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


}
