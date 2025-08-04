package com.org.fundatec.conta_bancaria.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.fundatec.conta_bancaria.SistemaContasBancariasApplication;
import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.exception.handler.ErrorResponse;
import com.org.fundatec.conta_bancaria.model.Banco;
import com.org.fundatec.conta_bancaria.service.BancoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SistemaContasBancariasApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class BancoControllerTest {
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BancoService bancoService;

    private static final Integer BANCO_INSERIR = 1;

    private Banco build(Integer id, Integer codigo, String nome, String cnpj){
        return new Banco(id, codigo, nome, cnpj);
    }

    @Test
    void testaSalvarBanco() throws Exception {
        Banco banco = build(1, 111, "Banco bom", "1234");

        when(bancoService.cadastrarBanco(eq(banco))).thenReturn(banco);

        MvcResult mvcResult =
                mockMvc.perform(post("/bancos").contentType("application/json")
                        .content(MAPPER.writeValueAsString(banco)))
                        .andExpect(status().is(HttpStatus.CREATED.value())).andReturn();

        Banco retorno = parseResponse(mvcResult, Banco.class);

        verify(bancoService, times(1)).cadastrarBanco(eq(banco));
        assertThat("Não retornou correto", retorno.getId(), equalTo(BANCO_INSERIR));
    }

    @Test
    void testasalvarBancoNegativo()throws Exception{
        Banco banco = build(null, 111, "Banco bom", "1234");

        doAnswer(invocationOnMock -> {
            throw new RegistroNaoEncontradoException("id: 1 não encontrado");
        }).when(bancoService).editar(eq(1), eq(banco));

        MvcResult mvcResult = mockMvc.perform(put("/bancos/1")
                .contentType("application/json")
                .content(MAPPER.writeValueAsString(banco)))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(bancoService, times(1)).editar(eq(1), eq(banco));

        ErrorResponse retornoErro = parseResponse(mvcResult, ErrorResponse.class);
        assertThat("Mensagem incorreta", retornoErro.getMensagem(), equalTo("id: 1 não encontrado"));

    }

    @Test
    void testaBuscaId() throws Exception {
        Banco banco = build(1, 111, "Banco bom", "1234");

        doAnswer(invocationOnMock -> {
            return banco;
        }).when(bancoService).busca(eq(banco.getId()));

        MvcResult mvcResult =
                mockMvc.perform(get("/bancos/1"))
                        .andExpect(status().is(HttpStatus.OK.value())).andReturn();

        Banco bancoRetorno =parseResponse(mvcResult, Banco.class);

        verify(bancoService, times(1)).busca(eq(banco.getId()));
        assertThat("Mensagem incorreta", bancoRetorno, equalTo(banco));
    }

    @Test
    void testaBuscaIdNegativo() throws Exception {
        Integer idInexistente = 2;
        Banco banco = build(1, 111, "Banco bom", "1234");

        doAnswer(invocationOnMock -> {
            throw new RegistroNaoEncontradoException("id: 2 não encontrado");
        }).when(bancoService).busca(eq(idInexistente));

        MvcResult mvcResult = mockMvc.perform(get("/bancos/" + idInexistente))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(bancoService, times(1)).busca(eq(idInexistente));

        ErrorResponse bancoRetorno = parseResponse(mvcResult, ErrorResponse.class);
        assertThat("Mensagem incorreta", bancoRetorno.getMensagem(), equalTo("id: 2 não encontrado"));
    }

    @Test
    void testaBuscarTodos() throws Exception {
        List<Banco> resultados = new ArrayList<>();
        resultados.add(build(BANCO_INSERIR, 111, "Banco bom", "1234"));

        doAnswer(invocationOnMock -> {
            return resultados;
        }).when(bancoService).buscarTodos();

        MvcResult mvcResult =
                mockMvc.perform(get("/bancos"))
                        .andExpect(status().is(HttpStatus.OK.value())).andReturn();

        List<Banco> bancos = parseResponseList(mvcResult, Banco.class);
        assertThat("Retorno incorreto", bancos.size(), is(1));

        verify(bancoService, times(1)).buscarTodos();
        assertThat("Não retornou correto", bancos.get(0).getId(), equalTo(BANCO_INSERIR));
    }

    @Test
    void testaBuscarTodosNegativo() throws Exception {
        doAnswer(invocationOnMock -> {
            throw new IllegalStateException("Nenhum banco encontrado");
        }).when(bancoService).buscarTodos();

        MvcResult mvcResult = mockMvc.perform(get("/bancos"))
                .andExpect(status().isInternalServerError())
                .andReturn();

        verify(bancoService, times(1)).buscarTodos();

        ErrorResponse errorResponse = parseResponse(mvcResult, ErrorResponse.class);
        assertThat("Mensagem incorreta", errorResponse.getMensagem(), equalTo("Nenhum banco encontrado"));
    }

    @Test
    void testaBuscarNomeAprox() throws Exception {
        List<Banco> lista = new ArrayList<>();
        lista.add(build(BANCO_INSERIR, 111, "Banco bom", "1234"));

        doAnswer(invocationOnMock -> lista)
                .when(bancoService).buscarNomeAprox("bom");

        MvcResult mvcResult = mockMvc.perform(get("/bancos/consulta-nome/bom"))
                .andExpect(status().isOk())
                .andReturn();

        List<Banco> bancos = parseResponseList(mvcResult, Banco.class);
        assertThat("Quantidade incorreta", bancos.size(), is(1));
        assertThat("Nome incorreto", bancos.get(0).getNome(), equalTo("Banco bom"));

        verify(bancoService, times(1)).buscarNomeAprox("bom");
    }

    @Test
    void testaBuscarNomeAproxNegativo() throws Exception {
        doThrow(new IllegalStateException("Banco não encontrado"))
                .when(bancoService).buscarNomeAprox("erro");

        MvcResult mvcResult = mockMvc.perform(get("/bancos/consulta-nome/erro"))
                .andExpect(status().isInternalServerError()).andReturn();

        verify(bancoService, times(1)).buscarNomeAprox("erro");

        ErrorResponse errorResponse = parseResponse(mvcResult, ErrorResponse.class);
        assertThat("Mensagem incorreta", errorResponse.getMensagem(), equalTo("Banco não encontrado"));
    }

    @Test
    void testaBuscarCodigo() throws Exception {
        Banco banco = build(1, 111, "Banco bom", "1234");

        doAnswer(invocationOnMock -> {
            return banco;
        }).when(bancoService).buscarPorCodigo(eq(banco.getCodigo()));

        MvcResult mvcResult =
                mockMvc.perform(get("/bancos/consulta-codigo/111"))
                        .andExpect(status().is(HttpStatus.OK.value())).andReturn();

        Banco bancoRetorno = parseResponse(mvcResult, Banco.class);

        verify(bancoService, times(1)).buscarPorCodigo(eq(banco.getCodigo()));
        assertThat("Mensagem incorreta", bancoRetorno, equalTo(banco));
    }

    @Test
    void testaBuscaCodigoNegativo() throws Exception {
        Integer coigoInexistente = 999;
        Banco banco = build(1, 111, "Banco bom", "1234");

        doThrow(new RegistroNaoEncontradoException("Banco com codigo" + coigoInexistente + "não enocntrado"))
                .when(bancoService).buscarPorCodigo(eq(coigoInexistente));

        MvcResult mvcResult = mockMvc.perform(get("/bancos/consulta-codigo/" + coigoInexistente))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(bancoService, times(1)).buscarPorCodigo(eq(coigoInexistente));

        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(response.contains("Banco com codigo" + coigoInexistente + "não enocntrado"));
    }

    @Test
    void testaEditar() throws Exception {
        Banco banco = build(BANCO_INSERIR, 111, "Banco bom", "1234");

        doAnswer(invocationOnMock -> {
            return banco;
        }).when(bancoService).editar(eq(banco.getId()), eq(banco));

        MvcResult mvcResult =
                mockMvc.perform(put("/bancos/1").contentType("application/json")
                        .content(MAPPER.writeValueAsString(banco)))
                        .andExpect(status().is(HttpStatus.OK.value())).andReturn();

        Banco bancoRetorno = parseResponse(mvcResult, Banco.class);

        verify(bancoService, times(1)).editar(eq(bancoRetorno.getId()), eq(bancoRetorno));
        assertThat("Não retornou correto", bancoRetorno.getId(), equalTo(1));
    }

    @Test
    void testaEditarNegativo() throws Exception {
        Banco banco = build(BANCO_INSERIR, 111, "Banco bom", "1234");

        doThrow(new RegistroNaoEncontradoException("id: " + banco.getId() + " não encontrado"))
                .when(bancoService).editar(eq(banco.getId()), eq(banco));

        MvcResult mvcResult =
                mockMvc.perform(put("/bancos/1")
                        .contentType("application/json")
                        .content(MAPPER.writeValueAsString(banco)))
                        .andExpect(status().isNotFound()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        verify(bancoService, times(1)).editar(eq(banco.getId()), eq(banco));
        assertTrue(response.contains("id: 1 não encontrado"), "mensagem incorreta");
    }

    @Test
    void testaRemover() throws Exception {
        doNothing().when(bancoService).remover(eq(BANCO_INSERIR));

        MvcResult mvcResult =
                mockMvc.perform(delete("/bancos/"+BANCO_INSERIR).contentType("application/json"))
                        .andExpect(status().is(HttpStatus.OK.value())).andReturn();

        verify(bancoService,  times(1)).remover(eq(BANCO_INSERIR));
    }

    @Test
    void testaRemoverNegativo() throws Exception {
        Integer idInexistente = BANCO_INSERIR;

        doThrow(new RegistroNaoEncontradoException("id: " + idInexistente + " não encontrado"))
                .when(bancoService).remover(eq(idInexistente));

        MvcResult mvcResult =
                mockMvc.perform(delete("/bancos/" + idInexistente)
                        .contentType("application/json"))
                        .andExpect(status().isNotFound())
                        .andReturn();

        verify(bancoService, times(1)).remover(eq(idInexistente));
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
