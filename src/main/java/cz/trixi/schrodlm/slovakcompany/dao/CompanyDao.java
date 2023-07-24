package cz.trixi.schrodlm.slovakcompany.dao;

import cz.trixi.schrodlm.slovakcompany.model.CompanyMetadata;
import cz.trixi.schrodlm.slovakcompany.model.CompanyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompanyDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    //upsert -> pokud už záznam existuje v DB pouze se aktualizuje a nebude se vytvářet nový
    String UPSERT_SQL = "INSERT INTO company (ico, dbModification,termination) " +
            "VALUES (?, ?, ?) " +
            "ON CONFLICT (ico) DO UPDATE SET " +
            "dbModification = EXCLUDED.dbModification, " +
            "termination = EXCLUDED.termination";

    public void batchInsert( Collection<CompanyModel> companyModelCollection ) {

        List<Object[]> batch = companyModelCollection.stream()
                .map( company -> new Object[] { company.ico(), company.dbModification(), company.termination() } )
                .collect( Collectors.toList() );

        jdbcTemplate.batchUpdate( UPSERT_SQL, batch );
    }
}