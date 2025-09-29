package repository.impl;
import repository.repositoryInterface.TransactionRepository;
import
public class TransactionReositoryImp implements TransactionRepository {

    private static TransactionRepository transactionRepository ;

    @Override
    public void depot() {

    }

    @Override
    public void withdraw() {

    }

    @Override
    public boolean isValideWithBanc() {
        return false;
    }
}
