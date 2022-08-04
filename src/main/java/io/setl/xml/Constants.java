package io.setl.xml;

/**
 * Message related constants.
 *
 * @author Simon Greatrix on 07/06/2022.
 */
public class Constants {

  /** The name of the signature envelope element in the business application header. */
  public static final String ISO_SIGNATURE_NODE = "Sgntr";

  /** Namespace for the ISO-20022 HEAD message. */
  public static final String NS_ISO_HEAD = "urn:iso:std:iso:20022:tech:xsd:head.001.001.03";

  /** Namespace for digital signatures. */
  public static final String NS_SIGNATURE = "http://www.w3.org/2000/09/xmldsig#";


  private Constants() {
    // do nothing
  }

}
