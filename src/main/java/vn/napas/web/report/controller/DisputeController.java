package vn.napas.web.report.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.napas.web.report.dto.EcomSearchForDisputeRequest;
import vn.napas.web.report.service.DisputeService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dispute")
public class DisputeController {
    private final DisputeService service;

    @PostMapping("/search")
    public ResponseEntity<List<Map<String,Object>>> search(@Valid @RequestBody EcomSearchForDisputeRequest req) {
        return ResponseEntity.ok(service.ecomSearchForDispute(req));
    }
}
