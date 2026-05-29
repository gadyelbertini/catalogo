package br.com.fatec.catalogo.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProdutoControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void historico_shouldReturnOkForAdmin() throws Exception {
        mockMvc.perform(get("/produtos/historico")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void historico_shouldReturnForbiddenForUser() throws Exception {
        mockMvc.perform(get("/produtos/historico")).andExpect(status().isForbidden());
    }
}
