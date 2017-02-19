package cz.fb.manaus.core.model;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Date;


@Entity
public class SettledBet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private long selectionId;

    @Column(nullable = false)
    private String selectionName;

    @Column(nullable = false)
    private double profitAndLoss;

    @Column
    private Date placed;

    @Column
    private Date matched;

    @Column(nullable = false)
    private Date settled;
    @Embedded
    private Price price;


    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    private BetAction betAction;


    public SettledBet(long selectionId, String selectionName, double profitAndLoss, Date settled, Price price) {
        this.selectionId = selectionId;
        this.selectionName = selectionName;
        this.profitAndLoss = profitAndLoss;
        this.settled = settled;
        this.price = price;
    }

    public SettledBet() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Price getPrice() {
        return price;
    }

    public long getSelectionId() {
        return selectionId;
    }

    public double getProfitAndLoss() {
        return profitAndLoss;
    }

    public String getSelectionName() {
        return selectionName;
    }

    public Date getPlaced() {
        return placed;
    }

    public void setPlaced(Date placed) {
        this.placed = placed;
    }

    public Date getMatched() {
        return matched;
    }

    public void setMatched(Date matched) {
        this.matched = matched;
    }

    public Date getSettled() {
        return settled;
    }

    public BetAction getBetAction() {
        return betAction;
    }

    public void setBetAction(BetAction betAction) {
        this.betAction = betAction;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("selectionId", selectionId)
                .add("selectionName", selectionName)
                .add("profitAndLoss", profitAndLoss)
                .add("placed", placed)
                .add("settled", settled)
                .add("price", price)
                .add("betAction", betAction)
                .toString();
    }
}
