package it.cript.model;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * Classe per la gestione del record TbClientCalls
 *
 */
@Component
public class TbClientCalls {


    private Integer id;
    private String clientID;
    private Date dataChiamata;
    private String metadataRichiesta;
    private String metadataRisposta;
    private String Stato;
    private String secretKey;
    private String nomeClient;

    /**
     * Costruttore vuoto
     */
    public TbClientCalls() {
        super();
   }
    /**
     * Costruttore con tutti i parametri
     */
    public TbClientCalls(Integer id, String clientID, Date dataChiamata, String metadataRichiesta, String metadataRisposta, String stato) {
    	super();
        this.id = id;
        this.clientID = clientID;
        this.dataChiamata = dataChiamata;
        this.metadataRichiesta = metadataRichiesta;
        this.metadataRisposta = metadataRisposta;
        this.Stato = stato;
    }
    
    public TbClientCalls(Integer id, String clientID, Date dataChiamata, String metadataRichiesta,
			String metadataRisposta, String stato, String secretKey, String nomeClient) {
		super();
		this.id = id;
		this.clientID = clientID;
		this.dataChiamata = dataChiamata;
		this.metadataRichiesta = metadataRichiesta;
		this.metadataRisposta = metadataRisposta;
		Stato = stato;
		this.secretKey = secretKey;
		this.nomeClient = nomeClient;
	}
	/**
     * Metodi get e set
     */
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getClientID() {
        return clientID;
    }
    public void setClientID(String clientID) {
        this.clientID = clientID;
    }
	public Date getDataChiamata() {
		return dataChiamata;
	}
	public void setDataChiamata(Date today) {
		this.dataChiamata = today;
	}
	public String getMetadataRichiesta() {
		return metadataRichiesta;
	}
	public void setMetadataRichiesta(String metadataRichiesta) {
		this.metadataRichiesta = metadataRichiesta;
	}
	public String getMetadataRisposta() {
		return metadataRisposta;
	}
	public void setMetadataRisposta(String metadataRisposta) {
		this.metadataRisposta = metadataRisposta;
	}
	public String getStato() {
		return Stato;
	}
	public void setStato(String stato) {
		Stato = stato;
	}
	

    public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public String getNomeClient() {
		return nomeClient;
	}
	public void setNomeClient(String nomeClient) {
		this.nomeClient = nomeClient;
	}
	/**
     * Metodo toString
     */
	@Override
    public String toString() {
        return "TbClientCalls [id=" + id + ", clientID=" + clientID + ", dataChiamata=" + dataChiamata
                + ", metadataRichiesta=" + metadataRichiesta + ", metadataRisposta=" + metadataRisposta + ", Stato=" + Stato + "]";
    }
	
}