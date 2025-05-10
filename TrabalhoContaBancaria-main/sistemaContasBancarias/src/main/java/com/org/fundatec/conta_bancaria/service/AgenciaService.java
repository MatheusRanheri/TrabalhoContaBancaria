package com.org.fundatec.conta_bancaria.service;

import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.model.Agencia;
import com.org.fundatec.conta_bancaria.model.Banco;
import com.org.fundatec.conta_bancaria.repository.AgenciaRepository;
import com.org.fundatec.conta_bancaria.repository.BancoRepository;
import com.org.fundatec.conta_bancaria.repository.ClienteRepository;
import com.org.fundatec.conta_bancaria.repository.ContaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class AgenciaService {

    @Autowired
    private AgenciaRepository agenciaRepository;

    @Autowired
    private BancoRepository bancoRepository;

    public Agencia cadastrarAgencia(Agencia agencia){
        agenciaRepository.save(agencia);
        return agencia;
    }

    public Agencia busca(Integer id) throws RegistroNaoEncontradoException{
        Optional<Agencia> busca = agenciaRepository.findById(id);
        return busca.orElseThrow(() -> new RegistroNaoEncontradoException("id: " + id + " não encontrado"));
    }

    public Agencia buscaPorBanco(Integer codigo) throws RegistroNaoEncontradoException{
        Optional<Agencia> busca = agenciaRepository.findByBancoCodigo(codigo);
        return busca.orElseThrow(() -> new RegistroNaoEncontradoException("banco: " + codigo + " não encontrado"));
    }

    public Agencia buscaPorNumero(Integer numero) throws RegistroNaoEncontradoException{
        Optional<Agencia> busca = agenciaRepository.findByNumero(numero);
        return busca.orElseThrow(() -> new RegistroNaoEncontradoException("numero: " + numero + " não encontrado"));
    }

    public Agencia editar(Integer id, Agencia agencia) throws RegistroNaoEncontradoException{
        Agencia agenciaExistente = agenciaRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Agencia não ncontrado"));

        Banco bancoExistente = bancoRepository.findByCodigo(agencia.getBanco().getCodigo())
                .orElseThrow(() -> new RegistroNaoEncontradoException("Banco não encontrado"));

        agenciaExistente.setNumero(agencia.getNumero());
        agenciaExistente.setNome(agencia.getNome());
        agenciaExistente.setBanco(bancoExistente);

        return agenciaRepository.save(agenciaExistente);
    }

    public void remover(Integer id){
        Agencia agenciaBusca = this.busca(id);
        agenciaRepository.delete(agenciaBusca);
    }

}
