package com.org.fundatec.conta_bancaria.exception.handler;

public class ErrorResponse {

    private Integer codigo;
    private String mensagem;

    public ErrorResponse(){

    }

    public ErrorResponse(Integer codigo, String mensagem){
        this.codigo = codigo;
        this.mensagem = mensagem;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getMensagem() {
        return mensagem;
    }
}
