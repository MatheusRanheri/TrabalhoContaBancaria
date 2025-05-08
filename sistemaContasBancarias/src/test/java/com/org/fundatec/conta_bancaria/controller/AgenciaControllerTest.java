package com.org.fundatec.conta_bancaria.controller;

import com.org.fundatec.conta_bancaria.SistemaContasBancariasApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = SistemaContasBancariasApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AgenciaControllerTest {
}
