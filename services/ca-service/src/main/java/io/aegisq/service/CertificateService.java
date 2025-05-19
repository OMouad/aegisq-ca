package io.aegisq.service;

import io.aegisq.crypto.CAKeyService;
import io.aegisq.crypto.PemUtils;
import io.aegisq.domain.Certificate;
import io.aegisq.domain.CertificateStatus;
import io.aegisq.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository repo;
    private final CAKeyService caKeys;
    private final SecureRandom rnd = new SecureRandom();

    @Transactional
    public String enroll(String csrPEM) throws Exception {

        var csrHolder = PemUtils.parseCSR(csrPEM);
        X500Name subject = csrHolder.getSubject();

        long serial = Math.abs(rnd.nextLong());

        var now = OffsetDateTime.now();
        var later = now.plusYears(1);

        var certBldr = new JcaX509v3CertificateBuilder(
                new X500Name(caKeys.getCaCert().getSubjectX500Principal().getName()), // issuer = our CA DN
                BigInteger.valueOf(serial),
                Timestamp.valueOf(now.toLocalDateTime()),
                Timestamp.valueOf(later.toLocalDateTime()),
                subject,
                csrHolder.getSubjectPublicKeyInfo());

        var signer = new JcaContentSignerBuilder("SHA256withRSA")
                .build(caKeys.getCaKeyPair().getPrivate());

        X509CertificateHolder holder = certBldr.build(signer);
        String pemCert = PemUtils.toPem(holder);

        Certificate cert = new Certificate();
        cert.setSerial(serial);
        cert.setSubjectDn(subject.toString());
        cert.setNotBefore(now);
        cert.setNotAfter(later);
        cert.setPem(pemCert);

        byte[] keyBytes = holder.getSubjectPublicKeyInfo().getPublicKeyData().getBytes();
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] thumbprint = digest.digest(keyBytes);
        String kid = Base64.getUrlEncoder().withoutPadding().encodeToString(thumbprint);

        cert.setKid(kid);

        repo.save(cert);
        return pemCert;
    }

    @Transactional
    public void revoke(long serial, String reason) {
        repo.findById(serial).ifPresent(c -> {
            c.setStatus(CertificateStatus.REVOKED);
            repo.save(c);
        });
        // TODO: insert RevocationEvent via RevocationRepository
    }
}