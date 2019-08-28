import REST.Application;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.FileInputStream;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileUploadControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    //Testa se os objetos necessários para executar o teste estão nulos
    @Test
    public void whenContexLoads_thenShouldNotBeNull() {
        assertNotNull(mockMvc);
    }


    //Testa o envio de arquivos Multipart. O teste pega o arquivo JUNIT_test_sample
    // e utiliza a API para fazer o upload para a AWS
    @Test
    public void whenUploadMultipartFile_thenShouldReturnStatusOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "JUNIT_test_sample.mkv", MediaType.ALL_VALUE, new FileInputStream("JUNIT_test_sample.mkv"));
        fileNameResult = this.mockMvc.perform(fileUpload("/api/v1/files/upload").file(file))
                .andDo(print()).andExpect(content().string(containsString("JUNIT_test_sample.mkv"))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

    }


    public static String fileNameResult;
}