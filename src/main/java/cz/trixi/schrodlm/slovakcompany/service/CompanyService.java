package cz.trixi.schrodlm.slovakcompany.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class CompanyService {
    @Value( "${zipDir}" )
    public String zipDir;

    @Value("${xmlDir}")
    public String xmlDir;
    public void persistCompany(){

    };

    public void updateCompanies(){

    }


    public boolean directoriesExistCheck(){
        File xmlDirectory = new File(xmlDir);
        File zipDirectory = new File(zipDir);


        //Checks if directory for zipped files exists
        if(!zipDirectory.exists())
            return false;
        //Checks if directory for xml exists
        if (!xmlDirectory.exists())
            return false;

        return true;
    }

}
