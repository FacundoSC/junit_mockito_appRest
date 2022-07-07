package org.fcordoba.springboot.test.repository;

import org.fcordoba.springboot.test.models.Banco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BancoRepository extends JpaRepository<Banco,Long> {
    //List<Banco> findAll();
   // Banco findByID(Long idBanco);
   // void update(Banco banco);
}
