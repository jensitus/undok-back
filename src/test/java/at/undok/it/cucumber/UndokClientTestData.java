package at.undok.it.cucumber;

import at.undok.undok.client.model.form.ClientForm;
import com.github.javafaker.Faker;

public class UndokClientTestData extends UndokAbstractFakeData {


    public UndokClientTestData(Faker faker) {
        super(faker);
    }

    public ClientForm createClientForm() {
        var newClient = new ClientForm();
        newClient.setFirstName(firstName());
        newClient.setLastName(lastName());
        newClient.setEmail(email());
        newClient.setDateOfBirth(dateOfBirth());
        newClient.setTelephone(telephone());
        newClient.setStreet(street());
        newClient.setCity(city());
        newClient.setZipCode(zipCode());
        newClient.setCountry(country());
        newClient.setEducation(education());
        newClient.setKeyword(keyword());
        newClient.setMaritalStatus(maritalStatus());

        return newClient;
    }

}
