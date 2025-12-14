package at.undok.undok.client.model.entity;

import at.undok.common.model.AbstractCrud;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "persons")
public class Person extends AbstractCrud implements Serializable {

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

//    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "client_id", referencedColumnName = "id")
//    private Client client;

    @OneToOne(mappedBy = "person")
    @JsonIgnore
    private Employer employer;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @Column(name = "contact_data")
    private String contactData;

    @Column(name = "email")
    private String email;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "gender")
    private String gender;

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

//    public Client getClient() {
//        return client;
//    }
//
//    public void setClient(Client client) {
//        this.client = client;
//    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getContactData() {
        return contactData;
    }

    public void setContactData(String contactData) {
        this.contactData = contactData;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
