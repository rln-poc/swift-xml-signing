package io.setl.xml;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;

/**
 * Simple namespace context resolver for X-Path. The namespace mappings are fixed.
 *
 * <pre>
 *   xmlns:head="urn:iso:std:iso:20022:tech:xsd:head.001.001.03"
 *   xmlns:sign="http://www.w3.org/2000/09/xmldsig#"
 * </pre>
 *
 * @author Simon Greatrix on 08/06/2022.
 */
public class XmlNamespaceContext implements NamespaceContext {

  private final Map<String, String> prefixesForUri = Map.of(
      Constants.NS_ISO_HEAD, "head",
      Constants.NS_SIGNATURE, "sign"
  );

  private final Map<String, String> uriForPrefix = Map.of(
      "head", Constants.NS_ISO_HEAD,
      "sign", Constants.NS_SIGNATURE
  );


  @Override
  public String getNamespaceURI(String prefix) {
    return uriForPrefix.get(prefix);
  }


  @Override
  public String getPrefix(String namespaceURI) {
    return prefixesForUri.get(namespaceURI);
  }


  @Override
  public Iterator<String> getPrefixes(String namespaceURI) {
    return List.of(getPrefix(namespaceURI)).iterator();
  }

}
