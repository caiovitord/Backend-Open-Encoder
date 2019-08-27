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
 * <p>
 * A classe salva em um arquivo, os Identificadores dos objetos de configuração.
 * Esses objetos de configuração são mantidos pela API da bitmovin, essa classe só possui
 * a reponsabilidade de manter registro das configurações que já foram criadas, evitando que
 * sejam utilizados recursos desnecessários da API bitmovin.
 * <p>
 * A classe salva em um arquivo os Ids.
 * Quando instanciada, ela tenta buscar esses ids, em caso de falha, ela cria os objetos
 * de configuração, por meio do acesso com a API bitmovin, e depois guarda os dados no arquivo
 * para ser usado posteriormente.
 */


public class EncoderConfigurationService {

    private final BitmovinApi bitmovinApi;


    private String inputId;
    private String outputId;
    private String lowVideoConfigId;
    private String mediumVideoConfigId;
    private String highVideoConfigId;
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
            lowVideoConfigId = sc.nextLine();
            audioConfigId = sc.nextLine();
            mediumVideoConfigId = sc.nextLine();
            highVideoConfigId = sc.nextLine();

            System.out.println("Retrieved config file. Check out the ids");
            System.out.println("InputId " + inputId);
            System.out.println("outputId " + outputId);
            System.out.println("lowVideoConfigId " + lowVideoConfigId);
            System.out.println("highVideoConfigId " + highVideoConfigId);
            System.out.println("mediumVideoConfigId " + mediumVideoConfigId);
            System.out.println("audioConfigId " + audioConfigId);

        } catch (Exception e) {
            //Em caso de falha, cria os objetos com a API
            System.out.println("Failed. Config file does not exists");
            System.out.println("Trying to create new config file");

            inputId = this.createInput();
            outputId = this.createOutput();
            lowVideoConfigId = this.createVideoConfig(VideoConfigurationEnum.LOW);
            mediumVideoConfigId = this.createVideoConfig(VideoConfigurationEnum.MEDIUM);
            highVideoConfigId = this.createVideoConfig(VideoConfigurationEnum.HIGH);
            audioConfigId = this.createAudioConfig();


            System.out.println("Created resources: Checkout the IDS:");
            System.out.println("InputId " + inputId);
            System.out.println("outputId " + outputId);
            System.out.println("videoConfigId " + lowVideoConfigId);
            System.out.println("audioConfigId " + audioConfigId);
            System.out.println("highVideoConfigId " + highVideoConfigId);
            System.out.println("mediumVideoConfigId " + mediumVideoConfigId);

            //Salva os Ids dos objetos de configuração criados.
            this.writeConfigurationToFile();
        }
    }

    private void writeConfigurationToFile() {
        try {
            FileWriter fw = new FileWriter(new File("bitmovinConfig.ini"));

            fw.write(inputId + "\n");
            fw.write(outputId + "\n");
            fw.write(lowVideoConfigId + "\n");
            fw.write(audioConfigId + "\n");
            fw.write(mediumVideoConfigId + "\n");
            fw.write(highVideoConfigId);

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
        H264VideoConfiguration videoCodecConfig = new H264VideoConfiguration();
        switch (vconf) {
            case LOW:
                videoCodecConfig.setName("LOW H264 Codec Config");
                videoCodecConfig.setBitrate(375000L);
                videoCodecConfig.setWidth(384);
                videoCodecConfig.setProfile(ProfileH264.HIGH);
                break;
            case MEDIUM:
                videoCodecConfig.setName("Getting Started H264 Codec Config 3");
                videoCodecConfig.setBitrate(750000L);
                videoCodecConfig.setWidth(640);
                videoCodecConfig.setProfile(ProfileH264.HIGH);
                break;
            case HIGH:
                videoCodecConfig.setName("HIGH H264 Codec Config");
                videoCodecConfig.setBitrate(1500000L);
                videoCodecConfig.setWidth(1024);
                break;
            default:
                throw new IllegalStateException();
        }
        videoCodecConfig.setProfile(ProfileH264.HIGH);
        videoCodecConfig = bitmovinApi.configuration.videoH264.create(videoCodecConfig);
        return videoCodecConfig.getId();
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
       switch (vconf){
           case MEDIUM:
               return this.mediumVideoConfigId;
           case HIGH:
               return this.highVideoConfigId;
           case LOW:
               return this.lowVideoConfigId;
           default:
               throw new IllegalStateException();
       }
    }
}
