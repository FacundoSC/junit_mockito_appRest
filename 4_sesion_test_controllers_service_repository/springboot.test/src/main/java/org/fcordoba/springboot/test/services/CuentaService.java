package org.fcordoba.springboot.test.services;

import org.fcordoba.springboot.test.models.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {

    List<Cuenta> findAll();

   void deleteById(Long id);
    Cuenta save(Cuenta cuenta);
    Cuenta findById(Long Id);
    int revisarTotalTransferencias(Long bancoId);
    BigDecimal revisarSaldo (Long idCuenta);
    void transferir(Long idCuentaOrigen, Long idCuentaDestino, BigDecimal monto, Long idBanco);
}
