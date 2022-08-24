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

@ApplicationScoped //hacemos a la clase un componete CDI
public class AppEvents {

      /* @PostConstruct //relacion con CDI, algo que se invoca luego de construir el compoente
    public void inicializar(){
        System.out.println("****Inicializar****");
    }

    //detectar cuando se destruya el componente
    @PreDestroy
    public void destruir(){
        System.out.println("****destruir****");
    }*/
    //estas anotaciones estan relacionadas en cuando se utiliza y cuando
    //no se utiliza el componente

    @Inject //necesitamos el puerto
    @ConfigProperty(name = "server.port", defaultValue = "7001")
    private Integer puerto;

    @Inject //necesitamos el puerto
    @ConfigProperty(name = "consul.ip", defaultValue = "127.0.0.1")
    private String consulIp;

    //necesitamos el nombre para la aplicacion
    public static final String NAME = "mp";
    //debemos dar un ID unico
    public static String ID;

    @PostConstruct //algo que se invoca luego de construir el compoente
    public void inicializar(){
        UUID uuid = UUID.randomUUID();
        ID = uuid.toString();
    }

    //vamos a utilizar eventos CDI
    //estos metodos se invocan cada vez que se cree el contexto de la aplicacion.
    public void init(@Observes @Initialized(ApplicationScoped.class) Object obj) {
        System.out.printf("[%s] App. Inicializada: %s, puerto : %d\n", NAME, ID, puerto);

        System.out.printf("****CONSUL: %s ", consulIp);
        /**Registrar
         * Nombre: mp
         * ID: aleatorio
         * ip : 127.0.0.1
         * puerto
         *
         */
        //conexion a Consul en localhost, con esto tenemos el consul client
        Consul client = Consul.builder()
                .withHostAndPort(HostAndPort.fromParts(consulIp, 8500))
                .build();


        //Url de chekeo http://localhost:8080/health/live
        String urlChequeo = String.format("http://127.0.0.1:%d/health/live", puerto);

        ImmutableRegCheck check = ImmutableRegCheck.builder()
                .http(urlChequeo)
                .interval("10s")
                .deregisterCriticalServiceAfter("5s")
                .build();

        //AgentCliente nos va servir para conectarnos.
        AgentClient agentClient = client.agentClient();

        Registration service = ImmutableRegistration.builder()
                .id(ID)
                .name(NAME)//es el nombre del grupo
                .address("127.0.0.1")
                .port(puerto)
                .putMeta("ip","127.0.0.1")
                .putMeta("puerto",puerto.toString())
                .check(check)
                .build();

        agentClient.register(service);

    }

    public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object obj){
        System.out.println("App. destruida");

        /**ELIMINAR EL REGISTRO: ID
         *
         */
        //conexion a Consul en localhost, con esto tenemos el consul client
        Consul client = Consul.builder()
                .withHostAndPort(HostAndPort.fromParts(consulIp, 8500))
                .build();
        //AgentCliente nos va servir para conectarnos.
        AgentClient agentClient = client.agentClient();
        //pero en lugar de registrarlo lo vamos a deregistrar, mediante el ID
        agentClient.deregister(ID);

    }
}
