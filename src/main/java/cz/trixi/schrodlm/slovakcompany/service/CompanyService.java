package cz.trixi.schrodlm.slovakcompany.service;

import cz.trixi.schrodlm.slovakcompany.file.FileUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class CompanyService {


    @Autowired
    CompanyS3Handler companyS3Handler;

    @Autowired
    CompanyFileService companyFileService;

    public void persistCompany() {

    }


    public void updateCompanies() {

    }

    public void downloadAndParseAllObjects() {
        companyS3Handler.downloadAllObjects();
        companyFileService.unzipAllBatches();
        companyFileService.unzipTodaysBatch();
    }




}
