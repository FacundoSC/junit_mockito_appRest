package org.fcordoba.springboot.test.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.fcordoba.springboot.test.datos.Datos.*;

import org.fcordoba.springboot.test.models.Cuenta;
import org.fcordoba.springboot.test.models.TransaccionDTO;
import org.fcordoba.springboot.test.services.CuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private CuentaService cuentaService;

    ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void detalleTest() throws Exception {
        //GIVEN
        when(cuentaService.findById(1L)).thenReturn(crearCuenta1().orElseThrow());

        //WHEN
        mvc.perform(get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))
               //THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.persona").value("Facundo"));

        verify(cuentaService).findById(1L);
    }

    @Test
    void transferir() throws Exception {
        //GIVEN
        TransaccionDTO transaccionDTO = new TransaccionDTO();
        transaccionDTO.setBancoId(1L);
        transaccionDTO.setCuentaOrigenId(1L);
        transaccionDTO.setCuentaDestinoId(2L);
        transaccionDTO.setMonto(new BigDecimal(100));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status","OK");
        response.put("mensaje","Transferencia realizada con Exito");
        response.put("transferencia",transaccionDTO);




        // WHEN
        mvc.perform(post("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaccionDTO)))
        //THEN
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.mensaje").value("Transferencia realizada con Exito"))
                .andExpect(jsonPath("$.transferencia.monto").value(100))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }


    @Test
    void findAllTest() throws Exception {
        List<Cuenta> cuentas = Arrays.asList(crearCuenta1().get(),crearCuenta2().get());
        when(cuentaService.findAll()).thenReturn(cuentas);
        mvc.perform(get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(cuentas)))
                .andExpect(jsonPath("$[0].persona").value("Facundo"))
                .andExpect(jsonPath("$",hasSize(2)));
    }


    @Test
    void saveTest() throws Exception {
        Cuenta cuenta = new Cuenta(null, "Dario", new BigDecimal(200));
        when(cuentaService.save(any(Cuenta.class))).then(
          invocation ->{
              Cuenta c  = invocation.getArgument(0);
              c.setId(3L);
              return c;
          }
        );
        mvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(cuenta)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id",is(3)))
        .andExpect(jsonPath("$.persona").value("Dario"))
                .andExpect(jsonPath("$.saldo").value(200));
    }
}