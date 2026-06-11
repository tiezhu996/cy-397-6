package com.contractapi.relation.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("contract_ticket_relations")
public class ContractTicketRelation {
  private Long id;
  private Long contractId;
  private Long ticketId;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getContractId() { return contractId; }
  public void setContractId(Long contractId) { this.contractId = contractId; }
  public Long getTicketId() { return ticketId; }
  public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
}
