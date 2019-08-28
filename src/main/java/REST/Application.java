package REST;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;

import javax.persistence.spi.PersistenceProvider;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/*
* Classe em que o aplicativo Spring começa a execução.
* */
@SpringBootApplication
public class Application {

    public static Date awakeDate;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        awakeDate = new Date();
    }

}
