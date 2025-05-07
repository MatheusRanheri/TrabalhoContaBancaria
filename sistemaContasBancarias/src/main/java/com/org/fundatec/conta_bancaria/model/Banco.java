package com.org.fundatec.conta_bancaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "banco")
public class Banco {

    @Id
    @GeneratedValue
    @Column
    private Integer id;

    @NotNull(message = "O código é um campo obrigatório")
    @Column
    private Integer codigo;

    @NotBlank(message = "O nome é um campo obrigatório")
    @Column
    private String nome;

    @NotBlank(message = "O cnpj é um campo obrigatório")
    @Column
    private String cnpj;

//    @OneToMany(mappedBy = "banco")
//    private List<Agencia> agencias;

    public Banco(){

    }

    public Banco(Integer id, Integer codigo, String nome, String cnpj) {
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.cnpj = cnpj;
    }

    public Integer getId() {
        return id;
    }

//    public List<Agencia> getAgencias() {
//        return agencias;
//    }
//
//    public void setAgencias(List<Agencia> agencias) {
//        this.agencias = agencias;
//    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Banco banco = (Banco) o;
        return id == banco.id && codigo == banco.codigo && Objects.equals(nome, banco.nome) && Objects.equals(cnpj, banco.cnpj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, codigo, nome, cnpj);
    }

    @Override
    public String toString() {
        return "Banco{" +
                "id=" + id +
                ", codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", cnpj='" + cnpj + '\'' +
                '}';
    }
}
