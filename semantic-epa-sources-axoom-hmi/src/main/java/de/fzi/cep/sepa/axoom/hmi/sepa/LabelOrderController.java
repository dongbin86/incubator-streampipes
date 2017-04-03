package de.fzi.cep.sepa.axoom.hmi.sepa;

import de.fzi.cep.sepa.axoom.hmi.config.FlinkConfig;
import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.sdk.builder.ProcessingElementBuilder;
import de.fzi.cep.sepa.sdk.helpers.OutputStrategies;
import de.fzi.cep.sepa.sdk.helpers.SupportedFormats;
import de.fzi.cep.sepa.sdk.helpers.SupportedProtocols;

public class LabelOrderController extends AbstractFlinkAgentDeclarer<LabelOrderParameters> {

    @Override
    public SepaDescription declareModel() {
        SepaDescription delayDescription = ProcessingElementBuilder
                .create("labelorder", "Label Orders", "This component labels the orders with the time difference to" +
                        "the next maintenance step.")
                .iconUrl("url")
                .supportedProtocols(SupportedProtocols.kafka())
                .supportedFormats(SupportedFormats.jsonFormat())
                //TODO add stream requirements
                .setStream1()
                .setStream2()
                .outputStrategy(OutputStrategies.append(new EventPropertyPrimitive(
                        XSD._long.toString(), "nextMaintenance", "",
                        de.fzi.cep.sepa.commons.Utils.createURI("http://schema.org/Number"))
                ))
                .build();

        return delayDescription;
    }

    @Override
    protected FlinkSepaRuntime<LabelOrderParameters> getRuntime(SepaInvocation graph) {

        LabelOrderParameters params = new LabelOrderParameters(graph);

//        return new LabelOrderProgram(params, new FlinkDeploymentConfig(FlinkConfig.JAR_FILE, FlinkConfig.FLINK_HOST, FlinkConfig.FLINK_PORT));
//
        return new LabelOrderProgram(params);
    }
}