package br.unitins.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "instrutor")
public class Instrutor extends DefaultEntity {

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(name = "registro_profissional", nullable = false, unique = true, length = 50)
    private String registroProfissional;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "autoescola_id", nullable = false)
    private Autoescola autoescola;

    @ManyToMany
    @JoinTable(
        name = "instrutor_categoria_habilitacao",
        joinColumns = @JoinColumn(name = "instrutor_id"),
        inverseJoinColumns = @JoinColumn(name = "categoria_habilitacao_id")
    )
    private Set<CategoriaHabilitacao> categorias = new HashSet<>();

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRegistroProfissional() {
        return registroProfissional;
    }

    public void setRegistroProfissional(String registroProfissional) {
        this.registroProfissional = registroProfissional;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Autoescola getAutoescola() {
        return autoescola;
    }

    public void setAutoescola(Autoescola autoescola) {
        this.autoescola = autoescola;
    }

    public Set<CategoriaHabilitacao> getCategorias() {
        return categorias;
    }

    public void setCategorias(Set<CategoriaHabilitacao> categorias) {
        this.categorias = categorias;
    }
}
