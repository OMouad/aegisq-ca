package io.aegisq.crypto;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Objects;

@Component
public class CAKeyService {

    private final KeyPair caKeyPair;
    private final X509Certificate caCertificate;

    public CAKeyService() throws Exception {
        Security.addProvider(new BouncyCastleProvider()); // <-- 👈 Register BC
        this.caKeyPair     = loadKeyPair("ca-key.pem");
        this.caCertificate = loadCert("ca-cert.pem");
    }

    public KeyPair getCaKeyPair()     { return caKeyPair; }
    public X509Certificate getCaCert() { return caCertificate; }

    /* ---------- helpers ---------- */
    private KeyPair loadKeyPair(String path) throws Exception {
        try (PEMParser parser = new PEMParser(
                new InputStreamReader(new ClassPathResource(path).getInputStream()))) {

            Object obj = Objects.requireNonNull(parser.readObject(), "Empty key file");
            var converter = new JcaPEMKeyConverter().setProvider("BC");

            if (obj instanceof PEMKeyPair pemKeyPair) {
                return converter.getKeyPair(pemKeyPair);

            } else if (obj instanceof PrivateKeyInfo pkInfo) {
                // In PKCS#8 or PKCS#1 cases
                return new KeyPair(null, converter.getPrivateKey(pkInfo));
            }

            throw new IllegalArgumentException("Unsupported key object: " + obj.getClass());
        }
    }

    private X509Certificate loadCert(String path) throws Exception {
        try (PEMParser p =
                     new PEMParser(new InputStreamReader(
                             new ClassPathResource(path).getInputStream()))) {
            var holder = (X509CertificateHolder) p.readObject();
            return new JcaX509CertificateConverter().getCertificate(holder);
        }
    }
}
