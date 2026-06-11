package com.contractapi.relation.service;

import java.util.ArrayList;
import java.util.List;
import com.contractapi.relation.entity.ContractTicketRelation;
import org.springframework.stereotype.Service;

@Service
public class ContractTicketRelationService {
  private final List<ContractTicketRelation> relations = new ArrayList<>();

  public void relate(Long contractId, Long ticketId) {
    if (isRelated(contractId, ticketId)) {
      return;
    }
    ContractTicketRelation relation = new ContractTicketRelation();
    relation.setId(System.currentTimeMillis());
    relation.setContractId(contractId);
    relation.setTicketId(ticketId);
    relations.add(relation);
  }

  public boolean isRelated(Long contractId, Long ticketId) {
    return relations.stream()
      .anyMatch(r -> r.getContractId().equals(contractId) && r.getTicketId().equals(ticketId));
  }

  public List<Long> findTicketIdsByContractId(Long contractId) {
    return relations.stream()
      .filter(r -> r.getContractId().equals(contractId))
      .map(ContractTicketRelation::getTicketId)
      .toList();
  }

  public List<Long> findContractIdsByTicketId(Long ticketId) {
    return relations.stream()
      .filter(r -> r.getTicketId().equals(ticketId))
      .map(ContractTicketRelation::getContractId)
      .toList();
  }

  public void unrelate(Long contractId, Long ticketId) {
    relations.removeIf(r -> r.getContractId().equals(contractId) && r.getTicketId().equals(ticketId));
  }
}
