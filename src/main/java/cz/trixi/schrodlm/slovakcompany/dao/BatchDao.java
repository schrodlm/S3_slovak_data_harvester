package cz.trixi.schrodlm.slovakcompany.dao;

import cz.trixi.schrodlm.slovakcompany.model.BatchModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;

@Component
public class BatchDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    String INSERT_SQL = "INSERT INTO batch (batch, export_data,path_to_file) VALUE (?,?,?)";
    String SELECT_ALL = "SELECT *.path_to_file FROM batch";

    public void batchInsert( Collection<BatchModel> batchModelCollection ) {

        List<Object[]> batch = batchModelCollection.stream()
                .map( company -> new Object[] { company.batchName(), company.exportDate(), company.pathToFile()} )
                .collect( Collectors.toList() );

        jdbcTemplate.batchUpdate( INSERT_SQL, batch );
    }

    public List<Path> getAllBatches(){
        List<Path> pathToAllBatches = jdbcTemplate.query( SELECT_ALL, JdbcTemplateMapperFactory.newInstance().newRowMapper( Path.class));

        return pathToAllBatches;
    }
}