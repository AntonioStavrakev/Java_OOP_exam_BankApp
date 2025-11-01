package bank.core;

import bank.Main;
import bank.common.ConstantMessages;
import bank.common.ExceptionMessages;
import bank.entities.bank.Bank;
import bank.entities.bank.BranchBank;
import bank.entities.bank.CentralBank;
import bank.entities.client.Adult;
import bank.entities.client.Client;
import bank.entities.client.Student;
import bank.entities.loan.Loan;
import bank.entities.loan.MortgageLoan;
import bank.entities.loan.StudentLoan;
import bank.repositories.LoanRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ControllerImpl implements Controller {
    private LoanRepository loanRepository;
    private Map<String, Bank> banks;

    public ControllerImpl() {
        this.loanRepository = new LoanRepository();
        this.banks = new HashMap<>();
    }

    @Override
    public String addBank(String type, String name) {
        Bank bank;
        switch (type) {
            case "BranchBank":
                bank = new BranchBank(name);
                break;
            case "CentralBank":
                bank = new CentralBank(name);
                break;
            default:
                throw new IllegalArgumentException(ExceptionMessages.INVALID_BANK_TYPE);
        }
        banks.put(name, bank);
        return String.format(ConstantMessages.SUCCESSFULLY_ADDED_BANK_OR_LOAN_TYPE, type);
    }

    @Override
    public String addLoan(String type) {
        Loan loan;
        switch (type) {
            case "StudentLoan":
                loan = new StudentLoan();
                break;
            case "MortgageLoan":
                loan = new MortgageLoan();
                break;
            default:
                throw new IllegalArgumentException(ExceptionMessages.INVALID_LOAN_TYPE);
        }
        this.loanRepository.addLoan(loan);
        return String.format(ConstantMessages.SUCCESSFULLY_ADDED_BANK_OR_LOAN_TYPE, type);
    }

    @Override
    public String returnedLoan(String bankName, String loanType) {

        Loan loan = loanRepository.findFirst(loanType);

        if (loan == null) {
            throw new IllegalArgumentException(String.format(ExceptionMessages.NO_LOAN_FOUND, loanType));
        }
        Bank bank = getBankByName(bankName);
        bank.addLoan(loan);
        this.loanRepository.removeLoan(loan);
        return String.format(ConstantMessages.SUCCESSFULLY_ADDED_CLIENT_OR_LOAN_TO_BANK, loanType, bankName);
    }

    @Override
    public String addClient(String bankName, String clientType, String clientName, String clientID, double income) {
        Client client;
        switch (clientType) {
            case "Adult":
                client = new Adult(clientName, clientID, income);

                break;
            case "Student":
                client = new Student(clientName, clientID, income);
                break;
            default:
                throw new IllegalArgumentException(ExceptionMessages.INVALID_CLIENT_TYPE);
        }
        Bank bank = getBankByName(bankName);
        String output;
        if (!isSuitable(clientType, bank)) {
            output = ConstantMessages.UNSUITABLE_BANK;
        } else {
            bank.addClient(client);
            output = String.format(ConstantMessages.SUCCESSFULLY_ADDED_CLIENT_OR_LOAN_TO_BANK, clientType, bankName);
        }

        return output;
    }

    @Override
    public String finalCalculation(String bankName) {
        Bank bank = getBankByName(bankName);
        double clientPrices = bank.getClients().stream()
                .mapToDouble(Client::getIncome).sum();
        double loanPrices = bank.getLoans().stream()
                .mapToDouble(Loan::getAmount).sum();
        return String.format(ConstantMessages.FUNDS_BANK, bankName, clientPrices + loanPrices);
    }

    @Override
    public String getStatistics() {
        return banks.values().stream()
                .map(Bank::getStatistics)
                .collect(Collectors.joining(System.lineSeparator())).trim();
    }

    private boolean isSuitable(String clientType, Bank bank) {
        String bankType = bank.getClass().getSimpleName();

        if (clientType.equals("Adult") && bankType.equals("CentralBank")) {
            return true;
        } else if (clientType.equals("Student") && bankType.equals("BranchBank")) {
            return true;
        }

        return false;
    }
    private Bank getBankByName(String bankName) {
        return this.banks.values().stream()
                .filter(b -> bankName.equals(b.getName()))
                .findFirst().orElse(null);
    }
}
