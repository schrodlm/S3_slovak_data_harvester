package cz.trixi.schrodlm.slovakcompany;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SlovakCompanyApplication {



	public static void main(String[] args){
		ApplicationContext context = SpringApplication.run( SlovakCompanyApplication.class, args);
	}

}
