package org.fcordoba.springboot.test.services;

import org.fcordoba.springboot.test.exceptions.DineroInsuficienteException;
import org.fcordoba.springboot.test.models.Banco;
import org.fcordoba.springboot.test.models.Cuenta;
import org.fcordoba.springboot.test.repository.BancoRepository;
import org.fcordoba.springboot.test.repository.CuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fcordoba.springboot.test.datos.Datos.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class CuentaServiceImplTest {
    @Autowired
    CuentaService cuentaService;
    @MockBean
    CuentaRepository cuentaRepository;
    @MockBean
    BancoRepository bancoRepository;

    @BeforeEach
    void setUp() {
        //cuentaRepository = mock(CuentaRepository.class);
       // bancoRepository = mock(BancoRepository.class);
       // cuentaService = new CuentaServiceImpl(cuentaRepository, bancoRepository);
    }

    @Test
    @Order(2)
    void name() {
        when(cuentaRepository.findById(1L)).thenReturn(crearCuenta1());
        when(cuentaRepository.findById(2L)).thenReturn(crearCuenta2());
        when(bancoRepository.findById(1L)).thenReturn(crearBanco());
        BigDecimal bigDecimalCuenta1 = cuentaService.revisarSaldo(1L);
        BigDecimal bigDecimalCuenta2 = cuentaService.revisarSaldo(2L);
        assertEquals("1000.00", bigDecimalCuenta1.toPlainString());
        assertEquals("2000.00", bigDecimalCuenta2.toPlainString());

        cuentaService.transferir(1L, 2L, new BigDecimal("1000.00"), 1L);
        int totalTransferencia = cuentaService.revisarTotalTransferencias(1L);

        bigDecimalCuenta1 = cuentaService.revisarSaldo(1L);
        bigDecimalCuenta2 = cuentaService.revisarSaldo(2L);
        assertEquals(new BigDecimal("0.00"), bigDecimalCuenta1);
        assertEquals(new BigDecimal("3000.00"), bigDecimalCuenta2);
        assertEquals(2, totalTransferencia);

        //verify(cuentaRepository, times(3)).findById(1L);
       // verify(cuentaRepository, times(3)).findById(2L);
        verify(cuentaRepository, times(6)).findById(anyLong());
        verify(cuentaRepository, times(2)).save(any(Cuenta.class));
        verify(bancoRepository, times(2)).findById(1L);
        verify(bancoRepository).save(any(Banco.class));
        verify(cuentaRepository,never()).findAll();
    }


    @Test
    @Order(1)
    void name2() {
        when(cuentaRepository.findById(1L)).thenReturn(crearCuenta1());
        when(cuentaRepository.findById(2L)).thenReturn(crearCuenta2());
        when(bancoRepository.findById(1L)).thenReturn(crearBanco());
        BigDecimal bigDecimalCuenta1 = cuentaService.revisarSaldo(1L);
        BigDecimal bigDecimalCuenta2 = cuentaService.revisarSaldo(2L);
        assertEquals("1000.00", bigDecimalCuenta1.toPlainString());
        assertEquals("2000.00", bigDecimalCuenta2.toPlainString());
        Exception e = assertThrows(DineroInsuficienteException.class, () -> {
            cuentaService.transferir(1L, 2L, new BigDecimal("2000.00"), 1L);

        });
        assertTrue(e instanceof DineroInsuficienteException);


        int totalTransferencia = cuentaService.revisarTotalTransferencias(1L);

        bigDecimalCuenta1 = cuentaService.revisarSaldo(1L);
        bigDecimalCuenta2 = cuentaService.revisarSaldo(2L);
        assertEquals(new BigDecimal("1000.00"), bigDecimalCuenta1);
        assertEquals(new BigDecimal("2000.00"), bigDecimalCuenta2);
        assertEquals(1, totalTransferencia);

        verify(cuentaRepository, times(3)).findById(1L);
        verify(cuentaRepository, times(3)).findById(2L);
        verify(cuentaRepository, never()).save(any(Cuenta.class));
        verify(bancoRepository, times(2)).findById(1L);
        verify(bancoRepository, never()).save(any(Banco.class));
    }


    @Test
    void findByIdTest() {
        when(cuentaRepository.findById(anyLong())).thenReturn(crearCuenta1());
        Cuenta cuenta = cuentaService.findById(1L);
        assertEquals(1L, cuenta.getId());

    }

    @Test
    void compararObjetosIgualesTest() {
        when(cuentaRepository.findById(anyLong())).thenReturn(crearCuenta1());
        Cuenta  cuenta = cuentaRepository.findById(1L).orElseThrow();
        Cuenta cuenta1 = new Cuenta(1L,"Facundo",new BigDecimal("1000.00"));
        assertEquals(cuenta1, cuenta); // compara usando equals y hashcode
        //assertTrue(cuenta == cuenta1)// compara por direccion de memoria
        //assertSame(cuenta1,cuenta); // compara por direccion de memoria

    }


    @Test
    void findAllTest() {
        List<Cuenta> datos = Arrays.asList(crearCuenta1().get(),crearCuenta2().get());
        System.out.println(datos instanceof ArrayList);
        when(cuentaRepository.findAll()).thenReturn(datos);
        List<Cuenta> cuentas = cuentaService.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2,cuentas.size());
        assertTrue(cuentas.contains(crearCuenta1().get()));
    }


    @Test
    void saveTest() {
        Cuenta cuenta = new Cuenta(null, "pepe", new BigDecimal(200));
        when(cuentaRepository.save(any(Cuenta.class))).then(invocation->{
         Cuenta c  =   invocation.getArgument(0);
         c.setId(3L);
         return  c;
        });

      Cuenta miCuenta = cuentaService.save(cuenta);
      assertNotNull(miCuenta);
      assertEquals(cuenta.getPersona(),miCuenta.getPersona());
      assertEquals(cuenta.getSaldo(),miCuenta.getSaldo());


    }
}