package com.programacion;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.microprofileext.config.source.consul.ConsulConfigSource;
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

    @GET
    @Path("/hola2")
    public String hola2(){

        return String.format("****HOLA2 (%d): %s %s",puerto, mensaje, LocalDateTime.now().toString());

    }
}

//localhost cambiado en windows
//C:\Windows\System32\drivers\etc
//abrir archivo host

// Consul
//cd C:\consul consul agent -dev

//ejecutar en consola
//Nota: C:\Users\isaag\Downloads\install\mp-config\bin>mp-config.bat -Dmensaje-xxx com.programacion.Main

//ejecutar otras instancias en consola:
//C:\Users\isaag\Documents\Distribuida\Clase21\mp-config\build\install\mp-config\lib>java -Dserver.port=8080 -classpath * com.programacion.Main

//traefik es el servidor que actuara como gateway o proxy
// cd C:\consul
//traefik version
//traefik -api.insecure true
//traefik.exe

//path para verificar el funcionamiento
//http://bk:9090/mayusculas/acbd
//http://mp:9090/hola
