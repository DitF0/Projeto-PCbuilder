package br.edu.eseg.pcbuilder.service;

import br.edu.eseg.pcbuilder.model.Configuracao;
import br.edu.eseg.pcbuilder.model.ItemConfiguracao;
import br.edu.eseg.pcbuilder.model.Usuario;
import br.edu.eseg.pcbuilder.repository.ConfiguracaoRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConfiguracaoService {

    private final ConfiguracaoRepository configuracaoRepository;

    public ConfiguracaoService(ConfiguracaoRepository configuracaoRepository) {
        this.configuracaoRepository = configuracaoRepository;
    }

    @Transactional
    public Configuracao salvarBuild(Configuracao build) {
        Usuario usuarioLogado = getUsuarioLogado();
        build.setUsuario(usuarioLogado);

        validarMinimoDeCategorias(build);
        validarCompatibilidadeEnergetica(build);

        for (ItemConfiguracao item : build.getItens()) {
            item.setConfiguracao(build);
        }

        return configuracaoRepository.save(build);
    }

    @Transactional
    public void excluirBuild(Long id) {
        Usuario usuarioLogado = getUsuarioLogado();
        Configuracao build = configuracaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Build não encontrada."));

        if (!build.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new IllegalArgumentException("Você não tem permissão para excluir esta build.");
        }

        configuracaoRepository.delete(build);
    }

    public List<Configuracao> buscarBuildsDoUsuario() {
        return configuracaoRepository.findByUsuario(getUsuarioLogado());
    }

    // CORRECAO: este metodo estava sendo chamado no controller mas nao existia no servico
    public Optional<Configuracao> buscarPorId(Long id) {
        return configuracaoRepository.findById(id);
    }


    // REGRA 1: build deve ter pelo menos 3 categorias distintas

    private void validarMinimoDeCategorias(Configuracao build) {
        long categoriasDiferentes = build.getItens().stream()
                .map(item -> item.getComponente().getCategoria().getId())
                .distinct()
                .count();

        if (categoriasDiferentes < 3) {
            throw new IllegalArgumentException(
                    "Sua build precisa ter componentes de pelo menos 3 categorias diferentes. " +
                    "Categorias selecionadas: " + categoriasDiferentes + "."
            );
        }
    }


    // REGRA 2: build deve conter uma Fonte de alimentação
    // REGRA 3: consumo total não pode ultrapassar 80% da capacidade da Fonte

    private void validarCompatibilidadeEnergetica(Configuracao build) {
        int capacidadeFonte = build.getItens().stream()
                .filter(item -> item.getComponente().getCategoria().getNome().equalsIgnoreCase("Fonte"))
                .mapToInt(item -> item.getComponente().getTdpWatts() * item.getQuantidade())
                .sum();

        if (capacidadeFonte == 0) {
            throw new IllegalArgumentException(
                    "Sua montagem precisa de uma Fonte de alimentação."
            );
        }

        int consumoTotal = build.getItens().stream()
                .filter(item -> !item.getComponente().getCategoria().getNome().equalsIgnoreCase("Fonte"))
                .mapToInt(ItemConfiguracao::getTdpTotal)
                .sum();

        int limiteSeguro = (int) (capacidadeFonte * 0.80);

        if (consumoTotal > limiteSeguro) {
            throw new IllegalArgumentException(
                    "A fonte não suporta esta configuração com segurança. " +
                    "Consumo estimado: " + consumoTotal + "W. " +
                    "Limite seguro da fonte (80%): " + limiteSeguro + "W. " +
                    "Escolha uma fonte maior ou reduza os componentes."
            );
        }
    }

    private Usuario getUsuarioLogado() {
        return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
