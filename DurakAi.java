import java.util.*;
public class DurakAi extends Player{
    private int speed; // remove
    private int aggressiveness;
    private int longP;
    
    public DurakAi(String power, String name){
        super(power, name);
        setTolerance();
    }

    public void setTolerance(){
        speed = (int)(Math.random() * 10) + 1;
        aggressiveness = (int)(Math.random() * 4) + 1;
        longP = (int)(Math.random() * 2) + 1;
    }

    public DurakInput playDefense(ArrayList<Object> river, int deckSize, String powerSuit){
        Durak d = new Durak("temp");
        Player t = new Player(powerSuit, ".");
        ArrayList<Card> temp = new ArrayList<Card>(super.cards());

        for(int i = 0; i < temp.size(); i++){
            t.add(temp.get(i));
        }

        if(d.rotatePossible(river)){
            for(int i = 0; i < temp.size(); i++){
                if(temp.get(i).rank().equals(((Card)river.get(0)).rank())){
                    return new DurakInput(i, -1, true, false, false);
                }
            }
        }
        if(d.legalDefensePossible(t)){
            for(int i = 0; i < river.size(); i++){
                if(river.get(i) instanceof Card){
                    for(int j = 0; j < temp.size(); j++){
                        Card rivCard = (Card)river.get(i);
                        Card handCard = temp.get(j);

                        if(rivCard.suit().equals(handCard.suit())){
                            if(rivCard.compareTo(handCard) < 0){
                                return new DurakInput(j, i, false, false, false);
                            }
                        }
                        else if(handCard.suit().equals(powerSuit)){
                            return new DurakInput(j, i, false, false, false);
                        }
                    }
                }
            }
        }
        
        return new DurakInput(-1, -1, false, false, true);
        /*int rotIn = -1;
        Set<Card> cards = new TreeSet<Card>();
        Integer uniqueCards;

        boolean contains = false;
        boolean allSame = temp.rotatePossible(river);
        ArrayList<Integer> handDiscard = new ArrayList<Integer>();
        ArrayList<Integer> riverDiscard = new ArrayList<Integer>();
        boolean winnable = true;
        Integer numGood = 0;
        ArrayList<Card> tempC = super.cards();
        ArrayList<Card> aiHand = new ArrayList<Card>();

        for(Card c : tempC){
            aiHand.add(c);
        }
        for(int i = 0; i < river.size(); i++){
            Integer smallestCover = null;
            for(int j = 0; j < aiHand.size(); j++){
                if(!(river.get(i) instanceof Combination) && aiHand.get(j).suit().equals(powerSuit) && (smallestCover == null || aiHand.get(j).compareTo(aiHand.get(smallestCover)) < 0) && (!(((Card)river.get(i)).suit().equals(powerSuit)) || aiHand.get(j).compareTo(((Card)river.get(i))) > 0)){
                    smallestCover = j;
                }
                else if(!(river.get(i) instanceof Combination) && aiHand.get(j).suit().equals(((Card)river.get(i)).suit()) && aiHand.get(j).compareTo(((Card)river.get(i))) > 0 && (smallestCover == null || aiHand.get(j).compareTo(aiHand.get(smallestCover)) < 0 || aiHand.get(smallestCover).suit().equals(powerSuit))){
                    smallestCover = j;
                }
            }
            if(smallestCover == null && !(river.get(i) instanceof Combination)){
                winnable = false;
            }
            else if(smallestCover != null){
                handDiscard.add(smallestCover);
                aiHand.remove((int)smallestCover);
                riverDiscard.add(i);
            }
        }

        for(int i = 0; i < aiHand.size(); i++){
            if(allSame && aiHand.get(i).rank().equals(((Card)river.get(0)).rank())){
                contains = true;
            }
        }

        for(int i = 0; i < river.size(); i++){
            if(river.get(i) instanceof Combination){
                Card[] card = ((Combination)river.get(i)).cards();
                cards.add(card[0]);
                cards.add(card[1]);
            }
            else{
                cards.add((Card)river.get(i));
            }
        }

        for(int i = 0; i < aiHand.size(); i++){
            if(aiHand.get(i).value() > 10 || aiHand.get(i).suit().equals(powerSuit)){
                numGood++;
            }
        }

        uniqueCards = cards.size();

        if(aiHand.size() == 0 && deckSize == 0){
            return new DurakInput(handDiscard.get(0), riverDiscard.get(0), false, false, false);
        }
        else if(!winnable && contains && allSame){
            for(int i = 0; i < aiHand.size(); i++){
                if(aiHand.get(i).rank().equals(((Card)(river.get(0))).rank()) && rotIn == -1){
                    rotIn = i;
                }
            }
            System.out.println(rotIn);
            return new DurakInput(rotIn, -1, true, false, false); // case 1
        }
        else if(winnable && (river.size() == 6 || river.size() == aiHand.size())){
            return new DurakInput(handDiscard.get(0), riverDiscard.get(0), false, false, false);
        }
        else if(winnable && aiHand.size() - numGood <= longP){
            return new DurakInput(handDiscard.get(0), riverDiscard.get(0), false, false, false);
        }
        else if(winnable && aggressiveness <= uniqueCards){
            return new DurakInput(handDiscard.get(0), riverDiscard.get(0), false, false, false);
        }
        else{
            return new DurakInput(-1, 1, false, false, true);
        }*/
    }

    public DurakInput playOffense(ArrayList<Object> river, int deckSize, String powerSuit, Player def){
        if(river.size() == 6 || river.size() == def.numCards()){
            return new DurakInput(-1, -1, false, false, true);
        }
        ArrayList<Card> temp = new ArrayList<Card>(super.cards());
        if(river.size() == 0){
            return new DurakInput(0, -1, false, true, false);
        }
        for(int i = 0; i < river.size(); i++){
            if(river.get(i) instanceof Combination){
                for(int j = 0; j < temp.size(); j++){
                    if(temp.get(j).rank().equals(((Combination)river.get(i)).cards()[0].rank()) || temp.get(j).rank().equals(((Combination)river.get(i)).cards()[1].rank())){
                        return new DurakInput(j, -1, false, true, false);
                    }
                }
            }
            else{
                for(int j = 0; j < temp.size(); j++){
                    if(temp.get(j).rank().equals(((Card)river.get(i)).rank())){
                        return new DurakInput(j, -1, false, true, false);
                    }
                }
            }
        }
        return new DurakInput(-1, -1, false, false, true);
        /*ArrayList<Card> viable = new ArrayList<Card>();
        ArrayList<Card> temp = super.cards();
        ArrayList<Card> aiHand = new ArrayList<Card>();

        for(Card c : temp){
            aiHand.add(c);
        }
        if(river.size() == 0){
            int i = 0;
            while(aiHand.get(i).suit().equals(powerSuit)){
                i++;
            }
            if(aiHand.size() > i){
                i = 0;
            }
            return new DurakInput(i, -1, false, true, false);
        }

        for(int i = 0; i < river.size(); i++){
            for(int j = 0; j < aiHand.size(); j++){
                if(river.get(i) instanceof Combination){
                    Card[] c = ((Combination)river.get(i)).cards();
                    if(aiHand.get(j).rank().equals(c[0].rank()) || aiHand.get(j).rank().equals(c[1].rank())){
                        viable.add(aiHand.get(j));
                    }
                }
                else{
                    if(aiHand.get(j).rank().equals(((Card)river.get(i)).rank())){
                        viable.add(aiHand.get(j));
                    }
                }
            }
        }
        int i = 0;
        while(i < viable.size() && viable.get(i).suit().equals(powerSuit)){
            i++;
        }

        if(i == viable.size()){
            return new DurakInput(-1, -1, false, false, true); // case 2
        }
        else{
            return new DurakInput(i, -1, false, true, false);
        }*/
    }

    //public int getSpeed(){
    //    return speed;
    //}
}