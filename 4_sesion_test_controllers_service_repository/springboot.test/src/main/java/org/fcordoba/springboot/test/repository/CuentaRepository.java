package org.fcordoba.springboot.test.repository;

import org.fcordoba.springboot.test.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;



@Repository
public interface CuentaRepository extends JpaRepository<Cuenta,Long> {
   // List<Cuenta> findAll();
   //  Cuenta findById(Long id);
    // void update(Cuenta cuenta);

     @Query("select c from Cuenta c where c.persona =?1")
     Optional<Cuenta> findByPersona(String persona);

}
