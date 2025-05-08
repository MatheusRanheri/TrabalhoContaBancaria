package com.org.fundatec.conta_bancaria.controller;

import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.model.Cliente;
import com.org.fundatec.conta_bancaria.model.Conta;
import com.org.fundatec.conta_bancaria.service.ContaService;
import jakarta.servlet.http.PushBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @PostMapping( consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Conta> salvarConta(@RequestBody @Valid Conta conta){
        return ResponseEntity.status(HttpStatus.CREATED).body(contaService.cadastrarConta(conta));
    }

    @GetMapping(value = { "conta/{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Conta> get(@RequestParam("id") Integer id)throws RegistroNaoEncontradoException {
        Conta retorno = contaService.busca(id);
        return ResponseEntity.status(HttpStatus.OK).body(retorno);
    }

    @PutMapping(value = { "{conta}" }, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Conta> editar(@PathVariable("conta") Integer id, @RequestBody Conta conta) throws RegistroNaoEncontradoException{
        return ResponseEntity.status(HttpStatus.OK).body(contaService.editar(id, conta));
    }

    @DeleteMapping(value = { "{conta}" },
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Conta> remover(@PathVariable("conta") Integer numId) throws RegistroNaoEncontradoException{
        contaService.remover(numId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = {"{sacar}"},
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Conta> sacar(@PathVariable("sacar") Double saque, @RequestParam("id")Integer id) throws RegistroNaoEncontradoException{
        contaService.sacar(saque, id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping(value = {"{depositar}"},
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Conta> depositar(@PathVariable("depositar") Double deposito, @RequestParam("id") Integer id)throws RegistroNaoEncontradoException{
        contaService.depositar(deposito, id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
