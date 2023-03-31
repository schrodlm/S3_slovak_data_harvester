package cz.trixi.schrodlm.slovakcompany.model;

import java.math.BigInteger;
import java.sql.Time;

/**
 * This class represents metadata about content of a bucket from Amazon S3 file system parsed from XML file. In specific
 * data about Slovakian Register batches taken from "https://frkqbrydxwdp.compat.objectstorage.eu-frankfurt-1.oraclecloud.com/susr-rpo"
 */
public class BatchMetadata {


    public BatchMetadata(String link, String lastModified, String ETag, BigInteger size, String storageClass) {
        this.link = link;
        this.lastModified = lastModified;
        this.ETag = ETag;
        this.size = size;
        this.storageClass = storageClass;
    }

    public String link;
    public String lastModified;

    public String ETag;

    public BigInteger size;

    public String storageClass;

}
