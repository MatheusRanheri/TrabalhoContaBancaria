package com.org.fundatec.conta_bancaria.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.fundatec.conta_bancaria.SistemaContasBancariasApplication;
import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.exception.handler.ErrorResponse;
import com.org.fundatec.conta_bancaria.model.Cliente;
import com.org.fundatec.conta_bancaria.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SistemaContasBancariasApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ClienteControllerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    private static final Integer CLIENTE_INSERIR = 1;

    private Cliente build(Integer id, String cpf, String nome){
        return new Cliente(id, cpf, nome);
    }

    @Test
    void testaSalvarConta() throws Exception {
        Cliente cliente = build(1, "111", "Matheus");

        when(clienteService.cadastrarCliente(eq(cliente))).thenReturn(cliente);

        MvcResult mvcResult =
                mockMvc.perform(post("/clientes").contentType("application/json")
                                .content(MAPPER.writeValueAsString(cliente)))
                        .andExpect(status().is(HttpStatus.CREATED.value())).andReturn();

        Cliente retorno = parseResponse(mvcResult, Cliente.class);

        verify(clienteService, times(1)).cadastrarCliente(eq(cliente));
        assertThat("Não retornou correto", retorno.getId(), equalTo(CLIENTE_INSERIR));
    }

    @Test
    void testasalvarClienteNegativo()throws Exception{
        Cliente cliente = build(1, "111", "Matheus");

        doAnswer(invocationOnMock -> {
            throw new RegistroNaoEncontradoException("id: 1 não encontrado");
        }).when(clienteService).editar(eq(1), eq(cliente));

        MvcResult mvcResult = mockMvc.perform(put("/clientes/1")
                        .contentType("application/json")
                        .content(MAPPER.writeValueAsString(cliente)))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(clienteService, times(1)).editar(eq(1), eq(cliente));

        ErrorResponse retornoErro = parseResponse(mvcResult, ErrorResponse.class);
        assertThat("Mensagem incorreta", retornoErro.getMensagem(), equalTo("id: 1 não encontrado"));

    }

    @Test
    void testaBuscaId() throws Exception {
        Cliente cliente = build(1, "111", "Matheus");

        doAnswer(invocationOnMock -> {
            return cliente;
        }).when(clienteService).busca(eq(cliente.getId()));

        MvcResult mvcResult =
                mockMvc.perform(get("/clientes/1"))
                        .andExpect(status().is(HttpStatus.OK.value())).andReturn();

        Cliente clienteRetorno = parseResponse(mvcResult, Cliente.class);

        verify(clienteService, times(1)).busca(eq(cliente.getId()));
        assertThat("Mensagem incorreta", clienteRetorno, equalTo(cliente));
    }

    @Test
    void testaBuscaIdNegativo() throws Exception {
        Integer idInexistente = 2;

        doAnswer(invocationOnMock -> {
            throw new RegistroNaoEncontradoException("id: 2 não encontrado");
        }).when(clienteService).busca(eq(idInexistente));

        MvcResult mvcResult = mockMvc.perform(get("/clientes/" + idInexistente))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(clienteService, times(1)).busca(eq(idInexistente));

        ErrorResponse clientesRetorno = parseResponse(mvcResult, ErrorResponse.class);
        assertThat("Mensagem incorreta", clientesRetorno.getMensagem(), equalTo("id: 2 não encontrado"));
    }

    @Test
    void testaBuscarNomeAprox() throws Exception {
        List<Cliente> lista = new ArrayList<>();
        lista.add(build(CLIENTE_INSERIR, "111", "Matheus"));

        doAnswer(invocationOnMock -> lista)
                .when(clienteService).buscarNomeAprox("Ma");

        MvcResult mvcResult = mockMvc.perform(get("/clientes/consulta-nome/Ma"))
                .andExpect(status().isOk())
                .andReturn();

        List<Cliente> clientes = parseResponseList(mvcResult, Cliente.class);
        assertThat("Quantidade incorreta", clientes.size(), is(1));
        assertThat("Nome incorreto", clientes.get(0).getNome(), equalTo("Matheus"));

        verify(clienteService, times(1)).buscarNomeAprox("Ma");
    }

    @Test
    void testaBuscarNomeAproxNegativo() throws Exception {
        doThrow(new IllegalStateException("Cliente não encontrado"))
                .when(clienteService).buscarNomeAprox("erro");

        MvcResult mvcResult = mockMvc.perform(get("/clientes/consulta-nome/erro"))
                .andExpect(status().isInternalServerError()).andReturn();

        verify(clienteService, times(1)).buscarNomeAprox("erro");

        ErrorResponse errorResponse = parseResponse(mvcResult, ErrorResponse.class);
        assertThat("Mensagem incorreta", errorResponse.getMensagem(), equalTo("Cliente não encontrado"));
    }

    @Test
    void testaEditar() throws Exception {
        Cliente cliente = build(CLIENTE_INSERIR, "111", "Matheus");

        doAnswer(invocationOnMock -> {
            return cliente;
        }).when(clienteService).editar(eq(cliente.getId()), eq(cliente));

        MvcResult mvcResult =
                mockMvc.perform(put("/clientes/1").contentType("application/json")
                                .content(MAPPER.writeValueAsString(cliente)))
                        .andExpect(status().is(HttpStatus.OK.value())).andReturn();

        Cliente clienteRetorno = parseResponse(mvcResult, Cliente.class);

        verify(clienteService, times(1)).editar(eq(clienteRetorno.getId()), eq(clienteRetorno));
        assertThat("Não retornou correto", clienteRetorno.getId(), equalTo(1));
    }

    @Test
    void testaEditarNegativo() throws Exception {
        Cliente cliente = build(CLIENTE_INSERIR, "111", "Matheus");

        doThrow(new RegistroNaoEncontradoException("id: " + cliente.getId() + " não encontrado"))
                .when(clienteService).editar(eq(cliente.getId()), eq(cliente));

        MvcResult mvcResult =
                mockMvc.perform(put("/clientes/1")
                                .contentType("application/json")
                                .content(MAPPER.writeValueAsString(cliente)))
                        .andExpect(status().isNotFound()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        verify(clienteService, times(1)).editar(eq(cliente.getId()), eq(cliente));
        assertTrue(response.contains("id: 1 não encontrado"), "mensagem incorreta");
    }

    @Test
    void testaRemover() throws Exception {
        doNothing().when(clienteService).remover(eq(CLIENTE_INSERIR));

        MvcResult mvcResult =
                mockMvc.perform(delete("/clientes/" + CLIENTE_INSERIR).contentType("application/json"))
                        .andExpect(status().is(HttpStatus.OK.value())).andReturn();

        verify(clienteService,  times(1)).remover(eq(CLIENTE_INSERIR));
    }

    @Test
    void testaRemoverNegativo() throws Exception {
        Integer idInexistente = CLIENTE_INSERIR;

        doThrow(new RegistroNaoEncontradoException("id: " + idInexistente + " não encontrado"))
                .when(clienteService).remover(eq(idInexistente));

        MvcResult mvcResult =
                mockMvc.perform(delete("/clientes/" + idInexistente)
                                .contentType("application/json"))
                        .andExpect(status().isNotFound())
                        .andReturn();

        verify(clienteService, times(1)).remover(eq(idInexistente));
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(response.contains("id: " + idInexistente + " não encontrado"), "Mensagem não encontrada");
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
