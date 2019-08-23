package Persistence.Services;

import Persistence.BucketsEnum;
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
import com.bitmovin.api.encoding.manifest.ManifestCreationProcessResponse;
import com.bitmovin.api.encoding.manifest.hls.HlsManifest;
import com.bitmovin.api.encoding.manifest.hls.StreamInfo;
import com.bitmovin.api.encoding.outputs.S3Output;
import com.bitmovin.api.encoding.status.Message;
import com.bitmovin.api.exceptions.BitmovinApiException;
import com.bitmovin.api.http.RestException;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EncoderService {

    private final BitmovinApi bitmovinApi = new BitmovinApi("91e8346c-a81c-4f09-b5cc-3b246f80e87d");

    private S3Output output;

    public EncoderService() throws IOException, BitmovinApiException, UnirestException, URISyntaxException {
        //Create output
        output = new S3Output();

        output.setAccessKey(AmazonS3Service.ACCESS_KEY);
        output.setSecretKey(AmazonS3Service.SECRET);
        output.setBucketName(BucketsEnum.OUTPUT_BUCKET.bucketName);
        output = bitmovinApi.output.s3.create(output);
    }

    public void encode(String inputFile) throws URISyntaxException, BitmovinApiException, UnirestException, IOException, RestException {
        //Create input
        HttpsInput input = new HttpsInput();
        input.setHost(BucketsEnum.INPUT_BUCKET.bucketHost);
        input = bitmovinApi.input.https.create(input);



        //Video codec config
        H264VideoConfiguration videoCodecConfig = new H264VideoConfiguration();

        videoCodecConfig.setName("Getting Started H264 Codec Config");
        videoCodecConfig.setBitrate(375000L);
        videoCodecConfig.setWidth(384);
        videoCodecConfig.setProfile(ProfileH264.HIGH);

        videoCodecConfig = bitmovinApi.configuration.videoH264.create(videoCodecConfig);



        //Audio codec config
        AACAudioConfig audioCodecConfig = new AACAudioConfig();
        audioCodecConfig.setName("Getting Started Audio Codec Config");
        audioCodecConfig.setBitrate(128000L);
        audioCodecConfig = bitmovinApi.configuration.audioAAC.create(audioCodecConfig);


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
        inputStreamVideo1.setInputId(input.getId());
        inputStreamVideo1.setInputPath(inputPath);
        inputStreamVideo1.setSelectionMode(StreamSelectionMode.AUTO);

        streamVideo.setCodecConfigId(videoCodecConfig.getId());
        streamVideo.addInputStream(inputStreamVideo1);

        streamVideo = bitmovinApi.encoding.stream.addStream(encoding, streamVideo);


        //Create audio stream
        Stream audioStream = new Stream();

        InputStream inputStreamAudio = new InputStream();
        inputStreamAudio.setInputId(input.getId());
        inputStreamAudio.setInputPath(inputPath);
        inputStreamAudio.setSelectionMode(StreamSelectionMode.AUTO);

        audioStream.setCodecConfigId(audioCodecConfig.getId());
        audioStream.addInputStream(inputStreamAudio);

        audioStream = bitmovinApi.encoding.stream.addStream(encoding, audioStream);



        //Create fmp4 muxin
        AclEntry aclEntry = new AclEntry();
        aclEntry.setPermission(AclPermission.PUBLIC_READ);
        List<AclEntry> aclEntries = new ArrayList<AclEntry>();
        aclEntries.add(aclEntry);


        double segmentLength = 4D;
        String outputPath = "0cf500e1-73e7-4165-bea7-5e73977d77ef/" + System.currentTimeMillis();
        String segmentNaming = "seg_%number%.m4s";
        String initSegmentName = "init.mp4";

        FMP4Muxing videoMuxing = new FMP4Muxing();

        MuxingStream muxingStream1 = new MuxingStream();
        muxingStream1.setStreamId(streamVideo.getId());

        EncodingOutput videoMuxingOutput = new EncodingOutput();
        videoMuxingOutput.setOutputId(output.getId());
        videoMuxingOutput.setOutputPath(String.format("%s%s", outputPath,"/video/384_375000/fmp4/"));
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
        encodingOutputFmp4Audio.setOutputId(output.getId());
        encodingOutputFmp4Audio.setOutputPath(String.format("%s%s", outputPath, "/audio/128000/fmp4/"));
        encodingOutputFmp4Audio.setAcl(aclEntries);

        fmpAudio4Muxing.setSegmentLength(segmentLength);
        fmpAudio4Muxing.setSegmentNaming(segmentNaming);
        fmpAudio4Muxing.setInitSegmentName(initSegmentName);
        fmpAudio4Muxing.addStream(muxingAudioStream);
        fmpAudio4Muxing.addOutput(encodingOutputFmp4Audio);

        fmpAudio4Muxing = bitmovinApi.encoding.muxing.addFmp4MuxingToEncoding(encoding, fmpAudio4Muxing);

        List<Message> msg = bitmovinApi.encoding.start(encoding);


        //Create manifest
        HlsManifest manifest = new HlsManifest();

        EncodingOutput encodingOutput = new EncodingOutput();
        encodingOutput.setOutputId(output.getId());
        encodingOutput.setOutputPath(outputPath);
        encodingOutput.setAcl(aclEntries);

        manifest.setName("manifest.m3u8");
        manifest.addOutput(encodingOutput);

        manifest = bitmovinApi.manifest.hls.create(manifest);

        //Create manifest video
        StreamInfo streamInfo = new StreamInfo();
        streamInfo.setAudio("audio_group");
        streamInfo.setClosedCaptions("NONE");
        streamInfo.setSegmentPath("video/384_375000/ts");
        streamInfo.setUri("video5.m3u8");
        streamInfo.setEncodingId(encoding.getId());
        streamInfo.setStreamId(streamVideo.getId());
        streamInfo.setMuxingId(videoMuxing.getId());

        streamInfo = bitmovinApi.manifest.hls.createStreamInfo(manifest, streamInfo);

        bitmovinApi.manifest.hls.startGeneration(manifest);

        
    }
}
