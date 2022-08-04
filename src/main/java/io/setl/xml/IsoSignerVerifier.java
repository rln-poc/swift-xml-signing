package io.setl.xml;

import static javax.xml.crypto.dsig.CanonicalizationMethod.EXCLUSIVE;

import java.io.ByteArrayInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Signing and verification on the ISO-20022 AppHdr and ISO-20022 Document elements.
 *
 * @author Simon Greatrix on 27/05/2022.
 */
public class IsoSignerVerifier {

  /** X-Path to locate the "Sgntr" signature envelope within the business header. */
  private static final String XPATH_SIGNATURE_ENV = "/head:AppHdr/head:Sgntr";

  /** X-Path to locate the "Signature" node within the signature envelope of the business header. */
  private static final String XPATH_SIGNATURE_NODE = "/head:AppHdr/head:Sgntr/sign:Signature";

  /** X-Path to locate the X.509 certificate within the "Signature" node of the signature envelope. */
  private static final String XPATH_X509_NODE = "/head:AppHdr/head:Sgntr/sign:Signature/sign:KeyInfo/sign:X509Data/sign:X509Certificate";

  /** Factory for deserializing X.509 certificates. */
  private static final CertificateFactory factory;


  static {
    try {
      factory = CertificateFactory.getInstance("X.509");
    } catch (CertificateException e) {
      // Support for X.509 certificates is required.
      throw new ExceptionInInitializerError(e);
    }
  }

  /** XML Signature factory for performing siging and validation. */
  private final XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");

  /** Generate KeyInfo elements. */
  private final KeyInfoFactory keyInfoFactory = xmlSignatureFactory.getKeyInfoFactory();


  /**
   * Create an XML Signature reference to the AppHdr root.
   *
   * @return the reference
   */
  private Reference appHdr() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    return xmlSignatureFactory.newReference("", xmlSignatureFactory.newDigestMethod(DigestMethod.SHA256, null),
        List.of(
            xmlSignatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null),
            xmlSignatureFactory.newTransform(EXCLUSIVE, (XMLStructure) null)
        ),
        null, null
    );
  }


  /**
   * Create an XML Signature reference to the associated document. This is handled as a special case where the identifying URI is unspecified.
   *
   * @return the reference
   */
  private Reference document() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    return xmlSignatureFactory.newReference(null, xmlSignatureFactory.newDigestMethod(DigestMethod.SHA256, null),
        List.of(
            xmlSignatureFactory.newTransform(EXCLUSIVE, (TransformParameterSpec) null)
        ),
        null, null
    );
  }


  /**
   * Retrieve an X.509 certificate from the AppHdr signature envelope
   *
   * @param appHdr the AppHdr root node
   *
   * @return the certificate
   */
  private X509Certificate getPublicCertFromDocument(Node appHdr) throws XMLSignatureException {
    Node x509Node = XPathUtil.findRequiredNode(XPATH_X509_NODE, appHdr);
    String text = x509Node.getTextContent();
    try {
      byte[] bytes = Base64.getMimeDecoder().decode(text);
      return (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(bytes));
    } catch (IllegalArgumentException | CertificateException e) {
      throw new XMLSignatureException("Invalid X.509 certificate", e);
    }
  }


  private Reference keyInfo() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    return xmlSignatureFactory.newReference(
        "#KeyInfo-" + UUID.randomUUID(),
        xmlSignatureFactory.newDigestMethod(DigestMethod.SHA256, null),
        List.of(xmlSignatureFactory.newTransform(EXCLUSIVE, (TransformParameterSpec) null)), null, null
    );
  }


  /**
   * Find or create an empty &lt;Sgntr&gt node in the &lt;AppHdr&gt; node. It is required that the &lt;AppHdr&gt; conforms to the appropriate schema.
   *
   * @param appHdr the XML envelope node
   *
   * @return the new or located Sgntr node
   */
  private Node makeSignatureEnvelope(Node appHdr) throws XMLSignatureException {
    // Remove all existing <Signature> nodes
    NodeList signatureList = XPathUtil.findNodes(XPATH_SIGNATURE_NODE, appHdr);
    for (int i = signatureList.getLength() - 1; i >= 0; i--) {
      Node c = signatureList.item(i);
      c.getParentNode().removeChild(c);
    }

    Node node = XPathUtil.findNode(XPATH_SIGNATURE_ENV, appHdr);
    if (node != null) {
      // Found existing "Sgntr" node.
      return node;
    }

    // The Sgntr node is near the end of the header. The only thing that comes after it are "Rltd" nodes.
    Node previous = null;
    node = appHdr.getLastChild();
    while (node.getNodeType() != Node.ELEMENT_NODE || "Rltd".equals(node.getLocalName())) {
      previous = node;
      node = node.getPreviousSibling();
    }

    // Create and insert the node. We are relying on the fact that the <AppHdr> node must contain elements before the <Sgntr> node.
    Element sgntr = node.getOwnerDocument().createElementNS(node.getNamespaceURI(), node.getPrefix() + ":" + Constants.ISO_SIGNATURE_NODE);
    appHdr.insertBefore(sgntr, previous);
    return sgntr;
  }


  /**
   * Sign a &lt;Message&gt; node that envelopes an ISO-20022 AppHdr and Document pair.
   *
   * @param header          the business header node
   * @param document        the document
   * @param privateKey      the private key to sign with
   * @param x509Certificate the certificate to include in the header
   * @param signatureMethod the chosen signature method (must be appropriate to the key type)
   *
   * @return the header, but signed
   */
  public Node sign(Node header, Node document, PrivateKey privateKey, X509Certificate x509Certificate, XMLSignatureMethod signatureMethod)
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, MarshalException, XMLSignatureException {
    // Normalize the XML documents prior to processing.
    header.normalize();
    document.normalize();

    // Define what we are signing
    Reference keyInfoReference = keyInfo();
    SignedInfo si = xmlSignatureFactory.newSignedInfo(
        xmlSignatureFactory.newCanonicalizationMethod(EXCLUSIVE, (C14NMethodParameterSpec) null),
        xmlSignatureFactory.newSignatureMethod(signatureMethod.getUri(), null),
        List.of(keyInfoReference, appHdr(), document())
    );

    X509Data xd = keyInfoFactory.newX509Data(List.of(x509Certificate));
    KeyInfo keyInfo = keyInfoFactory.newKeyInfo(List.of(xd), keyInfoReference.getURI().substring(1));

    IsoUriDereferencer temp = new IsoUriDereferencer(keyInfoFactory.getURIDereferencer(), header, document);
    Node sgntrNode = makeSignatureEnvelope(header);
    DOMSignContext dsc = new DOMSignContext(privateKey, sgntrNode);
    dsc.setDefaultNamespacePrefix("sign");
    dsc.setURIDereferencer(temp);
    XMLSignature signature = xmlSignatureFactory.newXMLSignature(si, keyInfo);

    signature.sign(dsc);

    return header;
  }


  /**
   * Validate the signature of a business header and document.
   *
   * @param header   the business header node
   * @param document the document
   *
   * @return true if the signature is valid
   */
  public boolean validate(Node header, Node document) throws MarshalException, XMLSignatureException {
    // Extract the certificate from the input document.
    X509Certificate x509Certificate = getPublicCertFromDocument(header);

    Node signatureNode = XPathUtil.findRequiredNode(XPATH_SIGNATURE_NODE, header);
    DOMValidateContext valContext = new DOMValidateContext(x509Certificate.getPublicKey(), signatureNode);
    valContext.setProperty("org.jcp.xml.dsig.secureValidation", Boolean.TRUE);

    IsoUriDereferencer noUri = new IsoUriDereferencer(keyInfoFactory.getURIDereferencer(), header, document);
    valContext.setURIDereferencer(noUri);

    XMLSignature signature = xmlSignatureFactory.unmarshalXMLSignature(valContext);

    // Return false if either of the 3 references fail. Else continue to signature validation
    for (Reference reference : signature.getSignedInfo().getReferences()) {
      boolean refValid = reference.validate(valContext);
      if (!refValid) {
        return false;
      }
    }

    return signature.validate(valContext);
  }

}
