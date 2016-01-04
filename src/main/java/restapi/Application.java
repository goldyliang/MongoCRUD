package restapi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Rest service of document CRUD with Spring boot and embedded Tomcat server
 * 
 * @author goldyliang@gmail.com
 *
 */
@SpringBootApplication
@ComponentScan (basePackages = {"docservice", "restapi"})
@EnableMongoRepositories(basePackages = {"repository"})
public class Application {

    public static void main(String[] args) {
    	SpringApplication.run(Application.class, args);
    }
    
}