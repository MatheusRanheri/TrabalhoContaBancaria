package com.org.fundatec.conta_bancaria.service;

import com.org.fundatec.conta_bancaria.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;
}
