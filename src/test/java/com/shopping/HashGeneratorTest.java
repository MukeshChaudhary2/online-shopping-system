package com.shopping;

import com.shopping.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HashGeneratorTest {

    @Test
    public void generateHashes() {
        String adminHash = PasswordUtil.hashPassword("Admin@123");
        String customerHash = PasswordUtil.hashPassword("Customer@123");

        System.out.println("ADMIN_HASH: " + adminHash);
        System.out.println("CUSTOMER_HASH: " + customerHash);

        assertTrue(PasswordUtil.checkPassword("Admin@123", adminHash));
        assertTrue(PasswordUtil.checkPassword("Customer@123", customerHash));
    }
}
