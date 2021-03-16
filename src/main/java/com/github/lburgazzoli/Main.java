//DEPS org.slf4j:slf4j-simple:1.7.30
//DEPS io.fabric8:kubernetes-client:5.1.1

package com.github.lburgazzoli;

import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.client.utils.Serialization;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;
import io.fabric8.kubernetes.api.model.Condition;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        var sp = new CamelConnectorSpec();
        sp.setConnectorId("cid");

        var st = new CamelConnectoStatus();
        st.setPhase("running");

        var cr = new CamelConnector();
        cr.setSpec(sp);
        cr.setStatus(st);

        var ser = Serialization.jsonMapper().writerWithDefaultPrettyPrinter().writeValueAsString(cr);
        var nod = Serialization.jsonMapper().readTree(ser);
        var des = Serialization.jsonMapper().treeToValue(nod, CamelConnector.class);

        LOGGER.info("s: {}", ser);
        LOGGER.info("d: {}", des);
    }

    public interface Connector {
    }

    @Group("lbrgz.com")
    @Version("v1alpha1")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final static class CamelConnector extends CustomResource<CamelConnectorSpec, CamelConnectoStatus> implements Connector {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final static class CamelConnectorSpec {      
        private String connectorId;
        private PodTemplateSpec template;   

        public void setConnectorId(String connectorId) {
            this.connectorId = connectorId;
        }

        public String getConnectorId() {
            return this.connectorId;
        }
        
        public PodTemplateSpec getTemplate() {
            return template;
        }
    
        public void setTemplate(PodTemplateSpec template) {
            this.template = template;
        }
        
        @Override
        public String toString() {
            return "CamelConnectorSpec{ connectorId=" + this.connectorId + ", template=" + this.template + "}";
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final static class CamelConnectoStatus extends Status { 
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Status {
        private String phase;
        private List<Condition> conditions;

        @JsonProperty
        public String getPhase() {
            return phase;
        }

        @JsonProperty
        public void setPhase(String phase) {
            this.phase = phase;
        }

        @JsonIgnore
        public void setPhase(Enum<?> type) {
            setPhase(type.name());
        }

        @JsonIgnore
        public boolean isInPhase(String type) {
            return Objects.equals(getPhase(), type);
        }

        @JsonIgnore
        public boolean isInPhase(Enum<?> type) {
            return Objects.equals(getPhase(), type.name());
        }

        @JsonProperty
        public List<Condition> getConditions() {
            return conditions;
        }

        @JsonProperty
        public void setConditions(List<Condition> conditions) {
            this.conditions = conditions;
        }

        @JsonIgnore
        public Optional<Condition> getLatestCondition() {
            return conditions != null
                    ? Optional.of(conditions.get(conditions.size() - 1))
                    : Optional.empty();
        }

        @Override
        public String toString() {
            return "Status{" +
                    "phase='" + phase + '\'' +
                    ", conditions=" + conditions +
                    '}';
        }
    }
}
