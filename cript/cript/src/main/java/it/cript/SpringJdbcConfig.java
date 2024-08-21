package it.cript;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

//@Configuration
//@ComponentScan("it.cript")
public class SpringJdbcConfig {
    //@Bean
    public DataSource mysqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/test_openkm");
        dataSource.setUsername("root");
        dataSource.setPassword("simonbasic");
        return dataSource;
    }
}