package com.org.fundatec.conta_bancaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table(name = "conta")
public class Conta {

    @Id
    @GeneratedValue
    private Integer id;

    @NotNull(message = "O atributo número é obrigatorio")
    private Integer numero;

    private Double saldo;

    @NotNull(message = "A agencia é um atributo obrigatorio")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_agencia", referencedColumnName = "id")
    private Agencia agencia;

    @NotNull(message = "O cliente é um atributo obrigatório")
    @ManyToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_cliente", referencedColumnName = "id")
    private Cliente cliente;

    public Conta(){

    }

    public Conta(Integer id, Integer numero, Double saldo, Agencia agencia, Cliente cliente) {
        this.id = id;
        this.numero = numero;
        this.saldo = saldo;
        this.cliente = cliente;
        this.agencia = agencia;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public @NotBlank(message = "A agencia é um campo obrigatorio") Agencia getAgencia() {
        return agencia;
    }

    public void setAgencia(@NotBlank(message = "A agencia é um campo obrigatorio") Agencia agencia) {
        this.agencia = agencia;
    }

    public @NotBlank(message = "O cliente é um campo obrigatório") Cliente getCliente() {
        return cliente;
    }

    public void setCliente(@NotBlank(message = "O cliente é um campo obrigatório") Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta = (Conta) o;
        return Objects.equals(id, conta.id) && Objects.equals(numero, conta.numero) && Objects.equals(saldo, conta.saldo) && Objects.equals(agencia, conta.agencia) && Objects.equals(cliente, conta.cliente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numero, saldo, agencia, cliente);
    }

    @Override
    public String toString() {
        return "Conta{" +
                "id=" + id +
                ", numero=" + numero +
                ", saldo=" + saldo +
                ", agencia=" + agencia +
                ", cliente=" + cliente +
                '}';
    }
}
