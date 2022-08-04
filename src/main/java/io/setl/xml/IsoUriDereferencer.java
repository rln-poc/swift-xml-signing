package io.setl.xml;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import javax.xml.crypto.Data;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.XMLCryptoContext;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * De-referencer for the special URIs used by the ISO-20022 AppHdr signature element. A missing URI refers to a separate ISO-20022 Document which has no URI,
 * and an empty URI refers to the AppHdr itself.
 *
 * @author Simon Greatrix on 27/05/2022.
 */
public class IsoUriDereferencer implements URIDereferencer {

  /**
   * An iterator across an XML document fragment that returns the elements and attributes in the appropriate order for a URI Dereferencer.
   */
  static class Itr implements Iterator<Node> {

    final LinkedList<Node> stack = new LinkedList<>();

    private Node nextNode;


    Itr(Node root) {
      stack.addFirst(root);
      nextNode = null;
    }


    private void getNext() {
      if (nextNode != null || stack.isEmpty()) {
        return;
      }
      Node n = stack.removeFirst();
      if (n.hasChildNodes()) {
        Node c = n.getLastChild();
        while (c != null) {
          stack.addFirst(c);
          c = c.getPreviousSibling();
        }
      }
      if (n.hasAttributes()) {
        NamedNodeMap attrs = n.getAttributes();
        for (int i = attrs.getLength() - 1; i >= 0; i--) {
          stack.addFirst(attrs.item(i));
        }
      }
      nextNode = n;
    }


    @Override
    public boolean hasNext() {
      getNext();
      return nextNode != null;
    }


    @Override
    public Node next() {
      getNext();
      if (nextNode == null) {
        throw new NoSuchElementException();
      }
      Node n = nextNode;
      nextNode = null;
      return n;
    }

  }



  /** A "real" dereferencer that can dereference standard URIs. */
  private final URIDereferencer dereferencer;

  /** The SWIFT Document element which is matched by a null URI. */
  private final Node document;

  /** The SWIFT Business Header which is matched by a URI of "". */
  private final Node header;


  /**
   * New instance.
   *
   * @param dereferencer a real de-referencer
   * @param header       the header node identified by URI=""
   * @param document     the document node identified by a null URI
   */
  public IsoUriDereferencer(URIDereferencer dereferencer, Node header, Node document) {
    this.dereferencer = dereferencer;
    this.header = header;
    this.document = document;
  }


  /**
   * {@inheritDoc}
   */
  public Data dereference(URIReference ref, XMLCryptoContext ctxt) throws URIReferenceException {
    if (ref != null) {
      String uri = ref.getURI();
      if (uri != null) {
        if (uri.isEmpty()) {
          // The "root" URI which is the header node
          return (NodeSetData<Node>) () -> new Itr(header);
        }

        // A real URI, so use the default de-referencer.
        return dereferencer.dereference(ref, ctxt);
      }
    }

    // null reference or null URI
    return (NodeSetData<Node>) () -> new Itr(document);
  }

}
