package gr.hua.dit.mycitygov;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyCityGovApplication {

    public static void main(String[] args) {
        System.out.println("dOWNLOADING .env..."); //for debugging

        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();

        if (dotenv.entries().isEmpty()) {
            System.err.println("file .env not found or empty!");
        } else {
            System.out.println("found: " + dotenv.entries().size());
        }

        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
            System.out.println("      Loaded: " + entry.getKey());
        });

        if (System.getProperty("MINIO_URL") == null) {
            System.err.println("ERROR: MINIO_URL null"); // debugging
        }

        SpringApplication.run(MyCityGovApplication.class, args);
    }

}
