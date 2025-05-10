package com.org.fundatec.conta_bancaria.controller;

import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.model.Agencia;
import com.org.fundatec.conta_bancaria.model.Banco;
import com.org.fundatec.conta_bancaria.service.AgenciaService;
import com.org.fundatec.conta_bancaria.service.BancoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "agencias")
public class AgenciaController {

    @Autowired
    private AgenciaService agenciaService;

    @Autowired
    private BancoService bancoService;

    @PostMapping( consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Agencia> salvarAgencia(@RequestBody Agencia agencia)throws RegistroNaoEncontradoException{
        Integer codigoBanco = agencia.getBanco().getCodigo();
        Banco banco = bancoService.buscarPorCodigo(codigoBanco);

        agencia.setBanco(banco);

        Agencia novaAgencia = agenciaService.cadastrarAgencia(agencia);

        return ResponseEntity.status(HttpStatus.CREATED).body(novaAgencia);
    }

    @GetMapping(value = { "{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Agencia> buscaPeloId(@PathVariable("id") Integer id)throws RegistroNaoEncontradoException {
        Agencia retorno = agenciaService.busca(id);
        return ResponseEntity.status(HttpStatus.OK).body(retorno);
    }

    @GetMapping(value = { "consulta-banco/{codigo}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Agencia> buscarPorBanco(@PathVariable("codigo") Integer codigo)throws RegistroNaoEncontradoException {
        Agencia retorno = agenciaService.buscaPorBanco(codigo);
        return ResponseEntity.status(HttpStatus.OK).body(retorno);
    }

    @GetMapping(value = { "consulta-numero/{numero}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Agencia> buscarPorNumero(@PathVariable("numero") Integer numero)throws RegistroNaoEncontradoException {
        Agencia retorno = agenciaService.buscaPorNumero(numero);
        return ResponseEntity.status(HttpStatus.OK).body(retorno);
    }

    @PutMapping(value = { "agencia/{id}" }, consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Agencia> editar(@PathVariable("id") Integer id, @RequestBody Agencia agencia) throws RegistroNaoEncontradoException{
        Agencia agenciaNova = agenciaService.editar(id, agencia);
        return ResponseEntity.status(HttpStatus.OK).body(agenciaNova);
    }

    @DeleteMapping(value = { "{agencia}" },
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Agencia> remover(@PathVariable("agencia") Integer id) throws RegistroNaoEncontradoException{
        agenciaService.remover(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
