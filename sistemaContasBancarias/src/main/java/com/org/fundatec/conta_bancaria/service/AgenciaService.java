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
//        Optional<Banco> bancoNovo = bancoRepository.findBancoById(agencia.getBanco().getId());
//
//        if (bancoNovo.isEmpty()){
//            return ResponseEntity.badRequest().body("Banco Id " + bancoRepository. + "aaa");
//        }
//
//        Agencia agencia = new Agencia();
//        agencia.setNome();
        agenciaRepository.save(agencia);
        return agencia;
    }

    public Agencia busca(Integer id) throws RegistroNaoEncontradoException{
        Optional<Agencia> busca = agenciaRepository.findAgenciaById(id);
        return busca.orElseThrow(() -> new RegistroNaoEncontradoException("id: " + id + " não encontrado"));
    }

    public Agencia buscaPorBanco(Banco banco) throws RegistroNaoEncontradoException{
        Optional<Agencia> busca = agenciaRepository.findAgenciaByBanco(banco);
        return busca.orElseThrow(() -> new RegistroNaoEncontradoException("banco: " + banco + " não encontrado"));
    }

    public Agencia buscaPorNumero(Integer numero) throws RegistroNaoEncontradoException{
        Optional<Agencia> busca = agenciaRepository.findAgenciaByNumero(numero);
        return busca.orElseThrow(() -> new RegistroNaoEncontradoException("numero: " + numero + " não encontrado"));
    }

    public Agencia editar(Integer id, Agencia agencia) throws RegistroNaoEncontradoException{
        Agencia agenciaBusca = this.busca(id);

        agenciaBusca.setNome(agencia.getNome());
        agenciaBusca.setBanco(agencia.getBanco());
        //agenciaBusca.setContas(agencia.getContas());
        agenciaBusca.setNumero(agencia.getNumero());

        agenciaRepository.save(agenciaBusca);

        return agenciaBusca;
    }

    public void remover(Integer id){
        Agencia agenciaBusca = this.busca(id);
        agenciaRepository.delete(agenciaBusca);
    }

}
