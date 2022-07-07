package org.fcordoba.springboot.test.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fcordoba.springboot.test.models.Cuenta;
import org.fcordoba.springboot.test.models.TransaccionDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Tag("integracion_wc")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CuentaControllerWebClientTest {

    @Autowired
    private WebTestClient webTestClient;

    ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }
    @Test
    @Order(3)
    void transferenciaTest() throws JsonProcessingException {
        // Given
        TransaccionDTO transaccionDTO = new TransaccionDTO();
        transaccionDTO.setCuentaOrigenId(1L);
        transaccionDTO.setCuentaDestinoId(2L);
        transaccionDTO.setBancoId(1L);
        transaccionDTO.setMonto(new BigDecimal(200));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con Exito");
        response.put("transferencia", transaccionDTO);

       // When
        webTestClient.post().uri("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transaccionDTO)
                .exchange()
        //Then
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(resp->{
                    try {
                        JsonNode jsonNode = objectMapper.readTree(resp.getResponseBody());
                        assertEquals(response.get("mensaje").toString(),jsonNode.path("mensaje").asText());
                        assertEquals(1,jsonNode.path("transferencia").path("cuentaOrigenId").asLong());

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .jsonPath("$.mensaje").isNotEmpty()
                .jsonPath("$.mensaje").value(is("Transferencia realizada con Exito"))
                .jsonPath("$.mensaje").value( valor-> assertEquals("Transferencia realizada con Exito",valor))
                .jsonPath("$.mensaje").isEqualTo("Transferencia realizada con Exito")
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response));
    }
    @Test
    @Order(1)
    void detalleTest() {
        webTestClient.get().uri("/api/cuentas/1").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.persona").isEqualTo("Facundo")
                .jsonPath("$.saldo").isEqualTo(1000);
    }
    @Test
    @Order(2)
    void detalleTest2() {
        webTestClient.get().uri("/api/cuentas/2").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response ->{
                    Cuenta responseBody = response.getResponseBody();
                    assertEquals("Fatima",responseBody.getPersona());
                    assertEquals("2000.00",responseBody.getSaldo().toPlainString());
                });

    }
    @Test
    @Order(4)
    void listarV1Test() {
        webTestClient.get().uri("/api/cuentas/").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2))
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].persona").isEqualTo("Facundo")
                .jsonPath("$[0].saldo").isEqualTo("800.0")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].persona").isEqualTo("Fatima")
                .jsonPath("$[1].saldo").isEqualTo("2200.0");
    }
    @Test
    @Order(5)
    void saveTest() {
        Cuenta cuenta = new Cuenta(null, "pepe", new BigDecimal(100));
        webTestClient.post().uri("/api/cuentas")
              .contentType(MediaType.APPLICATION_JSON).bodyValue(cuenta)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.persona").isEqualTo("pepe")
                .jsonPath("$.saldo").isEqualTo("100");
    }
    @Test
    @Order(6)
    void saveV2Test() {
        Cuenta cuenta = new Cuenta(null, "pepa", new BigDecimal(100));
        webTestClient.post().uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON).bodyValue(cuenta)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(response->{
                    Cuenta responseBody = response.getResponseBody();
                    assertNotNull(responseBody);
                    assertEquals("pepa",responseBody.getPersona());
                    assertEquals("100",responseBody.getSaldo().toString());

                });

    }
    @Test
    @Order(4)
    void listarV2Test() {
        webTestClient.get().uri("/api/cuentas/").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .consumeWith(response->{
                    List<Cuenta> cuentas = response.getResponseBody();
                    assertNotNull(cuentas);
                    assertEquals(2,cuentas.size());
                    assertEquals(1,cuentas.get(0).getId());
                    assertEquals("Facundo",cuentas.get(0).getPersona());
                    assertEquals("800.0",cuentas.get(0).getSaldo().toPlainString());
                    assertEquals(2,cuentas.get(1).getId());
                    assertEquals("Fatima",cuentas.get(1).getPersona());
                    assertEquals("2200.0",cuentas.get(1).getSaldo().toPlainString());
                }).hasSize(2).value(hasSize(2));

    }


    @Test
    @Order(7)
    void deleteTest() {
        webTestClient.get().uri("/api/cuentas").exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBodyList(Cuenta.class)
                                .hasSize(2);

        webTestClient.delete().uri("/api/cuentas/2")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();


        webTestClient.get().uri("/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(1);
        webTestClient.get().uri("/api/cuentas/2").exchange()
                //.expectStatus().is5xxServerError();
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

    }
}