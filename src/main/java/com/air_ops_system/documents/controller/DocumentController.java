package com.air_ops_system.documents.controller;

import com.air_ops_system.documents.dto.DocumentCreateDTO;
import com.air_ops_system.documents.dto.DocumentResponseDTO;
import com.air_ops_system.documents.dto.DocumentUpdateDTO;
import com.air_ops_system.documents.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class DocumentController {

  private final DocumentService documentService;

  @GetMapping
  public ResponseEntity<List<DocumentResponseDTO>> getAllDocuments() {
    return ResponseEntity.ok(documentService.getAllDocuments());
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('LEAD', 'ADM')")
  public ResponseEntity<DocumentResponseDTO> createDocument(@RequestBody @Valid DocumentCreateDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(documentService.createDocument(dto));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('LEAD', 'ADM')")
  public ResponseEntity<DocumentResponseDTO> updateDocument(@PathVariable UUID id,
                                                            @RequestBody DocumentUpdateDTO dto) {
    return ResponseEntity.ok(documentService.updateDocument(id, dto));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('LEAD', 'ADM')")
  public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
    documentService.deleteDocument(id);
    return ResponseEntity.noContent().build();
  }
}
