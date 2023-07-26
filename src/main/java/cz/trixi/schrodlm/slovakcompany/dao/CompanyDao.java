package cz.trixi.schrodlm.slovakcompany.dao;

import cz.trixi.schrodlm.slovakcompany.model.BatchModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompanyDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    String INSERT_SQL = "INSERT INTO company (batch, export_data,path_to_file) VALUE (?,?,?)";

    public void batchInsert( Collection<BatchModel> batchModelCollection ) {

        List<Object[]> batch = batchModelCollection.stream()
                .map( company -> new Object[] { company.batchName(), company.exportDate(), company.pathToFile()} )
                .collect( Collectors.toList() );

        jdbcTemplate.batchUpdate( INSERT_SQL, batch );
    }
}