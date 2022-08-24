package com.programacion;


import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.time.LocalDateTime;

@Path(value="/")
@ApplicationScoped
public class HolaMundoRest {

    //@Inject
    //private HolaMundo servicio;

    //@Inject private Config config;

    @Inject
    @ConfigProperty(name = "mensaje", defaultValue = "HOLA")//vale sin quitar o no el default---si dejo el defaul -me imprime el valor por defecto auq ponga otra direccion distinta a localhost en .properties
    private String mensaje;

    @Inject
    @ConfigProperty(name = "server.port", defaultValue = "7001")
    private Integer puerto;

    @GET
    @Path("/hola")
    public String hola(){

        Config cfg = ConfigProvider.getConfig();

        cfg.getConfigSources()
                .forEach( s->{
                    System.out.printf("%d: %s\n", s.getOrdinal(), s.getName());
                    /*
                    //Informacion de key y value
                    // if(s instanceof ConsulConfigSource cs)
                    // al usar el if poncemos cs.
                    {
                        s.getProperties().forEach((k,v)->{
                            System.out.printf("Key: %s, value: %s\n", k.toString(), v.toString());
                        });
                    }*/
                });
        // String msg=  cfg.getValue("mensaje", String.class );
         //String msg = config.getValue("mensaje", String.class);
         return String.format("(%d): %s %s",puerto, mensaje, LocalDateTime.now().toString());
    }
}
//ejecutar en consola
// en cmd
//C:\Users\isaag\Documents\Distribuida\Clase21\mp-config-quarkus\build\quarkus-app>
//java -Dquarkus.http.port=7001 -jar quarkus-run.jar