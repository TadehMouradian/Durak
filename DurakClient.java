import java.util.*;
public class DurakClient{
    public static void main(String[] args){
        char[][] print = new char[43][100];
        ListNode moveOrder = null;
        ListNode moveCopy = null;
        ListNode speedOrder = null; // removed
        ListNode speedFirst = null;
        Durak game = new Durak();
        Player user = new Player(game.powerSuit()); // moved over
        DurakAi ai1 = new DurakAi(game.powerSuit());
        DurakAi ai2 = new DurakAi(game.powerSuit());
        DurakAi ai3 = new DurakAi(game.powerSuit());
        Set<Player> temp = new HashSet<Player>();
        Set<Player> inGame = new HashSet<Player>();
        ArrayList<Object> river = new ArrayList<Object>(); // moved over
        ArrayList<Card> ai1Card; // shouldnt be needed
        ArrayList<Card> ai2Card;
        ArrayList<Card> ai3Card;
        Card smallestPower = null; // moved over
        Player smallestCard = null;
        int numPrint = 0;
        Scanner in = new Scanner(System.in);
        boolean pickUp = false;
        boolean trash = false;
        boolean allFull = true;
        int allFullNum = 0;
        boolean otherOpen = false;
        Set<Player> someTemp = new HashSet<Player>();
        boolean open = false;

        /* ideally this should just be
         *  Durak game = new Durak();
         *  while(!game.gameEnded()){
         *      game.nextTurn();
         *  }
         */
        for(int i = 0; i < 6; i++) // moved over
        {
            user.add(game.deal());
            ai1.add(game.deal());
            ai2.add(game.deal());
            ai3.add(game.deal());
        }

        ai1Card = ai1.cards(); // m
        ai2Card = ai2.cards();
        ai3Card = ai3.cards();

        temp.add(ai1); // ?
        temp.add(ai2);
        temp.add(ai3);
        temp.add(user);

        inGame.add(ai1); // ?
        inGame.add(ai2);
        inGame.add(ai3);
        inGame.add(user);

        Player fastest = null;

        while(!temp.isEmpty()) // removed
        {
            for(Player t : temp)
            {
                if(fastest == null)
                {
                    fastest = t;
                }
                else if(t instanceof DurakAi)
                {
                    if(fastest instanceof DurakAi && ((DurakAi)t).getSpeed() > ((DurakAi)fastest).getSpeed())
                    {
                        fastest = t;
                    }
                    else if(((DurakAi)t).getSpeed() > 5)
                    {
                        fastest = t;
                    }
                }
                else
                {
                    if(((DurakAi)fastest).getSpeed() < 5)
                    {
                        fastest = t;
                    }
                }
            }
            if(speedOrder == null)
            {
                speedOrder = new ListNode(fastest, null);
                speedFirst = speedOrder; 
                temp.remove(fastest);
                fastest = null;
            }
            else
            {
                speedOrder.setNext(new ListNode(fastest, null));
                speedOrder = speedOrder.getNext();
                temp.remove(fastest);
                fastest = null;
            }
        }
        speedOrder.setNext(speedFirst);

        // m
        for(Card c : user.cards())
        {
            if(smallestPower == null && (c.suit().equals(game.powerSuit())))
            {
                smallestPower = c;
                smallestCard = user;
            }
            else if(c.suit().equals(game.powerSuit()) && smallestPower.compareTo(c) > 0)
            {
                smallestPower = c;
                smallestCard = user;
            }
        }
// m
        for(Card c : ai1Card)
        {
            if(smallestPower == null && c.suit().equals(game.powerSuit()))
            {
                smallestPower = c;
                smallestCard = ai1;
            }
            else if(c.suit().equals(game.powerSuit()) && smallestPower.compareTo(c) > 0)
            {
                smallestPower = c;
                smallestCard = ai1;
            }
        }
// m
        for(Card c : ai2Card)
        {
            if(smallestPower == null && c.suit().equals(game.powerSuit()))
            {
                smallestPower = c;
                smallestCard = ai2;
            }
            else if(c.suit().equals(game.powerSuit()) && smallestPower.compareTo(c) > 0)
            {
                smallestPower = c;
                smallestCard = ai2;
            }
        }
//m
        for(Card c : ai3Card)
        {
            if(smallestPower == null && c.suit().equals(game.powerSuit()))
            {
                smallestPower = c;
                smallestCard = ai3;
            }
            else if(c.suit().equals(game.powerSuit()) && smallestPower.compareTo(c) > 0)
            {
                smallestPower = c;
                smallestCard = ai3;
            }
        }
// m
        moveOrder = new ListNode(smallestCard, null);

        if(smallestCard == user)
        {
            ListNode temp2 = moveOrder;
            moveOrder.setNext(new ListNode(ai1, null));
            moveOrder = moveOrder.getNext();
            moveOrder.setNext(new ListNode(ai2, null));
            moveOrder = moveOrder.getNext();
            moveOrder.setNext(new ListNode(ai3, null));
            moveOrder = moveOrder.getNext();
            moveOrder.setNext(temp2);
        }
        else if(smallestCard == ai1)
        {
            ListNode temp2 = moveOrder;
            moveOrder.setNext(new ListNode(ai2, null));
            moveOrder = moveOrder.getNext();
            moveOrder.setNext(new ListNode(ai3, null));
            moveOrder = moveOrder.getNext();
            moveOrder.setNext(new ListNode(user, null));
            moveOrder = moveOrder.getNext();
            moveOrder.setNext(temp2);
        }
        else if(smallestCard == ai2)
        {
            ListNode temp2 = moveOrder;
            moveOrder.setNext(new ListNode(ai3, null));
            moveOrder = moveOrder.getNext();
            moveOrder.setNext(new ListNode(user, null));
            moveOrder = moveOrder.getNext();
            moveOrder.setNext(new ListNode(ai1, null));
            moveOrder = moveOrder.getNext();
            moveOrder.setNext(temp2);
        }
        else
        {
            ListNode temp2 = moveOrder;
            moveOrder.setNext(new ListNode(user, null));
            moveOrder = moveOrder.getNext();
            moveOrder.setNext(new ListNode(ai1, null));
            moveOrder = moveOrder.getNext();
            moveOrder.setNext(new ListNode(ai2, null));
            moveOrder = moveOrder.getNext();
            moveOrder.setNext(temp2);
        }
        moveOrder = moveOrder.getNext();
        moveCopy = moveOrder;

        while(inGame.size() != 1)
        {
            Player atk = (Player)(moveOrder.getValue());
            Player def = (Player)(moveOrder.getNext().getValue());
            boolean atkB = true;
            
            // from here to line 405 is for printing board
            for(int r = 0; r < print.length; r++)
            {
                for(int c = 0; c < print[r].length; c++)
                {
                    if(r == 0 || r == print.length - 1 || c == 0 || c == print[r].length - 1)
                    {
                        print[r][c] = '*';
                    }
                    else
                    {
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
            print[6][11] = (char)(game.numCards() / 10 + 48);
            print[6][12] = (char)(game.numCards() % 10 + 48);
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
            print[11][19] = game.powerSuit().charAt(0);
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
            char[][] cardPrint1;
            char[][] cardPrint2;
            char[][] cardPrint3;

            if(user.numCards() > 6)
            {
                cardPrint1 = new char[8][60];
            }
            else if(user.numCards() > 0)
            {
                cardPrint1 = new char[8][10 * user.numCards()];
            }
            else
            {
                cardPrint1 = new char[0][0];
            }

            if(user.numCards() > 12)
            {
                cardPrint2 = new char[8][60];
            }
            else if(user.numCards() > 6)
            {
                cardPrint2 = new char[8][10 * (user.numCards() - 6)];
            }
            else
            {
                cardPrint2 = new char[0][0];
            }

            if(user.numCards() > 18)
            {
                cardPrint3 = new char[8][60];
            }
            else if(user.numCards() > 12)
            {
                cardPrint3 = new char[8][10 * (user.numCards() - 12)];
            }
            else
            {
                cardPrint3 = new char[0][0];
            }

            for(int r = 0; r < cardPrint1.length; r++)
            {
                for(int c = 0; c < cardPrint1[r].length; c++)
                {
                    if(numPrint < user.numCards() * 2 && r == 1 && c != 0 && c != 7 && c < 8)
                    {
                        cardPrint1[r][c] = '_';
                    }
                    else if(numPrint < user.numCards() * 2 && r == 2 && c == 0)
                    {
                        cardPrint1[r][c] = '/';
                    }
                    else if(numPrint < user.numCards() * 2 && r == 2 && c == 7)
                    {
                        cardPrint1[r][c] = '\\';
                    }
                    else if(numPrint < user.numCards() * 2 && r > 2 && r < 7 &&(c == 0 || c == 7))
                    {
                        cardPrint1[r][c] = '|';
                    }
                    else if(numPrint < user.numCards() * 2 && r == 7 && c != 0 && c != 7 && c < 8)
                    {
                        cardPrint1[r][c] = '_';
                    }
                    else if(numPrint < user.numCards() * 2 && r == 7 && c == 0)
                    {
                        cardPrint1[r][c] = '\\';
                    }
                    else if(numPrint < user.numCards() * 2 && r == 7 && c == 7)
                    {
                        cardPrint1[r][c] = '/';
                    }
                    else if(numPrint < user.numCards() * 2 && c > 9)
                    {
                        cardPrint1[r][c] = cardPrint1[r][c - 10];
                    }
                    else
                    {
                        cardPrint1[r][c] = ' ';
                    }
                    if(cardPrint1[r][c] == '/')
                    {
                        numPrint++;
                    }
                }
            }
            numPrint = 0;
            for(int r = 0; r < cardPrint2.length; r++)
            {
                for(int c = 0; c < cardPrint2[r].length; c++)
                {
                    cardPrint2[r][c] = cardPrint1[r][c]; 
                }
            }

            for(int r = 0; r < cardPrint3.length; r++)
            {
                for(int c = 0; c < cardPrint3[r].length; c++)
                {
                    cardPrint3[r][c] = cardPrint1[r][c];
                }
            }

            ArrayList<Card> userCards = user.cards();
            int col = 3;

            for(int i = 0; i < userCards.size(); i++)
            {
                if(i < 6)
                {
                    cardPrint1[4][col] = userCards.get(i).rank().charAt(0);
                    if(userCards.get(i).rank().equals("10"))
                    {
                        cardPrint1[4][col+1] = userCards.get(i).rank().charAt(1);
                        cardPrint1[4][col+2] = userCards.get(i).suit().charAt(0);
                    }
                    else
                    {
                        cardPrint1[4][col+1] = userCards.get(i).suit().charAt(0);
                    }
                    cardPrint1[4][col+5] = (char)(((i+1) / 10) + 48);
                    cardPrint1[4][col+6] = (char)(((i+1) % 10) + 48);
                    col += 10;
                    if(i == 5)
                    {
                        col = 3;
                    }
                }
                else if(i < 12)
                {
                    cardPrint2[4][col] = userCards.get(i).rank().charAt(0);
                    if(userCards.get(i).rank().equals("10"))
                    {
                        cardPrint2[4][col+1] = userCards.get(i).rank().charAt(1);
                        cardPrint2[4][col+2] = userCards.get(i).suit().charAt(0);
                    }
                    else
                    {
                        cardPrint2[4][col+1] = userCards.get(i).suit().charAt(0);
                    }
                    cardPrint2[4][col+5] = (char)(((i+1) / 10) + 48);
                    cardPrint2[4][col+6] = (char)(((i+1) % 10) + 48);
                    col+=10;
                    if(i == 11)
                    {
                        col = 3;
                    }
                }
                else
                {
                    cardPrint3[4][col] = userCards.get(i).rank().charAt(0);
                    if(userCards.get(i).rank().equals("10"))
                    {
                        cardPrint3[4][col+1] = userCards.get(i).rank().charAt(1);
                        cardPrint3[4][col+2] = userCards.get(i).suit().charAt(0);
                    }
                    else
                    {
                        cardPrint3[4][col+1] = userCards.get(i).suit().charAt(0);
                    }
                    cardPrint3[4][col+5] = (char)(((i+1) / 10) + 48);
                    cardPrint3[4][col+6] = (char)(((i+1) % 10) + 48);
                    col += 10;
                }
            }

            // from here to line 709 is for printing the river
            col = 3;
            int colO = 27;
            int row = 10;
            for(int i = 0; i < river.size(); i++)
            {
                if(river.get(i) instanceof Card)
                {
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

                if(((Card)river.get(i)).rank().equals("10"))
                {
                    print[row + 3][colO + 4] = (char)(((Card)(river.get(i))).rank().charAt(1));
                    print[row + 3][colO + 5] = (char)(((Card)(river.get(i))).suit().charAt(0));
                }
                else
                {
                    print[row + 3][colO + 4] = (char)(((Card)(river.get(i))).suit().charAt(0));
                }
                }
                else
                {
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

                    if(tempC[0].rank().equals("10"))
                    {
                        print[row + 3][colO + 4] = (char)(tempC[0].rank().charAt(1));
                        print[row + 3][colO + 5] = (char)(tempC[0].suit().charAt(0));
                    }
                    else
                    {
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

                    if(tempC[1].rank().equals("10"))
                    {
                        print[row + 7][colO + 6] = (char)(tempC[1].rank().charAt(1));
                        print[row + 7][colO + 7] = (char)(tempC[1].suit().charAt(0));
                    }
                    else
                    {
                        print[row + 7][colO + 6] = (char)(tempC[1].suit().charAt(0));
                    }
                }
                colO += 17;
                if(i == 2)
                {
                    colO = 27;
                    row += 11;
                }
            }
            
            // here to line 760 is actually printing
            colO = 27;
            row = 10;
            for(char[] c : print)
            {
                for(char e : c)
                {
                    System.out.print(e);
                }
                System.out.println();
            }
            for(char[] c : cardPrint1)
            {
                for(char e : c)
                {
                    System.out.print(e);
                }
                System.out.println();
            }
            for(char[] c : cardPrint2)
            {
                for(char e : c)
                {
                    System.out.print(e);
                }
                System.out.println();
            }
            for(char[] c : cardPrint3)
            {
                for(char e : c)
                {
                    System.out.print(e);
                }
                System.out.println();
            }

            for(int i = 0; i < river.size(); i++)
            {
                if(river.get(i) instanceof Card)
                {
                    atkB = false;
                }
            }
            for(Player p : inGame)
            {
                if(p.numCards() < 6)
                {
                    allFull = false;
                }
            }

            // start of turn logic
            if(river.size() == 0 || river.size() == 6 || river.size() == def.numCards() || someTemp.size() == inGame.size() - 1)
            {
                open = false;
                System.out.println("PALPLAPL");
            }
            if(open)
            {
                System.out.println("OPENPROBLEMS\n\n\n\n");
                if((Player)moveOrder.getValue() != def && moveOrder.getValue() instanceof DurakAi)
                {
                    if(game.legalAttackPossible(((Player)moveOrder.getValue()), river))
                    {
                        DurakInput dI = ((DurakAi)moveOrder.getValue()).playOffense(river, game.numCards(), game.powerSuit());
                        if(dI.pickUp())
                        {
                            someTemp.add((Player)moveOrder.getValue());
                            if(someTemp.size() == inGame.size() - 1)
                            {
                                someTemp = new TreeSet<Player>();
                                open = false;
                                for(Object o : river)
                                {
                                    if(o instanceof Card)
                                    {
                                        pickUp = true;
                                    }
                                    else
                                    {
                                        trash = true;
                                    }
                                }
                            }
                        }
                        else
                        {
                            river.add(((Player)(moveOrder.getValue())).play(dI.indexPlayerCardInput()));
                        }
                    }
                    else
                    {
                        someTemp.add((Player)moveOrder.getValue());
                        if(someTemp.size() == inGame.size() - 1)
                        {
                            someTemp = new TreeSet<Player>();
                            open = false;
                        }
                    }
                }
                else if((Player)moveOrder.getValue() != def)
                {
                    if(game.legalAttackPossible(((Player)moveOrder.getValue()), river))
                    {
                        System.out.print("\nChoose a card from your deck to attack with by entering its index (index is displayed to the right of the card) or N or n to open the field: ");
                        String res = in.nextLine();
                        if(res.equals("N") || res.equals("n"))
                        {
                            someTemp.add((Player)moveOrder.getValue());
                            if(someTemp.size() == inGame.size() - 1)
                            {
                                someTemp = new TreeSet<Player>();
                                open = false;
                            }
                        }
                        else
                        {
                            int index = getNum(res);
                            while(!otherOpen && (index < 0 || index > user.cards().size()))
                            {
                                System.out.println("Invalid range.");
                                System.out.print("\nChoose a card from your deck to attack with by entering its index (index is displayed to the right of the card) or N or n to open the field: ");
                                res = in.nextLine();
                                if(res.equals("N") || res.equals("n"))
                                {
                                    someTemp.add((Player)moveOrder.getValue());
                                    if(someTemp.size() == inGame.size() - 1)
                                    {
                                        someTemp = new TreeSet<Player>();
                                        open = false;
                                    }
                                    otherOpen = true;
                                }
                                else
                                    index = getNum(res);
                            }
                            if(!otherOpen)
                                river.add(user.play(index - 1));
                        }
                    }
                    else
                    {
                        someTemp.add((Player)moveOrder.getValue());
                        if(someTemp.size() == inGame.size() - 1)
                        {
                            someTemp = new TreeSet<Player>();
                            open = false;
                        }
                    }
                }
                else
                {
                    moveOrder = moveOrder.getNext();
                }
            }
            else if(pickUp)
            {
                System.out.println("PROBLEM HERE");
                pickUp = false;
                trash = false;
                for(int i = 0; i < river.size(); i++)
                {
                    if(river.get(i) instanceof Combination)
                    {
                        Card[] pTemp = ((Combination)river.get(i)).cards();
                        def.add(pTemp[0]);
                        def.add(pTemp[1]);
                    }
                    else
                    {
                        def.add((Card)river.get(i));
                    }
                }
                moveOrder = moveOrder.getNext().getNext();
                moveCopy = moveOrder;
                while(allFullNum != inGame.size())
                {
                    System.out.println("OOPS DOESNT WORK");
                    if(((Player)moveOrder.getValue()).numCards() < 6 && game.numCards() > 0)
                    {
                        ((Player)moveOrder.getValue()).add(game.deal());
                    }
                    else
                    {
                        allFullNum++;
                    }
                    moveOrder = moveOrder.getNext();
                    if(moveOrder.getNext() == moveCopy)
                    {
                        allFullNum = 0;
                    }
                }
                moveOrder = moveCopy;
                allFull = false;
                allFullNum = 0;
                river = new ArrayList<Object>();
            }
            else if(trash)
            {
                System.out.println("TRASH PROBLEMS");
                trash = false;
                river = new ArrayList<Object>();
                moveOrder = moveOrder.getNext();

                moveCopy = moveOrder;
                while(allFullNum != inGame.size())
                {
                    if(((Player)moveOrder.getValue()).numCards() < 6 && game.numCards() > 0)
                    {
                        ((Player)moveOrder.getValue()).add(game.deal());
                    }
                    else
                    {
                        allFullNum++;
                    }
                    moveOrder = moveOrder.getNext();
                    if(moveOrder.getNext() == moveCopy)
                    {
                        allFullNum = 0;
                    }
                }
                moveOrder = moveCopy;
                allFull = false;
                allFullNum = 0;
            }
            else if(atkB && river.size() != 6 && river.size() != def.cards().size())
            {
                System.out.println("WJAIJFI");
                if(atk instanceof DurakAi)
                {
                    System.out.println("OOP");
                    if(game.legalAttackPossible(atk, river))
                    {
                        System.out.println("OOP");
                        DurakInput dI = ((DurakAi)atk).playOffense(river, game.numCards(), game.powerSuit());
                        if(dI.pickUp())
                        {
                            open = true;
                            System.out.println("AOKOKO");
                        }
                        else
                        {
                            river.add(((Player)(atk)).play(dI.indexPlayerCardInput()));
                        }
                    }
                    else
                    {
                        open = true;
                    }
                }
                else
                {
                    if(game.legalAttackPossible(atk, river))
                    {
                        System.out.print("\nChoose a card from your deck to attack with by entering its index (index is displayed to the right of the card) or N or n to open the field: ");
                        String res = in.nextLine();
                        if(res.equals("N") || res.equals("n"))
                        {
                            System.out.println("REACHED N");
                            open = true;
                        }
                        else
                        {
                            int index = getNum(res);
                            while(!open && (index < 0 || index > user.cards().size()))
                            {
                                System.out.println("Invalid range.");
                                System.out.print("\nChoose a card from your deck to attack with by entering its index (index is displayed to the right of the card) or N or n to open the field: ");
                                res = in.nextLine();
                                if(res.equals("N") || res.equals("n"))
                                {
                                    open = true;
                                }
                                else
                                    index = getNum(res);
                            }
                            if(!open)
                                river.add(user.play(index - 1));
                        }
                    }
                    else
                    {
                        open = true;
                    }
                }
            }
            else
            {
                if(def instanceof DurakAi)
                {
                    if(game.legalDefensePossible(def, river))
                    {
                        DurakInput pls = ((DurakAi)def).playDefense(river, game.numCards(), game.powerSuit());
                        System.out.println(pls.indexPlayerCardInput() + " " + pls.riverIndex() + " " + pls.attackEntered() + " " + pls.rotateEntered() + " " + pls.pickUp() + "\n\n\n\n\n");
                        if(pls.pickUp())
                        {
                            System.out.println("OTHER PROBLEMS HERE IF");
                            pickUp = true;
                            open = true;
                        }
                        else if(pls.rotateEntered())
                        {
                            river.add(user.play(pls.indexPlayerCardInput()));
                            moveOrder = moveOrder.getNext();
                            System.out.println("ROTATE ISSUES\n\n\n\n\n\n\n");
                        }
                        else
                        {
                            river.set(pls.riverIndex(), new Combination((Card)(river.get(pls.riverIndex())), def.play(pls.indexPlayerCardInput())));
                        }
                    }
                    else
                    {
                        pickUp = true;
                        open = true;
                    }
                }
                else
                {
                    if(game.legalDefensePossible(def, river))
                    {
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
                        }
                    }
                    else
                    {
                        pickUp = true;
                        open = true;
                    }
                }
            }
            /*
            if(open)
            {
                System.out.println("BREAK");
                if(moveOrder.getValue() instanceof DurakAi)
                {
                    if(moveOrder == moveCopy && numNo == inGame.size())
                    {
                        open = false;
                    }
                    else if(moveOrder == moveCopy)
                    {
                        numNo = 0;
                    }
                    DurakInput tempD = ((DurakAi)moveOrder.getValue()).playOffense(river, game.numCards(), game.powerSuit());
                    if(tempD.attackEntered())
                    {
                        river.add(atk.play(tempD.indexPlayerCardInput()));
                        moveOrder = moveOrder.getNext();
                    }
                    else
                    {
                        numNo++;
                        moveOrder = moveOrder.getNext();
                    }
                }
                else
                {
                    if(moveOrder == moveCopy && numNo == inGame.size())
                    {
                        open = false;
                    }
                    else if(moveOrder == moveCopy)
                    {
                        numNo = 0;
                    }
                    System.out.print("\nChoose a card from your deck to attack with by entering its index (index is displayed to the right of the card): ");
                    String res = in.nextLine();
                    int index = getNum(res);
                    while(index < 0 || index > user.cards().size())
                    {
                        System.out.println("Invalid range.");
                        System.out.print("\nChoose a card from your deck to attack with by entering its index (index is displayed to the right of the card): ");
                        res = in.nextLine();
                        index = getNum(res);
                    }
                    //if no attack then numNO add and the rest
                    river.add(user.play(index - 1));
                    numNo++;
                }
            }
            else if(atkB && river.size() != 6 && river.size() != def.cards().size())
            {
                if(atk instanceof DurakAi)
                {
                    DurakInput at = ((DurakAi)atk).playOffense(river, game.numCards(), game.powerSuit());
                    if(at.attackEntered())
                    {
                        river.add(atk.play(at.indexPlayerCardInput()));
                    }
                    else
                    {
                        open = true;
                    }
                }
                else
                {
                    System.out.print("\nChoose a card from your deck to attack with by entering its index (index is displayed to the right of the card): ");
                    String res = in.nextLine();
                    int index = getNum(res);
                    while(index < 0 || index > user.cards().size())
                    {
                        System.out.println("Invalid range.");
                        System.out.print("\nChoose a card from your deck to attack with by entering its index (index is displayed to the right of the card): ");
                        res = in.nextLine();
                        index = getNum(res);
                    }
                    river.add(user.play(index - 1));
                }
            }
            else
            {
                if(def instanceof DurakAi)
                {
                    DurakInput de = ((DurakAi)def).playDefense(river, game.numCards(), game.powerSuit());
                    if(de.pickUp())
                    {
                        for(int i = 0; i < river.size(); i++)
                        {
                            if(river.get(i) instanceof Card)
                                def.add((Card)(river.get(i)));
                            else
                            {
                                Card[] oth = ((Combination)(river.get(i))).cards();
                                def.add(oth[0]);
                                def.add(oth[1]);
                            }
                        }
                        moveOrder = moveOrder.getNext();
                    }
                    else if(de.rotateEntered())
                    {
                        river.add(def.play(de.indexPlayerCardInput()));
                        moveOrder = moveOrder.getNext();
                    }
                    else if(de.pickUp())
                    {
                        for(int i = 0; i < river.size(); i++)
                        {
                            def.add((Card)(river.get(i)));
                        }
                        moveOrder = moveOrder.getNext();
                    }
                    else
                    {
                        System.out.println(de.riverIndex() + " " + de.indexPlayerCardInput() + " " + river.size());
                        river.set(de.riverIndex(), new Combination((Card)(river.get(de.riverIndex())), def.play(de.indexPlayerCardInput())));
                    }
                }
                else
                {
                    System.out.print("\nChoose a card from your deck to defend with (or rotate) by entering its index (index is displayed to the right of the card): ");
                    String res = in.nextLine();
                    int index = getNum(res);
                    while(index < 0 || index > user.cards().size())
                    {
                        System.out.println("Invalid range.");
                        System.out.print("\nChoose a card from your deck to defend with (or rotate) by entering its index (index is displayed to the right of the card): ");                        res = in.nextLine();
                        res = in.nextLine();
                        index = getNum(res);
                    }

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
                if(river.size() == 6 || river.size() == def.cards().size())
                {
                    boolean done = true;
                    for(int i = 0; i < river.size(); i++)
                    {
                        if(!(river.get(i) instanceof Combination))
                        {
                            done = false;
                        }
                    }
                    if(done)
                    {
                        river = new ArrayList<Object>();
                        moveOrder = moveOrder.getNext();
                        ListNode temp4 = moveOrder;
                        int numFull = 0;
                        while(game.hasCards() && numFull != inGame.size())
                        {
                            if((((Player)(moveOrder.getValue())).cards().size()) < 6)
                            {
                                game.deal(((Player)(moveOrder.getValue())));
                            }
                            else
                            {
                                numFull++;
                            }
                            moveOrder = moveOrder.getNext();
                        }
                        moveOrder = temp4;
                    }
                }
            }*/
            Player tempRemove = null;
            for(Player p : inGame)
            {
                if(game.numCards() == 0 && p.numCards() == 0)
                {
                    ListNode moveOrderCopy2 = moveOrder;
                    ListNode speedOrderCopy2 = speedOrder;

                    while((Player)moveOrder.getNext().getValue() != p)
                    {
                        moveOrder = moveOrder.getNext();
                    }
                    moveOrder.setNext(moveOrder.getNext().getNext());
                    if((Player)moveOrderCopy2.getValue() != p)
                        moveOrder = moveOrderCopy2;
                    else    
                        moveOrder = moveOrder.getNext();

                    while((Player)speedOrder.getNext().getValue() != p)
                    {
                        speedOrder = speedOrder.getNext();
                    }
                    speedOrder.setNext(speedOrder.getNext().getNext());
                    if((Player)speedOrderCopy2.getValue() != p)
                        speedOrder = speedOrderCopy2;
                    else    
                        speedOrder = speedOrder.getNext();
                }
            }
            if(tempRemove != null)
            {
                inGame.remove(tempRemove);
                tempRemove = null;
            }
        }
    }
    public static int getNum(String r) // moved
{
    String i_num = r;
	int num = -1;
	boolean rep = true;
    Scanner in = new Scanner(System.in);

    while (rep) {
			try {
				  if(!i_num.equals(r))
                    i_num = in.nextLine();
				  num = Integer.valueOf(i_num);
				  rep = false;
			}
			catch (Exception ex){ System.out.println("Please enter an integer number.\n");  
      }
		}
	return num;
    }   
}

/*
    move order attacker to defender
    if first card then let the attaker play something
    if defense possible then call the defend shit
    else let everyone else play any attacks then make defender pick up
    move twice forward if picked up
    if defense possible then defend
    then back to attack check if attack possible
    if no attack possible then open up
    if nobody attack possible or nobody adds then defend possible same shit

 */