package Services.Encoding;

import Services.Storage.BucketsEnum;
import Services.Storage.AmazonS3Service;
import com.bitmovin.api.BitmovinApi;
import com.bitmovin.api.encoding.codecConfigurations.AACAudioConfig;
import com.bitmovin.api.encoding.codecConfigurations.H264VideoConfiguration;
import com.bitmovin.api.encoding.codecConfigurations.enums.ProfileH264;
import com.bitmovin.api.encoding.inputs.HttpsInput;
import com.bitmovin.api.encoding.outputs.S3Output;
import com.bitmovin.api.exceptions.BitmovinApiException;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * A classe abaixo é responsável por criar e reutilizar as configurações
 * de encoding. Essas configurações são indispensáveis para realizar o encoding correto.
 *
 * A classe salva em um arquivo, os Identificadores dos objetos de configuração.
 * Esses objetos de configuração são mantidos pela API da bitmovin, essa classe só possui
 * a reponsabilidade de manter registro das configurações que já foram criadas, evitando que
 * sejam utilizados recursos desnecessários da API bitmovin.
 *
 * A classe salva em um arquivo os Ids.
 * Quando instanciada, ela tenta buscar esses ids, em caso de falha, ela cria os objetos
 * de configuração, por meio do acesso com a API bitmovin, e depois guarda os dados no arquivo
 * para ser usado posteriormente.
 *
 */


public class EncoderConfigurationService {

    private final BitmovinApi bitmovinApi;


    private String inputId;
    private String outputId;
    private String videoConfigId;
    private String audioConfigId;

    public EncoderConfigurationService(BitmovinApi bitmovinApi) {
        this.bitmovinApi = bitmovinApi;

        try {
            System.out.println("Trying to retrieve config file");
            this.retrieveConfigurationFile();
        } catch (BitmovinApiException e) {
            e.printStackTrace();
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void retrieveConfigurationFile() throws BitmovinApiException, UnirestException, IOException, URISyntaxException {
        try {
            //Busca os Ids nos arquivos
            Scanner sc = new Scanner(new File("bitmovinConfig.ini"));

            inputId = sc.nextLine();
            outputId = sc.nextLine();
            videoConfigId = sc.nextLine();
            audioConfigId = sc.nextLine();

            System.out.println("Retrieved config file. Check out the ids");
            System.out.println("InputId " +  inputId);
            System.out.println("outputId " + outputId);
            System.out.println("videoConfigId " + videoConfigId);
            System.out.println("audioConfigId " +  audioConfigId);

        } catch (FileNotFoundException e) {
            //Em caso de falha, cria os objetos com a API
            System.out.println("Failed. Config file does not exists");
            System.out.println("Trying to create new config file");

            inputId = this.createInput();
            outputId = this.createOutput();
            videoConfigId = this.createVideoConfig(VideoConfigurationEnum.LOW);
            audioConfigId = this.createAudioConfig();


            System.out.println("Created resources: Checkout the IDS:");
            System.out.println("InputId " +  inputId);
            System.out.println("outputId " + outputId);
            System.out.println("videoConfigId " + videoConfigId);
            System.out.println("audioConfigId " +  audioConfigId);

            //Salva os Ids dos objetos de configuração criados.
            this.writeConfigurationToFile();
        }
    }

    private void writeConfigurationToFile() {
        try {
            FileWriter fw = new FileWriter(new File("bitmovinConfig.ini"));

            fw.write(inputId + "\n");
            fw.write(outputId + "\n");
            fw.write(videoConfigId + "\n");
            fw.write(audioConfigId);

            fw.flush();
            fw.close();
            System.out.println("Created new config file");
        } catch (IOException e) {
            System.out.println("Failed to create new config file");
            e.printStackTrace();
        }
    }


    private String createOutput() throws URISyntaxException, BitmovinApiException, UnirestException, IOException {
        S3Output output = new S3Output();

        output.setAccessKey(AmazonS3Service.AWS_ACCESS_KEY);
        output.setSecretKey(AmazonS3Service.AWS_SECRET);
        output.setBucketName(BucketsEnum.OUTPUT_BUCKET.bucketName);
        output = bitmovinApi.output.s3.create(output);
        return output.getId();
    }

    private String createInput() throws URISyntaxException, BitmovinApiException, UnirestException, IOException {
        HttpsInput input = new HttpsInput();
        input.setHost(BucketsEnum.INPUT_BUCKET.bucketHost);
        input = bitmovinApi.input.https.create(input);
        return input.getId();
    }

    private String createAudioConfig() throws URISyntaxException, BitmovinApiException, UnirestException, IOException {
        //Audio codec config
        AACAudioConfig audioCodecConfig = new AACAudioConfig();
        audioCodecConfig.setName("Standart Audio Codec Config");
        audioCodecConfig.setBitrate(128000L);
        audioCodecConfig = bitmovinApi.configuration.audioAAC.create(audioCodecConfig);
        return audioCodecConfig.getId();
    }

    private String createVideoConfig(VideoConfigurationEnum vconf) throws URISyntaxException, BitmovinApiException, UnirestException, IOException {
        if (vconf == VideoConfigurationEnum.LOW) {
            //Video codec config
            H264VideoConfiguration videoCodecConfig = new H264VideoConfiguration();

            videoCodecConfig.setName("Standart LOW H264 Codec Config");
            videoCodecConfig.setBitrate(375000L);
            videoCodecConfig.setWidth(384);
            videoCodecConfig.setProfile(ProfileH264.HIGH);

            videoCodecConfig = bitmovinApi.configuration.videoH264.create(videoCodecConfig);
            return videoCodecConfig.getId();
        }
        throw new IllegalStateException();
    }


    String getOutputId() {
        return this.outputId;
    }

    String getInputId() {
        return this.inputId;
    }

    String getAudioConfigId() {
        return this.audioConfigId;
    }

    String getVideoConfigId(VideoConfigurationEnum vconf) {
        if (vconf == VideoConfigurationEnum.LOW) {
            return this.videoConfigId;
        }
        throw new IllegalStateException();
    }
}
