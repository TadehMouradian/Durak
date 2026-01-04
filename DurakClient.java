import java.util.*;
//import java.util.concurrent.TimeUnit;
public class DurakClient{
    public static void main(String[] args){
        String name;
        Scanner in = new Scanner(System.in);

        System.out.print("Enter name: ");
        name = in.nextLine();
        //in.close();

        Durak game = new Durak(name);
        try{
            while(!game.gameEnded()){
                game.nextTurn();
                //TimeUnit.SECONDS.sleep(1);
            }
        }catch(Exception e){
            System.out.println("Exception thrown, terminating program.\nException message: " + e.getLocalizedMessage());
        }
        System.out.println("The loser is " + game.getLoser() + ".");
    }
}