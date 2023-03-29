package cz.trixi.schrodlm.slovakcompany;

import cz.trixi.schrodlm.slovakcompany.file.FileUtility;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class SlovakCompanyApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SlovakCompanyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		FileUtility fileUtility = new FileUtility();

		//Create a directory for the resources
		File destDir = new File("resources/test_data");
		if (!destDir.exists()) {
			if(!destDir.mkdirs()) throw new RuntimeException();
		}

		//out file will be used to store the zipped content
		File out = new File(destDir.getPath() + "/download.xml");

		fileUtility.downloadSlovakRegister(out);
	}

}
