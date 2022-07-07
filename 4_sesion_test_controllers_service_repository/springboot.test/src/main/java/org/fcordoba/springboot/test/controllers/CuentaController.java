package org.fcordoba.springboot.test.controllers;


import org.fcordoba.springboot.test.models.Cuenta;
import org.fcordoba.springboot.test.models.TransaccionDTO;
import org.fcordoba.springboot.test.services.CuentaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;


    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.status(OK).body(cuentaService.findAll());
    }


    @PostMapping
    public ResponseEntity<?> save(@RequestBody Cuenta cuenta) {
        return ResponseEntity.status(CREATED).body(cuentaService.save(cuenta));
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public ResponseEntity<?> detalle(@PathVariable("id") Long id) {
        Cuenta cuenta = null;
        try {
           cuenta = cuentaService.findById(id);
        }
        catch (NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
         return ResponseEntity.ok(cuenta);
    }

    @PostMapping("/transferir")
    public ResponseEntity<?> transferir(@RequestBody TransaccionDTO dto) {
        cuentaService.transferir(dto.getCuentaOrigenId(),
                dto.getCuentaDestinoId(),
                dto.getMonto(), dto.getBancoId());
        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con Exito");
        response.put("transferencia", dto);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCuenta(@PathVariable Long id){
        cuentaService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

}
