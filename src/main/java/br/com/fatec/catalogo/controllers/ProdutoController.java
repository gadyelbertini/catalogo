package br.com.fatec.catalogo.controllers;

import br.com.fatec.catalogo.models.ProdutoModel;
import br.com.fatec.catalogo.services.CategoriaService;
import br.com.fatec.catalogo.services.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService service;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listar(@RequestParam(value = "nome", required = false) String nome,
                         @RequestParam(value = "categoriaId", required = false) Long categoriaId,
                         Model model) {

        if (nome != null && !nome.isBlank()) {
            model.addAttribute("produtos", service.listarPorNome(nome));
        } else if (categoriaId != null) {
            model.addAttribute("produtos", service.listarPorCategoria(categoriaId));
        } else {
            model.addAttribute("produtos", service.listarTodos());
        }

        model.addAttribute("categorias", categoriaService.listarTodas());
        return "lista-produtos";
    }

    @GetMapping("/historico")
    public String historico(Model model) {
        model.addAttribute("produtos", service.listarHistorico());
        return "historico-produtos";
    }

    @GetMapping("/novo")
    public String exibirFormulario(Model model) {
        model.addAttribute("produto", new ProdutoModel());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "cadastro-produto";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("produto") ProdutoModel produto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "cadastro-produto";
        }

        try {
            service.salvar(produto);
        } catch (IllegalArgumentException ex) {
            result.addError(new FieldError("produto", "quantidade", ex.getMessage()));
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "cadastro-produto";
        } catch (RuntimeException ex) {
            result.addError(new FieldError("produto", "nome", ex.getMessage()));
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "cadastro-produto";
        }

        redirectAttributes.addFlashAttribute("successMessage",
                String.format("Operação realizada com sucesso em %s.", produto.getDataAtualizacao()));
        return "redirect:/produtos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable long id, Model model) {
        model.addAttribute("produto", service.buscarPorId(id));
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "cadastro-produto";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable long id, RedirectAttributes redirectAttributes) {
        service.excluir(id);
        redirectAttributes.addFlashAttribute("successMessage", "Produto excluído com sucesso.");
        return "redirect:/produtos";
    }


}