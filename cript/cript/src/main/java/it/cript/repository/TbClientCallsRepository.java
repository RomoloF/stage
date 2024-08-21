package it.cript.repository;

import it.cript.model.TbClientCalls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TbClientCallsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<TbClientCalls> tbClientCallsRowMapper = new RowMapper<TbClientCalls>() {
        @Override
        public TbClientCalls mapRow(ResultSet rs, int rowNum) throws SQLException {
            TbClientCalls call = new TbClientCalls();
            call.setId(rs.getInt("id"));
            call.setClientID(rs.getString("clientID"));
            call.setDataChiamata(rs.getDate("dataChiamata"));
            call.setMetadataRichiesta(rs.getString("metadataRichiesta"));
            call.setMetadataRisposta(rs.getString("metadataRisposta"));
            call.setStato(rs.getString("Stato"));
            return call;
        }
    };

    public List<TbClientCalls> findAll() {
        String sql = "SELECT * FROM tbClientCalls";
        return jdbcTemplate.query(sql, tbClientCallsRowMapper);
    }

    public TbClientCalls findById(int id) {
        String sql = "SELECT * FROM tbClientCalls WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, tbClientCallsRowMapper);
    }

    public int save(TbClientCalls call) {
        String sql = "INSERT INTO tbClientCalls (clientID, dataChiamata, metadataRichiesta, metadataRisposta, Stato) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, call.getClientID(), call.getDataChiamata(), call.getMetadataRichiesta(), call.getMetadataRisposta(), call.getStato());
    }

    public int update(TbClientCalls call) {
        String sql = "UPDATE TbClientCalls SET clientID = ?, dataChiamata = ?, metadataRichiesta = ?, metadataRisposta = ?, Stato = ? WHERE id = ?";
        return jdbcTemplate.update(sql, call.getClientID(), call.getDataChiamata(), call.getMetadataRichiesta(), call.getMetadataRisposta(), call.getStato(), call.getId());
    }

    public int deleteById(int id) {
        String sql = "DELETE FROM bClientCalls WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
    public int save1(TbClientCalls call) {
        String sql = "INSERT INTO tbClientCalls (clientID, dataChiamata, metadataRichiesta, metadataRisposta, Stato) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, call.getClientID(), call.getDataChiamata(), call.getMetadataRichiesta(), call.getMetadataRisposta(), call.getStato());
    }

}
