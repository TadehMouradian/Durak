import java.util.ArrayList;
public class Player
{
    private ArrayList<Card> hand;
    private int numCards;
    private int spadeValue = -100;
    private int diamondValue = 0;
    private int heartValue = -1000;
    private int clubValue = -10000;
    private int powerValue = 20000;
    private String powerSuit;

    public Player(String power)
    {
        hand = new ArrayList<Card>();
        numCards = 0;
        powerSuit = power;
    }

    //insert in order instead of sorting probably
    public void add(Card c)
    {
        hand.add(c);
        numCards++;
        sort();
    }

    public boolean isEmpty()
    {
        return numCards == 0;
    }

    public int numCards()
    {
        return numCards;
    }

    public Card getCard(int ind)
    {
        return hand.get(ind);
    }

    public ArrayList<Card> cards()
    {
        return hand;
    }

    public Card play(int index)
    {
        numCards = numCards - 1;
        return hand.remove(index);
    }

    public void sort()
    {
        Card smallest;
        int small;
        Card swap;

        for(int i = 0; i < numCards; i++)
        {
            smallest = hand.get(i);
            small = i;
            swap = null;
            for(int j = i + 1; j < numCards; j++)
            {
                int value1 = hand.get(small).value();
                System.out.println(j + " " + hand.size() + " " + numCards);
                int value2 = hand.get(j).value();
                
                if(hand.get(i).suit().equals("Diamond"))
                {
                    value1 += diamondValue;
                }
                if(hand.get(j).suit().equals("Diamond"))
                {
                    value2 += diamondValue;
                }
                if(hand.get(i).suit().equals("Heart"))
                {
                    value1 += heartValue;
                }
                if(hand.get(j).suit().equals("Heart"))
                {
                    value2 += heartValue;
                }
                if(hand.get(i).suit().equals("Spade"))
                {
                    value1 += spadeValue;
                }
                if(hand.get(j).suit().equals("Spade"))
                {
                    value2 += spadeValue;
                }
                if(hand.get(i).suit().equals("Club"))
                {
                    value1 += clubValue;
                }
                if(hand.get(j).suit().equals("Club"))
                {
                    value2 += clubValue;
                }
                if(hand.get(i).suit().equals(powerSuit))
                {
                    value1 += powerValue;
                }
                if(hand.get(j).suit().equals(powerSuit))
                {
                    value2 += powerValue;
                }

                if(value2 < value1)
                {
                    smallest = hand.get(j);
                    small = j;
                }
            }
            swap = hand.get(i);
            hand.set(i, smallest);
            hand.set(small, swap);
        }
    }
}