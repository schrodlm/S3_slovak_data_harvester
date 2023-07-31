package cz.trixi.schrodlm.slovakcompany.dao;

import cz.trixi.schrodlm.slovakcompany.model.BatchModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.stereotype.Repository;

@Repository
public class BatchDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    String INSERT_SQL = "INSERT INTO batch_info (batch_name, export_date,path_to_file) VALUES (?,?,?)";

    public void batchInsert( Collection<BatchModel> batchModelCollection ) {

        List<Object[]> batch = batchModelCollection.stream()
                .map( company -> new Object[] { company.batchName(), company.exportDate(), company.pathToFile().toString()} )
                .collect( Collectors.toList() );

        jdbcTemplate.batchUpdate( INSERT_SQL, batch );
    }

    public List<Path> getAllBatches(){

        String SELECT_ALL = "SELECT b.path_to_file FROM batch_info AS b";

        List<Path> pathToAllBatches = jdbcTemplate.query( SELECT_ALL,
                ( ResultSet rs, int rowNum ) -> {
                    String filePath = rs.getString( "path_to_file" );
                    return filePath == null ? null : Paths.get(filePath);
                });
        return pathToAllBatches;
    }
}