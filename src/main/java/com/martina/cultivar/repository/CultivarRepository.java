package com.martina.cultivar.repository;

import com.martina.cultivar.dto.CultivarDto;
import com.martina.cultivar.dto.InserimentoCultivarDto;
import com.martina.cultivar.dto.RiepilogoSpecieDto;
import com.martina.cultivar.entity.Cultivar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Repository
public interface CultivarRepository extends JpaRepository<Cultivar, Long> {

    @Query("SELECT c FROM Cultivar c WHERE UPPER(c.tipoTerreno.nome) = UPPER(:nome) AND :ph BETWEEN c.phMin AND c.phMax")
    List<Cultivar> findAdatte(@Param("nome") String nome, @Param("ph") Double ph);

    List<Cultivar> findByTipoTerrenoNomeIgnoreCase(String nomeTerreno);

    List<Cultivar> findBySpecieIgnoreCase(String nomeSpecie);



}
