package com.org.fundatec.conta_bancaria.service;

import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
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

    public Conta busca(Integer numero) throws RegistroNaoEncontradoException {
        Optional<Conta> busca = contaRepository.findContaByNumero(numero);
        return busca.orElseThrow(() -> new RegistroNaoEncontradoException("numero: " + numero + " não encontrado"));
    }

    public Conta editar(Integer id, Conta conta) throws RegistroNaoEncontradoException{
        Conta contaBusca = this.busca(id);

        contaBusca.setNumero(conta.getNumero());
        contaBusca.setSaldo(conta.getSaldo());

        contaRepository.save(contaBusca);

        return contaBusca;
    }

    public void remover(Integer id){
        Conta contaBusca = this.busca(id);
        contaRepository.delete(contaBusca);
    }

    public Conta sacar(Double saque, Integer id) throws RegistroNaoEncontradoException{
        Conta contaSaque = this.busca(id);
        if(saque > contaSaque.getSaldo() ){
            throw new IllegalArgumentException("O saque é maior que o saldo da conta");
        }else if(saque < 0){
            throw new IllegalArgumentException("O saque não pode ser menor ou igual a zero");
        }else {

            contaSaque.setSaldo(contaSaque.getSaldo() - saque);
            return contaRepository.save(contaSaque);
        }

    }

    public Conta depositar(Double deposito, Integer id) throws RegistroNaoEncontradoException{
        Conta contaDeposito = this.busca(id);
        if(deposito <= 0){
            throw new IllegalArgumentException("O deposito não pode ser zero ou menor que zero");
        }else{

            contaDeposito.setSaldo(contaDeposito.getSaldo() + deposito);
            return contaRepository.save(contaDeposito);
        }
    }
}
