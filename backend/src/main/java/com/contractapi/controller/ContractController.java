package com.contractapi.controller;

import java.util.List;
import java.util.Map;
import com.contractapi.constants.ContractStatus;
import com.contractapi.constants.TicketType;
import com.contractapi.dto.GenerateContractRequest;
import com.contractapi.dto.TicketRequest;
import com.contractapi.entity.Contract;
import com.contractapi.entity.LegalTicket;
import com.contractapi.relation.service.ContractTicketRelationService;
import com.contractapi.service.ContractService;
import com.contractapi.service.TicketService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {
  private final ContractService contractService;
  private final TicketService ticketService;
  private final ContractTicketRelationService relationService;

  public ContractController(ContractService contractService, TicketService ticketService, ContractTicketRelationService relationService) {
    this.contractService = contractService;
    this.ticketService = ticketService;
    this.relationService = relationService;
  }

  @PostMapping("/generate")
  public Contract generate(@RequestBody GenerateContractRequest request) {
    return contractService.generate(request);
  }

  @GetMapping("/{id}")
  public Map<String, Object> detail(@PathVariable Long id) {
    Contract contract = contractService.getById(id);
    List<Long> ticketIds = relationService.findTicketIdsByContractId(id);
    List<LegalTicket> tickets = ticketService.listByIds(ticketIds);
    return Map.of(
      "id", contract.getId(),
      "userId", contract.getUserId(),
      "templateId", contract.getTemplateId(),
      "title", contract.getTitle(),
      "content", contract.getContent(),
      "status", contract.getStatus(),
      "signers", contract.getSigners(),
      "relatedTickets", tickets
    );
  }

  @PatchMapping("/{id}/status")
  public Contract updateStatus(@PathVariable Long id, @RequestParam ContractStatus status) {
    return contractService.updateStatus(id, status);
  }

  @GetMapping
  public List<Contract> list(@RequestParam(required = false) Long userId, @RequestParam(required = false) String status) {
    return contractService.list(userId, status);
  }

  @PostMapping("/{id}/tickets")
  public LegalTicket createTicket(@PathVariable Long id, @RequestBody TicketRequest request) {
    Contract contract = contractService.getById(id);
    String description = request.description();
    if (description == null || description.isEmpty()) {
      description = "合同纠纷：" + contract.getTitle();
    }
    TicketType type = request.type() != null ? request.type() : TicketType.CONTRACT;
    TicketRequest effectiveRequest = new TicketRequest(
      request.userId(),
      type,
      description,
      request.attachments(),
      id
    );
    LegalTicket ticket = ticketService.create(effectiveRequest);
    relationService.relate(id, ticket.getId());
    return ticket;
  }

  @PostMapping("/{id}/pdf")
  public String exportPdf(@PathVariable Long id) {
    return contractService.exportPdf(id);
  }
}
