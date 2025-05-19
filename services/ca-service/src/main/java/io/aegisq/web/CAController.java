package io.aegisq.web;

import io.aegisq.service.CertificateService;
import io.aegisq.web.dto.EnrollRequest;
import io.aegisq.web.dto.EnrollResponse;
import io.aegisq.web.dto.RevokeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ca")
@RequiredArgsConstructor
public class CAController {

    private final CertificateService svc;

    @PostMapping("/enroll")
    public ResponseEntity<EnrollResponse> enroll(@RequestBody EnrollRequest req)  throws Exception{
        String pem = svc.enroll(req.csrPEM());
        return ResponseEntity.ok(new EnrollResponse(pem));
    }

    @PostMapping("/revoke/{serial}")
    public ResponseEntity<Void> revoke(@PathVariable long serial, @RequestBody RevokeRequest req) {
        svc.revoke(serial, req.reason());
        return ResponseEntity.noContent().build();
    }
}
