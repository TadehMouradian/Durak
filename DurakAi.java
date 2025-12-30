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
        Durak temp = new Durak("temp");
        int rotIn = -1;
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
            return new DurakInput(rotIn, -1, true, false, false);
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
            return new DurakInput(-1, 01, false, false, true);
        }
    }

    public DurakInput playOffense(ArrayList<Object> river, int deckSize, String powerSuit){
        ArrayList<Card> viable = new ArrayList<Card>();
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
            return new DurakInput(-1, -1, false, false, true);
        }
        else{
            return new DurakInput(i, -1, false, true, false);
        }
    }

    public int getSpeed(){
        return speed;
    }
}