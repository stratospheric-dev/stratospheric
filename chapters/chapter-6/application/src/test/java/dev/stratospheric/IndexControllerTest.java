package dev.stratospheric;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(IndexController.class)
class IndexControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldReturnIndexPage() throws Exception {
    this.mockMvc.perform(get("/"))
      .andExpect(status().isOk())
      .andExpect(view().name("index"));
  }
}
