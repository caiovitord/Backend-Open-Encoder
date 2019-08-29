package Services.Encoding;

import Configuration.AppConfiguration;
import Persistence.DAO.VideoEncodingRequestDAO;
import Persistence.DataSourceSingleton;
import Persistence.Entities.VideoEncodingRequest;
import com.bitmovin.api.BitmovinApi;
import com.bitmovin.api.encoding.AclEntry;
import com.bitmovin.api.encoding.AclPermission;
import com.bitmovin.api.encoding.EncodingOutput;
import com.bitmovin.api.encoding.InputStream;
import com.bitmovin.api.encoding.encodings.Encoding;
import com.bitmovin.api.encoding.encodings.muxing.FMP4Muxing;
import com.bitmovin.api.encoding.encodings.muxing.MuxingStream;
import com.bitmovin.api.encoding.encodings.streams.Stream;
import com.bitmovin.api.encoding.enums.CloudRegion;
import com.bitmovin.api.encoding.enums.StreamSelectionMode;
import com.bitmovin.api.encoding.manifest.hls.AudioMediaInfo;
import com.bitmovin.api.encoding.manifest.hls.HlsManifest;
import com.bitmovin.api.encoding.manifest.hls.StreamInfo;
import com.bitmovin.api.encoding.status.Task;
import com.bitmovin.api.exceptions.BitmovinApiException;
import com.bitmovin.api.http.RestException;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Classe que acessa o serviço de encoding da BITMOVIN
 * <p>
 * Os seus métodos acessam a API Bitmovin e é responsável por construir todos os inputs, outputs,
 * objetos de streaming e muxin de video e audio.
 * <p>
 * A classe também  cria requisições para BITMOVIN inciar o processo de encoding em si.
 * <p>
 * Além disso, a classe possui um método para retornar o progresso
 * atual de um encoding em execução.
 */
public class EncoderService {

    private final BitmovinApi bitmovinApi;

    private final String inputId;
    private final String outputId;
    private final String lowVideoConfigId;
    private final String mediumVideoConfigId;
    private final String highVideoConfigId;
    private final String audioConfigId;

    private final ArrayList<AclEntry> aclEntries;

    private final String BITMOVIN_API_KEY = AppConfiguration.BITMOVIN_API_KEY;

    private final VideoEncodingRequestDAO videoEncodingRequestDAO = new VideoEncodingRequestDAO(DataSourceSingleton.getInstance().getEntityManager());

    public EncoderService() throws IOException {
        bitmovinApi = new BitmovinApi(BITMOVIN_API_KEY);
        EncoderConfigurationService config = new EncoderConfigurationService(bitmovinApi);

        this.inputId = config.getInputId();
        this.outputId = config.getOutputId();
        this.lowVideoConfigId = config.getVideoConfigId(VideoConfigurationEnum.LOW);
        this.mediumVideoConfigId = config.getVideoConfigId(VideoConfigurationEnum.MEDIUM);
        this.highVideoConfigId = config.getVideoConfigId(VideoConfigurationEnum.HIGH);
        this.audioConfigId = config.getAudioConfigId();

        AclEntry aclEntry = new AclEntry();
        aclEntry.setPermission(AclPermission.PUBLIC_READ);
        aclEntries = new ArrayList<AclEntry>();
        aclEntries.add(aclEntry);
    }

    public String getConfigurationId(VideoConfigurationEnum vconf) {
        switch (vconf) {
            case LOW:
                return this.lowVideoConfigId;
            case MEDIUM:
                return this.mediumVideoConfigId;
            case HIGH:
                return this.highVideoConfigId;
            default:
                throw new IllegalStateException();
        }
    }

    private long serverEncodingNumber = 0;

    public VideoEncodingRequest encode(String inputFile, VideoConfigurationEnum encodingQuality, VideoEncodingRequest request) throws URISyntaxException, BitmovinApiException, UnirestException, IOException, RestException {

        String actualVideoConfigId = getConfigurationId(encodingQuality);

        //Create encoding
        Encoding encoding = new Encoding();

        encoding.setName("Server Encoding nro " + serverEncodingNumber++);
        encoding.setCloudRegion(CloudRegion.AWS_US_EAST_1);
        encoding.setEncoderVersion("2.22.0");

        encoding = bitmovinApi.encoding.create(encoding);


        //Create video stream
        String inputPath = "/" + inputFile;

        Stream streamVideo = new Stream();

        InputStream inputStreamVideo1 = new InputStream();
        inputStreamVideo1.setInputId(inputId);
        inputStreamVideo1.setInputPath(inputPath);
        inputStreamVideo1.setSelectionMode(StreamSelectionMode.AUTO);

        streamVideo.setCodecConfigId(actualVideoConfigId);
        streamVideo.addInputStream(inputStreamVideo1);

        streamVideo = bitmovinApi.encoding.stream.addStream(encoding, streamVideo);


        //Create audio stream
        Stream audioStream = new Stream();

        InputStream inputStreamAudio = new InputStream();
        inputStreamAudio.setInputId(inputId);
        inputStreamAudio.setInputPath(inputPath);
        inputStreamAudio.setSelectionMode(StreamSelectionMode.AUTO);

        audioStream.setCodecConfigId(audioConfigId);
        audioStream.addInputStream(inputStreamAudio);

        audioStream = bitmovinApi.encoding.stream.addStream(encoding, audioStream);


        double segmentLength = 4D;
        String outputPath = "" + System.currentTimeMillis();
        String segmentNaming = "seg_%number%.m4s";
        String initSegmentName = "init.mp4";

        FMP4Muxing videoMuxing = new FMP4Muxing();

        MuxingStream muxingStream1 = new MuxingStream();
        muxingStream1.setStreamId(streamVideo.getId());

        EncodingOutput videoMuxingOutput = new EncodingOutput();
        videoMuxingOutput.setOutputId(outputId);
        videoMuxingOutput.setOutputPath(String.format("%s%s", outputPath, "/video/" +
                encodingQuality.resolution + "_" + encodingQuality.bitrate + "/fmp4/"));
        videoMuxingOutput.setAcl(aclEntries);

        videoMuxing.setSegmentLength(segmentLength);
        videoMuxing.setSegmentNaming(segmentNaming);
        videoMuxing.setInitSegmentName(initSegmentName);
        videoMuxing.addStream(muxingStream1);
        videoMuxing.addOutput(videoMuxingOutput);

        videoMuxing = bitmovinApi.encoding.muxing.addFmp4MuxingToEncoding(encoding, videoMuxing);


        //Create audio muxin
        FMP4Muxing fmpAudio4Muxing = new FMP4Muxing();

        MuxingStream muxingAudioStream = new MuxingStream();
        muxingAudioStream.setStreamId(audioStream.getId());

        EncodingOutput encodingOutputFmp4Audio = new EncodingOutput();
        encodingOutputFmp4Audio.setOutputId(outputId);
        encodingOutputFmp4Audio.setOutputPath(String.format("%s%s", outputPath, "/audio/128000/fmp4/"));
        encodingOutputFmp4Audio.setAcl(aclEntries);

        fmpAudio4Muxing.setSegmentLength(segmentLength);
        fmpAudio4Muxing.setSegmentNaming(segmentNaming);
        fmpAudio4Muxing.setInitSegmentName(initSegmentName);
        fmpAudio4Muxing.addStream(muxingAudioStream);
        fmpAudio4Muxing.addOutput(encodingOutputFmp4Audio);

        fmpAudio4Muxing = bitmovinApi.encoding.muxing.addFmp4MuxingToEncoding(encoding, fmpAudio4Muxing);

        bitmovinApi.encoding.start(encoding);


        //Sallva o objeto de VideoEncodingRequest

        request.setEncodingId(encoding.getId());
        request.setOutputPath(outputPath);
        request.setAudioStreamId(audioStream.getId());
        request.setFmp4AudioMuxinId(fmpAudio4Muxing.getId());
        request.setStreamVideoId(streamVideo.getId());
        request.setVideoMuxinId(videoMuxing.getId());
        request.setCreatedManifest(false);
        request.setEncodingQuality(encodingQuality);


        videoEncodingRequestDAO.update(request);
        return request;
    }

    public void createManifest(VideoEncodingRequest request) throws URISyntaxException, BitmovinApiException, UnirestException, IOException, RestException {
        //Should wait until finish to create manifest
        //Create manifest
        HlsManifest manifest = new HlsManifest();

        EncodingOutput encodingOutput = new EncodingOutput();
        encodingOutput.setOutputId(outputId);
        encodingOutput.setOutputPath(request.getOutputPath());
        encodingOutput.setAcl(aclEntries);

        manifest.setName("manifest.m3u8");
        manifest.addOutput(encodingOutput);

        manifest = bitmovinApi.manifest.hls.create(manifest);

        //Create audio media info
        AudioMediaInfo audioMediaInfo = new AudioMediaInfo();
        audioMediaInfo.setName("my-audio-media");
        audioMediaInfo.setGroupId("audio_group");
        audioMediaInfo.setSegmentPath("audio/128000/fmp4");
        audioMediaInfo.setUri("audiomedia.m3u8");
        audioMediaInfo.setEncodingId(request.getEncodingId());
        audioMediaInfo.setStreamId(request.getAudioStreamId());
        audioMediaInfo.setMuxingId(request.getFmp4AudioMuxinId());
        audioMediaInfo.setLanguage("en");

        audioMediaInfo = bitmovinApi.manifest.hls.createAudioMediaInfo(manifest, audioMediaInfo);


        //Create manifest video info
        StreamInfo streamInfo = new StreamInfo();
        streamInfo.setAudio("audio_group");
        streamInfo.setClosedCaptions("NONE");
        streamInfo.setSegmentPath("video/" + request.getEncodingQuality().resolution + "_" + request.getEncodingQuality().bitrate + "/fmp4");
        streamInfo.setUri("video.m3u8");
        streamInfo.setEncodingId(request.getEncodingId());
        streamInfo.setStreamId(request.getStreamVideoId());
        streamInfo.setMuxingId(request.getVideoMuxinId());

        streamInfo = bitmovinApi.manifest.hls.createStreamInfo(manifest, streamInfo);

        bitmovinApi.manifest.hls.startGeneration(manifest);

        request.setCreatedManifest(true);
        videoEncodingRequestDAO.update(request);
    }


    public Task getEncodingProgressStatus(String encodingId) throws URISyntaxException, BitmovinApiException, RestException, UnirestException, IOException {
        return bitmovinApi.encoding.getStatus(bitmovinApi.encoding.getDetails(encodingId));
    }
}
