package org.fcordoba.springboot.test.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fcordoba.springboot.test.models.Cuenta;
import org.fcordoba.springboot.test.models.TransaccionDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@Tag("integracion_tr")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CuentaControllerRestTemplateTest {
    @Autowired
    private TestRestTemplate cliente;
    private ObjectMapper mapper;

    @LocalServerPort
    private int puerto;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    @Order(3)
    void transferirTest() throws JsonProcessingException {
        // Given
        TransaccionDTO transaccionDTO = new TransaccionDTO();
        transaccionDTO.setCuentaOrigenId(1L);
        transaccionDTO.setCuentaDestinoId(2L);
        transaccionDTO.setBancoId(1L);
        transaccionDTO.setMonto(new BigDecimal(200));
        ResponseEntity<String> response = cliente
                .postForEntity(crearUri("/api/cuentas/transferir"), transaccionDTO, String.class);
        System.out.println(puerto);
        String json = response.getBody();
        assertNotNull(json);
        System.out.println(json);
        assertEquals("{\"date\":\"2022-07-07\",\"transferencia\":{\"cuentaOrigenId\":1,\"cuentaDestinoId\":2,\"monto\":200,\"bancoId\":1},\"mensaje\":\"Transferencia realizada con Exito\",\"status\":\"OK\"}", json);
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertTrue(json.contains("Transferencia realizada con Exito"));
        JsonNode jsonNode = mapper.readTree(json);
        assertEquals("OK", jsonNode.path("status").asText());

        Map<String, Object> response2 = new HashMap<>();
        response2.put("date", LocalDate.now().toString());
        response2.put("status", "OK");
        response2.put("mensaje", "Transferencia realizada con Exito");
        response2.put("transferencia", transaccionDTO);
        assertEquals(mapper.writeValueAsString(response2), json);
    }

    @Test
    @Order(1)
    void detalleCuenta() {
        ResponseEntity<Cuenta> response = cliente
                .getForEntity(crearUri("/api/cuentas/1"), Cuenta.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("Facundo", response.getBody().getPersona());
        assertEquals("1000.00", response.getBody().getSaldo().toPlainString());
    }


    @Test
    @Order(2)
    void listarTest() throws JsonProcessingException {
        ResponseEntity<Cuenta[]> respuesta = cliente
                .getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());
        List<Cuenta> cuentas = Arrays.asList(respuesta.getBody());
        assertNotNull(cuentas);
        assertFalse(cuentas.isEmpty());
        assertEquals("Facundo", cuentas.get(0).getPersona());
        assertEquals("1000.00", cuentas.get(0).getSaldo().toPlainString());
        assertEquals(1, cuentas.get(0).getId());
        JsonNode jsonNode = mapper.readTree(mapper.writeValueAsString(cuentas));
        assertEquals("Facundo", jsonNode.get(0).path("persona").asText());
        assertEquals("1000.0", jsonNode.get(0).path("saldo").asText());
        assertEquals(1L, jsonNode.get(0).path("id").asLong());
    }


    @Test
    @Order(4)
    void saveTest() {
        Cuenta cuenta = new Cuenta(null, "pepe", new BigDecimal(100));
        ResponseEntity<Cuenta> response = cliente.postForEntity(crearUri("/api/cuentas"), cuenta, Cuenta.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON,response.getHeaders().getContentType());
        Cuenta body = response.getBody();
        assertNotNull(body);
        assertEquals("pepe",body.getPersona());
        assertEquals("100",body.getSaldo().toPlainString());
        assertEquals(3, body.getId());

    }

    @Test
    @Order(5)
    void deleteTest() {
        ResponseEntity<Cuenta[]> respuesta = cliente
                .getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());
        List<Cuenta> cuentas = Arrays.asList(respuesta.getBody());
        assertNotNull(cuentas);
        assertFalse(cuentas.isEmpty());
       assertTrue(cuentas.size()==3);

        //cliente.delete(crearUri("/api/cuentas/3"));

        Map<String ,Long> pathVariable = new HashMap<>();
        pathVariable.put("id",3l);
        //ResponseEntity<String> exchange = cliente.exchange(crearUri("/api/cuentas/2"), HttpMethod.DELETE, null, String.class);
        ResponseEntity<String> exchange = cliente.exchange(crearUri("/api/cuentas/{id}"), HttpMethod.DELETE, null, String.class,pathVariable);
        assertEquals(HttpStatus.NO_CONTENT,exchange.getStatusCode());
        assertFalse(exchange.hasBody());

        respuesta = cliente
                .getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());
         cuentas = Arrays.asList(respuesta.getBody());
        assertNotNull(cuentas);
        assertFalse(cuentas.isEmpty());
        assertTrue(cuentas.size()==2);

        ResponseEntity<Cuenta> response = cliente
                .getForEntity(crearUri("/api/cuentas/3"), Cuenta.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.hasBody());
    }

    private String crearUri(String path) {
        return "http://localhost:" + puerto + path;
    }
}
