package it.cript.service;

import it.cript.model.TbClientCalls;
import it.cript.repository.TbClientCallsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbClientCallsService {

    @Autowired
    private TbClientCallsRepository tbClientCallsRepository;

    public List<TbClientCalls> getAllCalls() {
        return tbClientCallsRepository.findAll();
    }

    public TbClientCalls getCallById(int id) {
        return tbClientCallsRepository.findById(id);
    }

    public int saveCall(TbClientCalls call) {
        return tbClientCallsRepository.save(call);
    }

    public int updateCall(TbClientCalls call) {
        return tbClientCallsRepository.update(call);
    }

    public int deleteCall(int id) {
        return tbClientCallsRepository.deleteById(id);
    }
}
