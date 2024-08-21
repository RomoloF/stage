package it.cript;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@SpringBootApplication
public class CriptApplication {

	public static void main(String[] args) {
		SpringApplication.run(CriptApplication.class, args);

	}

@Bean
public DataSource mysqlDataSource1() {
DriverManagerDataSource dataSource = new DriverManagerDataSource();
dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
dataSource.setUrl("jdbc:mysql://localhost:3306/test_openkm");
dataSource.setUsername("root");
dataSource.setPassword("simonbasic");
return dataSource;
}


}
