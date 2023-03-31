package cz.trixi.schrodlm.slovakcompany;

import cz.trixi.schrodlm.slovakcompany.file.FileUtility;
import cz.trixi.schrodlm.slovakcompany.model.BatchMetadata;
import cz.trixi.schrodlm.slovakcompany.parsing.XMLBatchParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

@SpringBootApplication
public class SlovakCompanyApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SlovakCompanyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		//simple benchmark
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();


		FileUtility fileUtility = new FileUtility();

		//Create a directory for the resources
		File destDir = new File("resources/test_data");
		if (!destDir.exists()) {
			if(!destDir.mkdirs()) throw new RuntimeException();
		}

		//out file will be used to store the zipped content
		File out = new File(destDir.getPath() + "/download.xml");

		fileUtility.downloadSlovakRegister(out);


		XMLBatchParser xmlBatchParser = new XMLBatchParser(out);

		Collection<BatchMetadata> init_batches = new ArrayList<>();
		Collection<BatchMetadata> update_batches = new ArrayList<>();

		//Parse will parse the downloaded batch XML metadata and will also fill both collections
		xmlBatchParser.parseBatchMetadata(init_batches,update_batches);

		//Now I want to download all the init-batches to a file in resources

		//1. Create directory to store init-batches
		File zippedInitBatchesDirectory = new File(destDir.getPath() + "/zipped-batch-init");
		if(!zippedInitBatchesDirectory.exists())
			if(!zippedInitBatchesDirectory.mkdir()) throw new RuntimeException("Creating zipped batch directory failed");

		fileUtility.downloadBatchCollection(init_batches, zippedInitBatchesDirectory);

		//Unzip downloaded batch files
		File initBatchesDirectory = new File(destDir.getPath() + "/batch-init");
		if(!initBatchesDirectory.exists())
			if(!initBatchesDirectory.mkdir()) throw new RuntimeException("Creating batch directory failed");

		fileUtility.unzipDirectory(zippedInitBatchesDirectory, initBatchesDirectory);

		stopWatch.stop();
		long timeTaken = stopWatch.getTotalTimeMillis();
		System.out.println(timeTaken);
	}

}
