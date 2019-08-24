package Services.Encoding;

import Persistence.BucketsEnum;
import Services.AmazonS3Service;
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

public class EncoderConfigurationService {

    private final BitmovinApi bitmovinApi;

    private boolean shouldCreateInput;
    private boolean shouldCreateOutput;
    private boolean shouldCreateVideoConfig;
    private boolean shouldCreateAudioConfig;

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
            Scanner sc = new Scanner(new File("bitmovinConfig.ini"));

            inputId = sc.nextLine();
            outputId = sc.nextLine();
            videoConfigId = sc.nextLine();
            audioConfigId = sc.nextLine();

        } catch (FileNotFoundException e) {
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

        output.setAccessKey(AmazonS3Service.ACCESS_KEY);
        output.setSecretKey(AmazonS3Service.SECRET);
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
