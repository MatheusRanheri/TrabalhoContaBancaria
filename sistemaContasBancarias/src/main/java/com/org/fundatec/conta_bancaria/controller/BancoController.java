package com.org.fundatec.conta_bancaria.controller;

import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.model.Agencia;
import com.org.fundatec.conta_bancaria.model.Banco;
import com.org.fundatec.conta_bancaria.service.BancoService;
import jakarta.persistence.Access;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "bancos")
public class BancoController {

    @Autowired
    private BancoService bancoService;

    @PostMapping( consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Banco> salvarBanco(@RequestBody @Valid Banco banco){
        return ResponseEntity.status(HttpStatus.CREATED).body(bancoService.cadastrarBanco(banco));
    }

    @GetMapping(value = { "banco/{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Banco> get(@RequestParam("id") Integer id)throws RegistroNaoEncontradoException {
        Banco retorno = bancoService.busca(id);
        return ResponseEntity.status(HttpStatus.OK).body(retorno);
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Banco>> get(){
        return ResponseEntity.status(HttpStatus.OK).body(bancoService.buscarTodos());
    }

    @GetMapping(value={"consulta-nome/{nome}"},produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<List<Banco>> get(@PathVariable("nome") String nome) {
        return ResponseEntity.status(HttpStatus.OK).body(bancoService.buscarNomeAprox(nome));
    }

    @GetMapping(value = { "consulta-codigo{codigo}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Banco> buscaPorCodigo(@PathVariable("codigo") Integer codigo)throws RegistroNaoEncontradoException {
        Banco retorno = bancoService.buscarPorCodigo(codigo);
        return ResponseEntity.status(HttpStatus.OK).body(retorno);
    }

    @PutMapping(value = { "{banco}" }, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Banco> editar(@PathVariable("banco") Integer id, @RequestBody Banco banco) throws RegistroNaoEncontradoException{
        return ResponseEntity.status(HttpStatus.OK).body(bancoService.editar(id, banco));
    }

    @DeleteMapping(value = { "{banco}" },
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Banco> remover(@PathVariable("banco") Integer id) throws RegistroNaoEncontradoException{
        bancoService.remover(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
