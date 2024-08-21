package it.cript;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
//Classe di test
public class MysqlDataSource {
	        DriverManagerDataSource dataSource = new DriverManagerDataSource();
	        public DataSource mysqlDataSource() {
	            DriverManagerDataSource dataSource = new DriverManagerDataSource();
	            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
	            dataSource.setUrl("jdbc:mysql://localhost:3306/?????");
	            dataSource.setUsername("root");
	            dataSource.setPassword("simonbasic");
	            return dataSource;
	        }




}
