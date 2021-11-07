package at.undok.it.cucumber;

import com.github.javafaker.Faker;

public abstract class UndokAbstractFakeData {

    private static final String TEST_MARITAL_STATUS = "Aufgelöste eingetragene Partnerschaft";

    private final Faker faker;

    public UndokAbstractFakeData(Faker faker) {
        this.faker = faker;
    }

    // person:

    protected String firstName() {
        return faker.name().firstName();
    }

    protected String lastName() {
        return faker.name().lastName();
    }

    protected String email() {
        return faker.internet().emailAddress();
    }

    protected String telephone() {
        return faker.phoneNumber().cellPhone();
    }

    protected String dateOfBirth() {
        return "2012-12-23";
    }

    // address:

    protected String street() {
        return faker.address().streetName();
    }

    protected String city() {
        return faker.address().cityName();
    }

    protected String zipCode() {
        return faker.address().zipCode();
    }

    protected String country() {
        return faker.address().country();
    }

    // client:

    protected String keyword() {
        return faker.random().hex(12);
    }

    protected String education() {
     return faker.educator().secondarySchool();
    }

    protected String maritalStatus() {
        return TEST_MARITAL_STATUS;
    }

}
