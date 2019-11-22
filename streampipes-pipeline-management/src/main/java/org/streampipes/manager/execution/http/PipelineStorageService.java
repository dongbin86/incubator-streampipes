/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.manager.execution.http;

import org.streampipes.manager.data.PipelineGraph;
import org.streampipes.manager.data.PipelineGraphBuilder;
import org.streampipes.manager.matching.InvocationGraphBuilder;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.model.staticproperty.SecretStaticProperty;
import org.streampipes.storage.management.StorageDispatcher;
import org.streampipes.user.management.encryption.CredentialsManager;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineStorageService {

    private Pipeline pipeline;

    public PipelineStorageService(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void updatePipeline() {
     preparePipeline();
     StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().updatePipeline(pipeline);
    }

    public void addPipeline() {
        preparePipeline();
        StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI().store(pipeline);
    }

    private void preparePipeline() {
        PipelineGraph pipelineGraph = new PipelineGraphBuilder(pipeline).buildGraph();
        InvocationGraphBuilder builder = new InvocationGraphBuilder(pipelineGraph, pipeline.getPipelineId());
        List<InvocableStreamPipesEntity> graphs = encryptSecrets(builder.buildGraphs());

        List<DataSinkInvocation> secs = filter(graphs, DataSinkInvocation.class);
        List<DataProcessorInvocation> sepas = filter(graphs, DataProcessorInvocation.class);

        pipeline.setSepas(sepas);
        pipeline.setActions(secs);
    }

    private List<InvocableStreamPipesEntity> encryptSecrets(List<InvocableStreamPipesEntity> graphs) {
        graphs.forEach(g -> g.getStaticProperties()
                .stream()
                .filter(SecretStaticProperty.class::isInstance)
                .forEach(secret -> {
                    if (!((SecretStaticProperty) secret).getEncrypted()) {
                        try {
                            String encrypted = CredentialsManager.encrypt(pipeline.getCreatedByUser(),
                                    ((SecretStaticProperty) secret).getValue());
                            ((SecretStaticProperty) secret).setValue(encrypted);
                            ((SecretStaticProperty) secret).setEncrypted(true);
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                    }
                }));
        return graphs;
    }

    private <T> List<T> filter(List<InvocableStreamPipesEntity> graphs, Class<T> clazz) {
        return graphs
                .stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }
}
