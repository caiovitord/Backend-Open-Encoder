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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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


    //Testa se os objetos necessários para executar o teste estão nulos
    @Test
    public void a_whenContexLoads_thenShouldNotBeNull() {
        assertNotNull(controller);
        assertNotNull(mockMvc);
    }


    private static HashMap<String,Object> requestResult;

    //Após enviar o arquivo no teste de FileUploadController, esse teste
    // cria a requisição de encoding. É verificado o statusCode, juntamente com todos os dados
    // que deveriam estar no JSON de resposta.
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

    // Após realizar  pedido de encoding, esse teste
    // cria a requisição para obter esse encoding que acaba de ser criado
    // É verificado o statusCode, e se todos os dados correspondem
    @Test
    public void c_whenGetEncoding_thenShouldReturnStatusOk_AndReturnEncodingJSON() throws Exception {
        String jsonResponse = this.mockMvc.perform(
                get("/api/v1/encodings/" + requestResult.get("encodingId")))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        HashMap<String,Object> getResult =
                new ObjectMapper().readValue(jsonResponse, HashMap.class);
        assertNotNull(requestResult);

        assertEquals(requestResult.get("encodingId").toString(), getResult.get("encodingId").toString());
        assertEquals(requestResult.get("outputPath").toString(), getResult.get("outputPath").toString());
        assertEquals(requestResult.get("audioStreamId").toString(), getResult.get("audioStreamId").toString());
        assertEquals(requestResult.get("fmp4AudioMuxinId").toString(), getResult.get("fmp4AudioMuxinId").toString());
        assertEquals(requestResult.get("streamVideoId").toString(), getResult.get("streamVideoId").toString());
        assertEquals(requestResult.get("videoMuxinId").toString(), getResult.get("videoMuxinId").toString());
        assertEquals(requestResult.get("encodingQuality").toString(), getResult.get("encodingQuality").toString());
    }

    // Após realizar  pedido de encoding, esse teste
    // cria a requisição para checar o status do encoding.
    // É verificado o statusCode, juntamente com todos os dadosque deveriam estar no JSON de resposta de status.
    @Test
    public void d_whenGetEncodingStatus_thenShouldReturnStatusOk_AndReturnStatusJSON() throws Exception {


        String jsonResponse = this.mockMvc.perform(
                get("/api/v1/encodings/" + requestResult.get("encodingId") + "/status"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        //Verifica todos os dados do JSON de resposta
        assertThat(jsonResponse, containsString("status"));
        assertThat(jsonResponse, containsString("eta"));
        assertThat(jsonResponse, containsString("progress"));
    }


    // Após realizar  pedido de encoding, esse teste
    // cria a requisição para solicitar o link do encoding.
    // É verificado o statusCode, e verificado se a string do link é condizente.
    @Test
    public void e_whenGetLink_thenShouldReturnStatusOk_AndReturnLink() throws Exception {
        String jsonResponse = this.mockMvc.perform(
                get("/api/v1/encodings/" + requestResult.get("encodingId") + "/link"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        //Verifica todos os dados do JSON de resposta
        assertThat(jsonResponse, containsString("https://"));
        assertThat(jsonResponse, containsString(".s3.amazonaws.com"));
        assertThat(jsonResponse, containsString("manifest.m3u8"));
    }


    //Esse teste verifica o retorno 404 de todos os endpoints de encoding
    @Test
    public void f_whenTryGetWrongEncoding_thenShouldReturnStatus404() throws Exception {
        this.mockMvc.perform(
                get("/api/v1/encodings/NAOEXISTE"))
                .andDo(print())
                .andExpect(status().is(404));
        this.mockMvc.perform(
                get("/api/v1/encodings/NAOEXISTE/link"))
                .andDo(print())
                .andExpect(status().is(404));
        this.mockMvc.perform(
                get("/api/v1/encodings/NAOEXISTE/status"))
                .andDo(print())
                .andExpect(status().is(404));
    }

    //Esse teste verifica o retorno 404 quando a pessoa não enviou o arquivo previamente
    @Test
    public void g_whenTryCreateWrongEncoding_thenShouldReturnStatus404() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("fileName", "arquivoinexistente.mp4");
        obj.put("encodingQuality", VideoConfigurationEnum.LOW.toString());

        this.mockMvc.perform(
                post("/api/v1/encodings")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(obj.toString()))
                .andDo(print())
                .andExpect(status().is(404));
    }




}
