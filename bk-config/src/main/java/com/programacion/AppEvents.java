package com.programacion;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.agent.ImmutableRegCheck;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class AppEvents {
  @Inject
  @ConfigProperty(name = "server.port", defaultValue = "6001")
  private String port;

  @Inject
  @ConfigProperty(name = "consul.ip", defaultValue = "127.0.0.1")
  private String consulIp;
  public static final String NAME = "bk";
  public static String ID;
  @PostConstruct
  public void inizializar () {
    System.out.println("***inicializar");
    ID = UUID.randomUUID().toString();
  }

/*  @PreDestroy
  public void destruir () {
    System.out.println("***destruir");
  }*/

  public void init (@Observes @Initialized(ApplicationScoped.class) Object obj) {
    System.out.printf("[%s] App. inicializada: %s, puerto: %s", NAME, ID, port);
    /**
     * nombre mp
     * id aleatoria
     * ip localhost
     * puerto
     */
    // connect on localhost
    System.out.printf("******CONSUL: %s", consulIp);
    Consul client = Consul.builder()
            .withHostAndPort(HostAndPort.fromParts("localhost", 8500))
            .build();
    String urlChequeo = String.format("http://127.0.0.1:%s/health/live", port);
    ImmutableRegCheck check = ImmutableRegCheck.builder()
            .http(urlChequeo)
            .interval("10s")
            .deregisterCriticalServiceAfter("5s")
            .build();
    AgentClient agentClient = client.agentClient();
    Registration service = ImmutableRegistration.builder()
            .id(ID)
            .name(NAME)
            .address("127.0.0.1")
            .port(Integer.parseInt(port))
            .putMeta("ip", "127.0.0.1")
            .putMeta("puerto", port)
            .check(check)
            .build();

    agentClient.register(service);
  }

  public void destroy (@Observes @Destroyed(ApplicationScoped.class) Object obj) {
    System.out.println("App. destruida");
    Consul client = Consul.builder()
            .withHostAndPort(HostAndPort.fromParts("localhost", 8500))
            .build();

    AgentClient agentClient = client.agentClient();
    agentClient.deregister(ID);
  }
}
