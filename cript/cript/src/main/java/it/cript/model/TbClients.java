package it.cript.model;

import java.util.Date;

import org.springframework.stereotype.Component;
@Component
public class TbClients {
	
    private String clientID;
    private String clientSecret;
    private String nome;
    private Date dataCre;
    private String uteCre;
    private Date dataAgg;
    private String uteAgg;
    private Date dataAnn;
    private String uteAnn;
    private String json_originale;
    private String json_criptato;
    /**
     * Costruttore senza parametri.
     */
	public TbClients() {
		super();
	}
	 /**
     * Costruttore con tutti i parametri.
     */

	public TbClients(String clientID, String clientSecret, String nome, Date dataCre, String uteCre, Date dataAgg,
			String uteAgg, Date dataAnn, String uteAnn) {
		super();
		this.clientID = clientID;
		this.clientSecret = clientSecret;
		this.nome = nome;
		this.dataCre = dataCre;
		this.uteCre = uteCre;
		this.dataAgg = dataAgg;
		this.uteAgg = uteAgg;
		this.dataAnn = dataAnn;
		this.uteAnn = uteAnn;
	}

	public TbClients(String clientID, String clientSecret, String nome, Date dataCre, String uteCre, Date dataAgg,
			String uteAgg, Date dataAnn, String uteAnn, String json_originale, String json_criptato) {
		super();
		this.clientID = clientID;
		this.clientSecret = clientSecret;
		this.nome = nome;
		this.dataCre = dataCre;
		this.uteCre = uteCre;
		this.dataAgg = dataAgg;
		this.uteAgg = uteAgg;
		this.dataAnn = dataAnn;
		this.uteAnn = uteAnn;
		this.json_originale = json_originale;
		this.json_criptato = json_criptato;
	}
	/**
	 * Metodi get e set
	 */

	public String getClientID() {
		return clientID;
	}
	public String getJson_originale() {
		return json_originale;
	}
	public void setJson_originale(String json_originale) {
		this.json_originale = json_originale;
	}
	public String getJson_criptato() {
		return json_criptato;
	}
	public void setJson_criptato(String json_criptato) {
		this.json_criptato = json_criptato;
	}
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Date getDataCre() {
		return dataCre;
	}
	public void setDataCre(Date dataCre) {
		this.dataCre = dataCre;
	}
	public String getUteCre() {
		return uteCre;
	}
	public void setUteCre(String uteCre) {
		this.uteCre = uteCre;
	}
	public Date getDataAgg() {
		return dataAgg;
	}
	public void setDataAgg(Date dataAgg) {
		this.dataAgg = dataAgg;
	}
	public String getUteAgg() {
		return uteAgg;
	}
	public void setUteAgg(String uteAgg) {
		this.uteAgg = uteAgg;
	}
	public Date getDataAnn() {
		return dataAnn;
	}
	public void setDataAnn(Date dataAnn) {
		this.dataAnn = dataAnn;
	}
	public String getUteAnn() {
		return uteAnn;
	}
	public void setUteAnn(String uteAnn) {
		this.uteAnn = uteAnn;
	}
	/**
	 * Metodo toString
	 */
	@Override
	public String toString() {
		return "TbClients [clientID=" + clientID + ", clientSecret=" + clientSecret + ", nome=" + nome + ", dataCre="
				+ dataCre + ", uteCre=" + uteCre + ", dataAgg=" + dataAgg + ", uteAgg=" + uteAgg + ", dataAnn="
				+ dataAnn + ", uteAnn=" + uteAnn + "]";
	}



}
