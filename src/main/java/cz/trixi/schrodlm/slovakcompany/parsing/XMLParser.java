package cz.trixi.schrodlm.slovakcompany.parsing;

import cz.trixi.schrodlm.slovakcompany.model.BatchLink;
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
import java.util.Objects;

public class XMLParser {
    //this file contains all the 45 URLs to daily and init batches of the Slovakian Register
    public File XMLRegisterFile;
    public XMLParser(File dataDirectory) {
        this.XMLRegisterFile = dataDirectory;
    }

    /**
     *  Recursively parse all XML files in provided directory and saves them to the database. When file is parsed, it is deleted.
     */
    public void parse() throws ParserConfigurationException, IOException, SAXException {

        //directory is empty
        if(!XMLRegisterFile.exists()){
            System.out.println("The XML Batch with file path: " + XMLRegisterFile.getPath() + " doesn't exists");
            return;
        }

        System.out.println("Parsing Slovakian Company Register batches XML from " + XMLRegisterFile.getPath());


            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(XMLRegisterFile);

            //Normalize the XML structure
            document.getDocumentElement().normalize();

            //============================================================
            // MUNICIPALITY PARSING
            //============================================================

            // Get all municipalities by their tag name
            NodeList batchList = document.getElementsByTagName("Contents");


            for (int i = 0; i < batchList.getLength(); i++) {
                Node batch = batchList.item(i);
                if (batch.getNodeType() == Node.ELEMENT_NODE) {

                    Element municipalityElement = (Element) batch;

                    String key = ((Element) batch).getAttribute("Key");

                    //get specific municipality details
                    //Municipality Entity will only have one code and one name provided so fetching item(0) can be done
                    try {
                        //TODO: Tady dodělat ten objekt batche a zjistit jak to optimálně parsovat
                        String link = municipalityElement.getElementsByTagName("Key").item(0).getTextContent();

                        System.out.println(link);
                     //   BatchLink batch = new BatchLink();

                    } catch (NullPointerException e) {
                        throw new RuntimeException("Error: Insufficient information/wrong format provided for Municipality Entity in the XML file");
                    }
                }

            }
            System.out.println("Parsing of XML Batch file completed.");

        }

    }
