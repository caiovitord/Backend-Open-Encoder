package Persistence.Services;

import com.bitmovin.api.BitmovinApi;

import java.io.IOException;

public class EncoderService {

    private final BitmovinApi bitmovinApi = new BitmovinApi("91e8346c-a81c-4f09-b5cc-3b246f80e87d");

    public EncoderService() throws IOException {
    }
}
