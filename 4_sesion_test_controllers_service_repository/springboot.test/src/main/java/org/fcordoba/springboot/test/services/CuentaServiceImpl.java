package org.fcordoba.springboot.test.services;

import org.fcordoba.springboot.test.models.*;
import org.fcordoba.springboot.test.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CuentaServiceImpl implements CuentaService {

    private CuentaRepository cuentaRepository;
    private BancoRepository bancoRepository;

    public CuentaServiceImpl(CuentaRepository cuentaRepository, BancoRepository bancoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.bancoRepository = bancoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> findAll() {
        return cuentaRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        cuentaRepository.deleteById(id);
    }

    @Override
    public Cuenta save(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta findById(Long Id) {
        return cuentaRepository.findById(Id).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public int revisarTotalTransferencias(Long bancoId) {
        Banco banco = bancoRepository.findById(bancoId).orElseThrow();
        return banco.getTotalTransferencias();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal revisarSaldo(Long idCuenta) {
        Cuenta cuenta = cuentaRepository.findById(idCuenta).orElseThrow();
        return cuenta.getSaldo();
    }

    @Override
    @Transactional
    public void transferir(Long idCuentaOrigen, Long idCuentaDestino, BigDecimal monto, Long idBanco) {
         Banco banco = bancoRepository.findById(idBanco).orElseThrow();
         Cuenta cuentaOrigen =cuentaRepository.findById(idCuentaOrigen).orElseThrow();
         Cuenta cuentaDestino  = cuentaRepository.findById(idCuentaDestino).orElseThrow();
         cuentaOrigen.debito(monto);
         cuentaDestino.credito(monto);
         int totalTransferencias= banco.getTotalTransferencias();
         banco.setTotalTransferencias(++totalTransferencias);
         cuentaRepository.save(cuentaOrigen);
         cuentaRepository.save(cuentaDestino);
         bancoRepository.save(banco);
    }
}