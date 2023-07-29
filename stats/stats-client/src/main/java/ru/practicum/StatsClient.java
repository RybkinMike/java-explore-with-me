package ru.practicum.ewm.public1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.stats.StatsDTO;
import ru.practicum.ewm.dto.stats.StatsDtoForSave;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class Client {
    private  final RestTemplate restTemplate;
    ObjectMapper mapper = new  ObjectMapper();

    public Client(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:9090"))
                .build();
    }

    void postHit(String uri, String ip) {
        String app = "ewm-main-service";
        StatsDtoForSave statsDtoForSave = new StatsDtoForSave(app, uri, ip);
        try {
            StatsDtoForSave response = restTemplate.postForObject("/hit", statsDtoForSave, StatsDtoForSave.class);
            log.info("response: " + response);
        } catch (Exception ex) {
            log.error("error", ex);
        }


    }

    public Long getViews(String uri) {
        String start = "2000-01-01 01:01:01";
        String end = "3000-01-01 01:01:01";
        List<String> uris = new ArrayList();
        uris.add(uri);
        Long views = 0L;

        try {

            List<StatsDTO> response = mapper.convertValue(restTemplate.getForObject("/stats?start=" + start + "&end=" + end + "&uris=" + uri + "&unique=true", List.class), new TypeReference<List<StatsDTO>>() {});
            log.info("response = {}", response);
            log.info("1stats = {}", response.get(0));


            views = response.get(0).getHits();
        } catch (Exception ex) {
            log.error("error", ex);
        }
        return views;
    }


}
