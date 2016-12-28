package cz.fb.manaus.reactor.betting;

import cz.fb.manaus.core.model.AccountMoney;
import cz.fb.manaus.core.model.Bet;

import java.util.List;

public interface BetService {

    List<String> placeBets(List<Bet> bets);

    List<String> updateBets(List<Bet> bets);

    void cancelBets(List<Bet> bets);

    AccountMoney getAccountMoney();

}
