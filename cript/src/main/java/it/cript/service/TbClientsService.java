package it.cript.service;



import it.cript.model.TbClients;
//import it.cript.repository.TbClientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import it.cript.repository.TbClientsRepository;
@Service
public class TbClientsService {
	 @Autowired
    private final TbClientsRepository tbClientsRepository;
        
    public TbClientsService(TbClientsRepository tbClientsRepository) {
        this.tbClientsRepository = tbClientsRepository;
    }

    public List<TbClients> findAll() {
        return tbClientsRepository.findAll();
    }
    
    // Metodo per recuperare un client per ID
    public TbClients findById(String clientID) {
        return tbClientsRepository.findById(clientID);
    }

    // Metodo per salvare un client
    public void saveClient(TbClients client) {
        tbClientsRepository.save(client);
    }

    // Metodo per eliminare un client per ID
    public int deleteById(String clientID) {
        return tbClientsRepository.deleteById(clientID);
    }
}