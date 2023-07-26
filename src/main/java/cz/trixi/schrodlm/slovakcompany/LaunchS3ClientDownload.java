package cz.trixi.schrodlm.slovakcompany;

import cz.trixi.schrodlm.slovakcompany.service.CompanyService;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StopWatch;
import cz.trixi.schrodlm.slovakcompany.service.CompanyS3Handler;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class LaunchS3ClientDownload {

	public static void main( String[] args ) throws IOException {
		ApplicationContext context = new SpringApplicationBuilder( LaunchS3ClientDownload.class )
				.web( WebApplicationType.NONE )
				.run( args );

		//simple benchmark
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		//Download all zip objects
		CompanyS3Handler companyS3Handler = context.getBean( CompanyS3Handler.class );

		CompanyService companyService = context.getBean( CompanyService.class );

		//companyService.downloadAndParseAllObjects();

		//companyS3Handler.downloadTodaysBatch();

		//companyService.dailyUpdate();


		// Benchmarking
		stopWatch.stop();
		long timeTaken = stopWatch.getTotalTimeMillis();
		System.out.println( timeTaken );
	}
}
