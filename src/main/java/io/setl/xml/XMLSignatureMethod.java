package io.setl.xml;

/**
 * Possible types of cryptographic signature.
 *
 * @author Simon Greatrix on 07/06/2022.
 */
public enum XMLSignatureMethod {

  /**
   * The <a href="http://www.w3.org/2000/09/xmldsig#dsa-sha1">DSA-SHA1</a>
   * (DSS) signature method algorithm URI.
   */
  DSA_SHA1("http://www.w3.org/2000/09/xmldsig#dsa-sha1"),

  /**
   * The <a href="http://www.w3.org/2009/xmldsig11#dsa-sha256">DSA-SHA256</a>
   * (DSS) signature method algorithm URI.
   *
   * @since 11
   */
  DSA_SHA256("http://www.w3.org/2009/xmldsig11#dsa-sha256"),

  /**
   * The <a href="http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1">
   * ECDSA-SHA1</a> (FIPS 180-4) signature method algorithm URI.
   *
   * @since 11
   */
  ECDSA_SHA1("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1"),

  /**
   * The <a href="http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224">
   * ECDSA-SHA224</a> (FIPS 180-4) signature method algorithm URI.
   *
   * @since 11
   */
  ECDSA_SHA224("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224"),

  /**
   * The <a href="http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256">
   * ECDSA-SHA256</a> (FIPS 180-4) signature method algorithm URI.
   *
   * @since 11
   */
  ECDSA_SHA256("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"),

  /**
   * The <a href="http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384">
   * ECDSA-SHA384</a> (FIPS 180-4) signature method algorithm URI.
   *
   * @since 11
   */
  ECDSA_SHA384("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384"),

  /**
   * The <a href="http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512">
   * ECDSA-SHA512</a> (FIPS 180-4) signature method algorithm URI.
   *
   * @since 11
   */
  ECDSA_SHA512("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512"),

  /**
   * The <a href="http://www.w3.org/2000/09/xmldsig#hmac-sha1">HMAC-SHA1</a>
   * MAC signature method algorithm URI.
   */
  HMAC_SHA1("http://www.w3.org/2000/09/xmldsig#hmac-sha1"),

  /**
   * The <a href="http://www.w3.org/2001/04/xmldsig-more#hmac-sha224">
   * HMAC-SHA224</a> MAC signature method algorithm URI.
   *
   * @since 11
   */
  HMAC_SHA224("http://www.w3.org/2001/04/xmldsig-more#hmac-sha224"),

  /**
   * The <a href="http://www.w3.org/2001/04/xmldsig-more#hmac-sha256">
   * HMAC-SHA256</a> MAC signature method algorithm URI.
   *
   * @since 11
   */
  HMAC_SHA256("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256"),

  /**
   * The <a href="http://www.w3.org/2001/04/xmldsig-more#hmac-sha384">
   * HMAC-SHA384</a> MAC signature method algorithm URI.
   *
   * @since 11
   */
  HMAC_SHA384("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384"),

  /**
   * The <a href="http://www.w3.org/2001/04/xmldsig-more#hmac-sha512">
   * HMAC-SHA512</a> MAC signature method algorithm URI.
   *
   * @since 11
   */
  HMAC_SHA512("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512"),

  /**
   * The <a href="http://www.w3.org/2000/09/xmldsig#rsa-sha1">RSA-SHA1</a>
   * (PKCS #1) signature method algorithm URI.
   */
  RSA_SHA1("http://www.w3.org/2000/09/xmldsig#rsa-sha1"),

  /**
   * The <a href="http://www.w3.org/2001/04/xmldsig-more#rsa-sha224">
   * RSA-SHA224</a> (PKCS #1) signature method algorithm URI.
   *
   * @since 11
   */
  RSA_SHA224("http://www.w3.org/2001/04/xmldsig-more#rsa-sha224"),

  /**
   * The <a href="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256">
   * RSA-SHA256</a> (PKCS #1) signature method algorithm URI.
   *
   * @since 11
   */
  RSA_SHA256("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"),

  /**
   * The <a href="http://www.w3.org/2001/04/xmldsig-more#rsa-sha384">
   * RSA-SHA384</a> (PKCS #1) signature method algorithm URI.
   *
   * @since 11
   */
  RSA_SHA384("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384"),

  /**
   * The <a href="http://www.w3.org/2001/04/xmldsig-more#rsa-sha512">
   * RSA-SHA512</a> (PKCS #1) signature method algorithm URI.
   *
   * @since 11
   */
  RSA_SHA512("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"),

  /**
   * The <a href="http://www.w3.org/2007/05/xmldsig-more#sha1-rsa-MGF1">
   * SHA1-RSA-MGF1</a> (PKCS #1) signature method algorithm URI.
   *
   * @since 11
   */
  SHA1_RSA_MGF1("http://www.w3.org/2007/05/xmldsig-more#sha1-rsa-MGF1"),

  /**
   * The <a href="http://www.w3.org/2007/05/xmldsig-more#sha224-rsa-MGF1">
   * SHA224-RSA-MGF1</a> (PKCS #1) signature method algorithm URI.
   *
   * @since 11
   */
  SHA224_RSA_MGF1("http://www.w3.org/2007/05/xmldsig-more#sha224-rsa-MGF1"),

  /**
   * The <a href="http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1">
   * SHA256-RSA-MGF1</a> (PKCS #1) signature method algorithm URI.
   *
   * @since 11
   */
  SHA256_RSA_MGF1("http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1"),

  /**
   * The <a href="http://www.w3.org/2007/05/xmldsig-more#sha384-rsa-MGF1">
   * SHA384-RSA-MGF1</a> (PKCS #1) signature method algorithm URI.
   *
   * @since 11
   */
  SHA384_RSA_MGF1("http://www.w3.org/2007/05/xmldsig-more#sha384-rsa-MGF1"),

  /**
   * The <a href="http://www.w3.org/2007/05/xmldsig-more#sha512-rsa-MGF1">
   * SHA512-RSA-MGF1</a> (PKCS #1) signature method algorithm URI.
   *
   * @since 11
   */
  SHA512_RSA_MGF1("http://www.w3.org/2007/05/xmldsig-more#sha512-rsa-MGF1");

  private final String uri;


  XMLSignatureMethod(String uri) {
    this.uri = uri;
  }


  public String getUri() {
    return uri;
  }
}
