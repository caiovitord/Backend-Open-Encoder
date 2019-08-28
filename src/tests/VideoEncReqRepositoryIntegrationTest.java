import Persistence.Entities.VideoEncodingRequest;
import REST.Application;
import REST.Repositories.VideoEncodingRequestRepository;
import Services.Encoding.VideoConfigurationEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.Persistence;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class VideoEncReqRepositoryIntegrationTest {

    Random random = new Random();

    private VideoEncodingRequestRepository repository =
            new VideoEncodingRequestRepository(
                    Persistence.createEntityManagerFactory("test" + random.nextInt() + ".odb")
                            .createEntityManager());

    @Test
    public void contexLoads() {
        assertNotNull(repository);
    }

    @Test
    public void whenFindAll_thenReturnAllVideoEncodingRequest() {
        Map<String, VideoEncodingRequest> created = this.randomVideoEncodingRequest(10);
        created.forEach((s, request) -> {
            repository.save(request);
        });

        List<VideoEncodingRequest> found = repository.findAll();

        assertThat(created.size(), is(found.size()));

        //Garante que os dados são armazenados corretamente ao pesquisar e salvar vários
        found.forEach(request -> {
            assertThat(created.get(request.getEncodingId()).getEncodingId(), is(request.getEncodingId()));
            assertThat(created.get(request.getEncodingId()).getOutputPath(), is(request.getOutputPath()));
            assertThat(created.get(request.getEncodingId()).getAudioStreamId(), is(request.getAudioStreamId()));
            assertThat(created.get(request.getEncodingId()).getFmp4AudioMuxinId(), is(request.getFmp4AudioMuxinId()));
            assertThat(created.get(request.getEncodingId()).getStreamVideoId(), is(request.getStreamVideoId()));
            assertThat(created.get(request.getEncodingId()).getVideoMuxinId(), is(request.getVideoMuxinId()));
            assertThat(created.get(request.getEncodingId()).createdManifest(), is(request.createdManifest()));
            assertThat(created.get(request.getEncodingId()).getEncodingQuality(), is(request.getEncodingQuality()));
        });
    }


    @Test
    public void whenFindById_thenReturnVideoEncodingRequest() {

        VideoEncodingRequest videoEncodingRequest = oneRandomVideoEncodingRequest();

        repository.save(videoEncodingRequest);

        //Garante que os dados são armazenados corretamente
        Optional<VideoEncodingRequest> found = repository.findById(videoEncodingRequest.getEncodingId());
        assertThat(found.get().getEncodingId(), is(videoEncodingRequest.getEncodingId()));
        assertThat(found.get().getOutputPath(), is(videoEncodingRequest.getOutputPath()));
        assertThat(found.get().getAudioStreamId(), is(videoEncodingRequest.getAudioStreamId()));
        assertThat(found.get().getFmp4AudioMuxinId(), is(videoEncodingRequest.getFmp4AudioMuxinId()));
        assertThat(found.get().getStreamVideoId(), is(videoEncodingRequest.getStreamVideoId()));
        assertThat(found.get().getVideoMuxinId(), is(videoEncodingRequest.getVideoMuxinId()));
        assertThat(found.get().createdManifest(), is(videoEncodingRequest.createdManifest()));
        assertThat(found.get().getEncodingQuality(), is(videoEncodingRequest.getEncodingQuality()));
    }




    @Test
    public void whenDelete_thenShouldNotExistVideoEncodingRequest() {
        VideoEncodingRequest videoEncodingRequest = oneRandomVideoEncodingRequest();

        //Depois de salvar deve existir
        repository.save(videoEncodingRequest);
        Optional<VideoEncodingRequest> found = repository.findById(videoEncodingRequest.getEncodingId());
        assertTrue(found.isPresent());

        //Deppis de deletar não deve mais existir
        repository.delete(videoEncodingRequest);
        Optional<VideoEncodingRequest> foundAfterDelete = repository.findById(videoEncodingRequest.getEncodingId());
        assertFalse(foundAfterDelete.isPresent());
    }



    public String str(Object o) {
        return String.valueOf(o);
    }

    public VideoEncodingRequest oneRandomVideoEncodingRequest() {
        return new VideoEncodingRequest(
                str(random.nextLong()),
                str(random.nextLong()),
                str(random.nextLong()),
                str(random.nextLong()),
                str(random.nextLong()),
                str(random.nextLong()),
                random.nextBoolean(),
                VideoConfigurationEnum.values()[random.nextInt(VideoConfigurationEnum.values().length)]
        );
    }

    public HashMap<String, VideoEncodingRequest> randomVideoEncodingRequest(int amount) {
        HashMap<String, VideoEncodingRequest> randomVideoRequests = new HashMap<>();
        for (int i = 0; i < amount; i++) {
            String key = str(random.nextLong());
            randomVideoRequests.put(key, new VideoEncodingRequest(
                    key,
                    str(random.nextLong()),
                    str(random.nextLong()),
                    str(random.nextLong()),
                    str(random.nextLong()),
                    str(random.nextLong()),
                    random.nextBoolean(),
                    VideoConfigurationEnum.values()[random.nextInt(VideoConfigurationEnum.values().length)]
            ));
        }
        return randomVideoRequests;
    }

}