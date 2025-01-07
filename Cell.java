public interface Cell
{
    //Return the input text (aka String) this cell was init by (without any computation).
    String getData();//DONE

    //Changes the underline string of this cell
    void setData(String s);//DONE

     //Returns the type of this cell {TEXT,NUMBER, FORM, ERR_CYCLE_FORM, ERR_WRONG_FORM}
    public int getType();

     //Changes the type of this Cell {TEXT,NUMBER, FORM, ERR_CYCLE_FORM, ERR_WRONG_FORM}
    public void setType(int t);

     //Computes the natural order of this entry (cell) in case of a number or a String =0, else 1+ the max of all dependent cells.
    public int getOrder();

    //Changes the order of this Cell
    public void setOrder(int t);
}