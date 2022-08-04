# swift-xml-signing

Example code that shows how the signatures for SWIFT Business Application Headers are generated and validated.

The signature validation does not include validating the X.509 certificates.

## Examples

The class `ExampleSigning` loads an application header and document, an X.509 certificate and associated private key, and combines these to produce a signed AppHdr.

The class `ExampleVerify1` reads an AppHdr and Document XML structures an validates the signature is correct.

The class `ExampleVerify2` reads the same AppHdr and Document XML structures as `ExampleVerify1` except that the AppHdr has been passed through a pretty-printer. This changes the whitespace in the AppHdr and breaks its signature.
