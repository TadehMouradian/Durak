public class DurakInput
{
    private int usr;
    private int r;
    private boolean rotate;
    private boolean attack;
    private boolean lost;
    
    public DurakInput(int user, int river, boolean ro, boolean atk, boolean lose)
    {
        usr = user;
        r = river;
        rotate = ro;
        attack = atk;
        lost = lose;
    }

    public int indexPlayerCardInput()
    {
        return usr;
    }

    public int riverIndex()
    {
        return r;
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