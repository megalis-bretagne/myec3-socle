package org.myec3.socle.core.domain.model.validation;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.myec3.socle.core.domain.model.Address;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.util.Set;

public class AddressValidationTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidPostalAddress() {

        Address address = new Address();

        // Not null constraint
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateProperty(address, "postalAddress",
                Default.class);
        Assert.assertEquals("bean postalAddress property should be invalid : postalAddress cannot be null", 1,
                constraintViolations.size());

        // valid postalAddress
        address.setPostalAddress("test address");
        constraintViolations = validator.validateProperty(address, "postalAddress", Default.class);
        Assert.assertEquals("bean postalAddress property should be valid with value : " + address.getPostalAddress(), 0,
                constraintViolations.size());
    }

    @Test
    public void testValidPostalCode() throws Exception {

        Address address = new Address();

        // Not null constraint
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateProperty(address, "postalCode",
                Default.class);
        Assert.assertEquals("bean postalCode property should be invalid : postalCode cannot be null", 1, constraintViolations
                .size());

        // valid postalCode
        address.setPostalCode("test code");
        constraintViolations = validator.validateProperty(address, "postalCode", Default.class);
        Assert.assertEquals("bean postalCode property should be valid with value : " + address.getPostalCode(), 0,
                constraintViolations.size());

    }

    @Test
    public void testValidCity() {

        Address address = new Address();

        // Not null constraint
        Set<ConstraintViolation<Address>> constraintViolations = validator.validateProperty(address, "postalAddress",
                Default.class);
        Assert.assertEquals("bean city property should be invalid : city cannot be null", 1, constraintViolations.size());

        // valid postalAddress
        address.setCity("test city");
        constraintViolations = validator.validateProperty(address, "city", Default.class);
        Assert.assertEquals("bean city property should be valid with value : " + address.getCity(), 0, constraintViolations
                .size());

    }

    @Test
    public void testValidAddress() {

        Address address = new Address();

        // Not null constraint
        Set<ConstraintViolation<Address>> constraintViolations = validator.validate(address, Default.class);
        Assert.assertEquals("bean adress should be invalid : postalAddress, postalCode and city cannot be null", 3,
                constraintViolations.size());

        // valid postalAddress
        address.setPostalAddress("test address");
        address.setPostalCode("test code");
        address.setCity("test city");
        constraintViolations = validator.validate(address, Default.class);
        Assert.assertEquals("bean address should be valid with value : " + address, 0, constraintViolations.size());
    }
}
