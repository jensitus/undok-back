package at.undok.auth.model.entity;

import lombok.Data;
import org.hibernate.annotations.NaturalId;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "roles")
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @Enumerated(EnumType.STRING)
  @NaturalId
  @Column(name = "name")
  private RoleName name;

  public Role() {
  }

  public Role(RoleName name) {
    this.name = name;
  }

}
