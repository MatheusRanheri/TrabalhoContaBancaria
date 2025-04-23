package com.org.fundatec.conta_bancaria.service;

import com.org.fundatec.conta_bancaria.repository.BancoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BancoService {

    @Autowired
    private BancoRepository bancoRepository;
}
