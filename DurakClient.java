import java.util.*;
import java.util.concurrent.TimeUnit;
public class DurakClient{
    public static void main(String[] args) throws InterruptedException{
        String name;
        Scanner in = new Scanner(System.in);

        System.out.print("Enter name: ");
        name = in.nextLine();
        //in.close();

        Durak game = new Durak(name);
        while(!game.gameEnded()){
            game.nextTurn();
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println("The loser is " + game.getLoser() + ".");
    }
}