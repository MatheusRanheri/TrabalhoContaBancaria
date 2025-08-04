package com.org.fundatec.conta_bancaria.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.fundatec.conta_bancaria.SistemaContasBancariasApplication;
import com.org.fundatec.conta_bancaria.exception.RegistroNaoEncontradoException;
import com.org.fundatec.conta_bancaria.model.Agencia;
import com.org.fundatec.conta_bancaria.model.Banco;
import com.org.fundatec.conta_bancaria.service.AgenciaService;
import com.org.fundatec.conta_bancaria.service.BancoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


import java.io.IOException;
import java.util.List;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest(classes = SistemaContasBancariasApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AgenciaControllerTest {
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BancoService bancoService;

    @MockBean
    private AgenciaService agenciaService;

    private static final Integer AGENCIA_INSERIR = 1;

    private Agencia buildA(Integer id, Integer numero, String nome, Banco banco){
        return new Agencia(id, numero, nome, banco);
    }

    private Banco buildB(Integer id, Integer codigo, String nome, String cnpj){
        return new Banco(id, codigo, nome, cnpj);
    }

    @Test
    void testaSalvarAgencia() throws Exception {
        Banco banco = buildB(1, 222, "Banco bom", "1234");
        Agencia agencia = buildA(null, 111, "Agencia boa", banco);

        Agencia agenciaSalva = buildA(1, 111, "Agencia boa", banco);

        when(bancoService.buscarPorCodigo(eq(banco.getCodigo()))).thenReturn(banco);
        when(agenciaService.cadastrarAgencia(any(Agencia.class))).thenReturn(agenciaSalva);

        MvcResult mvcResult =
                mockMvc.perform(post("/agencias").contentType("application/json")
                                .content(MAPPER.writeValueAsString(agencia)))
                        .andExpect(status().isCreated()).andReturn();

        Agencia retorno = parseResponse(mvcResult, Agencia.class);

        verify(bancoService).buscarPorCodigo(eq(banco.getCodigo()));
        verify(agenciaService).cadastrarAgencia(any(Agencia.class));

        assertThat(retorno.getId(), equalTo(1));
        assertThat(retorno.getBanco().getCodigo(), equalTo(222));
    }

    @Test
    void testaSalvarAgenciaNegativo() throws Exception {
        Banco banco = buildB(null, 999, "Banco bom", "1234");
        Agencia agencia = buildA(null, 222, "Agencia boa", banco);

        doThrow(new RegistroNaoEncontradoException("codigo: " + banco.getCodigo() + " não encontrado"))
                .when(bancoService).buscarPorCodigo(eq(banco.getCodigo()));

        MvcResult mvcResult =
                mockMvc.perform(post("/agencias").contentType("application/json")
                                .content(MAPPER.writeValueAsString(agencia)))
                        .andExpect(status().isNotFound()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        verify(bancoService).buscarPorCodigo(eq(banco.getCodigo()));
        verify(agenciaService, times(0)).cadastrarAgencia(any());

        assertTrue(response.contains("codigo: " + banco.getCodigo() + " não encontrado"), "Mensagem não encontarda");
    }

    @Test
    void testaBuscaPorId() throws Exception {
        Banco banco = buildB(1, 222, "Banco bom", "1234");
        Agencia agencia = buildA(1, 111, "Agencia boa", banco);

        when(agenciaService.busca(eq(banco.getId()))).thenReturn(agencia);

        MvcResult mvcResult =
                mockMvc.perform(get("/agencias/" + banco.getId())
                                .contentType("application/json"))
                        .andExpect(status().isOk()).andReturn();

        Agencia retorno = parseResponse(mvcResult, Agencia.class);

        verify(agenciaService).busca(eq(banco.getId()));

        assertThat(retorno.getId(), equalTo(1));
        assertThat(retorno.getBanco().getCodigo(), equalTo(222));

    }

    @Test
    void testaBuscaPorIdNegativo() throws Exception {
        Integer idInexitente = 2;

        when(agenciaService.busca(eq(idInexitente)))
                .thenThrow(new RegistroNaoEncontradoException("id: " + idInexitente + " não encontrado"));

        MvcResult mvcResult = mockMvc.perform(get("/agencias/" + idInexitente).contentType("application/json"))
                .andExpect(status().isNotFound())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertTrue(response.contains("id: " + idInexitente + " não encontrado"));
        verify(agenciaService).busca(eq(idInexitente));
    }


    @Test
    void testaBuscaPorBanco() throws Exception {
        Banco banco = buildB(1, 222, "Banco bom", "1234");
        Agencia agencia = buildA(1, 111, "Agencia boa", banco);

        when(agenciaService.buscaPorBanco(eq(banco.getCodigo()))).thenReturn(agencia);

        MvcResult mvcResult =
                mockMvc.perform(get("/agencias/consulta-banco/" + banco.getCodigo())
                        .contentType("application/json"))
                        .andExpect(status().isOk()).andReturn();

        Agencia retorno = parseResponse(mvcResult, Agencia.class);

        verify(agenciaService).buscaPorBanco(eq(banco.getCodigo()));

        assertThat(retorno.getId(), equalTo(1));
        assertThat(retorno.getBanco().getCodigo(), equalTo(222));

    }

    @Test
    void testaBuscaPorBancoNegativo() throws Exception {
        Integer codigoInexistente = 999;

        when(agenciaService.buscaPorBanco(eq(codigoInexistente)))
                .thenThrow(new RegistroNaoEncontradoException("codigo: " + codigoInexistente + " não encontrado"));

        MvcResult mvcResult = mockMvc.perform(get("/agencias/consulta-banco/" + codigoInexistente).contentType("application/json"))
                .andExpect(status().isNotFound())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertTrue(response.contains("codigo: " + codigoInexistente + " não encontrado"));
        verify(agenciaService).buscaPorBanco(eq(codigoInexistente));
    }

    @Test
    void testaBuscaPorNumero() throws Exception {
        Banco banco = buildB(1, 222, "Banco bom", "1234");
        Agencia agencia = buildA(1, 111, "Agencia boa", banco);

        when(agenciaService.busca(eq(banco.getCodigo()))).thenReturn(agencia);

        MvcResult mvcResult =
                mockMvc.perform(get("/agencias/" + banco.getCodigo())
                                .contentType("application/json"))
                        .andExpect(status().isOk()).andReturn();

        Agencia retorno = parseResponse(mvcResult, Agencia.class);

        verify(agenciaService).busca(eq(banco.getCodigo()));

        assertThat(retorno.getId(), equalTo(1));
        assertThat(retorno.getBanco().getCodigo(), equalTo(222));

    }

    @Test
    void testaBuscaPorNumeroNegativo() throws Exception {
        Integer numeroInexitente = 2;

        when(agenciaService.busca(eq(numeroInexitente)))
                .thenThrow(new RegistroNaoEncontradoException("numero: " + numeroInexitente + " não encontrado"));

        MvcResult mvcResult = mockMvc.perform(get("/agencias/" + numeroInexitente).contentType("application/json"))
                .andExpect(status().isNotFound())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        assertTrue(response.contains("numero: " + numeroInexitente + " não encontrado"));
        verify(agenciaService).busca(eq(numeroInexitente));
    }

    @Test
    void testaEditar() throws Exception {
        Banco banco = new Banco(1, 222, "Banco bom", "1234");
        Agencia agencia = new Agencia(1, 111, "Agência boa", banco);

        when(agenciaService.editar(eq(1), any(Agencia.class))).thenReturn(agencia);

        MvcResult mvcResult = mockMvc.perform(put("/agencias/1")
                        .contentType("application/json")
                        .content(MAPPER.writeValueAsString(agencia)))
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();

        Agencia retorno = MAPPER.readValue(json, Agencia.class);

        verify(agenciaService, times(1)).editar(eq(1), any(Agencia.class));
        assertThat(retorno.getId(), equalTo(1));
        assertThat(retorno.getNumero(), equalTo(111));
        assertThat(retorno.getBanco().getCodigo(), equalTo(222));
    }

    @Test
    void testaEditarNegativo() throws Exception {
        Banco banco = new Banco(1, 222, "Banco bom", "1234");
        Agencia agencia = new Agencia(1, 111, "Agência boa", banco);

        when(agenciaService.editar(eq(1), any(Agencia.class)))
                .thenThrow(new RegistroNaoEncontradoException("Agência não encontrada"));

        mockMvc.perform(put("/agencias/1")
                        .contentType("application/json")
                        .content(MAPPER.writeValueAsString(agencia)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testaRemover() throws Exception {
        Banco banco = buildB(1, 222, "Banco bom", "1234");
        Agencia agencia = buildA(999, 111, "Agencia falsa", banco);


        doNothing().when(agenciaService).remover(agencia.getId());

        mockMvc.perform(delete("/agencias/" + agencia.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(agenciaService, times(1)).remover(agencia.getId());
    }

    @Test
    void testaRemoverNegativo() throws Exception {
        Integer id = 999;

        doThrow(new RegistroNaoEncontradoException("Agencia não encontrada"))
                .when(agenciaService).remover(id);

        mockMvc.perform(delete("/agencias/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(agenciaService, times(1)).remover(id);
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
