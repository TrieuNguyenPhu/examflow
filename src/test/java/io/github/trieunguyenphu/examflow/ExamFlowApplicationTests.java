package io.github.trieunguyenphu.examflow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExamFlowApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void publicPagesRender() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/login")).andExpect(status().isOk());
        mvc.perform(get("/register")).andExpect(status().isOk());
    }

    @Test
    void anonymousUsersCannotAccessAdministration() throws Exception {
        mvc.perform(get("/admin/dashboard")).andExpect(status().is3xxRedirection());
    }

    @Test
    void studentsCannotAccessAdministration() throws Exception {
        mvc.perform(get("/admin/dashboard").with(user("student@example.com").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void postRequestsRequireCsrf() throws Exception {
        mvc.perform(post("/register")).andExpect(status().isForbidden());
        mvc.perform(post("/register").with(csrf())).andExpect(status().isOk());
    }
}
