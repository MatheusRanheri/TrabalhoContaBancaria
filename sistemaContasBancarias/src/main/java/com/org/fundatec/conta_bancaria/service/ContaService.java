package com.org.fundatec.conta_bancaria.service;

import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.model.Cliente;
import com.org.fundatec.conta_bancaria.model.Conta;
import com.org.fundatec.conta_bancaria.repository.ContaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    public Conta cadastrarConta(@Valid Conta conta){
        contaRepository.save(conta);
        return conta;
    }

    public Conta busca(Integer numId) throws RegistroNaoEncontradoException {
        Optional<Conta> busca = contaRepository.findContaById(numId);
        return busca.orElseThrow(() -> new RegistroNaoEncontradoException("id: " + numId + " não encontrado"));
    }

    public Conta editar(Integer numId, Conta conta) throws RegistroNaoEncontradoException{
        Conta contaBusca = this.busca(numId);

        contaBusca.setNumero(conta.getNumero());
        contaBusca.setSaldo(conta.getSaldo());

        contaRepository.save(contaBusca);

        return contaBusca;
    }

    public void remover(Integer numId){
        Conta contaBusca = this.busca(numId);
        contaRepository.delete(contaBusca);
    }
}
