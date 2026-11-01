package com.martina.cultivar.repository;

import com.martina.cultivar.dto.StatisticheTerrenoDto;
import com.martina.cultivar.entity.Cultivar;
import com.martina.cultivar.entity.TipoTerreno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoTerrenoRepository extends JpaRepository<TipoTerreno, Long> {

    @Query("SELECT t FROM TipoTerreno t LEFT JOIN FETCH t.cultivars")   // tutti i terreni + le loro cultivar, in UNA query
    List<TipoTerreno> findAllConCultivar();

    Optional<TipoTerreno> findByNomeIgnoreCase(String nome);

}
