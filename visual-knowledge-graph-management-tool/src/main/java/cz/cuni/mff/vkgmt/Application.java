package cz.cuni.mff.vkgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Spring settings and runner.
 * @author Ales Woska
 *
 */
@Configuration 
@PropertySource("classpath:sparql.properties")
@ComponentScan("cz.cuni.mff.vkgmt") 
@EnableWebMvc   
@EnableAutoConfiguration
public class Application {  
	
	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
} 
