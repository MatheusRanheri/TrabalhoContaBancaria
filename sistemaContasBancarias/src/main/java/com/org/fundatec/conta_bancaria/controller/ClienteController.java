package com.org.fundatec.conta_bancaria.controller;

import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.model.Banco;
import com.org.fundatec.conta_bancaria.model.Cliente;
import com.org.fundatec.conta_bancaria.repository.ClienteRepository;
import com.org.fundatec.conta_bancaria.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping( consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Cliente> salvarCliente(@RequestBody @Valid Cliente cliente){
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.cadastrarCliente(cliente));
    }

    @GetMapping(value = { "cliente/{id}" }, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Cliente> get(@RequestParam("id") Integer id)throws RegistroNaoEncontradoException {
        Cliente retorno = clienteService.busca(id);
        return ResponseEntity.status(HttpStatus.OK).body(retorno);
    }

    @GetMapping(value={"consulta-nome{nome}"},produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<List<Cliente>> get(@PathVariable("nome") String nome) {
        return ResponseEntity.status(HttpStatus.OK).body(clienteService.buscarNomeAprox(nome));
    }

    @PutMapping(value = { "{cliente}" }, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Cliente> editar(@PathVariable("cliente") Integer id, @RequestBody Cliente cliente) throws RegistroNaoEncontradoException{
        return ResponseEntity.status(HttpStatus.OK).body(clienteService.editar(id, cliente));
    }

    @DeleteMapping(value = { "{cliente}" },
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Cliente> remover(@PathVariable("cliente") Integer id) throws RegistroNaoEncontradoException{
        clienteService.remover(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
