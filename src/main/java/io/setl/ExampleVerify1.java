package io.setl;

import static io.setl.ExampleSigning.loadXMLResource;

import org.w3c.dom.Document;

import io.setl.xml.IsoSignerVerifier;

/**
 * A simple command line application that attempts to verify a valid document.
 *
 * @author Simon Greatrix on 04/08/2022.
 */
public class ExampleVerify1 {


  public static void main(String[] args) throws Exception {
    Document appHdrDoc = loadXMLResource("examples/verify1/apphdr.xml");
    Document document = loadXMLResource("examples/verify1/document.xml");
    boolean isValid = new IsoSignerVerifier().validate(appHdrDoc.getDocumentElement(), document.getDocumentElement());
    System.out.println("The signature is valid? (should be true) " + isValid);
  }


}
