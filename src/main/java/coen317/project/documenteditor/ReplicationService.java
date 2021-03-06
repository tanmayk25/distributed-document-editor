package coen317.project.documenteditor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static coen317.project.documenteditor.DocumentController.DOCUMENT_ADD_PATH;
import static coen317.project.documenteditor.DocumentController.DOCUMENT_UPDATE_PATH;

@Service
@Slf4j
public class ReplicationService {
    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    NodesConfig nodesConfig;
    @Async
    public void replicate(WordDocument document) {
        if (nodesConfig.isLeader()) {
            nodesConfig.getNodeMap().entrySet().forEach(entry -> {
                try {
                    String[] hostAndPort = entry.getValue().split(":");
                    log.info("Replicating {} to: {}", document.getId(), entry.getKey());
                    String url = UriComponentsBuilder.newInstance()
                            .scheme("http").host(hostAndPort[0]).port(hostAndPort[1])
                            .path(DOCUMENT_ADD_PATH).toUriString();
                    restTemplate.postForEntity(url, document, WordDocument.class);
                    log.info("Replicated");
                } catch (Exception ce) {
                    log.info("Unable to replicate to node: " + entry.getKey() + ce.getMessage());
                }
            });
        }
    }
    @Async
    public void replicate(WordDocument document, String documentId, String user) {
        if (nodesConfig.isLeader()) {
            nodesConfig.getNodeMap().entrySet().forEach(entry -> {
                try {
                    String[] hostAndPort = entry.getValue().split(":");
                    log.info("Replicating update of {} to: {}", documentId, entry.getKey());
                    String url = UriComponentsBuilder.newInstance()
                            .scheme("http").host(hostAndPort[0]).port(hostAndPort[1])
                            .path(DOCUMENT_UPDATE_PATH).queryParam("user", user).buildAndExpand(documentId).toUriString();
                    restTemplate.postForEntity(url, document, WordDocument.class);
                    log.info("Replicated");
                } catch (Exception e) {
                    log.error("Unable to replicate to node: " + entry.getKey(), e);
                }
            });
        }
    }
}
