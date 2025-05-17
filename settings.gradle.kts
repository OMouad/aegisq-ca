rootProject.name = "aegisq-ca"
include("libs:common-model",
        "services:auth-server",
        "services:ca-service",
        "services:ocsp-service",
        "services:audit-service",
        "services:gateway")
