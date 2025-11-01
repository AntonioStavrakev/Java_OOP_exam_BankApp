package bank.entities.client;

public class Adult extends BaseClient{


    public Adult(String name, String ID, double income) {
        super(name, ID, 4, income);
    }

    @Override
    public void increase() {
        this.setInterest(this.getInterest() + 2);
    }
}
