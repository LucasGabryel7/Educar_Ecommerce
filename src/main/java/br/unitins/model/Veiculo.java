package br.unitins.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "veiculo")
public class Veiculo extends DefaultEntity {

    @Column(nullable = false, unique = true, length = 10)
    private String placa;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 50)
    private String modelo;

    @Column(name = "ano_fabricacao", nullable = false)
    private Integer anoFabricacao;

    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "autoescola_id", nullable = false)
    private Autoescola autoescola;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_habilitacao_id", nullable = false)
    private CategoriaHabilitacao categoriaHabilitacao;

    @OneToOne(mappedBy = "veiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private DocumentoVeiculo documentoVeiculo;

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAnoFabricacao() {
        return anoFabricacao;
    }

    public void setAnoFabricacao(Integer anoFabricacao) {
        this.anoFabricacao = anoFabricacao;
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

    public CategoriaHabilitacao getCategoriaHabilitacao() {
        return categoriaHabilitacao;
    }

    public void setCategoriaHabilitacao(CategoriaHabilitacao categoriaHabilitacao) {
        this.categoriaHabilitacao = categoriaHabilitacao;
    }

    public DocumentoVeiculo getDocumentoVeiculo() {
        return documentoVeiculo;
    }

    public void setDocumentoVeiculo(DocumentoVeiculo documentoVeiculo) {
        this.documentoVeiculo = documentoVeiculo;
        if (documentoVeiculo != null) {
            documentoVeiculo.setVeiculo(this);
        }
    }
}
