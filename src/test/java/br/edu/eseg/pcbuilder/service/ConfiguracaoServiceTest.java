package br.edu.eseg.pcbuilder.service;

import br.edu.eseg.pcbuilder.model.Categoria;
import br.edu.eseg.pcbuilder.model.Componente;
import br.edu.eseg.pcbuilder.model.Configuracao;
import br.edu.eseg.pcbuilder.model.ItemConfiguracao;
import br.edu.eseg.pcbuilder.model.Usuario;
import br.edu.eseg.pcbuilder.repository.ConfiguracaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConfiguracaoServiceTest {

    @Mock
    private ConfiguracaoRepository configuracaoRepository;

    @InjectMocks
    private ConfiguracaoService configuracaoService;

    private Categoria categoriaCpu;
    private Categoria categoriaGpu;
    private Categoria categoriaRam;
    private Categoria categoriaFonte;

    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Teste");
        usuario.setEmail("teste@test.com");
        usuario.setSenha("senha");
        usuario.setRole("ROLE_USER");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuario, null, List.of())
        );

        categoriaCpu = new Categoria();
        categoriaCpu.setId(1L);
        categoriaCpu.setNome("Processador");

        categoriaGpu = new Categoria();
        categoriaGpu.setId(2L);
        categoriaGpu.setNome("Placa de Video");

        categoriaRam = new Categoria();
        categoriaRam.setId(3L);
        categoriaRam.setNome("Memoria RAM");

        categoriaFonte = new Categoria();
        categoriaFonte.setId(7L);
        categoriaFonte.setNome("Fonte");
    }


    // Regra 1: minimo de 3 categorias

    @Test
    void salvarBuild_deveLancarExcecao_quandoMenosDeTresCategorias() {
        Configuracao build = buildComUmaCategoriaApenas();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> configuracaoService.salvarBuild(build));

        assertTrue(ex.getMessage().contains("pelo menos 3 categorias"));
        verify(configuracaoRepository, never()).save(any());
    }


    // Regra 2: obrigatoriedade de Fonte

    @Test
    void salvarBuild_deveLancarExcecao_quandoSemFonte() {
        Configuracao build = buildSemFonte();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> configuracaoService.salvarBuild(build));

        assertTrue(ex.getMessage().contains("Fonte de alimentação"));
        verify(configuracaoRepository, never()).save(any());
    }


    // Regra 3: consumo nao pode superar 80% da fonte

    @Test
    void salvarBuild_deveLancarExcecao_quandoFonteInsuficiente() {
        Configuracao build = buildComFonteInsuficiente();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> configuracaoService.salvarBuild(build));

        assertTrue(ex.getMessage().contains("não suporta"));
        verify(configuracaoRepository, never()).save(any());
    }


    // Caminho feliz: build valida deve ser salva

    @Test
    void salvarBuild_deveSalvarComSucesso_quandoTodaValidacaoPassar() {
        Configuracao build = buildValida();
        when(configuracaoRepository.save(any(Configuracao.class))).thenReturn(build);

        Configuracao resultado = configuracaoService.salvarBuild(build);

        assertNotNull(resultado);
        verify(configuracaoRepository, times(1)).save(build);
    }


    // Helpers para montar cenários de teste


    private ItemConfiguracao criarItem(Categoria categoria, int tdpWatts, int quantidade) {
        Componente comp = new Componente();
        comp.setNome("Componente Teste");
        comp.setPreco(100.0);
        comp.setTdpWatts(tdpWatts);
        comp.setCategoria(categoria);

        ItemConfiguracao item = new ItemConfiguracao();
        item.setComponente(comp);
        item.setQuantidade(quantidade);
        return item;
    }

    private Configuracao buildComUmaCategoriaApenas() {
        Configuracao build = new Configuracao();
        build.setNomeBuild("Só CPU");
        build.getItens().add(criarItem(categoriaCpu, 100, 1));
        return build;
    }

    private Configuracao buildSemFonte() {
        Configuracao build = new Configuracao();
        build.setNomeBuild("Sem Fonte");
        build.getItens().add(criarItem(categoriaCpu, 100, 1));
        build.getItens().add(criarItem(categoriaGpu, 150, 1));
        build.getItens().add(criarItem(categoriaRam, 10, 1));
        return build;
    }

    private Configuracao buildComFonteInsuficiente() {
        Configuracao build = new Configuracao();
        build.setNomeBuild("Fonte Fraca");
        build.getItens().add(criarItem(categoriaCpu, 300, 1));
        build.getItens().add(criarItem(categoriaGpu, 400, 1));
        build.getItens().add(criarItem(categoriaRam, 20, 1));
        // Fonte de 400W não suporta 720W de consumo (limite 80% = 320W)
        build.getItens().add(criarItem(categoriaFonte, 400, 1));
        return build;
    }

    private Configuracao buildValida() {
        Configuracao build = new Configuracao();
        build.setNomeBuild("Build Valida");
        build.getItens().add(criarItem(categoriaCpu, 125, 1));
        build.getItens().add(criarItem(categoriaGpu, 200, 1));
        build.getItens().add(criarItem(categoriaRam, 15, 1));
        // Fonte de 850W, consumo = 340W, limite 80% = 680W — passa
        build.getItens().add(criarItem(categoriaFonte, 850, 1));
        return build;
    }
}
