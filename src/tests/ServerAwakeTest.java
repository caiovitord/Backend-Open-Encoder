import REST.Application;
import REST.Controllers.VideoEncodingRequestController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class ServerAwakeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenContexLoads_thenShouldNotBeNull() {
        assertNotNull(mockMvc);
    }

    @Test
    public void whenTryToContactServer_thenItShouldAnswer() throws Exception {
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello")));
        this.mockMvc.perform(get("/api/v1/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello")));
    }


}
