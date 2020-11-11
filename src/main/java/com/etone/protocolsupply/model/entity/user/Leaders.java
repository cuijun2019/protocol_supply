package com.etone.protocolsupply.model.entity.user;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@Data
@Entity
@Table(name = "LEADERS")
public class Leaders implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private BigInteger id;

  @Column(name = "DWMC", length = 255)
  private String dwmc;

  @Column(name = "LEADER", length = 255)
  private String leader;

  @Column(name = "STATUS", length = 255)
  private String status;



}
