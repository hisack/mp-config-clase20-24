package com.programacion;
import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.model.health.Service;
import com.orbitz.consul.model.health.ServiceHealth;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
public class Cliente {
    public static void main(String[] args) throws IOException {

        //A continuación listamos los servicios, el siguiente es el servidor de registro o configuración
        Consul consul = Consul.builder()//conectarnos a consult
                .withHostAndPort(HostAndPort.fromParts("127.0.0.1", 8500))//ip y puerto del servidor de configuracion
                .build();//como estamos el localhost le dejamos sin la IP

        HealthClient healthClient = consul.healthClient();

        List<ServiceHealth> instancia = healthClient.getAllServiceInstances("mp")
                .getResponse();
        instancia.stream()
                .forEach(s->{
                    Service service = s.getService();//nos retorna un servicio
                    String url = String.format("[%s] http://%s:%d",
                            service.getId(),
                            service.getAddress(),//obtenemos la Ip
                            service.getPort());//obtenemos el puerto
                    System.out.println(url);//debe listarnos los servicios levantados en el ejemplo son 3
                });
        //invocamos una de las instancias, usando Http Client para invocar una de las instancias
        //una de esa hay que invocar al Url que armamos anteriormente.
/*
        try {
            Service serviceSelect = instancia.get(1).getService();
            HttpClient clientHttp = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://"+serviceSelect.getAddress()+":"+serviceSelect.getPort()+"/hola"))
                    .GET()
                    .build();
            HttpResponse response = clientHttp.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Esto me devuelve: " + request.toString());
            System.out.println(response.body());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
*/

         //Otro cliente Http
         Service srv = instancia.get(0)
                 .getService();
         String urls = String.format("http://%s:%d",
                            srv.getAddress(),//obtenemos la Ip
                            srv.getPort());//obtenemos el puerto
        System.out.println("Invocando a: "+urls);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet req = new HttpGet(urls + "/hola");
        var response = client.execute(req);
        HttpEntity entity = response.getEntity();
        String result =  EntityUtils.toString(entity);;
        System.out.println(result);


    }
}