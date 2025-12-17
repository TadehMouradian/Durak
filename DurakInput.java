public class DurakInput
{
    private int user;
    private int river;
    private boolean rotate;
    private boolean attack;
    private boolean lost;

    public DurakInput(){
        user = -1;
        river = -1;
        rotate = false;
        attack = false;
        lost = false;
    }
    
    public DurakInput(int usr, int r, boolean ro, boolean atk, boolean lose)
    {
        user = usr;
        river = r;
        rotate = ro;
        attack = atk;
        lost = lose;
    }

    public int indexPlayerCardInput()
    {
        return user;
    }

    public int riverIndex()
    {
        return river;
    }

    public boolean rotateEntered()
    {
        return rotate;
    }

    public boolean attackEntered()
    {
        return attack;
    }

    public boolean pickUp()
    {
        return lost;
    }
}