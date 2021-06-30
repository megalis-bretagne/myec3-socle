package org.myec3.socle.core.service;

import org.junit.Test;
import org.myec3.socle.AbstractDbSocleUnitTest;
import org.myec3.socle.core.domain.model.Application;
import org.myec3.socle.core.domain.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import javax.transaction.Transactional;

import static org.junit.Assert.*;

@Transactional
@SqlGroup({
        // WITH CUSTOMER
        @Sql(value = {"classpath:/db/test/initData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
})
public class CustomerServiceTest extends AbstractDbSocleUnitTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ApplicationService applicationService;

    @Test
    public void testCreate() {
        Customer newCustomer = new Customer();
        newCustomer.setName("AA");
        newCustomer.setLabel("label");
        newCustomer.setEmail("email@test.fr");
        assertEquals(Boolean.FALSE, newCustomer.isAuthorizedToManageCompanies());

        // DO
        this.customerService.create(newCustomer);
        Customer created = this.customerService.findByName(newCustomer.getName());
        assertNotNull(created);
        assertEquals(newCustomer.getName(), created.getName());
        assertEquals(newCustomer.getEmail(), created.getEmail());

    }

    @Test
    public void testUpdate() {
        Customer foundCustomer = this.customerService
                .findByName("MB");
        assertEquals(1, foundCustomer.getApplications().size());
        assertEquals("Mégalis Bretagne", foundCustomer.getLabel());

        Application foundApplication = this.applicationService
                .findByName("GU");
        assertNotNull(foundApplication);

        String newCustomerLabel = "new label";
        foundCustomer.setLabel(newCustomerLabel);
        foundCustomer.removeApplication(foundApplication);
        foundCustomer = this.customerService.update(foundCustomer);
        assertEquals(newCustomerLabel, foundCustomer.getLabel());
        assertEquals(0, foundCustomer.getApplications().size());
    }

    @Test
    public void testDelete() {
        Customer newCustomer = new Customer();
        newCustomer.setLabel("test");
        newCustomer.setName("test");
        newCustomer.setEmail("test@test.fr");
        newCustomer.setAuthorizedToManageCompanies(Boolean.FALSE);
        customerService.create(newCustomer);

        Customer created = customerService.findByName(newCustomer.getName());

        this.customerService.delete(created);
        Customer foundCustomer = this.customerService.findByName(newCustomer.getName());
        assertNull(foundCustomer);
    }
}
