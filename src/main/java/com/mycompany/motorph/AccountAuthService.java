package com.mycompany.motorph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class AccountAuthService {
    private static final String ACCOUNTS_CSV = "/authorized_accounts.csv";

    public static class AuthResult {
        public final boolean authenticated;
        public final String role;

        public AuthResult(boolean authenticated, String role) {
            this.authenticated = authenticated;
            this.role = role;
        }
    }

    private static class AccountInfo {
        String password;
        String role;

        AccountInfo(String password, String role) {
            this.password = password;
            this.role = role;
        }
    }

    private final Map<String, AccountInfo> authorizedAccounts = new HashMap<>();
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
                br.readLine(); // header
                String line;
                String line = br.readLine(); // header
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] parts = line.split(",", -1);
                    if (parts.length < 2) continue;
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    String role = parts.length >= 3 ? parts[2].trim() : "EMPLOYEE";
                    authorizedAccounts.put(username, new AccountInfo(password, role));
                    authorizedAccounts.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException ex) {
            System.err.println("Unable to load authorized accounts: " + ex.getMessage());
        }
    }

    public boolean validateCredentials(String username, String password) {
        return authenticate(username, password).authenticated;
    }

    public AuthResult authenticate(String username, String password) {
        if (username == null || password == null) return new AuthResult(false, "");
        AccountInfo info = authorizedAccounts.get(username.trim());
        if (info != null && info.password.equals(password)) {
            return new AuthResult(true, info.role);
        }
        return new AuthResult(false, "");
        if (username == null || password == null) return false;
        String storedPassword = authorizedAccounts.get(username.trim());
        return storedPassword != null && storedPassword.equals(password);
    }
}
