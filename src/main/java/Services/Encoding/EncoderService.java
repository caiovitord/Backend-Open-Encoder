package Services.Encoding;

import Persistence.BucketsEnum;
import Persistence.DAO.VideoEncodingRequestDAO;
import Persistence.DataSource;
import Persistence.Entities.VideoEncodingRequest;
import Services.AmazonS3Service;
import com.bitmovin.api.BitmovinApi;
import com.bitmovin.api.encoding.AclEntry;
import com.bitmovin.api.encoding.AclPermission;
import com.bitmovin.api.encoding.EncodingOutput;
import com.bitmovin.api.encoding.InputStream;
import com.bitmovin.api.encoding.codecConfigurations.AACAudioConfig;
import com.bitmovin.api.encoding.codecConfigurations.H264VideoConfiguration;
import com.bitmovin.api.encoding.codecConfigurations.enums.ProfileH264;
import com.bitmovin.api.encoding.encodings.Encoding;
import com.bitmovin.api.encoding.encodings.muxing.FMP4Muxing;
import com.bitmovin.api.encoding.encodings.muxing.MuxingStream;
import com.bitmovin.api.encoding.encodings.streams.Stream;
import com.bitmovin.api.encoding.enums.CloudRegion;
import com.bitmovin.api.encoding.enums.StreamSelectionMode;
import com.bitmovin.api.encoding.inputs.HttpsInput;
import com.bitmovin.api.encoding.manifest.hls.AudioMediaInfo;
import com.bitmovin.api.encoding.manifest.hls.HlsManifest;
import com.bitmovin.api.encoding.manifest.hls.StreamInfo;
import com.bitmovin.api.encoding.outputs.S3Output;
import com.bitmovin.api.encoding.status.Message;
import com.bitmovin.api.encoding.status.Task;
import com.bitmovin.api.exceptions.BitmovinApiException;
import com.bitmovin.api.http.RestException;
import com.mashape.unirest.http.exceptions.UnirestException;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class EncoderService {

    private final BitmovinApi bitmovinApi;

    private final String inputId;
    private final String outputId;
    private final String videoConfigId;
    private final String audioConfigId;
    private final ArrayList<AclEntry> aclEntries;


    private final VideoEncodingRequestDAO videoEncodingRequestDAO = new VideoEncodingRequestDAO(DataSource.getInstance().getEntityManager());

    public EncoderService() throws IOException {
        bitmovinApi = new BitmovinApi("91e8346c-a81c-4f09-b5cc-3b246f80e87d");
        EncoderConfigurationService config = new EncoderConfigurationService(bitmovinApi);

        this.inputId = config.getInputId();
        this.outputId = config.getOutputId();
        this.videoConfigId = config.getVideoConfigId(VideoConfigurationEnum.LOW);
        this.audioConfigId = config.getAudioConfigId();

        AclEntry aclEntry = new AclEntry();
        aclEntry.setPermission(AclPermission.PUBLIC_READ);
        aclEntries = new ArrayList<AclEntry>();
        aclEntries.add(aclEntry);

    }

    public VideoEncodingRequest encode(String inputFile) throws URISyntaxException, BitmovinApiException, UnirestException, IOException, RestException {
        //Create encoding
        Encoding encoding = new Encoding();

        encoding.setName("Getting Started Encoding");
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

        streamVideo.setCodecConfigId(videoConfigId);
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
        videoMuxingOutput.setOutputPath(String.format("%s%s", outputPath, "/video/384_375000/fmp4/"));
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

        List<Message> msg = bitmovinApi.encoding.start(encoding);

        VideoEncodingRequest request = new VideoEncodingRequest(
                encoding.getId(),
                outputPath,
                audioStream.getId(),
                fmpAudio4Muxing.getId(),
                streamVideo.getId(),
                videoMuxing.getId()
        );

        videoEncodingRequestDAO.create(request);
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
        streamInfo.setSegmentPath("video/384_375000/fmp4");
        streamInfo.setUri("video.m3u8");
        streamInfo.setEncodingId(request.getEncodingId());
        streamInfo.setStreamId(request.getStreamVideoId());
        streamInfo.setMuxingId(request.getVideoMuxinId());

        streamInfo = bitmovinApi.manifest.hls.createStreamInfo(manifest, streamInfo);

        bitmovinApi.manifest.hls.startGeneration(manifest);
    }


    public Task getStatusAndProgressOfEncoding(String encodingId) throws URISyntaxException, BitmovinApiException, RestException, UnirestException, IOException {
        return  bitmovinApi.encoding.getStatus(bitmovinApi.encoding.getDetails(encodingId));
    }
}
