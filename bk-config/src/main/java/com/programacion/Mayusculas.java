package com.programacion;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.time.LocalDateTime;

@Path("/")
@ApplicationScoped
public class Mayusculas {
  @Inject
  private Config config;

 /* @Inject
  @ConfigProperty(name = "abc")
  private String abc;*/
  @Inject
  @ConfigProperty(name = "server.port", defaultValue = "6001")
  private Integer puerto;
  @GET
  @Path("/mayusculas/{abc}")
  public String hola(@PathParam("abc") String ABC) {
    Config cfg = ConfigProvider.getConfig();
    cfg.getConfigSources().forEach(s->{
      System.out.printf("%d: %s\n", s.getOrdinal(), s.getName());

    });
/*    String msg = config.getValue("mensaje", String.class);*/

    return String.format("(%d): %s %s %s",puerto, "Este es el abecedario", ABC.toUpperCase(), LocalDateTime.now().toString());
}


}