package cz.trixi.schrodlm.slovakcompany.service;

import cz.trixi.schrodlm.slovakcompany.model.BatchMetadata;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Handles all communication with SRPO S3 File Storage.
 */
@Component
public class BatchS3Handler {
    final public URI endpoint = URI.create( "https://compat.objectstorage.eu-frankfurt-1.oraclecloud.com/susr-rpo" );

    final public String bucket = "frkqbrydxwdp";

    public S3Client s3Client;

    @Value("${zipDir}")
    public String zipDir;

    @Value("${unzippedDir}")
    public String xmlDir;

    Logger log = LoggerFactory.getLogger( getClass() );

    /**
     * Post construct class that initializes S3Client object
     */
    @PostConstruct
    private void S3Connect() {
        S3Client s3 = S3Client.builder()
                .region( Region.AF_SOUTH_1 )
                .credentialsProvider( AnonymousCredentialsProvider.create() )
                .endpointOverride( endpoint )
                .build();

        this.s3Client = s3;
    }

    /**
     * Retrieves metadata about an s3 object
     */
    public BatchMetadata retrieveMetadata( S3Object s3Object ) {

        // Retrieving basic metadata about object
        String key_string = s3Object.key();
        long size = s3Object.size();
        Instant lastModified = s3Object.lastModified();
        String eTag = s3Object.eTag();
        String storageClass = s3Object.storageClassAsString();

        BatchMetadata companyMetadata = new BatchMetadata( key_string, lastModified, eTag, size, storageClass );
        return companyMetadata;
    }

    /**
     * Downloads all objects (zipped JSONs containing info about companies) from an S3 bucket and saves them as individual files
     * in the specified zipDir.
     */
    public void downloadAllBatches() {

        // Create a ListObjectsV2Request object
        ListObjectsV2Request listObjectsReqManual = ListObjectsV2Request.builder()
                .bucket( bucket )
                .build();

        ListObjectsV2Iterable response = s3Client.listObjectsV2Paginator( listObjectsReqManual );

        if ( !Files.isDirectory( Paths.get( zipDir ) ) ) {
            log.info( "Directory doesn't exist" );
            return;
        }

        for ( S3Object companiesInfoFile : response.contents() ) {

            log.info( "Downloading zipped batch: " + companiesInfoFile.key() );

            // Prepare complete path
            Path targetPath = Paths.get( zipDir ).resolve( companiesInfoFile.key() );

            if ( Files.exists( targetPath ) ) {

                log.info( "Batch \"" + companiesInfoFile.key() + "\" has already been downloaded... " );
                continue;
            }

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket( bucket )
                    .key( companiesInfoFile.key() )
                    .build();

            // Downloading object
            s3Client.getObject( objectRequest, ResponseTransformer.toFile( targetPath ) );

            log.info( "Downloaded zipped batch: " + companiesInfoFile.key() );
        }
    }

    /**
     * Download a zipped file containing changes to companies from specified date
     */
    public void downloadBatchFrom( LocalDate date ) {
        String formattedDate = date.format( DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) );
        String key = "batch-daily/actual_" + formattedDate + ".json.gz";

        if ( Files.exists( Paths.get( zipDir ).resolve( key ) ) )
        {
            log.warn( "File \"{}\" already exists.", key);
            return;
        }

            GetObjectRequest s3ObectReq = GetObjectRequest.builder()
                    .bucket( bucket )
                    .key( key )
                    .build();
        log.info( "Downloading {} batch...", formattedDate );
        s3Client.getObject( s3ObectReq, ResponseTransformer.toFile( Paths.get( zipDir ).resolve( key ) ) );
    }

    public void downloadTodaysBatch() {
        downloadBatchFrom( LocalDate.now() );
    }
}

