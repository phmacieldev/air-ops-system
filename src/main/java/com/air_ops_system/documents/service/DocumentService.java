package com.air_ops_system.documents.service;

import com.air_ops_system.documents.domain.Document;
import com.air_ops_system.documents.dto.DocumentCreateDTO;
import com.air_ops_system.documents.dto.DocumentResponseDTO;
import com.air_ops_system.documents.dto.DocumentUpdateDTO;
import com.air_ops_system.documents.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

  private final DocumentRepository documentRepository;

  public List<DocumentResponseDTO> getAllDocuments() {
    return documentRepository.findAll().stream()
        .map(this::toDTO)
        .toList();
  }

  public DocumentResponseDTO createDocument(DocumentCreateDTO dto) {
    Document document = Document.builder()
        .title(dto.title())
        .url(dto.url())
        .category(dto.category())
        .build();

    return toDTO(documentRepository.save(document));
  }

  public DocumentResponseDTO updateDocument(UUID id, DocumentUpdateDTO dto) {
    Document document = documentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Documento não encontrado."));

    if (dto.title() != null) document.setTitle(dto.title());
    if (dto.url() != null) document.setUrl(dto.url());
    if (dto.category() != null) document.setCategory(dto.category());

    return toDTO(documentRepository.save(document));
  }

  public void deleteDocument(UUID id) {
    Document document = documentRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Documento não encontrado."));
    documentRepository.delete(document);
  }

  private DocumentResponseDTO toDTO(Document d) {
    return new DocumentResponseDTO(d.getId(), d.getTitle(), d.getUrl(), d.getCategory(), d.getUpdatedAt());
  }
}
