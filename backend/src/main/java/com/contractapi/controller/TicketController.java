package com.contractapi.controller;

import java.util.List;
import java.util.Map;
import com.contractapi.constants.TicketStatus;
import com.contractapi.dto.TicketRequest;
import com.contractapi.entity.Contract;
import com.contractapi.entity.LegalTicket;
import com.contractapi.relation.service.ContractTicketRelationService;
import com.contractapi.service.ContractService;
import com.contractapi.service.TicketService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
  private final TicketService ticketService;
  private final ContractService contractService;
  private final ContractTicketRelationService relationService;

  public TicketController(TicketService ticketService, ContractService contractService, ContractTicketRelationService relationService) {
    this.ticketService = ticketService;
    this.contractService = contractService;
    this.relationService = relationService;
  }

  @PostMapping
  public LegalTicket create(@RequestBody TicketRequest request) {
    if (request.contractId() != null) {
      Contract contract = contractService.getById(request.contractId());
      LegalTicket ticket = ticketService.create(request);
      relationService.relate(contract.getId(), ticket.getId());
      return ticket;
    }
    return ticketService.create(request);
  }

  @GetMapping("/{id}")
  public Map<String, Object> detail(@PathVariable Long id) {
    LegalTicket ticket = ticketService.getById(id);
    List<Long> contractIds = relationService.findContractIdsByTicketId(id);
    List<Contract> contracts = contractIds.stream()
      .map(contractService::getById)
      .toList();
    return Map.of(
      "id", ticket.getId(),
      "userId", ticket.getUserId(),
      "type", ticket.getType(),
      "description", ticket.getDescription(),
      "status", ticket.getStatus(),
      "attachments", ticket.getAttachments(),
      "relatedContracts", contracts
    );
  }

  @PatchMapping("/{id}/status")
  public LegalTicket status(@PathVariable Long id, @RequestParam TicketStatus status) {
    return ticketService.updateStatus(id, status);
  }

  @PostMapping("/{id}/replies")
  public Map<String, Object> reply(@PathVariable Long id, @RequestBody Map<String, Object> body) {
    return ticketService.reply(id, String.valueOf(body.get("content")), (List<String>) body.getOrDefault("attachments", List.of()));
  }
}
