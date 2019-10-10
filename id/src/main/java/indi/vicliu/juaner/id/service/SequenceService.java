package indi.vicliu.juaner.id.service;

import indi.vicliu.juaner.id.utils.SequenceGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SequenceService {

    private SequenceGenerator sequenceGenerator;

    public long nextId() {
        return sequenceGenerator.nextId();
    }
    @Value("${node.id}")
    public void setNodeId(int nodeId){
        log.info("id service node id:{}", nodeId);
        sequenceGenerator = new SequenceGenerator(nodeId);
    }
}