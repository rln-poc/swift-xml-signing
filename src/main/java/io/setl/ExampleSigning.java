package io.setl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;

import io.setl.xml.IsoSignerVerifier;
import io.setl.xml.XMLSignatureMethod;

/**
 * A simple command line application that demonstrates the signing of a business application header with an associated document.
 *
 * @author Simon Greatrix on 04/08/2022.
 */
public class ExampleSigning {

  /** XML load and save support. */
  private static final DOMImplementationLS DOM_IMPLEMENTATION_LS;

  /** XML Transformer for converting documents to text. */
  private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();


  public static KeyStore loadKeyStore(String resource) throws IOException, GeneralSecurityException {
    try (InputStream inputStream = ExampleSigning.class.getClassLoader().getResourceAsStream(resource)) {
      KeyStore keyStore = KeyStore.getInstance("JKS");
      keyStore.load(inputStream, "password".toCharArray());
      return keyStore;
    }
  }


  public static Document loadXMLResource(String resource) throws IOException {
    try (InputStream inputStream = ExampleSigning.class.getClassLoader().getResourceAsStream(resource)) {
      String text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      return parse(text);
    }
  }


  public static void main(String[] args) throws Exception {
    Document appHdrDoc = loadXMLResource("examples/sign1/apphdr.xml");
    Document document = loadXMLResource("examples/sign1/document.xml");
    KeyStore keyStore = loadKeyStore("examples/keystore.jks");
    PrivateKey privateKey = (PrivateKey) keyStore.getKey("example", "password".toCharArray());
    X509Certificate x509 = (X509Certificate) keyStore.getCertificate("example");

    IsoSignerVerifier isoSignerVerifier = new IsoSignerVerifier();
    isoSignerVerifier.sign(appHdrDoc.getDocumentElement(), document.getDocumentElement(), privateKey, x509, XMLSignatureMethod.RSA_SHA256);
    System.out.println("\n\nSigned header:\n\n" + xmlToString(appHdrDoc.getDocumentElement()));

    boolean crossCheck = isoSignerVerifier.validate(appHdrDoc.getDocumentElement(), document.getDocumentElement());
    System.out.println("\n\nDid we produce a valid signature? : " + crossCheck);

    try(FileWriter writer = new FileWriter("./signed-apphdr.xml")) {
      writer.write(xmlToString(appHdrDoc.getDocumentElement()));
    }
  }


  /**
   * Convert some XML to a document.
   *
   * @param rawXml the raw XML
   *
   * @return the document
   */
  public static Document parse(String rawXml) {
    LSInput input = DOM_IMPLEMENTATION_LS.createLSInput();
    input.setStringData(rawXml);
    LSParser parser = DOM_IMPLEMENTATION_LS.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, XMLConstants.W3C_XML_SCHEMA_NS_URI);
    return parser.parse(input);
  }


  /**
   * Convert an XML node to a string.
   *
   * @param node the XML node
   *
   * @return the string representation
   */
  public static String xmlToString(Node node) {
    try (StringWriter writer = new StringWriter()) {
      Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
      DOMSource source = new DOMSource(node);
      StreamResult result = new StreamResult(writer);
      transformer.transform(source, result);
      return writer.toString();
    } catch (IOException e) {
      throw new InternalError("IOException without I/O", e);
    } catch (TransformerConfigurationException e) {
      throw new InternalError("Transformer configuration exception on default configuration", e);
    } catch (TransformerException e) {
      throw new InternalError("Transformer exception on no-operation transform", e);
    }
  }


  static {
    try {
      DOM_IMPLEMENTATION_LS = (DOMImplementationLS) DOMImplementationRegistry.newInstance().getDOMImplementation("LS");
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      throw new InternalError("Required XML Load-and-Save functionality is not available", e);
    }
  }

}
