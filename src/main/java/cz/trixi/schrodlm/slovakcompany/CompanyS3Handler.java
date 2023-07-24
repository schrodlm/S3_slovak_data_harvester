package cz.trixi.schrodlm.slovakcompany;

import cz.trixi.schrodlm.slovakcompany.model.BatchMetadata;
import jakarta.annotation.PostConstruct;
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

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

/**
 *
 * Handles all communication with SRPO S3 File Storage.
 *
 */
@Component
public class CompanyS3Handler {
    final public URI endpoint = URI.create("https://compat.objectstorage.eu-frankfurt-1.oraclecloud.com/susr-rpo");

    final public String bucket = "frkqbrydxwdp";

    public S3Client s3Client;

    @Value("${zipDir}")
    public String zipDir;

    @Value("${xmlDir}")
    public String xmlDir;

    /**
     * Post construct class that initializes S3Client object
     */
    @PostConstruct
    private void S3Connect() {
        S3Client s3 = S3Client.builder()
                .region(Region.AF_SOUTH_1)
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .endpointOverride( endpoint )
                .build();

        this.s3Client = s3;
    }

    /**
     * Retrieves metadata about an s3 object
     */
    public BatchMetadata retrieveMetadata(S3Object s3Object){

        // Retrieving basic metadata about object
        String key_string = s3Object.key();
        long size = s3Object.size();
        Instant lastModified = s3Object.lastModified();
        String eTag = s3Object.eTag();
        String storageClass = s3Object.storageClassAsString();

        BatchMetadata batchMetadata = new BatchMetadata(key_string, lastModified,eTag,size,storageClass);
        return batchMetadata;
    }

    /**
     *  Downloads all objects from an S3 bucket and saves them as individual files in the specified zipDir.
     */
    public void downloadAllObjects(){

        // Create a ListObjectsV2Request object
        ListObjectsV2Request listObjectsReqManual = ListObjectsV2Request.builder()
                .bucket(bucket)
                .maxKeys(1)
                .build();

        ListObjectsV2Iterable response = s3Client.listObjectsV2Paginator(listObjectsReqManual);


        if(!Files.isDirectory(Paths.get( zipDir )))
        {
            System.out.println("Directory doesn't exist");
            return;
        }


        for (S3Object companiesInfoFile : response.contents()) {

            System.out.println("Downloading zipped file: " + companiesInfoFile.key());

            // Prepare complete path
            Path targetPath = Paths.get(zipDir).resolve(companiesInfoFile.key());


            if (Files.exists(targetPath)) {

                System.out.println("Object " + companiesInfoFile.key() + " has already been downloaded: ");
                continue;
            }

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(companiesInfoFile.key())
                    .build();


            // Downloading object
            s3Client.getObject(objectRequest, ResponseTransformer.toFile(targetPath));

            //Retrieve metadata
            BatchMetadata batchMetadata = retrieveMetadata( companiesInfoFile );

            System.out.println("Downloaded object: " + companiesInfoFile.key());
        }
    }
}
