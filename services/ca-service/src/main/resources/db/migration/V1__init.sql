/* ************  V1__init.sql  ************ */

/* Certificates table
   ==================
   Stores every X.509 certificate we issue.
*/
CREATE TABLE certificate (
                             serial          BIGINT PRIMARY KEY,
                             subject_dn      VARCHAR(255) NOT NULL,               -- full DN for audit
                             not_before      TIMESTAMP    NOT NULL,
                             not_after       TIMESTAMP    NOT NULL,
                             status          VARCHAR(20)  NOT NULL,               -- ISSUED | REVOKED
                             pem             TEXT         NOT NULL,               -- PEM-encoded cert
                             kid             VARCHAR(64)  NOT NULL,               -- key identifier
                             issued_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

/* Fast look-ups for OCSP or portal screens */
CREATE INDEX idx_certificate_status       ON certificate(status);
CREATE INDEX idx_certificate_subject_dn   ON certificate(subject_dn);

/* Revocation events
   =================
   One certificate may have multiple revocation events (re-key, mistake, etc.).
*/
CREATE TABLE revocation (
                            id             BIGSERIAL PRIMARY KEY,
                            cert_serial    BIGINT       NOT NULL REFERENCES certificate(serial),
                            reason         VARCHAR(50)  NOT NULL,
                            revoked_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

/* Helpful compound index when building a CRL */
CREATE INDEX idx_revocation_cert_reason ON revocation(cert_serial, reason);
