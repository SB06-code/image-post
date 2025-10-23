package codeit.sb06.imagepost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ImagePostApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImagePostApplication.class, args);
    }

}
