package com.org.fundatec.conta_bancaria.service;

import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.model.Banco;
import com.org.fundatec.conta_bancaria.repository.BancoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class BancoService {

    @Autowired
    private BancoRepository bancoRepository;

    public Banco cadastrarBanco(@Valid Banco banco){
        bancoRepository.save(banco);
        return banco;
    }

    public Banco busca(Integer id) throws RegistroNaoEncontradoException {
        Optional<Banco> busca = bancoRepository.findById(id);
        return busca.orElseThrow(() -> new RegistroNaoEncontradoException("id: " + id + " não encontrado"));
    }

    public List<Banco> buscarNomeAprox(String nome){
        return bancoRepository.findBancoByNomeContains(nome);
    }

    public Banco buscarPorCodigo(Integer codigo) throws RegistroNaoEncontradoException {
        return bancoRepository.findByCodigo(codigo).orElseThrow(() -> new RegistroNaoEncontradoException("codigo: " + codigo + " não encontrado"));
    }

    public List<Banco> buscarTodos(){
        return StreamSupport.stream(bancoRepository.findAll().spliterator(), false).toList();
    }

    public Banco editar(Integer id, Banco banco) throws RegistroNaoEncontradoException{
        Banco bancoBusca = this.busca(id);

        bancoBusca.setCodigo(banco.getCodigo());
        bancoBusca.setNome(banco.getNome());
        bancoBusca.setCnpj(banco.getCnpj());

        bancoRepository.save(bancoBusca);

        return bancoBusca;
    }

    public void remover(Integer id){
        Banco bancoBusca = this.busca(id);
        bancoRepository.delete(bancoBusca);
    }
}
