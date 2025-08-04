package com.org.fundatec.conta_bancaria.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.fundatec.conta_bancaria.SistemaContasBancariasApplication;
import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.model.Agencia;
import com.org.fundatec.conta_bancaria.model.Banco;
import com.org.fundatec.conta_bancaria.model.Cliente;
import com.org.fundatec.conta_bancaria.model.Conta;
import com.org.fundatec.conta_bancaria.service.AgenciaService;
import com.org.fundatec.conta_bancaria.service.BancoService;
import com.org.fundatec.conta_bancaria.service.ClienteService;
import com.org.fundatec.conta_bancaria.service.ContaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = SistemaContasBancariasApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ContaControllerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContaService contaService;

    @MockBean
    private BancoService bancoService;

    @MockBean
    private ClienteService clienteService;

    @MockBean
    private AgenciaService agenciaService;

    private static final Integer CONTA_INSERIR = 1;

    private Agencia buildAgencia(Integer id, Integer numero, String nome, Banco banco){
        return new Agencia(id, numero, nome, banco);
    }

    private Banco buildBanco(Integer id, Integer codigo, String nome, String cnpj){
        return new Banco(id, codigo, nome, cnpj);
    }

    private Cliente buildCliente(Integer id, String nome, String cpf){
        return new Cliente(id, nome, cpf);
    }

    private Conta buildConta(Integer id, Integer numero, Double valor, Double saldo, Cliente cliente, Agencia agencia){
        return new Conta(id, numero, valor, saldo, agencia, cliente);
    }

    @Test
    void testaSalvarConta() throws Exception {
        Banco banco = new Banco(1, 222, "Banco bom", "1234");
        Agencia agencia = new Agencia(1, 111, "Agencia boa", banco);
        Cliente cliente = new Cliente(1, "1234", "Matheus");

        Conta contaParaSalvar = new Conta(null, 333, 100.00, 100.00, agencia, cliente);
        Conta contaSalva = new Conta(1, 333, 100.00, 100.00, agencia, cliente);

        when(agenciaService.buscaPorNumero(agencia.getNumero())).thenReturn(agencia);
        when(clienteService.busca(cliente.getId())).thenReturn(cliente);
        when(contaService.cadastrarConta(any(Conta.class))).thenReturn(contaSalva);

        MvcResult mvcResult = mockMvc.perform(post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(contaParaSalvar)))
                .andExpect(status().isCreated())
                .andReturn();

        Conta retorno = parseResponse(mvcResult, Conta.class);
        assertEquals(contaSalva.getId(), retorno.getId());
        assertEquals(contaSalva.getNumero(), retorno.getNumero());
        assertEquals(contaSalva.getSaldo(), retorno.getSaldo());

        verify(agenciaService).buscaPorNumero(agencia.getNumero());
        verify(clienteService).busca(cliente.getId());
        verify(contaService).cadastrarConta(any(Conta.class));
    }

    @Test
    void testaSalvarContaNegativo() throws Exception {
        Banco banco = new Banco(1, 222, "Banco bom", "1234");
        Agencia agencia = new Agencia(1, 111, "Agencia boa", banco);
        Cliente cliente = new Cliente(99, "1234", "Matheus");

        Conta conta = new Conta(null, 333, 50.00, 50.00, agencia, cliente);

        when(agenciaService.buscaPorNumero(agencia.getNumero())).thenReturn(agencia);
        when(clienteService.busca(cliente.getId())).thenThrow(new RegistroNaoEncontradoException("id: 99 não encontrado"));

        mockMvc.perform(post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(conta)))
                .andExpect(status().isNotFound());

        verify(agenciaService).buscaPorNumero(agencia.getNumero());
        verify(clienteService).busca(cliente.getId());
        verify(contaService, never()).cadastrarConta(any(Conta.class));
    }

    @Test
    void testaBuscaNumero() throws Exception {
        Banco banco = new Banco(1, 222, "Banco bom", "1234");
        Agencia agencia = new Agencia(1, 111, "Agencia boa", banco);
        Cliente cliente = new Cliente(1, "1234", "Matheus");

        Conta conta = new Conta(1, 333, 100.00, 100.00, agencia, cliente);

        when(contaService.busca(333)).thenReturn(conta);

        MvcResult mvcResult = mockMvc.perform(get("/contas/consulta-numero/" + conta.getNumero())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Conta retorno = parseResponse(mvcResult, Conta.class);
        assertEquals(conta.getId(), retorno.getId());
        assertEquals(conta.getNumero(), retorno.getNumero());
        assertEquals(conta.getSaldo(), retorno.getSaldo());

        verify(contaService, times(1)).busca(333);
    }

    @Test
    void testaBuscaNumeroNegativo() throws Exception {
        int numeroInexistente = 999;

        when(contaService.busca(numeroInexistente)).thenThrow(new RegistroNaoEncontradoException("numero: " + numeroInexistente + " encontrado"));

        mockMvc.perform(get("/contas/consulta-numero/" + numeroInexistente)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(contaService, times(1)).busca(numeroInexistente);
    }

    @Test
    void testaEditar() throws Exception {
        Banco banco = new Banco(1, 222, "Banco bom", "1234");
        Agencia agencia = new Agencia(1, 111, "Agencia boa", banco);
        Cliente cliente = new Cliente(1, "1234", "Matheus");

        Conta conta = new Conta(1, 333, 100.00, 100.00, agencia, cliente);

        when(contaService.editar(eq(1), any(Conta.class))).thenReturn(conta);

        MvcResult mvcResult = mockMvc.perform(put("/contas/1")
                        .contentType("application/json")
                        .content(MAPPER.writeValueAsString(agencia)))
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();

        Conta retorno = MAPPER.readValue(json, Conta.class);

        verify(contaService, times(1)).editar(eq(1), any(Conta.class));
        assertThat(retorno.getId(), equalTo(1));
        assertThat(retorno.getNumero(), equalTo(333));
    }

    @Test
    void testaEditarNegativo() throws Exception {
        Banco banco = new Banco(1, 222, "Banco bom", "1234");
        Agencia agencia = new Agencia(1, 111, "Agencia boa", banco);
        Cliente cliente = new Cliente(1, "1234", "Matheus");

        when(contaService.editar(eq(1), any(Conta.class)))
                .thenThrow(new RegistroNaoEncontradoException("Conta não encontrada"));

        mockMvc.perform(put("/contas/1")
                        .contentType("application/json")
                        .content(MAPPER.writeValueAsString(agencia)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testaRemover() throws Exception {
        Banco banco = new Banco(1, 222, "Banco bom", "1234");
        Agencia agencia = new Agencia(1, 111, "Agencia boa", banco);
        Cliente cliente = new Cliente(1, "1234", "Matheus");

        Conta conta = new Conta(1, 333, 100.00, 100.00, agencia, cliente);

        doNothing().when(contaService).remover(conta.getId());

        mockMvc.perform(delete("/contas/1", conta.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(contaService).remover(conta.getId());
    }

    @Test
    void testaRemoverNegativo() throws Exception {
        Banco banco = new Banco(1, 222, "Banco bom", "1234");
        Agencia agencia = new Agencia(1, 111, "Agencia boa", banco);
        Cliente cliente = new Cliente(1, "1234", "Matheus");

        Conta conta = new Conta(1, 333, 100.00, 100.00, agencia, cliente);

        doThrow(new RegistroNaoEncontradoException("id: 1 não encontrado")).when(contaService).remover(conta.getId());

        mockMvc.perform(delete("/contas/1", conta.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(contaService).remover(conta.getId());
    }

    @Test
    void testaSacar() throws Exception {
        Banco banco = new Banco(1, 222, "Banco bom", "1234");
        Agencia agencia = new Agencia(1, 111, "Agencia boa", banco);
        Cliente cliente = new Cliente(1, "1234", "Matheus");

        Conta valorSaque = new Conta();
        valorSaque.setValor(50.00);

        Conta contaAtualizada = new Conta(1, 333, 50.00, 50.00, agencia, cliente);

        when(contaService.sacar(50.0, 333)).thenReturn(contaAtualizada);


        MvcResult mvcResult = mockMvc.perform(post("/contas/333/sacar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(valorSaque)))
                .andExpect(status().isOk())
                .andReturn();

        Conta retorno = parseResponse(mvcResult, Conta.class);
        assertEquals(50.00, retorno.getSaldo());
        assertEquals(50.00, retorno.getValor());
        verify(contaService).sacar(50.00,333);
    }

    @Test
    void testaSacarNegativoValorMaiorSaldo() throws Exception {
        Conta contaParaSaque = new Conta();
        contaParaSaque.setValor(150.00);

        when(contaService.sacar(150.00, 333))
                .thenThrow(new RegistroNaoEncontradoException("O saque não pode ser maior que o saldo"));

        mockMvc.perform(post("/contas/333/sacar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(contaParaSaque)))
                .andExpect(status().isNotFound());

        verify(contaService).sacar(150.00, 333);
    }

    @Test
    void testaSacarNegativoValorInvalido() throws Exception {
        Conta contaParaSaque = new Conta();
        contaParaSaque.setValor(0.00);

        when(contaService.sacar(0.00, 333))
                .thenThrow(new RegistroNaoEncontradoException("O saque não pode ser menor ou igual a zero"));

        mockMvc.perform(post("/contas/333/sacar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(contaParaSaque)))
                .andExpect(status().isNotFound());

        verify(contaService).sacar(0.00, 333);
    }

    @Test
    void testaSacarNegativoContaNaoEncontrada() throws Exception {
        Conta contaParaSaque = new Conta();
        contaParaSaque.setValor(50.00);

        when(contaService.sacar(50.00, 999))
                .thenThrow(new RegistroNaoEncontradoException("Conta não encontrada"));

        mockMvc.perform(post("/contas/999/sacar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(contaParaSaque)))
                .andExpect(status().isNotFound());

        verify(contaService).sacar(50.00, 999);
    }

    @Test
    void testaDepositar() throws Exception {
        Banco banco = new Banco(1, 222, "Banco bom", "1234");
        Agencia agencia = new Agencia(1, 111, "Agencia boa", banco);
        Cliente cliente = new Cliente(1, "1234", "Matheus");

        Conta contaDeposito = new Conta(1, 333, 0.00, 150.00, agencia, cliente);

        Conta contaParaDeposito = new Conta();
        contaParaDeposito.setValor(50.00);

        when(contaService.depositar(50.00,333)).thenReturn(contaDeposito);

        MvcResult mvcResult = mockMvc.perform(post("/contas/333/depositar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(contaParaDeposito)))
                .andExpect(status().isOk())
                .andReturn();

        Conta retorno = parseResponse(mvcResult, Conta.class);

        verify(contaService).depositar(50.00, 333);
        assertEquals(contaDeposito.getId(), retorno.getId());
        assertEquals(contaDeposito.getSaldo(), retorno.getSaldo());
        assertEquals(contaDeposito.getNumero(), retorno.getNumero());
    }

    @Test
    void testaDepositoValorInvalidoNegativo()throws Exception {
        Conta contaParaDeposito = new Conta();
        contaParaDeposito.setValor(0.00);

        when(contaService.depositar(0.00, 333))
                .thenThrow(new RegistroNaoEncontradoException("O deposito não pode ser zero ou menor que zero"));

        mockMvc.perform(post("/contas/333/depositar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(contaParaDeposito)))
                .andExpect(status().isNotFound());

        verify(contaService).depositar(0.00, 333);
    }

    @Test
    void testaDepositarContaNaoEncontradaNegativo() throws Exception {
        Conta contaParaDeposito = new Conta();
        contaParaDeposito.setValor(50.00);

        when(contaService.depositar(50.00, 999))
                .thenThrow(new RegistroNaoEncontradoException("Conta não encontrada"));

        mockMvc.perform(post("/contas/999/depositar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(contaParaDeposito)))
                .andExpect(status().isNotFound());

        verify(contaService).depositar(50.00, 999);
    }

    private static <T> List<T> parseResponseList(MvcResult mockHttpServletResponse, Class<T> clazz) {
        try {
            String contentAsString = mockHttpServletResponse.getResponse().getContentAsString();
            return MAPPER.readValue(contentAsString, MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T parseResponse(MvcResult mockHttpServletResponse, Class<T> clazz) {
        try {
            String contentAsString = mockHttpServletResponse.getResponse().getContentAsString();
            return MAPPER.readValue(contentAsString, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
