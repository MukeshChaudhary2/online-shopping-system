package com.shopping;

import com.shopping.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VerifySampleDataHashesTest {

    @Test
    public void testSampleDataHashes() {
        String adminHashInSql = "$2a$12$PWgd/grZYZh5gnYOJ/1XLOAmaO1MLyyLsj6JTxlWaB/TzHQd/qmHG";
        String customerHashInSql = "$2a$12$pCm0Jv.gREhUOqTqcUJyw.m7x6RPd89ZSMPTFEXBtpxRz9nopaUP.";

        boolean adminMatches = PasswordUtil.checkPassword("Admin@123", adminHashInSql);
        boolean customerMatches = PasswordUtil.checkPassword("Customer@123", customerHashInSql);

        System.out.println("Admin@123 matches adminHashInSql: " + adminMatches);
        System.out.println("Customer@123 matches customerHashInSql: " + customerMatches);

        assertTrue(adminMatches, "Admin@123 should match admin hash in sample_data.sql");
        assertTrue(customerMatches, "Customer@123 should match customer hash in sample_data.sql");
    }
}
