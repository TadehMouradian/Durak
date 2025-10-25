import java.util.ArrayList;
public class Deck
{
    private ArrayList<Card> deck;
    private int size;

    public Deck(String[] suits, String[] ranks, int[] values)
    {
        deck = new ArrayList<Card>();
        
        for(int i = 0; i < suits.length; i++)
        {
            for(int j = 0; j < ranks.length; j++)
            {
                deck.add(new Card(suits[i], ranks[j], values[j]));
            }
        }
        size = deck.size();
        shuffle();
    }

    public boolean isEmpty()
    {
        return size == 0;
    }

    public int size()
    {
        return size;
    }

    public void shuffle() 
    {
        size = deck.size();
        Card swap;
        int random;
        for(int i = size - 1; i > 0; i--)
        {
            random = (int)(Math.random() * (i+1));
            swap = deck.get(i);
            deck.set(i, deck.get(random));
            deck.set(random, swap);
        }
    }

    public Card deal() 
    {
        if(!isEmpty())
        {
            size--;
            return deck.get(size);
        }
        return null;
    }
}