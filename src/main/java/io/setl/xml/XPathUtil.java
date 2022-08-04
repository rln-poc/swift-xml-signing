package io.setl.xml;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import javax.validation.constraints.NotNull;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XPathFactory, XPath and XPathExpression instances are not thread-safe, and therefore need to be created and compiled afresh for every use or placed in a
 * pool.
 *
 * @author Simon Greatrix on 08/06/2022.
 */
public class XPathUtil {

  /** Cache of pools of X-Path expressions. */
  private static final ConcurrentHashMap<String, BlockingQueue<XPathExpression>> cachedExpressions = new ConcurrentHashMap<>();

  /** The logger. */
  private static final Logger log = System.getLogger(XPathUtil.class.getName());

  /** XPathFactory for creating new XPath instances. */
  private static final XPathFactory xPathFactory = XPathFactory.newInstance();


  /**
   * Find a single node using an X-Path.
   *
   * @param expression the X-Path expression
   * @param node       the node to start searching at.
   *
   * @return the node, or null if it does not exist
   *
   * @throws XMLSignatureException if more than one matching node exists
   */
  public static Node findNode(String expression, Node node) throws XMLSignatureException {
    NodeList list = findNodes(expression, node);
    if (list.getLength() == 0) {
      return null;
    }
    if (list.getLength() > 1) {
      throw new XMLSignatureException("Multiple\"" + expression + "\" nodes were found:" + list.getLength());
    }
    return list.item(0);
  }


  /**
   * Find the nodes that match the expression.
   *
   * @param expression the expression
   * @param node       the node to search from
   *
   * @return the list of nodes
   */
  public static NodeList findNodes(String expression, Node node) {
    try {
      BlockingQueue<XPathExpression> cached = cachedExpressions.computeIfAbsent(expression, k -> new LinkedBlockingQueue<>());
      XPathExpression impl = cached.poll();
      if (impl == null) {
        // create new expression implementation
        synchronized (xPathFactory) {
          XPath xPath = xPathFactory.newXPath();
          xPath.setNamespaceContext(new XmlNamespaceContext());
          impl = xPath.compile(expression);
        }
      }
      try {
        return (NodeList) impl.evaluate(node, XPathConstants.NODESET);
      } finally {
        if (!cached.offer(impl)) {
          // No problem really, we will just create a new one.
          log.log(Level.ERROR, "Unlimited capacity BlockingQueue did not accept offered item");
        }
      }
    } catch (XPathExpressionException e) {
      throw new IllegalArgumentException("Invalid XPath: " + expression, e);
    }
  }


  /**
   * Find a required single node using an X-Path.
   *
   * @param expression the X-Path expression
   * @param node       the node to start searching at
   *
   * @return the node
   *
   * @throws XMLSignatureException if the node is not found, or if more than one exists
   */
  @NotNull
  public static Node findRequiredNode(String expression, Node node) throws XMLSignatureException {
    Node result = findNode(expression, node);
    if (result == null) {
      throw new XMLSignatureException("The \"" + expression + "\" node was not found.");
    }
    return result;
  }


  private XPathUtil() {
    // do nothing
  }

}
