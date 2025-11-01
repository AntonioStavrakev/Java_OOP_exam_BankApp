package bank.entities.bank;

import bank.common.ExceptionMessages;
import bank.entities.client.Client;
import bank.entities.loan.Loan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class BaseBank implements Bank{
    private String name;
    private int capacity;
    private Collection<Loan> loans;
    private Collection<Client> clients;

    public BaseBank(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.loans = new ArrayList<>();
        this.clients = new ArrayList<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessages.BANK_NAME_CANNOT_BE_NULL_OR_EMPTY);
        }
        this.name = name;
    }

    @Override
    public Collection<Client> getClients() {
        return this.clients;
    }

    @Override
    public Collection<Loan> getLoans() {
        return this.loans;
    }

    @Override
    public void addClient(Client client) {
        if (this.getClients().size() < this.capacity) {
            if (client.getClass().getSimpleName().equals("Adult") && this.getClass().getSimpleName().equals("CentralBank")) {
                this.clients.add(client);
            } else if (client.getClass().getSimpleName().equals("Student") && this.getClass().getSimpleName().equals("BranchBank")) {
                this.clients.add(client);
            } else {
                throw new IllegalStateException(ExceptionMessages.NOT_ENOUGH_CAPACITY_FOR_CLIENT);
            }
        }


    }

    @Override
    public void removeClient(Client client) {
        this.getClients().remove(client);
    }

    @Override
    public void addLoan(Loan loan) {
        this.getLoans().add(loan);
    }

    @Override
    public int sumOfInterestRates() {
        int sum = 0;

        for (Loan loan : this.getLoans()) {
            sum += loan.getInterestRate();
        }

        return sum;
    }

    @Override
    public String getStatistics() {
//        return String.format("Name: %s, Type: %s:%n", this.getName(), this.getClass().getSimpleName())
//                + String.format("Clients: %s%n", getClients().isEmpty()
//                ? "none"
//                : this.getClients().stream().map(Client::getName).collect(Collectors.joining(", ")).trim())
//                + String.format("Loans: %s, Sum of interest rates: %d%n", this.getLoans().size(), this.sumOfInterestRates());
        return String.format("Name: %s, Type: %s%nClients: %s%nLoans: %d, Sum of interest rates: %d", this.getName(), this.getClass().getSimpleName(), getClients().isEmpty() ? "none" : this.getClients().stream().map(Client::getName).collect(Collectors.joining(", ")).trim(), this.getLoans().size(), this.sumOfInterestRates()).trim();
    }
}
