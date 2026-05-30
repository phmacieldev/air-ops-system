package com.air_ops_system.certifications.service;

import com.air_ops_system.certifications.domain.Certification;
import com.air_ops_system.certifications.domain.CertificationType;
import com.air_ops_system.certifications.domain.HolderType;
import com.air_ops_system.certifications.dto.CertificationCreateDTO;
import com.air_ops_system.certifications.dto.CertificationResponseDTO;
import com.air_ops_system.certifications.repository.CertificationRepository;
import com.air_ops_system.pilots.domain.Pilot;
import com.air_ops_system.pilots.repository.PilotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificationService {

  private final CertificationRepository certificationRepository;
  private final PilotRepository pilotRepository;

  public List<CertificationResponseDTO> getAll() {
    return certificationRepository.findByOrderByIssuedAtDesc().stream()
        .map(this::toDTO)
        .toList();
  }

  public CertificationResponseDTO create(CertificationCreateDTO dto) {
    if (dto.holderType() == HolderType.MEMBER &&
        (dto.certificateType() == CertificationType.COPILOT || dto.certificateType() == CertificationType.TRANSPORT)) {
      throw new RuntimeException("COPILOT e TRANSPORT são exclusivos para externos.");
    }

    if (dto.holderType() == HolderType.EXTERNAL &&
        (dto.certificateType() == CertificationType.PURSUIT ||
         dto.certificateType() == CertificationType.OPERATIONAL ||
         dto.certificateType() == CertificationType.SCENE_CONTROL)) {
      throw new RuntimeException("PURSUIT, OPERATIONAL e SCENE_CONTROL são exclusivos para membros.");
    }

    Pilot issuer = pilotRepository.findByUserEmail(dto.issuedByEmail())
        .orElseThrow(() -> new RuntimeException("Emissor não encontrado."));

    Pilot member = null;
    if (dto.holderType() == HolderType.MEMBER) {
      if (dto.memberId() == null) throw new RuntimeException("ID do membro é obrigatório para certificações de membros.");
      member = pilotRepository.findById(dto.memberId())
          .orElseThrow(() -> new RuntimeException("Membro não encontrado."));
    }

    Certification cert = Certification.builder()
        .holderType(dto.holderType())
        .member(member)
        .fullName(dto.fullName())
        .discordId(dto.discordId())
        .externalCallsign(dto.externalCallsign())
        .externalRank(dto.externalRank())
        .externalUnit(dto.externalUnit())
        .certificateType(dto.certificateType())
        .issuedBy(issuer)
        .notes(dto.notes())
        .build();

    return toDTO(certificationRepository.save(cert));
  }

  public void delete(UUID id) {
    Certification cert = certificationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Certificação não encontrada."));
    certificationRepository.delete(cert);
  }

  public List<String> getCertificationTypesByPilot(Pilot pilot) {
    return certificationRepository.findByMember(pilot).stream()
        .map(c -> c.getCertificateType().name())
        .toList();
  }

  private CertificationResponseDTO toDTO(Certification c) {
    return new CertificationResponseDTO(
        c.getId(),
        c.getHolderType().name(),
        c.getMember() != null ? c.getMember().getId() : null,
        c.getMember() != null ? c.getMember().getCallsign() : null,
        c.getMember() != null ? c.getMember().getProfileImageUrl() : null,
        c.getFullName(),
        c.getDiscordId(),
        c.getExternalCallsign(),
        c.getExternalRank(),
        c.getExternalUnit(),
        c.getCertificateType().name(),
        c.getIssuedBy().getCallsign(),
        c.getIssuedAt(),
        c.getNotes()
    );
  }
}
