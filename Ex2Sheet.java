
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the Sheet interface for a spreadsheet.
 */
public class Ex2Sheet implements Sheet
{
    private Cell[][] table;

    public Ex2Sheet(int x, int y)
    {
        table = new SCell[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
        eval();
    }

    public Ex2Sheet()
    {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public String value(int x, int y)
    {
        String ans = Ex2Utils.EMPTY_CELL;
        if (!isIn(x, y)) return Ex2Utils.EMPTY_CELL;
        Cell c = get(x, y);
        if(c!=null)
        {
            ans = c.toString();
        }
        else return Ex2Utils.EMPTY_CELL;
        return ans;

    }

    @Override
    public Cell get(int x, int y)
    {
        if(isIn(x,y)==false) return null;
        return table[x][y];
    }

    @Override
    public Cell get(String cords)
    {
        Cell ans = null;
        try
        {
            int x = cords.charAt(0) - 'A';
            int y = Integer.parseInt(cords.substring(1)) - 1;
            ans = get(x, y);
        }
        catch (Exception e)
        {
            return null;
        }
        return ans;
    }

    @Override
    public int width()
    {
        return table.length;
    }

    @Override
    public int height()
    {
        return table[0].length;
    }

    @Override
    public void set(int x, int y, String s)
    {
        if (!isIn(x, y)) return;
        Cell c = new SCell(s);
        table[x][y] = new SCell(s);
        eval();
    }

    @Override
    public void eval()
    {
        int[][] dd = depth();
        for (int x = 0; x < width(); x++)
        {
            for (int y = 0; y < height(); y++)
            {
                Cell c = table[x][y];
                if (c != null && c.getType() == Ex2Utils.FORM)
                {
                    String result = eval(x, y);
                    c.setData(result);
                }
            }
        }
    }

    @Override
    public boolean isIn(int x, int y)
    {
        boolean ans = x>=0 && y>=0;
        ans = ans && x < width() && y < height();
        return ans;
    }

    @Override
    public int[][] depth()
    {
        int[][] ans = new int[width()][height()];
        for (int[] row : ans)
        {
            Arrays.fill(row, -1);
        }
        boolean Didchanged = true;
        int iter = 0;
        while (Didchanged && iter < width() * height()) 
        {
            Didchanged = false;
            for (int x = 0; x < width(); x++) 
            {
                for (int y = 0; y < height(); y++) 
                {
                    if (ans[x][y] == -1) 
                    {
                        int temp = computeCellDepth(x, y, ans);
                        if (temp >= 0)
                        {
                            ans[x][y] = temp;
                            Didchanged = true;
                        }
                    }
                }
            }
            iter++;
        }
        return ans;
    }

    private int computeCellDepth(int x, int y, int[][] dpeth) 
    {
        if (isIn(x, y)==false) return 0;
        Cell c = table[x][y];
        if (c == null || c.getType() == Ex2Utils.TEXT || c.getType() == Ex2Utils.NUMBER) {
            return 0;
        }

        if (c.getType() == Ex2Utils.FORM) {
            Set<String> dependene = extractDependeces(c.getData());
            int maxDepth = 0;

            for (String dep : dependene) {
                Cell depCell = get(dep);
                if (depCell == null) return -1;
                int depDepth = dpeth[getColumnIndx(dep)][getRowIndx(dep)];
                if (depDepth == -1) return -1;
                maxDepth = Math.max(maxDepth, depDepth);
            }
            return maxDepth + 1;
        }
        return -1; // Invalid or cyclic dependency
    }

    private Set<String> extractDependeces(String formula) {
        Set<String> dependences = new HashSet<>();
        String[] tok = formula.substring(1).split("[+\\-*/()]");
        for (String token : tok) 
        {
            if (token.matches("[A-Za-z]+\\d+")) {
                
                dependences.add(token);
            }
        }
        return dependences;
    }

    private int getColumnIndx(String cords) {
        return cords.charAt(0) - 'A';
    }

    private int getRowIndx(String cords) {
        return Integer.parseInt(cords.substring(1)) - 1;
    }

    @Override
    public void load(String fileName) throws IOException
    {
        try
        {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            String line;
            line = br.readLine();
            line = br.readLine();
            while (line != null)
            {
                if(line.length()>=5)
                {
                    if((line.charAt(0)>='0'&&line.charAt(0)<='9')
                            &&(line.charAt(1)==',')
                            &&(line.charAt(2)>='0'&&line.charAt(2)<='9')
                            &&(line.charAt(3)==','))
                    {
                        String tmp=line.substring(4);
                        this.table[line.charAt(0)-'0'][line.charAt(2)-'0'].setData(tmp);
                    }
                }
                line = br.readLine();
            }

        }
        catch(IOException ex)
        {
            System.out.print("Error reading file\n" + ex);
            System.exit(2);
        }

    }

    @Override
    public void save(String fileName) throws IOException
    {
        try
        {
            FileWriter fw = new FileWriter(fileName);
            PrintWriter outs = new PrintWriter(fw);
            for(int i=0;i<this.table[0].length;i++)
            {
                for(int j=0;j<this.table.length;j++)
                {
                    SCell tmp=new SCell(this.table[i][j].getData());
                    if(tmp.DoesValidEntry()==true)
                    {
                        outs.println(i+","+j+","+tmp.getData());
                    }
                }
            }
            outs.close();
            fw.close();
        }
        catch(IOException ex) {
            System.out.print("Error writing file\n" + ex);
        }

    }
    
    private String resolveSelfReferences(String formula, int currentX, int currentY, Set<String> visited)
    {
        StringBuilder resolvedForm = new StringBuilder();
        String regex = "[A-Za-z]+[0-9]+";
        Pattern pr = Pattern.compile(regex);
        Matcher mchr = pr.matcher(formula);
        int lastIndex = 0;
        while (mchr.find())
        {
            String ref = mchr.group();
            int x = getColumnIndx(ref);
            int y = getRowIndx(ref);
            resolvedForm.append(formula, lastIndex, mchr.start());
            String cellKey = x + "," + y;
            if (visited.contains(cellKey)) {
                throw new IllegalArgumentException("Circular reference: " + ref);
            }
            visited.add(cellKey);

            if (x == currentX && y == currentY) {
                return String.valueOf(Ex2Utils.ERR_CYCLE_FORM);
            }

            if (isIn(x, y)) {
                Cell referencedCell = get(x, y);
                if (referencedCell == null || referencedCell.getData().isEmpty()) {
                    resolvedForm.append("0");
                } else {
                    String value = eval(x, y, visited); 
                    if (value.equals(String.valueOf(Ex2Utils.ERR_CYCLE_FORM))) {
                        return String.valueOf(Ex2Utils.ERR_CYCLE_FORM);
                    }
                    resolvedForm.append(value);
                }
            } else
            {
                throw new IllegalArgumentException("Out of boundries, cell: " + ref);
            }

            lastIndex = mchr.end();
            visited.remove(cellKey);
        }

        resolvedForm.append(formula.substring(lastIndex));
        return resolvedForm.toString();
    }

    @Override
    public String eval(int x, int y) {
        return eval(x, y, new HashSet<>());
    }

    private String eval(int x, int y, Set<String> visited)
    {
        if (!isIn(x, y)) return Ex2Utils.EMPTY_CELL;
        Cell cell = get(x, y);
        if (cell == null) return Ex2Utils.EMPTY_CELL;
        String data = cell.getData();
        if (cell.getType() == Ex2Utils.NUMBER || cell.getType() == Ex2Utils.TEXT) {
            return data;
        }
        if (cell.getType() == Ex2Utils.FORM)
        {
            try
            {
                String formula = data.substring(1);
                String resolvedForm = resolveSelfReferences(formula, x, y, visited);
                return Double.toString(SCell.computeFormulaValue("=" + resolvedForm));
            }
            catch (ArithmeticException e)
            {
                return String.valueOf(Ex2Utils.ERR_CYCLE_FORM);
            }
            catch (Exception e)
            {
                return String.valueOf("ERR_WRONG_FORM");
            }
        }
        return Ex2Utils.EMPTY_CELL;
    }
}
