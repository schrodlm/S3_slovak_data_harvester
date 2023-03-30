package cz.trixi.schrodlm.slovakcompany.model;

import java.math.BigInteger;
import java.sql.Time;

public class BatchLink {


    public BatchLink(String link, Time lastModified, String ETag, BigInteger size, String storageClass) {
        this.link = link;
        this.lastModified = lastModified;
        this.ETag = ETag;
        this.size = size;
        this.storageClass = storageClass;
    }

    public String link;
    public Time lastModified;

    public String ETag;

    public BigInteger size;

    public String storageClass;




    /*
<Key>batch-init/init_2023-03-04_008.json.gz</Key>
<LastModified>2023-03-04T21:00:16.000Z</LastModified>
<ETag>"5f38bef0d35b05ea2ebfc13221bf435f"</ETag>
<Size>35482295</Size>
<StorageClass>STANDARD</StorageClass>
<Owner>
<ID>AnonymousUserSubject</ID>
<DisplayName>AnonymousUserSubject</DisplayName>
</Owner>
*/
}
