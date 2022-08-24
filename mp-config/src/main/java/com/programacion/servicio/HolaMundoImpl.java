package com.programacion.servicio;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class HolaMundoImpl implements HolaMundo {

    public String hola(){

        return "hola "+ LocalDateTime.now();
    }


}