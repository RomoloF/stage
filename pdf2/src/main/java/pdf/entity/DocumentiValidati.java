package pdf.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * Entity class utilizzata per rappresentare i documenti PDF validati.
 *
 * @author Romolo Fiorenza
 */

@Entity
@Table(name="documenti_validati")
@NamedQuery(name="DocumentiValidati.findAll", query="SELECT d FROM DocumentiValidati d")
public class DocumentiValidati implements Serializable {
	private static final long serialVersionUID = 1L;

	 /**
     * Identificativo unico del documento validato.
     */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Opzionale per auto-increment
	private int id;

	 /**
     * Contenuto del documento PDF memorizzato come array di byte.
     */
	@Lob
	@Column(name="contenuto_pdf")
	private byte[] contenutoPdf;

	 /**
     * Data di avvenuta validazione del documento.
     */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_validazione")
	private Date dataValidazione;

	/**
     * Dimensione del file del documento PDF in byte.
     */
	@Column(name="dimensione_file")
	private int dimensioneFile;

    /**
     * Nome del file del documento PDF.
     */
	@Column(name="nome_file")
	private String nomeFile;



	 /**
     * Flag che indica lo stato di validità del documento (1 valido, 0 non valido).
     */
	private byte valido;


	/**
     * Costruttore vuoto.
     */
	public DocumentiValidati() {
	}

	 // Getter e Setter per ogni attributo
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte[] getContenutoPdf() {
		return this.contenutoPdf;
	}

	public void setContenutoPdf(byte[] contenutoPdf) {
		this.contenutoPdf = contenutoPdf;
	}

	public Date getDataValidazione() {
		return this.dataValidazione;
	}

	public void setDataValidazione(Date dataValidazione) {
		this.dataValidazione = dataValidazione;
	}

	public int getDimensioneFile() {
		return this.dimensioneFile;
	}

	public void setDimensioneFile(int dimensioneFile) {
		this.dimensioneFile = dimensioneFile;
	}

	public String getNomeFile() {
		return this.nomeFile;
	}

	public void setNomeFile(String nomeFile) {
		this.nomeFile = nomeFile;
	}

	public byte getValido() {
		return this.valido;
	}

	public void setValido(byte valido) {
		this.valido = valido;
	}

}