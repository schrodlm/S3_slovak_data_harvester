package cz.trixi.schrodlm.slovakcompany.model;

import java.time.Instant;

/**
 * This class represents metadata about content of a bucket from Amazon S3 file system parsed from XML file. In specific
 * data about Slovakian Register batches taken from "https://frkqbrydxwdp.compat.objectstorage.eu-frankfurt-1.oraclecloud.com/susr-rpo"
 */


// TODO: Could be a record
public class CompanyMetadata {


    //TODO: Find out what type should @lastModified be
    public CompanyMetadata(String key,  Instant lastModified, String ETag, long size, String storageClass) {
        this.key = key;
        this.lastModified = lastModified;
        this.ETag = ETag;
        this.size = size;
        this.storageClass = storageClass;
    }

    public String key;
    public Instant lastModified;

    public String ETag;

    public long size;

    public String storageClass;

}
