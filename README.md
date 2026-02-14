# MotorPH Payroll Application

This project ships a Java Swing payroll application with login authentication and employee management.

## Key Features
- Login gate using authorized accounts from `src/main/resources/authorized_accounts.csv`.
- Dashboard-style GUI for payroll and employee workflows.
- Employee management with JTable, row selection, update, delete, and CSV persistence.

## OOP Model (Encapsulation, Abstraction, Inheritance)
- **Interfaces (contracts):**
  - `Payables` for salary/deductions/tax computations.
  - `PayrollCalculations` (compatibility interface that extends `Payables`).
  - `CrudOperations<T, K>` for CRUD behavior.
  - `HROperations`, `AdminOperations` for role-specific operations.
- **Abstract template class:**
  - `Employee` is now an abstract parent class with encapsulated attributes, constructors, getters/setters, and overridden payroll-calculation methods.
- **User type classes (inheritance):**
  - `HREmployee`, `FinanceEmployee`, `ITEmployee`, `AdminEmployee`, `RegularEmployee`, `ContractualEmployee`, `PartTimeEmployee`, and `StaffEmployee` all extend `Employee`.
  - Role-specific interfaces are implemented where appropriate (`HROperations`, `AdminOperations`).

## Run
```bash
javac -d out src/main/java/com/mycompany/motorph/*.java
java -cp out:src/main/resources com.mycompany.motorph.MotorPH
```
