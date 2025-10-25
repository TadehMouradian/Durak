public class Card implements Comparable<Card>
{
    private String suit;
    private String rank;
    private int value;

    public Card(String sui, String ran, int valu)
    {
        suit = sui;
        rank = ran;
        value = valu;
    }

    public String suit()
    {
        return suit;
    }

    public String rank()
    {
        return rank;
    }
    
    public int value()
    {
        return value;
    }

    public int compareTo(Card other)
    {
        return value - other.value();
    }
}