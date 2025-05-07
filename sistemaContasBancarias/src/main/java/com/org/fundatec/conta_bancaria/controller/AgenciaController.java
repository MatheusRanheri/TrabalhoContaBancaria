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

@RestController
@RequestMapping(path = "agencias")
public class AgenciaController {

    @Autowired
    private AgenciaService agenciaService;

    @Autowired
    private BancoService bancoService;

    @PostMapping( consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Agencia> salvarAgencia(@RequestBody @Valid Agencia agencia){
        agencia.setBanco(bancoService.buscarPorCodigo(agencia.getBanco().getCodigo()));

        return ResponseEntity.status(HttpStatus.CREATED).body(agenciaService.cadastrarAgencia(agencia));
    }

    @GetMapping(value = { "{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Agencia> buscaPeloId(@RequestParam("id") Integer id)throws RegistroNaoEncontradoException {
        Agencia retorno = agenciaService.busca(id);
        return ResponseEntity.status(HttpStatus.OK).body(retorno);
    }

    @GetMapping(value = { "consulta-banco/{banco}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Agencia> buscarPorBanco(@PathVariable("agencia") Banco banco)throws RegistroNaoEncontradoException {
        Agencia retorno = agenciaService.buscaPorBanco(banco);
        return ResponseEntity.status(HttpStatus.OK).body(retorno);
    }

    @GetMapping(value = { "consulta-numero/{numero}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Agencia> buscarPorNumero(@PathVariable("numero") Integer numero)throws RegistroNaoEncontradoException {
        Agencia retorno = agenciaService.buscaPorNumero(numero);
        return ResponseEntity.status(HttpStatus.OK).body(retorno);
    }

    @PutMapping(value = { "{agencia}" }, consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Agencia> editar(@PathVariable("agencia") Integer id, @RequestBody Agencia agencia) throws RegistroNaoEncontradoException{
        return ResponseEntity.status(HttpStatus.OK).body(agenciaService.editar(id, agencia));
    }

    @DeleteMapping(value = { "{agencia}" },
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Agencia> remover(@PathVariable("agencia") Integer id) throws RegistroNaoEncontradoException{
        agenciaService.remover(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
