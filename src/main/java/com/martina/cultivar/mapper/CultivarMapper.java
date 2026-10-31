package com.martina.cultivar.mapper;

import com.martina.cultivar.dto.CultivarDto;
import com.martina.cultivar.dto.InserimentoCultivarDto;
import com.martina.cultivar.dto.ModificaParzialeCultivarDto;
import com.martina.cultivar.dto.PhTerrenoDto;
import com.martina.cultivar.entity.Cultivar;
import com.martina.cultivar.entity.TipoTerreno;
import jakarta.validation.constraints.Null;
import org.mapstruct.*;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface CultivarMapper {

    @Mapping(target = "tipoTerreno", source = "tipoTerreno.nome")
    CultivarDto cultivarToCultivarDto(Cultivar cultivar);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "dto.nome")
    @Mapping(target = "tipoTerreno", source = "terreno")
    Cultivar inserimentoCultivarDtotoCultivarDto(InserimentoCultivarDto dto, TipoTerreno terreno);

    //MapStruct ha @MappingTarget, che dice: “non creare un oggetto nuovo, riempi quello che ti passo”. Nel mapper:
    //il mapper non “fa uscire” niente, lavora sull’oggetto che gli dai. Poi nel service vai avanti tranquilla, perché sai che quell’oggetto è già cambiato.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "dto.nome")
    @Mapping(target = "tipoTerreno", source = "terreno")
    void aggiornaCultivar(InserimentoCultivarDto dto, TipoTerreno terreno, @MappingTarget Cultivar cultivar);

    //se un campo del DTO è null, non toccare quello della cultivar
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "dto.nome")
    @Mapping(target = "tipoTerreno", source = "terreno")
    void dtoETerrenoToCultivar(ModificaParzialeCultivarDto dto, TipoTerreno terreno, @MappingTarget Cultivar cultivar);

}
