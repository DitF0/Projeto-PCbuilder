package br.edu.eseg.pcbuilder.controller;

import br.edu.eseg.pcbuilder.model.Componente;
import br.edu.eseg.pcbuilder.model.Configuracao;
import br.edu.eseg.pcbuilder.model.ItemConfiguracao;
import br.edu.eseg.pcbuilder.repository.CategoriaRepository;
import br.edu.eseg.pcbuilder.repository.ComponenteRepository;
import br.edu.eseg.pcbuilder.service.ConfiguracaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class PcBuilderController {

    private final ComponenteRepository componenteRepository;
    private final CategoriaRepository categoriaRepository;
    private final ConfiguracaoService configuracaoService;

    public PcBuilderController(ComponenteRepository componenteRepository,
                               CategoriaRepository categoriaRepository,
                               ConfiguracaoService configuracaoService) {
        this.componenteRepository = componenteRepository;
        this.categoriaRepository = categoriaRepository;
        this.configuracaoService = configuracaoService;
    }

    @GetMapping
    public String exibirDashboard(Model model) {
        List<Configuracao> builds = configuracaoService.buscarBuildsDoUsuario();

        double totalInvestido = builds.stream()
                .mapToDouble(Configuracao::getValorTotal)
                .sum();

        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("componentes", componenteRepository.findAll());
        model.addAttribute("buildsSalvas", builds);
        String totalFormatado = String.format("R$ %,.2f", totalInvestido)
                .replace(",", "X").replace(".", ",").replace("X", ".");
        model.addAttribute("totalInvestido", totalFormatado);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/salvar")
    public String salvarBuild(@RequestParam("nomeBuild") String nomeBuild,
                              @RequestParam(value = "componenteId", required = false) List<Long> componenteIds,
                              @RequestParam(value = "quantidade", required = false) List<Integer> quantidades,
                              RedirectAttributes redirectAttributes) {
        try {
            Configuracao build = new Configuracao();
            build.setNomeBuild(nomeBuild);

            if (componenteIds != null && quantidades != null) {
                for (int i = 0; i < componenteIds.size(); i++) {
                    Long componenteId = componenteIds.get(i);
                    Integer quantidade = quantidades.get(i);

                    if (componenteId != null && componenteId > 0 && quantidade != null && quantidade > 0) {
                        Componente componente = componenteRepository.findById(componenteId)
                                .orElseThrow(() -> new IllegalArgumentException("Componente não encontrado."));
                        ItemConfiguracao item = new ItemConfiguracao();
                        item.setComponente(componente);
                        item.setQuantidade(quantidade);
                        build.getItens().add(item);
                    }
                }
            }

            configuracaoService.salvarBuild(build);
            redirectAttributes.addFlashAttribute("sucesso", "Build montada e validada com sucesso!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro inesperado ao salvar. Tente novamente.");
        }

        return "redirect:/";
    }

    @GetMapping("/editar/{id}")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model,
                                         RedirectAttributes redirectAttributes) {
        Optional<Configuracao> configuracaoOptional = configuracaoService.buscarPorId(id);

        if (configuracaoOptional.isPresent()) {
            Configuracao configuracao = configuracaoOptional.get();

            // Mapas simples para o template usar sem lambdas
            Map<Long, Long> componentesPorCategoria = new java.util.HashMap<>();
            Map<Long, Integer> quantidadesPorCategoria = new java.util.HashMap<>();
            for (ItemConfiguracao item : configuracao.getItens()) {
                Long catId = item.getComponente().getCategoria().getId();
                componentesPorCategoria.put(catId, item.getComponente().getId());
                quantidadesPorCategoria.put(catId, item.getQuantidade());
            }

            model.addAttribute("configuracaoParaEditar", configuracao);
            model.addAttribute("componentesPorCategoria", componentesPorCategoria);
            model.addAttribute("quantidadesPorCategoria", quantidadesPorCategoria);
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("componentes", componenteRepository.findAll());
            return "editar-build";
        }

        redirectAttributes.addFlashAttribute("erro", "Build não encontrada.");
        return "redirect:/";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarBuild(@PathVariable Long id,
                                 @RequestParam("nomeBuild") String nomeBuild,
                                 @RequestParam(value = "componenteId", required = false) List<Long> componenteIds,
                                 @RequestParam(value = "quantidade", required = false) List<Integer> quantidades,
                                 RedirectAttributes redirectAttributes) {
        try {
            Configuracao build = configuracaoService.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Build não encontrada para atualização."));

            build.setNomeBuild(nomeBuild);
            build.getItens().clear();

            if (componenteIds != null && quantidades != null) {
                for (int i = 0; i < componenteIds.size(); i++) {
                    Long componenteId = componenteIds.get(i);
                    Integer quantidade = quantidades.get(i);

                    if (componenteId != null && componenteId > 0 && quantidade != null && quantidade > 0) {
                        Componente componente = componenteRepository.findById(componenteId)
                                .orElseThrow(() -> new IllegalArgumentException("Componente não encontrado."));
                        ItemConfiguracao item = new ItemConfiguracao();
                        item.setComponente(componente);
                        item.setQuantidade(quantidade);
                        build.getItens().add(item);
                    }
                }
            }

            configuracaoService.salvarBuild(build);
            redirectAttributes.addFlashAttribute("sucesso", "Build atualizada com sucesso!");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro inesperado ao atualizar. Tente novamente.");
        }

        return "redirect:/";
    }

    @PostMapping("/excluir/{id}")
    public String excluirBuild(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            configuracaoService.excluirBuild(id);
            redirectAttributes.addFlashAttribute("sucesso", "Build excluída com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro inesperado ao excluir.");
        }

        return "redirect:/";
    }
}
