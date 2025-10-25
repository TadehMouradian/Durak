import java.util.ArrayList;
public class Durak
{
    private Deck cards;
    private String[] suits = {"Spade", "Club", "Heart", "Diamond"};
    private String[] ranks = {"6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private int[] values = {6, 7, 8, 9, 10, 11, 12, 13, 14};
    private String powerSuit;

    public Durak()
    {
        cards = new Deck(suits, ranks, values);
        powerSuit = suits[(int)(Math.random() * 4)];
    }

    public int numCards()
    {
        return cards.size();
    }
    
    public boolean legalDefensePossible(Player p, ArrayList<Object> riv)
    {
        ArrayList<Card> cards = p.cards();
        boolean possible = false;
        for(int i = 0; i < riv.size(); i++)
        {
            if(riv.get(i) instanceof Card)
            {
                for(int j = 0; j < cards.size(); j++)
                {
                    DurakInput d = new DurakInput(j, i, false, false, false);
                    if(legalDefense(d, p, riv))
                        possible = true;
                }
            }
        }
        return possible;
    }

    public boolean legalAttackPossible(Player p, ArrayList<Object> riv)
    {
        ArrayList<Card> cards = p.cards();
        boolean possible = false;

        for(int i = 0; i < riv.size(); i++)
        {
            for(int j = 0; j < cards.size(); j++)
            {
                if(legalAttack(new DurakInput(j, -1, false, true, false), p, riv))
                    possible = true;
            }
        }
        if(riv.size() == 0)
            possible = true;
        return possible;
    }

    public boolean legalDefense(DurakInput d, Player p, ArrayList<Object> riv)
    {
        if(d.rotateEntered())
        {
            return (rotatePossible(riv) && p.getCard(d.indexPlayerCardInput()).rank().equals(((Card)riv.get(0)).rank()));
        }
        else
        {
            Card rivCard = (Card)riv.get(d.riverIndex());
            Card handCard = p.getCard(d.indexPlayerCardInput());
            if(rivCard.suit().equals(handCard.suit()))
            {
                return (rivCard.compareTo(handCard) < 0);
            }
            else
            {
                return (handCard.suit().equals(powerSuit));
            }
        }
    }

    public boolean legalAttack(DurakInput d, Player p, ArrayList<Object> riv)
    {
        boolean legal = false;

        if(d.attackEntered())
        {
            for(int i = 0; i < riv.size(); i++)
            {
                Card playerCard = p.getCard(d.indexPlayerCardInput());
                if(!(riv.get(i) instanceof Combination) && ((Card)riv.get(i)).rank().equals(playerCard.rank()))
                {
                    legal = true;
                }
                else if(riv.get(i) instanceof Combination)
                {
                    Card[] cards = ((Combination)riv.get(i)).cards();
                    if(cards[0].rank().equals(playerCard.rank()) || cards[1].rank().equals(playerCard.rank()))
                        legal = true;
                }
            }
        }

        return legal;
    }

    public boolean rotatePossible(ArrayList<Object> riv)
    {
        boolean allUncover = true;
        boolean allSame = true;

        for(int i = 0; i < riv.size(); i++)
        {
            if(riv.get(i) instanceof Combination)
                allUncover = false;
            else if(i > 0 && allUncover)
            {
                Card c1 = (Card)riv.get(i);
                Card c2 = (Card)riv.get(i-1);
                if(!(c1.suit().equals(c2.suit()) && c2.rank().equals(c1.rank())))
                {
                    allSame = false;
                }
            }
            
        }
        return (riv.size() > 0 && allUncover && allSame);
    }

    public boolean hasCards()
    {
        return cards.isEmpty();
    }

    public Card deal()
    {
        return cards.deal();
    }

    public String powerSuit()
    {
        return powerSuit;
    }

    public ListNode goesFirst(ListNode node)
    {
        ListNode temp = node;
        Card smallestCard = null;
        ListNode smallest = node;
        do
        {
            ArrayList<Card> hand = ((Player)temp.getValue()).cards();
            for(int i = 0; i < hand.size(); i++)
            {
                if(hand.get(i).suit().equals(powerSuit))
                {
                    if(smallestCard == null)
                    {
                        smallestCard = hand.get(i);
                        smallest = temp;
                    }
                    else if(hand.get(i).compareTo(smallestCard) < 0)
                    {
                        smallestCard = hand.get(i);
                        smallest = temp;
                    }
                }
            }
            temp = temp.getNext();
        }while(temp != node);

        return smallest;
    }
}