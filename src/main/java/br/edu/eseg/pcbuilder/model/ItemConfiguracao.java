package br.edu.eseg.pcbuilder.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class ItemConfiguracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "configuracao_id")
    private Configuracao configuracao;

    @ManyToOne
    @JoinColumn(name = "componente_id")
    private Componente componente;

    @NotNull
    @Min(1)
    private Integer quantidade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Configuracao getConfiguracao() {
        return configuracao;
    }

    public void setConfiguracao(Configuracao configuracao) {
        this.configuracao = configuracao;
    }

    public Componente getComponente() {
        return componente;
    }

    public void setComponente(Componente componente) {
        this.componente = componente;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPrecoTotal() {
        return componente.getPreco() * quantidade;
    }

    public Integer getTdpTotal() {
        return componente.getTdpWatts() * quantidade;
    }
    public String getPrecoTotalFormatado() {
        return String.format("R$ %,.2f", getPrecoTotal())
                .replace(",", "X").replace(".", ",").replace("X", ".");
    }
}
