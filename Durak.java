import java.util.ArrayList;
import java.util.Scanner;
public class Durak{
    private Deck cards;
    private String[] suits = {"Spade", "Club", "Heart", "Diamond"};
    private String[] ranks = {"6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private int[] values = {6, 7, 8, 9, 10, 11, 12, 13, 14};
    private String powerSuit;
    private ListNode players;
    private boolean defending;
    private boolean openBoard;
    private boolean pickup;
    private boolean pickupEntered;
    private boolean trash;
    private int numPlayers;
    private Player user;
    private Player initialAttacker;
    private Player initialDefender;
    private int atkCount;
    private ListNode atkList;
    private DurakAi ai1;
    private DurakAi ai2;
    private DurakAi ai3;
    private ArrayList<Object> river = new ArrayList<Object>();

    public Durak(String name){
        cards = new Deck(suits, ranks, values);
        powerSuit = suits[(int)(Math.random() * 4)];
        initializePlayers(name);
        initializeCards();
        players = findLowestPower();
        defending = false;
        openBoard = false;
        pickup = false;
        pickupEntered = false;
        trash = false;
        numPlayers = 4;
        atkCount = 4;
        initialAttacker = players.getValue();
        initialDefender = players.getNext().getValue();
        atkList = players;
    }

    private void initializePlayers(String name){
        user = new Player(powerSuit, name);
        players = new ListNode(user, null);
        ListNode temp = players;
        ai1 = new DurakAi(powerSuit, "A.I. 1");
        players.setNext(new ListNode(ai1, null));
        players = players.getNext();
        ai2 = new DurakAi(powerSuit, "A.I. 2");
        players.setNext(new ListNode(ai2, null));
        players = players.getNext();
        ai3 = new DurakAi(powerSuit, "A.I. 3");
        players.setNext(new ListNode(ai3, null));
        players = players.getNext();
        players.setNext(temp);
        players = players.getNext();
    }

    private void initializeCards(){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 6; j++){
                Card c = cards.deal();
                players.getValue().add(c);
            }
            players = players.getNext();
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
        return numPlayers == 1;
    }

    public void nextTurn() throws InterruptedException{
        printGame();
        Scanner in = new Scanner(System.in);

        if(defending && legalDefensePossible(players.getNext().getValue(), players.getNext().getNext().getValue())){
            Player defender = players.getNext().getValue();
            Player nextDef = players.getNext().getNext().getValue();
            DurakInput defense = new DurakInput();

            if(defender instanceof DurakAi){
                defense = ((DurakAi)defender).playDefense(river, cards.size(), powerSuit, nextDef);
            }
            else{
                int playerIndex = -1, riverIndex = -1;
                String res;
                boolean contin = true;

                do{
                    System.out.print("\nChoose a card from your deck to defend (or rotate) with by entering its index (index is displayed to the right of the card) or N or n to pick up the cards: ");
                    res = in.nextLine();

                    if(res.equals("N") || res.equals("n")){
                        defense = new DurakInput(-1, -1, false, false, true);
                        contin = false;
                    }
                    else{
                        if(checkNum(res)){
                            playerIndex = Integer.parseInt(res) - 1;

                            if(playerIndex < 0/*<= 0 since index is printed starting with 1? */ || playerIndex >= defender.cards().size()){
                                System.out.println("Index is out of bounds, try again.\n");
                            }
                            else{
                                System.out.print("\nChoose a card on the field to cover by entering its index (index is displayed to the right of the card), or enter R or r to rotate, or enter N or n to cancel your earlier selection: ");
                                res = in.nextLine();

                                if(res.equals("R") || res.equals("r")){
                                    defense = new DurakInput(playerIndex, -1, true, true, false);

                                    if(!legalDefense(defense, defender, nextDef)){ // doesnt work
                                        System.out.println("That move is illegal, try again.\n");
                                    }
                                    else{
                                        contin = false;
                                    }
                                }
                                else if(!res.equalsIgnoreCase("N") && checkNum(res)){ // ignore case the rest
                                    riverIndex = Integer.parseInt(res) - 1;
                                    defense = new DurakInput(playerIndex, riverIndex, false, false, false);

                                    if(riverIndex < 0/*<= 0 since index is printed starting with 1? */ || riverIndex >= river.size()){
                                        System.out.println("Index is out of bounds, try again.\n");
                                    }
                                    else if(!legalDefense(defense, defender, nextDef)){
                                        System.out.println("That move is illegal, try again.\n");
                                    }
                                    else{
                                        contin = false;
                                    }
                                }
                            }
                        }
                    }
                }while(contin);
            }

            if(!defense.pickUp() && !defense.rotateEntered()){
                // defend
                river.set(defense.riverIndex()/* - 1*/, new Combination((Card)(river.get(defense.riverIndex()/* - 1*/)), defender.play(defense.indexPlayerCardInput()/* - 1*/))); // works?
            }
            else if(!defense.pickUp()){
                // rotate
                river.add(defender.play(defense.indexPlayerCardInput()/* - 1*/)); // works?
                players = players.getNext();
            }
            else{
                pickupEntered = true;
                defending = false;
            }

            defending = false;
            for(int i = 0; i < river.size() && !defending && !pickupEntered; i++){
                defending = river.get(i) instanceof Card;
                trash = !defending && i == 5;
            }
        }
        else if(defending){
            System.out.println("No legal defense possible, picking up the board.\n"); // maybe only print if this is the user
            pickupEntered = true;
            defending = false;
        }
        else if(pickup){ // possibly move pickup to below attack, since the attackers should be able to place down one final set of cards if the defender decides to pick up
            Player pickingUp = players.getNext().getValue();
            for(int i = 0; i < river.size(); i++){
                if(river.get(i) instanceof Combination){
                    pickingUp.add(((Combination)river.get(i)).cards()[0]);
                    pickingUp.add(((Combination)river.get(i)).cards()[1]);
                }
                else{
                    pickingUp.add(((Card)river.get(i)));
                }
            }
            river.clear();
            pickup = false;
            pickupEntered = false;
            defending = false;
            openBoard = false;
            dealCards();
            removeWinners();
            players = players.getNext().getNext();
        }
        else if(trash){ // for both pickup and trash make sure to deal cards until deck is empty or everyone has >6 cards
            river.clear();
            trash = false;
            defending = false;
            openBoard = false;
            dealCards();
            removeWinners();
            players = players.getNext();
        }
        else{
            int index = -1;
            String res;
            boolean contin = true;
            DurakInput attack = new DurakInput();
            //ListNode temp = players;
            //int count = numPlayers;
            if(atkCount == 4){
                atkList = players;
                initialAttacker = players.getValue();
                initialDefender = players.getNext().getValue();

                if(initialAttacker instanceof DurakAi){
                    attack = ((DurakAi)initialAttacker).playOffense(river, cards.size(), powerSuit, initialDefender);
                    if(!attack.attackEntered()){
                        boolean covered = false;
                        for(int i = 0; i < river.size() && !covered; i++){
                            covered = river.get(i) instanceof Combination;
                        }
                        if(pickupEntered || covered){
                            openBoard = true;
                            atkCount--;
                            atkList = atkList.getNext();
                        }
                    }
                }
                else if(legalAttackPossible(initialAttacker, initialDefender)){
                    do{
                        boolean covered = false;
                        for(int i = 0; i < river.size() && !covered; i++){
                            covered = river.get(i) instanceof Combination;
                        }

                        if(covered || pickupEntered){ // open board + allowed no cards placed
                            System.out.print("\nChoose a card from your deck to attack with by entering its index (index is displayed to the right of the card) or N or n to finish your turn: ");
                            res = in.nextLine();

                            if(res.equals("N") || res.equals("n")){ // make it so they have to play at least 1 card if river is empty also make sure to change board to open if they end their turn and board has a combination or someone said to pickup
                                attack = new DurakInput(-1, -1, false, false, false);
                                contin = false;
                                openBoard = true;
                            }
                            else{
                                if(checkNum(res)){
                                    index = Integer.parseInt(res) - 1;
                                    attack = new DurakInput(index, -1, false, true, false);
                                    if(index < 0 /*<= 0 since index is printed starting with 1? */|| index >= initialAttacker.cards().size()){
                                        System.out.println("Index is out of bounds, try again.\n");
                                    }
                                    else if(!legalAttack(attack, initialAttacker, initialDefender)){
                                        System.out.println("That move is illegal, try again.\n");
                                    }
                                    else{
                                        contin = false;
                                    }
                                }
                            }
                        }
                        else if(river.size() == 0){ // no open board + has to place at least 1 card
                            System.out.print("\nChoose a card from your deck to attack with by entering its index (index is displayed to the right of the card): ");
                            res = in.nextLine();

                            if(checkNum(res)){
                                index = Integer.parseInt(res) - 1;
                                attack = new DurakInput(index, -1, false, true, false);
                                if(index < 0 /*<= 0 since index is printed starting with 1? */|| index >= initialAttacker.cards().size()){
                                    System.out.println("Index is out of bounds, try again.\n");
                                }
                                else if(!legalAttack(attack, initialAttacker, initialDefender)){
                                    System.out.println("That move is illegal, try again.\n");
                                }
                                else{
                                    contin = false;
                                }
                            }
                        }
                        else{
                            System.out.print("\nChoose a card from your deck to attack with by entering its index (index is displayed to the right of the card) or N or n to finish your turn: ");
                            res = in.nextLine();

                            if(res.equals("N") || res.equals("n")){ // make it so they have to play at least 1 card if river is empty also make sure to change board to open if they end their turn and board has a combination or someone said to pickup
                                attack = new DurakInput(-1, -1, false, false, false);
                                contin = false;
                            }
                            else{
                                if(checkNum(res)){
                                    index = Integer.parseInt(res) - 1;
                                    attack = new DurakInput(index, -1, false, true, false);
                                    if(index < 0 /*<= 0 since index is printed starting with 1? */|| index >= initialAttacker.cards().size()){
                                        System.out.println("Index is out of bounds, try again.\n");
                                    }
                                    else if(!legalAttack(attack, initialAttacker, initialDefender)){
                                        System.out.println("That move is illegal, try again.\n");
                                    }
                                    else{
                                        contin = false;
                                    }
                                }
                            }
                        }
                    }while(contin);
                }
                else{
                    System.out.println("No legal attack possible, ending turn automatically.\n");
                    
                    boolean covered = false;
                    for(int i = 0; i < river.size() && !covered; i++){
                        covered = river.get(i) instanceof Combination;
                    }
                    if(pickupEntered || covered){
                        openBoard = true;
                        atkCount--;
                        atkList = atkList.getNext();
                    }
                }
            }
            //do{
            else if(atkCount > 0){
                if(openBoard && atkList.getValue() != initialDefender){
                    if(atkList.getValue() instanceof DurakAi){
                        attack = ((DurakAi)atkList.getValue()).playOffense(river, cards.size(), powerSuit, initialDefender);
                    }
                    else{
                        do{
                            System.out.print("\nChoose a card from your deck to attack with by entering its index (index is displayed to the right of the card) or N or n to finish your turn: ");
                            res = in.nextLine();

                            if(res.equals("N") || res.equals("n")){
                                attack = new DurakInput(-1, -1, false, false, false);
                                contin = false;
                            }
                            else{
                                if(checkNum(res)){
                                    index = Integer.parseInt(res) - 1;
                                    attack = new DurakInput(index, -1, false, true, false);
                                    if(index < 0 /*<= 0 since index is printed starting with 1? */|| index >= atkList.getValue().cards().size()){
                                        System.out.println("Index is out of bounds, try again.\n");
                                    }
                                    else if(!legalAttack(attack, atkList.getValue(), initialDefender)){
                                        System.out.println("That move is illegal, try again.\n");
                                    }
                                    else{
                                        contin = false;
                                    }
                                }
                            }
                        }while(contin);
                    }
                }

                attack = new DurakInput();
            }
            else{
                trash = true;
                for(int i = 0; i < river.size() && trash; i++){
                    trash = river.get(i) instanceof Combination;
                }

                defending = !pickupEntered && !trash;
                pickup = pickupEntered;
            }

            if(attack.attackEntered()){
                river.add(atkList.getValue().play(attack.indexPlayerCardInput()));
            }
            else if(!openBoard){
                trash = true;
                for(int i = 0; i < river.size() && trash; i++){
                    trash = river.get(i) instanceof Combination;
                }

                defending = !pickupEntered && !trash;
                pickup = pickupEntered;
            }
            else{
                atkList = atkList.getNext();
                atkCount--;
            }
            //}while(count > 0);
            
            
        }
        //in.close();
    }

    private void dealCards(){
        ListNode temp = players.getNext().getNext(); // double get next since defender picks up last
        int count = numPlayers;

        while(count > 0){
            if(cards.size() == 0){
                break;
            }

            if(temp.getValue().numCards() < 6){
                temp.getValue().add(cards.deal());
            }
            else{
                count--;
                temp = temp.getNext();
            }
        }
    }
    
    private void removeWinners(){
        if(numPlayers == 1){
            return;
        }
        ListNode temp = players.getNext(); // wrote getNext since defender will be last to be removed presumably
        int i = 0;
        while(i < numPlayers){
            if(temp.getNext().getValue().numCards() == 0){
                temp.setNext(temp.getNext().getNext());
                numPlayers--;
            }
            else{
                temp = temp.getNext();
                i++;
            }
        }
    }

    private void printGame(){
        char[][] print = new char[43][100];
        int numPrint = 0;

        for(int r = 0; r < print.length; r++){
            for(int c = 0; c < print[r].length; c++){
                if(r == 0 || r == print.length - 1 || c == 0 || c == print[r].length - 1){
                    print[r][c] = '*';
                }
                else{
                    print[r][c] = ' ';
                }
            }
        }
        print[1][48] = 'a';
        print[1][49] = 'i';
        print[1][50] = '2';
        print[22][2] = 'a';
        print[22][3] = 'i';
        print[22][4] = '3';
        print[22][95] = 'a';
        print[22][96] = 'i';
        print[22][97] = '1';
        print[41][48] = 'y';
        print[41][49] = 'o';
        print[41][50] = 'u';
        print[2][10] = 'D';
        print[2][11] = 'e';
        print[2][12] = 'c';
        print[2][13] = 'k';
        print[3][8] = ' ';
        print[3][9] = '_';
        print[3][10] = '_';
        print[3][11] = '_';
        print[3][12] = '_';
        print[3][13] = '_';
        print[3][14] = '_';
        print[3][15] = ' ';
        print[4][8] = '/';
        print[4][15] = '\\';
        print[5][8] = '|';
        print[5][15] = '|';
        print[6][8] = '|';
        print[6][15] = '|';
        print[7][8] = '|';
        print[7][15] = '|';
        print[8][8] = '|';
        print[8][15] = '|';
        print[9][8] = '\\';
        print[9][9] = '_';
        print[9][10] = '_';
        print[9][11] = '_';
        print[9][12] = '_';
        print[9][13] = '_';
        print[9][14] = '_';
        print[9][15] = '/';
        print[6][11] = (char)(cards.size() / 10 + 48); // used to be game.numCards() with game being a Durak variable
        print[6][12] = (char)(cards.size() % 10 + 48);
        print[11][6] = 'p';
        print[11][7] = 'o';
        print[11][8] = 'w';
        print[11][9] = 'e';
        print[11][10] = 'r';
        print[11][11] = ' ';
        print[11][12] = 's';
        print[11][13] = 'u';
        print[11][14] = 'i';
        print[11][15] = 't';
        print[11][16] = ' ';
        print[11][17] = '=';
        print[11][18] = ' ';
        print[11][19] = powerSuit.charAt(0);
        print[2][45] = ' ';
        print[2][46] = '_';
        print[2][47] = '_';
        print[2][48] = '_';
        print[2][49] = '_';
        print[2][50] = '_';
        print[2][51] = '_';
        print[2][52] = ' ';
        print[3][45] = '/';
        print[3][52] = '\\';
        print[4][45] = '|';
        print[4][52] = '|';
        print[5][45] = '|';
        print[5][52] = '|';
        print[6][45] = '|';
        print[6][52] = '|';
        print[7][45] = '|';
        print[7][52] = '|';
        print[8][45] = '\\';
        print[8][46] = '_';
        print[8][47] = '_';
        print[8][48] = '_';
        print[8][49] = '_';
        print[8][50] = '_';
        print[8][51] = '_';
        print[8][52] = '/';
        print[5][48] = (char)(ai2.cards().size() / 10 + 48);
        print[5][49] = (char)(ai2.cards().size() % 10 + 48);
        print[34][45] = ' ';
        print[34][46] = '_';
        print[34][47] = '_';
        print[34][48] = '_';
        print[34][49] = '_';
        print[34][50] = '_';
        print[34][51] = '_';
        print[34][52] = ' ';
        print[35][45] = '/';
        print[35][52] = '\\';
        print[36][45] = '|';
        print[36][52] = '|';
        print[37][45] = '|';
        print[37][52] = '|';
        print[38][45] = '|';
        print[38][52] = '|';
        print[39][45] = '|';
        print[39][52] = '|';
        print[40][45] = '\\';
        print[40][46] = '_';
        print[40][47] = '_';
        print[40][48] = '_';
        print[40][49] = '_';
        print[40][50] = '_';
        print[40][51] = '_';
        print[40][52] = '/';
        print[37][48] = (char)(user.cards().size() / 10 + 48);
        print[37][49] = (char)(user.cards().size() % 10 + 48);
        print[19][6] = ' ';
        print[19][7] = '_';
        print[19][8] = '_';
        print[19][9] = '_';
        print[19][10] = '_';
        print[19][11] = '_';
        print[19][12] = '_';
        print[19][13] = ' ';
        print[20][6] = '/';
        print[20][13] = '\\';
        print[21][6] = '|';
        print[21][13] = '|';
        print[22][6] = '|';
        print[22][13] = '|';
        print[23][6] = '|';
        print[23][13] = '|';
        print[24][6] = '|';
        print[24][13] = '|';
        print[25][6] = '\\';
        print[25][7] = '_';
        print[25][8] = '_';
        print[25][9] = '_';
        print[25][10] = '_';
        print[25][11] = '_';
        print[25][12] = '_';
        print[25][13] = '/';
        print[22][9] = (char)(ai3.cards().size() / 10 + 48);
        print[22][10] = (char)(ai3.cards().size() % 10 + 48);
        print[19][86] = ' ';
        print[19][87] = '_';
        print[19][88] = '_';
        print[19][89] = '_';
        print[19][90] = '_';
        print[19][91] = '_';
        print[19][92] = '_';
        print[19][93] = ' ';
        print[20][86] = '/';
        print[20][93] = '\\';
        print[21][86] = '|';
        print[21][93] = '|';
        print[22][86] = '|';
        print[22][93] = '|';
        print[23][86] = '|';
        print[23][93] = '|';
        print[24][86] = '|';
        print[24][93] = '|';
        print[25][86] = '\\';
        print[25][87] = '_';
        print[25][88] = '_';
        print[25][89] = '_';
        print[25][90] = '_';
        print[25][91] = '_';
        print[25][92] = '_';
        print[25][93] = '/';
        print[22][89] = (char)(ai1.cards().size() / 10 + 48);
        print[22][90] = (char)(ai1.cards().size() % 10 + 48);

        // from here to line 574 is for printing the user's cards
        char[][] cardPrint1; // can print up to 36 cards in 1 hand (the full deck in a game of Durak is 36 cards)
        char[][] cardPrint2;
        char[][] cardPrint3;
        char[][] cardPrint4;
        char[][] cardPrint5;
        char[][] cardPrint6;

        if(user.numCards() > 6){
            cardPrint1 = new char[8][60];
        }
        else if(user.numCards() > 0){
            cardPrint1 = new char[8][10 * user.numCards()];
        }
        else{
            cardPrint1 = new char[0][0];
        }

        if(user.numCards() > 12){
            cardPrint2 = new char[8][60];
        }
        else if(user.numCards() > 6){
            cardPrint2 = new char[8][10 * (user.numCards() - 6)];
        }
        else{
            cardPrint2 = new char[0][0];
        }

        if(user.numCards() > 18){
            cardPrint3 = new char[8][60];
        }
        else if(user.numCards() > 12){
            cardPrint3 = new char[8][10 * (user.numCards() - 12)];
        }
        else{
            cardPrint3 = new char[0][0];
        }

        if(user.numCards() > 24){
            cardPrint4 = new char[8][60];
        }
        else if(user.numCards() > 18){
            cardPrint4 = new char[8][10 * (user.numCards() - 18)];
        }
        else{
            cardPrint4 = new char[0][0];
        }

        if(user.numCards() > 30){
            cardPrint5 = new char[8][60];
        }
        else if(user.numCards() > 24){
            cardPrint5 = new char[8][10 * (user.numCards() - 24)];
        }
        else{
            cardPrint5 = new char[0][0];
        }

        if(user.numCards() > 36){
            cardPrint6 = new char[8][60];
        }
        else if(user.numCards() > 30){
            cardPrint6 = new char[8][10 * (user.numCards() - 30)];
        }
        else{
            cardPrint6 = new char[0][0];
        }

        for(int r = 0; r < cardPrint1.length; r++){
            for(int c = 0; c < cardPrint1[r].length; c++){
                if(numPrint < user.numCards() * 2 && r == 1 && c != 0 && c != 7 && c < 8){
                    cardPrint1[r][c] = '_';
                }
                else if(numPrint < user.numCards() * 2 && r == 2 && c == 0){
                    cardPrint1[r][c] = '/';
                }
                else if(numPrint < user.numCards() * 2 && r == 2 && c == 7){
                    cardPrint1[r][c] = '\\';
                }
                else if(numPrint < user.numCards() * 2 && r > 2 && r < 7 &&(c == 0 || c == 7)){
                    cardPrint1[r][c] = '|';
                }
                else if(numPrint < user.numCards() * 2 && r == 7 && c != 0 && c != 7 && c < 8){
                    cardPrint1[r][c] = '_';
                }
                else if(numPrint < user.numCards() * 2 && r == 7 && c == 0){
                    cardPrint1[r][c] = '\\';
                }
                else if(numPrint < user.numCards() * 2 && r == 7 && c == 7){
                    cardPrint1[r][c] = '/';
                }
                else if(numPrint < user.numCards() * 2 && c > 9){
                    cardPrint1[r][c] = cardPrint1[r][c - 10];
                }
                else{
                    cardPrint1[r][c] = ' ';
                }
                if(cardPrint1[r][c] == '/'){
                    numPrint++;
                }
            }
        }

        numPrint = 0;
        for(int r = 0; r < cardPrint2.length; r++){
            for(int c = 0; c < cardPrint2[r].length; c++){
                cardPrint2[r][c] = cardPrint1[r][c]; 
            }
        }

        for(int r = 0; r < cardPrint3.length; r++){
            for(int c = 0; c < cardPrint3[r].length; c++){
                cardPrint3[r][c] = cardPrint1[r][c];
            }
        }

        for(int r = 0; r < cardPrint4.length; r++){
            for(int c = 0; c < cardPrint4[r].length; c++){
                cardPrint4[r][c] = cardPrint1[r][c];
            }
        }

        for(int r = 0; r < cardPrint5.length; r++){
            for(int c = 0; c < cardPrint5[r].length; c++){
                cardPrint5[r][c] = cardPrint1[r][c];
            }
        }

        for(int r = 0; r < cardPrint6.length; r++){
            for(int c = 0; c < cardPrint6[r].length; c++){
                cardPrint6[r][c] = cardPrint1[r][c];
            }
        }

        ArrayList<Card> userCards = user.cards();
        int col = 3;

        for(int i = 0; i < userCards.size(); i++){
            if(i < 6){
                cardPrint1[4][col] = userCards.get(i).rank().charAt(0);
                if(userCards.get(i).rank().equals("10")){
                    cardPrint1[4][col+1] = userCards.get(i).rank().charAt(1);
                    cardPrint1[4][col+2] = userCards.get(i).suit().charAt(0);
                }
                else{
                    cardPrint1[4][col+1] = userCards.get(i).suit().charAt(0);
                }
                cardPrint1[4][col+5] = (char)(((i+1) / 10) + 48);
                cardPrint1[4][col+6] = (char)(((i+1) % 10) + 48);
                col += 10;
                if(i == 5){
                    col = 3;
                }
            }
            else if(i < 12){
                cardPrint2[4][col] = userCards.get(i).rank().charAt(0);
                if(userCards.get(i).rank().equals("10")){
                    cardPrint2[4][col+1] = userCards.get(i).rank().charAt(1);
                    cardPrint2[4][col+2] = userCards.get(i).suit().charAt(0);
                }
                else{
                    cardPrint2[4][col+1] = userCards.get(i).suit().charAt(0);
                }
                cardPrint2[4][col+5] = (char)(((i+1) / 10) + 48);
                cardPrint2[4][col+6] = (char)(((i+1) % 10) + 48);
                col+=10;
                if(i == 11){
                    col = 3;
                }
            }
            else if(i < 18){
                cardPrint3[4][col] = userCards.get(i).rank().charAt(0);
                if(userCards.get(i).rank().equals("10")){
                    cardPrint3[4][col+1] = userCards.get(i).rank().charAt(1);
                    cardPrint3[4][col+2] = userCards.get(i).suit().charAt(0);
                }
                else{
                    cardPrint3[4][col+1] = userCards.get(i).suit().charAt(0);
                }
                cardPrint3[4][col+5] = (char)(((i+1) / 10) + 48);
                cardPrint3[4][col+6] = (char)(((i+1) % 10) + 48);
                col += 10;
                if(i == 17){
                    col = 3;
                }
            }
            else if(i < 24){
                cardPrint4[4][col] = userCards.get(i).rank().charAt(0);
                if(userCards.get(i).rank().equals("10")){
                    cardPrint4[4][col+1] = userCards.get(i).rank().charAt(1);
                    cardPrint4[4][col+2] = userCards.get(i).suit().charAt(0);
                }
                else{
                    cardPrint4[4][col+1] = userCards.get(i).suit().charAt(0);
                }
                cardPrint4[4][col+5] = (char)(((i+1) / 10) + 48);
                cardPrint4[4][col+6] = (char)(((i+1) % 10) + 48);
                col += 10;
                if(i == 23){
                    col = 3;
                }
            }
            else if(i < 30){
                cardPrint5[4][col] = userCards.get(i).rank().charAt(0);
                if(userCards.get(i).rank().equals("10")){
                    cardPrint5[4][col+1] = userCards.get(i).rank().charAt(1);
                    cardPrint5[4][col+2] = userCards.get(i).suit().charAt(0);
                }
                else{
                    cardPrint5[4][col+1] = userCards.get(i).suit().charAt(0);
                }
                cardPrint5[4][col+5] = (char)(((i+1) / 10) + 48);
                cardPrint5[4][col+6] = (char)(((i+1) % 10) + 48);
                col += 10;
                if(i == 29){
                    col = 3;
                }
            }
            else{
                cardPrint6[4][col] = userCards.get(i).rank().charAt(0);
                if(userCards.get(i).rank().equals("10")){
                    cardPrint6[4][col+1] = userCards.get(i).rank().charAt(1);
                    cardPrint6[4][col+2] = userCards.get(i).suit().charAt(0);
                }
                else{
                    cardPrint6[4][col+1] = userCards.get(i).suit().charAt(0);
                }
                cardPrint6[4][col+5] = (char)(((i+1) / 10) + 48);
                cardPrint6[4][col+6] = (char)(((i+1) % 10) + 48);
                col += 10;
            }
        }

        // from here to line 709 is for printing the river
        col = 3;
        int colO = 27;
        int row = 10;
        for(int i = 0; i < river.size(); i++){
            if(river.get(i) instanceof Card){
                print[row][colO] = ' ';
                print[row][colO + 1] = '_';
                print[row][colO + 2] = '_';
                print[row][colO + 3] = '_';
                print[row][colO + 4] = '_';
                print[row][colO + 5] = '_';
                print[row][colO + 6] = '_';
                print[row][colO + 7] = ' ';
                print[row + 1][colO] = '/';
                print[row + 1][colO + 7] = '\\';
                print[row + 2][colO] = '|';
                print[row + 2][colO + 7] = '|';
                print[row + 3][colO] = '|';
                print[row + 3][colO + 7] = '|';
                print[row + 4][colO] = '|';
                print[row + 4][colO + 7] = '|';
                print[row + 5][colO] = '|';
                print[row + 5][colO + 7] = '|';
                print[row + 6][colO] = '\\';
                print[row + 6][colO + 1] = '_';
                print[row + 6][colO + 2] = '_';
                print[row + 6][colO + 3] = '_';
                print[row + 6][colO + 4] = '_';
                print[row + 6][colO + 5] = '_';
                print[row + 6][colO + 6] = '_';
                print[row + 6][colO + 7] = '/';
                print[row + 3][colO + 3] = (char)(((Card)(river.get(i))).rank().charAt(0));
                print[row + 3][colO + 8] = (char)(((i+1) / 10) + 48);
                print[row + 3][colO + 9] = (char)(((i+1) % 10) + 48);

                if(((Card)river.get(i)).rank().equals("10")){
                    print[row + 3][colO + 4] = (char)(((Card)(river.get(i))).rank().charAt(1));
                    print[row + 3][colO + 5] = (char)(((Card)(river.get(i))).suit().charAt(0));
                }
                else{
                    print[row + 3][colO + 4] = (char)(((Card)(river.get(i))).suit().charAt(0));
                }
            }
            else{
                Card[] tempC = ((Combination)river.get(i)).cards();
                print[row][colO] = ' ';
                print[row][colO + 1] = '_';
                print[row][colO + 2] = '_';
                print[row][colO + 3] = '_';
                print[row][colO + 4] = '_';
                print[row][colO + 5] = '_';
                print[row][colO + 6] = '_';
                print[row][colO + 7] = ' ';
                print[row + 1][colO] = '/';
                print[row + 1][colO + 7] = '\\';
                print[row + 2][colO] = '|';
                print[row + 2][colO + 7] = '|';
                print[row + 3][colO] = '|';
                print[row + 3][colO + 7] = '|';
                print[row + 4][colO] = '|';
                print[row + 4][colO + 7] = '|';
                print[row + 5][colO] = '|';
                print[row + 5][colO + 7] = '|';
                print[row + 6][colO] = '\\';
                print[row + 6][colO + 1] = '_';
                print[row + 6][colO + 2] = '_';
                print[row + 6][colO + 3] = '_';
                print[row + 6][colO + 4] = '_';
                print[row + 6][colO + 5] = '_';
                print[row + 6][colO + 6] = '_';
                print[row + 6][colO + 7] = '/';
                print[row + 3][colO + 3] = (char)(tempC[0].rank().charAt(0));

                if(tempC[0].rank().equals("10")){
                    print[row + 3][colO + 4] = (char)(tempC[0].rank().charAt(1));
                    print[row + 3][colO + 5] = (char)(tempC[0].suit().charAt(0));
                }
                else{
                    print[row + 3][colO + 4] = (char)(tempC[0].suit().charAt(0));
                }

                print[row + 4][colO + 2] = ' ';
                print[row + 4][colO + 3] = '_';
                print[row + 4][colO + 4] = '_';
                print[row + 4][colO + 5] = '_';
                print[row + 4][colO + 6] = '_';
                print[row + 4][colO + 7] = '_';
                print[row + 4][colO + 8] = '_';
                print[row + 4][colO + 9] = ' ';
                print[row + 5][colO + 2] = '/';
                print[row + 5][colO + 9] = '\\';
                print[row + 6][colO + 2] = '|';
                print[row + 6][colO + 9] = '|';
                print[row + 7][colO + 2] = '|';
                print[row + 7][colO + 9] = '|';
                print[row + 8][colO + 2] = '|';
                print[row + 8][colO + 9] = '|';
                print[row + 9][colO + 2] = '|';
                print[row + 9][colO + 9] = '|';
                print[row + 10][colO + 2] = '\\';
                print[row + 10][colO + 3] = '_';
                print[row + 10][colO + 4] = '_';
                print[row + 10][colO + 5] = '_';
                print[row + 10][colO + 6] = '_';
                print[row + 10][colO + 7] = '_';
                print[row + 10][colO + 8] = '_';
                print[row + 10][colO + 9] = '/';
                print[row + 7][colO + 5] = (char)(tempC[1].rank().charAt(0));

                if(tempC[1].rank().equals("10")){
                    print[row + 7][colO + 6] = (char)(tempC[1].rank().charAt(1));
                    print[row + 7][colO + 7] = (char)(tempC[1].suit().charAt(0));
                }
                else{
                    print[row + 7][colO + 6] = (char)(tempC[1].suit().charAt(0));
                }
            }
            colO += 17;
            if(i == 2){
                colO = 27;
                row += 11;
            }
        }
        
        // here to line 760 is actually printing
        colO = 27;
        row = 10;
        for(char[] c : print){
            for(char e : c){
                System.out.print(e);
            }
            System.out.println();
        }
        for(char[] c : cardPrint1){
            for(char e : c){
                System.out.print(e);
            }
            System.out.println();
        }
        for(char[] c : cardPrint2){
            for(char e : c){
                System.out.print(e);
            }
            System.out.println();
        }
        for(char[] c : cardPrint3){
            for(char e : c){
                System.out.print(e);
            }
            System.out.println();
        }
        for(char[] c : cardPrint4){
            for(char e : c){
                System.out.print(e);
            }
            System.out.println();
        }
        for(char[] c : cardPrint5){
            for(char e : c){
                System.out.print(e);
            }
            System.out.println();
        }
        for(char[] c : cardPrint6){
            for(char e : c){
                System.out.print(e);
            }
            System.out.println();
        }
        System.out.println();
    }

    public boolean legalDefensePossible(Player p, Player def){
        ArrayList<Card> cards = new ArrayList<Card>(p.cards());
        boolean possible = true;

        if(rotatePossible(river, def)){
            for(int i = 0; i < cards.size(); i++){
                if(cards.get(i).rank().equals(((Card)river.get(i)).rank())){
                    return true;
                }
            }
        }

        for(int i = 0; i < river.size() && possible; i++){
            if(river.get(i) instanceof Card){
                for(int j = 0; j < cards.size(); j++){
                    DurakInput d = new DurakInput(j, i, false, false, false);
                    if(legalDefense(d, p, def)){
                        cards.remove(j);
                        break;
                    }
                    else if(j == cards.size() - 1){
                        possible = false;
                        break;
                    }
                }
            }
        }
        return possible;
    }

    public boolean legalAttackPossible(Player p, Player def){
        if(river.size() == 6){
            return false;
        }

        int numCards = 0;
        for(int i = 0; i < river.size(); i++){
            if(river.get(i) instanceof Card){
                numCards++;
            }
        }

        if(numCards == def.numCards()){
            return false;
        }

        ArrayList<Card> cards = new ArrayList<Card>(p.cards());
        boolean possible = false;

        for(int i = 0; i < river.size(); i++){
            for(int j = 0; j < cards.size(); j++){
                if(legalAttack(new DurakInput(j, -1, false, true, false), p, def)){
                    possible = true;
                    break;
                }
            }
        }
        return possible || river.size() == 0;
    }

    public boolean legalDefense(DurakInput d, Player p, Player def){
        if(d.rotateEntered()){
            return (rotatePossible(river, def) && p.getCard(d.indexPlayerCardInput()).rank().equals(((Card)river.get(0)).rank()));
        }
        else{
            Card rivCard = (Card)river.get(d.riverIndex());
            Card handCard = p.getCard(d.indexPlayerCardInput());
            if(rivCard.suit().equals(handCard.suit())){
                return (rivCard.compareTo(handCard) < 0);
            }
            else{
                return (handCard.suit().equals(powerSuit));
            }
        }
    }

    public boolean legalAttack(DurakInput d, Player p, Player def){
        boolean legal = false;

        int numCards = 0;
        for(int i = 0; i < river.size(); i++){
            if(river.get(i) instanceof Card){
                numCards++;
            }
        }

        if(d.attackEntered() && river.size() != 6 && def.numCards() != numCards){
            for(int i = 0; i < river.size(); i++){
                Card playerCard = p.getCard(d.indexPlayerCardInput());
                if(river.get(i) instanceof Card && ((Card)river.get(i)).rank().equals(playerCard.rank())){
                    legal = true;
                    break;
                }
                else if(river.get(i) instanceof Combination){
                    Card[] cards = ((Combination)river.get(i)).cards();
                    if(cards[0].rank().equals(playerCard.rank()) || cards[1].rank().equals(playerCard.rank())){
                        legal = true;
                        break;
                    }
                }
            }
        }

        return legal || river.size() == 0;
    }

    public boolean rotatePossible(ArrayList<Object> riv, Player def){
        boolean allUncover = true;
        boolean allSame = true;

        for(int i = 0; i < riv.size(); i++){
            if(riv.get(i) instanceof Combination){
                allUncover = false;
                break;
            }
            else if(i > 0 && allUncover){
                Card c1 = (Card)riv.get(i);
                Card c2 = (Card)riv.get(i-1);
                if(!c2.rank().equals(c1.rank())){
                    allSame = false;
                    break;
                }
            }
            
        }
        return (riv.size() > 0 && allUncover && allSame && (riv.size() + 1) <= def.numCards());
    }

    private boolean checkNum(String r){
        try{
            Integer.parseInt(r);
            return true;
        }
        catch(Exception ex){
            System.out.println("Invalid input entered, try again.\n");
            return false;
        }
    }

    public String getLoser(){
        return players.getValue().getName();
    }
} 