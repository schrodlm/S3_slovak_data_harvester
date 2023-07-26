package cz.trixi.schrodlm.slovakcompany;

import cz.trixi.schrodlm.slovakcompany.file.FileUtility;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StopWatch;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class SlovakCompanyApplication {



	public static void main(String[] args){
		ApplicationContext context = SpringApplication.run( SlovakCompanyApplication.class, args);
	}

}
