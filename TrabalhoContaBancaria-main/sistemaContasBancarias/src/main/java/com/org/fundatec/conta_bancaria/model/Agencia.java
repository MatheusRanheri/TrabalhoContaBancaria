package com.org.fundatec.conta_bancaria.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "agencia")
public class Agencia {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pessoa_sequence")
    private Integer id;

    @NotNull(message = "O atributo número é obrigatorio")
    @Column
    private Integer numero;

    @NotBlank(message = "O atributo nome é obrigatório")
    @Column
    private String nome;

    @NotNull(message = "O banco é um atributo obrigatório")
    @ManyToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "id_banco", referencedColumnName = "id")
    private Banco banco;

//    @OneToMany(mappedBy = "agencia")
//    private List<Conta> contas;

    public Agencia(){

    }

    public Agencia(Integer id, Integer numero, String nome, Banco banco) {
        this.id = id;
        this.banco = banco;
        this.numero = numero;
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

//    public List<Conta> getContas() {
//        return contas;
//    }
//
//    public void setContas(List<Conta> contas) {
//        this.contas = contas;
//    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Agencia agencia = (Agencia) o;
        return Objects.equals(id, agencia.id) && Objects.equals(banco, agencia.banco) && Objects.equals(numero, agencia.numero) && Objects.equals(nome, agencia.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, banco, numero, nome);
    }

    @Override
    public String toString() {
        return "Agencia{" +
                "id=" + id +
                ", banco=" + banco +
                ", numero=" + numero +
                ", nome='" + nome + '\'' +
                '}';
    }
}
