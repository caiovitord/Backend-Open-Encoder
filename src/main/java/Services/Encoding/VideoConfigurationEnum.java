package Services.Encoding;

/**
*  Enum que armazena os tipos de qualidade de vídeo
 */
public enum VideoConfigurationEnum {
    HIGH(1024,1500000L),
    MEDIUM(640,750000L),
    LOW(384, 375000L);

    public final int resolution;
    public final long bitrate;

    VideoConfigurationEnum(int resolution, long bitrate) {
        this.resolution = resolution;
        this.bitrate = bitrate;
    }


}
