public class Combination{
    Card[] cards;

    public Combination(Card c1, Card c2){
        cards = new Card[2];
        cards[0] = c1;
        cards[1] = c2;
    }

    public Card[] cards(){
        return cards;
    }
}