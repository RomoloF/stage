package it.cript.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import it.cript.model.TbClients;

@Repository
public class TbClientsRepository {

    private final JdbcTemplate jdbcTemplate;

//    @Autowired
//    public TbclientsRepository(DataSource dataSource) {
//        this.jdbcTemplate = new JdbcTemplate(dataSource);
//    }

    @Autowired
    public TbClientsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private final RowMapper<TbClients> tbClientsRowMapper = new RowMapper<>() {
        @Override
        public TbClients mapRow(ResultSet rs, int rowNum) throws SQLException {
            TbClients client = new TbClients();
            client.setClientID(rs.getString("clientID"));
            client.setClientSecret(rs.getString("clientSecret"));
            client.setNome(rs.getString("nome"));
            client.setDataCre(rs.getDate("dataCre"));
            client.setUteCre(rs.getString("uteCre"));
            client.setDataAgg(rs.getDate("dataAgg"));
            client.setUteAgg(rs.getString("uteAgg"));
            client.setDataAnn(rs.getDate("dataAnn"));
            client.setUteAnn(rs.getString("uteAnn"));
            return client;
        }
    };




    public List<TbClients> findAll() {
        String sql = "SELECT * FROM TbClients";
        return jdbcTemplate.query(sql, tbClientsRowMapper);
    }

    public TbClients findById(String clientID) {
        String sql = "SELECT * FROM TbClients WHERE clientID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{clientID}, tbClientsRowMapper);
    }

    public void save(TbClients client) {
        String sql = "INSERT INTO TbClients (clientID, clientSecret, nome, dataCre, uteCre, dataAgg, uteAgg, dataAnn, uteAnn) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, client.getClientID(), client.getClientSecret(), client.getNome(), client.getDataCre(), client.getUteCre(), client.getDataAgg(), client.getUteAgg(), client.getDataAnn(), client.getUteAnn());
        
    }

    public int update(TbClients client) {
        String sql = "UPDATE TbClients SET clientSecret = ?, nome = ?, dataCre = ?, uteCre = ?, dataAgg = ?, uteAgg = ?, dataAnn = ?, uteAnn = ? WHERE clientID = ?";
        return jdbcTemplate.update(sql, client.getClientSecret(), client.getNome(), client.getDataCre(), client.getUteCre(), client.getDataAgg(), client.getUteAgg(), client.getDataAnn(), client.getUteAnn(), client.getClientID());
    }

    public int deleteById(String clientID) {
        String sql = "DELETE FROM TbClients WHERE clientID = ?";
        return jdbcTemplate.update(sql, clientID);
    }
}
