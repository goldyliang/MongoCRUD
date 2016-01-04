package restapi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@SpringBootApplication
@ComponentScan (basePackages = {"docservice", "restapi"})
@EnableMongoRepositories(basePackages = {"repository"})
public class Application {

    public static void main(String[] args) {
    	ApplicationContext ctx = SpringApplication.run(Application.class, args);
    }
    
}