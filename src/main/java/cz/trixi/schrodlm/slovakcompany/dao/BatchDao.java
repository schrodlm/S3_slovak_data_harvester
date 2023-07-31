package cz.trixi.schrodlm.slovakcompany.dao;

import cz.trixi.schrodlm.slovakcompany.model.BatchModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class BatchDao {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    String INSERT_SQL = "INSERT INTO batch_info (batch_name, export_date,path_to_file) VALUES (?,?,?)";

    public void batchInsert( Collection<BatchModel> batchModelCollection ) {

        List<Object[]> batch = batchModelCollection.stream()
                .map( company -> new Object[] { company.batchName(), company.exportDate(), company.pathToFile().toString() } )
                .collect( Collectors.toList() );

        jdbcTemplate.batchUpdate( INSERT_SQL, batch );
    }

    public void insert( BatchModel batchModel ) {
        jdbcTemplate.update( INSERT_SQL, batchModel );
    }

    public List<Path> getAllBatches() {

        String SELECT_ALL = "SELECT b.path_to_file FROM batch_info AS b";

        List<Path> pathToAllBatches = jdbcTemplate.query( SELECT_ALL,
                ( ResultSet rs, int rowNum ) -> {
                    String filePath = rs.getString( "path_to_file" );
                    return filePath == null ? null : Paths.get( filePath );
                } );
        return pathToAllBatches;
    }

    /**
     * Retrieves a list of batch file paths from the database which have an export date on or before the provided date.
     *
     * The resulting paths are collected into a list and returned. If no paths are found, an empty list is returned.
     *
     * @param date The date up to which batch paths should be fetched.
     * @return A list of Paths pointing to batch files that were exported after before the given date.
     * @throws SQLException If any database operation fails.
     */
    public List<Path> getBatchesSince( LocalDate date ) {

        List<Path> pathsToBatches = new ArrayList<>();

        String SELECT_BATCHES_SINCE = "SELECT b.path_to_file FROM batch_info AS b WHERE b.export_date > ?";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement( SELECT_BATCHES_SINCE )) {
            ps.setDate( 1, Date.valueOf( date ));

            try (ResultSet rs = ps.executeQuery()) {
                while ( rs.next() ) {
                    String path = rs.getString( "path_to_file" );
                    pathsToBatches.add( Paths.get( path ) );
                }
            }
        }
        catch ( SQLException e ) {
            throw new RuntimeException( e );
        }

        return pathsToBatches;
    }
}