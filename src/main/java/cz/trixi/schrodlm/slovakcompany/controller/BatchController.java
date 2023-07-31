package cz.trixi.schrodlm.slovakcompany.controller;

import cz.trixi.schrodlm.slovakcompany.service.BatchServerService;
import cz.trixi.schrodlm.slovakcompany.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class BatchController {

    @Autowired
    BatchService batchService;

    @Autowired
    BatchServerService batchServerService;

    /**
     * Downloads batch corresponding to today's date
     */
    @GetMapping("/downloadTodaysBatch")
    public ResponseEntity<Resource> serveTodaysBatch() {
        Resource resource = batchServerService.serveTodaysBatch();

        return ResponseEntity.ok()
                .contentType( MediaType.APPLICATION_JSON )
                .header( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"" )
                .body( resource );
    }

    /**
     * Downloads all batches available from the database
     */
    @GetMapping("/downloadAllBatches")
    public ResponseEntity<ByteArrayResource> serveAllBatches() throws IOException {
        List<Resource> resources = batchServerService.serveAllBatches();

        File zipFile = File.createTempFile( "batches", ".zip" );

        try (ZipOutputStream zipOut = new ZipOutputStream( new FileOutputStream( zipFile ) )) {
            for ( Resource resource : resources ) {
                ZipEntry zipEntry = new ZipEntry( Objects.requireNonNull( resource.getFilename() ) );
                zipOut.putNextEntry( zipEntry );

                InputStream in = resource.getInputStream();
                byte[] buffer = new byte[2048];

                int len;
                while((len = in.read(buffer)) > 0){
                    zipOut.write( buffer, 0, len );
                }
                in.close();
            }
        }

        byte [] zipContent = Files.readAllBytes(zipFile.toPath());
        ByteArrayResource byteArrayResource = new ByteArrayResource( zipContent );

        // Clean up the temporary zip file
        Files.delete( zipFile.toPath() );

        return ResponseEntity.ok(  )
                .contentType( MediaType.APPLICATION_JSON )
                .header( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = batches.zip" )
                .body( byteArrayResource );

    }
    /**
     * @GetMapping("/downloadAllBatches")
     * public ResponseEntity<ByteArrayResource> serveAllBatches() throws IOException {
     *     List<Resource> resources = batchServerService.serveAllBatches();
     *
     *     // Temporary zip file
     *     File zipFile = File.createTempFile("batches", ".zip");
     *
     *     try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
     *         for (Resource resource : resources) {
     *             ZipEntry zipEntry = new ZipEntry(Objects.requireNonNull(resource.getFilename()));
     *             zipOut.putNextEntry(zipEntry);
     *
     *             InputStream in = resource.getInputStream();
     *             byte[] buffer = new byte[1024];
     *             int len;
     *             while ((len = in.read(buffer)) > 0) {
     *                 zipOut.write(buffer, 0, len);
     *             }
     *             in.close();
     *         }
     *     }
     *
     *     byte[] zipContent = Files.readAllBytes(zipFile.toPath());
     *     ByteArrayResource byteArrayResource = new ByteArrayResource(zipContent);
     *
     *     // Clean up the temporary zip file
     *     Files.delete(zipFile.toPath());
     *
     *     return ResponseEntity.ok()
     *             .contentType(MediaType.APPLICATION_OCTET_STREAM)
     *             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=batches.zip")
     *             .body(byteArrayResource);
     * }
     */

}
