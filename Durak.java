import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;
public class Durak
{
    private Deck cards;
    private String[] suits = {"Spade", "Club", "Heart", "Diamond"};
    private String[] ranks = {"6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private int[] values = {6, 7, 8, 9, 10, 11, 12, 13, 14};
    private String powerSuit;
    private ListNode players;
    private boolean defending;
    private boolean openBoard;
    private boolean pickup;
    private boolean trash;
    private int numPlayers;
    private ArrayList<Object> river = new ArrayList<Object>();

    public Durak()
    {
        cards = new Deck(suits, ranks, values);
        powerSuit = suits[(int)(Math.random() * 4)];
        initializePlayers();
        initializeCards();
        players = findLowestPower();
        defending = false;
        openBoard = false;
        pickup = false;
        trash = false;
        numPlayers = 4;
    }

    private void initializePlayers(){
        players = new ListNode(new Player(powerSuit), null);
        ListNode temp = players;
        players.setNext(new ListNode(new DurakAi(powerSuit), null));
        players = players.getNext();
        players.setNext(new ListNode(new DurakAi(powerSuit), null));
        players = players.getNext();
        players.setNext(new ListNode(new DurakAi(powerSuit), null));
        players = players.getNext();
        players.setNext(temp);
        players = players.getNext();
    }

    private void initializeCards(){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 6; j++){
                players.getValue().add(cards.deal());
            }
        }
    }

    private ListNode findLowestPower(){
        Card lowest = null;
        ListNode player = players, temp = players;
        do{
            for(Card c : temp.getValue().cards()){
                if(c.suit().equals(powerSuit)){
                    if(lowest == null || lowest.compareTo(c) > 0){
                        lowest = c;
                        player = temp;
                    }
                }
            }
            temp = temp.getNext();
        }while(temp != players);
        return player;
    }

    public boolean gameEnded(){
        return players.getNext() == players;
    }

    public void nextTurn(){
        // edge case 2 players left 1 player attacks and the other defends and they both have 0 cards
// defense first
// then open board
// then pickup
// then trash
// then attack
// potentially change the variable from an attacking variable to a defending variable


// Defense
// Pickup
// Trash
// Merge attack and open with a loop through all attackers and only let the others get prompted to attack if openBoard
// Migrate the attack function to a prompt attack function so that it can just be called by the attack if statement
        Scanner in = new Scanner(System.in);

        if(defending && legalDefensePossible(players.getNext().getValue())){ // to cover all cards
            Player defender = players.getNext().getValue();
            DurakInput defense = new DurakInput();

            if(defender instanceof DurakAi){
                defense = ((DurakAi)defender).playDefense(river, cards.size(), powerSuit);
            }
            else{
                int playerIndex = -1, riverIndex = -1;
                String res;
                boolean contin = true;

                do{
                    System.out.print("\nChoose a card from your deck to defend with (or rotate) by entering its index (index is displayed to the right of the card) or N or n to pick up the cards: ");
                    res = in.nextLine(); // system.in input needs here

                    if(res.equals("N") || res.equals("n")){
                        defense = new DurakInput(-1, -1, false, false, true /*used to be false */);
                        pickup = true;
                        contin = false;
                    }
                    else{
                        if(checkNum(res)){
                            playerIndex = Integer.parseInt(res);
                            //attack = new DurakInput(index, -1, false, true, false);
                            if(playerIndex < 0/*<= 0 since index is printed starting with 1? */ || playerIndex >= defender.cards().size()){
                                System.out.println("Index is out of bounds, try again.\n");
                            }
                            else if(!legalAttack(attack, attacker)){ // check legal defense possible with entered card which may need a different method also check if rotation is valid too
                                System.out.println("That move is illegal, try again.\n"); // this might be just covered in the later check after they have entered the river index
                            }
                            else{
                                System.out.print("\nChoose a card on the field to cover by entering its index (index is displayed to the right of the card) or enter R or r to rotate: ");
                                res = in.nextLine();

                                if(res.equals("R") || res.equals("r")) // check if allowed to do this
                                {
                                    river.add(defender.play(playerIndex - 1)); // other person enough cards to cover to make legal check too
                                    players = players.getNext();
                                }
                                else if(checkNum(res)){
                                    riverIndex = Integer.parseInt(res);

                                    if(riverIndex < 0/*<= 0 since index is printed starting with 1? */ || riverIndex >= river.size()){
                                        System.out.println("Index is out of bounds, try again.\n");
                                    }
                                    else if(!legalDefense()){ // check if with river index if the defense is legal
// also there should be a way to abort the defense although it might already be like that since theres no while loop here unlike before
                                    }
                                    else{
                                        river.set(riverIndex - 1, new Combination((Card)(river.get(riverIndex - 1)), defender.play(playerIndex - 1)));
                                    }
                                }
                                contin = true; //?
                            }
                        }
                    }

                    // Check if the board is all covered then defending is false



                }while(contin);
            }
            /*
            if(attack.attackEntered()){
                Card c = attacker.play(attack.indexPlayerCardInput());
                river.add(c);
            }
            else{
                if(numPlayers > 2){
                    openBoard = true;
                }
                else{
                    attacking = false;
                }
            }

                        System.out.print("\nChoose a card from your deck to defend with (or rotate) by entering its index (index is displayed to the right of the card) or N or n to pick up the cards: ");
                        String res = in.nextLine();
                        if(res.equals("N") || res.equals("n"))
                        {
                            pickUp = true;
                            open = true;
                        }
                        else
                        {
                            int index = getNum(res);
                            while(index < 0 || index > user.cards().size())
                            {
                                System.out.println("Invalid range.");
                                System.out.print("\nChoose a card from your deck to defend with (or rotate) by entering its index (index is displayed to the right of the card) or N or n to pick up the cards: ");
                                res = in.nextLine();
                                if(res.equals("N") || res.equals("n"))
                                {
                                    pickUp = true;
                                    open = true;
                                }
                                if(!pickUp)
                                    index = getNum(res);
                            }
                            if(!pickUp)
                            {
                                System.out.print("\nChoose a card on the field to cover by entering its index (index is displayed to the right of the card) or enter R or r to rotate: ");
                                res = in.nextLine();
                                if(res.equals("R") || res.equals("r"))
                                {
                                    river.add(user.play(index - 1));
                                    moveOrder = moveOrder.getNext();
                                }
                                else
                                {
                                    int othIndex = getNum(res);
                                    while(index < 0 || index > user.cards().size())
                                    {
                                        System.out.println("Invalid range.");
                                        System.out.print("\nChoose a card on the field to cover by entering its index (index is displayed to the right of the card) or enter R or r to rotate: ");
                                        res = in.nextLine();
                                        if(res.equals("R") || res.equals("r"))
                                        {
                                            river.add(user.play(index - 1));
                                            moveOrder = moveOrder.getNext();
                                            break;
                                        }
                                        othIndex = getNum(res);
                                    }
                                    river.set(othIndex - 1, new Combination((Card)(river.get(othIndex - 1)), user.play(index - 1)));
                                }
                            }
                        }*/
        }
        else if(defending){
            System.out.println("No legal defense possible, picking up the board.\n");
            pickup = true;
        }
        else if(pickup){
            pickup = false;
        }
        else if(trash){
            trash = false;
        }
        else if(legalAttackPossible(players.getValue())){
            Player attacker = players.getValue();
            DurakInput attack = new DurakInput();

            if(attacker instanceof DurakAi){
                attack = ((DurakAi)attacker).playOffense(river, cards.size(), powerSuit);
            }
            else{
                int index = -1;
                String res;
                boolean contin = true;

                do{
                    System.out.print("\nChoose a card from your deck to attack with by entering its index (index is displayed to the right of the card) or N or n to finish your turn: ");
                    res = in.nextLine(); // system.in input needs here

                    if(res.equals("N") || res.equals("n")){
                        attack = new DurakInput(-1, -1, false, false, false);
                        contin = false;
                    }
                    else{
                        if(checkNum(res)){
                            index = Integer.parseInt(res);
                            attack = new DurakInput(index, -1, false, true, false);
                            if(index < 0 || index >= attacker.cards().size()){
                                System.out.println("Index is out of bounds, try again.\n");
                            }
                            else if(!legalAttack(attack, attacker)){
                                System.out.println("That move is illegal, try again.\n");
                            }
                            else{
                                contin = true;
                            }
                        }
                    }
                }while(contin);
            }

            if(attack.attackEntered()){
                Card c = attacker.play(attack.indexPlayerCardInput());
                river.add(c);
            }
            else{
                if(numPlayers > 2){
                    openBoard = true;
                }
                else{
                    defending = true;
                }
            }
        }
        else{
            if(numPlayers > 2){
                System.out.println("No legal attack possible, opening the board.\n");
                openBoard = true;
            }
            else{
                System.out.println("No legal attack possible.\n");
                defending = true;
            }
        }
        in.close();





        // At some point there should be a check to see if a player has won and should be removed from the game and also check
        // to see if there are enough players to keep going or end the game
    }
    
    public boolean legalDefensePossible(Player p)
    {
        ArrayList<Card> cards = p.cards();
        ArrayList<Object> riv = river;
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

    public boolean legalAttackPossible(Player p)
    {
        ArrayList<Card> cards = p.cards();
        ArrayList<Object> riv = river;
        boolean possible = false;

        for(int i = 0; i < riv.size(); i++)
        {
            for(int j = 0; j < cards.size(); j++)
            {
                if(legalAttack(new DurakInput(j, -1, false, true, false), p))
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

    public boolean legalAttack(DurakInput d, Player p)
    {
        boolean legal = false;
        ArrayList<Object> riv = river; // chage

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

    private boolean checkNum(String r)
    {
        try{
            Integer.parseInt(r);
            return true;
        }
        catch(Exception ex){
            System.out.println("Invalid input entered, try again.\n");
            return false;
        }
    }
} 