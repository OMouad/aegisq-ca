package io.aegisq.crypto;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.StringReader;
import java.io.StringWriter;

/**
 * Small utility class for converting between PEM strings and
 * Bouncy Castle holder objects.
 */
public final class PemUtils {

    private PemUtils() { /* utility class – no instances */ }

    /** Parse a PEM-encoded CSR into a BC PKCS10 object. */
    public static PKCS10CertificationRequest parseCSR(String pem) throws Exception {
        try (PEMParser p = new PEMParser(new StringReader(pem))) {
            return (PKCS10CertificationRequest) p.readObject();
        }
    }

    /** Convert a BC X509CertificateHolder to a PEM string. */
    public static String toPem(X509CertificateHolder holder) throws Exception {
        try (StringWriter sw = new StringWriter();
             JcaPEMWriter w  = new JcaPEMWriter(sw)) {
            w.writeObject(holder);
            w.flush();
            return sw.toString();
        }
    }
}
