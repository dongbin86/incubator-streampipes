/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.rest.impl;

import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSourceDescription;
import org.apache.streampipes.rest.api.IApplicationLink;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/applink")
public class ApplicationLink extends AbstractRestInterface implements IApplicationLink {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getApplicationLinks() {
        return ok(generateAppLinks());
    }

    private List<org.apache.streampipes.model.ApplicationLink> generateAppLinks() {
        List<NamedStreamPipesEntity> allElements = new ArrayList<>();
        List<org.apache.streampipes.model.ApplicationLink> allApplicationLinks = new ArrayList<>();

        allElements.addAll(getPipelineElementRdfStorage()
                .getAllDataProcessors().stream().map(e -> new DataProcessorDescription(e)).collect(Collectors.toList()));
        allElements.addAll(getPipelineElementRdfStorage()
                .getAllDataSinks().stream().map(e -> new DataSinkDescription(e)).collect(Collectors.toList()));
        allElements.addAll(getPipelineElementRdfStorage()
                .getAllDataSources().stream().map(e -> new DataSourceDescription(e)).collect(Collectors.toList()));

        allElements.stream().forEach(e -> allApplicationLinks.addAll(removeDuplicates(allApplicationLinks, e.getApplicationLinks())));

        return allApplicationLinks;
    }

    private List<org.apache.streampipes.model.ApplicationLink> removeDuplicates(List<org.apache.streampipes.model.ApplicationLink> allApplicationLinks,
                                                                         List<org.apache.streampipes.model.ApplicationLink> applicationLinks) {
        List<org.apache.streampipes.model.ApplicationLink> result = new ArrayList<>();

        applicationLinks.forEach( a -> {
                if (allApplicationLinks
                        .stream()
                        .noneMatch(existing -> existing.getApplicationUrl()
                                .equals(existing.getApplicationUrl()))) {
                    result.add(a);
                }
        });

        return result;

    }
}
