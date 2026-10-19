package com.martina.cultivar.repository;

import com.martina.cultivar.dto.StatisticheTerrenoDto;
import com.martina.cultivar.entity.Cultivar;
import com.martina.cultivar.entity.TipoTerreno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoTerrenoRepository extends JpaRepository<TipoTerreno, Long> {

    Optional<TipoTerreno> findByNomeIgnoreCase(String nome);

}
