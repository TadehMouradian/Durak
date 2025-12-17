public class ListNode
{
    private Player value;
	private ListNode next;
	
	public ListNode(Player initValue, ListNode initNext)
	{
		value = initValue;
		next = initNext;
	}
	
	public Player getValue()
	{
		return value;
	}
	
	public ListNode getNext()
	{
		return next;
	}
	
	public void setValue(Player theNewValue)
	{
		value = theNewValue;
	}
	
	public void setNext(ListNode theNewNext)
	{
		next = theNewNext;
	}
}