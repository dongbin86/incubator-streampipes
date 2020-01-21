/*
Copyright 2020 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.apache.streampipes.rest.impl.dashboard;

import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.rest.api.dashboard.IDashboard;
import org.apache.streampipes.rest.impl.AbstractRestInterface;
import org.apache.streampipes.rest.shared.annotation.JsonLdSerialized;
import org.apache.streampipes.storage.api.IDashboardStorage;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/ld/dashboards")
public class Dashboard extends AbstractRestInterface implements IDashboard {

  @GET
  @JsonLdSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response getAllDashboards() {
    return ok(getDashboardStorage().getAllDashboards());
  }

  @GET
  @JsonLdSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{dashboardId}")
  @Override
  public Response getDashboard(@PathParam("dashboardId") String dashboardId) {
    return ok(getDashboardStorage().getDashboard(dashboardId));
  }

  @PUT
  @JsonLdSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{dashboardId}")
  @Override
  public Response modifyDashboard(DashboardModel dashboardModel) {
    getDashboardStorage().updateDashboard(dashboardModel);
    return ok();
  }

  @DELETE
  @JsonLdSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{dashboardId}")
  @Override
  public Response deleteDashboard(@PathParam("dashboardId") String dashboardId) {
    getDashboardStorage().deleteDashboard(dashboardId);
    return ok();
  }

  @POST
  @JsonLdSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Override
  public Response createDashboard(DashboardModel dashboardModel) {
    getDashboardStorage().storeDashboard(dashboardModel);
    return ok();
  }

  private IDashboardStorage getDashboardStorage() {
    return getNoSqlStorage().getDashboardStorage();
  }
}
