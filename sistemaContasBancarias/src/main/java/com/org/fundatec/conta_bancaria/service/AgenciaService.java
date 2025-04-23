package com.org.fundatec.conta_bancaria.service;

import com.org.fundatec.conta_bancaria.model.Agencia;
import com.org.fundatec.conta_bancaria.repository.AgenciaRepository;
import com.org.fundatec.conta_bancaria.repository.BancoRepository;
import com.org.fundatec.conta_bancaria.repository.ClienteRepository;
import com.org.fundatec.conta_bancaria.repository.ContaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgenciaService {

    @Autowired
    private AgenciaRepository agenciaRepository;

    public Agencia cadastrarAgencia(Agencia agencia){
        agenciaRepository.save(agencia);
        return agencia;
    }
    public void remover(Long numId){

    }

}
