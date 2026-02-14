package com.mycompany.motorph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class AccountAuthService {
    private static final String ACCOUNTS_CSV = "/authorized_accounts.csv";
    private final Map<String, String> authorizedAccounts = new HashMap<>();

    public AccountAuthService() {
        loadAccounts();
    }

    private void loadAccounts() {
        try (InputStream is = AccountAuthService.class.getResourceAsStream(ACCOUNTS_CSV)) {
            if (is == null) {
                throw new IOException("Account CSV not found: " + ACCOUNTS_CSV);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line = br.readLine(); // header
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(",", -1);
                    if (parts.length < 2) continue;
                    authorizedAccounts.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException ex) {
            System.err.println("Unable to load authorized accounts: " + ex.getMessage());
        }
    }

    public boolean validateCredentials(String username, String password) {
        if (username == null || password == null) return false;
        String storedPassword = authorizedAccounts.get(username.trim());
        return storedPassword != null && storedPassword.equals(password);
    }
}
