package it.cript.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TbClientsControllerVecchio {

//    @Autowired
//    private TbClientsService tbClientsService;
//    @Autowired
//    private TbClientService2 tbClientsService2;
//    @Autowired
//    private TbClientsRepository tbClientsRepository;
//    @Autowired
//    private JdbcTemplateApplication jdbcTemplateApplication;
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//    @Autowired
//    private DecryptApplication decryptApplication;
//    @Autowired
//    private Decriptare_Criptare_Key decriptare_Criptare_Key ;
//
















    private static final String SQL_S_CLIENTS_BY_CLIENTID = "select ClientID, ClientSecret from tbClients where ClientID = ? and DataAnn is null";

    @GetMapping("/")
    public String index() {

        return "index";
    }

//    @GetMapping("/addClient")
//    public String showAddClientForm(Model model) {
//        TbClients client = new TbClients();
//        model.addAttribute("client", client);
//        return "addClient";
//    }
//
//    @PostMapping("/addClient")
//    public String addClient(@ModelAttribute("client") TbClients client) {
//        tbClientsService.saveClient(client);
//        return "redirect:/";
//    }





//    @GetMapping("/protocollo")
//    @ResponseBody
//    public String startProtocollo(@RequestParam String clientID, @RequestParam String metadata) throws Exception {
//    	Response response = new Response();
//        LocalDate today = LocalDate.now();
//        String stato = "";
//        System.out.println("Stampo clienteID :"+clientID+"     Stampo metadata criptato :"+metadata);
//        //String metadataConv=metadata.trim();
//        //controllo esistenza di utente su db
//        System.out.println("Cerco il clientID >>>"+clientID);
//        TbClients client = tbClientsRepository.findById(clientID);
//        if (client == null) {
//            response.setMessage("Cliente non trovato");
//            return " Ciao";
//        }
//
//        String clientSecret = client.getClientSecret();
//        System.out.println("Stampo il clientSecret recuperato nel db  >>> "+clientSecret);
//        System.out.println();
//        System.out.println("Stampo il metadata ricevuto :"+metadata);
//        System.out.println("VADO AL METODO DECRYPT");
//        //decrypt del metadata
//        String jsonDecriptato =decryptApplication.decrypt(clientSecret,metadata);
//        //decrypt del json
//
//        System.out.println("Questo è il json decriptato >>>> "+jsonDecriptato);
//        //String decryptedJson = cryptingService256.decrypt(metadata, clientSecret);
//        response.setData(client);
//        response.setMessage(jsonDecriptato);
//
//        //registrazione chiamata su db
//        //tbClientsRepository.registerTbClientCalls(clientID, today, metadata, decryptedJson, stato);
//
//        return jsonDecriptato;
//
//}
//








//    @GetMapping("/generateClientIdAndSecret")
//    public Response generateClientIdAndSecret() {
//        Response response = new Response();
//        SecretKey key;
//        String keyString;
//        String uuid;
//        try {
//            key = generateKey();
//            keyString = getStringFromKey(key);
//            log.info("Key: " + keyString);
//            uuid = generateUUID();
//            log.info("UUID: " + uuid);
//
//            Map<String, String> coppia = new HashMap<String, String>();
//            coppia.put("clientId", uuid);
//            coppia.put("clientSecret", keyString);
//
//            response.setData(coppia);
//            response.setStatus(200);
//            response.setMessage("ClientId e ClientSecret generato");
//        } catch (Exception e) {
//        log.error("Errore generazione clientId e clientSecret", e);
//        response.setData(null);
//        response.setStatus(500);
//        response.setInternalMessage(e.getMessage());
//        response.setMessage("ClientId e ClientSecret non generato");
//    	}
//        return response;
//    	}
//
//    @GetMapping("/encryptJson")
//    public Response encryptJson(@RequestParam String base64Text {
//        Response response = new Response();
//        try {
//            Map<String, Object> coppia = jdbcTemplate.queryForMap(SQL_S_CLIENTS_BY_CLIENTID, "a682d4cc-64fe-4ae5-bbfc-6fdbccfe9320";
//            String keyString = coppia.get("clientSecret").toString();
//
//            SecretKey key = getKeyFromString(keyString);
//
//            String plainText = new String(Base64.getDecoder().decode(base64Text.getBytes());
//
//            String encryptText = encrypt(plainText, key);
//
//            log.info("Encrypt testo: " + plainText + " -> " + encryptText);
//
//            response.setData(encryptText);
//            response.setStatus(200);
//            response.setMessage("Encrypt effettuato");
//        } catch (Exception e {
//            log.error("Errore encrypt", e);
//
//    		response.setData(null);
//            response.setStatus(500);
//            response.setInternalMessage(e.getMessage());
//            response.setMessage("Errore encrypt");
//        }
//        return response;
//    }
//    @GetMapping("/decrypt")
//    public Response decrypt(@RequestParam String encryptText) {
//        Response response = new Response();
//        try {
//          //  Map<String, Object> coppia = jdbcTemplate.queryForMap(SQL_S_CLIENTS_BY_CLIENTID, "a682d4cc-64fe-4ae5-bbfc-6fdbccfe9320");
//          //  String keyString = coppia.get("clientSecret").toString();
//
//
//        	String keyString=" ";
//        	SecretKey key=null;
//            key = keyString;//getKeyFromString(keyString);//Ladevo recuperare dal db la KeyString
//            String plainText = decriptare_Criptare_Key.decrypt(encryptText, key);
//            log.info("Decrypt testo: " + encryptText + " -> " + plainText);
//
//            response.setData(plainText);
//            response.setStatus(200);
//            response.setMessage("Decrypt effettuato");
//        } catch (Exception e) {
//            log.error("Errore decrypt", e);
//            response.setData(null);
//            response.setStatus(500);
//            response.setInternalMessage(e.getMessage());
//            response.setMessage("Errore decrypt");
//        }
//        return response;
//    }










//  //qui si decripta il json
//  	@GetMapping("/jsonCriptatoIn")
//  	public Response startProtocollo2(@RequestParam String clientID, @RequestParam String jsonCriptato) throws Exception {
//  		Response response = new Response();
//  		try {
//			Map<String, Object> coppia = jdbcTemplate.queryForMap(SQL_S_CLIENTS_BY_CLIENTID, clientID);
//			String keyString = coppia.get("clientSecret").toString();
//			SecretKey key = tbClientsService2.getKeyFromString(keyString);
//			String plainText = tbClientsService2.decrypt(jsonCriptato, key);
//			response.setData(plainText);
//			response.setStatus(200);
//			response.setMessage("Decrypt effettuato");
//		} catch (Exception e) {
//			response.setData(null);
//			response.setStatus(500);
//			response.setInternalMessage(e.getMessage());
//			response.setMessage("Errore decrypt");
//		}
//		return response;
//
//  	}
//
//
//  //questo cripta il json
//  	@PostMapping("/jsonDecriptatoIn")
//  	public Response jsonCriptatoIn(@RequestParam String clientID, @RequestBody String jsonDecriptato) {
//  		Response response = new Response();
//  		try {
//  			Map<String, Object> coppia = jdbcTemplate.queryForMap(SQL_S_CLIENTS_BY_CLIENTID, clientID);
//  			String keyString = coppia.get("clientSecret").toString();
//  			SecretKey key = tbClientsService2.getKeyFromString(keyString);
//  			String encryptedText = tbClientsService2.encrypt(jsonDecriptato, key);
//  			response.setData(encryptedText);
//  			response.setStatus(200);
//  			response.setMessage("Encript effettuato");
//  		} catch (Exception e) {
//  			response.setData(null);
//  			response.setStatus(500);
//  			response.setInternalMessage(e.getMessage());
//  			response.setMessage("Errore encrypting");
//  		}
//  		return response;
//  	}


























}

