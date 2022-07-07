package org.fcordoba.springboot.test.datos;

import org.fcordoba.springboot.test.models.Banco;
import org.fcordoba.springboot.test.models.Cuenta;

import java.math.BigDecimal;
import java.util.Optional;

public class Datos {
//    public static Cuenta CUENTA2 = new Cuenta(2L,"Fatima Quinteros", new BigDecimal("2000.00"));
//    public static Cuenta CUENTA3 = new Cuenta(3L, "Flavio Fuenzalida", new BigDecimal("3000.00"));
  //  public static Banco BANCO1 = new Banco(1L,"Banco del Pueblo",1);

    public  static Optional <Cuenta> crearCuenta1(){
        return  Optional.of(new Cuenta(1L,"Facundo",new BigDecimal("1000.00")));
    }
    public static Optional<Cuenta> crearCuenta2(){
        return Optional.of(new Cuenta(2L,"Fatima Quinteros", new BigDecimal("2000.00")));
    }

    public static Optional<Banco> crearBanco()
    {
        return Optional.of(new Banco(1L,"Banco del Pueblo",1));
    }

}