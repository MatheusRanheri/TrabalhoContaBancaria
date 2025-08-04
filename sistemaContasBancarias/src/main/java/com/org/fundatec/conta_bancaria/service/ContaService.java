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
        Optional<Conta> busca = contaRepository.findByNumero(numero);
        return busca.orElseThrow(() -> new RegistroNaoEncontradoException("numero: " + numero + " não encontrado"));
    }

    public Conta buscaPorId(Integer id) throws RegistroNaoEncontradoException {
        Optional<Conta> busca = contaRepository.findById(id);
        return busca.orElseThrow(() -> new RegistroNaoEncontradoException("id: " + id + " não encontrado"));
    }

    public Conta editar(Integer id, Conta conta) throws RegistroNaoEncontradoException{
        Conta contaBusca = this.buscaPorId(id);

        contaBusca.setNumero(conta.getNumero());
        contaBusca.setSaldo(conta.getSaldo());

        contaRepository.save(contaBusca);

        return contaBusca;
    }

    public void remover(Integer id){
        Conta contaBusca = this.buscaPorId(id);
        contaRepository.delete(contaBusca);
    }

    public Conta sacar(Double valor, Integer numero) throws RegistroNaoEncontradoException{
        Optional<Conta> conta = contaRepository.findByNumero(numero);
        Conta contaCerta = conta
                .orElseThrow(() -> new RegistroNaoEncontradoException("Conta não encontrada"));

        if(valor > contaCerta.getSaldo() ){
            throw new RegistroNaoEncontradoException("O saque é maior que o saldo da conta");
        }else if(valor <= 0){
            throw new RegistroNaoEncontradoException("O saque não pode ser menor ou igual a zero");
        }else {

            contaCerta.setSaldo(contaCerta.getSaldo() - valor);
            return contaRepository.save(contaCerta);
        }

    }

    public Conta depositar(Double valor, Integer numero) throws RegistroNaoEncontradoException{
        Optional<Conta> conta = contaRepository.findByNumero(numero);
        Conta contaCerta = conta
                .orElseThrow(() -> new RegistroNaoEncontradoException("Conta não encontrada"));

        if(valor <= 0){
            throw new RegistroNaoEncontradoException("O deposito não pode ser zero ou menor que zero");
        }else{

            contaCerta.setSaldo(contaCerta.getSaldo() + valor);
            return contaRepository.save(contaCerta);
        }
    }
}
