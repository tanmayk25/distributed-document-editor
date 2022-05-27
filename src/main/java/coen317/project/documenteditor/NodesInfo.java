package coen317.project.documenteditor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Component
public class NodesInfo {
    private Map<Integer, String> nodeMap;
    @Value("${self}")
    private int self;

    @Value("${leader}")
    private int leader;

    @Value("#{${nodeMap}}")
    public void setNodeMap(Map<Integer, String> nodeMap) {
        this.nodeMap = nodeMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    public synchronized void removeNode(Integer node) {
        log.info("Removing node {}", node);
        nodeMap.remove(node);
    }

    public synchronized void removeLeader() {
        log.info("Removing leader node {} from map", leader);
        nodeMap.remove(leader);
    }

    public void setNewLeader(int leader) {
        removeLeader();
        log.info("Assigning leader {}", leader);
        this.leader = leader;
    }

    public boolean isLeader() {
        return self == leader;
    }
}