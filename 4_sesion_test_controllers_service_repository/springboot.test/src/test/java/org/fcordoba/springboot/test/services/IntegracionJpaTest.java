package org.fcordoba.springboot.test.services;

import org.fcordoba.springboot.test.datos.Datos;
import org.fcordoba.springboot.test.models.Cuenta;
import org.fcordoba.springboot.test.repository.CuentaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IntegracionJpaTest {
    @Autowired
    CuentaRepository cuentaRepository;


    @Test
    void findByIdTest() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        List<Cuenta> lista = cuentaRepository.findAll();
        assertTrue(cuenta.isPresent());
        Cuenta miCuenta = cuenta.orElseThrow();
        assertEquals(Datos.crearCuenta1().orElseThrow().getPersona(), miCuenta.getPersona());
    }

    @Test
    void findByPersonaTest() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Facundo");
        assertTrue(cuenta.isPresent());
        Cuenta miCuenta = cuenta.orElseThrow();
        assertEquals(Datos.crearCuenta1().orElseThrow().getPersona(), miCuenta.getPersona());
    }

    @Test
    void findByPersonaThrowExceptionTest() {
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("pepa");
        assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
    }


    @Test
    void findAllTest() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
    }

    @Test
    void saveTest() {
    // Given
      Cuenta cuenta = new Cuenta(null,"pepe",new BigDecimal("200"));
      cuentaRepository.save(cuenta);
      //When
      Cuenta cuenta1=   cuentaRepository.findByPersona("pepe").orElseThrow();
      //Then
       assertEquals(cuenta.getPersona(),cuenta1.getPersona());
       assertEquals(cuenta.getSaldo().toPlainString(), cuenta1.getSaldo().toPlainString());

    }


    @Test
    void updateTest() {
        // Given
        Cuenta cuenta = new Cuenta(null,"pepe",new BigDecimal("200"));
        cuentaRepository.save(cuenta);
        //Then
        Cuenta cuentaRecuperada =  cuentaRepository.findByPersona("pepe").orElseThrow();
        cuentaRecuperada.setSaldo(new BigDecimal("3800"));
        Cuenta cuentaUpdate = cuentaRepository.save(cuentaRecuperada);
        //When
        assertEquals(new BigDecimal("3800"), cuentaUpdate.getSaldo());

    }


    @Test
    void deleteTest() {
        // Given
        Cuenta  cuenta = cuentaRepository.findByPersona("Facundo").orElseThrow();
        //Then
          cuentaRepository.delete(cuenta);
        //When
         assertThrows(NoSuchElementException.class,()->{cuentaRepository.findByPersona("Facundo").orElseThrow();});
    }
}
