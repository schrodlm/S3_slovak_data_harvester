package cz.trixi.schrodlm.slovakcompany.parsing;

import cz.trixi.schrodlm.slovakcompany.model.BatchMetadata;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;

/**
 * This class will handle all parsing of batches, even batch metadata files
 */
public class XMLBatchParser {

    //this file should contain all URLs to daily and init batches of the Slovakian Register
    public File registerMetadataFile;
    public XMLBatchParser(File dataDirectory) {
        this.registerMetadataFile = dataDirectory;
    }

    /**
     * Method will try to parse batch metadata file and will fill both of input collections with
     * right content
     */
    public void parseBatchMetadata(Collection<BatchMetadata> init_batches,Collection<BatchMetadata> update_batches) throws ParserConfigurationException, IOException, SAXException {

        //directory is empty
        if(!registerMetadataFile.exists()){
            System.out.println("The XML Batch with file path: " + registerMetadataFile.getPath() + " doesn't exists");
            return;
        }

        System.out.println("Parsing Slovakian Company Register metadata XML from " + registerMetadataFile.getPath());


            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(registerMetadataFile);

            //Normalize the XML structure
            document.getDocumentElement().normalize();

            //============================================================
            // BATCH PARSING
            //============================================================

            // Get all municipalities by their tag name
            NodeList batchList = document.getElementsByTagName("Contents");


            for (int i = 0; i < batchList.getLength(); i++) {
                Node batch = batchList.item(i);
                if (batch.getNodeType() == Node.ELEMENT_NODE) {

                    Element batchElement = (Element) batch;

                    String key = ((Element) batch).getAttribute("Key");

                    //get specific batch details
                    try {
                        String link = batchElement.getElementsByTagName("Key").item(0).getTextContent();
                        String lastModified = batchElement.getElementsByTagName("LastModified").item(0).getTextContent();
                        String ETag = batchElement.getElementsByTagName("ETag").item(0).getTextContent();
                        BigInteger size = new BigInteger(batchElement.getElementsByTagName("Size").item(0).getTextContent());
                        String storageClass = batchElement.getElementsByTagName("StorageClass").item(0).getTextContent();


                        if(link.startsWith("batch-init") && link.endsWith("json.gz"))
                            init_batches.add(new BatchMetadata(link,lastModified,ETag, size,storageClass));

                        else if(link.startsWith("batch-daily"))
                            update_batches.add(new BatchMetadata(link,lastModified,ETag,size,storageClass));

                    } catch (NullPointerException e) {
                        throw new RuntimeException("Error: Insufficient information/wrong format provided for Municipality Entity in the XML file");
                    }
                }

            }
            System.out.println("Parsing of batch metadata file completed.");

        }

    }
