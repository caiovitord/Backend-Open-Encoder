import Persistence.Entities.VideoEncodingRequest;
import REST.Application;
import REST.Controllers.VideoEncodingRequestController;
import Services.Encoding.VideoConfigurationEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import java.util.HashMap;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VideoEncReqControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    VideoEncodingRequestController controller;



    @Test
    public void a_whenContexLoads_thenShouldNotBeNull() {
        assertNotNull(controller);
        assertNotNull(mockMvc);
    }


    private static HashMap<String,Object> requestResult;

    @Test
    public void b_whenUploadMultipartFile_AndCreateEncoding_thenShouldReturnStatusOk_AndReturnJSON() throws Exception {
        assertNotNull(FileUploadControllerIntegrationTest.fileNameResult);

        JSONObject obj = new JSONObject();
        obj.put("fileName", FileUploadControllerIntegrationTest.fileNameResult);
        obj.put("encodingQuality", VideoConfigurationEnum.LOW.toString());

        String jsonResponse = this.mockMvc.perform(
                post("/api/v1/encodings")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(obj.toString()))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        //Verifica todos os dados do JSON de resposta
        assertThat(jsonResponse, containsString("encodingId"));
        assertThat(jsonResponse, containsString("outputPath"));
        assertThat(jsonResponse, containsString("audioStreamId"));
        assertThat(jsonResponse, containsString("fmp4AudioMuxinId"));
        assertThat(jsonResponse, containsString("streamVideoId"));
        assertThat(jsonResponse, containsString("videoMuxinId"));
        assertThat(jsonResponse, containsString("encodingQuality"));
        assertThat(jsonResponse, containsString("LOW"));

        requestResult =
                new ObjectMapper().readValue(jsonResponse, HashMap.class);
        assertNotNull(requestResult.get("encodingId"));

    }

    @Test
    public void c_whenGetEncodingStatus_thenShouldReturnStatusOk_AndReturnStatusJSON() throws Exception {


        String jsonResponse = this.mockMvc.perform(
                get("/api/v1/encodings/" + requestResult.get("encodingId") + "/status"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        //Verifica todos os dados do JSON de resposta
        assertThat(jsonResponse, containsString("status"));
        assertThat(jsonResponse, containsString("eta"));
        assertThat(jsonResponse, containsString("progress"));
    }

    @Test
    public void d_whenGetLink_thenShouldReturnStatusOk_AndReturnLink() throws Exception {
        String jsonResponse = this.mockMvc.perform(
                get("/api/v1/encodings/" + requestResult.get("encodingId") + "/link"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        //Verifica todos os dados do JSON de resposta
        assertThat(jsonResponse, containsString("https://"));
        assertThat(jsonResponse, containsString(".s3.amazonaws.com"));
        assertThat(jsonResponse, containsString("manifest.m3u8"));
    }




}
